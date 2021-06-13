package com.github.ungarscool1.Roboto.listeners.commands.music;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import com.github.ungarscool1.Roboto.Main;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import io.sentry.ISpan;
import io.sentry.ITransaction;
import io.sentry.SpanStatus;

public class DiscoboomSubCommand {

	static HashMap<Server, ServerMusicManager> musicManagers = new HashMap<>();
	static AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

	public static void help(MessageCreateEvent event, ITransaction transaction) {
		ISpan span = transaction.startChild("Writing message");
		ResourceBundle language = ResourceBundle.getBundle("lang.lang", Main.locByServ.get(event.getServer().get()));
		EmbedBuilder embed = new EmbedBuilder().setTitle("DiscoBoom 2000")
			.setDescription(language.getString("discoboom.help.embed.description"))
			.addField("play", language.getString("discoboom.help.embed.play"))
			.addField("pause", language.getString("discoboom.help.embed.pause"))
			.addField("next", language.getString("discoboom.help.embed.next"))
			.addField("queue", language.getString("discoboom.help.embed.queue"))
			.addField("clear", language.getString("discoboom.help.embed.clear"))
			.addField("stop", language.getString("discoboom.help.embed.stop"))
			.addField("disconnect", language.getString("discoboom.help.embed.disconnect"))
			.setColor(Color.green);
		span.finish(SpanStatus.OK);
		span = transaction.startChild("Sending message");
		event.getChannel().sendMessage(embed);
		span.finish(SpanStatus.OK);
	}
	
	public static void play(MessageCreateEvent event, String url, ITransaction transaction) {
		ISpan span = transaction.startChild("Player init");
		User user = null;
		Server server = event.getServer().get();
		ResourceBundle language = ResourceBundle.getBundle("lang.lang", Main.locByServ.get(server));
		Optional<ServerVoiceChannel> voice = null;
		EmbedBuilder embed = new EmbedBuilder().setTitle("DiscoBoom 2000");
		ServerMusicManager musicManager = musicManagers.getOrDefault(server, new ServerMusicManager());
		AudioSourceManagers.registerRemoteSources(playerManager);
		final String title = "";

		if (!event.getMessageAuthor().isUser())
			return;
		if (musicManager.player == null) {
			musicManager.player = playerManager.createPlayer();
			musicManager.scheduler = new TrackScheduler(musicManager.player, event.getChannel());
			musicManager.player.addListener(musicManager.scheduler);
			musicManager.channel = event.getChannel();
		}
		user = event.getMessageAuthor().asUser().get();
		voice = server.getConnectedVoiceChannel(user);
		if (!voice.isPresent()) {
			embed.setDescription(language.getString("discoboom.play.not_connected"))
				.setColor(Color.RED);
			event.getChannel().sendMessage(embed);
			span.finish(SpanStatus.OK);
			return;
		}
		playerManager.registerSourceManager(new YoutubeAudioSourceManager());
		if (musicManager.connection == null) {
			musicManager.source = new LavaplayerAudioSource(event.getApi(), musicManager.player);
			musicManager.connection = voice.get().connect().join();
			musicManager.connection.setAudioSource(musicManager.source);
			musicManagers.put(server, musicManager);
		}
		span.finish(SpanStatus.OK);
		span = transaction.startChild("player - start music");
		
		playerManager.loadItem(url, new AudioLoadResultHandler() {
			@Override
			public void trackLoaded(AudioTrack track) {
				title.concat(track.getInfo().title);
				musicManager.scheduler.queue(track);
				musicManager.player.setVolume(50);
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				for (AudioTrack track : playlist.getTracks()) {
					musicManager.scheduler.queue(track);
				}
			}

			@Override
			public void noMatches() {}

			@Override
			public void loadFailed(FriendlyException throwable) {}
		});
		span.finish(SpanStatus.OK);
	}
	
	public static void next(MessageCreateEvent event, ITransaction transaction) {
		ISpan span = transaction.startChild("Next init");
		Server server = event.getServer().get();
		ServerMusicManager musicManager = musicManagers.get(server);
		
		span.finish();
		span = transaction.startChild("Next function");
		musicManager.scheduler.nextTrack();
		span.finish();
	}
	
	public static void disconnect(MessageCreateEvent event, ITransaction transaction) {
		ISpan span = transaction.startChild("Writing message");
		Server server = event.getServer().get();
		ResourceBundle language = ResourceBundle.getBundle("lang.lang", Main.locByServ.get(server));
		ServerMusicManager musicManager = null;
		EmbedBuilder embed = new EmbedBuilder().setTitle("DiscoBoom 2000")
				.setColor(Color.green);

		span.finish(SpanStatus.OK);
		span = transaction.startChild("Disconnection");
		if (musicManagers.containsKey(server)) {
			musicManager = musicManagers.get(server);
			musicManager.connection.close();
			musicManager.player.destroy();
			musicManagers.remove(server);
			embed.setDescription(language.getString("discoboom.disconnect.description"));
		} else {
			embed.setDescription(language.getString("discoboom.disconnect.error"));
		}
		span.finish(SpanStatus.OK);
		span = transaction.startChild("Sending");
		event.getChannel().sendMessage(embed);
		span.finish(SpanStatus.OK);
	}

	public static void getQueue(MessageCreateEvent event, ITransaction transaction) {
		ISpan span = transaction.startChild("Writing message");
		StringBuilder title = new StringBuilder();
		ResourceBundle language = ResourceBundle.getBundle("lang.lang", Main.locByServ.get(event.getServer().get()));
		ServerMusicManager musicManager = musicManagers.get(event.getServer().get());
		List<AudioTrack> tracks = musicManager.scheduler.getTrackList();
		EmbedBuilder embed = new EmbedBuilder().setTitle("DiscoBoom 2000")
			.setDescription(language.getString("discoboom.queue.description"));

		title.append("[")
		.append(musicManager.player.getPlayingTrack().getInfo().title)
		.append("](")
		.append(musicManager.player.getPlayingTrack().getInfo().uri)
		.append(")\n");
		tracks.forEach(track -> title.append("• [")
				.append(track.getInfo().title)
				.append("](")
				.append(track.getInfo().uri)
				.append(")\n"));
		embed.setDescription(String.format("▶️ %s", title.toString()));
		span.finish(SpanStatus.OK);
		span = transaction.startChild("Sending message");
		event.getChannel().sendMessage(embed);
		span.finish(SpanStatus.OK);
	}

	public static void stop(MessageCreateEvent event, ITransaction transaction) {
		ISpan span = transaction.startChild("Writing message");
		ResourceBundle language = ResourceBundle.getBundle("lang.lang", Main.locByServ.get(event.getServer().get()));
		ServerMusicManager musicManager = musicManagers.get(event.getServer().get());
		EmbedBuilder embed = new EmbedBuilder().setTitle("DiscoBoom 2000")
				.setDescription(language.getString("discoboom.stop.description"));

		span.finish();
		span = transaction.startChild("Stopping");
		musicManager.scheduler.stop();
		span.finish();
		event.getChannel().sendMessage(embed);
	}

	public static void clear(MessageCreateEvent event, ITransaction transaction) {
		ISpan span = transaction.startChild("Writing message");
		ServerMusicManager musicManager = musicManagers.get(event.getServer().get());
		ResourceBundle language = ResourceBundle.getBundle("lang.lang", Main.locByServ.get(event.getServer().get()));
		EmbedBuilder embed = new EmbedBuilder().setTitle("DiscoBoom 2000")
				.setDescription(language.getString("discoboom.clear.description"));

		musicManager.scheduler.clearQueue();
		span.finish();
		event.getChannel().sendMessage(embed);
	}

	public static void pause(MessageCreateEvent event, ITransaction transaction) {
		ServerMusicManager musicManager = musicManagers.get(event.getServer().get());

		musicManager.player.setPaused(!musicManager.player.isPaused());
	}
}

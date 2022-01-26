package com.github.ungarscool1.Roboto.listeners.commands.music;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.channel.TextChannel;
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

	public static EmbedBuilder help(Server server, ITransaction transaction) {
		ISpan span = transaction.startChild("Writing message");
		ResourceBundle language = ResourceBundle.getBundle("lang.lang", Main.locByServ.get(server));
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
		return embed;
	}
	
	public static EmbedBuilder play(Server server, TextChannel channel, User user, String url, ITransaction transaction) {
		ISpan span = transaction.startChild("Player init");
		ResourceBundle language = ResourceBundle.getBundle("lang.lang", Main.locByServ.get(server));
		Optional<ServerVoiceChannel> voice = null;
		EmbedBuilder embed = new EmbedBuilder().setTitle("DiscoBoom 2000");
		ServerMusicManager musicManager = musicManagers.getOrDefault(server, new ServerMusicManager());
		AudioSourceManagers.registerRemoteSources(playerManager);
		final String title = "";

		if (musicManager.player == null) {
			musicManager.player = playerManager.createPlayer();
			musicManager.scheduler = new TrackScheduler(musicManager.player, channel);
			musicManager.player.addListener(musicManager.scheduler);
			musicManager.channel = channel;
		}
		voice = server.getConnectedVoiceChannel(user);
		if (!voice.isPresent()) {
			embed.setDescription(language.getString("discoboom.play.not_connected"))
				.setColor(Color.RED);
			span.finish(SpanStatus.OK);
			return embed;
		}
		playerManager.registerSourceManager(new YoutubeAudioSourceManager());
		if (musicManager.connection == null) {
			musicManager.source = new LavaplayerAudioSource(server.getApi(), musicManager.player);
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
		return new EmbedBuilder().setTitle("Added to queue");
	}
	
	public static EmbedBuilder next(Server server, ITransaction transaction) {
		ISpan span = transaction.startChild("Next init");
		ServerMusicManager musicManager = musicManagers.get(server);
		
		span.finish();
		span = transaction.startChild("Next function");
		musicManager.scheduler.nextTrack();
		span.finish();
		return new EmbedBuilder().setTitle("Changing to the next sound");
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

	public static EmbedBuilder getQueue(Server server, ITransaction transaction) {
		ISpan span = transaction.startChild("Writing message");
		StringBuilder title = new StringBuilder();
		ResourceBundle language = ResourceBundle.getBundle("lang.lang", Main.locByServ.get(server));
		ServerMusicManager musicManager = musicManagers.get(server);
		List<AudioTrack> tracks = musicManager == null ? null : musicManager.scheduler.getTrackList();
		EmbedBuilder embed = new EmbedBuilder().setTitle("DiscoBoom 2000")
			.setDescription(language.getString("discoboom.queue.description"));

		if (musicManager != null && musicManager.player.getPlayingTrack() != null) {
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
		} else {
			embed.setDescription(language.getString("discoboom.queue.empty"));
		}
		span.finish(SpanStatus.OK);
		return embed;
	}

	public static EmbedBuilder stop(Server server, ITransaction transaction) {
		ISpan span = transaction.startChild("Writing message");
		ResourceBundle language = ResourceBundle.getBundle("lang.lang", Main.locByServ.get(server));
		ServerMusicManager musicManager = musicManagers.get(server);
		EmbedBuilder embed = new EmbedBuilder().setTitle("DiscoBoom 2000")
				.setDescription(language.getString("discoboom.stop.description"));

		span.finish();
		span = transaction.startChild("Stopping");
		musicManager.scheduler.stop();
		span.finish();
		return embed;
	}

	public static EmbedBuilder clear(Server server, ITransaction transaction) {
		ISpan span = transaction.startChild("Writing message");
		ServerMusicManager musicManager = musicManagers.get(server);
		ResourceBundle language = ResourceBundle.getBundle("lang.lang", Main.locByServ.get(server));
		EmbedBuilder embed = new EmbedBuilder().setTitle("DiscoBoom 2000")
				.setDescription(language.getString("discoboom.clear.description"));

		musicManager.scheduler.clearQueue();
		span.finish();
		return embed;
	}

	public static EmbedBuilder pause(Server server, ITransaction transaction) {
		ServerMusicManager musicManager = musicManagers.get(server);

		musicManager.player.setPaused(!musicManager.player.isPaused());
		return new EmbedBuilder().setTitle("Paused");
	}
}

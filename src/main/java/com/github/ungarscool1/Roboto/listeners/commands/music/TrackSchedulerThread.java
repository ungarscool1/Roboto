package com.github.ungarscool1.Roboto.listeners.commands.music;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.Reaction;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.reaction.ReactionAddEvent;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import io.sentry.ISpan;
import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;

public class TrackSchedulerThread implements Runnable {
	private final TextChannel channel;
	private TrackScheduler scheduler;
	private Message message;
	private AudioTrack track;
	private long lastPos = -1;
	
	public TrackSchedulerThread(TextChannel channel, TrackScheduler scheduler, AudioTrack track) {
		this.channel = channel;
		this.scheduler = scheduler;
		this.track = track;
	}
	
	private EmbedBuilder createEmbed() {
		StringBuilder title = new StringBuilder()
				.append("[")
				.append(track.getInfo().title)
				.append("](")
				.append(track.getInfo().uri)
				.append(")\n");
		double i = 0;
		double calc = Math.round((double) track.getPosition() / (double) track.getDuration() * 14.0);
		StringBuilder progress = new StringBuilder();
		Date duration = new Date(track.getDuration());
		Date position = new Date(track.getPosition());
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		String durationFormatted = formatter.format(duration);
		String positionFormatted = formatter.format(position);
		EmbedBuilder embed = new EmbedBuilder()
				.setTitle("DiscoBoom 2000")
				.addField(scheduler.language.getString("discoboom.play.title"), title.toString())
				.addField(scheduler.language.getString("discoboom.play.author"), track.getInfo().author);

		lastPos = track.getPosition();
		progress.append("[");
		for (i = 0; i < calc; i++)
			progress.append("▬");
		progress.append("](")
			.append(track.getInfo().uri)
			.append(")");
		for (int j = 0; j + i < 14; j++)
			progress.append("▬");
		progress.append(" ")
			.append(positionFormatted)
			.append("/")
			.append(durationFormatted);
		embed.setDescription(progress.toString());
		return embed;
	}
	
	private void handleReaction(ReactionAddEvent event) {
		Reaction react = null;
		User user = event.requestUser().join();
		
		if (user.isBot())
			return;
		react = event.requestReaction().join().get();
		react.getEmoji().asUnicodeEmoji().ifPresent(string -> {
			if (string.equals("⏯️"))
				scheduler.pauseResume();
			else if (string.equals("⏭️"))
				scheduler.nextTrack();
			else if (string.equals("⏹️"))
				scheduler.stop();
		});
		react.removeUser(user);
	}
	
	public void run() {
		ITransaction transaction = Sentry.startTransaction("Discoboom player thread", "Discoboom player thread");
		ISpan span;
		message = channel.sendMessage(createEmbed()).join();
		message.addReactions("⏯️", "⏭️", "⏹️");
		message.addReactionAddListener(event -> handleReaction(event));
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			Sentry.captureException(e);
			e.printStackTrace();
		}
		while (lastPos != track.getPosition() || scheduler.isPaused()) {
			span = transaction.startChild("Editing message");
			message.edit(createEmbed());
			span.finish();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				Sentry.captureException(e);
				e.printStackTrace();
			}
		}
		message.delete();
		transaction.finish(SpanStatus.OK);
	}
}

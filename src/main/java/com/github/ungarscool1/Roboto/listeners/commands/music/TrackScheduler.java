package com.github.ungarscool1.Roboto.listeners.commands.music;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import com.github.ungarscool1.Roboto.Main;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

public class TrackScheduler extends AudioEventAdapter {
	  private final AudioPlayer player;
	  private final BlockingQueue<AudioTrack> queue;
	  private final TextChannel channel;
	  private TrackSchedulerThread trackSchedulerThread;
	  private Thread t;
	  protected ResourceBundle language;

	  /**
	   * @param player The audio player this scheduler uses
	   */
	  public TrackScheduler(AudioPlayer player, TextChannel channel) {
	    this.player = player;
	    this.queue = new LinkedBlockingQueue<>();
	    this.channel = channel;
	    this.language = ResourceBundle.getBundle("lang.lang", Main.locByServ.get(channel.asServerTextChannel().get().getServer()));
	  }

	  /**
	   * Add the next track to queue or play right away if nothing is in the queue.
	   *
	   * @param track The track to play or add to queue.
	   */
	  public void queue(AudioTrack track) {
		  EmbedBuilder embed = new EmbedBuilder().setTitle("DiscoBoom2000")
					.setUrl(track.getInfo().uri);

	    if (!player.startTrack(track, true)) {
	    	queue.add(track);
	    	embed.addField(language.getString("discoboom.play.queued"), track.getInfo().title);
	    } else {
	    	trackSchedulerThread = new TrackSchedulerThread(channel, this, track);
	    	t = new Thread(trackSchedulerThread);
	    	t.start();
	    	return;
	    }
	    channel.sendMessage(embed);
	  }

	  /**
	   * Start the next track, stopping the current one if it is playing.
	   */
	  public void nextTrack() {
		  AudioTrack next = queue.poll();
		  EmbedBuilder embed = new EmbedBuilder().setTitle("DiscoBoom 2000")
				  .setUrl(next.getInfo().uri);

		  if (player.startTrack(next, false)) {
			  trackSchedulerThread = new TrackSchedulerThread(channel, this, next);
			  t = new Thread(trackSchedulerThread);
			  t.start();
			  return;
		  } else
			  embed.addField(language.getString("errors.title"), "An error occured while trying to play '" + next.getInfo().title + "'");
		  channel.sendMessage(embed);
	  }
	  
	  public void pauseResume() {
		  player.setPaused(!player.isPaused());
	  }

	  @Override
	  public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
	    // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
	    if (endReason.mayStartNext) {
	      nextTrack();
	    }
	  }
	  
	  public List<AudioTrack> getTrackList() {
		  List<AudioTrack> tracks = new ArrayList<>();

		  queue.forEach(track -> tracks.add(track));
		  return tracks;
	  }
	  
	  public AudioTrack getCurrentTrack() {
		  return player.getPlayingTrack();
	  }
	  
	  public AudioTrack getTrackLastTrack() {
		  return getTrackList().get(getTrackList().size());
	  }
	  
	  public void clearQueue() {
		  this.queue.clear();
	  }
	  
	  public void stop() {
		player.stopTrack();
		clearQueue();
	  }
	  
	  public boolean isPaused() {
		  return player.isPaused();
	  }

}

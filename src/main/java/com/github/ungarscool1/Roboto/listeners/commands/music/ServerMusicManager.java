package com.github.ungarscool1.Roboto.listeners.commands.music;

import org.javacord.api.audio.AudioConnection;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.entity.channel.TextChannel;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

public class ServerMusicManager {
	AudioConnection connection = null;
	TrackScheduler scheduler;
	AudioPlayer player = null;
	AudioSource source;
	TextChannel channel;
}

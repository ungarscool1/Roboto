package com.github.ungarscool1.Roboto.listeners.commands;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.DiscordClient;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.entity.user.UserStatus;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import com.github.ungarscool1.Roboto.Main;
import com.github.ungarscool1.Roboto.ServerLanguage;


public class UtilsCommand implements MessageCreateListener{

	public void onMessageCreate(MessageCreateEvent event) {
		Message message = event.getMessage();

		if (!message.getServer().isPresent() || message.getAuthor().isBotUser()) {
			return;
		}
		
		// I still love u
		if (message.getContent().contains("<3") || message.getContent().contains("❤")) {
			message.addReaction("❤");
		}
		if (message.getContent().equalsIgnoreCase("beep boop")) {
			message.addReaction("<:bot:745718850041020446>");
		}
		if (message.getContent().contains("<@!373199180161613824>") || message.getContent().contains("<@373199180161613824>")) {
			if (message.getContent().contains("Je t'aime")) {
				message.getChannel().sendMessage("Moi aussi je t'aime bb ❤");
			}
		}
		
	}

}

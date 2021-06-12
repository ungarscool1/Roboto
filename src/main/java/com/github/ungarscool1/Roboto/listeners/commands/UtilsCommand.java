package com.github.ungarscool1.Roboto.listeners.commands;

import org.javacord.api.entity.message.Message;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

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

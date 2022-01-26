package com.github.ungarscool1.Roboto.listeners.commands.music;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import io.sentry.ITransaction;
import io.sentry.Sentry;

public class DiscoboomCommand implements MessageCreateListener {

	@Override
	public void onMessageCreate(MessageCreateEvent event) {
		Message message = event.getMessage();
		String args[] = null;
		ITransaction transaction = null;
		Server server = null;
		
		if (message.isPrivateMessage())
			return;
		if (!message.getContent().startsWith("!discoboom"))
			return;
		server = message.getServer().get();
		transaction = Sentry.startTransaction("!discoboom", "command");
		args = message.getContent().split(" ");
		if (args.length == 1 || args[1].equalsIgnoreCase("help")) {
			message.getChannel().sendMessage(DiscoboomSubCommand.help(server, transaction));
			transaction.setDescription("Discoboom help command processing");
		} else if (args[1].equalsIgnoreCase("play") && args.length >= 3) {
			DiscoboomSubCommand.play(server, message.getChannel(), message.getUserAuthor().get(), args[2], transaction);
			transaction.setDescription("Discoboom play command processing");
		} else if (args[1].equalsIgnoreCase("next")) {
			DiscoboomSubCommand.next(event, transaction);
			transaction.setDescription("Discoboom next command processing");
		} else if (args[1].equalsIgnoreCase("queue")) {
			event.getChannel().sendMessage(DiscoboomSubCommand.getQueue(server, transaction));
			transaction.setDescription("Discoboom queue command processing");
		} else if (args[1].equalsIgnoreCase("disconnect")) {
			DiscoboomSubCommand.disconnect(event, transaction);
			transaction.setDescription("Discoboom disconnect command processing");
		} else if (args[1].equalsIgnoreCase("stop")) {
			event.getChannel().sendMessage(DiscoboomSubCommand.stop(server, transaction));
			transaction.setDescription("Discoboom stop command processing");
		} else if (args[1].equalsIgnoreCase("clear")) {
			DiscoboomSubCommand.clear(event, transaction);
			transaction.setDescription("Discoboom clear command processing");
		} else if (args[1].equalsIgnoreCase("pause")) {
			DiscoboomSubCommand.pause(event, transaction);
			transaction.setDescription("Discoboom pause command processing");
		} else {
			DiscoboomSubCommand.help(server, transaction);
			transaction.setDescription("Discoboom help command processing");
		}
		transaction.finish();
	}

}

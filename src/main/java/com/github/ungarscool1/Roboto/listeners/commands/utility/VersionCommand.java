package com.github.ungarscool1.Roboto.listeners.commands.utility;

import com.github.ungarscool1.Roboto.Main;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;
import java.util.ResourceBundle;

public class VersionCommand implements MessageCreateListener {
	@Override
	public void onMessageCreate(MessageCreateEvent event) {
		Message message = event.getMessage();
		DiscordApi api = event.getApi();
		if (!message.getServer().isPresent() || message.getAuthor().isBotUser())
			return;
		ResourceBundle language = ResourceBundle.getBundle("lang.lang", Main.locByServ.get(message.getServer().get()));

		if (message.getContent().startsWith("!ver") || message.getContent().startsWith("!version")) {
			ITransaction transaction = Sentry.startTransaction("!version", "command");
			message.getChannel().sendMessage(com.github.ungarscool1.Roboto.commands.utility.VersionCommand.output(language, api));
			transaction.finish(SpanStatus.OK);
		}
	}
}
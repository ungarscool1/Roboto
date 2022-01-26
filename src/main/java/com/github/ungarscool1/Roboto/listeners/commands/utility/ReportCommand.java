package com.github.ungarscool1.Roboto.listeners.commands.utility;

import com.github.ungarscool1.Roboto.commands.utility.HelpCommand;
import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;

public class ReportCommand implements MessageCreateListener {
	@Override
	public void onMessageCreate(MessageCreateEvent event) {
		Message message = event.getMessage();
		if (!message.getServer().isPresent() || message.getAuthor().isBotUser())
			return;
		if (message.getContent().startsWith("!report")) {
			ITransaction transaction = Sentry.startTransaction("!report", "command");
			message.getChannel().sendMessage(com.github.ungarscool1.Roboto.commands.utility.ReportCommand.output());
			transaction.finish(SpanStatus.OK);
		}
	}
}

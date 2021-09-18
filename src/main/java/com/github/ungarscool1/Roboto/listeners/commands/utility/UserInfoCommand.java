package com.github.ungarscool1.Roboto.listeners.commands.utility;

import com.github.ungarscool1.Roboto.Main;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class UserInfoCommand implements MessageCreateListener {
	@Override
	public void onMessageCreate(MessageCreateEvent event) {
		Message message = event.getMessage();
		if (!message.getServer().isPresent() || message.getAuthor().isBotUser())
			return;
		ResourceBundle language = ResourceBundle.getBundle("lang.lang", Main.locByServ.get(message.getServer().get()));

		if (message.getContent().startsWith("!ui")) {
			ITransaction transaction = Sentry.startTransaction("!ui", "command");
			String[] args = message.getContent().split(" ");

			message.getChannel().sendMessage(com.github.ungarscool1.Roboto.commands.utility.UserInfoCommand.output(args, message.getServer().get(), language, message.getUserAuthor().get()));
			transaction.setStatus(SpanStatus.OK);
		}
	}
}

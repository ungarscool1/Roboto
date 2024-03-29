package com.github.ungarscool1.Roboto.listeners.commands.admin;

import com.github.ungarscool1.Roboto.Main;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.ResourceBundle;

public class AdminHelpCommand implements MessageCreateListener {
	@Override
	public void onMessageCreate(MessageCreateEvent event) {
		Message message = event.getMessage();
		if (!message.getServer().isPresent() || message.getAuthor().isBotUser())
			return;
		ResourceBundle language = ResourceBundle.getBundle("lang.lang", Main.locByServ.get(message.getServer().get()));

		if (message.getAuthor().canBanUsersFromServer() && message.getContent().equalsIgnoreCase("@help")) {
			ITransaction transaction = Sentry.startTransaction("@help", "command");
			EmbedBuilder embed = new EmbedBuilder();
			embed.setTitle(language.getString("admin.help.name"))
					.addField(language.getString("admin.help.ban.name"), language.getString("admin.help.ban.desc"))
					.addField(language.getString("admin.help.kick.name"), language.getString("admin.help.kick.desc"))
					.addField("@lang <lang>", language.getString("help.lang.desc"))
					.setFooter(language.getString("admin.help.footer"));
			message.getChannel().sendMessage(embed);
			transaction.finish(SpanStatus.OK);
		}
	}
}

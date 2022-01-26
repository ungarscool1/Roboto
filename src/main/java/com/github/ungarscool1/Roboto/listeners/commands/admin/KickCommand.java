package com.github.ungarscool1.Roboto.listeners.commands.admin;

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
import java.util.ResourceBundle;

public class KickCommand implements MessageCreateListener {
	@Override
	public void onMessageCreate(MessageCreateEvent event) {
		Message message = event.getMessage();
		if (!message.getServer().isPresent() || message.getAuthor().isBotUser())
			return;
		ResourceBundle language = ResourceBundle.getBundle("lang.lang", Main.locByServ.get(message.getServer().get()));

		if (message.getAuthor().canBanUsersFromServer() && message.getContent().startsWith("@kick")) {
			ITransaction transaction = Sentry.startTransaction("@kick", "command");
			String[] args = message.getContent().split(" ");
			EmbedBuilder embed = new EmbedBuilder()
					.setTitle(language.getString("admin.kick.name"))
					.setColor(Color.RED)
					.setFooter(language.getString("admin.help.footer"));
			if (args.length == 1) {
				embed.setDescription(language.getString("admin.kick.missingargs"));
				transaction.finish(SpanStatus.INVALID_ARGUMENT);
			} else if (args.length > 1) {
				User toKick = null;
				String description;
				StringBuilder reason = new StringBuilder();
				try {
					toKick = message.getMentionedUsers().get(0);
				} catch (Exception e) {
					embed.setDescription(language.getString("admin.kick.missingargs"));
					message.getChannel().sendMessage(embed);
					transaction.finish(SpanStatus.INVALID_ARGUMENT);
					return;
				}
				if (args.length == 2)
					description = String.format(language.getString("admin.kick.desc.default"), toKick.getDiscriminatedName());
				else {
					for (int i = 2; i < args.length; i++)
						reason.append(args[i] + " ");
					description = String.format(language.getString("admin.kick.desc"), toKick.getDiscriminatedName(), reason);
				}
				embed.setDescription(description)
						.setAuthor(message.getAuthor())
						.setColor(Color.GREEN);
				if (args.length == 2)
					description = language.getString("admin.kick.toKick.desc.default");
				else
					description = String.format(language.getString("admin.kick.toKick.desc"), reason);
				toKick.sendMessage(new EmbedBuilder().setTitle(language.getString("admin.kick.toKick.name"))
						.setDescription(description)
						.setAuthor(message.getAuthor()));
				message.getServer().get().kickUser(toKick, reason.toString());
				transaction.finish(SpanStatus.OK);
			}
			message.getChannel().sendMessage(embed);
		}
	}
}

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
			
			transaction.setStatus(SpanStatus.OK);
			if (args.length > 1) {
				for (int i = 1; i < args.length; i++) {
					String user = args[i];
					EmbedBuilder embedBuilder = new EmbedBuilder();
					boolean doExist = false;

					if (Pattern.matches("[0-9]+", user) && message.getServer().get().getMemberById(user).isPresent()) {
						doExist = true;
					} else if (Pattern.matches("([a-zA-Z0-9]+.#.[0-9]+)", user) && message.getServer().get().getMemberByDiscriminatedNameIgnoreCase(user).isPresent()) {
						doExist = true;
						user = message.getServer().get().getMemberByDiscriminatedNameIgnoreCase(user).get().getIdAsString();
					} else if (user.contains("<@") && user.contains(">")) {
						if (user.contains("!")) {
							user = user.substring(3, user.indexOf(">"));
						} else {
							user = user.substring(2, user.indexOf(">"));
						}
						if (message.getServer().get().getMemberById(user).isPresent()) {
							doExist = true;
						}
					}
					if (doExist) {
						User u = message.getServer().get().getMemberById(user).get();
						Date joinDate = Date.from(u.getJoinedAtTimestamp(message.getServer().get()).get());
						Date creationDate = Date.from(u.getCreationTimestamp());
						SimpleDateFormat formatter = new SimpleDateFormat(language.getString("ui.date.format"));

						String comp = "";
						if (u.isBot())
							comp = " <:bot:745718850041020446>";
						Color color = Color.GREEN;
						if (u.getRoleColor(message.getServer().get()).isPresent()) {
							color = u.getRoleColor(message.getServer().get()).get();
						}
						embedBuilder.setTitle(String.format(language.getString("ui.title"), u.getName()) + comp)
								.addField(language.getString("ui.join.date"), formatter.format(joinDate), true)
								.addField(language.getString("ui.register.date"), formatter.format(creationDate), true)
								.setThumbnail(u.getAvatar())
								.setColor(color);

					} else {
						embedBuilder.setTitle(language.getString("ui.notFound"))
								.addField(language.getString("ui.searchedUser"), user)
								.setDescription(String.format(language.getString("ui.notFound.desc"), user))
								.setColor(Color.ORANGE);
						transaction.setStatus(SpanStatus.NOT_FOUND);
					}
					message.getChannel().sendMessage(embedBuilder);
				}
			} else {
				EmbedBuilder embedBuilder = new EmbedBuilder();
				User u = message.getAuthor().asUser().get();

				// Get and convert join date to human readable value
				Date joinDate = u.getJoinedAtTimestamp(message.getServer().get()).isPresent() ? Date.from(u.getJoinedAtTimestamp(message.getServer().get()).get()) : null;
				Date creationDate = Date.from(u.getCreationTimestamp());
				SimpleDateFormat formatter = new SimpleDateFormat(language.getString("ui.date.format"));

				Color color = Color.GREEN;
				if (u.getRoleColor(message.getServer().get()).isPresent()) {
					color = u.getRoleColor(message.getServer().get()).get();
				}

				embedBuilder.setTitle(String.format(language.getString("ui.title"), u.getName()))
						.addField(language.getString("ui.join.date"), joinDate != null ? formatter.format(joinDate) : "Failed to fetch join date", true)
						.addField(language.getString("ui.register.date"), formatter.format(creationDate), true)
						.setThumbnail(u.getAvatar())
						.setColor(color);
				message.getChannel().sendMessage(embedBuilder);
				transaction.finish();
			}
		}
	}
}

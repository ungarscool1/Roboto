package com.github.ungarscool1.Roboto.listeners.commands.utility;

import com.github.ungarscool1.Roboto.Main;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class ServerInfoCommand implements MessageCreateListener {
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        Message message = event.getMessage();
        if (!message.getServer().isPresent() || message.getAuthor().isBotUser())
            return;
        ResourceBundle language = ResourceBundle.getBundle("lang.lang", Main.locByServ.get(message.getServer().get()));

        if (message.getContent().startsWith("!si")) {
			ITransaction transaction = Sentry.startTransaction("!si", "command");
            Server server = message.getServer().get();
            EmbedBuilder embedBuilder = new EmbedBuilder();

            // Get and convert join date to human readable value
            Date creationDate = Date.from(server.getCreationTimestamp());
            SimpleDateFormat formatter = new SimpleDateFormat(language.getString("ui.date.format"));

            // Get member and bot count
            int[] members = {0, 0};
            server.getMembers().forEach(member -> {
                if (member.isBot())
                    members[0]++;
                else
                    members[1]++;
            });

            try {
				embedBuilder.setTitle(String.format(language.getString("si.title"), server.getName()))
				        .addField(language.getString("si.id"), server.getIdAsString(), true)
				        .addField(language.getString("si.creation.date"), formatter.format(creationDate), true)
				        .addField(language.getString("si.owner"), server.requestOwner().get().getDiscriminatedName())
				        .addField(language.getString("si.memberscount"), members[1] + " " + language.getString("si.members"))
				        .addField(language.getString("si.botscount"), members[0] + " bots")
				        .setColor(Color.GREEN);
			} catch (Exception e) {
				Sentry.captureException(e);
			}
            message.getChannel().sendMessage(embedBuilder);
            transaction.finish(SpanStatus.OK);
        }
    }
}

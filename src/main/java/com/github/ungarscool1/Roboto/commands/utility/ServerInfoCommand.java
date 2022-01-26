package com.github.ungarscool1.Roboto.commands.utility;

import io.sentry.Sentry;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class ServerInfoCommand {
    public static EmbedBuilder output(ResourceBundle language, Server server) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

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
        return embedBuilder;
    }
}

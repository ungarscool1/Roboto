package com.github.ungarscool1.Roboto.commands.utility;

import io.sentry.SpanStatus;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class UserInfoCommand {
    public static EmbedBuilder output(String[] args, Server server, ResourceBundle language, User sender) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        if (args.length > 1) {
            for (int i = 1; i < args.length; i++) {
                String user = args[i];
                boolean doExist = false;

                if (Pattern.matches("[0-9]+", user) && server.getMemberById(user).isPresent()) {
                    doExist = true;
                } else if (Pattern.matches("([a-zA-Z0-9]+.#.[0-9]+)", user) && server.getMemberByDiscriminatedNameIgnoreCase(user).isPresent()) {
                    doExist = true;
                    user = server.getMemberByDiscriminatedNameIgnoreCase(user).get().getIdAsString();
                } else if (user.contains("<@") && user.contains(">")) {
                    if (user.contains("!")) {
                        user = user.substring(3, user.indexOf(">"));
                    } else {
                        user = user.substring(2, user.indexOf(">"));
                    }
                    if (server.getMemberById(user).isPresent()) {
                        doExist = true;
                    }
                }
                if (doExist) {
                    User u = server.getMemberById(user).get();
                    Date joinDate = Date.from(u.getJoinedAtTimestamp(server).get());
                    Date creationDate = Date.from(u.getCreationTimestamp());
                    SimpleDateFormat formatter = new SimpleDateFormat(language.getString("ui.date.format"));

                    String comp = "";
                    if (u.isBot())
                        comp = " <:bot:745718850041020446>";
                    Color color = Color.GREEN;
                    if (u.getRoleColor(server).isPresent()) {
                        color = u.getRoleColor(server).get();
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
                }
            }
        } else {
            Date joinDate = Date.from(sender.getJoinedAtTimestamp(server).get());
            Date creationDate = Date.from(sender.getCreationTimestamp());
            SimpleDateFormat formatter = new SimpleDateFormat(language.getString("ui.date.format"));

            Color color = Color.GREEN;
            if (sender.getRoleColor(server).isPresent()) {
                color = sender.getRoleColor(server).get();
            }

            embedBuilder.setTitle(String.format(language.getString("ui.title"), sender.getName()))
                    .addField(language.getString("ui.join.date"), formatter.format(joinDate), true)
                    .addField(language.getString("ui.register.date"), formatter.format(creationDate), true)
                    .setThumbnail(sender.getAvatar())
                    .setColor(color);
        }
        return embedBuilder;
    }
}

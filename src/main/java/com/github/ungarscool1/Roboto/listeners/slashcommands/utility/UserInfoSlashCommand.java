package com.github.ungarscool1.Roboto.listeners.slashcommands.utility;

import com.github.ungarscool1.Roboto.Main;
import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class UserInfoSlashCommand implements SlashCommandCreateListener {
    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();
        ResourceBundle language = null;
        Server server = null;

        if (!interaction.getServer().isPresent() || interaction.getUser().isYourself())
            return;
        server = interaction.getServer().get();
        language = ResourceBundle.getBundle("lang.lang", Main.locByServ.get(server));
        if (!interaction.getCommandName().equals("ui"))
            return;
        ITransaction transaction = Sentry.startTransaction("/ui", "Slash Command");
        SlashCommandInteractionOption option = interaction.getOptionByIndex(0).isPresent() ? interaction.getOptionByIndex(0).get() : null;

        transaction.setStatus(SpanStatus.OK);
        if (option != null) {
            String user = option.getStringValue().get();
            EmbedBuilder embedBuilder = new EmbedBuilder();
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
                transaction.setStatus(SpanStatus.NOT_FOUND);
            }
            interaction.createImmediateResponder().addEmbed(embedBuilder).respond().join();
        } else {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            User u = interaction.getUser();

            // Get and convert join date to human readable value
            Date joinDate = Date.from(u.getJoinedAtTimestamp(server).get());
            Date creationDate = Date.from(u.getCreationTimestamp());
            SimpleDateFormat formatter = new SimpleDateFormat(language.getString("ui.date.format"));

            Color color = Color.GREEN;
            if (u.getRoleColor(server).isPresent()) {
                color = u.getRoleColor(server).get();
            }

            embedBuilder.setTitle(String.format(language.getString("ui.title"), u.getName()))
                    .addField(language.getString("ui.join.date"), formatter.format(joinDate), true)
                    .addField(language.getString("ui.register.date"), formatter.format(creationDate), true)
                    .setThumbnail(u.getAvatar())
                    .setColor(color);
            interaction.createImmediateResponder().addEmbed(embedBuilder).respond().join();
            transaction.finish();
        }
    }
}

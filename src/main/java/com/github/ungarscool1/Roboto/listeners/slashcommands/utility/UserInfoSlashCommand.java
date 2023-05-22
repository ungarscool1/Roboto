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
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

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
        String arg = interaction.getArgumentByIndex(0).isPresent() ? interaction.getArgumentByIndex(0).get().getMentionableValue().get().getMentionTag() : null;

        transaction.setStatus(SpanStatus.OK);
        if (arg != null) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            boolean doExist = false;

            if (arg.contains("!")) {
                arg = arg.substring(3, arg.indexOf(">"));
            } else {
                arg = arg.substring(2, arg.indexOf(">"));
            }
            if (server.getMemberById(arg).isPresent()) {
                User u = server.getMemberById(arg).get();
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
                        .addField(language.getString("ui.searchedUser"), arg)
                        .setDescription(String.format(language.getString("ui.notFound.desc"), arg))
                        .setColor(Color.ORANGE);
                transaction.setStatus(SpanStatus.NOT_FOUND);
            }
            interaction.createImmediateResponder().addEmbed(embedBuilder).respond().join();
        } else {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            User u = interaction.getUser();

            // Get and convert join date to human readable value
            Date joinDate = Date.from(u.getJoinedAtTimestamp(server).isPresent() ? u.getJoinedAtTimestamp(server).get() : u.getCreationTimestamp());
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

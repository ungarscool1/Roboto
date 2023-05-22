package com.github.ungarscool1.Roboto.listeners.slashcommands.utility;

import com.github.ungarscool1.Roboto.Main;
import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class ServerInfoSlashCommand implements SlashCommandCreateListener {
    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();
        ResourceBundle language = null;

        if (!interaction.getServer().isPresent() || interaction.getUser().isYourself())
            return;
        language = ResourceBundle.getBundle("lang.lang", Main.locByServ.get(interaction.getServer().get()));
        if (!interaction.getCommandName().equals("si"))
            return;
        ITransaction transaction = Sentry.startTransaction("/si", "Slash Command");
        Server server = interaction.getServer().get();
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
                    .setThumbnail(server.getIcon().map(t -> t.getUrl().toString()).orElse("https://cdn.discordapp.com/embed/avatars/0.png"))
                    .setColor(Color.GREEN);
        } catch (Exception e) {
            Sentry.captureException(e);
        }
        interaction.createImmediateResponder().addEmbed(embedBuilder).respond().join();
        transaction.finish(SpanStatus.OK);
    }
}

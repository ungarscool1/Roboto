package com.github.ungarscool1.Roboto.listeners.slashcommands.utility;

import com.github.ungarscool1.Roboto.Main;
import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.awt.*;
import java.util.ResourceBundle;

public class HelpCommand implements SlashCommandCreateListener {
    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();
        ResourceBundle language = null;

        if (!interaction.getServer().isPresent() || interaction.getUser().isYourself())
            return;
        language = ResourceBundle.getBundle("lang.lang", Main.locByServ.get(interaction.getServer().get()));
        if (!interaction.getCommandName().equals("help"))
            return;
        ITransaction transaction = Sentry.startTransaction("/help", "Slash Command");
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(language.getString("help"))
                .addField(language.getString("help.game.cmd"), language.getString("help.game.desc"))
                .addField("@help", language.getString("help.admin.desc"))
                .addField("/ui [args]", language.getString("help.ui.desc"))
                .addField("/si", language.getString("help.si.desc"))
                .addField("/version", language.getString("help.ver.desc"))
                .addField("/vote", language.getString("help.vote.desc"))
                .addField("/report", language.getString("help.report.desc"))
                .addField("/discoboom", language.getString("help.discoboom.desc"))
                .setColor(Color.GREEN)
                .setFooter("Roboto v.3 by Ungarscool1");
        interaction.createImmediateResponder().addEmbed(embedBuilder).respond().join();
        transaction.finish(SpanStatus.OK);
    }
}

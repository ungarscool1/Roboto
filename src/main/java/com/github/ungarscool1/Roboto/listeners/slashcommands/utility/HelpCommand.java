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
        interaction.createImmediateResponder().addEmbed(com.github.ungarscool1.Roboto.commands.utility.HelpCommand.output(language)).respond().join();
        transaction.finish(SpanStatus.OK);
    }
}

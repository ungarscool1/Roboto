package com.github.ungarscool1.Roboto.listeners.slashcommands.utility;

import com.github.ungarscool1.Roboto.Main;
import com.github.ungarscool1.Roboto.commands.utility.ServerInfoCommand;
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

        interaction.createImmediateResponder().addEmbed(ServerInfoCommand.output(language, server)).respond().join();
        transaction.finish(SpanStatus.OK);
    }
}

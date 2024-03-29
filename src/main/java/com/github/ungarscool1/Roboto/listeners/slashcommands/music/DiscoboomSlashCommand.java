package com.github.ungarscool1.Roboto.listeners.slashcommands.music;

import com.github.ungarscool1.Roboto.Main;
import com.github.ungarscool1.Roboto.listeners.commands.music.DiscoboomSubCommand;
import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.awt.*;
import java.util.ResourceBundle;

public class DiscoboomSlashCommand implements SlashCommandCreateListener {
    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();
        ResourceBundle language = null;
        Server server = null;

        if (!interaction.getServer().isPresent() || interaction.getUser().isYourself())
            return;
        language = ResourceBundle.getBundle("lang.lang", Main.locByServ.get(interaction.getServer().get()));
        server = event.getSlashCommandInteraction().getServer().get();
        if (!interaction.getCommandName().equals("discoboom"))
            return;
        ITransaction transaction = Sentry.startTransaction("/discoboom", "Slash Command");
        String arg = interaction.getOptionByIndex(1).isPresent() ? interaction.getOptionByIndex(1).get().getStringValue().get() : null;
        EmbedBuilder embedBuilder = null;
        if (interaction.getOptionByName("Help").isPresent())
            embedBuilder = DiscoboomSubCommand.help(server, transaction);
        else if (interaction.getOptionByName("Play").isPresent())
            embedBuilder = DiscoboomSubCommand.play(server, interaction.getChannel().get(), interaction.getUser(), arg, transaction);
        else
            embedBuilder = DiscoboomSubCommand.help(server, transaction);
        interaction.createImmediateResponder().addEmbed(embedBuilder).respond().join();
        transaction.finish(SpanStatus.OK);
    }
}

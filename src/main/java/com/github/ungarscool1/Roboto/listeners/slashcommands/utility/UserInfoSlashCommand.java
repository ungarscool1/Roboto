package com.github.ungarscool1.Roboto.listeners.slashcommands.utility;

import com.github.ungarscool1.Roboto.Main;
import com.github.ungarscool1.Roboto.commands.utility.UserInfoCommand;
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
        User sender = interaction.getUser();
        Server server = null;

        if (!interaction.getServer().isPresent() || interaction.getUser().isYourself())
            return;
        server = interaction.getServer().get();
        language = ResourceBundle.getBundle("lang.lang", Main.locByServ.get(server));
        if (!interaction.getCommandName().equals("ui"))
            return;
        ITransaction transaction = Sentry.startTransaction("/ui", "Slash Command");
        SlashCommandInteractionOption option = interaction.getFirstOption().isPresent() ? interaction.getFirstOption().get() : null;

        transaction.setStatus(SpanStatus.OK);
        interaction.createImmediateResponder().addEmbed(UserInfoCommand.output(null, server, language, sender)).respond().join();
    }
}

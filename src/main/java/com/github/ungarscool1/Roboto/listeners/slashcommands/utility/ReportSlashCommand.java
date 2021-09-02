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

public class ReportSlashCommand implements SlashCommandCreateListener {
    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();
        ResourceBundle language = null;

        if (!interaction.getServer().isPresent() || interaction.getUser().isYourself())
            return;
        language = ResourceBundle.getBundle("lang.lang", Main.locByServ.get(interaction.getServer().get()));
        if (!interaction.getCommandName().equals("report"))
            return;
        ITransaction transaction = Sentry.startTransaction("/report", "Slash Command");
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Report")
                .setDescription("Create issue on GitHub")
                .setUrl("https://github.com/ungarscool1/Roboto-v2/issues")
                .addField("Link", "https://github.com/ungarscool1/Roboto-v2/issues")
                .addField("Template", "https://github.com/ungarscool1/Roboto-v2/issues/new?assignees=&labels=bug&template=bug_report.md&title=")
                .addField("Feature in progress", "This feature is not finished ! However, you can create an issue on GitHub")
                .setColor(Color.GREEN)
                .setFooter("Roboto v.3 by Ungarscool1");
        interaction.createImmediateResponder().addEmbed(embedBuilder).respond().join();
        transaction.finish(SpanStatus.OK);
    }
}

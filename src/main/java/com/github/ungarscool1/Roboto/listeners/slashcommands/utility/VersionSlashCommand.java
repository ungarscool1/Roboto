package com.github.ungarscool1.Roboto.listeners.slashcommands.utility;

import com.github.ungarscool1.Roboto.Main;
import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.awt.*;
import java.util.ResourceBundle;

public class VersionSlashCommand implements SlashCommandCreateListener {
    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction interaction = event.getSlashCommandInteraction();
        ResourceBundle language = null;
        DiscordApi api = interaction.getApi();

        if (!interaction.getServer().isPresent() || interaction.getUser().isYourself())
            return;
        language = ResourceBundle.getBundle("lang.lang", Main.locByServ.get(interaction.getServer().get()));
        if (!interaction.getCommandName().equals("version"))
            return;
        ITransaction transaction = Sentry.startTransaction("/version", "Slash Command");
        EmbedBuilder embedBuilder = new EmbedBuilder();
        int users = api.getServers().stream().mapToInt(Server::getMemberCount).sum();
        try {
            embedBuilder.setTitle(language.getString("version.name"))
                    .addField("Version", "3.1.0")
                    .addField(language.getString("version.lib.name"), language.getString("version.lib.desc"))
                    .addField("Build", "020921-02.10")
                    .addField("Bot owner", api.getOwner().get().getDiscriminatedName())
                    .addField(language.getString("version.github"), "https://github.com/ungarscool1/Roboto-v2")
                    .addField(language.getString("version.listen.user"), users + " " + language.getString("version.users"))
                    .addField(language.getString("version.listen"), api.getServers().size() + " " + language.getString("version.servers"))
                    .setColor(Color.GREEN)
                    .setFooter("Roboto v.3 by Ungarscool1");
        } catch (Exception e) {
            embedBuilder.setTitle(language.getString("errors.title"))
                    .setDescription(language.getString("errors.unkown_error"))
                    .addField("Report bugs in English", "https://github.com/ungarscool1/Roboto-v2/issues/new?assignees=&labels=bug&template=bug_report.md&title=!ver%20Command%20fail")
                    .setColor(Color.RED);
        }
        interaction.createImmediateResponder().addEmbed(embedBuilder).respond().join();
        transaction.finish(SpanStatus.OK);
    }
}

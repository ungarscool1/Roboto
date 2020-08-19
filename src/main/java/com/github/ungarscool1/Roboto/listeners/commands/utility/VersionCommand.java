package com.github.ungarscool1.Roboto.listeners.commands.utility;

import com.github.ungarscool1.Roboto.Main;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;
import java.util.ResourceBundle;

public class VersionCommand implements MessageCreateListener {
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        Message message = event.getMessage();
        ResourceBundle language = ResourceBundle.getBundle("lang.lang", Main.locByServ.get(message.getServer().get()));
        DiscordApi api = event.getApi();

        if (!message.getServer().isPresent() || message.getAuthor().isBotUser())
            return;
        if (message.getContent().equalsIgnoreCase("!ver") || message.getContent().equalsIgnoreCase("!version")) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            int users = api.getServers().stream().mapToInt(Server::getMemberCount).sum();
            try {
                embedBuilder.setTitle(language.getString("version.name"))
                        .addField("Version", "3.0.0 DEV")
                        .addField(language.getString("version.lib.name"), language.getString("version.lib.desc"))
                        .addField("Build", "190820-17.1")
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
            message.getChannel().sendMessage(embedBuilder);
        }
    }
}

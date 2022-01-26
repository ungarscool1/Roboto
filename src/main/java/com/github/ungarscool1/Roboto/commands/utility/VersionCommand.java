package com.github.ungarscool1.Roboto.commands.utility;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.awt.*;
import java.util.ResourceBundle;

public class VersionCommand {
    public static EmbedBuilder output(ResourceBundle language, DiscordApi api) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        int users = api.getServers().stream().mapToInt(Server::getMemberCount).sum();
        User user = api.getOwner().join();
        embedBuilder.setTitle(language.getString("version.name"))
                .addField("Version", "3.1.0")
                .addField(language.getString("version.lib.name"), language.getString("version.lib.desc"))
                .addField("Build", "020921-02.10")
                .addField("Bot owner", user.getDiscriminatedName())
                .addField(language.getString("version.github"), "https://github.com/ungarscool1/Roboto-v2")
                .addField(language.getString("version.listen.user"), users + " " + language.getString("version.users"))
                .addField(language.getString("version.listen"), api.getServers().size() + " " + language.getString("version.servers"))
                .setColor(Color.GREEN)
                .setFooter("Roboto v.3 by Ungarscool1");
        return embedBuilder;
    }
}

package com.github.ungarscool1.Roboto.listeners.commands;

import com.github.ungarscool1.Roboto.Main;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;
import java.util.ResourceBundle;

public class AdminCommand implements MessageCreateListener {
    public void onMessageCreate(MessageCreateEvent event) {
        Message message = event.getMessage();
        ResourceBundle language = ResourceBundle.getBundle("lang.lang", Main.locByServ.get(message.getServer().get()));
        if (!message.getServer().isPresent() || message.getAuthor().isBotUser())
            return;
        if (message.getAuthor().canBanUsersFromServer() && message.getContent().contains("@ban")) {
            String[] args = message.getContent().split(" ");
            EmbedBuilder embed = new EmbedBuilder();
            if (args.length == 1)
                embed.setTitle(language.getString("admin.ban.name"))
                    .setDescription(language.getString("admin.ban.missingargs"))
                    .setColor(Color.RED)
                    .setFooter("Roboto v.3 by Ungarscool1");
            if (args.length > 1) {
                User toBan = message.getMentionedUsers().get(0);
                String description;
                StringBuilder reason = new StringBuilder();
                if (args.length == 2)
                    description = String.format(language.getString("admin.ban.desc.default"), toBan.getDiscriminatedName());
                else {
                    for (int i = 2; i < args.length; i++)
                        reason.append(args[i] + " ");
                    description = String.format(language.getString("admin.ban.desc"), toBan.getDiscriminatedName(), reason.toString());
                }
                embed.setTitle(language.getString("admin.ban.name"))
                        .setDescription(description)
                        .setAuthor(message.getAuthor())
                        .setFooter("Roboto v.3 by Ungarscool1")
                        .setColor(Color.GREEN);
                if (args.length == 2)
                    description = language.getString("admin.ban.toBan.desc.default");
                else
                    description = String.format(language.getString("admin.ban.toBan.desc"), reason.toString());
                toBan.sendMessage(new EmbedBuilder().setTitle(language.getString("admin.ban.toBan.name"))
                        .setDescription(description)
                        .setAuthor(message.getAuthor())
                        .setColor(Color.RED));
                message.getChannel().sendMessage(embed);
                message.getServer().get().banUser(toBan, 0, reason.toString());
            }
        }
    }
}

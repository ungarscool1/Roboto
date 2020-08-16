package com.github.ungarscool1.Roboto.listeners.commands;

import com.github.ungarscool1.Roboto.Main;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.entity.user.UserStatus;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;
import java.util.Locale;
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

        if (message.getContent().equalsIgnoreCase("@@info") && message.getAuthor().isBotOwner()) {
            System.out.println("Getting servers info...");
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Informations de tout les serveurs").setDescription("Toutes les informations des serveurs sont affichés ici");
            event.getApi().getServers().forEach(server -> {
                embed.addField(server.getName(), "Owner: " + server.getOwner().getDiscriminatedName() + "\nIl y a " + server.getMemberCount() + " utilisateurs sur le serveur.\nRégion: " + server.getRegion().getName() + "\nEst admin ? " + server.isAdmin(event.getApi().getYourself()) + "\nLangue du bot: " + Main.locByServ.get(server).getDisplayLanguage(Locale.FRANCE) + " - " + Main.locByServ.get(server).getDisplayCountry(Locale.FRANCE));
            });
            embed.setAuthor(event.getApi().getYourself()).setColor(Color.GREEN);
            message.getChannel().sendMessage(embed);
        }

        if (message.getContent().contains("@@changeGame") && message.getAuthor().isBotOwner()) {
            DiscordApi api = event.getApi();
            String arg = message.getContent().substring(13);
            if (arg.length() == 0)
                api.updateActivity(ActivityType.LISTENING, api.getServers().size() + " servers");
            else
                api.updateActivity(arg);
        }

        if (message.getContent().equalsIgnoreCase("@@maintenance") && message.getAuthor().isBotOwner()) {
            DiscordApi api = event.getApi();
            if (api.getStatus().equals(UserStatus.DO_NOT_DISTURB)) {
                message.getChannel().sendMessage("Mode maintenance désactivé !");
                api.updateStatus(UserStatus.ONLINE);
                api.updateActivity(ActivityType.LISTENING, api.getServers().size() + " servers");
            } else {
                message.getChannel().sendMessage("Mode maintenance activé !");
                api.updateStatus(UserStatus.DO_NOT_DISTURB);
                api.updateActivity("Maintenance mode...");
            }
        }

        if (message.getAuthor().canBanUsersFromServer() && message.getContent().equalsIgnoreCase("@help")) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle(language.getString("admin.help.name"))
                    .addField(language.getString("admin.help.ban.name"), language.getString("admin.help.ban.desc"))
                    .addField("@lang <lang>", language.getString("help.lang.desc"))
                    .setFooter("Roboto admin mode");
            message.getChannel().sendMessage(embed);
        }
    }
}

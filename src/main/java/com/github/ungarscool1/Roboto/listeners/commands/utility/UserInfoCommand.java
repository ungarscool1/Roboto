package com.github.ungarscool1.Roboto.listeners.commands.utility;

import com.github.ungarscool1.Roboto.Main;
import org.javacord.api.entity.DiscordClient;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.entity.user.UserStatus;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class UserInfoCommand implements MessageCreateListener {
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        Message message = event.getMessage();
        ResourceBundle language = ResourceBundle.getBundle("lang.lang", Main.locByServ.get(message.getServer().get()));

        if (!message.getServer().isPresent() || message.getAuthor().isBotUser())
            return;
        if (message.getContent().contains("!ui") && message.getContent().indexOf("!ui") == 0) {
            String[] args = message.getContent().split(" ");

            if (args.length > 1) {
                for (int i = 1; i < args.length; i++) {
                    String user = args[i];
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    boolean doExist = false;

                    if (Pattern.matches("[0-9]+", user) && message.getServer().get().getMemberById(user).isPresent()) {
                        doExist = true;
                    } else if (Pattern.matches("([a-zA-Z0-9]+.#.[0-9]+)", user) && message.getServer().get().getMemberByDiscriminatedNameIgnoreCase(user).isPresent()) {
                        doExist = true;
                        user = message.getServer().get().getMemberByDiscriminatedNameIgnoreCase(user).get().getIdAsString();
                    } else if (user.contains("<@") && user.contains(">")) {
                        if (user.contains("!")) {
                            user = user.substring(3, user.indexOf(">"));
                        } else {
                            user = user.substring(2, user.indexOf(">"));
                        }
                        if (message.getServer().get().getMemberById(user).isPresent()) {
                            doExist = true;
                        }
                    }
                    if (doExist) {
                        User u = message.getServer().get().getMemberById(user).get();
                        Date joinDate = Date.from(u.getJoinedAtTimestamp(message.getServer().get()).get());
                        Date creationDate = Date.from(u.getCreationTimestamp());
                        SimpleDateFormat formatter = new SimpleDateFormat(language.getString("ui.date.format"));

                        // Get if user is connected on PC / Mobile / WEB
                        String connectedOn;
                        if (!u.getStatusOnClient(DiscordClient.DESKTOP).equals(UserStatus.OFFLINE)) {
                            connectedOn = "PC";
                        } else if (u.getStatusOnClient(DiscordClient.MOBILE).equals(UserStatus.ONLINE)) {
                            connectedOn = language.getString("ui.connectedOn.phone");
                        } else if (u.isBot()) {
                            connectedOn = language.getString("ui.connectedOn.notApplicable");
                        } else {
                            connectedOn = language.getString("ui.connectedOn.notSure");
                        }

                        String comp = "";
                        if (u.isBot())
                            comp = " <:bot:745718850041020446>";
                        Color color = Color.GREEN;
                        if (u.getRoleColor(message.getServer().get()).isPresent()) {
                            color = u.getRoleColor(message.getServer().get()).get();
                        }
                        embedBuilder.setTitle(String.format(language.getString("ui.title"), u.getName()) + comp)
                                .addField(language.getString("ui.join.date"), formatter.format(joinDate), true)
                                .addField(language.getString("ui.register.date"), formatter.format(creationDate), true)
                                .addField(language.getString("ui.status"), language.getString("ui.connect."+u.getStatus().getStatusString()), true)
                                .addField(language.getString("ui.connectedOn"), connectedOn, true)
                                .setThumbnail(u.getAvatar())
                                .setColor(color);

                    } else {
                        embedBuilder.setTitle(language.getString("ui.notFound"))
                                .addField(language.getString("ui.searchedUser"), user)
                                .setDescription(String.format(language.getString("ui.notFound.desc"), user))
                                .setColor(Color.ORANGE);
                    }
                    message.getChannel().sendMessage(embedBuilder);
                }
            } else {
                EmbedBuilder embedBuilder = new EmbedBuilder();
                User u = message.getAuthor().asUser().get();

                // Get and convert join date to human readable value
                Date joinDate = Date.from(u.getJoinedAtTimestamp(message.getServer().get()).get());
                Date creationDate = Date.from(u.getCreationTimestamp());
                SimpleDateFormat formatter = new SimpleDateFormat(language.getString("ui.date.format"));

                // Get if user is connected on PC / Mobile / WEB
                String connectedOn;
                if (!u.getStatusOnClient(DiscordClient.DESKTOP).equals(UserStatus.OFFLINE)) {
                    connectedOn = "PC";
                } else if (u.getStatusOnClient(DiscordClient.MOBILE).equals(UserStatus.ONLINE)) {
                    connectedOn = language.getString("ui.connectedOn.phone");
                } else {
                    connectedOn = language.getString("ui.connectedOn.notSure");
                }

                String status;
                if (u.getStatus().equals(UserStatus.OFFLINE))
                    status = language.getString("ui.connect.invisible");
                else
                    status = language.getString("ui.connect."+u.getStatus().getStatusString());

                Color color = Color.GREEN;
                if (u.getRoleColor(message.getServer().get()).isPresent()) {
                    color = u.getRoleColor(message.getServer().get()).get();
                }

                embedBuilder.setTitle(String.format(language.getString("ui.title"), u.getName()))
                        .addField(language.getString("ui.join.date"), formatter.format(joinDate), true)
                        .addField(language.getString("ui.register.date"), formatter.format(creationDate), true)
                        .addField(language.getString("ui.status"), status, true)
                        .addField(language.getString("ui.connectedOn"), connectedOn, true)
                        .setThumbnail(u.getAvatar())
                        .setColor(color);
                message.getChannel().sendMessage(embedBuilder);
            }
        }
    }
}

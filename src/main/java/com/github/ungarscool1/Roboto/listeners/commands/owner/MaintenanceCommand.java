package com.github.ungarscool1.Roboto.listeners.commands.owner;

import com.github.ungarscool1.Roboto.Main;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.user.UserStatus;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.ResourceBundle;

public class MaintenanceCommand implements MessageCreateListener {
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        Message message = event.getMessage();

        if (!message.getServer().isPresent() || message.getAuthor().isBotUser())
            return;
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
    }
}

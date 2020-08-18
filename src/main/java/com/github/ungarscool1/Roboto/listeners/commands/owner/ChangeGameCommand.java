package com.github.ungarscool1.Roboto.listeners.commands.owner;

import com.github.ungarscool1.Roboto.Main;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.message.Message;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.ResourceBundle;

public class ChangeGameCommand implements MessageCreateListener {
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        Message message = event.getMessage();
        ResourceBundle language = ResourceBundle.getBundle("lang.lang", Main.locByServ.get(message.getServer().get()));

        if (!message.getServer().isPresent() || message.getAuthor().isBotUser())
            return;
        if (message.getContent().contains("@@changeGame") && message.getAuthor().isBotOwner()) {
            DiscordApi api = event.getApi();
            String arg = message.getContent().substring(13);
            if (arg.length() == 0)
                api.updateActivity(ActivityType.LISTENING, api.getServers().size() + " servers");
            else
                api.updateActivity(arg);
        }
    }
}

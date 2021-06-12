package com.github.ungarscool1.Roboto.listeners.commands.utility;

import org.javacord.api.entity.message.Message;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

public class FunMessage implements MessageCreateListener {
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        Message message = event.getMessage();
        if (message.getContent().equals("Make the party blob !")) {
            message.getChannel().sendMessage("<a:partyblob:423936638473994261><a:partyblob:423936638473994261><a:partyblob:423936638473994261>");
        }
    }
}

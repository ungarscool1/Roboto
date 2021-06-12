package com.github.ungarscool1.Roboto.listeners.commands.utility;

import com.github.ungarscool1.Roboto.Main;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;
import java.util.ResourceBundle;

public class HelpCommand implements MessageCreateListener {
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        Message message = event.getMessage();
        if (!message.getServer().isPresent() || message.getAuthor().isBotUser())
            return;
        ResourceBundle language = ResourceBundle.getBundle("lang.lang", Main.locByServ.get(message.getServer().get()));

        if (message.getContent().startsWith("!help") || message.getContent().startsWith("!aide")) {
			ITransaction transaction = Sentry.startTransaction("!help", "command");
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(language.getString("help"))
                    .addField(language.getString("help.game.cmd"), language.getString("help.game.desc"))
                    .addField("@help", language.getString("help.admin.desc"))
                    .addField("!ui [args]", language.getString("help.ui.desc"))
                    .addField("!si", language.getString("help.si.desc"))
                    .addField("!ver", language.getString("help.ver.desc"))
                    .addField("!vote", language.getString("help.vote.desc"))
                    .addField("!report", language.getString("help.report.desc"))
                    .addField("!discoboom", language.getString("help.discoboom.desc"))
                    .setColor(Color.GREEN)
                    .setFooter("Roboto v.3 by Ungarscool1");
            message.getChannel().sendMessage(embedBuilder);
            transaction.finish(SpanStatus.OK);
        }
    }
}

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

public class ReportCommand implements MessageCreateListener {
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        Message message = event.getMessage();
        if (!message.getServer().isPresent() || message.getAuthor().isBotUser())
            return;
        ResourceBundle language = ResourceBundle.getBundle("lang.lang", Main.locByServ.get(message.getServer().get()));

        if (message.getContent().equalsIgnoreCase("!report")) {
			ITransaction transaction = Sentry.startTransaction("!report", "command");
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Report")
                    .setDescription("Create issue on GitHub")
                    .setUrl("https://github.com/ungarscool1/Roboto-v2/issues")
                    .addField("Link", "https://github.com/ungarscool1/Roboto-v2/issues")
                    .addField("Template", "https://github.com/ungarscool1/Roboto-v2/issues/new?assignees=&labels=bug&template=bug_report.md&title=")
                    .addField("Feature in progress", "This feature is not finished ! However, you can create an issue on GitHub")
                    .setColor(Color.GREEN)
                    .setFooter("Roboto v.3 by Ungarscool1");
            message.getChannel().sendMessage(embed);
            transaction.finish(SpanStatus.OK);
        }
    }
}

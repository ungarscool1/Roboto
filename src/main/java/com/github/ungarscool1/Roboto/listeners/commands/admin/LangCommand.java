package com.github.ungarscool1.Roboto.listeners.commands.admin;

import com.github.ungarscool1.Roboto.Main;
import com.github.ungarscool1.Roboto.ServerLanguage;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.Locale;
import java.util.ResourceBundle;

public class LangCommand implements MessageCreateListener {
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        Message message = event.getMessage();
        ResourceBundle language = ResourceBundle.getBundle("lang.lang", Main.locByServ.get(message.getServer().get()));

        if (!message.getServer().isPresent() || message.getAuthor().isBotUser())
            return;
        if ((message.getContent().contains("@lang") || message.getContent().contains("@language") || message.getContent().contains("@langue")) && message.getAuthor().isServerAdmin()) {
            EmbedBuilder embed = new EmbedBuilder();
            if (message.getContent().contains("en_US") || message.getContent().contains("fr_FR") || message.getContent().contains("es_ES")) {
                String lang = message.getContent().substring(message.getContent().indexOf(" ") + 1);
                lang = lang.substring(0, message.getContent().indexOf(" "));
                String[] l;
                l = lang.split("_");
                new ServerLanguage().setServerLanguage(message.getServer().get(), l[0] + "_" + l[1]);
                Main.locByServ.replace(message.getServer().get(), new Locale(l[0], l[1]));
                language = ResourceBundle.getBundle("lang.lang", new Locale(l[0], l[1]));
                embed.setTitle(language.getString("lang.changed.name"))
                        .setDescription(language.getString("lang.changed.desc"));
            } else {
                embed.setTitle(language.getString("lang.help.name"))
                        .addField(language.getString("lang.help.languages"), String.format("- fr_FR (%s)\n- en_US (%s)\n- es_ES (%s)", language.getString("lang.help.french.name"), language.getString("lang.help.english.name"), language.getString("lang.help.spanish.name")));
            }
            message.getChannel().sendMessage(embed);
        }
    }
}

package com.github.ungarscool1.Roboto.commands.utility;

import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;
import java.util.ResourceBundle;

public class HelpCommand {
    public static EmbedBuilder output(ResourceBundle language) {
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
        return embedBuilder;
    }
}

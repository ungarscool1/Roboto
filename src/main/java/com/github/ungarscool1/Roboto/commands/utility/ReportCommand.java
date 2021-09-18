package com.github.ungarscool1.Roboto.commands.utility;

import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;

public class ReportCommand {
    public static EmbedBuilder output() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Report")
                .setDescription("Create issue on GitHub")
                .setUrl("https://github.com/ungarscool1/Roboto-v2/issues")
                .addField("Link", "https://github.com/ungarscool1/Roboto-v2/issues")
                .addField("Template", "https://github.com/ungarscool1/Roboto-v2/issues/new?assignees=&labels=bug&template=bug_report.md&title=")
                .addField("Feature in progress", "This feature is not finished ! However, you can create an issue on GitHub")
                .setColor(Color.GREEN)
                .setFooter("Roboto v.3 by Ungarscool1");
        return embed;
    }
}

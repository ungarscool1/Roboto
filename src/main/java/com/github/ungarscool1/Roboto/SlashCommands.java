package com.github.ungarscool1.Roboto;

import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionBuilder;
import org.javacord.api.interaction.SlashCommandOptionType;

import java.util.ArrayList;
import java.util.List;

public class SlashCommands {
    private DiscordApi api = null;

    public SlashCommands(DiscordApi api) {
        this.api = api;
    }

    public void setup() {
        discoboomSetup();
        smallCommandSetup();
        userInformationSetup();
    }

    private void discoboomSetup() {
        List<SlashCommandOption> discoboomOptions = new ArrayList<>();
        discoboomOptions.add(new SlashCommandOptionBuilder()
                .setName("Help")
                .setDescription("Display the help")
                .setType(SlashCommandOptionType.SUB_COMMAND)
                .build());
        discoboomOptions.add(new SlashCommandOptionBuilder()
                .setName("Play")
                .setDescription("Play music")
                .setType(SlashCommandOptionType.SUB_COMMAND)
                .addOption(new SlashCommandOptionBuilder()
                        .setName("url")
                        .setDescription("Track's url")
                        .setRequired(true)
                        .setType(SlashCommandOptionType.STRING)
                        .build()
                )
                .build()
        );
        discoboomOptions.add(new SlashCommandOptionBuilder()
                .setName("Pause")
                .setDescription("Pause the dance floor")
                .setType(SlashCommandOptionType.SUB_COMMAND)
                .build()
        );
        discoboomOptions.add(new SlashCommandOptionBuilder()
                .setName("Next")
                .setDescription("Jump to the next song")
                .setType(SlashCommandOptionType.SUB_COMMAND)
                .build()
        );
        discoboomOptions.add(new SlashCommandOptionBuilder()
                .setName("Queue")
                .setDescription("Show the tracks queue")
                .setType(SlashCommandOptionType.SUB_COMMAND)
                .build()
        );
        discoboomOptions.add(new SlashCommandOptionBuilder()
                .setName("Clear")
                .setDescription("Clear the tracks queue")
                .setType(SlashCommandOptionType.SUB_COMMAND)
                .build()
        );
        discoboomOptions.add(new SlashCommandOptionBuilder()
                .setName("Stop")
                .setDescription("Stop the disco")
                .setType(SlashCommandOptionType.SUB_COMMAND)
                .build()
        );
        discoboomOptions.add(new SlashCommandOptionBuilder()
                .setName("Disconnect")
                .setDescription("Disco(nnect)")
                .setType(SlashCommandOptionType.SUB_COMMAND)
                .build()
        );
        SlashCommand.with("discoboom", "Enable discoboom mode", discoboomOptions).createGlobal(api).join();
    }

    private void userInformationSetup() {
        SlashCommand.with("ui", "Get user information").addOption(new SlashCommandOptionBuilder()
                .setName("user")
                .setDescription("User mention tag")
                .setRequired(true)
                .setType(SlashCommandOptionType.USER)
                .build()).createGlobal(api).join();
    }

    private void smallCommandSetup() {
        SlashCommand.with("help", "Display this help message.").createGlobal(api).join();
        SlashCommand.with("report", "Report a problem on GitHub").createGlobal(api).join();
        SlashCommand.with("si", "Get server information").createGlobal(api).join();
        SlashCommand.with("version", "Get bot version").createGlobal(api).join();
        SlashCommand.with("vote", "Create a poll").createGlobal(api).join();
    }

}

package com.github.ungarscool1.Roboto;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import com.github.ungarscool1.Roboto.listeners.ReacListener;
import com.github.ungarscool1.Roboto.listeners.commands.GameCommand;
import com.github.ungarscool1.Roboto.listeners.commands.UtilsCommand;
import com.github.ungarscool1.Roboto.listeners.commands.VoteCommand;

public class Main {
	public static DiscordApi API;
    public static void main(String[] args) {
        DiscordApi api = new DiscordApiBuilder().setToken(args[0]).login().join();
        API = api;
        api.addMessageCreateListener(new VoteCommand());
        api.addMessageCreateListener(new GameCommand());
        api.addMessageCreateListener(new UtilsCommand());
        
        api.addReactionAddListener(new ReacListener());
    }

}
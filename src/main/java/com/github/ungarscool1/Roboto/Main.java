package com.github.ungarscool1.Roboto;

import java.util.HashMap;
import java.util.Locale;

import org.discordbots.api.client.DiscordBotListAPI;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.util.logging.ExceptionLogger;

import com.github.ungarscool1.Roboto.listeners.ReacListener;
import com.github.ungarscool1.Roboto.listeners.commands.GameCommand;
import com.github.ungarscool1.Roboto.listeners.commands.UtilsCommand;
import com.github.ungarscool1.Roboto.listeners.commands.VoteCommand;

public class Main {
	
	public static HashMap<Server, Locale> locByServ = new HashMap<Server, Locale>();
	private static DiscordBotListAPI dbl;
	
    public static void main(String[] args) {
        new DiscordApiBuilder()
        	.setToken(args[0])
        	.setRecommendedTotalShards()
        	.join()
        	.loginAllShards()
        	.forEach(shardFuture -> shardFuture.thenAcceptAsync(Main::onShardLogin).exceptionally(ExceptionLogger.get()));
        dbl = new DiscordBotListAPI.Builder()
        	.token(args[1])
        	.botId("373199180161613824")
        	.build();
    }
    
    private static void onShardLogin(DiscordApi api) {
        System.out.println("Shard " + api.getCurrentShard() + " logged in!");
        
        dbl.setStats(api.getCurrentShard(), api.getTotalShards(), api.getServers().size());
        
        api.updateActivity(ActivityType.LISTENING, api.getServers().size() + " servers");
        
        api.getServers().forEach(server -> {
        	ServerLanguage serverLanguage = new ServerLanguage();
        	String l[] = {"", ""};
			l = serverLanguage.getServerLanguage(server).split("_");
        	locByServ.put(server, new Locale(l[0], l[1]));
        	//locByServ.put(server, new Locale("en", "US"));
        	api.updateActivity(ActivityType.LISTENING, api.getServers().size() + " servers");
        });
        
        api.addServerJoinListener(event -> {
        	locByServ.put(event.getServer(), new Locale("en", "US"));
        	api.updateActivity(ActivityType.LISTENING, api.getServers().size() + " servers");
        	dbl.setStats(api.getCurrentShard(), api.getTotalShards(), api.getServers().size());
        });
        
        api.addServerLeaveListener(event -> {
        	locByServ.remove(event.getServer());
        	api.updateActivity(ActivityType.LISTENING, api.getServers().size() + " servers");
        	dbl.setStats(api.getCurrentShard(), api.getTotalShards(), api.getServers().size());
        });
        
        api.addMessageCreateListener(new VoteCommand());
        api.addMessageCreateListener(new GameCommand(api));
        api.addMessageCreateListener(new UtilsCommand(api));
        
        api.addReactionAddListener(new ReacListener());
    }

}
package com.github.ungarscool1.Roboto;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Locale;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;

import com.github.ungarscool1.Roboto.listeners.commands.admin.*;
import com.github.ungarscool1.Roboto.listeners.commands.music.DiscoboomCommand;
import com.github.ungarscool1.Roboto.listeners.commands.owner.*;
import com.github.ungarscool1.Roboto.listeners.commands.utility.*;
import com.github.ungarscool1.Roboto.listeners.servers.*;
import com.google.gson.Gson;

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
	
	public static HashMap<Server, Locale> locByServ = new HashMap<>();
	public static DiscordBotListAPI dbl;
	public static Configuration config;
	
    public static void main(String[] args) {
    	Gson gson = new Gson();
        try {
            config = gson.fromJson(new FileReader(new File("config.json")), Configuration.class);
        } catch (Exception e) {
            System.err.println("The configuration file is missing.");
            System.exit(1);
        }
        System.out.println("Running on " + config.env + " mode");
    	Sentry.init(options -> {
    		  options.setDsn("https://638cad2e6bd84eb488e505925cf6da51@o553695.ingest.sentry.io/5803038");
    		  options.setTracesSampleRate(config.sentry_io_trace_sample_rate);
    		  options.setDebug(config.sentry_io_debug);
    		  options.setRelease("120621-22.1");
    		  options.setEnvironment(config.env);
    		  options.setEnableAutoSessionTracking(true);
    		});
		Sentry.startSession();
        new DiscordApiBuilder()
        	.setToken(config.bot_token)
        	.setRecommendedTotalShards()
        	.join()
        	.loginAllShards()
        	.forEach(shardFuture -> shardFuture.thenAcceptAsync(Main::onShardLogin).exceptionally(ExceptionLogger.get()));
        dbl = new DiscordBotListAPI.Builder()
        	.token(config.discord_bot_list_key)
        	.botId("373199180161613824")
        	.build();
    }
    
    private static void onShardLogin(DiscordApi api) {
		ITransaction transaction = Sentry.startTransaction("onShardLogin()", "Instance initialization");
        System.out.println("Shard " + api.getCurrentShard() + " logged in!");
        
        dbl.setStats(api.getCurrentShard(), api.getTotalShards(), api.getServers().size());
        
        api.updateActivity(ActivityType.LISTENING, api.getServers().size() + " servers");
        
        api.getServers().forEach(server -> {
			try {
				ServerLanguage serverLanguage = new ServerLanguage();
        		String[] l;
				l = serverLanguage.getServerLanguage(server).split("_");
        		locByServ.put(server, new Locale(l[0], l[1]));
			} catch (Exception e) {
				locByServ.put(server, new Locale("en", "US"));
				new ServerLanguage().addServer(server);
			}
        });
        
        api.addServerJoinListener(new JoinListener());
        api.addServerLeaveListener(new LeaveListener());

        api.addMessageCreateListener(new GameCommand(api));
        api.addMessageCreateListener(new UtilsCommand());
        api.addMessageCreateListener(new FunMessage());
		/**
		 * Utilities Commands
		 */
		api.addMessageCreateListener(new HelpCommand());
		api.addMessageCreateListener(new ReportCommand());
		api.addMessageCreateListener(new ServerInfoCommand());
		api.addMessageCreateListener(new UserInfoCommand());
		api.addMessageCreateListener(new VersionCommand());
		api.addMessageCreateListener(new VoteCommand());
		api.addMessageCreateListener(new DiscoboomCommand());
		/**
		 * Admin commands
		 */
		api.addMessageCreateListener(new AdminHelpCommand());
		api.addMessageCreateListener(new BanCommand());
		api.addMessageCreateListener(new KickCommand());
		api.addMessageCreateListener(new LangCommand());
		/**
		 * Owner commands
		 */
		api.addMessageCreateListener(new ChangeGameCommand());
		api.addMessageCreateListener(new MaintenanceCommand());
		api.addMessageCreateListener(new OwnerInfoCommand());
		api.addMessageCreateListener(new ChangeStatusCommand());

		api.addReactionAddListener(new ReacListener());
		transaction.setStatus(SpanStatus.OK);
        transaction.finish();
    }



}
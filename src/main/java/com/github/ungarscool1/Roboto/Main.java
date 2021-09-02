package com.github.ungarscool1.Roboto;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.github.ungarscool1.Roboto.listeners.slashcommands.music.DiscoboomSlashCommand;
import com.github.ungarscool1.Roboto.listeners.slashcommands.utility.ReportSlashCommand;
import com.github.ungarscool1.Roboto.listeners.slashcommands.utility.ServerInfoSlashCommand;
import com.github.ungarscool1.Roboto.listeners.slashcommands.utility.UserInfoSlashCommand;
import com.github.ungarscool1.Roboto.listeners.slashcommands.utility.VersionSlashCommand;
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
import org.javacord.api.interaction.*;
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
			config = gson.fromJson(new FileReader("config.json"), Configuration.class);
		} catch (Exception e) {
			System.err.println("The configuration file is missing.");
			System.exit(1);
		}
		System.out.println("Running on " + config.env + " mode");
		Sentry.init(options -> {
			  options.setDsn(config.sentry_io_dsn);
			  options.setTracesSampleRate(config.sentry_io_trace_sample_rate);
			  options.setDebug(config.sentry_io_debug);
			  options.setRelease("020921-02.10");
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

	private static void setupSlashCommand(DiscordApi api) {
		if (config.isSetup)
			return;
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
		SlashCommand.with("help", "Display this help message.").createGlobal(api).join();
		SlashCommand.with("report", "Report a problem on GitHub").createGlobal(api).join();
		SlashCommand.with("si", "Get server information").createGlobal(api).join();
		/*List<SlashCommandOption> uiOptions = new ArrayList<>();
		uiOptions.add(new SlashCommandOptionBuilder().setRequired(false).setName("user").setDescription("Mentionned user").setType(SlashCommandOptionType.MENTIONABLE).build());
		SlashCommand.with("ui", "Get user information", uiOptions).createGlobal(api).join();
		*/SlashCommand.with("version", "Get bot version").createGlobal(api).join();
		SlashCommand.with("vote", "Create a poll").createGlobal(api).join();
		config.isSetup = true;
		try {
			FileWriter writer = new FileWriter("config.json");
			writer.write(new Gson().toJson(config));
			writer.close();
		} catch (Exception e) {
			Sentry.captureException(e);
		}
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

		//  !--------------------------------------------------------------------------------------------! //
		/**
		 * Slash Commands: Utility
		 */
		api.addSlashCommandCreateListener(new com.github.ungarscool1.Roboto.listeners.slashcommands.utility.HelpCommand());
		api.addSlashCommandCreateListener(new ReportSlashCommand());
		api.addSlashCommandCreateListener(new ServerInfoSlashCommand());
		api.addSlashCommandCreateListener(new UserInfoSlashCommand());
		api.addSlashCommandCreateListener(new VersionSlashCommand());
		api.addSlashCommandCreateListener(new DiscoboomSlashCommand());
		//

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

		api.addReactionAddListener(new ReacListener());
		transaction.setStatus(SpanStatus.OK);
		transaction.finish();
	}



}
package com.github.ungarscool1.Roboto.listeners.commands;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.DiscordClient;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.entity.user.UserStatus;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import com.github.ungarscool1.Roboto.Main;
import com.github.ungarscool1.Roboto.ServerLanguage;


public class UtilsCommand implements MessageCreateListener{
	
	private DiscordApi api;
	
	public UtilsCommand(DiscordApi api) {
		this.api = api;
	}

	public void onMessageCreate(MessageCreateEvent event) {
		Message message = event.getMessage();
		
		// Ignore if the message is sent in PM
		if (!message.getServer().isPresent() || message.getAuthor().isBotUser()) {
			return;
		}
		
		ResourceBundle language = ResourceBundle.getBundle("lang.lang", Main.locByServ.get(message.getServer().get()));
		
		if (message.getContent().equalsIgnoreCase("!help") || message.getContent().equalsIgnoreCase("!aide")) {
			EmbedBuilder embedBuilder = new EmbedBuilder();
			embedBuilder.setTitle(language.getString("help"))
				.addField(language.getString("help.game.cmd"), language.getString("help.game.desc"))
				.addField("@help", language.getString("help.admin.desc"))
				.addField("!ui [args]", language.getString("help.ui.desc"))
				.addField("!ver", language.getString("help.ver.desc"))
				.addField("!vote", language.getString("help.vote.desc"))
				.addField("!report", language.getString("help.report.desc"))
				.setColor(Color.GREEN)
				.setFooter("Roboto v.3 by Ungarscool1");
			message.getChannel().sendMessage(embedBuilder);
		}

		if (message.getContent().equalsIgnoreCase("!report")) {
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
		}
		
		if (message.getContent().equalsIgnoreCase("!ver") || message.getContent().equalsIgnoreCase("!version")) {
			EmbedBuilder embedBuilder = new EmbedBuilder();
			int users = api.getServers().stream().mapToInt(Server::getMemberCount).sum();
			try {
				embedBuilder.setTitle(language.getString("version.name"))
					.addField("Version", "3.0.0 DEV")
					.addField(language.getString("version.lib.name"), language.getString("version.lib.desc"))
					.addField("Build", "180820-18.1")
					.addField("Bot owner", api.getOwner().get().getDiscriminatedName())
					.addField(language.getString("version.github"), "https://github.com/ungarscool1/Roboto-v2")
					.addField(language.getString("version.listen.user"), users + " " + language.getString("version.users"))
					.addField(language.getString("version.listen"), api.getServers().size() + " " + language.getString("version.servers"))
					.setColor(Color.GREEN)
					.setFooter("Roboto v.3 by Ungarscool1");
			} catch (Exception e) {
				embedBuilder.setTitle(language.getString("errors.title"))
						.setDescription(language.getString("errors.unkown_error"))
						.addField("Report bugs in English", "https://github.com/ungarscool1/Roboto-v2/issues/new?assignees=&labels=bug&template=bug_report.md&title=!ver%20Command%20fail")
						.setColor(Color.RED);
			}
			message.getChannel().sendMessage(embedBuilder);
		}
		
		if (message.getContent().contains("!ui") && message.getContent().indexOf("!ui") == 0) {
			String[] args = message.getContent().split(" ");
			
			if (args.length > 1) {
				for (int i = 1; i < args.length; i++) {
					String user = args[i];
					EmbedBuilder embedBuilder = new EmbedBuilder();
					boolean doExist = false;
					
					// Verify if the user enter id, Name or Name#DISC
					if (Pattern.matches("[0-9]+", user) && message.getServer().get().getMemberById(user).isPresent()) {
						// It's an id
						doExist = true;
					} else if (Pattern.matches("([a-zA-Z0-9]+.#.[0-9]+)", user) && message.getServer().get().getMemberByDiscriminatedNameIgnoreCase(user).isPresent()) {
						doExist = true;
						user = message.getServer().get().getMemberByDiscriminatedNameIgnoreCase(user).get().getIdAsString();
					} else if (user.contains("<@") && user.contains(">")) {
						if (user.contains("!")) {
							user = user.substring(3, user.indexOf(">"));
						} else {
							user = user.substring(2, user.indexOf(">"));
						}
						if (message.getServer().get().getMemberById(user).isPresent()) {
							doExist = true;
						}
					}
						if (doExist) {
							User u = message.getServer().get().getMemberById(user).get();
							// Get and convert join date to human readable value
							Date joinDate = Date.from(u.getJoinedAtTimestamp(message.getServer().get()).get());
							Date creationDate = Date.from(u.getCreationTimestamp());
							SimpleDateFormat formatter = new SimpleDateFormat(language.getString("ui.date.format"));
							
							// Get if user is connected on PC / Mobile / WEB
							String connectedOn;
							if (!u.getStatusOnClient(DiscordClient.DESKTOP).equals(UserStatus.OFFLINE)) {
								connectedOn = "PC";
							} else if (u.getStatusOnClient(DiscordClient.MOBILE).equals(UserStatus.ONLINE)) {
								connectedOn = language.getString("ui.connectedOn.phone");
							} else {
								connectedOn = language.getString("ui.connectedOn.not");
							}
							
							
							String comp = "";
							if (u.isBot())
								comp = " (🤖)";
							Color color = Color.GREEN;
							if (u.getRoleColor(message.getServer().get()).isPresent()) {
								color = u.getRoleColor(message.getServer().get()).get();
							}
							embedBuilder.setTitle(String.format(language.getString("ui.title"), u.getName()) + comp)
								.addField(language.getString("ui.join.date"), formatter.format(joinDate), true)
								.addField(language.getString("ui.register.date"), formatter.format(creationDate), true)
								.addField(language.getString("ui.status"), u.getStatus().getStatusString(), true)
								.addField(language.getString("ui.connectedOn"), connectedOn, true)
								.setThumbnail(u.getAvatar())
								.setColor(color);
							
						} else {
							embedBuilder.setTitle(language.getString("ui.notFound"))
								.addField(language.getString("ui.searchedUser"), user)
								.setDescription(String.format(language.getString("ui.notFound.desc"), user))
								.setColor(Color.ORANGE);
						}
					message.getChannel().sendMessage(embedBuilder);
				}
			} else {
				EmbedBuilder embedBuilder = new EmbedBuilder();
				User u = message.getAuthor().asUser().get();
				
				// Get and convert join date to human readable value
				Date joinDate = Date.from(u.getJoinedAtTimestamp(message.getServer().get()).get());
				Date creationDate = Date.from(u.getCreationTimestamp());
				SimpleDateFormat formatter = new SimpleDateFormat(language.getString("ui.date.format"));
				
				// Get if user is connected on PC / Mobile / WEB
				String connectedOn;
				if (!u.getStatusOnClient(DiscordClient.DESKTOP).equals(UserStatus.OFFLINE)) {
					connectedOn = "PC";
				} else if (u.getStatusOnClient(DiscordClient.MOBILE).equals(UserStatus.ONLINE)) {
					connectedOn = language.getString("ui.connectedOn.phone");
				} else {
					connectedOn = language.getString("ui.connectedOn.notSure");
				}
				
				String status;
				if (u.getStatus().equals(UserStatus.OFFLINE)) {
					status = "invisible";
				} else {
					status = u.getStatus().getStatusString();
				}
				
				Color color = Color.GREEN;
				if (u.getRoleColor(message.getServer().get()).isPresent()) {
					color = u.getRoleColor(message.getServer().get()).get();
				}
				
				embedBuilder.setTitle(String.format(language.getString("ui.title"), u.getName()))
					.addField(language.getString("ui.join.date"), formatter.format(joinDate), true)
					.addField(language.getString("ui.register.date"), formatter.format(creationDate), true)
					.addField(language.getString("ui.status"), status, true)
					.addField(language.getString("ui.connectedOn"), connectedOn, true)
					.setThumbnail(u.getAvatar())
					.setColor(color);
				message.getChannel().sendMessage(embedBuilder);
			}
		}
		
		if (message.getContent().equals("!si")) {
			Server server = message.getServer().get();
			EmbedBuilder embedBuilder = new EmbedBuilder();
			
			// Get and convert join date to human readable value
			Date creationDate = Date.from(server.getCreationTimestamp());
			SimpleDateFormat formatter = new SimpleDateFormat(language.getString("ui.date.format"));
			
			// Get member and bot count
			int[] members = {0, 0};
			server.getMembers().forEach(member -> {
				if (member.isBot()) 
					members[0]++;
				else
					members[1]++;
			});
			
			embedBuilder.setTitle(String.format(language.getString("si.title"), server.getName()))
				.addField(language.getString("si.id"), server.getIdAsString(), true)
				.addField(language.getString("si.creation.date"), formatter.format(creationDate), true)
				.addField(language.getString("si.owner"), server.getOwner().getDiscriminatedName())
				.addField(language.getString("si.memberscount"), members[1] + " " + language.getString("si.members"))
				.addField(language.getString("si.botscount"), members[0] + " bots")
				.setColor(Color.GREEN);
			message.getChannel().sendMessage(embedBuilder);
		}
		
		
		// I still love u
		if (message.getContent().contains("<3") || message.getContent().contains("❤")) {
			message.addReaction("❤");
		}
		
		if (message.getContent().contains("<@!373199180161613824>") || message.getContent().contains("<@373199180161613824>")) {
			if (message.getContent().contains("Je t'aime")) {
				message.getChannel().sendMessage("Moi aussi je t'aime bb ❤");
			}
		}
		
	}

}

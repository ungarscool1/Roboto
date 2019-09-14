package com.github.ungarscool1.Roboto.listeners.commands;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import org.javacord.api.entity.DiscordClient;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.entity.user.UserStatus;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import com.github.ungarscool1.Roboto.Main;


public class UtilsCommand implements MessageCreateListener{

	public void onMessageCreate(MessageCreateEvent event) {
		Message message = event.getMessage();
		
		// Ignore if the message is sent in PM
		if (!message.getServer().isPresent()) {
			return;
		}
		
		
		if (message.getContent().equalsIgnoreCase("!help") || message.getContent().equalsIgnoreCase("!aide")) {
			EmbedBuilder embedBuilder = new EmbedBuilder();
			embedBuilder.setTitle("Aides")
				.addField("!game <nom du jeu>", "Jouer à un jeu seul ou avec des amis.\n(bêta)")
				.addField("!ui [args]", "Récupérer les informations d'un membre du serveur.\n Les arguments sont facultatifs")
				.addField("!ver", "Obtenir la version du bot")
				.addField("!vote", "Faire un vote")
				.setColor(Color.GREEN)
				.setFooter("Roboto v.3 by Ungarscool1");
			message.getChannel().sendMessage(embedBuilder);
		}
		
		if (message.getContent().equalsIgnoreCase("!ver") || message.getContent().equalsIgnoreCase("!version")) {
			EmbedBuilder embedBuilder = new EmbedBuilder();
			try {
				embedBuilder.setTitle("Version et information du bot")
					.addField("Version", "3.0.0 DEV")
					.addField("Version librairie et API", "Javacord 3.0.4 / Discord API v6")
					.addField("Build", "140919-15.4")
					.addField("Bot owner", Main.API.getOwner().get().getDiscriminatedName())
					.addField("GitHub du bot", "https://github.com/ungarscool1/Roboto-v2")
					.addField("Roboto est actif sur ", Main.API.getServers().size() + " serveurs")
					.setColor(Color.GREEN)
					.setFooter("Roboto v.3 by Ungarscool1");
			} catch (Exception e) {
				// You can't get any error \o/
			}
			message.getChannel().sendMessage(embedBuilder);
		}
		
		if(message.getContent().contains("!ui") && message.getContent().indexOf("!ui") == 0) {
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
							SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
							
							// Get if user is connected on PC / Mobile / WEB
							String connectedOn;
							if (!u.getStatusOnClient(DiscordClient.DESKTOP).equals(UserStatus.OFFLINE)) {
								connectedOn = "PC";
							} else if (u.getStatusOnClient(DiscordClient.MOBILE).equals(UserStatus.ONLINE)) {
								connectedOn = "Téléphone";
							} else {
								connectedOn = "N'est pas connecté ou AFK";
							}
							
							String comp = "";
							if (u.isBot())
								comp = " (🤖)";
							embedBuilder.setTitle("Information de " + u.getName() + comp)
								.addField("A rejoint le serveur", formatter.format(joinDate), true)
								.addField("S'est inscrit sur discord", formatter.format(creationDate), true)
								.addField("Statut", u.getStatus().getStatusString(), true)
								.addField("Connecté depuis un", connectedOn, true)
								.setThumbnail(u.getAvatar())
								.setColor(Color.GREEN);
						} else {
							embedBuilder.setTitle("Utilisateur introuvable")
								.addField("Personne recherché", user)
								.setDescription("L'utilisateur " + user + " n'a pas été trouvé ou une erreur s'est produite")
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
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				
				// Get if user is connected on PC / Mobile / WEB
				String connectedOn;
				if (!u.getStatusOnClient(DiscordClient.DESKTOP).equals(UserStatus.OFFLINE)) {
					connectedOn = "PC";
				} else if (u.getStatusOnClient(DiscordClient.MOBILE).equals(UserStatus.ONLINE)) {
					connectedOn = "Téléphone";
				} else {
					connectedOn = "ça dépend...\nLe statut du joueur ne permet pas de savoir s'il est connecté depuis une plateforme spécifique.";
				}
				
				String status;
				if (u.getStatus().equals(UserStatus.OFFLINE)) {
					status = "invisible";
				} else {
					status = u.getStatus().getStatusString();
				}
				
				embedBuilder.setTitle("Information de " + u.getName())
					.addField("A rejoint le serveur", formatter.format(joinDate), true)
					.addField("S'est inscrit sur discord", formatter.format(creationDate), true)
					.addField("Statut", status, true)
					.addField("Connecté depuis un", connectedOn, true)
					.setThumbnail(u.getAvatar())
					.setColor(Color.GREEN);
				message.getChannel().sendMessage(embedBuilder);
			}
		}
		
		if (message.getContent().equals("!si")) {
			Server server = message.getServer().get();
			EmbedBuilder embedBuilder = new EmbedBuilder();
			
			// Get and convert join date to human readable value
			Date creationDate = Date.from(server.getCreationTimestamp());
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			
			embedBuilder.setTitle("Information sur le serveur " + server.getName())
				.addField("Identifiant unique", server.getIdAsString(), true)
				.addField("Date de création", formatter.format(creationDate), true)
				.addField("Propriétaire", server.getOwner().getDiscriminatedName())
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

package com.github.ungarscool1.Roboto.listeners.commands;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.SSLEngineResult.Status;

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
				.addField("!ui [args]", "Récupérer les informations d'un membre du serveur.\n Les arguments sont facultatifs")
				.addField("!game <nom du jeu>", "Jouer à un jeu seul ou avec des amis.\n(bêta)")
				.addField("!vote", "Faire un vote")
				.setColor(Color.GREEN)
				.setFooter("Roboto v.3 by Ungarscool1");
			message.getChannel().sendMessage(embedBuilder);
		}
		
		
		if(message.getContent().contains("!ui")) {
			String[] args = message.getContent().split(" ");
			
			if (args.length > 1) {
				for (int i = 1; i < args.length; i++) {
					String user = args[i];
					EmbedBuilder embedBuilder = new EmbedBuilder();
					if (user.contains("<@") && user.contains(">")) {
						if (user.contains("!")) {
							user = user.substring(3, user.indexOf(">"));
						} else {
							user = user.substring(2, user.indexOf(">"));
						}
						
						if (message.getServer().get().getMemberById(user).isPresent()) {
							User u = message.getServer().get().getMemberById(user).get();
							// Get and convert join date to human readable value
							Date joinDate = Date.from(u.getJoinedAtTimestamp(message.getServer().get()).get());
							SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
							
							// Get if user is connected on PC / Mobile / WEB
							String connectedOn;
							if (!u.getStatusOnClient(DiscordClient.DESKTOP).equals(UserStatus.IDLE)) {
								connectedOn = "PC";
							} else if (!u.getStatusOnClient(DiscordClient.MOBILE).equals(UserStatus.IDLE)) {
								connectedOn = "Téléphone";
							} else if (!u.getStatusOnClient(DiscordClient.WEB).equals(UserStatus.IDLE)) {
								connectedOn = "Depuis un client web";
							} else {
								connectedOn = "N'est pas connecté ou AFK";
							}
							
							embedBuilder.setTitle("Information de " + u.getName())
								.addField("A rejoint le serveur", formatter.format(joinDate))
								.addField("Statut", u.getStatus().getStatusString(), true)
								.addField("Connecté depuis", connectedOn, true)
								.setThumbnail(u.getAvatar())
								.setColor(Color.GREEN);
						} else {
							embedBuilder.setTitle("Utilisateur introuvable")
								.addField("Personne recherché", user)
								.setDescription("L'utilisateur avec l'id " + user + " n'a pas été trouvé")
								.setColor(Color.ORANGE);
						}
					} else {
						embedBuilder.setTitle("Utilisation incorrect")
							.addField("Utilisation", "!ui <mension tag>\n!ui")
							.setColor(Color.RED);
					}
					message.getChannel().sendMessage(embedBuilder);
				}
			} else {
				EmbedBuilder embedBuilder = new EmbedBuilder();
				User u = message.getAuthor().asUser().get();
				
				// Get and convert join date to human readable value
				Date joinDate = Date.from(u.getJoinedAtTimestamp(message.getServer().get()).get());
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				
				// Get if user is connected on PC / Mobile / WEB
				String connectedOn;
				if (!u.getStatusOnClient(DiscordClient.DESKTOP).equals(UserStatus.IDLE)) {
					connectedOn = "PC";
				} else if (!u.getStatusOnClient(DiscordClient.MOBILE).equals(UserStatus.IDLE)) {
					connectedOn = "Téléphone";
				} else if (!u.getStatusOnClient(DiscordClient.WEB).equals(UserStatus.IDLE)) {
					connectedOn = "Depuis un client web";
				} else {
					connectedOn = "N'est pas connecté ou AFK";
				}
				
				embedBuilder.setTitle("Information de " + u.getName())
					.addField("A rejoint le serveur", formatter.format(joinDate))
					.addField("Statut", u.getStatus().getStatusString(), true)
					.addField("Connecté depuis", connectedOn, true)
					.setThumbnail(u.getAvatar())
					.setColor(Color.GREEN);
				message.getChannel().sendMessage(embedBuilder);
			}
		}
		
		if (message.getContent().equals("!si")) {
			Server server = message.getServer().get();
			EmbedBuilder embedBuilder = new EmbedBuilder();
			
			// Get and convert join date to human readable value
			Date joinDate = Date.from(server.getCreationTimestamp());
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			
			embedBuilder.setTitle("Information sur le serveur " + server.getName())
				.addField("Identifiant unique", server.getIdAsString())
				.addField(name, );
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

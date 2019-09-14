package com.github.ungarscool1.Roboto.entity.game;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static java.util.stream.Collectors.*;
import static java.util.Map.Entry.*;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import com.github.ungarscool1.Roboto.listeners.ReacListener;
import com.github.ungarscool1.Roboto.listeners.commands.GameCommand;

public class PFCbr {
	
	private DiscordApi api;
	private Message joinMessage;
	private ArrayList<User> players = new ArrayList<>();
	private HashMap<User, Integer> score = new HashMap<>();
	private HashMap<User, Boolean> played = new HashMap<>();
	private HashMap<User, PFC> PFCs = new HashMap<>();
	private int slots;
	private boolean inGame;
	private Thread th;
	
	
	public PFCbr(User Owner, int slots, DiscordApi api) {
		this.api = api;
		join(Owner);
		this.inGame = false;
		this.slots = slots;
	}
	
	public EmbedBuilder joinMessage() {
		String playersToString = "";
		for (int i = 0; i < players.size(); i++) {
			playersToString += "- " + players.get(i).getMentionTag() + "\n";
		}
		Color color = Color.RED;
		String desc = players.get(0).getName() + " vous a invité à jouer à Pierre Feuille Ciseaux: Battle Royal.";
		if (inGame) {
			color = Color.GREEN;
			desc = "La partie en mode Battle Royal a commencé !";
		}
		return new EmbedBuilder().setTitle("Pierre Feuille Ciseaux").setDescription(desc).addField("Slots", players.size() + " / " + slots, true).addField("Joueurs dans la partie", playersToString).setColor(color);
	}
	
	public void setJoinMessage(Message message) {
		this.joinMessage = message;
	}
	
	public Message getJoinMessage() {
		return this.joinMessage;
	}
	
	public String join (User player) {
		if (!players.contains(player)) {
			if (players.size() <= slots) {
				players.add(player);
				score.put(player, 0);
				if (players.size() == slots) {
					inGame = true;
					return "Starting";
				}
				return "Joined";
			} else {
				return "This party has already started";
			}
		} else {
			return "Already in party";
		}
	}
	
	public String leave(User player) {
		players.remove(player);
		if (players.size()==0) {
			return "party disbended";
		}
		return "disconnected";
	}
	
	private boolean playersDoPlayed() {
		for (int i = 0; i < players.size(); i++) {
			if (!played.get(players.get(i))) return false;
		}
		return true;
	}
	
	
	private void finishGame() {
		if (inGame == false) return;
		inGame = false;
		score = score.entrySet().stream().sorted(Collections.reverseOrder(HashMap.Entry.comparingByValue())).collect(
				toMap(HashMap.Entry::getKey, HashMap.Entry::getValue, (e1, e2) -> e2,
		                LinkedHashMap::new)
				);
		String leaderboard = "";
		
		for(HashMap.Entry<User, Integer> entry : score.entrySet()) {
		    User user = entry.getKey();
		    Integer sc = entry.getValue();
		    leaderboard += "- " + user.getDisplayName(joinMessage.getServer().get()) + " (" + sc + " points)\n";
		}
		
		joinMessage.getChannel().sendMessage(new EmbedBuilder()
				 .setTitle("La partie Pierre Feuille Ciseaux BR terminée")
				 .setColor(Color.green)
				 .setDescription("Le gagnant est " + players.get(0).getDisplayName(joinMessage.getServer().get()))
				 .setThumbnail(players.get(0).getAvatar())
				 .addField("Classement", leaderboard)
				 ).join();
		
		
		GameCommand.PFCs.remove(joinMessage);
		ReacListener.updateGames();
		th.stop();
	}
	
	/**
	 * This func will create instance of PFC
	 * That make battle between 2 players
	 */
	private void createGame() {
		System.out.println("Creating games...");
		for (int i = 0; i < players.size(); i+=2) {
			System.out.println("Intencing...");
			played.put(players.get(i), false);
			played.put(players.get((i + 1)), false);
			PFC pfc = new PFC(players.get(i), 1, this, api);
			pfc.join(players.get((i+1)));
			pfc.setJoinMessage(joinMessage);
			pfc.gameHandler();
			PFCs.put(players.get(i), pfc);
		}
	}
	
	/**
	 * This func is exec from PFC
	 * @param winner
	 * @param looser
	 */
	public void win(User winner, User looser) {
		players.remove(looser);
		score.replace(winner, (score.get(winner) + 1));
		played.replace(winner, true);
		played.remove(looser);
		winner.sendMessage("Vous avez gagné le duel contre " + looser.getDisplayName(joinMessage.getServer().get()));
		looser.sendMessage("Vous avez perdu le duel contre " + winner.getDisplayName(joinMessage.getServer().get()));
		if (!playersDoPlayed()) {
			EmbedBuilder embed = new EmbedBuilder();
			embed.setTitle("Pierre Feuille Ciseaux");
			embed.setColor(new Color(107, 135, 232));
			embed.setDescription("En attente des joueurs... Tous les joueurs doivent avoir terminé leurs duels pour lancer d'autre duel");
			embed.addField("Joueurs restant", players.size() + " / " + slots);
			embed.setFooter("Partie en mode Battle Royal");
			winner.sendMessage(embed);
		} else {
			checkPlayers();
		}
		
		
	}
	
	private void checkPlayers() {
		if (players.size() == 1) {
			finishGame();
		} else {
			createGame();
		}
	}

	public void gameHandler() {
		th = new Thread(new Runnable() {
		     public void run() {
		    	 createGame();
		     }
		});
		th.start();
	}
}


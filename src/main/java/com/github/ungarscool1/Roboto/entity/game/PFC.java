package com.github.ungarscool1.Roboto.entity.game;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.Event;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.listener.message.reaction.ReactionAddListener;
import org.javacord.api.util.event.ListenerManager;

import com.github.ungarscool1.Roboto.Main;
import com.github.ungarscool1.Roboto.listeners.ReacListener;
import com.github.ungarscool1.Roboto.listeners.commands.GameCommand;

public class PFC {
	
	private Message joinMessage;
	private Message latest;
	private ArrayList<User> players = new ArrayList<User>();
	private HashMap<User, Integer> score = new HashMap<User, Integer>();
	private HashMap<User, Integer> played = new HashMap<User, Integer>();
	private HashMap<TextChannel, Message> messagesByChannel = new HashMap<TextChannel, Message>();
	private int slots;
	private boolean inGame;
	private int manche;
	private int maxManche;
	private ListenerManager<ReactionAddListener> listener;
	private Thread th;
	// if in BR mode
	private PFCbr br;
	
	
	public PFC(User Owner, int maxManche) {
		join(Owner);
		this.inGame = false;
		this.slots = 2;
		if (maxManche == 0) this.maxManche = 3;
		else this.maxManche = maxManche;
	}
	
	public PFC(User Owner, int maxManche, PFCbr br) {
		join(Owner);
		this.inGame = false;
		this.slots = 2;
		this.br = br;
		if (maxManche == 0) this.maxManche = 3;
		else this.maxManche = maxManche;
	}
	
	public EmbedBuilder joinMessage() {
		String playersToString = "";
		for (int i = 0; i < players.size(); i++) {
			playersToString += "- " + players.get(i).getMentionTag() + "\n";
		}
		Color color = Color.RED;
		String desc = players.get(0).getName() + " vous a invité à jouer à Pierre Feuille Ciseaux.";
		if (inGame) {
			color = Color.GREEN;
			desc = "La partie de " + players.get(0).getName() + " a commencée !";
		}
		return new EmbedBuilder().setTitle("Pierre Feuille Ciseaux").setDescription(desc).addField("Slots", players.size() + " / " + slots, true).addField("Manches", "Il y a " + maxManche, true).addField("Joueurs dans la partie", playersToString).setColor(color);
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
					manche = 1;
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
	
	private String getEmojiFromInt(int code) {
		if (code == 1) {
			return "👊";
		} else if (code == 2) {
			return "🍂";
		} else if (code == 3) {
			return "✂";
		} else {
			return "Non reconnu, a-t-il essayé de tricher ?";
		}
	}
	
	private void mpPlayers() {
		EmbedBuilder embed = new EmbedBuilder().setTitle("1... 2... 3... Pierre... Feuille... Ciseaux").setDescription("👊, 🍂 ou ✂").setColor(Color.GREEN).addField("Manches", manche + " / " + maxManche).addField("Scores", "Hôte: " + score.get(players.get(0)) + " Invité: " + score.get(players.get(1))).setFooter("Partie de " + players.get(0).getName());
		if (this.br != null) {
			embed.setTitle("Duel " + players.get(0).getDisplayName(joinMessage.getServer().get()) + " vs " + players.get(1).getDisplayName(joinMessage.getServer().get()));
			embed.setFooter("Partie en mode Battle Royal");
		}
		for(int i = 0; i < players.size(); i++) {
        	if (played.containsKey(players.get(i))) {
        		played.remove(players.get(i));
            }
      	  Message message = players.get(i).sendMessage(embed).join();
      	  messagesByChannel.put(message.getChannel(), message);
      	  message.addReactions("👊", "🍂", "✂", "❌");
        }
	}
	
	private void finishGame() {
		if (inGame == false) return;
		inGame = false;
		String winner = "";
		if (score.get(players.get(0)) > score.get(players.get(1))) {
			winner = players.get(0).getMentionTag() + " a gagner la partie contre " + players.get(1).getMentionTag();
			if (this.br != null) {
				this.br.win(players.get(0), players.get(1));
			}
		} else if (score.get(players.get(0)) < score.get(players.get(1))) {
			winner = players.get(1).getMentionTag() + " a gagner la partie contre " + players.get(0).getMentionTag();
			if (this.br != null) {
				this.br.win(players.get(1), players.get(0));
			}
		}
		
		
		
		joinMessage.getChannel().sendMessage(new EmbedBuilder().
				 setTitle(players.get(0).getDisplayName(joinMessage.getServer().get()) + " vs " + players.get(1).getDisplayName(joinMessage.getServer().get())).setColor(Color.green).
				 setDescription(winner)
				 ).join();
		
		
		GameCommand.PFCs.remove(joinMessage);
		ReacListener.updatePFCs();
		
		listener.remove();
		th.stop();
	}

	public void gameHandler() {
		th = new Thread(new Runnable() {
		     public void run() {
		    	 listener = Main.API.addReactionAddListener(event -> {
		    		 if (event.getUser().isYourself()) return;
		    		 Emoji emoji = event.getEmoji();
		    		 boolean isDM = event.getMessage().get().isPrivateMessage();
		    		 Message message = event.getMessage().get();
		    		 if (isDM) {
		    			 if (messagesByChannel.containsValue(message)) {
		    				 if (emoji.asUnicodeEmoji().get().equals("👊")) {
		    					 played.put(event.getUser(), 1);
		    				 } else if (emoji.asUnicodeEmoji().get().equals("🍂")) {
		    					 played.put(event.getUser(), 2);
		    				 } else if (emoji.asUnicodeEmoji().get().equals("✂")) {
		    					 played.put(event.getUser(), 3);
		    				 } else if (emoji.asUnicodeEmoji().get().equals("❌")) {
		    					 for (int i = 0; i < players.size(); i++) {
		    						 if (!players.get(i).equals(event.getUser())) {
		    							 players.get(i).sendMessage(event.getUser().getDisplayName(joinMessage.getServer().get()) + " abandonne la partie");
		    							 if (br != null) {
		    								 br.win(players.get(i), event.getUser());
		    							 }
		    						 }
		    					 }
		    					 finishGame();
		    				 } else {
		    					 message.getChannel().sendMessage(emoji.asUnicodeEmoji().get() + " n'existe pas dans pfc :confused:");
		    				 }
		    				 
		    				 if (played.size() == slots) {
		    					 String winner;
		    					 if ((played.get(players.get(0)) == 1 && played.get(players.get(1)) == 1) || (played.get(players.get(0)) == 2 && played.get(players.get(1)) == 2) || (played.get(players.get(0)) == 3 && played.get(players.get(1)) == 3)) {
		    						 winner = "Il n'y a pas de gagnant";
		    					 } else if ((played.get(players.get(0)) == 1 && played.get(players.get(1)) == 2) || (played.get(players.get(0)) == 2 && played.get(players.get(1)) == 3) || (played.get(players.get(0)) == 3 && played.get(players.get(1)) == 1)) {
		    						 winner = "Le gagnant de cette manche est " + players.get(1).getDisplayName(joinMessage.getServer().get());
		    						 if (score.containsKey(players.get(1))) {
										int sc = score.get(players.get(1)) + 1;
										score.replace(players.get(1), sc);
									} else {
										score.put(players.get(1), 1);
									}
		    					 } else if ((played.get(players.get(0)) == 2 && played.get(players.get(1)) == 1) || (played.get(players.get(0)) == 3 && played.get(players.get(1)) == 2) || (played.get(players.get(0)) == 1 && played.get(players.get(1)) == 3)) {
		    						 winner = "Le gagnant de cette manche est " + players.get(0).getDisplayName(joinMessage.getServer().get());
		    						 if (score.containsKey(players.get(0))) {
											int sc = score.get(players.get(0)) + 1;
											score.replace(players.get(0), sc);
										} else {
											score.put(players.get(0), 1);
										}
		    					 } else {
		    						 winner = "Non géré :/";
		    					 }
		    					 latest = joinMessage.getChannel().sendMessage(new EmbedBuilder().
	    								 setTitle(players.get(0).getDisplayName(joinMessage.getServer().get()) + " vs " + players.get(1).getDisplayName(joinMessage.getServer().get())).setColor(Color.green).
	    								 addInlineField("Action de " + players.get(0).getDisplayName(joinMessage.getServer().get()), getEmojiFromInt(played.get(players.get(0))))
	    								 .addInlineField("Action de " + players.get(1).getDisplayName(joinMessage.getServer().get()), getEmojiFromInt(played.get(players.get(1))))
	    								 .addField(winner, score.get(players.get(0)) + " - " + score.get(players.get(1)))
	    								 ).join();
		    					 messagesByChannel.remove(message.getChannel());
		    					 if ((manche < maxManche) || score.get(players.get(0)) == score.get(players.get(1))) {
		    						 manche++;
		    						 mpPlayers();
								} else {
									finishGame();
								}
		    				 }
		    				 
		    				 
		    			 }
		    		 }
		    	 });
		    	 mpPlayers();
		     }
		});
		th.start();
	}
}


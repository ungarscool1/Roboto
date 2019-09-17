package com.github.ungarscool1.Roboto.entity.game;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import org.javacord.api.DiscordApi;
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
	
	private DiscordApi api;
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
	private ResourceBundle language;
	// if in BR mode
	private PFCbr br;
	
	
	public PFC(User Owner, int maxManche, DiscordApi api, Locale locals) {
		this.api = api;
		this.language = ResourceBundle.getBundle("lang.lang", locals);
		join(Owner);
		this.inGame = false;
		this.slots = 2;
		if (maxManche == 0) this.maxManche = 3;
		else this.maxManche = maxManche;
	}
	
	public PFC(User Owner, int maxManche, PFCbr br, DiscordApi api, Locale locals) {
		this(Owner, maxManche, api, locals);
		this.br = br;
	}
	
	public EmbedBuilder joinMessage() {
		String playersToString = "";
		for (int i = 0; i < players.size(); i++) {
			playersToString += "- " + players.get(i).getMentionTag() + "\n";
		}
		Color color = Color.RED;
		String desc = String.format(language.getString("game.pfc.invitation.inviteYou"), players.get(0).getName());
		if (inGame) {
			color = Color.GREEN;
			desc = String.format(language.getString("game.pfc.invitation.inGame"), players.get(0).getName());
		}
		return new EmbedBuilder().setTitle(language.getString("game.pfc.invitation.name")).setDescription(desc).addField("Slots", players.size() + " / " + slots, true).addField(language.getString("game.pfc.invitation.round"), String.format(language.getString("game.pfc.invitation.round.desc"), maxManche), true).addField(language.getString("game.pfc.invitation.players"), playersToString).setColor(color);
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
		EmbedBuilder embed = new EmbedBuilder().setTitle(language.getString("game.pfc.inGame.name")).setDescription(language.getString("game.pfc.inGame.desc")).setColor(Color.GREEN).addField(language.getString("game.pfc.inGame.round"), manche + " / " + maxManche).addField("Scores", language.getString("game.pfc.inGame.score.host") + score.get(players.get(0)) + language.getString("game.pfc.inGame.score.guest") + score.get(players.get(1))).setFooter(String.format(language.getString("game.pfc.inGame.party"), players.get(0).getName()));
		if (this.br != null) {
			embed.setTitle("Duel " + players.get(0).getDisplayName(joinMessage.getServer().get()) + " vs " + players.get(1).getDisplayName(joinMessage.getServer().get()));
			embed.setFooter(language.getString("game.pfc.br.footer"));
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
			winner = String.format(language.getString("game.pfc.br.won"), players.get(0).getMentionTag(), players.get(1).getMentionTag());
			if (this.br != null) {
				this.br.win(players.get(0), players.get(1));
			}
		} else if (score.get(players.get(0)) < score.get(players.get(1))) {
			winner = String.format(language.getString("game.pfc.br.won"), players.get(1).getMentionTag(), players.get(0).getMentionTag());
			if (this.br != null) {
				this.br.win(players.get(1), players.get(0));
			}
		}
		
		
		
		joinMessage.getChannel().sendMessage(new EmbedBuilder().
				 setTitle(players.get(0).getDisplayName(joinMessage.getServer().get()) + " vs " + players.get(1).getDisplayName(joinMessage.getServer().get())).setColor(Color.green).
				 setDescription(winner)
				 ).join();
		
		
		GameCommand.PFCs.remove(joinMessage);
		ReacListener.updateGames();
		
		listener.remove();
		th.stop();
	}

	public void gameHandler() {
		th = new Thread(new Runnable() {
		     public void run() {
		    	 listener = api.addReactionAddListener(event -> {
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
		    							 players.get(i).sendMessage(String.format(language.getString("game.pfc.inGame.abandonment"), event.getUser().getDisplayName(joinMessage.getServer().get())));
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
		    						 winner = String.format(language.getString("game.pfc.inGame.winner"), players.get(1).getDisplayName(joinMessage.getServer().get()));
		    						 if (score.containsKey(players.get(1))) {
										int sc = score.get(players.get(1)) + 1;
										score.replace(players.get(1), sc);
									} else {
										score.put(players.get(1), 1);
									}
		    					 } else if ((played.get(players.get(0)) == 2 && played.get(players.get(1)) == 1) || (played.get(players.get(0)) == 3 && played.get(players.get(1)) == 2) || (played.get(players.get(0)) == 1 && played.get(players.get(1)) == 3)) {
		    						 winner = String.format(language.getString("game.pfc.inGame.winner"), players.get(0).getDisplayName(joinMessage.getServer().get()));
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
	    								 addInlineField(String.format(language.getString("game.pfc.inGame.play"), players.get(0).getDisplayName(joinMessage.getServer().get())), getEmojiFromInt(played.get(players.get(0))))
	    								 .addInlineField(String.format(language.getString("game.pfc.inGame.play"), players.get(1).getDisplayName(joinMessage.getServer().get())), getEmojiFromInt(played.get(players.get(1))))
	    								 .addField(winner, score.get(players.get(0)) + " - " + score.get(players.get(1)))
	    								 ).join();
		    					 latest.delete("Anti spam");
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


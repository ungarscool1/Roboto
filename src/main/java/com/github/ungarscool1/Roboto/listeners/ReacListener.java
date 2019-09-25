package com.github.ungarscool1.Roboto.listeners;

import java.util.HashMap;
import java.util.ResourceBundle;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.listener.message.reaction.ReactionAddListener;

import com.github.ungarscool1.Roboto.Main;
import com.github.ungarscool1.Roboto.entity.game.PFC;
import com.github.ungarscool1.Roboto.entity.game.PFCbr;
import com.github.ungarscool1.Roboto.entity.game.Puissance4;
import com.github.ungarscool1.Roboto.listeners.commands.GameCommand;

public class ReacListener implements ReactionAddListener{

	private static HashMap<Message, PFC> PFCs = GameCommand.PFCs;
	private static HashMap<Message, PFCbr> PFCbrs = GameCommand.PFCbrs;
	private static HashMap<Message, Puissance4> P4 = new HashMap<Message, Puissance4>();
	
	public void onReactionAdd(ReactionAddEvent event) {
		
		if (event.getUser().isYourself()) return;
		Message message = null;
		if (event.getMessage().isPresent() && event.getMessageAuthor​().get().equals(event.getUser().isYourself())) {
			message = event.getMessage().get();
		} else {
			event.getChannel().sendMessage(event.getUser().getMentionTag() + " Sorry this message is too old");
			return;
		}
		ResourceBundle language = ResourceBundle.getBundle("lang.lang", Main.locByServ.get(message.getServer().get()));
		if (PFCs.containsKey(message)) {
			if (event.getEmoji().asUnicodeEmoji().get().equals("✅"))  {
				String res = PFCs.get(message).join(event.getUser());
				if (res.equals("Joined") || res.equals("Starting")) {
					message.getChannel().sendMessage(String.format(language.getString("game.join"), event.getUser().getMentionTag()));
					PFC pfc = PFCs.get(message);
					PFCs.remove(message);
					TextChannel channel = message.getChannel();
					message.delete();
					message = channel.sendMessage(pfc.joinMessage()).join();
					pfc.setJoinMessage(message);
					PFCs.put(message, pfc);
					GameCommand.updateGames(PFCs, PFCbrs, P4);
					if (res.equals("Joined")) {
						message.addReactions("✅","❌");
					} else {
						pfc.gameHandler();
					}
				} else if (res.equals("Already in party")) {
					message.getChannel().sendMessage(String.format(language.getString("game.alreadyInParty"), event.getUser().getMentionTag()));
				} else {
					message.getChannel().sendMessage(language.getString("game.inGame"));
				}
			} else if (event.getEmoji().asUnicodeEmoji().get().equals("❌")) {
				String res = PFCs.get(message).leave(event.getUser());
				if (res.equals("party disbended")) {
					PFCs.remove(message);
					message.delete();
					GameCommand.updateGames(PFCs, PFCbrs, P4);
				}
			}
			
		}
		
		if (PFCbrs.containsKey(message)) {
			if (event.getEmoji().asUnicodeEmoji().get().equals("✅")) {
				String res = PFCbrs.get(message).join(event.getUser());
				if (res.equals("Joined") || res.equals("Starting")) {
					message.getChannel().sendMessage(String.format(language.getString("game.join"), event.getUser().getMentionTag()));
					PFCbr pfc = PFCbrs.get(message);
					PFCs.remove(message);
					TextChannel channel = message.getChannel();
					message.delete();
					message = channel.sendMessage(pfc.joinMessage()).join();
					pfc.setJoinMessage(message);
					PFCbrs.put(message, pfc);
					GameCommand.updateGames(PFCs, PFCbrs, P4);
					if (res.equals("Joined")) {
						message.addReactions("✅", "❌");
					} else {
						pfc.gameHandler();
					}
				} else if (res.equals("Already in party")) {
					message.getChannel().sendMessage(String.format(language.getString("game.alreadyInParty"), event.getUser().getMentionTag()));
				} else {
					message.getChannel().sendMessage(language.getString("game.inGame"));
				}
			} else if (event.getEmoji().asUnicodeEmoji().get().equals("❌")) {
				String res = PFCbrs.get(message).leave(event.getUser());
				if (res.equals("party disbended")) {
					PFCbrs.remove(message);
					message.delete();
					GameCommand.updateGames(PFCs, PFCbrs, P4);
				} else {
					TextChannel channel = message.getChannel();
					PFCbr pfc = PFCbrs.get(message);
					PFCbrs.remove(message);
					message.delete();
					message = channel.sendMessage(pfc.joinMessage()).join();
					message.addReactions("✅", "❌");
					PFCbrs.put(message, pfc);
				}
				
			}
			
		}
		
		
		if (P4.containsKey(message)) {
			if (event.getEmoji().asUnicodeEmoji().get().equals("✅")) {
				String res = P4.get(message).join(event.getUser());
				if (res.equals("Joined") || res.equals("Starting")) {
					message.getChannel().sendMessage(String.format(language.getString("game.join"), event.getUser().getMentionTag()));
					Puissance4 p4 = P4.get(message);
					P4.remove(message);
					TextChannel channel = message.getChannel();
					message.delete();
					message = channel.sendMessage(p4.joinMessage()).join();
					p4.setJoinMessage(message);
					P4.put(message, p4);
					GameCommand.updateGames(PFCs, PFCbrs, P4);
					if (res.equals("Joined")) {
						message.addReactions("✅", "❌");
					} else {
						p4.gameHandler();
					}
				} else if (res.equals("Already in party")) {
					message.getChannel().sendMessage(String.format(language.getString("game.alreadyInParty"), event.getUser().getMentionTag()));
				} else {
					message.getChannel().sendMessage(language.getString("game.inGame"));
				}
			} else if (event.getEmoji().asUnicodeEmoji().get().equals("❌")) {
				String res = P4.get(message).leave(event.getUser());
				if (res.equals("party disbended")) {
					P4.remove(message);
					message.delete();
					GameCommand.updateGames(PFCs, PFCbrs, P4);
				} else {
					TextChannel channel = message.getChannel();
					Puissance4 p4 = P4.get(message);
					P4.remove(message);
					message.delete();
					message = channel.sendMessage(p4.joinMessage()).join();
					message.addReactions("✅", "❌");
					P4.put(message, p4);
				}
				
			}
			
		}
	}
	
	public static void updateGames() {
		System.out.println("[INFO] Liste des jeux mis à jour");
		PFCs = GameCommand.PFCs;
		PFCbrs = GameCommand.PFCbrs;
		P4 = GameCommand.P4;
	}

}

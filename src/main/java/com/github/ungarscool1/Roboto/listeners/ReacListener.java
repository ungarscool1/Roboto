package com.github.ungarscool1.Roboto.listeners;

import java.util.ArrayList;
import java.util.HashMap;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.listener.message.reaction.ReactionAddListener;

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
		Message message = event.getMessage().get();
		if (PFCs.containsKey(message)) {
			System.out.println("JE suis l22");
			if (event.getEmoji().asUnicodeEmoji().get().equals("✅"))  {
				String res = PFCs.get(message).join(event.getUser());
				if (res.equals("Joined") || res.equals("Starting")) {
					System.out.println("JE suis l25, qquun a rejoin");
					message.getChannel().sendMessage(event.getUser().getDisplayName(message.getServer().get()) + " a rejoint la partie");
					PFC pfc = PFCs.get(message);
					PFCs.remove(message);
					TextChannel channel = message.getChannel();
					message.delete();
					message = channel.sendMessage(pfc.joinMessage()).join();
					pfc.setJoinMessage(message);
					PFCs.put(message, pfc);
					GameCommand.updateGames(PFCs, PFCbrs, P4);
					if (res.equals("Joined")) {
						System.out.println("JE suis l33, la partie ne peut pas encore commencé !");
						message.addReactions("✅","❌");
					} else {
						System.out.println("La partie commence");
						pfc.gameHandler();
					}
				} else if (res.equals("Already in party")) {
					message.getChannel().sendMessage(event.getUser().getMentionTag() + " vous êtes déjà dans la partie");
				} else {
					message.getChannel().sendMessage(event.getUser().getMentionTag() + " la partie a déjà commencée");
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
			System.out.println("JE suis l22");
			if (event.getEmoji().asUnicodeEmoji().get().equals("✅")) {
				String res = PFCbrs.get(message).join(event.getUser());
				if (res.equals("Joined") || res.equals("Starting")) {
					System.out.println("JE suis l25, qquun a rejoin");
					message.getChannel().sendMessage(event.getUser().getDisplayName(message.getServer().get()) + " a rejoint la partie");
					PFCbr pfc = PFCbrs.get(message);
					PFCs.remove(message);
					TextChannel channel = message.getChannel();
					message.delete();
					message = channel.sendMessage(pfc.joinMessage()).join();
					pfc.setJoinMessage(message);
					PFCbrs.put(message, pfc);
					GameCommand.updateGames(PFCs, PFCbrs, P4);
					if (res.equals("Joined")) {
						System.out.println("JE suis l33, la partie ne peut pas encore commencé !");
						message.addReactions("✅", "❌");
					} else {
						System.out.println("La partie commence");
						pfc.gameHandler();
					}
				} else if (res.equals("Already in party")) {
					message.getChannel().sendMessage(event.getUser().getMentionTag() + " vous êtes déjà dans la partie");
				} else {
					message.getChannel().sendMessage(event.getUser().getMentionTag() + " la partie a déjà commencer");
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
			System.out.println("JE suis l113");
			if (event.getEmoji().asUnicodeEmoji().get().equals("✅")) {
				String res = P4.get(message).join(event.getUser());
				if (res.equals("Joined") || res.equals("Starting")) {
					System.out.println("JE suis l117, qquun a rejoin");
					message.getChannel().sendMessage(event.getUser().getDisplayName(message.getServer().get()) + " a rejoint la partie");
					Puissance4 p4 = P4.get(message);
					P4.remove(message);
					TextChannel channel = message.getChannel();
					message.delete();
					message = channel.sendMessage(p4.joinMessage()).join();
					p4.setJoinMessage(message);
					P4.put(message, p4);
					GameCommand.updateGames(PFCs, PFCbrs, P4);
					if (res.equals("Joined")) {
						System.out.println("JE suis l33, la partie ne peut pas encore commencé !");
						message.addReactions("✅", "❌");
					} else {
						System.out.println("La partie commence");
						p4.gameHandler();
					}
				} else if (res.equals("Already in party")) {
					message.getChannel().sendMessage(event.getUser().getMentionTag() + " vous êtes déjà dans la partie");
				} else {
					message.getChannel().sendMessage(event.getUser().getMentionTag() + " la partie a déjà commencer");
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
		
		System.out.println("Emoji " + event.getEmoji().getMentionTag());
	}
	
	public static void updateGames() {
		System.out.println("On a mis à jour ReacListener.Games");
		PFCs = GameCommand.PFCs;
		PFCbrs = GameCommand.PFCbrs;
		P4 = GameCommand.P4;
	}

}

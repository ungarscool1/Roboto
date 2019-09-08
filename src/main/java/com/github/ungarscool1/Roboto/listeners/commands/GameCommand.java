package com.github.ungarscool1.Roboto.listeners.commands;

import java.util.HashMap;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import com.github.ungarscool1.Roboto.entity.game.PFC;
import com.github.ungarscool1.Roboto.entity.game.PFCbr;
import com.github.ungarscool1.Roboto.entity.game.Puissance4;
import com.github.ungarscool1.Roboto.listeners.ReacListener;

public class GameCommand implements MessageCreateListener {

	public static HashMap<Message, PFC> PFCs = new HashMap<Message, PFC>();
	public static HashMap<Message, PFCbr> PFCbrs = new HashMap<Message, PFCbr>();
	public static HashMap<Message, Puissance4> P4 = new HashMap<Message, Puissance4>();
	
	public void onMessageCreate(MessageCreateEvent event) {
		Message message = event.getMessage();
		// Ignore if the message is sent in PM
		if (!message.getServer().isPresent()) {
			return;
		}
				
		if (message.getContent().contains("!game")) {
			if (message.getContent().length() > 5) {
				// !game pfc 10
				String[] args = message.getContent().substring(6).split(" ");
				if (args[0].equalsIgnoreCase("pfc")) {
					int manche = 0;
					System.out.println("Length args = " + args.length);
					if (args.length == 1) {
						manche = 3;
					} else {
						manche = Integer.parseInt(args[1]);
					}
					PFC pfc = new PFC(message.getAuthor().asUser().get(), manche);
					message = event.getChannel().sendMessage(pfc.joinMessage()).join();
					message.addReactions("✅","❌");
					pfc.setJoinMessage(message);
					PFCs.put(message, pfc);
					ReacListener.updateGames();
				} else if (args[0].equalsIgnoreCase("pfcBR")) {
					int slots = 0;
					System.out.println("Length args = " + args.length);
					if (args.length == 1) {
						slots = 100;
					} else {
						slots = Integer.parseInt(args[1]);
					}
					if (slots % 2 != 0 || slots < 2) {
						message.getChannel().sendMessage("Le nombre de slots doit être paire (multiple de 2)");
					} else {
						PFCbr pfc = new PFCbr(message.getAuthor().asUser().get(), slots);
						message = event.getChannel().sendMessage(pfc.joinMessage()).join();
						message.addReactions("✅","❌");
						pfc.setJoinMessage(message);
						PFCbrs.put(message, pfc);
						ReacListener.updateGames();
					}
				} else if (args[0].equalsIgnoreCase("^4") || args[0].equalsIgnoreCase("puissance4")) {
					Puissance4 p4 = new Puissance4(message.getAuthor().asUser().get());
					message = message.getChannel().sendMessage(p4.joinMessage()).join();
					message.addReactions("✅","❌");
					p4.setJoinMessage(message);
					P4.put(message, p4);
					ReacListener.updateGames();
				}
			} else {
				message.getChannel().sendMessage(new EmbedBuilder()
						.setTitle("Aides de la commande !game")
						.addField("pfc [nombre de manche (optionel)]", "Jouer à Pierre Feuille Ciseaux")
						.addField("pfcbr <nombre de participant>", "Jouer au Pierre Feuille Ciseaux: Battle Royal")
						.addField("^4", "Jouer à un Puissance 4")
						.addField("Prochaines fonctionnalités", "```diff\n"
								+ "+ Jouer avec plusieurs serveurs discord\n"
								+ "~ Inviter des personnes sans qu'elle fasse partie d'un serveur discord où Roboto existe\n"
								+ "```\n"
								+ "Légende:\n"
								+ "+: plutôt sûr de l'ajout\n"
								+ "~: si l'api nous le permet\n"
								+ "-: tâche annulée"));
			}
		}
		
	}
	
	public static void updateGames(HashMap<Message, PFC> pfcs, HashMap<Message, PFCbr> pFCbrs2, HashMap<Message, Puissance4> p4) {
		PFCs = pfcs;
		PFCbrs = pFCbrs2;
		P4 = p4;
	}

}

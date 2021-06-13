package com.github.ungarscool1.Roboto.listeners.commands;

import java.util.HashMap;
import java.util.ResourceBundle;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import com.github.ungarscool1.Roboto.Main;
import com.github.ungarscool1.Roboto.entity.game.PFC;
import com.github.ungarscool1.Roboto.entity.game.PFCbr;
import com.github.ungarscool1.Roboto.entity.game.Puissance4;
import com.github.ungarscool1.Roboto.listeners.ReacListener;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;

public class GameCommand implements MessageCreateListener {

	private DiscordApi api;
	public static HashMap<Message, PFC> PFCs = new HashMap<Message, PFC>();
	public static HashMap<Message, PFCbr> PFCbrs = new HashMap<Message, PFCbr>();
	public static HashMap<Message, Puissance4> P4 = new HashMap<Message, Puissance4>();

	public GameCommand(DiscordApi api) {
		this.api = api;
	}

	public void onMessageCreate(MessageCreateEvent event) {
		Message message = event.getMessage();
		if (!message.getServer().isPresent() || message.getAuthor().isBotUser()) {
			return;
		}
		ResourceBundle language = ResourceBundle.getBundle("lang.lang", Main.locByServ.get(message.getServer().get()));
				
		if (message.getContent().startsWith("!game")) {
			ITransaction transaction = Sentry.startTransaction("!game", "command");
			if (message.getContent().length() > 5) {
				String[] args = message.getContent().substring(6).split(" ");
				if (args[0].equalsIgnoreCase("pfc") || args[0].equalsIgnoreCase("rps")) {
					int manche = 0;
					if (args.length == 1) {
						manche = 3;
					} else {
						manche = Integer.parseInt(args[1]);
					}
					PFC pfc = new PFC(message.getAuthor().asUser().get(), manche, api, language.getLocale());
					message = event.getChannel().sendMessage(pfc.joinMessage()).join();
					message.addReactions("✅","❌");
					pfc.setJoinMessage(message);
					PFCs.put(message, pfc);
					ReacListener.updateGames();
				} else if (args[0].equalsIgnoreCase("pfcBR") || args[0].equalsIgnoreCase("rpsBR")) {
					int slots = 0;
					if (args.length == 1) {
						slots = 100;
					} else {
						slots = Integer.parseInt(args[1]);
					}
					if (slots % 2 != 0 || slots < 2) {
						message.getChannel().sendMessage("Le nombre de slots doit être paire (multiple de 2)");
					} else {
						PFCbr pfc = new PFCbr(message.getAuthor().asUser().get(), slots, api, Main.locByServ.get(message.getServer().get()));
						message = event.getChannel().sendMessage(pfc.joinMessage()).join();
						message.addReactions("✅","❌");
						pfc.setJoinMessage(message);
						PFCbrs.put(message, pfc);
						ReacListener.updateGames();
					}
				} else if (args[0].equalsIgnoreCase("^4") || args[0].equalsIgnoreCase("puissance4") || args[0].equalsIgnoreCase("connect4")) {
					Puissance4 p4 = new Puissance4(message.getAuthor().asUser().get(), api, Main.locByServ.get(message.getServer().get()));
					message = message.getChannel().sendMessage(p4.joinMessage()).join();
					message.addReactions("✅","❌");
					p4.setJoinMessage(message);
					P4.put(message, p4);
					ReacListener.updateGames();
				}
			} else {
				message.getChannel().sendMessage(new EmbedBuilder()
						.setTitle(language.getString("game.help.name"))
						.addField(language.getString("game.help.pfc.name"), language.getString("game.help.pfc.desc"))
						.addField(language.getString("game.help.pfcbr.name"), language.getString("game.help.pfcbr.desc"))
						.addField("^4", language.getString("game.help.p4.desc")));
			}
			transaction.finish(SpanStatus.OK);
		}
		
	}

	public static void updateGames(HashMap<Message, PFC> pfcs, HashMap<Message, PFCbr> pFCbrs2, HashMap<Message, Puissance4> p4) {
		PFCs = pfcs;
		PFCbrs = pFCbrs2;
		P4 = p4;
	}

}

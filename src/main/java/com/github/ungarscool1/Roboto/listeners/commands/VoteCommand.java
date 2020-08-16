package com.github.ungarscool1.Roboto.listeners.commands;

import java.awt.Color;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionException;

import org.graalvm.compiler.api.replacements.Snippet;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.entity.user.User;

import com.github.ungarscool1.Roboto.Main;
import com.github.ungarscool1.Roboto.entity.Vote;

public class VoteCommand implements MessageCreateListener {

	// Vote sytem
	private HashMap<User, Vote> votes = new HashMap<>();
	private HashMap<User, Message> voteMsg = new HashMap<>();

	private void onError(@Snippet.NonNullParameter User user, TextChannel channel, ResourceBundle language) {
		channel.sendMessage(new EmbedBuilder().setTitle(language.getString("errors.title"))
				.setDescription(language.getString("errors.unkown_error"))
				.addField("Report bugs in English", "https://github.com/ungarscool1/Roboto-v2/issues/new?assignees=&labels=bug&template=bug_report.md&title=!ver%20Command%20fail")
				.setColor(Color.RED));
		votes.remove(user);
	}

	public void onMessageCreate(MessageCreateEvent event) {
		Message message = event.getMessage();
		
		// Ignore if the message is sent in PM
		if (!message.getServer().isPresent()) {
			return;
		}
		
		ResourceBundle language = ResourceBundle.getBundle("lang.lang", Main.locByServ.get(message.getServer().get()));
		
		if (message.getContent().contains("!vote")) {
			User user = message.getAuthor().asUser().get();
			if (!votes.containsKey(message.getAuthor().asUser().get())) {
				message.delete("Remove vote creation message");
				Message msg = null;
				votes.put(user, new Vote(message.getAuthor().asUser().get()));
				try {
					msg = event.getChannel().sendMessage(new EmbedBuilder().setTitle(language.getString("vote.title")).setDescription(language.getString("vote.set.name"))).get();
				} catch (Exception e) {
					onError(user, event.getChannel(), language);
					return;
				}
				voteMsg.put(user, msg);
			} else {
				String res = votes.get(user).builder(message.getContent().substring(6));
				voteMsg.get(user).delete("Remove vote creation message");
				message.delete("Remove vote creation message");
				Message msg = null;
				if (res.equals("description")) {
					try {
						msg = event.getChannel().sendMessage(new EmbedBuilder().setTitle(language.getString("vote.title")).setDescription(language.getString("vote.set.desc"))).get();
					} catch (Exception e) {
						onError(user, event.getChannel(), language);
						return;
					}
				} else if (res.equals("multi")) {
					try {
						msg = event.getChannel().sendMessage(new EmbedBuilder().setTitle(language.getString("vote.title")).setDescription(language.getString("vote.set.plus2"))).get();
					} catch (Exception e) {
						onError(user, event.getChannel(), language);
						return;
					}
				} else if(res.equals("fini")) {
					Vote vote = votes.get(user);
					try {
						event.getChannel().sendMessage(new EmbedBuilder().setTitle(vote.getName()).setDescription(vote.getDescription()).setColor(new Color(107, 135, 232)).setFooter(String.format(language.getString("vote.createdBy"), user.getDisplayName(message.getServer().get())))).get().addReactions("👍","👎");
					} catch (Exception e) {
						onError(user, event.getChannel(), language);
						return;
					}
					votes.remove(user);
				} else if(res.equals("nbrOption")) {
					try {
						msg = event.getChannel().sendMessage(new EmbedBuilder().setTitle(language.getString("vote.title")).setDescription(language.getString("vote.set.howManyAnwser"))).get();
					} catch (Exception e) {
						onError(user, event.getChannel(), language);
						return;
					}
				} else if(res.contains("option")) {
					try {
						msg = event.getChannel().sendMessage(new EmbedBuilder().setTitle(language.getString("vote.title")).setDescription(language.getString("vote.answser") + " (" + res.substring(6) + ")")).get();
					} catch (Exception e) {
						onError(user, event.getChannel(), language);
						return;
					}
				} else if(res.equals("fin multi")) {
					Vote vote = votes.get(user);
					EmbedBuilder embed = new EmbedBuilder().setTitle(vote.getName()).setDescription(vote.getDescription()).setColor(new Color(107, 135, 232)).setFooter(String.format(language.getString("vote.createdBy"), user.getDisplayName(message.getServer().get())));
					try {
						for(int i = 0; i < vote.getOptions().length; i++) {
							embed.addField(language.getString("vote.answser") + " n°" + (i + 1), vote.getOptions()[i]);
						}
						msg = event.getChannel().sendMessage(embed).join();
						if (vote.getOptions().length == 1) {
							msg.addReaction("1⃣").join();
						}
						if (vote.getOptions().length == 2) {
							msg.addReactions("1⃣", "2⃣").join();
						}
						if (vote.getOptions().length == 3) {
							msg.addReactions("1⃣", "2⃣", "3⃣").join();
						}
						if (vote.getOptions().length == 4) {
							msg.addReactions("1⃣", "2⃣", "3⃣", "4⃣").join();
						}
						if (vote.getOptions().length == 5) {
							msg.addReactions("1⃣", "2⃣", "3⃣", "4⃣", "5⃣").join();
						}
						if (vote.getOptions().length == 6) {
							msg.addReactions("1⃣", "2⃣", "3⃣", "4⃣", "5⃣", "6⃣");
						}
						if (vote.getOptions().length == 7) {
							msg.addReactions("1⃣", "2⃣", "3⃣", "4⃣", "5⃣", "6⃣", "7⃣");
						}
						if (vote.getOptions().length == 8) {
							msg.addReactions("1⃣", "2⃣", "3⃣", "4⃣", "5⃣", "6⃣", "7⃣", "8⃣");
						}
						if (vote.getOptions().length == 9) {
							msg.addReactions("1⃣", "2⃣", "3⃣", "4⃣", "5⃣", "6⃣", "7⃣", "8⃣", "9⃣");
						}
						if (vote.getOptions().length == 10) {
							msg.addReactions("1⃣", "2⃣", "3⃣", "4⃣", "5⃣", "6⃣", "7⃣", "8⃣", "9⃣", "🔟");
						}
						votes.remove(user);
						return;
					} catch (MissingResourceException e) {
						votes.remove(user);
						event.getChannel().sendMessage(new EmbedBuilder().setTitle(language.getString("errors.title")).setDescription(language.getString("errors.ressources")).setColor(Color.RED));
					} catch (CancellationException | CompletionException e) {
						votes.remove(user);
						event.getChannel().sendMessage(new EmbedBuilder().setTitle(language.getString("errors.title")).setDescription(language.getString("errors.unkown_error")).setColor(Color.RED));
					}
				}
				voteMsg.put(user, msg);
			}
		}
	}

}

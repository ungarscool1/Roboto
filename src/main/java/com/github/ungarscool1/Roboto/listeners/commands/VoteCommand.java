package com.github.ungarscool1.Roboto.listeners.commands;

import java.awt.Color;
import java.util.HashMap;
import java.util.ResourceBundle;

import com.github.ungarscool1.Roboto.enums.VoteEnum;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.entity.user.User;

import com.github.ungarscool1.Roboto.Main;
import com.github.ungarscool1.Roboto.entity.Vote;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;

public class VoteCommand implements MessageCreateListener {

	// Vote sytem
	private HashMap<User, Vote> votes = new HashMap<>();
	private HashMap<User, Message> voteMsg = new HashMap<>();

	private void onError(User user, TextChannel channel, ResourceBundle language) {
		channel.sendMessage(new EmbedBuilder().setTitle(language.getString("errors.title"))
				.setDescription(language.getString("errors.unkown_error"))
				.addField("Report bugs in English", "https://github.com/ungarscool1/Roboto-v2/issues/new?assignees=&labels=bug&template=bug_report.md&title=!ver%20Command%20fail")
				.setColor(Color.RED));
		votes.remove(user);
	}

	public void onMessageCreate(MessageCreateEvent event) {
		Message message = event.getMessage();
		if (!message.getServer().isPresent() || message.getAuthor().isBotUser())
			return;
		ResourceBundle language = ResourceBundle.getBundle("lang.lang", Main.locByServ.get(message.getServer().get()));


		if (message.getContent().startsWith("!vote")) {
			ITransaction transaction = Sentry.startTransaction("!vote", "command");
			User user = message.getAuthor().asUser().get();
			if (!votes.containsKey(message.getAuthor().asUser().get())) {
				message.delete("Remove vote creation message");
				Message msg = null;
				votes.put(user, new Vote());
				try {
					msg = event.getChannel().sendMessage(new EmbedBuilder().setTitle(language.getString("vote.title")).setDescription(language.getString("vote.set.name"))).get();
				} catch (Exception e) {
					Sentry.captureException(e);
					onError(user, event.getChannel(), language);
					transaction.setStatus(SpanStatus.DATA_LOSS);
					return;
				}
				voteMsg.put(user, msg);
			} else {
				VoteEnum res = votes.get(user).builder(message.getContent().substring(6));
				voteMsg.get(user).delete("Remove vote creation message");
				message.delete("Remove vote creation message");
				Message msg = null;
				if (res.equals(VoteEnum.DESCRIPTION)) {
					try {
						msg = event.getChannel().sendMessage(new EmbedBuilder().setTitle(language.getString("vote.title")).setDescription(language.getString("vote.set.desc"))).get();
					} catch (Exception e) {
						onError(user, event.getChannel(), language);
						transaction.setStatus(SpanStatus.DATA_LOSS);
						return;
					}
				} else if (res.equals(VoteEnum.MULTI)) {
					try {
						msg = event.getChannel().sendMessage(new EmbedBuilder().setTitle(language.getString("vote.title")).setDescription(language.getString("vote.set.plus2"))).get();
					} catch (Exception e) {
						onError(user, event.getChannel(), language);
						transaction.setStatus(SpanStatus.DATA_LOSS);
						return;
					}
				} else if(res.equals(VoteEnum.ENDED)) {
					Vote vote = votes.get(user);
					try {
						event.getChannel().sendMessage(new EmbedBuilder().setTitle(vote.getName()).setDescription(vote.getDescription()).setColor(new Color(107, 135, 232)).setFooter(String.format(language.getString("vote.createdBy"), user.getDisplayName(message.getServer().get())))).get().addReactions("👍","👎");
					} catch (Exception e) {
						onError(user, event.getChannel(), language);
						transaction.setStatus(SpanStatus.DATA_LOSS);
						return;
					}
					votes.remove(user);
				} else if(res.equals(VoteEnum.OPTIONNUMBERS)) {
					try {
						msg = event.getChannel().sendMessage(new EmbedBuilder().setTitle(language.getString("vote.title")).setDescription(language.getString("vote.set.howManyAnwser"))).get();
					} catch (Exception e) {
						onError(user, event.getChannel(), language);
						transaction.setStatus(SpanStatus.DATA_LOSS);
						return;
					}
				} else if(res.equals(VoteEnum.FILL) || res.equals(VoteEnum.OPTION)) {
					try {
						msg = event.getChannel().sendMessage(new EmbedBuilder().setTitle(language.getString("vote.title")).setDescription(language.getString("vote.answser") + " (" + (votes.get(user).getWhere() - 3) + ")")).get();
					} catch (Exception e) {
						onError(user, event.getChannel(), language);
						transaction.setStatus(SpanStatus.DATA_LOSS);
						return;
					}
				} else if(res.equals(VoteEnum.MULTIPLE_ENDED)) {
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
						return;
					} catch (Exception e) {
						Sentry.captureException(e);
						transaction.setStatus(SpanStatus.UNKNOWN_ERROR);
						event.getChannel().sendMessage(new EmbedBuilder().setTitle(language.getString("errors.title")).setDescription(language.getString("errors.unkown_error")).setColor(Color.RED));
					} finally {
						votes.remove(user);
					}
				}
				voteMsg.put(user, msg);
			}
			transaction.finish();
		}
	}

}

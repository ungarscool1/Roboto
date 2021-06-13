package com.github.ungarscool1.Roboto.listeners.commands.owner;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.message.Message;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

public class ChangeGameCommand implements MessageCreateListener {
	@Override
	public void onMessageCreate(MessageCreateEvent event) {
		Message message = event.getMessage();

		if (!message.getServer().isPresent() || message.getAuthor().isBotUser())
			return;
		if (message.getContent().startsWith("@@changeGame") && message.getAuthor().isBotOwner()) {
			ITransaction transaction = Sentry.startTransaction("@@changeGame", "command");
			DiscordApi api = event.getApi();
			String arg = message.getContent().substring(12);
			if (arg.length() == 0)
				api.updateActivity(ActivityType.LISTENING, api.getServers().size() + " servers");
			else
				api.updateActivity(arg.substring(1));
			transaction.finish(SpanStatus.OK);
		}
	}
}

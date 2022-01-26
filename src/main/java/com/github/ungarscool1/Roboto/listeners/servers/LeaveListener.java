package com.github.ungarscool1.Roboto.listeners.servers;

import com.github.ungarscool1.Roboto.ServerLanguage;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;

import com.github.ungarscool1.Roboto.Main;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.event.server.ServerLeaveEvent;
import org.javacord.api.listener.server.ServerLeaveListener;

public class LeaveListener implements ServerLeaveListener {
	@Override
	public void onServerLeave(ServerLeaveEvent event) {
		ITransaction transaction = Sentry.startTransaction("onServerJoin()", "task");
		DiscordApi api = event.getApi();
		ServerLanguage serverLanguage = new ServerLanguage();
		Main.locByServ.remove(event.getServer());
		serverLanguage.save(event.getServer(), false);
		api.updateActivity(ActivityType.LISTENING, api.getServers().size() + " servers");
		Main.dbl.setStats(api.getCurrentShard(), api.getTotalShards(), api.getServers().size());
		transaction.finish(SpanStatus.OK);
	}
}

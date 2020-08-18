package com.github.ungarscool1.Roboto.listeners.servers;

import com.github.ungarscool1.Roboto.ServerLanguage;
import com.github.ungarscool1.Roboto.Main;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.event.server.ServerLeaveEvent;
import org.javacord.api.listener.server.ServerLeaveListener;

public class LeaveListener implements ServerLeaveListener {
    @Override
    public void onServerLeave(ServerLeaveEvent event) {
        DiscordApi api = event.getApi();
        ServerLanguage serverLanguage = new ServerLanguage();
        Main.locByServ.remove(event.getServer());
        serverLanguage.removeServer(event.getServer());
        api.updateActivity(ActivityType.LISTENING, api.getServers().size() + " servers");
        Main.dbl.setStats(api.getCurrentShard(), api.getTotalShards(), api.getServers().size());
    }
}

package com.github.ungarscool1.Roboto.listeners.commands.owner;

import com.github.ungarscool1.Roboto.Main;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class OwnerInfoCommand implements MessageCreateListener {
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        Message message = event.getMessage();

        if (!message.getServer().isPresent() || message.getAuthor().isBotUser())
            return;
        if (message.getContent().equalsIgnoreCase("@@info") && message.getAuthor().isBotOwner()) {
			ITransaction transaction = Sentry.startTransaction("@@info", "command");
            System.out.println("Getting servers info...");
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Informations de tout les serveurs").setDescription("Toutes les informations des serveurs sont affichés ici");
            event.getApi().getServers().forEach(server -> {
                embed.addField(server.getName(), "Owner: " + server.getOwner().get().getDiscriminatedName() + "\nIl y a " + server.getMemberCount() + " utilisateurs sur le serveur.\nRégion: " + server.getRegion().getName() + "\nEst admin ? " + server.isAdmin(event.getApi().getYourself()) + "\nLangue du bot: " + Main.locByServ.get(server).getDisplayLanguage(Locale.FRANCE) + " - " + Main.locByServ.get(server).getDisplayCountry(Locale.FRANCE));
            });
            embed.setAuthor(event.getApi().getYourself()).setColor(Color.GREEN);
            message.getChannel().sendMessage(embed);
            transaction.setStatus(SpanStatus.OK);
        }
    }
}

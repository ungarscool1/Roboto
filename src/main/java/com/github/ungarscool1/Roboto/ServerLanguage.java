package com.github.ungarscool1.Roboto;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Writer;
import java.io.FileWriter;
import java.lang.Exception;

import org.javacord.api.entity.server.Server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;

public class ServerLanguage {
	
	private BufferedReader reader = null;
	private Gson gson = new Gson();
	
	public ServerLanguage() {
		ITransaction transaction = Sentry.startTransaction("ServerLanguage()", "task");
		try {
			reader = new BufferedReader(new FileReader("serversLanguage.json"));
			gson = new GsonBuilder().setPrettyPrinting().create();
		} catch (FileNotFoundException e) {
			System.err.println("serversLanguage.json can't be readable / writable / do not exist");
			Sentry.captureException(e);
			transaction.finish(SpanStatus.NOT_FOUND);
			System.exit(1);
		}
		transaction.finish(SpanStatus.OK);
	}

	public void setServerLanguage(Server server, String lang) {
		ITransaction transaction = Sentry.startTransaction("ServerLanguage::setServerLanguage()", "task");
		JsonObject object = gson.fromJson(reader, JsonObject.class);
		if (!object.get(server.getIdAsString()).isJsonNull()) {
			object.remove(server.getIdAsString());
		}
		object.addProperty(server.getIdAsString(), lang);
		try (Writer writer = new FileWriter("serversLanguage.json")) {
			gson.toJson(object, writer);
		} catch (Exception e) {
			Sentry.captureException(e);
			transaction.setStatus(SpanStatus.ABORTED);
		} finally {
			transaction.finish();
		}
	}
	
	public String getServerLanguage(Server server) {
		JsonObject object = gson.fromJson(reader, JsonObject.class);
		String lang;
		try {
			lang = object.get(server.getIdAsString()).getAsString();
		} catch (NullPointerException e) {
			setServerLanguage(server, "en_US");
			Sentry.captureException(e);
			e.printStackTrace();
			return "en_US";
		}
		return lang;
	}
	
	public void addServer(Server server) {
		JsonObject object = gson.fromJson(reader, JsonObject.class);
		object.addProperty(server.getIdAsString(), "en_US");
		try (Writer writer = new FileWriter("serversLanguage.json")) {
			gson.toJson(object, writer);
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
		}
	}

	public void removeServer(Server server) {
		JsonObject object = gson.fromJson(reader,JsonObject.class);
		object.remove(server.getIdAsString());
		try (Writer writer = new FileWriter("serversLanguage.json")) {
			gson.toJson(object, writer);
		} catch (Exception e) {
			Sentry.captureException(e);
			e.printStackTrace();
		}
	}
	
}

package com.github.ungarscool1.Roboto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.javacord.api.entity.server.Server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class ServerLanguage {
	
	private BufferedReader reader = null;
	private boolean init = false;
	private Gson gson = new Gson();
	
	public ServerLanguage() {
		try {
            reader = new BufferedReader(new FileReader("serversLanguage.json"));
    		gson = new GsonBuilder().setPrettyPrinting().create();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("The file can't be readable / writable / do not exist");
        }
	}

	public void setServerLanguage(Server server, String lang) {
		JsonObject object = gson.fromJson(reader,JsonObject.class);
        object.remove(server.getIdAsString());
        object.addProperty(server.getIdAsString(), lang);
    }
    
    public String getServerLanguage(Server server) {
    	JsonObject object = gson.fromJson(reader,JsonObject.class);
    	if (object.get(server.getIdAsString()).isJsonNull()) {
    		setServerLanguage(server, "en_US");
    		return "en_US";
    	} else
    		return object.get(server.getIdAsString()).getAsString();
    }
	
}

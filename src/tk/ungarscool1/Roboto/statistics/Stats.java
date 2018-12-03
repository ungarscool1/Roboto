package tk.ungarscool1.Roboto.statistics;

import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Stats {

    private String key;
    private String url;
    private String metricId;

    public Stats(String[] args) {
        if (args[0].equalsIgnoreCase("true")) {
            this.key = args[1];
            this.url = args[2]+"/api/v1/";
            this.metricId = args[3];
        } else {
            return;
        }
    }

    public void sendStats() {
        try{
            URL url = new URL(this.url+"metrics/"+this.metricId+"/points");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("X-Cachet-Token", this.key);
            connection.setRequestMethod("POST");
            long timestamp = System.currentTimeMillis() / 1000L;
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("value",1);
            jsonObject.addProperty("timestamp",timestamp+"");
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(jsonObject.toString());
            writer.flush();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer stringBuffer = new StringBuffer();
            String res ;
            while ((res = in.readLine()) != null) {
                stringBuffer.append(res);
            }
            in.close();
            System.out.println("res: "+stringBuffer.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setBotStatus(String status) {
        int statut = 0;
        if (status.equalsIgnoreCase("online")) {
            statut = 1;
        } else if (status.equalsIgnoreCase("perf")) {
            statut = 2;
        } else if (status.equalsIgnoreCase("partial outage")) {
            statut = 3;
        } else if (status.equalsIgnoreCase("major outage")) {
            statut = 4;
        } else {
            System.err.println("L'argument "+status+" n'est pas valide !");
            return;
        }


        try{
            URL url = new URL(this.url+"components/1");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("X-Cachet-Token", this.key);
            connection.setRequestMethod("PUT");
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status",statut);
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(jsonObject.toString());
            writer.flush();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer stringBuffer = new StringBuffer();
            String res ;
            while ((res = in.readLine()) != null) {
                stringBuffer.append(res);
            }
            in.close();
            System.out.println("res: "+stringBuffer.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

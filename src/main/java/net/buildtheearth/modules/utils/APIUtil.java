package net.buildtheearth.modules.utils;

import org.bukkit.Bukkit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;

public class APIUtil {

    // A function that returns the content of a GET Request from a given URL
    public static String get(URL url) {
        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            // Add headers to the request
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            return content.toString();
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "An error occurred while performing a GET request to an API!");
            e.printStackTrace();
        }

        return null;
    }

    public static JSONArray createJSONArray(String jsonString){
        JSONArray jsonArray = new JSONArray();
        JSONParser jsonParser = new JSONParser();
        if ((jsonString != null) && !(jsonString.isEmpty())) {
            try {
                jsonArray = (JSONArray) jsonParser.parse(jsonString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

    public static JSONObject createJSONObject(String jsonString){
        JSONObject jsonObject = new JSONObject();
        JSONParser jsonParser = new JSONParser();
        if ((jsonString != null) && !(jsonString.isEmpty())) {
            try {
                jsonObject = (JSONObject) jsonParser.parse(jsonString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

}

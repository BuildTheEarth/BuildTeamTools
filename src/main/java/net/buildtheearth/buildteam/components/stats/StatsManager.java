package net.buildtheearth.buildteam.components.stats;

import net.buildtheearth.buildteam.components.BTENetwork;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class StatsManager {

    public static int RATE_LIMIT = BTENetwork.CACHE_UPLOAD_SPEED / 20;

    private StatsServer statsServer;
    private HashMap<UUID, StatsPlayer> statsPlayerList;

    public StatsManager(){
        statsServer = new StatsServer();
        statsPlayerList = new HashMap<>();
    }

    public StatsServer getStatsServer() {
        return statsServer;
    }

    public StatsPlayer getStatsPlayer(UUID uuid){
        if(statsPlayerList.get(uuid) == null)
            addStatsPlayer(uuid);

        return statsPlayerList.get(uuid);
    }

    public void resetCache(){
        statsServer = new StatsServer();
        statsPlayerList = new HashMap<>();
    }

    public void addStatsPlayer(UUID uuid){
        statsPlayerList.remove(uuid);
        statsPlayerList.put(uuid, new StatsPlayer(uuid));
    }

    public JSONObject getCurrentCache(){
        JSONObject jsonObject = statsServer.toJSON();

        //Player Stats
        JSONArray jsonArray = new JSONArray();
        for(UUID uuid : statsPlayerList.keySet())
            jsonArray.put(statsPlayerList.get(uuid).toJSON());
        jsonObject.put("PLAYERS", jsonArray);

        return jsonObject;
    }
}

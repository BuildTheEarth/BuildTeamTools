package net.buildtheearth.modules.stats;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.buildtheearth.Main;
import net.buildtheearth.modules.network.ProxyManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class StatsManager {

    public static int RATE_LIMIT = ProxyManager.CACHE_UPLOAD_SPEED / 20;

    private StatsServer statsServer;
    private HashMap<UUID, StatsPlayer> statsPlayerList;

    public StatsManager() {
        statsServer = new StatsServer();
        statsPlayerList = new HashMap<>();
    }

    public StatsServer getStatsServer() {
        return statsServer;
    }

    public StatsPlayer getStatsPlayer(UUID uuid) {
        if (statsPlayerList.get(uuid) == null)
            addStatsPlayer(uuid);

        return statsPlayerList.get(uuid);
    }

    public void resetCache() {
        statsServer = new StatsServer();
        statsPlayerList = new HashMap<>();
    }

    public void addStatsPlayer(UUID uuid) {
        statsPlayerList.remove(uuid);
        statsPlayerList.put(uuid, new StatsPlayer(uuid));
    }

    /**
     * Sends the current stats cache to the network.
     * This also saves it to the database.
     *
     * @return true if success, false if failed
     */
    public boolean updateAndSave() {
        List<UUID> communicators = Main.getBuildTeamTools().getProxyManager().getCommunicators();
        if (communicators.size() == 0) return false;

        if (!Main.instance.isEnabled()) return false;

        Player p = Bukkit.getPlayer(communicators.get(0));

        if (p == null) {
            communicators.remove(0);
            return false;
        }
        if (!p.isOnline()) {
            communicators.remove(0);
            return false;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Stats");
        out.writeUTF(p.getUniqueId().toString());
        out.writeUTF(Main.getBuildTeamTools().getStatsManager().getCurrentCache().toJSONString());

        p.sendPluginMessage(Main.instance, "BuildTeam", out.toByteArray());

        return true;
    }

    public JSONObject getCurrentCache() {
        JSONObject jsonObject = statsServer.toJSON();

        //Player Stats
        JSONArray jsonArray = new JSONArray();
        for (UUID uuid : statsPlayerList.keySet())
            jsonArray.put(statsPlayerList.get(uuid).toJSON());
        jsonObject.put("PLAYERS", jsonArray);

        return jsonObject;
    }
}

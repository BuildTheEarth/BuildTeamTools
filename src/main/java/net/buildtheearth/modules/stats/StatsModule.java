package net.buildtheearth.modules.stats;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.Module;
import net.buildtheearth.modules.network.NetworkModule;
import net.buildtheearth.modules.stats.listeners.StatsListener;
import net.buildtheearth.modules.stats.model.StatsPlayer;
import net.buildtheearth.modules.stats.model.StatsServer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class StatsModule extends Module {

    public static int RATE_LIMIT = NetworkModule.CACHE_UPLOAD_SPEED / 20;

    private StatsServer statsServer;
    private HashMap<UUID, StatsPlayer> statsPlayerList;



    private static StatsModule instance = null;

    public StatsModule() {
        super("Stats");
    }

    public static StatsModule getInstance() {
        return instance == null ? instance = new StatsModule() : instance;
    }



    @Override
    public void enable() {
        statsServer = new StatsServer();
        statsPlayerList = new HashMap<>();

        super.enable();
    }

    @Override
    public void disable() {
        if(!isEnabled())
            return;

        updateAndSave();

        super.disable();
    }

    @Override
    public void registerListeners() {
        super.registerListeners(new StatsListener());
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
        List<UUID> communicators = NetworkModule.getInstance().getCommunicators();
        if (communicators.size() == 0) return false;

        if (!BuildTeamTools.getInstance().isEnabled()) return false;

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
        out.writeUTF(StatsModule.getInstance().getCurrentCache().toJSONString());

        p.sendPluginMessage(BuildTeamTools.getInstance(), "btt:buildteam", out.toByteArray());

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

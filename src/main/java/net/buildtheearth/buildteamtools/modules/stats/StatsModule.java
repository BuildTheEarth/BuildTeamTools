package net.buildtheearth.buildteamtools.modules.stats;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.Module;
import net.buildtheearth.buildteamtools.modules.network.NetworkModule;
import net.buildtheearth.buildteamtools.modules.stats.listeners.StatsListener;
import net.buildtheearth.buildteamtools.modules.stats.model.StatsPlayer;
import net.buildtheearth.buildteamtools.modules.stats.model.StatsServer;
import net.buildtheearth.buildteamtools.utils.WikiLinks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class StatsModule extends Module {

    public static final int RATE_LIMIT = NetworkModule.CACHE_UPLOAD_SPEED / 20;

    @Getter
    private StatsServer statsServer;
    private HashMap<UUID, StatsPlayer> statsPlayerList;



    private static StatsModule instance = null;

    public StatsModule() {
        super("Stats", WikiLinks.STATS);
    }

    public static StatsModule getInstance() {
        return instance == null ? instance = new StatsModule() : instance;
    }



    @Override
    public void enable() {
        if (NetworkModule.getInstance().getBuildTeam() == null) {
            shutdown("The Network Module failed to load the Build Team.");
            return;
        }

        if (!NetworkModule.getInstance().getBuildTeam().isConnected()) {
            shutdown("The Build Team have to be connected to the BtE Network (Proxy).");
            return;
        }

        try {
            if (!Bukkit.getServerConfig().isProxyEnabled()) {
                shutdown("The Build Team have to be connected to the BtE Network (Proxy).");
                return;
            }
        } catch (NoSuchMethodError e) { /* it's fine - we assume proxy is enabled This Method only exist in 1.21.5+ */}

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
        if (communicators.isEmpty()) return false;

        if (!BuildTeamTools.getInstance().isEnabled()) return false;

        Player p = Bukkit.getPlayer(communicators.getFirst());

        if (p == null) {
            communicators.removeFirst();
            return false;
        }
        if (!p.isOnline()) {
            communicators.removeFirst();
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
        for (StatsPlayer statsPlayer : statsPlayerList.values())
            jsonArray.put(statsPlayer.toJSON());
        jsonObject.put("PLAYERS", jsonArray);

        return jsonObject;
    }
}

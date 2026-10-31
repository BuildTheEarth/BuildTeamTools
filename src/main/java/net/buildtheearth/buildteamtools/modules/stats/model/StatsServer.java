package net.buildtheearth.buildteamtools.modules.stats.model;

import org.json.JSONObject;

import java.util.HashMap;

public class StatsServer {

    private final HashMap<StatsServerType, Object> stats;

    public StatsServer() {
        stats = new HashMap<>();
    }

    public void addValue(StatsServerType statsServerType, Object value) {
        Object updated = StatsValues.accumulate(stats.get(statsServerType), value);
        if (updated == null) {
            return;
        }
        stats.put(statsServerType, updated);
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();

        for (StatsServerType statsServerType : stats.keySet()) {
            jsonObject.put(statsServerType.name(), stats.get(statsServerType));
        }

        return jsonObject;
    }
}

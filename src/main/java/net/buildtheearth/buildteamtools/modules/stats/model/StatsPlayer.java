package net.buildtheearth.buildteamtools.modules.stats.model;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.UUID;

public class StatsPlayer {

    private final UUID uuid;
    private final HashMap<StatsPlayerType, Object> stats;

    public StatsPlayer(UUID uuid) {
        this.uuid = uuid;
        this.stats = new HashMap<>();
    }

    public void addValue(StatsPlayerType statsPlayerType, Object value) {
        Object updated = StatsValues.accumulate(stats.get(statsPlayerType), value);
        if (updated == null) {
            return;
        }
        stats.put(statsPlayerType, updated);
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("UUID", uuid.toString());
        for (StatsPlayerType statsPlayerType : stats.keySet()) {
            jsonObject.put(statsPlayerType.name(), stats.get(statsPlayerType));
        }

        return jsonObject;
    }
}

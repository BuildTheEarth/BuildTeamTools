package net.buildtheearth.modules.stats.model;

import net.buildtheearth.modules.stats.StatsModule;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.UUID;

public class StatsPlayer {

    private final UUID uuid;
    private final HashMap<StatsPlayerType, Object> stats;

    public StatsPlayer(UUID uuid) {
        this.uuid = uuid;
        this.stats = new HashMap<>();
    }

    public void addValue(StatsPlayerType statsPlayerType, Object value){
        Object object = stats.get(statsPlayerType);

        if(object == null)
            object = value;
        else if (object instanceof Integer && value instanceof Integer) {
            if ((Integer) object > StatsModule.RATE_LIMIT)
                return;

            object = (Integer) object + (Integer) value;
        } else if (object instanceof Double && value instanceof Double) {
            if ((Double) object > StatsModule.RATE_LIMIT)
                return;

            object = (Double) object + (Double) value;
        } else if (object instanceof Float && value instanceof Float) {
            if ((Float) object > StatsModule.RATE_LIMIT)
                return;

            object = (Float) object + (Float) value;
        } else if (object instanceof Long && value instanceof Long) {
            if ((Long) object > StatsModule.RATE_LIMIT)
                return;

            object = (Long) object + (Long) value;
        }else {
            object = value;
        }

        stats.remove(statsPlayerType);
        stats.put(statsPlayerType, object);
    }

    public JSONObject toJSON(){
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("UUID", uuid);
        for(StatsPlayerType statsPlayerType : stats.keySet())
            jsonObject.put(statsPlayerType, stats.get(statsPlayerType));

        return jsonObject;
    }
}

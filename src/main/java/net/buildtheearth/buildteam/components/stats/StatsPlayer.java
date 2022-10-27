package net.buildtheearth.buildteam.components.stats;

import net.buildtheearth.buildteam.components.BTENetwork;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.UUID;

public class StatsPlayer {

    private UUID uuid;
    private HashMap<StatsPlayerType, Object> stats;

    public StatsPlayer(UUID uuid){
        this.uuid = uuid;
        this.stats = new HashMap<>();
    }

    public void addValue(StatsPlayerType statsPlayerType, Object value){
        Object object = stats.get(statsPlayerType);

        if(object == null)
            object = value;
        else if(object instanceof Integer && value instanceof Integer) {
            if((Integer) object > StatsManager.RATE_LIMIT)
                return;

            object = (Integer) object + (Integer) value;
        }else if(object instanceof Double && value instanceof Double) {
            if((Double) object > StatsManager.RATE_LIMIT)
                return;

            object = (Double) object + (Double) value;
        }else if(object instanceof Float && value instanceof Float) {
            if((Float) object > StatsManager.RATE_LIMIT)
                return;

            object = (Float) object + (Float) value;
        }else if(object instanceof Long && value instanceof Long) {
            if((Long) object > StatsManager.RATE_LIMIT)
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

package net.buildtheearth.modules.stats;

import org.json.simple.JSONObject;

import java.util.HashMap;

public class StatsServer {

    private HashMap<StatsServerType, Object> stats;

    public StatsServer(){
        stats = new HashMap<>();
    }

    public void addValue(StatsServerType statsServerType, Object value){
        Object object = stats.get(statsServerType);

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

        stats.remove(statsServerType);
        stats.put(statsServerType, object);
    }

    public JSONObject toJSON(){
        JSONObject jsonObject = new JSONObject();

        for(StatsServerType statsServerType : stats.keySet())
            jsonObject.put(statsServerType, stats.get(statsServerType));

        return jsonObject;
    }
}


package net.buildtheearth.buildteam.components.stats;

import lombok.Getter;
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
        else if(object instanceof Integer && value instanceof Integer)
            object = (Integer) object + (Integer) value;
        else if(object instanceof Double && value instanceof Double)
            object = (Double) object + (Double) value;
        else if(object instanceof Float && value instanceof Float)
            object = (Float) object + (Float) value;
        else if(object instanceof Long && value instanceof Long)
            object = (Long) object + (Long) value;
        else
            object = value;

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


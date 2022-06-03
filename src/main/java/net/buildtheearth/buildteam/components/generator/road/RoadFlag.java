package net.buildtheearth.buildteam.components.generator.road;

import net.buildtheearth.buildteam.components.generator.house.HouseFlag;

public enum RoadFlag {
    LANE_COUNT("c"),
    LANE_WIDTH("w"),
    ROAD_MATERIAL("m"),
    LANE_GAP("g"),

    MARKING_LENGTH("ll"),
    MARKING_GAP("lg"),
    MARKING_MATERIAL("lm"),

    SIDEWALK_WIDTH("sw"),
    SIDEWALK_MATERIAL("sm");

    private String flag;

    RoadFlag(String flag){
        this.flag = flag;
    }

    public String getFlag() {
        return flag;
    }

    public static RoadFlag byString(String flag){
        for(RoadFlag roadFlag : RoadFlag.values())
            if(roadFlag.getFlag().equalsIgnoreCase(flag))
                return roadFlag;

        return null;
    }
}

package net.buildtheearth.modules.generator.modules.road;


import net.buildtheearth.modules.generator.model.Flag;

public enum RoadFlag implements Flag {
    LANE_COUNT("c"),
    LANE_WIDTH("w"),
    ROAD_MATERIAL("m"),
    LANE_GAP("g"),

    MARKING_LENGTH("ll"),
    MARKING_GAP("lg"),
    MARKING_MATERIAL("lm"),

    SIDEWALK_WIDTH("sw"),
    SIDEWALK_MATERIAL("sm"),

    SIDEWALK_SLAB_COLOR("ss"),      // String "123:24,21:3,3,..."

    ROAD_SLAB_COLOR("rs"),          // String "123:24,21:3,3,..."

    CROSSWALK("cw");                // boolean (ON/OFF)

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

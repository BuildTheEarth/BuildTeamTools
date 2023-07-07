package net.buildtheearth.buildteam.components.generator.tree;

import net.buildtheearth.buildteam.components.generator.road.RoadFlag;

public enum TreeFlag {

    TYPE("t"),
    WIDTH("w"),
    HEIGHT("h");

    private String flag;

    TreeFlag(String flag){
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

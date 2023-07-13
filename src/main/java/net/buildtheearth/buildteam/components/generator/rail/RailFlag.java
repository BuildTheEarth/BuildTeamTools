package net.buildtheearth.buildteam.components.generator.rail;

import net.buildtheearth.buildteam.components.generator.Flag;

public enum RailFlag implements Flag {
    LANE_COUNT("c");

    private String flag;

    RailFlag(String flag){
        this.flag = flag;
    }

    public String getFlag() {
        return flag;
    }

    public static RailFlag byString(String flag){
        for(RailFlag railFlag : RailFlag.values())
            if(railFlag.getFlag().equalsIgnoreCase(flag))
                return railFlag;

        return null;
    }
}

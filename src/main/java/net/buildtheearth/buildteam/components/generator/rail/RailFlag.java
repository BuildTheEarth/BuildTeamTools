package net.buildtheearth.buildteam.components.generator.rail;

import net.buildtheearth.buildteam.components.generator.Flag;
import net.buildtheearth.buildteam.components.generator.FlagType;

public enum RailFlag implements Flag {
    LANE_COUNT("c", FlagType.INTEGER);

    private final String flag;
    private final FlagType flagType;

    RailFlag(String flag, FlagType flagType){
        this.flag = flag;
        this.flagType = flagType;
    }

    @Override
    public String getFlag() {
        return flag;
    }

    @Override
    public FlagType getFlagType() {
        return null;
    }

    public static RailFlag byString(String flag){
        for(RailFlag railFlag : RailFlag.values())
            if(railFlag.getFlag().equalsIgnoreCase(flag))
                return railFlag;

        return null;
    }
}

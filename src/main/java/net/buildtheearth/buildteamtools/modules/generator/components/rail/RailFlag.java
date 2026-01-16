package net.buildtheearth.buildteamtools.modules.generator.components.rail;

import net.buildtheearth.buildteamtools.modules.generator.model.Flag;
import net.buildtheearth.buildteamtools.modules.generator.model.FlagType;

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
        return flagType;
    }

    public static RailFlag byString(String flag){
        for(RailFlag railFlag : RailFlag.values())
            if(railFlag.getFlag().equalsIgnoreCase(flag))
                return railFlag;

        return null;
    }
}

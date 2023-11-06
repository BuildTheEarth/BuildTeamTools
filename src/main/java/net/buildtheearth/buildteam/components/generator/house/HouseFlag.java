package net.buildtheearth.buildteam.components.generator.house;

import net.buildtheearth.buildteam.components.generator.Flag;
import net.buildtheearth.buildteam.components.generator.FlagType;

public enum HouseFlag implements Flag {
    WALL_COLOR("w", FlagType.BLOCKS),
    ROOF_COLOR("r", FlagType.BLOCKS),
    BASE_COLOR("b", FlagType.BLOCKS),
    WINDOW_COLOR("wd", FlagType.BLOCKS),

    ROOF_TYPE("rt", FlagType.ENUM),
    FLOOR_COUNT("fc", FlagType.INTEGER),
    FLOOR_HEIGHT("fh", FlagType.INTEGER),
    BASE_HEIGHT("bh", FlagType.INTEGER),

    WINDOW_HEIGHT("wdh", FlagType.INTEGER),
    WINDOW_WIDTH("wdw", FlagType.INTEGER),
    WINDOW_DISTANCE("wdd", FlagType.INTEGER),

    MAX_ROOF_HEIGHT("mrh", FlagType.INTEGER);

    private final String flag;
    private final FlagType flagType;

    HouseFlag(String flag, FlagType flagType){
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

    public static HouseFlag byString(String flag){
        for(HouseFlag houseFlag : HouseFlag.values())
            if(houseFlag.getFlag().equalsIgnoreCase(flag))
                return houseFlag;
        return null;
    }
}

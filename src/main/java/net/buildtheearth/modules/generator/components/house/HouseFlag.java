package net.buildtheearth.modules.generator.components.house;

import net.buildtheearth.modules.generator.model.FlagType;
import net.buildtheearth.modules.generator.model.Flag;

public enum HouseFlag implements Flag {
    WALL_COLOR("w", FlagType.XMATERIAL_LIST),
    ROOF_COLOR("r", FlagType.XMATERIAL_LIST),
    BASE_COLOR("b", FlagType.XMATERIAL_LIST),
    WINDOW_COLOR("wd", FlagType.XMATERIAL_LIST),
    BALCONY_COLOR("bc", FlagType.XMATERIAL_LIST),
    BALCONY_FENCE_COLOR("bfc", FlagType.XMATERIAL),
    ROOF_TYPE("rt", FlagType.ROOF_TYPE),
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

package net.buildtheearth.buildteam.components.generator.house;

import net.buildtheearth.utils.Utils;

public enum HouseFlag {


    WALL_COLOR("w"),
    ROOF_COLOR("r"),
    BASE_COLOR("b"),
    WINDOW_COLOR("wd"),

    ROOF_TYPE("rt"),
    FLOOR_COUNT("fc"),
    FLOOR_HEIGHT("fh"),
    BASE_HEIGHT("bh"),

    WINDOW_HEIGHT("wdh"),
    WINDOW_WIDTH("wdw"),
    WINDOW_DISTANCE("wdd"),

    MAX_ROOF_HEIGHT("mrh");

    private String flag;

    HouseFlag(String flag){
        this.flag = flag;
    }

    public String getFlag() {
        return flag;
    }

    public static HouseFlag byString(String flag){
        for(HouseFlag houseFlag : HouseFlag.values())
        if(houseFlag.getFlag().equalsIgnoreCase(flag))
            return houseFlag;

        return null;
    }
}

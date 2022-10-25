package net.buildtheearth.buildteam.components.generator.house;

import net.buildtheearth.utils.Utils;

public enum HouseFlag {


    WALL_COLOR("w"),            // String "123:24, 21:3, 3, ..."
    ROOF_COLOR("r"),            // String "123:24, 21:3, 3, ..."
    BASE_COLOR("b"),            // String "123:24, 21:3, 3, ..."
    WINDOW_COLOR("wd"),         // String "123:24, 21:3, 3, ..."

    ROOF_TYPE("rt"),            // RoofType
    FLOOR_COUNT("fc"),          // Integer
    FLOOR_HEIGHT("fh"),         // Integer
    BASE_HEIGHT("bh"),          // Integer

    WINDOW_HEIGHT("wdh"),       // Integer
    WINDOW_WIDTH("wdw"),        // Integer
    WINDOW_DISTANCE("wdd"),     // Integer

    MAX_ROOF_HEIGHT("mrh");     // Integer

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

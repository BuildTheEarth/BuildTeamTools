package net.buildtheearth.modules.generator.components.rail;

import net.buildtheearth.modules.generator.model.Flag;

public enum RailFlag implements Flag {
    LANE_COUNT("c");

    private final String flag;

    RailFlag(String flag) {
        this.flag = flag;
    }

    public static RailFlag byString(String flag) {
        for (RailFlag railFlag : RailFlag.values())
            if (railFlag.getFlag().equalsIgnoreCase(flag))
                return railFlag;

        return null;
    }

    public String getFlag() {
        return flag;
    }
}

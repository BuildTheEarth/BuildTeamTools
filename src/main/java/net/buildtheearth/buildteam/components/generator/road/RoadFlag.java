package net.buildtheearth.buildteam.components.generator.road;


import net.buildtheearth.buildteam.components.generator.Flag;
import net.buildtheearth.buildteam.components.generator.FlagType;

public enum RoadFlag implements Flag {
    LANE_COUNT("c", FlagType.NUMBER),
    LANE_WIDTH("w", FlagType.NUMBER),
    ROAD_MATERIAL("m", FlagType.BLOCKS),
    LANE_GAP("g", FlagType.NUMBER),

    MARKING_LENGTH("ll", FlagType.NUMBER),
    MARKING_GAP("lg", FlagType.NUMBER),
    MARKING_MATERIAL("lm", FlagType.BLOCKS),

    SIDEWALK_WIDTH("sw", FlagType.NUMBER),
    SIDEWALK_MATERIAL("sm", FlagType.BLOCKS),

    SIDEWALK_SLAB_COLOR("ss", FlagType.BLOCKS),

    ROAD_SLAB_COLOR("rs", FlagType.BLOCKS),

    CROSSWALK("cw", FlagType.BOOLEAN);

    private final String flag;
    private final FlagType flagType;

    RoadFlag(String flag, FlagType flagType){
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

    public static RoadFlag byString(String flag){
        for(RoadFlag roadFlag : RoadFlag.values())
            if(roadFlag.getFlag().equalsIgnoreCase(flag))
                return roadFlag;

        return null;
    }
}

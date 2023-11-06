package net.buildtheearth.buildteam.components.generator.road;


import net.buildtheearth.buildteam.components.generator.Flag;
import net.buildtheearth.buildteam.components.generator.FlagType;

public enum RoadFlag implements Flag {
    LANE_COUNT("c", FlagType.INTEGER),
    LANE_WIDTH("w", FlagType.INTEGER),
    ROAD_MATERIAL("m", FlagType.BLOCKS),
    LANE_GAP("g", FlagType.INTEGER),

    MARKING_LENGTH("ll", FlagType.INTEGER),
    MARKING_GAP("lg", FlagType.INTEGER),
    MARKING_MATERIAL("lm", FlagType.BLOCKS),

    SIDEWALK_WIDTH("sw", FlagType.INTEGER),
    SIDEWALK_MATERIAL("sm", FlagType.BLOCKS),

    SIDEWALK_SLAB_COLOR("ss", FlagType.BLOCKS),

    ROAD_SLAB_COLOR("rs", FlagType.BLOCKS),

    CROSSWALK("cw", FlagType.BOOLEAN),

    STREET_LAMP_TYPE("st", FlagType.ENUM),
    STREET_LAMP_DISTANCE("sd", FlagType.INTEGER);

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

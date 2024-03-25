package net.buildtheearth.modules.generator.components.road;


import net.buildtheearth.modules.generator.model.FlagType;
import net.buildtheearth.modules.generator.model.Flag;

public enum RoadFlag implements Flag {
    LANE_COUNT("c", FlagType.INTEGER),
    LANE_WIDTH("w", FlagType.INTEGER),
    ROAD_MATERIAL("m", FlagType.XMATERIAL_LIST),
    LANE_GAP("g", FlagType.INTEGER),

    MARKING_LENGTH("ll", FlagType.INTEGER),
    MARKING_GAP("lg", FlagType.INTEGER),
    MARKING_MATERIAL("lm", FlagType.XMATERIAL),

    SIDEWALK_WIDTH("sw", FlagType.INTEGER),
    SIDEWALK_MATERIAL("sm", FlagType.XMATERIAL_LIST),

    SIDEWALK_SLAB_COLOR("ss", FlagType.XMATERIAL_LIST),

    ROAD_SLAB_COLOR("rs", FlagType.XMATERIAL_LIST),

    CROSSWALK("cw", FlagType.BOOLEAN),

    STREET_LAMP_TYPE("st", FlagType.STRING),
    STREET_LAMP_DISTANCE("sd", FlagType.INTEGER),

    ROAD_SIDE("rs", FlagType.INTEGER);

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

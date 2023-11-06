package net.buildtheearth.buildteam.components.generator.field;

import net.buildtheearth.buildteam.components.generator.Flag;
import net.buildtheearth.buildteam.components.generator.FlagType;

public enum FieldFlag implements Flag {

    CROP_TYPE("t", FlagType.ENUM),
    CROP_STAGE("s", FlagType.ENUM),
    FENCE("f", FlagType.BLOCKS);


    private final String flag;
    private final FlagType flagType;


    FieldFlag(String flag, FlagType flagType) {
        this.flag = flag;
        this.flagType = flagType;
    }

    public static FieldFlag byString(String flag) {
        for (FieldFlag fieldFlag : FieldFlag.values())
            if (fieldFlag.getFlag().equalsIgnoreCase(flag))
                return fieldFlag;
        return null;

    }

    @Override
    public String getFlag() {
        return flag;
    }

    @Override
    public FlagType getFlagType() {
        return flagType;
    }
}

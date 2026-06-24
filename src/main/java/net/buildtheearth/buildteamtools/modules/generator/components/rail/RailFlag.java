package net.buildtheearth.buildteamtools.modules.generator.components.rail;

import lombok.Getter;
import net.buildtheearth.buildteamtools.modules.generator.model.Flag;
import net.buildtheearth.buildteamtools.modules.generator.model.FlagType;
import org.jspecify.annotations.Nullable;

public enum RailFlag implements Flag {

    RAIL_TYPE("t", FlagType.RAIL_TYPE);

    @Getter
    private final String flag;

    @Getter
    private final FlagType flagType;

    RailFlag(String flag, FlagType flagType) {
        this.flag = flag;
        this.flagType = flagType;
    }

    public static @Nullable RailFlag byString(String flag) {
        for (RailFlag railFlag : RailFlag.values()) {
            if (railFlag.getFlag().equalsIgnoreCase(flag))
                return railFlag;
        }

        return null;
    }
}

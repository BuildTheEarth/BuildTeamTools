package net.buildtheearth.modules.generator.model;

import lombok.Getter;
import net.buildtheearth.modules.generator.components.FlagType;
import net.buildtheearth.modules.generator.components.field.FieldFlag;
import net.buildtheearth.modules.generator.components.house.HouseFlag;
import net.buildtheearth.modules.generator.components.rail.RailFlag;
import net.buildtheearth.modules.generator.components.road.RoadFlag;
import net.buildtheearth.modules.generator.components.tree.TreeFlag;

public interface Flag {

    String DISABLED = "OFF";
    String ENABLED = "ON";

    String getFlag();

    FlagType getFlagType();

    /** @return the flag by the given string and generator type */
    static Flag byString(GeneratorType generatorType, String flag) {
        switch (generatorType) {
            case HOUSE:
                return HouseFlag.byString(flag);
            case ROAD:
                return RoadFlag.byString(flag);
            case TREE:
                return TreeFlag.byString(flag);
            case RAILWAY:
                return RailFlag.byString(flag);
            case FIELD:
                return FieldFlag.byString(flag);
        }
        return null;
    }
}

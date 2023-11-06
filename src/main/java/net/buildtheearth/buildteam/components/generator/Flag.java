package net.buildtheearth.buildteam.components.generator;

import net.buildtheearth.buildteam.components.generator.field.FieldFlag;
import net.buildtheearth.buildteam.components.generator.house.HouseFlag;
import net.buildtheearth.buildteam.components.generator.rail.RailFlag;
import net.buildtheearth.buildteam.components.generator.road.RoadFlag;
import net.buildtheearth.buildteam.components.generator.tree.TreeFlag;

public interface Flag {

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

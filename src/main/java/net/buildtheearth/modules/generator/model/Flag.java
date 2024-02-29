package net.buildtheearth.modules.generator.model;

import net.buildtheearth.modules.generator.components.house.HouseFlag;
import net.buildtheearth.modules.generator.components.rail.RailFlag;
import net.buildtheearth.modules.generator.components.road.RoadFlag;
import net.buildtheearth.modules.generator.components.tree.TreeFlag;

public interface Flag {

    /**
     * @return the flag by the given string and generator type
     */
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
        }
        return null;
    }

    String getFlag();
}

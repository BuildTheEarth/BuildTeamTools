package net.buildtheearth.buildteamtools.modules.generator.model;

import net.buildtheearth.buildteamtools.modules.generator.components.field.FieldFlag;
import net.buildtheearth.buildteamtools.modules.generator.components.house.HouseFlag;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.RailFlag;
import net.buildtheearth.buildteamtools.modules.generator.components.road.RoadFlag;
import net.buildtheearth.buildteamtools.modules.generator.components.tree.TreeFlag;
import org.jspecify.annotations.NonNull;

public interface Flag {

    String getFlag();

    FlagType getFlagType();

    /** @return the flag by the given string and generator type */
    static Flag byString(@NonNull GeneratorType generatorType, String flag) {
        return switch (generatorType) {
            case HOUSE -> HouseFlag.byString(flag);
            case ROAD -> RoadFlag.byString(flag);
            case TREE -> TreeFlag.byString(flag);
            case RAILWAY -> RailFlag.byString(flag);
            case FIELD -> FieldFlag.byString(flag);
        };
    }
}

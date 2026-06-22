package net.buildtheearth.buildteamtools.modules.generator.components.rail;

import com.sk89q.worldedit.util.Direction;

import java.util.LinkedHashMap;
import java.util.Map;

final class RailSideBlock {

    private final PositionKey key;
    private final Direction defaultFacing;
    private final Map<Direction, Integer> facingScores = new LinkedHashMap<>();

    RailSideBlock(PositionKey key, Direction defaultFacing) {
        this.key = key;
        this.defaultFacing = defaultFacing;
    }

    PositionKey key() {
        return key;
    }

    void addFacing(Direction facing) {
        facingScores.merge(facing, 1, Integer::sum);
    }

    Direction getPreferredFacing() {
        Direction preferredFacing = defaultFacing;
        int preferredScore = -1;

        for (Map.Entry<Direction, Integer> entry : facingScores.entrySet()) {
            if (entry.getValue() > preferredScore) {
                preferredFacing = entry.getKey();
                preferredScore = entry.getValue();
            }
        }

        return preferredFacing;
    }
}

package net.buildtheearth.buildteamtools.modules.generator.components.rail.side;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.util.Vector;

import java.util.Map;

public class RailOrientationResolver {

    public Vector resolveDirection(RailSideBlock sideBlock, Map<BlockVector3, RailSideBlock> sideBlocks) {
        BlockVector3 position = sideBlock.getPosition();

        boolean east = sideBlocks.containsKey(position.add(1, 0, 0));
        boolean west = sideBlocks.containsKey(position.add(-1, 0, 0));
        boolean south = sideBlocks.containsKey(position.add(0, 0, 1));
        boolean north = sideBlocks.containsKey(position.add(0, 0, -1));

        boolean eastWest = east || west;
        boolean northSouth = north || south;

        if (eastWest && !northSouth)
            return getHorizontalDirection(east, west);

        if (northSouth && !eastWest)
            return getVerticalDirection(south, north);

        if (eastWest && northSouth)
            return resolveCornerDirection(sideBlock, east, west, south, north);

        return sideBlock.getAverageDirection();
    }

    private Vector getHorizontalDirection(boolean east, boolean west) {
        if (east && !west)
            return new Vector(1, 0, 0);

        if (west && !east)
            return new Vector(-1, 0, 0);

        return new Vector(1, 0, 0);
    }

    private Vector getVerticalDirection(boolean south, boolean north) {
        if (south && !north)
            return new Vector(0, 0, 1);

        if (north && !south)
            return new Vector(0, 0, -1);

        return new Vector(0, 0, 1);
    }

    private Vector resolveCornerDirection(
            RailSideBlock sideBlock,
            boolean east,
            boolean west,
            boolean south,
            boolean north
    ) {
        Vector average = sideBlock.getAverageDirection();

        int avgX = average.getBlockX();
        int avgZ = average.getBlockZ();

        if (Math.abs(avgX) > Math.abs(avgZ)) {
            if (avgX > 0 && east)
                return new Vector(1, 0, 0);

            if (avgX < 0 && west)
                return new Vector(-1, 0, 0);
        }

        if (Math.abs(avgZ) > Math.abs(avgX)) {
            if (avgZ > 0 && south)
                return new Vector(0, 0, 1);

            if (avgZ < 0 && north)
                return new Vector(0, 0, -1);
        }

        if (east)
            return new Vector(1, 0, 0);

        if (west)
            return new Vector(-1, 0, 0);

        if (south)
            return new Vector(0, 0, 1);

        if (north)
            return new Vector(0, 0, -1);

        return average;
    }
}
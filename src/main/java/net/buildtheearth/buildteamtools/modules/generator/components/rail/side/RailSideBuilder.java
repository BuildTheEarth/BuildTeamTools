package net.buildtheearth.buildteamtools.modules.generator.components.rail.side;

import com.sk89q.worldedit.math.BlockVector3;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.path.RailPath;
import org.bukkit.util.Vector;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class RailSideBuilder {

    public Map<BlockVector3, RailSideBlock> buildSideBlocks(RailPath railPath) {
        Map<BlockVector3, RailSideBlock> sideBlocks = new LinkedHashMap<>();
        LinkedHashSet<BlockVector3> centerPositions = getCenterPositions(railPath);

        List<Vector> centerPath = railPath.getCenterPath();

        for (int i = 0; i < centerPath.size() - 1; i++) {
            Vector from = centerPath.get(i);
            Vector to = centerPath.get(i + 1);
            Vector direction = getDirection(from, to);

            if (direction == null)
                continue;

            int dx = direction.getBlockX();
            int dz = direction.getBlockZ();

            if (dx != 0 && dz != 0) {
                placeDiagonalEdgeSideBlocks(sideBlocks, centerPositions, from, dx, dz);
            } else {
                placeStraightEdgeSideBlocks(sideBlocks, centerPositions, from, to, direction);
            }
        }

        removeFloatingSideBlocks(sideBlocks);

        return sideBlocks;
    }

    public LinkedHashSet<BlockVector3> getCenterPositions(RailPath railPath) {
        LinkedHashSet<BlockVector3> centerPositions = new LinkedHashSet<>();

        for (Vector center : railPath.getCenterPath())
            centerPositions.add(toBlockVector3(center));

        return centerPositions;
    }

    private void placeStraightEdgeSideBlocks(
            Map<BlockVector3, RailSideBlock> sideBlocks,
            LinkedHashSet<BlockVector3> centerPositions,
            Vector from,
            Vector to,
            Vector direction
    ) {
        Vector leftOffset = getLeftOffset(direction);
        Vector rightOffset = getRightOffset(direction);

        addSideBlock(sideBlocks, centerPositions, offset(from, leftOffset), direction);
        addSideBlock(sideBlocks, centerPositions, offset(to, leftOffset), direction);
        addSideBlock(sideBlocks, centerPositions, offset(from, rightOffset), direction);
        addSideBlock(sideBlocks, centerPositions, offset(to, rightOffset), direction);
    }

    private void placeDiagonalEdgeSideBlocks(
            Map<BlockVector3, RailSideBlock> sideBlocks,
            LinkedHashSet<BlockVector3> centerPositions,
            Vector from,
            int dx,
            int dz
    ) {
        Vector direction = new Vector(dx, 0, dz);

        BlockVector3 horizontalSide = BlockVector3.at(
                from.getBlockX() + dx,
                from.getBlockY(),
                from.getBlockZ()
        );

        BlockVector3 verticalSide = BlockVector3.at(
                from.getBlockX(),
                from.getBlockY(),
                from.getBlockZ() + dz
        );

        addSideBlock(sideBlocks, centerPositions, horizontalSide, direction);
        addSideBlock(sideBlocks, centerPositions, verticalSide, direction);
    }

    private void addSideBlock(
            Map<BlockVector3, RailSideBlock> sideBlocks,
            LinkedHashSet<BlockVector3> centerPositions,
            BlockVector3 position,
            Vector direction
    ) {
        if (centerPositions.contains(position))
            return;

        RailSideBlock sideBlock = sideBlocks.computeIfAbsent(position, RailSideBlock::new);
        sideBlock.addDirection(direction);
    }

    private void removeFloatingSideBlocks(Map<BlockVector3, RailSideBlock> sideBlocks) {
        if (sideBlocks.size() <= 2)
            return;

        sideBlocks.entrySet().removeIf(entry -> countSideNeighbours(entry.getKey(), sideBlocks) == 0);
    }

    private int countSideNeighbours(BlockVector3 position, Map<BlockVector3, RailSideBlock> sideBlocks) {
        int count = 0;

        if (sideBlocks.containsKey(position.add(1, 0, 0)))
            count++;

        if (sideBlocks.containsKey(position.add(-1, 0, 0)))
            count++;

        if (sideBlocks.containsKey(position.add(0, 0, 1)))
            count++;

        if (sideBlocks.containsKey(position.add(0, 0, -1)))
            count++;

        if (sideBlocks.containsKey(position.add(1, 0, 1)))
            count++;

        if (sideBlocks.containsKey(position.add(1, 0, -1)))
            count++;

        if (sideBlocks.containsKey(position.add(-1, 0, 1)))
            count++;

        if (sideBlocks.containsKey(position.add(-1, 0, -1)))
            count++;

        return count;
    }

    private Vector getLeftOffset(Vector direction) {
        int dx = direction.getBlockX();
        int dz = direction.getBlockZ();

        return new Vector(-dz, 0, dx);
    }

    private Vector getRightOffset(Vector direction) {
        int dx = direction.getBlockX();
        int dz = direction.getBlockZ();

        return new Vector(dz, 0, -dx);
    }

    private BlockVector3 offset(Vector center, Vector offset) {
        return BlockVector3.at(
                center.getBlockX() + offset.getBlockX(),
                center.getBlockY(),
                center.getBlockZ() + offset.getBlockZ()
        );
    }

    private Vector getDirection(Vector from, Vector to) {
        int dx = Integer.compare(to.getBlockX() - from.getBlockX(), 0);
        int dz = Integer.compare(to.getBlockZ() - from.getBlockZ(), 0);

        if (dx == 0 && dz == 0)
            return null;

        return new Vector(dx, 0, dz);
    }

    private BlockVector3 toBlockVector3(Vector vector) {
        return BlockVector3.at(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }
}
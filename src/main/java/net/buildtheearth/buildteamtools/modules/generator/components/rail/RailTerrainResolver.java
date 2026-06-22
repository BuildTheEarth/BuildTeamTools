package net.buildtheearth.buildteamtools.modules.generator.components.rail;

import net.buildtheearth.buildteamtools.utils.MenuItems;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class RailTerrainResolver {

    private static final int SURFACE_Y_OFFSET = 1;

    private final Map<PositionKey, Block> blocksByPosition = new HashMap<>();
    private final Map<RailColumnKey, List<Block>> blocksByColumn = new HashMap<>();
    private final Map<PositionKey, Integer> railYCache = new HashMap<>();

    RailTerrainResolver(Block[][][] blocks) {
        indexPreparedBlocks(blocks);
    }

    void snapMissingHeightsToTerrain(List<Vector> points, int referenceY) {
        if (!hasAnyMissingHeights(points))
            return;

        for (Vector point : points)
            if (point.getBlockY() == 0)
                point.setY(getNearestRailSurfaceY(point.getBlockX(), point.getBlockZ(), referenceY));
    }

    void adjustPathToTerrain(List<Vector> path) {
        for (Vector point : path)
            point.setY(getNearestRailSurfaceY(point.getBlockX(), point.getBlockZ(), point.getBlockY()));
    }

    int getNearestRailSurfaceY(int x, int z, int fallbackY) {
        PositionKey cacheKey = PositionKey.of(x, fallbackY, z);
        return railYCache.computeIfAbsent(cacheKey, ignored -> findNearestRailSurfaceY(x, z, fallbackY));
    }

    static boolean hasAnyMissingHeights(List<Vector> points) {
        for (Vector point : points)
            if (point.getBlockY() == 0) return true;

        return false;
    }

    private void indexPreparedBlocks(Block[][][] blocks) {
        if (blocks == null)
            return;

        for (Block[][] block2D : blocks) {
            for (Block[] block1D : block2D) {
                for (Block block : block1D) {
                    if (block == null)
                        continue;

                    blocksByPosition.put(PositionKey.of(block.getX(), block.getY(), block.getZ()), block);
                    blocksByColumn
                            .computeIfAbsent(RailColumnKey.of(block.getX(), block.getZ()), ignored -> new ArrayList<>())
                            .add(block);
                }
            }
        }

        for (List<Block> columnBlocks : blocksByColumn.values())
            columnBlocks.sort(Comparator.comparingInt(Block::getY));
    }

    private int findNearestRailSurfaceY(int x, int z, int fallbackY) {
        List<Block> columnBlocks = blocksByColumn.get(RailColumnKey.of(x, z));

        if (columnBlocks == null || columnBlocks.isEmpty())
            return fallbackY;

        int nearestRailY = fallbackY;
        int nearestDistance = Integer.MAX_VALUE;
        int highestRailY = Integer.MIN_VALUE;

        for (Block block : columnBlocks) {
            if (!isRailGroundBlock(block))
                continue;

            int railY = block.getY() + SURFACE_Y_OFFSET;
            int distance = Math.abs(railY - fallbackY);
            highestRailY = Math.max(highestRailY, railY);

            if (distance < nearestDistance || distance == nearestDistance && railY <= fallbackY) {
                nearestRailY = railY;
                nearestDistance = distance;
            }
        }

        if (nearestDistance != Integer.MAX_VALUE)
            return nearestRailY;

        return highestRailY == Integer.MIN_VALUE ? fallbackY : highestRailY;
    }

    private boolean isRailGroundBlock(Block block) {
        if (block.isLiquid() || !block.getType().isSolid())
            return false;

        for (Material ignoredMaterial : MenuItems.getIgnoredMaterials())
            if (block.getType() == ignoredMaterial)
                return false;

        return isRailPlacementOpen(block.getX(), block.getY() + SURFACE_Y_OFFSET, block.getZ());
    }

    private boolean isRailPlacementOpen(int x, int y, int z) {
        Block block = blocksByPosition.get(PositionKey.of(x, y, z));

        if (block == null)
            return true;

        if (block.isLiquid() || block.getType().isAir() || !block.getType().isSolid())
            return true;

        for (Material ignoredMaterial : MenuItems.getIgnoredMaterials())
            if (block.getType() == ignoredMaterial)
                return true;

        return false;
    }
}

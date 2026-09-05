package net.buildtheearth.buildteamtools.modules.generator.components.rail.generation;

import com.alpsbte.alpslib.utils.GeneratorUtils;
import com.alpsbte.alpslib.utils.item.Item;
import com.cryptomorin.xseries.XMaterial;
import com.fastasyncworldedit.core.registry.state.PropertyKey;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.configuration.RailType;
import org.bukkit.util.Vector;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class RailBlockBuilder {

    private static final Direction DEFAULT_FACING = Direction.EAST;
    private static final String FACING_PROPERTY = "facing";
    private static final String SHAPE_PROPERTY = "shape";
    private static final int FACING_SMOOTHING_RADIUS = 3;
    // Sleeper ends stick out one block beyond the rails, which sit at offset 1 from the track center.
    private static final int SLEEPER_SIDE_OFFSET = 2;

    private final RailTerrainResolver terrainResolver;
    private final RailType railType;
    private final BlockType railBlockType;
    private final RailPreparationProgress preparationProgress;
    private final long terrainAdjustedPercentage;
    private final long buildFinishedPercentage;

    RailBlockBuilder(
            RailTerrainResolver terrainResolver,
            RailType railType,
            RailPreparationProgress preparationProgress,
            long terrainAdjustedPercentage,
            long buildFinishedPercentage
    ) {
        this.terrainResolver = terrainResolver;
        this.railType = railType;
        this.railBlockType = getRailBlockType(railType);
        this.preparationProgress = preparationProgress;
        this.terrainAdjustedPercentage = terrainAdjustedPercentage;
        this.buildFinishedPercentage = buildFinishedPercentage;
    }

    Map<PositionKey, BlockState> build(List<List<Vector>> railCenterPaths) {
        Map<PositionKey, BlockState> railBlocks = new LinkedHashMap<>();
        Set<PositionKey> centerPositions = getCenterPositions(railCenterPaths);
        Map<PositionKey, RailSideBlock> sideBlocks = new LinkedHashMap<>();
        int totalPathPoints = getTotalPathPointCount(railCenterPaths);
        int processedPathPoints = 0;

        for (List<Vector> railCenterPath : railCenterPaths) {
            for (int index = 0; index < railCenterPath.size(); index++) {
                Vector center = railCenterPath.get(index);
                Direction facing = getSmoothedFacing(railCenterPath, index);

                for (RailSidePlacement sidePlacement : getSidePlacements(railCenterPath, index, facing))
                    addSideBlock(sideBlocks, center, sidePlacement, centerPositions);

                processedPathPoints++;
                preparationProgress.update(preparationProgress.scale(processedPathPoints, totalPathPoints, terrainAdjustedPercentage, 86L));
            }
        }

        int processedSideBlocks = 0;

        for (RailSideBlock sideBlock : sideBlocks.values()) {
            railBlocks.put(sideBlock.key(), createRailBlockState(sideBlock, sideBlocks));
            processedSideBlocks++;
            preparationProgress.update(preparationProgress.scale(processedSideBlocks, sideBlocks.size(), 86L, 89L));
        }

        if (railType.hasSleepers())
            for (List<Vector> railCenterPath : railCenterPaths)
                addSleeperBlocks(railBlocks, railCenterPath, centerPositions);

        int processedCenterPoints = 0;

        for (List<Vector> railCenterPath : railCenterPaths) {
            for (int index = 0; index < railCenterPath.size(); index++) {
                Vector center = railCenterPath.get(index);
                railBlocks.put(PositionKey.from(center), createCenterBlockState(center, isSleeperPoint(index)));
                processedCenterPoints++;
                preparationProgress.update(preparationProgress.scale(processedCenterPoints, totalPathPoints, 89L, buildFinishedPercentage));
            }
        }

        return railBlocks;
    }

    private boolean isSleeperPoint(int index) {
        return railType.hasSleepers() && index % railType.getSleeperSpacing() == 0;
    }

    private void addSleeperBlocks(Map<PositionKey, BlockState> railBlocks, List<Vector> path, Set<PositionKey> centerPositions) {
        BlockState sleeperBlockState = GeneratorUtils.getBlockState(railType.getSleeperBlock());

        for (int index = 0; index < path.size(); index++) {
            if (!isSleeperPoint(index))
                continue;

            Vector center = path.get(index);
            RailStep step = getRailStep(path, index, new RailStep(1, 0));
            RailStep perpendicularStep = new RailStep(-step.dz(), step.dx());

            addSleeperBlock(railBlocks, center, perpendicularStep, SLEEPER_SIDE_OFFSET, sleeperBlockState, centerPositions);
            addSleeperBlock(railBlocks, center, perpendicularStep, -SLEEPER_SIDE_OFFSET, sleeperBlockState, centerPositions);
        }
    }

    private void addSleeperBlock(
            Map<PositionKey, BlockState> railBlocks,
            Vector center,
            RailStep perpendicularStep,
            int offset,
            BlockState sleeperBlockState,
            Set<PositionKey> centerPositions
    ) {
        if (perpendicularStep.dx() == 0 && perpendicularStep.dz() == 0)
            return;

        int x = center.getBlockX() + perpendicularStep.dx() * offset;
        int z = center.getBlockZ() + perpendicularStep.dz() * offset;
        int y = terrainResolver.getNearestRailSurfaceY(x, z, center.getBlockY());

        PositionKey key = PositionKey.of(x, y, z);

        // Rails and track centers take precedence over sleeper ends.
        if (centerPositions.contains(key) || railBlocks.containsKey(key))
            return;

        railBlocks.put(key, sleeperBlockState);
    }

    private int getTotalPathPointCount(List<List<Vector>> railCenterPaths) {
        int totalPathPoints = 0;

        for (List<Vector> railCenterPath : railCenterPaths)
            totalPathPoints += railCenterPath.size();

        return Math.max(1, totalPathPoints);
    }

    private static List<RailSidePlacement> getSidePlacements(List<Vector> path, int index, Direction facing) {
        List<RailSidePlacement> placements = new ArrayList<>();
        Vector center = path.get(index);
        RailStep previousStep = index > 0 ? getStep(path.get(index - 1), center) : null;
        RailStep nextStep = index < path.size() - 1 ? getStep(center, path.get(index + 1)) : null;

        addSidePlacements(placements, getRailStep(path, index, new RailStep(1, 0)), facing);

        if (previousStep != null)
            addSidePlacements(placements, previousStep, facing);

        if (nextStep != null)
            addSidePlacements(placements, nextStep, facing);

        return placements;
    }

    private static void addSidePlacements(List<RailSidePlacement> placements, RailStep step, Direction facing) {
        if (step.dx() != 0 && step.dz() != 0) {
            addSidePlacement(placements, new RailStep(step.dx(), 0), facing);
            addSidePlacement(placements, new RailStep(0, step.dz()), facing);
            return;
        }

        if (step.dx() != 0) {
            addSidePlacement(placements, new RailStep(0, 1), facing);
            addSidePlacement(placements, new RailStep(0, -1), facing);
            return;
        }

        addSidePlacement(placements, new RailStep(1, 0), facing);
        addSidePlacement(placements, new RailStep(-1, 0), facing);
    }

    private static Direction getSmoothedFacing(List<Vector> path, int index) {
        int fromIndex = Math.max(0, index - FACING_SMOOTHING_RADIUS);
        int toIndex = Math.min(path.size() - 1, index + FACING_SMOOTHING_RADIUS);
        Vector from = path.get(fromIndex);
        Vector to = path.get(toIndex);
        int dx = to.getBlockX() - from.getBlockX();
        int dz = to.getBlockZ() - from.getBlockZ();

        if (Math.abs(dx) == Math.abs(dz)) {
            Vector pathStart = path.getFirst();
            Vector pathEnd = path.getLast();
            int totalDx = pathEnd.getBlockX() - pathStart.getBlockX();
            int totalDz = pathEnd.getBlockZ() - pathStart.getBlockZ();

            if (Math.abs(totalDx) != Math.abs(totalDz)) {
                dx = totalDx;
                dz = totalDz;
            }
        }

        if (Math.abs(dx) >= Math.abs(dz) && dx != 0)
            return GeneratorUtils.getFacing(Integer.compare(dx, 0), 0, DEFAULT_FACING);

        if (dz != 0)
            return GeneratorUtils.getFacing(0, Integer.compare(dz, 0), DEFAULT_FACING);

        return DEFAULT_FACING;
    }

    private static void addSidePlacement(List<RailSidePlacement> placements, RailStep offset, Direction facing) {
        for (RailSidePlacement placement : placements) {
            if (placement.offset().equals(offset)) return;
        }

        placements.add(new RailSidePlacement(offset, facing));
    }

    private void addSideBlock(
            Map<PositionKey, RailSideBlock> sideBlocks,
            Vector center,
            RailSidePlacement sidePlacement,
            Set<PositionKey> centerPositions
    ) {
        RailStep sideOffset = sidePlacement.offset();

        if (sideOffset.dx() == 0 && sideOffset.dz() == 0)
            return;

        int x = center.getBlockX() + sideOffset.dx();
        int z = center.getBlockZ() + sideOffset.dz();
        int y = terrainResolver.getNearestRailSurfaceY(x, z, center.getBlockY());

        PositionKey key = PositionKey.of(x, y, z);

        if (centerPositions.contains(key))
            return;

        sideBlocks
                .computeIfAbsent(key, ignored -> new RailSideBlock(key, DEFAULT_FACING))
                .addFacing(sidePlacement.facing());
    }

    private Direction resolveSideBlockFacing(RailSideBlock sideBlock, Map<PositionKey, RailSideBlock> sideBlocks) {
        RailConnections connections = getConnections(sideBlock.key(), sideBlocks);
        int xConnections = (connections.east() ? 1 : 0) + (connections.west() ? 1 : 0);
        int zConnections = (connections.south() ? 1 : 0) + (connections.north() ? 1 : 0);
        Direction preferredFacing = sideBlock.getPreferredFacing();

        if (xConnections > zConnections)
            return resolveAxisFacing(
                    preferredFacing,
                    Direction.EAST,
                    Direction.WEST,
                    connections.east(),
                    connections.west()
            );

        if (zConnections > xConnections)
            return resolveAxisFacing(
                    preferredFacing,
                    Direction.SOUTH,
                    Direction.NORTH,
                    connections.south(),
                    connections.north()
            );

        return preferredFacing;
    }

    private Direction resolveAxisFacing(
            Direction preferredFacing,
            Direction positiveFacing,
            Direction negativeFacing,
            boolean hasPositiveNeighbor,
            boolean hasNegativeNeighbor
    ) {
        if (preferredFacing == positiveFacing && hasPositiveNeighbor || preferredFacing == negativeFacing && hasNegativeNeighbor)
            return preferredFacing;

        if (hasPositiveNeighbor && !hasNegativeNeighbor)
            return positiveFacing;

        if (hasNegativeNeighbor && !hasPositiveNeighbor)
            return negativeFacing;

        return preferredFacing == negativeFacing ? negativeFacing : positiveFacing;
    }

    private Set<PositionKey> getCenterPositions(List<List<Vector>> railCenterPaths) {
        Set<PositionKey> centerPositions = new HashSet<>();

        for (List<Vector> railCenterPath : railCenterPaths)
            for (Vector center : railCenterPath)
                centerPositions.add(PositionKey.from(center));

        return centerPositions;
    }

    private static RailStep getRailStep(List<Vector> path, int index, RailStep fallbackStep) {
        RailStep previousStep = index > 0 ? getStep(path.get(index - 1), path.get(index)) : null;
        RailStep nextStep = index < path.size() - 1 ? getStep(path.get(index), path.get(index + 1)) : null;

        if (previousStep != null && nextStep != null) {
            int dx = Integer.compare(previousStep.dx() + nextStep.dx(), 0);
            int dz = Integer.compare(previousStep.dz() + nextStep.dz(), 0);

            if (dx != 0 || dz != 0) return new RailStep(dx, dz);
        }

        if (nextStep != null) return nextStep;

        if (previousStep != null) return previousStep;

        return fallbackStep;
    }

    private static RailStep getStep(Vector from, Vector to) {
        int dx = Integer.compare(to.getBlockX() - from.getBlockX(), 0);
        int dz = Integer.compare(to.getBlockZ() - from.getBlockZ(), 0);

        if (dx == 0 && dz == 0) return null;

        return new RailStep(dx, dz);
    }

    private BlockState createCenterBlockState(Vector position, boolean sleeperPoint) {
        if (sleeperPoint)
            return GeneratorUtils.getBlockState(railType.getSleeperBlock());

        List<XMaterial> blocksBelow = railType.getBlocksBelow();
        int index = Math.floorMod(
                position.getBlockX() * 31 + position.getBlockY() * 23 + position.getBlockZ() * 17,
                blocksBelow.size()
        );

        return GeneratorUtils.getBlockState(blocksBelow.get(index));
    }

    private BlockState createRailBlockState(
            RailSideBlock sideBlock,
            Map<PositionKey, RailSideBlock> sideBlocks
    ) {
        Direction preferredDirection = sideBlock.getPreferredFacing();

        if (railBlockType.getPropertyMap().containsKey(SHAPE_PROPERTY))
            return createRailShapeBlockState(
                    resolveSideBlockFacing(sideBlock, sideBlocks),
                    getConnections(sideBlock.key(), sideBlocks)
            );

        if (!railBlockType.getPropertyMap().containsKey(FACING_PROPERTY))
            return railBlockType.getDefaultState();

        return GeneratorUtils.getBlockStateWithFacing(railBlockType, preferredDirection);
    }

    private BlockState createRailShapeBlockState(Direction preferredDirection, RailConnections connections) {
        String shape = resolveRailShape(preferredDirection, connections);

        try {
            return railBlockType.getDefaultState().with(
                    PropertyKey.SHAPE,
                    shape
            );
        } catch (IllegalArgumentException exception) {
            return railBlockType.getDefaultState().with(
                    PropertyKey.SHAPE,
                    getStraightRailShape(preferredDirection)
            );
        }
    }

    private String resolveRailShape(Direction preferredDirection, RailConnections connections) {
        String ascendingShape = getAscendingRailShape(connections);

        if (ascendingShape != null)
            return ascendingShape;

        String cornerShape = getCornerRailShape(connections);

        if (cornerShape != null)
            return cornerShape;

        return getAxisRailShape(preferredDirection, connections);
    }

    private @Nullable String getAscendingRailShape(RailConnections connections) {
        if (isRaised(connections.eastHeightDifference()))
            return "ascending_east";

        if (isRaised(connections.westHeightDifference()))
            return "ascending_west";

        if (isRaised(connections.southHeightDifference()))
            return "ascending_south";

        if (isRaised(connections.northHeightDifference()))
            return "ascending_north";

        return null;
    }

    private @Nullable String getCornerRailShape(RailConnections connections) {
        if (connections.connectionCount() != 2)
            return null;

        if (connections.north() && connections.east())
            return "north_east";

        if (connections.north() && connections.west())
            return "north_west";

        if (connections.south() && connections.east())
            return "south_east";

        return connections.south() && connections.west() ? "south_west" : null;
    }

    private String getAxisRailShape(Direction preferredDirection, RailConnections connections) {
        int xConnections = (connections.east() ? 1 : 0) + (connections.west() ? 1 : 0);
        int zConnections = (connections.south() ? 1 : 0) + (connections.north() ? 1 : 0);

        if (xConnections > zConnections)
            return "east_west";

        if (zConnections > xConnections)
            return "north_south";

        return getStraightRailShape(preferredDirection);
    }

    private String getStraightRailShape(Direction direction) {
        return direction == Direction.NORTH || direction == Direction.SOUTH ? "north_south" : "east_west";
    }

    private boolean isRaised(@Nullable Integer heightDifference) {
        return heightDifference != null && heightDifference > 0;
    }

    private RailConnections getConnections(
            PositionKey key,
            Map<PositionKey, RailSideBlock> sideBlocks
    ) {
        return new RailConnections(
                getNeighborHeightDifference(key, 1, 0, sideBlocks),
                getNeighborHeightDifference(key, -1, 0, sideBlocks),
                getNeighborHeightDifference(key, 0, 1, sideBlocks),
                getNeighborHeightDifference(key, 0, -1, sideBlocks)
        );
    }

    private @Nullable Integer getNeighborHeightDifference(
            PositionKey key,
            int xOffset,
            int zOffset,
            Map<PositionKey, RailSideBlock> sideBlocks
    ) {
        for (int heightDifference = 0; heightDifference <= 1; heightDifference++) {
            if (sideBlocks.containsKey(PositionKey.of(
                    key.x() + xOffset,
                    key.y() + heightDifference,
                    key.z() + zOffset
            )))
                return heightDifference;

            if (heightDifference > 0 && sideBlocks.containsKey(PositionKey.of(
                    key.x() + xOffset,
                    key.y() - heightDifference,
                    key.z() + zOffset
            )))
                return -heightDifference;
        }

        return null;
    }

    private BlockType getRailBlockType(RailType railType) {
        BlockType blockType = Item.convertXMaterialToWEBlockType(railType.getRailBlock());

        return blockType == null ? BlockTypes.ANVIL : blockType;
    }
}

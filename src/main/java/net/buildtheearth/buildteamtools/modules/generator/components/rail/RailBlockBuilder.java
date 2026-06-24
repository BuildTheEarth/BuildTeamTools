package net.buildtheearth.buildteamtools.modules.generator.components.rail;

import com.alpsbte.alpslib.utils.GeneratorUtils;
import com.cryptomorin.xseries.XMaterial;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class RailBlockBuilder {

    private static final Direction DEFAULT_FACING = Direction.EAST;
    private static final XMaterial[] CENTER_MATERIALS = new XMaterial[]{
            XMaterial.DEAD_FIRE_CORAL_BLOCK,
            XMaterial.STONE,
            XMaterial.COBBLESTONE
    };

    private final List<Vector> controlPoints;
    private final RailTerrainResolver terrainResolver;
    private final RailType railType;
    private final RailPreparationProgress preparationProgress;
    private final int railLaneCount;
    private final int railLaneSpacing;
    private final long terrainAdjustedPercentage;
    private final long buildFinishedPercentage;

    RailBlockBuilder(
            List<Vector> controlPoints,
            RailTerrainResolver terrainResolver,
            RailType railType,
            RailPreparationProgress preparationProgress,
            int railLaneCount,
            int railLaneSpacing,
            long terrainAdjustedPercentage,
            long buildFinishedPercentage
    ) {
        this.controlPoints = controlPoints;
        this.terrainResolver = terrainResolver;
        this.railType = railType;
        this.preparationProgress = preparationProgress;
        this.railLaneCount = railLaneCount;
        this.railLaneSpacing = railLaneSpacing;
        this.terrainAdjustedPercentage = terrainAdjustedPercentage;
        this.buildFinishedPercentage = buildFinishedPercentage;
    }

    Map<PositionKey, BlockState> build(List<Vector> path) {
        Map<PositionKey, BlockState> railBlocks = new LinkedHashMap<>();
        List<List<Vector>> railCenterPaths = new RailLanePathBuilder(controlPoints, terrainResolver, railLaneCount, railLaneSpacing)
                .createRailCenterPaths(path);
        Set<PositionKey> centerPositions = getCenterPositions(railCenterPaths);
        Map<PositionKey, RailSideBlock> sideBlocks = new LinkedHashMap<>();
        int totalPathPoints = getTotalPathPointCount(railCenterPaths);
        int processedPathPoints = 0;

        for (List<Vector> railCenterPath : railCenterPaths) {
            for (int index = 0; index < railCenterPath.size(); index++) {
                Vector center = railCenterPath.get(index);

                for (RailSidePlacement sidePlacement : getSidePlacements(railCenterPath, index))
                    addSideBlock(sideBlocks, center, sidePlacement, centerPositions);

                processedPathPoints++;
                preparationProgress.update(preparationProgress.scale(processedPathPoints, totalPathPoints, terrainAdjustedPercentage, 86L));
            }
        }

        int processedSideBlocks = 0;

        for (RailSideBlock sideBlock : sideBlocks.values()) {
            railBlocks.put(sideBlock.key(), createAnvilBlockState(resolveSideBlockFacing(sideBlock, sideBlocks)));
            processedSideBlocks++;
            preparationProgress.update(preparationProgress.scale(processedSideBlocks, sideBlocks.size(), 86L, 89L));
        }

        int processedCenterPoints = 0;

        for (List<Vector> railCenterPath : railCenterPaths) {
            for (Vector center : railCenterPath) {
                railBlocks.put(PositionKey.from(center), createCenterBlockState(center));
                processedCenterPoints++;
                preparationProgress.update(preparationProgress.scale(processedCenterPoints, totalPathPoints, 89L, buildFinishedPercentage));
            }
        }

        return railBlocks;
    }

    private int getTotalPathPointCount(List<List<Vector>> railCenterPaths) {
        int totalPathPoints = 0;

        for (List<Vector> railCenterPath : railCenterPaths)
            totalPathPoints += railCenterPath.size();

        return Math.max(1, totalPathPoints);
    }

    private List<RailSidePlacement> getSidePlacements(List<Vector> path, int index) {
        List<RailSidePlacement> placements = new ArrayList<>();
        Vector center = path.get(index);
        RailStep previousStep = index > 0 ? getStep(path.get(index - 1), center) : null;
        RailStep nextStep = index < path.size() - 1 ? getStep(center, path.get(index + 1)) : null;

        addSidePlacements(placements, getRailStep(path, index, new RailStep(1, 0)));

        if (previousStep != null)
            addSidePlacements(placements, previousStep);

        if (nextStep != null)
            addSidePlacements(placements, nextStep);

        return placements;
    }

    private void addSidePlacements(List<RailSidePlacement> placements, RailStep step) {
        if (step.dx() != 0 && step.dz() != 0) {
            addSidePlacement(placements, new RailStep(step.dx(), 0), GeneratorUtils.getFacing(0, step.dz(), DEFAULT_FACING));
            addSidePlacement(placements, new RailStep(0, step.dz()), GeneratorUtils.getFacing(step.dx(), 0, DEFAULT_FACING));
            return;
        }

        if (step.dx() != 0) {
            Direction facing = GeneratorUtils.getFacing(step.dx(), 0, DEFAULT_FACING);
            addSidePlacement(placements, new RailStep(0, 1), facing);
            addSidePlacement(placements, new RailStep(0, -1), facing);
            return;
        }

        Direction facing = GeneratorUtils.getFacing(0, step.dz(), DEFAULT_FACING);
        addSidePlacement(placements, new RailStep(1, 0), facing);
        addSidePlacement(placements, new RailStep(-1, 0), facing);
    }

    private void addSidePlacement(List<RailSidePlacement> placements, RailStep offset, Direction facing) {
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
        PositionKey key = sideBlock.key();
        boolean east = sideBlocks.containsKey(PositionKey.of(key.x() + 1, key.y(), key.z()));
        boolean west = sideBlocks.containsKey(PositionKey.of(key.x() - 1, key.y(), key.z()));
        boolean south = sideBlocks.containsKey(PositionKey.of(key.x(), key.y(), key.z() + 1));
        boolean north = sideBlocks.containsKey(PositionKey.of(key.x(), key.y(), key.z() - 1));
        int xConnections = (east ? 1 : 0) + (west ? 1 : 0);
        int zConnections = (south ? 1 : 0) + (north ? 1 : 0);
        Direction preferredFacing = sideBlock.getPreferredFacing();

        if (xConnections > zConnections)
            return resolveAxisFacing(preferredFacing, Direction.EAST, Direction.WEST, east, west);

        if (zConnections > xConnections)
            return resolveAxisFacing(preferredFacing, Direction.SOUTH, Direction.NORTH, south, north);

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

    private RailStep getRailStep(List<Vector> path, int index, RailStep fallbackStep) {
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

    private RailStep getStep(Vector from, Vector to) {
        int dx = Integer.compare(to.getBlockX() - from.getBlockX(), 0);
        int dz = Integer.compare(to.getBlockZ() - from.getBlockZ(), 0);

        if (dx == 0 && dz == 0) return null;

        return new RailStep(dx, dz);
    }

    private BlockState createCenterBlockState(Vector position) {
        return switch (railType) {
            case STANDARD -> createStandardCenterBlockState(position);
        };
    }

    private BlockState createStandardCenterBlockState(Vector position) {
        int index = Math.floorMod(
                position.getBlockX() * 31 + position.getBlockY() * 23 + position.getBlockZ() * 17,
                CENTER_MATERIALS.length
        );

        return GeneratorUtils.getBlockState(CENTER_MATERIALS[index]);
    }

    private BlockState createAnvilBlockState(Direction direction) {
        return GeneratorUtils.getBlockStateWithFacing(BlockTypes.ANVIL, direction);
    }
}

package net.buildtheearth.buildteamtools.modules.generator.components.rail.generation;

import com.alpsbte.alpslib.utils.GeneratorUtils;
import com.alpsbte.alpslib.utils.item.Item;
import com.fastasyncworldedit.core.registry.state.PropertyKey;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.configuration.RailType;
import org.bukkit.util.Vector;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class RailOverheadBuilder {

    private static final Direction DEFAULT_FACING = Direction.EAST;
    private static final String FACING_PROPERTY = "facing";
    private static final int MAX_POLE_COLLISION_SHIFTS = RailType.MAX_TRACK_COUNT * RailType.MAX_TRACK_SPACING;

    private final RailTerrainResolver terrainResolver;
    private final RailType railType;

    RailOverheadBuilder(RailTerrainResolver terrainResolver, RailType railType) {
        this.terrainResolver = terrainResolver;
        this.railType = railType;
    }

    void addTo(Map<PositionKey, BlockState> blocks, List<List<Vector>> railCenterPaths) {
        int overheadY = getOverheadY(railCenterPaths);
        Set<HorizontalPosition> trackFootprint = getHorizontalFootprint(blocks.keySet());

        if (railType.hasOverheadWires())
            addWires(blocks, railCenterPaths, overheadY);

        if (!railType.hasOverheadPoles() || railCenterPaths.isEmpty())
            return;

        addPortals(
                blocks,
                railCenterPaths.getFirst(),
                railCenterPaths.getLast(),
                overheadY,
                trackFootprint
        );
    }

    private Set<HorizontalPosition> getHorizontalFootprint(Set<PositionKey> positions) {
        Set<HorizontalPosition> footprint = new HashSet<>();

        for (PositionKey position : positions)
            footprint.add(new HorizontalPosition(position.x(), position.z()));

        return footprint;
    }

    private int getOverheadY(List<List<Vector>> railCenterPaths) {
        int maxRailY = Integer.MIN_VALUE;

        for (List<Vector> path : railCenterPaths)
            for (Vector point : path)
                maxRailY = Math.max(maxRailY, point.getBlockY());

        return (maxRailY == Integer.MIN_VALUE ? 0 : maxRailY) + railType.getOverheadPoleHeight();
    }

    private void addWires(Map<PositionKey, BlockState> blocks, List<List<Vector>> railCenterPaths, int overheadY) {
        List<BlockType> wireBlockTypes = railType.getOverheadWireBlocks().stream().map(Item::convertXMaterialToWEBlockType).toList();

        for (List<Vector> path : railCenterPaths) {
            List<Vector> wirePath = createOverheadPath(path, overheadY);
            addPathBlocks(blocks, wirePath, wireBlockTypes, false);
        }
    }

    private void addPortals(
            Map<PositionKey, BlockState> blocks,
            List<Vector> leftPath,
            List<Vector> rightPath,
            int overheadY,
            Set<HorizontalPosition> trackFootprint
    ) {
        List<BlockType> supportBlockTypes = railType.getOverheadSupportBlocks().stream().map(Item::convertXMaterialToWEBlockType).toList();

        for (int index = 0; index < leftPath.size(); index += railType.getOverheadPoleSpacing()) {
            Vector leftCenter = leftPath.get(index);
            int rightIndex = getProportionalIndex(index, leftPath.size(), rightPath.size());
            Vector rightCenter = rightPath.get(rightIndex);
            PortalPole leftPole = createPole(leftPath, index, leftCenter, -1, trackFootprint);
            PortalPole rightPole = createPole(rightPath, rightIndex, rightCenter, 1, trackFootprint);

            if (leftPole == null || rightPole == null)
                continue;

            addPole(blocks, leftPole.x(), leftPole.z(), leftPole.surfaceY(), overheadY);
            addPole(blocks, rightPole.x(), rightPole.z(), rightPole.surfaceY(), overheadY);
            addPortalSupport(blocks, leftPole.x(), overheadY + 1, leftPole.z(), rightPole.x(), rightPole.z(), supportBlockTypes);
        }
    }

    private int getProportionalIndex(int sourceIndex, int sourceSize, int targetSize) {
        if (sourceSize <= 1 || targetSize <= 1)
            return 0;

        double progress = sourceIndex / (double) (sourceSize - 1);
        return Math.clamp((int) Math.round(progress * (targetSize - 1)), 0, targetSize - 1);
    }

    private @Nullable PortalPole createPole(
            List<Vector> path,
            int index,
            Vector center,
            int sideSign,
            Set<HorizontalPosition> trackFootprint
    ) {
        RailStep perpendicular = getPerpendicularStep(path, index, sideSign);
        int configuredDistance = railType.getOverheadPoleOffset();

        for (int shift = 0; shift <= MAX_POLE_COLLISION_SHIFTS; shift++) {
            int distance = configuredDistance + shift;
            int poleX = center.getBlockX() + perpendicular.dx() * distance;
            int poleZ = center.getBlockZ() + perpendicular.dz() * distance;

            if (trackFootprint.contains(new HorizontalPosition(poleX, poleZ)))
                continue;

            int surfaceY = terrainResolver.getNearestRailSurfaceY(poleX, poleZ, center.getBlockY());
            return new PortalPole(poleX, poleZ, surfaceY);
        }

        return null;
    }

    private void addPole(
            Map<PositionKey, BlockState> blocks,
            int poleX,
            int poleZ,
            int surfaceY,
            int topY
    ) {
        for (int y = Math.min(surfaceY, topY); y <= topY; y++) {
            PositionKey position = PositionKey.of(poleX, y, poleZ);
            blocks.putIfAbsent(position, GeneratorUtils.getBlockState(
                    RailMaterialPalette.select(railType.getOverheadPoleBlocks(), position)));
        }
    }

    private void addPortalSupport(
            Map<PositionKey, BlockState> blocks,
            int startX,
            int topY,
            int startZ,
            int endX,
            int endZ,
            List<BlockType> supportBlockTypes
    ) {
        Vector start = new Vector(startX, topY, startZ);
        Vector end = new Vector(endX, topY, endZ);
        addPathBlocks(blocks, createOrthogonalPath(start, end), supportBlockTypes, true);
    }

    private RailStep getPerpendicularStep(List<Vector> path, int index, int sideSign) {
        RailStep direction = getStep(path, index);
        return new RailStep(-direction.dz() * sideSign, direction.dx() * sideSign);
    }

    private List<Vector> createOverheadPath(List<Vector> path, int overheadY) {
        List<Vector> overheadPoints = path.stream()
                .map(point -> new Vector(point.getBlockX(), overheadY, point.getBlockZ()))
                .toList();

        return GeneratorUtils.createShortestBlockPath(overheadPoints);
    }

    private List<Vector> createOrthogonalPath(Vector start, Vector end) {
        return GeneratorUtils.createShortestBlockPath(List.of(start, end));
    }

    private void addPathBlocks(
            Map<PositionKey, BlockState> blocks,
            List<Vector> path,
            List<BlockType> blockTypes,
            boolean overwrite
    ) {
        Set<PositionKey> positions = createConnectedPathPositions(path);

        for (PositionKey position : positions) {
            BlockType blockType = RailMaterialPalette.select(blockTypes, position);
            BlockState blockState = createPathState(blockType, position, positions);

            if (overwrite)
                blocks.put(position, blockState);
            else
                blocks.computeIfAbsent(position, ignored -> blockState);
        }
    }

    private Set<PositionKey> createConnectedPathPositions(List<Vector> path) {
        Set<PositionKey> positions = new LinkedHashSet<>();

        for (Vector point : path)
            positions.add(PositionKey.from(point));

        addDiagonalConnectorPositions(positions, path);
        return positions;
    }

    /**
     * Minecraft blocks only connect over cardinal edges. The rasterizer also
     * emits diagonal steps, so bridge each one with a single orthogonal block.
     * Continuing the preceding/following cardinal direction avoids bulky 2x2
     * corners while keeping panes, bars, fences, walls and full blocks connected.
     */
    private void addDiagonalConnectorPositions(Set<PositionKey> positions, List<Vector> path) {
        for (int index = 1; index < path.size(); index++) {
            Vector previous = path.get(index - 1);
            Vector current = path.get(index);
            RailStep step = getStepBetween(previous, current);

            if (step.dx() == 0 || step.dz() == 0)
                continue;

            positions.add(PositionKey.from(selectDiagonalBridge(path, index, previous, current, step)));
        }
    }

    private Vector selectDiagonalBridge(
            List<Vector> path,
            int index,
            Vector previous,
            Vector current,
            RailStep diagonalStep
    ) {
        Vector xFirst = new Vector(
                previous.getBlockX() + diagonalStep.dx(),
                current.getBlockY(),
                previous.getBlockZ()
        );
        Vector zFirst = new Vector(
                previous.getBlockX(),
                current.getBlockY(),
                previous.getBlockZ() + diagonalStep.dz()
        );

        if (index > 1) {
            RailStep previousStep = getStepBetween(path.get(index - 2), previous);

            if (previousStep.dx() != 0 && previousStep.dz() == 0)
                return xFirst;

            if (previousStep.dz() != 0 && previousStep.dx() == 0)
                return zFirst;
        }

        if (index < path.size() - 1) {
            RailStep nextStep = getStepBetween(current, path.get(index + 1));

            if (nextStep.dx() != 0 && nextStep.dz() == 0)
                return zFirst;

            if (nextStep.dz() != 0 && nextStep.dx() == 0)
                return xFirst;
        }

        return index % 2 == 0 ? xFirst : zFirst;
    }

    private BlockState createPathState(BlockType blockType, PositionKey position, Set<PositionKey> positions) {
        Set<Direction> connections = EnumSet.noneOf(Direction.class);
        addConnectionIfPresent(connections, positions, position, Direction.NORTH, 0, -1);
        addConnectionIfPresent(connections, positions, position, Direction.EAST, 1, 0);
        addConnectionIfPresent(connections, positions, position, Direction.SOUTH, 0, 1);
        addConnectionIfPresent(connections, positions, position, Direction.WEST, -1, 0);

        if (connections.isEmpty())
            connections.add(DEFAULT_FACING);

        return createConnectedState(blockType, connections);
    }

    private void addConnectionIfPresent(
            Set<Direction> connections,
            Set<PositionKey> positions,
            PositionKey position,
            Direction direction,
            int xOffset,
            int zOffset
    ) {
        if (positions.contains(PositionKey.of(position.x() + xOffset, position.y(), position.z() + zOffset)))
            connections.add(direction);
    }

    private BlockState createConnectedState(BlockType blockType, Set<Direction> connections) {
        BlockState blockState = blockType.getDefaultState();

        if (!hasDirectionalConnectionProperties(blockType))
            return createDirectionalSupportState(blockType, connections.iterator().next());

        blockState = applyConnection(blockState, blockType, PropertyKey.NORTH, connections.contains(Direction.NORTH));
        blockState = applyConnection(blockState, blockType, PropertyKey.EAST, connections.contains(Direction.EAST));
        blockState = applyConnection(blockState, blockType, PropertyKey.SOUTH, connections.contains(Direction.SOUTH));
        blockState = applyConnection(blockState, blockType, PropertyKey.WEST, connections.contains(Direction.WEST));

        if (hasProperty(blockType, PropertyKey.UP))
            blockState = blockState.with(PropertyKey.UP, true);

        return blockState;
    }

    private BlockState createDirectionalSupportState(BlockType blockType, Direction direction) {
        BlockState blockState = createDirectionalState(blockType, direction);

        if (!hasProperty(blockType, PropertyKey.TYPE))
            return blockState;

        try {
            return blockState.with(PropertyKey.TYPE, "bottom");
        } catch (IllegalArgumentException exception) {
            return blockState;
        }
    }

    private BlockState applyConnection(BlockState blockState, BlockType blockType, PropertyKey key, boolean connected) {
        if (!hasProperty(blockType, key))
            return blockState;

        try {
            return blockState.with(key, connected);
        } catch (IllegalArgumentException exception) {
            return blockState.with(key, connected ? "low" : "none");
        }
    }

    private boolean hasDirectionalConnectionProperties(BlockType blockType) {
        return hasProperty(blockType, PropertyKey.NORTH)
                || hasProperty(blockType, PropertyKey.EAST)
                || hasProperty(blockType, PropertyKey.SOUTH)
                || hasProperty(blockType, PropertyKey.WEST);
    }

    private boolean hasProperty(BlockType blockType, PropertyKey key) {
        return blockType.getPropertyMap().containsKey(key.getName());
    }

    private RailStep getStepBetween(Vector from, Vector to) {
        int xDirection = Integer.compare(to.getBlockX() - from.getBlockX(), 0);
        int zDirection = Integer.compare(to.getBlockZ() - from.getBlockZ(), 0);
        return new RailStep(xDirection, zDirection);
    }

    private RailStep getStep(List<Vector> path, int index) {
        Vector from = index > 0 ? path.get(index - 1) : path.get(index);
        Vector to = index < path.size() - 1 ? path.get(index + 1) : path.get(index);
        int xDirection = Integer.compare(to.getBlockX() - from.getBlockX(), 0);
        int zDirection = Integer.compare(to.getBlockZ() - from.getBlockZ(), 0);

        if (xDirection == 0 && zDirection == 0)
            return new RailStep(1, 0);

        return new RailStep(xDirection, zDirection);
    }

    private BlockState createDirectionalState(BlockType blockType, Direction direction) {
        if (!blockType.getPropertyMap().containsKey(FACING_PROPERTY))
            return blockType.getDefaultState();

        return GeneratorUtils.getBlockStateWithFacing(blockType, direction);
    }

    private record PortalPole(int x, int z, int surfaceY) {
    }

    private record HorizontalPosition(int x, int z) {
    }
}

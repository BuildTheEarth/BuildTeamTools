package net.buildtheearth.buildteamtools.modules.generator.components.rail;

import com.alpsbte.alpslib.utils.GeneratorUtils;
import com.cryptomorin.xseries.XMaterial;
import com.fastasyncworldedit.core.registry.state.PropertyKey;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockTypes;
import net.buildtheearth.buildteamtools.modules.generator.model.GeneratorComponent;
import net.buildtheearth.buildteamtools.modules.generator.model.Script;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RailScripts extends Script {

    private static final int MAX_CONTROL_POINTS = 250;
    private static final int MAX_PATH_POINTS = 20_000;
    private static final int MAX_BLOCK_PLACEMENTS = 100_000;

    private static final XMaterial[] CENTER_MATERIALS = new XMaterial[]{
            XMaterial.DEAD_FIRE_CORAL_BLOCK,
            XMaterial.STONE,
            XMaterial.COBBLESTONE
    };

    private final List<Vector> customControlPoints;

    private List<Vector> controlPoints;
    private List<Vector> centerPath;

    public RailScripts(Player player, GeneratorComponent generatorComponent) {
        this(player, generatorComponent, null);
    }

    public RailScripts(Player player, GeneratorComponent generatorComponent, List<Vector> customControlPoints) {
        super(player, generatorComponent);
        this.customControlPoints = customControlPoints;

        Thread thread = new Thread(this::railScript_v_1_0);
        thread.start();
    }

    private void railScript_v_1_0() {
        controlPoints = getControlPoints();

        if (controlPoints.size() < 2) {
            getPlayer().sendMessage("§cRail Generator needs at least two points.");
            return;
        }

        if (controlPoints.size() > MAX_CONTROL_POINTS) {
            getPlayer().sendMessage("§cRail Generator has too many points. Please use fewer points.");
            return;
        }

        centerPath = createEightDirectionalPath(controlPoints);

        if (centerPath.size() < 2) {
            getPlayer().sendMessage("§cRail Generator could not create a valid rail path.");
            return;
        }

        if (centerPath.size() > MAX_PATH_POINTS) {
            getPlayer().sendMessage("§cRail Generator path is too large. Please use a smaller selection.");
            return;
        }

        Map<PositionKey, RailSideBlock> sideBlocks = buildSideBlocks(centerPath);
        Map<PositionKey, BlockState> blocksToPlace = buildBlockMap(centerPath, sideBlocks);

        if (blocksToPlace.size() > MAX_BLOCK_PLACEMENTS) {
            getPlayer().sendMessage("§cRail Generator would place too many blocks. Please use a smaller selection.");
            return;
        }

        for (Map.Entry<PositionKey, BlockState> entry : blocksToPlace.entrySet()) {
            PositionKey key = entry.getKey();
            BlockState blockState = entry.getValue();

            if (blockState == null)
                continue;

            Vector position = key.toVector();

            createCuboidSelection(position, position);
            replaceBlocks((BlockState[]) null, blockState);
        }

        finish(null, getCuboidRestoreSelection());
    }

    private List<Vector> getControlPoints() {
        if (customControlPoints != null && customControlPoints.size() >= 2)
            return copyPoints(customControlPoints);

        if (getRegion() instanceof CuboidRegion cuboidRegion)
            return getCuboidControlPoints(cuboidRegion);

        if (getRegion() instanceof ConvexPolyhedralRegion convexRegion)
            return getConvexControlPoints(convexRegion);

        if (getRegion() instanceof Polygonal2DRegion polygonalRegion)
            return getPolygonalControlPoints(polygonalRegion);

        return new ArrayList<>(GeneratorUtils.getSelectionPointsFromRegion(getRegion()));
    }

    private List<Vector> copyPoints(List<Vector> points) {
        List<Vector> copiedPoints = new ArrayList<>();

        for (Vector point : points)
            copiedPoints.add(toBlockVector(point));

        return copiedPoints;
    }

    private List<Vector> getCuboidControlPoints(CuboidRegion cuboidRegion) {
        List<Vector> points = new ArrayList<>();

        BlockVector3 pos1 = cuboidRegion.getPos1();
        BlockVector3 pos2 = cuboidRegion.getPos2();

        points.add(new Vector(pos1.x(), pos1.y(), pos1.z()));
        points.add(new Vector(pos2.x(), pos2.y(), pos2.z()));

        return points;
    }

    private List<Vector> getConvexControlPoints(ConvexPolyhedralRegion convexRegion) {
        List<Vector> points = new ArrayList<>();

        for (BlockVector3 point : convexRegion.getVertices()) {
            points.add(new Vector(
                    point.x(),
                    point.y(),
                    point.z()
            ));
        }

        return points;
    }

    private List<Vector> getPolygonalControlPoints(Polygonal2DRegion polygonalRegion) {
        List<Vector> points = new ArrayList<>();

        for (BlockVector2 point : polygonalRegion.getPoints()) {
            int y = getRailYFromWorld(
                    point.x(),
                    point.z(),
                    polygonalRegion.getMinimumY(),
                    polygonalRegion.getMaximumY()
            );

            points.add(new Vector(
                    point.x(),
                    y,
                    point.z()
            ));
        }

        return points;
    }

    private int getRailYFromWorld(int x, int z, int minimumY, int maximumY) {
        for (int y = maximumY; y >= minimumY; y--) {
            Block block = getPlayer().getWorld().getBlockAt(x, y, z);
            Material material = block.getType();

            if (!material.isAir()
                    && material != Material.WATER
                    && material != Material.LAVA) {
                return y + 1;
            }
        }

        return minimumY;
    }

    private List<Vector> createEightDirectionalPath(List<Vector> points) {
        List<Vector> path = new ArrayList<>();

        if (points == null || points.isEmpty())
            return path;

        addPointIfNew(path, toBlockVector(points.get(0)));

        for (int index = 0; index < points.size() - 1; index++) {
            Vector start = toBlockVector(points.get(index));
            Vector end = toBlockVector(points.get(index + 1));

            appendEightDirectionalLine(path, start, end);
        }

        return path;
    }

    private void appendEightDirectionalLine(List<Vector> path, Vector start, Vector end) {
        int x = start.getBlockX();
        int z = start.getBlockZ();

        int startY = start.getBlockY();

        int endX = end.getBlockX();
        int endY = end.getBlockY();
        int endZ = end.getBlockZ();

        int totalHorizontalSteps = Math.max(
                Math.abs(endX - x),
                Math.abs(endZ - z)
        );

        if (totalHorizontalSteps == 0) {
            addPointIfNew(path, new Vector(endX, endY, endZ));
            return;
        }

        int currentStep = 0;

        while (x != endX || z != endZ) {
            if (x < endX)
                x++;
            else if (x > endX)
                x--;

            if (z < endZ)
                z++;
            else if (z > endZ)
                z--;

            currentStep++;

            double progress = currentStep / (double) totalHorizontalSteps;
            int y = (int) Math.round(startY + (endY - startY) * progress);

            addPointIfNew(path, new Vector(x, y, z));
        }
    }

    private Map<PositionKey, RailSideBlock> buildSideBlocks(List<Vector> centerPath) {
        Map<PositionKey, RailSideBlock> sideBlocks = new LinkedHashMap<>();
        Map<PositionKey, Vector> centerBlocks = createCenterBlockMap(centerPath);

        for (int index = 0; index < centerPath.size(); index++) {
            Vector center = centerPath.get(index);
            List<Vector> directions = getDirectionsForCenterPoint(centerPath, index);

            for (Vector direction : directions)
                addSideBlocksForDirection(sideBlocks, centerBlocks, center, direction);
        }

        return sideBlocks;
    }

    private Map<PositionKey, Vector> createCenterBlockMap(List<Vector> centerPath) {
        Map<PositionKey, Vector> centerBlocks = new LinkedHashMap<>();

        for (Vector center : centerPath)
            centerBlocks.put(PositionKey.from(center), center);

        return centerBlocks;
    }

    private List<Vector> getDirectionsForCenterPoint(List<Vector> path, int index) {
        List<Vector> directions = new ArrayList<>();

        if (index > 0)
            addDirectionIfNew(directions, getHorizontalDirection(path.get(index - 1), path.get(index)));

        if (index < path.size() - 1)
            addDirectionIfNew(directions, getHorizontalDirection(path.get(index), path.get(index + 1)));

        return directions;
    }

    private void addDirectionIfNew(List<Vector> directions, Vector direction) {
        if (direction == null)
            return;

        for (Vector existingDirection : directions) {
            if (existingDirection.getBlockX() == direction.getBlockX()
                    && existingDirection.getBlockZ() == direction.getBlockZ())
                return;
        }

        directions.add(direction);
    }

    private void addSideBlocksForDirection(
            Map<PositionKey, RailSideBlock> sideBlocks,
            Map<PositionKey, Vector> centerBlocks,
            Vector center,
            Vector direction
    ) {
        if (isDiagonal(direction)) {
            addDiagonalSideBlocks(sideBlocks, centerBlocks, center, direction);
            return;
        }

        addStraightSideBlocks(sideBlocks, centerBlocks, center, direction);
    }

    private void addStraightSideBlocks(
            Map<PositionKey, RailSideBlock> sideBlocks,
            Map<PositionKey, Vector> centerBlocks,
            Vector center,
            Vector direction
    ) {
        Vector left = center.clone().add(getLeftOffset(direction));
        Vector right = center.clone().add(getRightOffset(direction));

        addSideBlock(sideBlocks, centerBlocks, left, direction);
        addSideBlock(sideBlocks, centerBlocks, right, direction);
    }

    private void addDiagonalSideBlocks(
            Map<PositionKey, RailSideBlock> sideBlocks,
            Map<PositionKey, Vector> centerBlocks,
            Vector center,
            Vector direction
    ) {
        int dx = direction.getBlockX();
        int dz = direction.getBlockZ();

        Vector firstSide = new Vector(
                center.getBlockX() + dx,
                center.getBlockY(),
                center.getBlockZ()
        );

        Vector secondSide = new Vector(
                center.getBlockX(),
                center.getBlockY(),
                center.getBlockZ() + dz
        );

        addSideBlock(sideBlocks, centerBlocks, firstSide, direction);
        addSideBlock(sideBlocks, centerBlocks, secondSide, direction);
    }

    private void addSideBlock(
            Map<PositionKey, RailSideBlock> sideBlocks,
            Map<PositionKey, Vector> centerBlocks,
            Vector position,
            Vector direction
    ) {
        PositionKey key = PositionKey.from(position);

        if (centerBlocks.containsKey(key))
            return;

        RailSideBlock sideBlock = sideBlocks.computeIfAbsent(
                key,
                ignored -> new RailSideBlock(position)
        );

        sideBlock.addDirection(direction);
    }

    private Map<PositionKey, BlockState> buildBlockMap(
            List<Vector> centerPath,
            Map<PositionKey, RailSideBlock> sideBlocks
    ) {
        Map<PositionKey, BlockState> blockMap = new LinkedHashMap<>();

        for (RailSideBlock sideBlock : sideBlocks.values()) {
            Vector resolvedDirection = resolveSideBlockDirection(sideBlock, sideBlocks);
            BlockState anvil = createAnvilBlockState(resolvedDirection);

            if (anvil != null)
                blockMap.put(PositionKey.from(sideBlock.position()), anvil);
        }

        for (Vector center : centerPath)
            blockMap.put(PositionKey.from(center), createCenterBlockState(center));

        return blockMap;
    }

    private Vector resolveSideBlockDirection(
            RailSideBlock sideBlock,
            Map<PositionKey, RailSideBlock> sideBlocks
    ) {
        Vector position = sideBlock.position();

        boolean east = sideBlocks.containsKey(PositionKey.of(
                position.getBlockX() + 1,
                position.getBlockY(),
                position.getBlockZ()
        ));

        boolean west = sideBlocks.containsKey(PositionKey.of(
                position.getBlockX() - 1,
                position.getBlockY(),
                position.getBlockZ()
        ));

        boolean south = sideBlocks.containsKey(PositionKey.of(
                position.getBlockX(),
                position.getBlockY(),
                position.getBlockZ() + 1
        ));

        boolean north = sideBlocks.containsKey(PositionKey.of(
                position.getBlockX(),
                position.getBlockY(),
                position.getBlockZ() - 1
        ));

        boolean eastWest = east || west;
        boolean northSouth = north || south;

        if (eastWest && !northSouth)
            return getHorizontalDirection(east, west);

        if (!eastWest && northSouth)
            return getVerticalDirection(south, north);

        if (eastWest || northSouth)
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

        int averageX = average.getBlockX();
        int averageZ = average.getBlockZ();

        if (Math.abs(averageX) > Math.abs(averageZ)) {
            if (averageX > 0 && east)
                return new Vector(1, 0, 0);

            if (averageX < 0 && west)
                return new Vector(-1, 0, 0);
        }

        if (Math.abs(averageZ) > Math.abs(averageX)) {
            if (averageZ > 0 && south)
                return new Vector(0, 0, 1);

            if (averageZ < 0 && north)
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

    private BlockState createCenterBlockState(Vector position) {
        int index = Math.floorMod(
                position.getBlockX() * 31 + position.getBlockZ() * 17,
                CENTER_MATERIALS.length
        );

        return GeneratorUtils.getBlockState(CENTER_MATERIALS[index]);
    }

    private BlockState createAnvilBlockState(Vector direction) {
        if (BlockTypes.ANVIL == null)
            return null;

        return BlockTypes.ANVIL
                .getDefaultState()
                .with(PropertyKey.FACING, getWorldEditDirection(direction));
    }

    private Direction getWorldEditDirection(Vector direction) {
        int dx = direction.getBlockX();
        int dz = direction.getBlockZ();

        if (Math.abs(dx) >= Math.abs(dz)) {
            if (dx > 0)
                return Direction.EAST;

            if (dx < 0)
                return Direction.WEST;
        }

        if (dz > 0)
            return Direction.SOUTH;

        if (dz < 0)
            return Direction.NORTH;

        return Direction.EAST;
    }

    private Vector getHorizontalDirection(Vector from, Vector to) {
        int dx = Integer.compare(to.getBlockX() - from.getBlockX(), 0);
        int dz = Integer.compare(to.getBlockZ() - from.getBlockZ(), 0);

        if (dx == 0 && dz == 0)
            return null;

        return new Vector(dx, 0, dz);
    }

    private boolean isDiagonal(Vector direction) {
        return direction.getBlockX() != 0 && direction.getBlockZ() != 0;
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

    private Vector toBlockVector(Vector vector) {
        return new Vector(
                vector.getBlockX(),
                vector.getBlockY(),
                vector.getBlockZ()
        );
    }

    private void addPointIfNew(List<Vector> points, Vector point) {
        Vector blockPoint = toBlockVector(point);

        if (points.isEmpty()) {
            points.add(blockPoint);
            return;
        }

        if (!isSameBlock(points.get(points.size() - 1), blockPoint))
            points.add(blockPoint);
    }

    private List<Vector> getCuboidRestoreSelection() {
        List<Vector> restoreSelection = new ArrayList<>();

        restoreSelection.add(controlPoints.get(0));
        restoreSelection.add(controlPoints.get(controlPoints.size() - 1));

        return restoreSelection;
    }

    private boolean isSameBlock(Vector first, Vector second) {
        return first.getBlockX() == second.getBlockX()
                && first.getBlockY() == second.getBlockY()
                && first.getBlockZ() == second.getBlockZ();
    }

    private record PositionKey(int x, int y, int z) {

        private static PositionKey from(Vector vector) {
            return new PositionKey(
                    vector.getBlockX(),
                    vector.getBlockY(),
                    vector.getBlockZ()
            );
        }

        private static PositionKey of(int x, int y, int z) {
            return new PositionKey(x, y, z);
        }

        private Vector toVector() {
            return new Vector(x, y, z);
        }
    }

    private static class RailSideBlock {

        private final Vector position;
        private int directionX;
        private int directionZ;

        private RailSideBlock(Vector position) {
            this.position = position;
        }

        private Vector position() {
            return position;
        }

        private void addDirection(Vector direction) {
            directionX += direction.getBlockX();
            directionZ += direction.getBlockZ();
        }

        private Vector getAverageDirection() {
            int dx = Integer.compare(directionX, 0);
            int dz = Integer.compare(directionZ, 0);

            if (dx == 0 && dz == 0)
                return new Vector(1, 0, 0);

            return new Vector(dx, 0, dz);
        }
    }
}
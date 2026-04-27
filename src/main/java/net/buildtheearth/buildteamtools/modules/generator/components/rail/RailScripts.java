package net.buildtheearth.buildteamtools.modules.generator.components.rail;

import com.alpsbte.alpslib.utils.GeneratorUtils;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.generator.GeneratorModule;
import net.buildtheearth.buildteamtools.modules.generator.model.GeneratorComponent;
import net.buildtheearth.buildteamtools.modules.generator.model.GeneratorType;
import net.buildtheearth.buildteamtools.modules.generator.model.History;
import net.buildtheearth.buildteamtools.modules.generator.model.Script;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class RailScripts extends Script {

    private static final Material[] CENTER_MATERIALS = new Material[]{
            Material.DEAD_FIRE_CORAL_BLOCK,
            Material.STONE,
            Material.COBBLESTONE
    };

    private static final Material SIDE_MATERIAL = Material.ANVIL;

    public RailScripts(Player player, GeneratorComponent generatorComponent) {
        super(player, generatorComponent);

        Thread thread = new Thread(this::generateRail);
        thread.start();
    }

    private void generateRail() {
        try {
            List<Vector> controlPoints = getControlPoints();

            if (controlPoints.size() < 2) {
                getPlayer().sendMessage("§cRail Generator needs at least two usable points in the selection.");
                return;
            }

            List<Vector> centerPath = createCenterPath(controlPoints);

            if (centerPath.size() < 2) {
                getPlayer().sendMessage("§cRail Generator could not derive a valid path from this selection.");
                return;
            }

            Bukkit.getScheduler().runTask(BuildTeamTools.getInstance(), () -> placeSampleTrack(centerPath));
        } catch (Exception exception) {
            getPlayer().sendMessage("§cRail Generator failed while reading the WorldEdit selection.");
            exception.printStackTrace();
        }
    }

    private List<Vector> getControlPoints() {
        if (getRegion() instanceof CuboidRegion cuboidRegion)
            return getCuboidControlPoints(cuboidRegion);

        if (getRegion() instanceof Polygonal2DRegion polygonalRegion)
            return getPolygonalControlPoints(polygonalRegion);

        List<Vector> points = GeneratorUtils.getSelectionPointsFromRegion(getRegion());

        if (points == null || points.size() < 2)
            return new ArrayList<>();

        List<Vector> blockPoints = new ArrayList<>();

        for (Vector point : points)
            blockPoints.add(toBlockVector(point));

        return orderPointsAsPath(blockPoints);
    }

    private List<Vector> getCuboidControlPoints(CuboidRegion cuboidRegion) {
        List<Vector> points = new ArrayList<>();

        BlockVector3 pos1 = cuboidRegion.getPos1();
        BlockVector3 pos2 = cuboidRegion.getPos2();

        Vector start = new Vector(pos1.x(), pos1.y(), pos1.z());
        Vector end = new Vector(pos2.x(), pos2.y(), pos2.z());

        if (sameBlock(start, end)) {
            start = new Vector(
                    cuboidRegion.getMinimumPoint().x(),
                    cuboidRegion.getMinimumPoint().y(),
                    cuboidRegion.getMinimumPoint().z()
            );

            end = new Vector(
                    cuboidRegion.getMaximumPoint().x(),
                    cuboidRegion.getMinimumPoint().y(),
                    cuboidRegion.getMaximumPoint().z()
            );
        }

        points.add(start);
        points.add(end);

        return points;
    }

    private List<Vector> getPolygonalControlPoints(Polygonal2DRegion polygonalRegion) {
        List<Vector> points = new ArrayList<>();

        int minY = polygonalRegion.getMinimumY();
        int maxY = polygonalRegion.getMaximumY();

        for (BlockVector2 point : polygonalRegion.getPoints()) {
            int y = findBestYForPolygonPoint(point.x(), point.z(), minY, maxY);
            points.add(new Vector(point.x(), y, point.z()));
        }

        if (points.size() < 2)
            return points;

        return removeOnlyConsecutiveDuplicates(points);
    }

    private int findBestYForPolygonPoint(int x, int z, int minY, int maxY) {
        org.bukkit.World world = getPlayer().getWorld();

        int safeMinY = Math.max(world.getMinHeight(), Math.min(minY, maxY));
        int safeMaxY = Math.min(world.getMaxHeight() - 1, Math.max(minY, maxY));

        for (int y = safeMaxY; y >= safeMinY; y--) {
            Material material = world.getBlockAt(x, y, z).getType();

            if (material.isSolid())
                return y;
        }

        return safeMaxY;
    }

    private List<Vector> orderPointsAsPath(List<Vector> points) {
        List<Vector> remaining = new ArrayList<>();
        List<Vector> ordered = new ArrayList<>();

        for (Vector point : points) {
            if (!containsBlock(remaining, point))
                remaining.add(point);
        }

        if (remaining.isEmpty())
            return ordered;

        Vector current = findStartPoint(remaining);
        ordered.add(current);
        remaining.remove(current);

        while (!remaining.isEmpty()) {
            Vector next = findNearestPoint(current, remaining);
            ordered.add(next);
            remaining.remove(next);
            current = next;
        }

        return ordered;
    }

    private Vector findStartPoint(List<Vector> points) {
        Vector best = points.get(0);

        for (Vector point : points) {
            if (point.getBlockX() < best.getBlockX()) {
                best = point;
                continue;
            }

            if (point.getBlockX() == best.getBlockX() && point.getBlockZ() < best.getBlockZ())
                best = point;
        }

        return best;
    }

    private Vector findNearestPoint(Vector from, List<Vector> points) {
        Vector best = points.get(0);
        double bestDistance = distanceSquared2D(from, best);

        for (Vector point : points) {
            double distance = distanceSquared2D(from, point);

            if (distance < bestDistance) {
                best = point;
                bestDistance = distance;
            }
        }

        return best;
    }

    private double distanceSquared2D(Vector a, Vector b) {
        double dx = a.getBlockX() - b.getBlockX();
        double dz = a.getBlockZ() - b.getBlockZ();

        return dx * dx + dz * dz;
    }

    private boolean containsBlock(List<Vector> points, Vector target) {
        for (Vector point : points) {
            if (sameBlock(point, target))
                return true;
        }

        return false;
    }

    private List<Vector> createCenterPath(List<Vector> controlPoints) {
        List<Vector> centerPath = new ArrayList<>();

        for (int i = 0; i < controlPoints.size() - 1; i++) {
            Vector from = controlPoints.get(i);
            Vector to = controlPoints.get(i + 1);

            appendEightDirectionalLine(centerPath, from, to);
        }

        return removeOnlyConsecutiveDuplicates(repairGaps(centerPath));
    }

    private void appendEightDirectionalLine(List<Vector> path, Vector from, Vector to) {
        int startX = from.getBlockX();
        int startY = from.getBlockY();
        int startZ = from.getBlockZ();

        int endX = to.getBlockX();
        int endY = to.getBlockY();
        int endZ = to.getBlockZ();

        int dx = endX - startX;
        int dy = endY - startY;
        int dz = endZ - startZ;

        int steps = Math.max(Math.abs(dx), Math.abs(dz));

        if (steps == 0) {
            addPointIfNew(path, new Vector(startX, startY, startZ));
            return;
        }

        for (int step = 0; step <= steps; step++) {
            double t = step / (double) steps;

            int x = (int) Math.round(startX + dx * t);
            int y = (int) Math.round(startY + dy * t);
            int z = (int) Math.round(startZ + dz * t);

            addPointIfNew(path, new Vector(x, y, z));
        }
    }

    private List<Vector> repairGaps(List<Vector> path) {
        if (path.size() < 2)
            return path;

        List<Vector> repaired = new ArrayList<>();
        repaired.add(path.get(0));

        for (int i = 1; i < path.size(); i++) {
            Vector previous = repaired.get(repaired.size() - 1);
            Vector current = path.get(i);

            if (getChebyshevDistance(previous, current) <= 1) {
                addPointIfNew(repaired, current);
                continue;
            }

            appendEightDirectionalLine(repaired, previous, current);
        }

        return repaired;
    }

    private int getChebyshevDistance(Vector a, Vector b) {
        int dx = Math.abs(a.getBlockX() - b.getBlockX());
        int dz = Math.abs(a.getBlockZ() - b.getBlockZ());

        return Math.max(dx, dz);
    }

    private void addPointIfNew(List<Vector> path, Vector point) {
        if (path.isEmpty()) {
            path.add(point);
            return;
        }

        Vector last = path.get(path.size() - 1);

        if (!sameBlock(last, point))
            path.add(point);
    }

    private List<Vector> removeOnlyConsecutiveDuplicates(List<Vector> path) {
        List<Vector> result = new ArrayList<>();

        for (Vector point : path)
            addPointIfNew(result, point);

        return result;
    }

    private Vector toBlockVector(Vector vector) {
        return new Vector(
                Math.round(vector.getX()),
                Math.round(vector.getY()),
                Math.round(vector.getZ())
        );
    }

    private boolean sameBlock(Vector a, Vector b) {
        return a.getBlockX() == b.getBlockX()
                && a.getBlockY() == b.getBlockY()
                && a.getBlockZ() == b.getBlockZ();
    }

    private void placeSampleTrack(List<Vector> centerPath) {
        Map<BlockVector3, BlockData> blockMap = new LinkedHashMap<>();
        LinkedHashSet<BlockVector3> centerPositions = new LinkedHashSet<>();
        Map<BlockVector3, SideBlock> sideBlocks = new LinkedHashMap<>();

        for (Vector center : centerPath)
            centerPositions.add(toBlockVector3(center));

        createSideBlocksFromCenterEdges(centerPath, centerPositions, sideBlocks);
        placeSideBlocks(blockMap, sideBlocks);
        placeCenterBlocks(blockMap, centerPositions);

        boolean placedWithWorldEdit = tryPlaceWithWorldEdit(blockMap);

        if (!placedWithWorldEdit) {
            getPlayer().sendMessage("§eWorldEdit history is unavailable. Falling back to Bukkit placement.");
            getPlayer().sendMessage("§eUse §6/gen undo§e instead of §6//undo§e for this generation.");
            placeWithBukkitFallback(blockMap);
            return;
        }

        GeneratorModule.getInstance()
                .getPlayerHistory(getPlayer())
                .addHistoryEntry(new History.HistoryEntry(GeneratorType.RAILWAY, this, 1));

        getGeneratorComponent().sendSuccessMessage(getPlayer());
    }

    private void createSideBlocksFromCenterEdges(
            List<Vector> centerPath,
            LinkedHashSet<BlockVector3> centerPositions,
            Map<BlockVector3, SideBlock> sideBlocks
    ) {
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
    }

    private void placeStraightEdgeSideBlocks(
            Map<BlockVector3, SideBlock> sideBlocks,
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
            Map<BlockVector3, SideBlock> sideBlocks,
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

    private BlockVector3 offset(Vector center, Vector offset) {
        return BlockVector3.at(
                center.getBlockX() + offset.getBlockX(),
                center.getBlockY(),
                center.getBlockZ() + offset.getBlockZ()
        );
    }

    private void addSideBlock(
            Map<BlockVector3, SideBlock> sideBlocks,
            LinkedHashSet<BlockVector3> centerPositions,
            BlockVector3 position,
            Vector direction
    ) {
        if (centerPositions.contains(position))
            return;

        SideBlock sideBlock = sideBlocks.computeIfAbsent(position, SideBlock::new);
        sideBlock.addDirection(direction);
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

    private void placeSideBlocks(Map<BlockVector3, BlockData> blockMap, Map<BlockVector3, SideBlock> sideBlocks) {
        for (SideBlock sideBlock : sideBlocks.values()) {
            Vector direction = getBestAnvilDirection(sideBlock, sideBlocks);
            blockMap.put(sideBlock.position, getAnvilBlockData(direction));
        }
    }

    private Vector getBestAnvilDirection(SideBlock sideBlock, Map<BlockVector3, SideBlock> sideBlocks) {
        boolean eastWest = sideBlocks.containsKey(sideBlock.position.add(1, 0, 0))
                || sideBlocks.containsKey(sideBlock.position.add(-1, 0, 0));

        boolean northSouth = sideBlocks.containsKey(sideBlock.position.add(0, 0, 1))
                || sideBlocks.containsKey(sideBlock.position.add(0, 0, -1));

        if (eastWest && !northSouth)
            return new Vector(1, 0, 0);

        if (northSouth && !eastWest)
            return new Vector(0, 0, 1);

        return sideBlock.getAverageDirection();
    }

    private Vector getDirection(Vector from, Vector to) {
        int dx = Integer.compare(to.getBlockX() - from.getBlockX(), 0);
        int dz = Integer.compare(to.getBlockZ() - from.getBlockZ(), 0);

        if (dx == 0 && dz == 0)
            return null;

        return new Vector(dx, 0, dz);
    }

    private void placeCenterBlocks(Map<BlockVector3, BlockData> blockMap, LinkedHashSet<BlockVector3> centerPositions) {
        for (BlockVector3 center : centerPositions)
            blockMap.put(center, getCenterBlockData(center));
    }

    private boolean tryPlaceWithWorldEdit(Map<BlockVector3, BlockData> blockMap) {
        try (EditSession editSession = WorldEdit.getInstance()
                .newEditSessionBuilder()
                .world(getWeWorld())
                .actor(getActor())
                .build()) {

            for (Map.Entry<BlockVector3, BlockData> entry : blockMap.entrySet())
                editSession.setBlock(entry.getKey(), BukkitAdapter.adapt(entry.getValue()));

            editSession.flushQueue();
            getLocalSession().remember(editSession);
            return true;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return false;
        }
    }

    private void placeWithBukkitFallback(Map<BlockVector3, BlockData> blockMap) {
        List<History.BlockChange> changes = new ArrayList<>();

        for (Map.Entry<BlockVector3, BlockData> entry : blockMap.entrySet()) {
            BlockVector3 position = entry.getKey();
            BlockData newData = entry.getValue();

            org.bukkit.block.Block block = getPlayer().getWorld().getBlockAt(position.x(), position.y(), position.z());

            changes.add(new History.BlockChange(
                    getPlayer().getWorld().getName(),
                    position.x(),
                    position.y(),
                    position.z(),
                    block.getBlockData().getAsString(),
                    newData.getAsString()
            ));

            block.setBlockData(newData, false);
        }

        GeneratorModule.getInstance()
                .getPlayerHistory(getPlayer())
                .addHistoryEntry(new History.HistoryEntry(GeneratorType.RAILWAY, this, changes));

        getGeneratorComponent().sendSuccessMessage(getPlayer());
    }

    private BlockData getCenterBlockData(BlockVector3 position) {
        int index = Math.floorMod(position.x() * 31 + position.z() * 17, CENTER_MATERIALS.length);
        return CENTER_MATERIALS[index].createBlockData();
    }

    private BlockData getAnvilBlockData(Vector direction) {
        BlockData data = SIDE_MATERIAL.createBlockData();

        if (data instanceof Directional directional)
            directional.setFacing(toBlockFace(direction));

        return data;
    }

    private BlockFace toBlockFace(Vector direction) {
        int dx = direction.getBlockX();
        int dz = direction.getBlockZ();

        if (Math.abs(dx) >= Math.abs(dz)) {
            if (dx > 0)
                return BlockFace.EAST;

            if (dx < 0)
                return BlockFace.WEST;
        }

        if (dz > 0)
            return BlockFace.SOUTH;

        if (dz < 0)
            return BlockFace.NORTH;

        return BlockFace.EAST;
    }

    private BlockVector3 toBlockVector3(Vector vector) {
        return BlockVector3.at(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }

    private static class SideBlock {

        private final BlockVector3 position;
        private int directionX;
        private int directionZ;

        private SideBlock(BlockVector3 position) {
            this.position = position;
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
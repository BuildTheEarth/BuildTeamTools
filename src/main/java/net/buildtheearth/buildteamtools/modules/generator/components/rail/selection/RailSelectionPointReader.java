package net.buildtheearth.buildteamtools.modules.generator.components.rail.selection;

import com.alpsbte.alpslib.utils.GeneratorUtils;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class RailSelectionPointReader {

    private final Player player;
    private final Region region;

    public RailSelectionPointReader(Player player, Region region) {
        this.player = player;
        this.region = region;
    }

    public boolean isSupportedSelection() {
        return region instanceof CuboidRegion
                || region instanceof Polygonal2DRegion
                || region instanceof ConvexPolyhedralRegion;
    }

    public List<Vector> readControlPoints() {
        if (region instanceof CuboidRegion cuboidRegion)
            return readCuboidPoints(cuboidRegion);

        if (region instanceof Polygonal2DRegion polygonalRegion)
            return readPolygonalPoints(polygonalRegion);

        if (region instanceof ConvexPolyhedralRegion convexRegion)
            return readConvexPoints(convexRegion);

        return new ArrayList<>();
    }

    private List<Vector> readCuboidPoints(CuboidRegion cuboidRegion) {
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

    private List<Vector> readPolygonalPoints(Polygonal2DRegion polygonalRegion) {
        List<Vector> points = new ArrayList<>();

        int minY = polygonalRegion.getMinimumY();
        int maxY = polygonalRegion.getMaximumY();

        for (BlockVector2 point : polygonalRegion.getPoints()) {
            int y = findBestY(point.x(), point.z(), minY, maxY);
            points.add(new Vector(point.x(), y, point.z()));
        }

        return removeOnlyConsecutiveDuplicates(points);
    }

    private List<Vector> readConvexPoints(ConvexPolyhedralRegion convexRegion) {
        List<Vector> points = new ArrayList<>();

        for (BlockVector3 point : convexRegion.getVertices()) {
            Vector vector = new Vector(point.x(), point.y(), point.z());

            if (!containsBlock(points, vector))
                points.add(vector);
        }

        if (points.size() < 2) {
            List<Vector> fallbackPoints = GeneratorUtils.getSelectionPointsFromRegion(region);

            if (fallbackPoints != null) {
                for (Vector point : fallbackPoints) {
                    Vector blockPoint = toBlockVector(point);

                    if (!containsBlock(points, blockPoint))
                        points.add(blockPoint);
                }
            }
        }

        return orderPointsAsPath(points);
    }

    private int findBestY(int x, int z, int minY, int maxY) {
        World world = player.getWorld();

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

    private List<Vector> removeOnlyConsecutiveDuplicates(List<Vector> points) {
        List<Vector> result = new ArrayList<>();

        for (Vector point : points) {
            if (result.isEmpty() || !sameBlock(result.get(result.size() - 1), point))
                result.add(point);
        }

        return result;
    }

    private Vector toBlockVector(Vector vector) {
        return new Vector(
                Math.round(vector.getX()),
                Math.round(vector.getY()),
                Math.round(vector.getZ())
        );
    }

    private boolean containsBlock(List<Vector> points, Vector target) {
        for (Vector point : points) {
            if (sameBlock(point, target))
                return true;
        }

        return false;
    }

    private boolean sameBlock(Vector a, Vector b) {
        return a.getBlockX() == b.getBlockX()
                && a.getBlockY() == b.getBlockY()
                && a.getBlockZ() == b.getBlockZ();
    }
}
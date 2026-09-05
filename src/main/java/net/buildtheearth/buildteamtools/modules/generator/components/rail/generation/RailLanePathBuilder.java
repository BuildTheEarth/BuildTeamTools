package net.buildtheearth.buildteamtools.modules.generator.components.rail.generation;

import com.alpsbte.alpslib.utils.GeneratorUtils;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class RailLanePathBuilder {

    private final List<Vector> controlPoints;
    private final RailTerrainResolver terrainResolver;
    private final int railLaneCount;
    private final List<Integer> railLaneSpacings;

    RailLanePathBuilder(
            List<Vector> controlPoints,
            RailTerrainResolver terrainResolver,
            int railLaneCount,
            List<Integer> railLaneSpacings
    ) {
        this.controlPoints = controlPoints;
        this.terrainResolver = terrainResolver;
        this.railLaneCount = railLaneCount;
        this.railLaneSpacings = railLaneSpacings;
    }

    List<List<Vector>> createRailCenterPaths(List<Vector> path) {
        if (railLaneCount <= 1)
            return List.of(path);

        List<List<Vector>> railCenterPaths = new ArrayList<>();
        List<Integer> offsets = createLaneOffsets();

        // Lanes are spread symmetrically around the selected path. Even track counts
        // have no lane on the path itself, only shifted lanes on both sides.
        for (int laneIndex = 0; laneIndex < railLaneCount; laneIndex++) {
            int offset = offsets.get(laneIndex);

            if (offset == 0) {
                railCenterPaths.add(path);
                continue;
            }

            List<Vector> shiftedLane = createShiftedRailLane(Math.abs(offset), offset > 0 ? 1 : -1);

            if (shiftedLane.size() >= 2)
                railCenterPaths.add(shiftedLane);
        }

        return railCenterPaths;
    }

    private List<Integer> createLaneOffsets() {
        List<Integer> positions = new ArrayList<>(railLaneCount);
        int position = 0;
        positions.add(position);

        for (int spacing : railLaneSpacings) {
            position += spacing;
            positions.add(position);
        }

        double center = position / 2.0D;
        return positions.stream().map(value -> (int) Math.round(value - center)).toList();
    }

    private List<Vector> createShiftedRailLane(int distance, int sideSign) {
        List<Vector> shiftedControlPoints = createShiftedControlPoints(distance, sideSign);

        if (shiftedControlPoints.size() < 2)
            return Collections.emptyList();

        List<Vector> shiftedPath = createCenterPath(shiftedControlPoints);

        if (shiftedPath.size() < 2)
            return Collections.emptyList();

        terrainResolver.adjustPathToTerrain(shiftedPath);
        return shiftedPath;
    }

    private List<Vector> createShiftedControlPoints(int distance, int sideSign) {
        List<Vector> shiftedControlPoints = new ArrayList<>();

        for (int index = 0; index < controlPoints.size(); index++) {
            Vector basePoint = controlPoints.get(index);
            Vector offset = getMiterOffset(index, distance, sideSign);

            if (offset.lengthSquared() != 0)
                addIfDifferentFromPrevious(shiftedControlPoints, new Vector(
                        basePoint.getBlockX() + (int) Math.round(offset.getX()),
                        basePoint.getBlockY(),
                        basePoint.getBlockZ() + (int) Math.round(offset.getZ())
                ));
        }

        return shiftedControlPoints;
    }

    private Vector getMiterOffset(int index, int distance, int sideSign) {
        Vector previousDirection = index > 0
                ? normalizedHorizontalDirection(controlPoints.get(index - 1), controlPoints.get(index))
                : new Vector();
        Vector nextDirection = index < controlPoints.size() - 1
                ? normalizedHorizontalDirection(controlPoints.get(index), controlPoints.get(index + 1))
                : new Vector();

        if (previousDirection.lengthSquared() == 0)
            return perpendicular(nextDirection, sideSign).multiply(distance);

        if (nextDirection.lengthSquared() == 0)
            return perpendicular(previousDirection, sideSign).multiply(distance);

        Vector previousNormal = perpendicular(previousDirection, sideSign);
        Vector nextNormal = perpendicular(nextDirection, sideSign);
        Vector miter = previousNormal.clone().add(nextNormal);

        if (miter.lengthSquared() < 0.0001D)
            return nextNormal.multiply(distance);

        miter.normalize();
        double denominator = miter.dot(nextNormal);

        if (Math.abs(denominator) < 0.25D)
            return nextNormal.multiply(distance);

        // The miter keeps both adjacent segments at the requested perpendicular
        // distance. Limit extreme spikes at very sharp control-point corners.
        double miterLength = Math.min(distance / denominator, distance * 4.0D);
        return miter.multiply(miterLength);
    }

    private Vector normalizedHorizontalDirection(Vector from, Vector to) {
        Vector direction = new Vector(
                to.getBlockX() - from.getBlockX(),
                0,
                to.getBlockZ() - from.getBlockZ()
        );

        return direction.lengthSquared() == 0 ? direction : direction.normalize();
    }

    private Vector perpendicular(Vector direction, int sideSign) {
        return new Vector(-direction.getZ() * sideSign, 0, direction.getX() * sideSign);
    }

    private void addIfDifferentFromPrevious(List<Vector> points, Vector point) {
        if (points.isEmpty()) {
            points.add(point);
            return;
        }

        Vector previousPoint = points.get(points.size() - 1);

        if (previousPoint.getBlockX() == point.getBlockX() && previousPoint.getBlockZ() == point.getBlockZ())
            return;

        points.add(point);
    }

    private List<Vector> createCenterPath(List<Vector> points) {
        return GeneratorUtils.removeOrthogonalCorners(GeneratorUtils.createShortestBlockPath(points));
    }
}

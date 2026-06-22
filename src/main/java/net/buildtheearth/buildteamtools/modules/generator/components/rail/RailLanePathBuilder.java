package net.buildtheearth.buildteamtools.modules.generator.components.rail;

import com.alpsbte.alpslib.utils.GeneratorUtils;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class RailLanePathBuilder {

    private final List<Vector> controlPoints;
    private final RailTerrainResolver terrainResolver;
    private final int railLaneCount;
    private final int railLaneSpacing;

    RailLanePathBuilder(List<Vector> controlPoints, RailTerrainResolver terrainResolver, int railLaneCount, int railLaneSpacing) {
        this.controlPoints = controlPoints;
        this.terrainResolver = terrainResolver;
        this.railLaneCount = railLaneCount;
        this.railLaneSpacing = railLaneSpacing;
    }

    List<List<Vector>> createRailCenterPaths(List<Vector> path) {
        if (railLaneCount <= 1)
            return List.of(path);

        List<List<Vector>> railCenterPaths = new ArrayList<>();
        int sideLaneCount = (railLaneCount - 1) / 2;

        for (int laneIndex = sideLaneCount; laneIndex >= 1; laneIndex--) {
            List<Vector> leftLane = createShiftedRailLane(laneIndex * railLaneSpacing, 1);

            if (leftLane.size() >= 2)
                railCenterPaths.add(leftLane);
        }

        railCenterPaths.add(path);

        for (int laneIndex = 1; laneIndex <= sideLaneCount; laneIndex++) {
            List<Vector> rightLane = createShiftedRailLane(laneIndex * railLaneSpacing, -1);

            if (rightLane.size() >= 2)
                railCenterPaths.add(rightLane);
        }

        return railCenterPaths;
    }

    private List<Vector> createShiftedRailLane(int distance, int sideSign) {
        List<List<Vector>> shiftedLines = GeneratorUtils.shiftPointsAll(controlPoints, distance);
        List<Vector> candidatePoints = flattenShiftedLines(shiftedLines);

        if (candidatePoints.isEmpty())
            return Collections.emptyList();

        List<Vector> shiftedControlPoints = createShiftedControlPoints(candidatePoints, distance, sideSign);

        if (shiftedControlPoints.size() < 2)
            return Collections.emptyList();

        List<Vector> shiftedPath = createCenterPath(shiftedControlPoints);

        if (shiftedPath.size() < 2)
            return Collections.emptyList();

        terrainResolver.adjustPathToTerrain(shiftedPath);
        return shiftedPath;
    }

    private List<Vector> flattenShiftedLines(List<List<Vector>> shiftedLines) {
        if (shiftedLines == null || shiftedLines.isEmpty())
            return Collections.emptyList();

        List<Vector> candidatePoints = new ArrayList<>();

        for (List<Vector> shiftedLine : shiftedLines) {
            if (shiftedLine == null || shiftedLine.isEmpty())
                continue;

            for (Vector point : shiftedLine) {
                if (point != null)
                    candidatePoints.add(point);
            }
        }

        return candidatePoints;
    }

    private List<Vector> createShiftedControlPoints(List<Vector> candidatePoints, int distance, int sideSign) {
        List<Vector> shiftedControlPoints = new ArrayList<>();

        for (int index = 0; index < controlPoints.size(); index++) {
            Vector basePoint = controlPoints.get(index);
            Vector direction = getControlPointDirection(index);

            if (direction.lengthSquared() == 0)
                continue;

            direction.normalize();

            Vector normal = new Vector(-direction.getZ(), 0, direction.getX());

            if (sideSign < 0)
                normal.multiply(-1);

            Vector shiftedPoint = getBestShiftedPoint(basePoint, normal, candidatePoints, distance);
            addIfDifferentFromPrevious(shiftedControlPoints, shiftedPoint);
        }

        return shiftedControlPoints;
    }

    private Vector getControlPointDirection(int index) {
        if (controlPoints.size() < 2)
            return new Vector(0, 0, 0);

        if (index == 0)
            return getHorizontalDirection(controlPoints.get(0), controlPoints.get(1));

        if (index == controlPoints.size() - 1)
            return getHorizontalDirection(controlPoints.get(index - 1), controlPoints.get(index));

        Vector previousDirection = getHorizontalDirection(controlPoints.get(index - 1), controlPoints.get(index));
        Vector nextDirection = getHorizontalDirection(controlPoints.get(index), controlPoints.get(index + 1));
        Vector combinedDirection = previousDirection.add(nextDirection);

        if (combinedDirection.lengthSquared() != 0)
            return combinedDirection;

        if (nextDirection.lengthSquared() != 0)
            return nextDirection;

        return previousDirection;
    }

    private Vector getHorizontalDirection(Vector from, Vector to) {
        return new Vector(
                to.getBlockX() - from.getBlockX(),
                0,
                to.getBlockZ() - from.getBlockZ()
        );
    }

    private Vector getBestShiftedPoint(Vector basePoint, Vector normal, List<Vector> candidatePoints, int distance) {
        Vector idealPoint = getIdealShiftedPoint(basePoint, normal, distance);
        Vector bestPoint = null;
        double bestDistanceSquared = Double.MAX_VALUE;

        for (Vector candidatePoint : candidatePoints) {
            double signedOffset = getSignedOffset(basePoint, candidatePoint, normal);

            if (signedOffset < 0.5D)
                continue;

            double distanceSquared = getHorizontalDistanceSquared(candidatePoint, idealPoint);

            if (distanceSquared < bestDistanceSquared) {
                bestDistanceSquared = distanceSquared;
                bestPoint = candidatePoint;
            }
        }

        double maxCandidateDistanceSquared = Math.max(16D, distance * distance * 2.25D);

        if (bestPoint == null || bestDistanceSquared > maxCandidateDistanceSquared)
            return idealPoint;

        return new Vector(bestPoint.getBlockX(), basePoint.getBlockY(), bestPoint.getBlockZ());
    }

    private Vector getIdealShiftedPoint(Vector basePoint, Vector normal, int distance) {
        return new Vector(
                basePoint.getBlockX() + (int) Math.round(normal.getX() * distance),
                basePoint.getBlockY(),
                basePoint.getBlockZ() + (int) Math.round(normal.getZ() * distance)
        );
    }

    private double getSignedOffset(Vector basePoint, Vector candidatePoint, Vector normal) {
        double offsetX = candidatePoint.getX() - basePoint.getX();
        double offsetZ = candidatePoint.getZ() - basePoint.getZ();

        return offsetX * normal.getX() + offsetZ * normal.getZ();
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

    private double getHorizontalDistanceSquared(Vector first, Vector second) {
        double dx = first.getX() - second.getX();
        double dz = first.getZ() - second.getZ();

        return dx * dx + dz * dz;
    }

    private List<Vector> createCenterPath(List<Vector> points) {
        return GeneratorUtils.removeOrthogonalCorners(GeneratorUtils.createShortestBlockPath(points));
    }
}

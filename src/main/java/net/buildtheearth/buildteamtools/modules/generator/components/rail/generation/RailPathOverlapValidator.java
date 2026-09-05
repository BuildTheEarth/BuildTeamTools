package net.buildtheearth.buildteamtools.modules.generator.components.rail.generation;

import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class RailPathOverlapValidator {

    private static final List<Integer> VERTICAL_OFFSETS = List.of(-1, 0, 1);

    boolean hasOverlap(List<List<Vector>> railCenterPaths) {
        Set<PositionKey> previousLanePositions = new HashSet<>();
        Set<DiagonalStep> previousLaneDiagonals = new HashSet<>();

        for (List<Vector> railCenterPath : railCenterPaths) {
            LaneGeometry currentLane = createLaneGeometry(railCenterPath);

            if (currentLane == null
                    || overlapsPreviousLane(currentLane, previousLanePositions, previousLaneDiagonals))
                return true;

            previousLanePositions.addAll(currentLane.centers());
            previousLaneDiagonals.addAll(currentLane.diagonals());
        }

        return false;
    }

    private LaneGeometry createLaneGeometry(List<Vector> railCenterPath) {
        Set<PositionKey> centers = new HashSet<>();
        Set<DiagonalStep> diagonals = new HashSet<>();

        for (int index = 0; index < railCenterPath.size(); index++) {
            if (!centers.add(PositionKey.from(railCenterPath.get(index))))
                return null;

            if (index == 0)
                continue;

            DiagonalStep diagonal = createDiagonalStep(railCenterPath.get(index - 1), railCenterPath.get(index));

            if (diagonal != null && crossesDiagonal(diagonal, diagonals))
                return null;

            if (diagonal != null)
                diagonals.add(diagonal);
        }

        return new LaneGeometry(centers, diagonals);
    }

    private boolean overlapsPreviousLane(LaneGeometry lane,
                                         Set<PositionKey> previousLanePositions,
                                         Set<DiagonalStep> previousLaneDiagonals) {
        for (PositionKey center : lane.centers()) {
            if (crossesPreviousLane(center, previousLanePositions))
                return true;
        }

        for (DiagonalStep diagonal : lane.diagonals()) {
            if (crossesDiagonal(diagonal, previousLaneDiagonals))
                return true;
        }

        return false;
    }

    private DiagonalStep createDiagonalStep(Vector from, Vector to) {
        int dx = to.getBlockX() - from.getBlockX();
        int dz = to.getBlockZ() - from.getBlockZ();

        if (Math.abs(dx) != 1 || Math.abs(dz) != 1)
            return null;

        return new DiagonalStep(
                Math.min(from.getBlockX(), to.getBlockX()),
                Math.min(from.getBlockZ(), to.getBlockZ()),
                Math.min(from.getBlockY(), to.getBlockY()),
                dx == dz
        );
    }

    private boolean crossesDiagonal(DiagonalStep diagonal, Set<DiagonalStep> otherDiagonals) {
        for (int yOffset : VERTICAL_OFFSETS) {
            DiagonalStep crossing = new DiagonalStep(
                    diagonal.x(),
                    diagonal.z(),
                    diagonal.y() + yOffset,
                    !diagonal.positiveSlope()
            );

            if (otherDiagonals.contains(crossing))
                return true;
        }

        return false;
    }

    private boolean crossesPreviousLane(PositionKey center, Set<PositionKey> previousLanePositions) {
        for (int yOffset : VERTICAL_OFFSETS) {
            PositionKey sameColumnPosition = PositionKey.of(center.x(), center.y() + yOffset, center.z());

            if (previousLanePositions.contains(sameColumnPosition))
                return true;
        }

        return false;
    }

    private record DiagonalStep(int x, int z, int y, boolean positiveSlope) {
    }

    private record LaneGeometry(Set<PositionKey> centers, Set<DiagonalStep> diagonals) {
    }
}

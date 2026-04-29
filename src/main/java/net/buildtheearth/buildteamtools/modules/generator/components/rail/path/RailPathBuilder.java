package net.buildtheearth.buildteamtools.modules.generator.components.rail.path;

import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class RailPathBuilder {

    public RailPath build(List<Vector> controlPoints) {
        if (controlPoints == null || controlPoints.size() < 2)
            return new RailPath(new ArrayList<>());

        List<Vector> centerPath = new ArrayList<>();

        for (int i = 0; i < controlPoints.size() - 1; i++) {
            Vector from = controlPoints.get(i);
            Vector to = controlPoints.get(i + 1);

            appendEightDirectionalLine(centerPath, from, to);
        }

        centerPath = repairGaps(centerPath);
        centerPath = removeOnlyConsecutiveDuplicates(centerPath);

        return new RailPath(centerPath);
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

    private boolean sameBlock(Vector a, Vector b) {
        return a.getBlockX() == b.getBlockX()
                && a.getBlockY() == b.getBlockY()
                && a.getBlockZ() == b.getBlockZ();
    }
}
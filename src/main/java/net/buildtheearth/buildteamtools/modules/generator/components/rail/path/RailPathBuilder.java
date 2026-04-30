package net.buildtheearth.buildteamtools.modules.generator.components.rail.path;

import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class RailPathBuilder {

    private static final int MAX_CORNER_TRIM = 4;
    private static final int MIN_CORNER_DISTANCE = 4;

    public RailPath build(List<Vector> controlPoints) {
        if (controlPoints == null || controlPoints.size() < 2)
            return new RailPath(new ArrayList<>());

        List<Vector> normalizedControlPoints = removeOnlyConsecutiveDuplicates(controlPoints);

        if (normalizedControlPoints.size() < 2)
            return new RailPath(new ArrayList<>());

        List<Vector> centerPath = createCurvedCenterPath(normalizedControlPoints);

        centerPath = repairGaps(centerPath);
        centerPath = removeImmediateBacktracking(centerPath);
        centerPath = removeOnlyConsecutiveDuplicates(centerPath);

        return new RailPath(centerPath);
    }

    private List<Vector> createCurvedCenterPath(List<Vector> controlPoints) {
        List<Vector> path = new ArrayList<>();

        addPointIfNew(path, toBlockVector(controlPoints.get(0)));

        if (controlPoints.size() == 2) {
            appendEightDirectionalLine(path, controlPoints.get(0), controlPoints.get(1));
            return path;
        }

        for (int i = 1; i < controlPoints.size() - 1; i++) {
            Vector previous = toBlockVector(controlPoints.get(i - 1));
            Vector current = toBlockVector(controlPoints.get(i));
            Vector next = toBlockVector(controlPoints.get(i + 1));

            if (!shouldCurveCorner(previous, current, next)) {
                appendEightDirectionalLine(path, path.get(path.size() - 1), current);
                continue;
            }

            int trim = getCornerTrim(previous, current, next);

            if (trim <= 0) {
                appendEightDirectionalLine(path, path.get(path.size() - 1), current);
                continue;
            }

            Vector beforeCorner = getPointBeforeCorner(previous, current, trim);
            Vector afterCorner = getPointAfterCorner(current, next, trim);

            if (sameBlock(beforeCorner, current) || sameBlock(afterCorner, current)) {
                appendEightDirectionalLine(path, path.get(path.size() - 1), current);
                continue;
            }

            appendEightDirectionalLine(path, path.get(path.size() - 1), beforeCorner);
            appendQuadraticCurve(path, beforeCorner, current, afterCorner);
        }

        appendEightDirectionalLine(path, path.get(path.size() - 1), controlPoints.get(controlPoints.size() - 1));

        return path;
    }

    private boolean shouldCurveCorner(Vector previous, Vector current, Vector next) {
        Vector incoming = getDirection(previous, current);
        Vector outgoing = getDirection(current, next);

        if (incoming == null || outgoing == null)
            return false;

        if (sameDirection(incoming, outgoing))
            return false;

        if (oppositeDirection(incoming, outgoing))
            return false;

        int incomingLength = getChebyshevDistance(previous, current);
        int outgoingLength = getChebyshevDistance(current, next);

        return incomingLength >= MIN_CORNER_DISTANCE && outgoingLength >= MIN_CORNER_DISTANCE;
    }

    private int getCornerTrim(Vector previous, Vector current, Vector next) {
        int incomingLength = getChebyshevDistance(previous, current);
        int outgoingLength = getChebyshevDistance(current, next);

        int shortest = Math.min(incomingLength, outgoingLength);

        if (shortest < MIN_CORNER_DISTANCE)
            return 0;

        return Math.max(1, Math.min(MAX_CORNER_TRIM, shortest / 3));
    }

    private Vector getPointBeforeCorner(Vector previous, Vector current, int trim) {
        List<Vector> incomingLine = createEightDirectionalLine(previous, current);

        int index = Math.max(0, incomingLine.size() - 1 - trim);

        return incomingLine.get(index);
    }

    private Vector getPointAfterCorner(Vector current, Vector next, int trim) {
        List<Vector> outgoingLine = createEightDirectionalLine(current, next);

        int index = Math.min(outgoingLine.size() - 1, trim);

        return outgoingLine.get(index);
    }

    private void appendQuadraticCurve(List<Vector> path, Vector start, Vector control, Vector end) {
        int firstDistance = getChebyshevDistance(start, control);
        int secondDistance = getChebyshevDistance(control, end);
        int samples = Math.max(6, (firstDistance + secondDistance) * 3);

        for (int sample = 1; sample <= samples; sample++) {
            double t = sample / (double) samples;

            double inverse = 1.0 - t;

            double x = inverse * inverse * start.getX()
                    + 2.0 * inverse * t * control.getX()
                    + t * t * end.getX();

            double y = inverse * inverse * start.getY()
                    + 2.0 * inverse * t * control.getY()
                    + t * t * end.getY();

            double z = inverse * inverse * start.getZ()
                    + 2.0 * inverse * t * control.getZ()
                    + t * t * end.getZ();

            Vector point = toBlockVector(new Vector(x, y, z));

            if (path.isEmpty()) {
                addPointIfNew(path, point);
                continue;
            }

            Vector last = path.get(path.size() - 1);

            if (getChebyshevDistance(last, point) <= 1) {
                addPointIfNew(path, point);
            } else {
                appendEightDirectionalLine(path, last, point);
            }
        }
    }

    private List<Vector> createEightDirectionalLine(Vector from, Vector to) {
        List<Vector> line = new ArrayList<>();
        appendEightDirectionalLine(line, from, to);
        return line;
    }

    private void appendEightDirectionalLine(List<Vector> path, Vector from, Vector to) {
        Vector start = toBlockVector(from);
        Vector end = toBlockVector(to);

        int startX = start.getBlockX();
        int startY = start.getBlockY();
        int startZ = start.getBlockZ();

        int endX = end.getBlockX();
        int endY = end.getBlockY();
        int endZ = end.getBlockZ();

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

    private List<Vector> removeImmediateBacktracking(List<Vector> path) {
        if (path.size() < 3)
            return path;

        List<Vector> cleaned = new ArrayList<>();

        for (Vector point : path) {
            addPointIfNew(cleaned, point);

            while (cleaned.size() >= 3) {
                Vector a = cleaned.get(cleaned.size() - 3);
                Vector b = cleaned.get(cleaned.size() - 2);
                Vector c = cleaned.get(cleaned.size() - 1);

                if (sameBlock(a, c)) {
                    cleaned.remove(cleaned.size() - 1);
                    cleaned.remove(cleaned.size() - 1);
                    continue;
                }

                break;
            }
        }

        return cleaned;
    }

    private List<Vector> removeOnlyConsecutiveDuplicates(List<Vector> path) {
        List<Vector> result = new ArrayList<>();

        for (Vector point : path)
            addPointIfNew(result, toBlockVector(point));

        return result;
    }

    private void addPointIfNew(List<Vector> path, Vector point) {
        Vector blockPoint = toBlockVector(point);

        if (path.isEmpty()) {
            path.add(blockPoint);
            return;
        }

        Vector last = path.get(path.size() - 1);

        if (!sameBlock(last, blockPoint))
            path.add(blockPoint);
    }

    private Vector getDirection(Vector from, Vector to) {
        int dx = Integer.compare(to.getBlockX() - from.getBlockX(), 0);
        int dz = Integer.compare(to.getBlockZ() - from.getBlockZ(), 0);

        if (dx == 0 && dz == 0)
            return null;

        return new Vector(dx, 0, dz);
    }

    private boolean sameDirection(Vector a, Vector b) {
        return a.getBlockX() == b.getBlockX()
                && a.getBlockZ() == b.getBlockZ();
    }

    private boolean oppositeDirection(Vector a, Vector b) {
        return a.getBlockX() == -b.getBlockX()
                && a.getBlockZ() == -b.getBlockZ();
    }

    private int getChebyshevDistance(Vector a, Vector b) {
        int dx = Math.abs(a.getBlockX() - b.getBlockX());
        int dz = Math.abs(a.getBlockZ() - b.getBlockZ());

        return Math.max(dx, dz);
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
}
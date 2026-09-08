package net.buildtheearth.buildteamtools.modules.generator.components.rail.generation;

import org.jspecify.annotations.Nullable;

record RailConnections(
        @Nullable Integer eastHeightDifference,
        @Nullable Integer westHeightDifference,
        @Nullable Integer southHeightDifference,
        @Nullable Integer northHeightDifference
) {

    boolean east() {
        return eastHeightDifference != null;
    }

    boolean west() {
        return westHeightDifference != null;
    }

    boolean south() {
        return southHeightDifference != null;
    }

    boolean north() {
        return northHeightDifference != null;
    }

    int connectionCount() {
        return (east() ? 1 : 0) + (west() ? 1 : 0) + (south() ? 1 : 0) + (north() ? 1 : 0);
    }
}

package net.buildtheearth.buildteamtools.modules.generator.components.rail.generation;

import java.util.List;

/** Stable spatial mixing: a repeated generation picks the same material at each position. */
final class RailMaterialPalette {
    private RailMaterialPalette() {
    }

    static <T> T select(List<T> materials, int x, int y, int z) {
        if (materials.isEmpty())
            throw new IllegalArgumentException("A material palette must not be empty.");
        int hash = x * 73428767 ^ y * 912931 ^ z * 438289;
        hash = (hash ^ (hash >>> 16)) * 0x45d9f3b;
        hash ^= hash >>> 16;
        return materials.get(Math.floorMod(hash, materials.size()));
    }

    static <T> T select(List<T> materials, PositionKey position) {
        return select(materials, position.x(), position.y(), position.z());
    }
}

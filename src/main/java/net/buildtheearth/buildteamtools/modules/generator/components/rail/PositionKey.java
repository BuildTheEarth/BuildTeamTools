package net.buildtheearth.buildteamtools.modules.generator.components.rail;

import org.bukkit.util.Vector;

record PositionKey(int x, int y, int z) {

    static PositionKey from(Vector vector) {
        return new PositionKey(
                vector.getBlockX(),
                vector.getBlockY(),
                vector.getBlockZ()
        );
    }

    static PositionKey of(int x, int y, int z) {
        return new PositionKey(x, y, z);
    }

    Vector toVector() {
        return new Vector(x, y, z);
    }
}

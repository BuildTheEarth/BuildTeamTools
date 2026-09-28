package net.buildtheearth.buildteamtools.modules.generator.components.rail.generation;

record RailColumnKey(int x, int z) {

    static RailColumnKey of(int x, int z) {
        return new RailColumnKey(x, z);
    }
}

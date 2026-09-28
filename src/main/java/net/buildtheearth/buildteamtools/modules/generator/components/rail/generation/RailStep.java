package net.buildtheearth.buildteamtools.modules.generator.components.rail.generation;

record RailStep(int dx, int dz) {

    RailStep opposite() {
        return new RailStep(-dx, -dz);
    }
}

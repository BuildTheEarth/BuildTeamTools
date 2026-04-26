package net.buildtheearth.buildteamtools.modules.navigation.components.warps;

import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.WarpMigrationSource;

import java.util.concurrent.CompletableFuture;

public class WarpMigrator {

    private final WarpMigrationSource source;

    public WarpMigrator(WarpMigrationSource source) {
        this.source = source;
    }

    public CompletableFuture<Void> migrate() {
        return CompletableFuture.runAsync(() -> {

        });
    }
}

package net.buildtheearth.buildteamtools.modules.navigation.components.warps;

import net.buildtheearth.buildteamtools.modules.navigation.components.warps.migrators.EssentialsWarpMigrator;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.migrators.IWarpMigrator;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.WarpMigrationSource;

import java.util.concurrent.CompletableFuture;

public class WarpMigrator {

    private final IWarpMigrator migrator;

    public WarpMigrator(WarpMigrationSource source) {
        this.migrator = switch (source) {
            case ESSENTIALS -> new EssentialsWarpMigrator();
        };
    }

    public CompletableFuture<Void> migrate() {
        return migrator.migrate();
    }
}

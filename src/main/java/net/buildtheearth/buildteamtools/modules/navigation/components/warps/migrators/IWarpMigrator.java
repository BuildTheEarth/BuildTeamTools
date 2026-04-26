package net.buildtheearth.buildteamtools.modules.navigation.components.warps.migrators;

import java.util.concurrent.CompletableFuture;

public interface IWarpMigrator {
    CompletableFuture<Void> migrate();
}

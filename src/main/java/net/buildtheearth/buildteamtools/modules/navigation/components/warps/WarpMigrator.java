package net.buildtheearth.buildteamtools.modules.navigation.components.warps;

import net.buildtheearth.buildteamtools.modules.navigation.components.warps.migrators.EssentialsWarpMigrator;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.migrators.IWarpMigrator;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.MigrationResult;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.WarpMigrationSource;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class WarpMigrator {

    private final IWarpMigrator migrator;

    @Contract(pure = true)
    public WarpMigrator(@NonNull WarpMigrationSource source) {
        this.migrator = switch (source) {
            case ESSENTIALS -> new EssentialsWarpMigrator();
        };
    }

    public CompletableFuture<MigrationResult> migrate(Player player) {
        return migrator.migrate(player);
    }
}

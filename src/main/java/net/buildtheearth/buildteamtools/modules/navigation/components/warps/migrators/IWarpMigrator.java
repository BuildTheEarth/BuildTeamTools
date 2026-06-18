package net.buildtheearth.buildteamtools.modules.navigation.components.warps.migrators;

import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.MigrationResult;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public interface IWarpMigrator {
    CompletableFuture<MigrationResult> migrate(Player player);
}

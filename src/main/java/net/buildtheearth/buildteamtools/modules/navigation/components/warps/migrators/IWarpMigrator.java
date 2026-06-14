package net.buildtheearth.buildteamtools.modules.navigation.components.warps.migrators;

import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public interface IWarpMigrator {
    CompletableFuture<Void> migrate(Player player);
}

package net.buildtheearth.buildteamtools.modules.navigation.components.warps.migrators;

import com.alpsbte.alpslib.utils.ChatHelper;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.Warps;
import net.buildtheearth.buildteamtools.modules.navigation.NavUtils;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.WarpsComponent;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.MigrationResult;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.WarpGroup;
import net.buildtheearth.buildteamtools.modules.network.NetworkModule;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class EssentialsWarpMigrator implements IWarpMigrator {
    @Override
    public CompletableFuture<MigrationResult> migrate(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            int migratedCount = 0;
            int failedCount = 0;
            
            try {
                Essentials essentials = JavaPlugin.getPlugin(Essentials.class);
                Warps warps = essentials.getWarps();

                for (String warp : warps.getList()) {
                    if (migrateIndividualWarp(warps, warp, player)) {
                        migratedCount++;
                    } else {
                        failedCount++;
                    }
                }

                return MigrationResult.success(migratedCount, failedCount);
            } catch (Exception e) {
                ChatHelper.logError(
                        """
                                An error occurred while migrating the essentials warps! (this probably means essentials isn't /installed)
                                 %s""", e);
                return MigrationResult.failure("Failed to access Essentials warps. Is Essentials installed? Error: " + e.getMessage());
            }
        });
    }

    private boolean migrateIndividualWarp(Warps warps, String warp, Player player) {
        try {
            WarpGroup group =
                    WarpsComponent.getOtherWarpGroup(Objects.requireNonNull(NetworkModule.getInstance().getBuildTeam()).getWarpGroups());
            if (group != null && group.getWarps().stream().anyMatch(w -> w.getName().equalsIgnoreCase(warp))) {
                ChatHelper.sendErrorMessage(player, "Warp '%s' already exists in the database, skipping migration.", warp);
                return false;
            }

            if (group == null) {
                group = NavUtils.createOtherWarpGroup(NetworkModule.getInstance().getBuildTeam());
            }
            WarpsComponent.createWarp(warps.getWarp(warp), warp, group, player);
            return true;
        } catch (Exception e) {
            ChatHelper.logError("Failed to migrate warp '%s': %s", warp, e.getMessage());
            return false;
        }
    }
}

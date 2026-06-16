package net.buildtheearth.buildteamtools.modules.navigation.components.warps.commands;

import com.alpsbte.alpslib.utils.ChatHelper;
import net.buildtheearth.buildteamtools.modules.navigation.NavigationModule;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.WarpMigrator;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.WarpsComponent;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.MigrationResult;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.Warp;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.WarpMigrationSource;
import net.buildtheearth.buildteamtools.modules.network.NetworkModule;
import net.buildtheearth.buildteamtools.modules.network.model.BuildTeam;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WarpCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatHelper.getErrorComponent("This command can only be used by a player!"));
            return true;
        }

        if (NetworkModule.getInstance().getBuildTeam() == null) {
            sender.sendMessage(ChatHelper.getErrorComponent("The Warp Module is currently disabled because the Build Team " +
                    "failed to load!"));
            return true;
        }

        // If no arguments were supplied assume the player wants to open the warp menu
        if (args.length == 0) {
            if (checkForWarpUsePermissionAndMessage(player)) WarpsComponent.openWarpMenu(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("create")) {
            return handleCreateCommand(player, args);
        }

        if (args[0].equalsIgnoreCase("migrate")) {
            return handleMigrateCommand(player, args);
        }

        return handleWarpTeleport(player, args);
    }

    private boolean handleCreateCommand(@NonNull Player player, String @NonNull [] args) {
        if (!player.hasPermission(Permissions.WARP_CREATE)) {
            player.sendMessage(ChatHelper.getErrorComponent("You don't have the required %s to %s warps.", "permission",
                    "create"));
            return true;
        }

        if (args.length > 1) {
            player.sendMessage(ChatHelper.getErrorComponent("Usage: /warp create"));
            return true;
        }

        player.sendActionBar(ChatHelper.getStandardComponent(false, "Creating the warp..."));
        WarpsComponent.createWarp(player);
        return true;
    }

    private boolean handleMigrateCommand(@NonNull Player player, String @NonNull [] args) {
        if (!player.hasPermission(Permissions.WARP_MIGRATE)) {
            player.sendMessage(ChatHelper.getErrorString("You don't have the required %s to %s warps.", "permission",
                    "migrate"));
            return true;
        }

        if (args.length != 2) {
            player.sendMessage(ChatHelper.getErrorComponent("Usage: /warp migrate <source>"));
            player.sendMessage(ChatHelper.getErrorComponent("Valid sources are: %s",
                    Arrays.toString(WarpMigrationSource.values())));
            return true;
        }

        WarpMigrationSource source = WarpMigrationSource.fromString(args[1].toLowerCase());
        if (source == null) {
            player.sendMessage(ChatHelper.getErrorComponent("Invalid source: %s", args[1]));
            player.sendMessage(ChatHelper.getErrorComponent("Valid sources are: %s",
                    Arrays.toString(WarpMigrationSource.values())));
            return true;
        }

        WarpMigrator migrator = new WarpMigrator(source);
        player.sendMessage(ChatHelper.getStandardComponent(true, "Migrating the warps..."));
        migrator.migrate(player).whenComplete((result, throwable) ->
                handleMigrationResult(player, result, throwable));
        return true;
    }

    private void handleMigrationResult(@NonNull Player player, @NonNull MigrationResult result, @Nullable Throwable throwable) {
        if (throwable != null) {
            player.sendMessage(ChatHelper.getErrorComponent("Something went wrong while migrating the warps: %s",
                    throwable.getMessage()));
            return;
        }

        if (!result.success()) {
            player.sendMessage(ChatHelper.getErrorComponent("Migration failed: %s", result.errorMessage()));
            return;
        }

        if (result.migratedCount() == 0 && result.failedCount() == 0) {
            player.sendMessage(ChatHelper.getStandardComponent(false, "No warps found to migrate."));
        } else if (result.failedCount() == 0) {
            player.sendMessage(ChatHelper.getSuccessComponent("Successfully migrated %d warp(s)!",
                    result.migratedCount()));
        } else if (result.migratedCount() == 0) {
            player.sendMessage(ChatHelper.getErrorComponent("Failed to migrate all %d warp(s)!", result.failedCount()));
        } else {
            player.sendMessage(ChatHelper.getStandardComponent(false, "Migration completed: %d warp(s) migrated, %d failed.",
                    result.migratedCount(), result.failedCount()));
        }
    }

    private boolean handleWarpTeleport(@NonNull Player player, String @NonNull [] args) {
        String key = String.join(" ", args);

        if (!checkForWarpUsePermissionAndMessage(player)) return true;

        Warp warp = NavigationModule.getInstance().getWarpsComponent().getWarpByName(key);

        if (warp == null) {
            player.sendMessage(ChatHelper.getErrorComponent("The warp with the name %s does not exist in this team!", key));
            return true;
        }

        NavigationModule.getInstance().getWarpsComponent().warpPlayer(player, warp);
        return true;
    }

    private static boolean checkForWarpUsePermissionAndMessage(@NonNull Player player) {
        if (!player.hasPermission(Permissions.WARP_USE)) {
            player.sendMessage(ChatHelper.getErrorComponent("You don't have the required %s to %s warps.", "permission", "use"));
            return false;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            BuildTeam buildTeam = NetworkModule.getInstance().getBuildTeam();
            if (buildTeam != null && buildTeam.getWarpGroups() != null) {
                return buildTeam.getWarpGroups().stream()
                        .flatMap(gr -> gr.getWarps().stream().map(Warp::getName))
                        .filter(s -> s.toLowerCase().startsWith(partial))
                        .toList();
            }
        }
        return Collections.emptyList();

    }
}

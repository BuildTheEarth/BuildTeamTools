package net.buildtheearth.buildteamtools.modules.navigation.components.warps.commands;

import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import net.buildtheearth.buildteamtools.i18n.Messages;
import net.buildtheearth.buildteamtools.modules.navigation.NavigationModule;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.WarpMigrator;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.WarpsComponent;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.MigrationResult;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.Warp;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.WarpMigrationSource;
import net.buildtheearth.buildteamtools.modules.network.NetworkModule;
import net.buildtheearth.buildteamtools.modules.network.model.BuildTeam;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import net.buildtheearth.buildteamtools.modules.common.commands.BttCommandManager;
import net.buildtheearth.buildteamtools.modules.common.metrics.FeatureUsage;
import net.buildtheearth.buildteamtools.modules.common.metrics.UsageDetail;
import net.buildtheearth.buildteamtools.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.Module;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import org.incendo.cloud.parser.standard.EnumParser;
import org.incendo.cloud.minecraft.extras.RichDescription;

import java.util.ArrayList;
import java.util.List;

public class WarpCommand {

    public void register(Module owner) {
        var commandManager = BuildTeamTools.getInstance().getCommandManager();
        commandManager.register(owner, "warp", Permissions.WARP_USE, this::execute, this::suggestions);
        commandManager.registerSubcommand(owner, "warp", "create", "Create a warp at your current location.",
                builder -> builder.permission(Permissions.WARP_CREATE),
                context -> {
                    CommandSender sender = context.sender().getSender();
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage(Messages.playerOnly());
                    } else if (checkWarpModule(sender)) {
                        handleCreateCommand(player);
                    }
                });
        commandManager.registerSubcommand(owner, "warp", "migrate", "Migrate warps from another warp provider.",
                builder -> builder.permission(Permissions.WARP_MIGRATE)
                        .required("source", EnumParser.enumParser(WarpMigrationSource.class),
                                RichDescription.of(Component.text("Warp migration provider"))),
                context -> {
                    CommandSender sender = context.sender().getSender();
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage(Messages.playerOnly());
                    } else if (checkWarpModule(sender)) {
                        handleMigrateCommand(player, context.get("source"));
                    }
                });
        commandManager.registerSubcommand(owner, "warp", "random", "Warp to a random build team warp.",
                builder -> builder.permission(Permissions.WARP_RANDOM),
                context -> {
                    CommandSender sender = context.sender().getSender();
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage(Messages.playerOnly());
                    } else if (checkWarpModule(sender)) {
                        handleRandomWarpCommand(player);
                    }
                });
    }

    private void execute(@NonNull CommandSender sender, String @NonNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Messages.playerOnly());
            return;
        }

        if (!checkWarpModule(sender)) return;

        // If no arguments were supplied assume the player wants to open the warp menu
        if (args.length == 0) {
            if (checkForWarpUsePermissionAndMessage(player)) WarpsComponent.openWarpMenu(player);
            return;
        }

        handleWarpTeleport(player, args);
    }

    private boolean checkWarpModule(CommandSender sender) {
        if (NetworkModule.getInstance().getBuildTeam() != null) return true;
        sender.sendMessage(Messages.component(MessageKeys.Warp.MODULE_DISABLED));
        return false;
    }

    private void handleCreateCommand(@NonNull Player player) {
        if (!player.hasPermission(Permissions.WARP_CREATE)) {
            player.sendMessage(Messages.component(
                    MessageKeys.Warp.NO_PERMISSION_ACTION,
                    Component.text("create")));
            return;
        }

        player.sendActionBar(Messages.component(MessageKeys.Warp.CREATING));
        WarpsComponent.createWarp(player);
    }

    private void handleMigrateCommand(@NonNull Player player, WarpMigrationSource source) {
        if (!player.hasPermission(Permissions.WARP_MIGRATE)) {
            player.sendMessage(Messages.component(
                    MessageKeys.Warp.NO_PERMISSION_ACTION,
                    Component.text("migrate")));
            return;
        }

        FeatureUsage.record(UsageDetail.WARPS_MIGRATE);
        WarpMigrator migrator = new WarpMigrator(source);
        player.sendMessage(Messages.component(MessageKeys.Warp.MIGRATING));
        migrator.migrate(player).whenComplete((result, throwable) ->
                handleMigrationResult(player, result, throwable));
    }

    private void handleRandomWarpCommand(@NonNull Player player) {

        if (!player.hasPermission(Permissions.WARP_RANDOM)) {
            Permissions.sendNoPermissionMessage(player, Permissions.WARP_RANDOM);
            return;
        }

        BuildTeam buildTeam = NetworkModule.getInstance().getBuildTeam();
        if (buildTeam == null) {
            return;
        }

        List<Warp> warps = buildTeam.getWarpGroups().stream()
                .flatMap(group -> group.getWarps().stream())
                .toList();

        Warp warp = Utils.pickRandom(warps);
        if (warp == null) {
            player.sendMessage(Messages.component(MessageKeys.Warp.NO_WARP_FOUND));
            return;
        }

        NavigationModule.getInstance().getWarpsComponent().warpPlayer(player, warp);
    }

    private void handleMigrationResult(@NonNull Player player, @NonNull MigrationResult result, @Nullable Throwable throwable) {
        if (throwable != null) {
            player.sendMessage(Messages.component(
                    MessageKeys.Warp.MIGRATE_ERROR,
                    Component.text(String.valueOf(throwable.getMessage()))));
            return;
        }

        if (!result.success()) {
            player.sendMessage(Messages.component(
                    MessageKeys.Warp.MIGRATE_FAILED,
                    Component.text(String.valueOf(result.errorMessage()))));
            return;
        }

        if (result.migratedCount() == 0 && result.failedCount() == 0) {
            player.sendMessage(Messages.component(MessageKeys.Warp.MIGRATE_NONE));
        } else if (result.failedCount() == 0) {
            player.sendMessage(Messages.component(
                    MessageKeys.Warp.MIGRATE_SUCCESS,
                    Component.text(String.valueOf(result.migratedCount()))));
        } else if (result.migratedCount() == 0) {
            player.sendMessage(Messages.component(
                    MessageKeys.Warp.MIGRATE_ALL_FAILED,
                    Component.text(String.valueOf(result.failedCount()))));
        } else {
            player.sendMessage(Messages.component(
                    MessageKeys.Warp.MIGRATE_PARTIAL,
                    Component.text(String.valueOf(result.migratedCount())),
                    Component.text(String.valueOf(result.failedCount()))));
        }
    }

    private void handleWarpTeleport(@NonNull Player player, String @NonNull [] args) {
        String key = String.join(" ", args);

        if (!checkForWarpUsePermissionAndMessage(player)) return;

        Warp warp = NavigationModule.getInstance().getWarpsComponent().getWarpByName(key);

        if (warp == null) {
            player.sendMessage(Messages.component(MessageKeys.Warp.NOT_EXISTS, Component.text(key)));
            return;
        }

        NavigationModule.getInstance().getWarpsComponent().warpPlayer(player, warp);
    }

    private static boolean checkForWarpUsePermissionAndMessage(@NonNull Player player) {
        if (!player.hasPermission(Permissions.WARP_USE)) {
            player.sendMessage(Messages.component(
                    MessageKeys.Warp.NO_PERMISSION_ACTION,
                    Component.text("use")));
            return false;
        }
        return true;
    }

    private List<String> suggestions(CommandSender sender, String input) {
        String[] arguments = input.trim().split("\\s+");
        if (input.isBlank() || arguments.length == 1) {
            List<String> options = new ArrayList<>();
            if (sender.hasPermission(Permissions.WARP_CREATE)) options.add("create");
            if (sender.hasPermission(Permissions.WARP_MIGRATE)) options.add("migrate");
            if (sender.hasPermission(Permissions.WARP_RANDOM)) options.add("random");
            BuildTeam buildTeam = NetworkModule.getInstance().getBuildTeam();
            if (buildTeam != null && buildTeam.getWarpGroups() != null)
                buildTeam.getWarpGroups().stream().flatMap(group -> group.getWarps().stream())
                        .map(Warp::getName).forEach(options::add);
            return BttCommandManager.matchingSuggestions(options, input);
        }
        return List.of();
    }

}

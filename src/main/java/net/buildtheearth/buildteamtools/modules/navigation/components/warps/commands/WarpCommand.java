package net.buildtheearth.buildteamtools.modules.navigation.components.warps.commands;

import com.alpsbte.alpslib.utils.ChatHelper;
import net.buildtheearth.buildteamtools.modules.navigation.NavigationModule;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.WarpsComponent;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.Warp;
import net.buildtheearth.buildteamtools.modules.network.NetworkModule;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class WarpCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatHelper.getErrorString("This command can only be used by a player!"));
            return true;
        }

        // Check if the build team is loaded
        if(NetworkModule.getInstance().getBuildTeam() == null){
            sender.sendMessage(ChatHelper.getErrorString("The Warp Module is currently disabled because the Build Team failed to load!"));
            return true;
        }

        // If no arguments were supplied assume the player wants to open the warp menu
        if (args.length == 0) {
            if (checkForWarpUsePermissionAndMessage(player)) WarpsComponent.openWarpMenu(player);
            return true;
        }

        // WARP CREATE
        if (args[0].equalsIgnoreCase("create")) {
            // Check if the player has the required permissions
            if (!player.hasPermission(Permissions.WARP_CREATE)) {
                player.sendMessage(ChatHelper.getErrorString("You don't have the required %s to %s warps.", "permission", "create"));
                return true;
            }

            // Check if the command has only one argument
            if (args.length > 1) {
                player.sendMessage(ChatHelper.getErrorString("Usage: /warp create"));
                return true;
            }

            player.sendActionBar(ChatHelper.getStandardString(false, "Creating the warp..."));

            NavigationModule.getInstance().getWarpsComponent().createWarp(player);
            return true;
        }


        // Combine the args to one warp name
        String key = String.join(" ", args);

        if (!checkForWarpUsePermissionAndMessage(player)) return true;

        // Find the warp with the given key
        Warp warp = NavigationModule.getInstance().getWarpsComponent().getWarpByName(key);

        if(warp == null) {
            player.sendMessage(ChatHelper.getErrorString("The warp with the name %s does not exist in this team!", key));
            return true;
        }

        NavigationModule.getInstance().getWarpsComponent().warpPlayer(player, warp);

        return true;
    }

    private static boolean checkForWarpUsePermissionAndMessage(@NotNull Player player) {
        // Check if the player has the required permission
        if (!player.hasPermission(Permissions.WARP_USE)) {
            player.sendMessage(ChatHelper.getErrorString("You don't have the required %s to %s warps.", "permission", "use"));
            return false;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            return NetworkModule.getInstance().getBuildTeam().getWarpGroups().stream()
                    .flatMap(gr -> gr.getWarps().stream().map(Warp::getName))
                    .filter(s -> s.toLowerCase().startsWith(partial))
                    .toList();
        }
        return Collections.emptyList();

    }
}

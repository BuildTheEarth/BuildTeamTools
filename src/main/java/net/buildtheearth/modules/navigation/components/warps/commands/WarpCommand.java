package net.buildtheearth.modules.navigation.components.warps.commands;

import net.buildtheearth.modules.navigation.NavigationModule;
import net.buildtheearth.modules.navigation.components.warps.menu.WarpGroupMenu;
import net.buildtheearth.modules.navigation.components.warps.menu.WarpMenu;
import net.buildtheearth.modules.navigation.components.warps.model.Warp;
import net.buildtheearth.modules.network.NetworkModule;
import net.buildtheearth.modules.network.model.Permissions;
import net.buildtheearth.utils.ChatUtil;
import net.buildtheearth.utils.lang.LangPaths;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            ChatUtil.sendError(sender, LangPaths.ERROR.NO_PLAYER);
            return true;
        }

        // Check if the build team is loaded
        if(NetworkModule.getInstance().getBuildTeam() == null){
            sender.sendMessage(ChatUtil.getErrorString("The Warp Module is currently disabled because the Build Team failed to load!"));
            return true;
        }

        Player player = (Player) sender;

        // If no arguments were supplied assume the player wants to open the warp menu
        if (args.length == 0) {
            int warpGroupCount = NetworkModule.getInstance().getBuildTeam().getWarpGroups().size();

            if(warpGroupCount == 0){
                player.sendMessage(ChatUtil.getErrorString("This server does not have any warps yet!"));
                return true;
            }else if(warpGroupCount == 1)
                new WarpMenu(player, NetworkModule.getInstance().getBuildTeam().getWarpGroups().get(0), false, true);
            else
                new WarpGroupMenu(player, NetworkModule.getInstance().getBuildTeam(), false, true);

            return true;
        }

        // WARP CREATE
        if (args[0].equalsIgnoreCase("create")) {
            // Check if the command has only one argument
            if (args.length > 1){
                player.sendMessage(ChatUtil.getErrorString("Usage: /warp create"));
                return true;
            }

            // Check if the player has the required permissions
            if (!player.hasPermission(Permissions.WARP_CREATE)) {
                player.sendMessage(ChatUtil.getErrorString("You don't have the required %s to %s warps.", "permission", "create"));
                return true;
            }

            player.sendActionBar(ChatUtil.getStandardString(false, "Creating the warp..."));

            NavigationModule.getInstance().getWarpsComponent().createWarp(player);
            return true;
        }


        // Combine the args to one warp name
        String key = String.join(" ", args);

        // Check if the player has the required permission
        if (!player.hasPermission(Permissions.WARP_USE)) {
            player.sendMessage(ChatUtil.getErrorString("You don't have the required %s to %s warps.", "permission", "use"));
            return true;
        }

        // Find the warp with the given key
        Warp warp = NavigationModule.getInstance().getWarpsComponent().getWarpByName(key);

        if(warp == null) {
            player.sendMessage(ChatUtil.getErrorString("The warp with the name %s does not exist in this team!", key));
            return true;
        }

        NavigationModule.getInstance().getWarpsComponent().warpPlayer(player, warp);

        return true;
    }
}

package net.buildtheearth.modules.warp.commands;

import net.buildtheearth.Main;
import net.buildtheearth.modules.network.model.Permissions;
import net.buildtheearth.modules.utils.ChatHelper;
import net.buildtheearth.modules.warp.WarpModule;
import net.buildtheearth.modules.warp.menu.WarpGroupMenu;
import net.buildtheearth.modules.warp.menu.WarpMenu;
import net.buildtheearth.modules.warp.model.Warp;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatHelper.error("This command can only be used by a player!"));
            return true;
        }

        // Check if the build team is loaded
        if(Main.getBuildTeamTools().getProxyModule().getBuildTeam() == null){
            sender.sendMessage(ChatHelper.error("The Warp Module is currently disabled because the Build Team failed to load!"));
            return true;
        }

        Player player = (Player) sender;

        // If no arguments were supplied assume the player wants to open the warp menu
        if (args.length == 0) {
            int warpGroupCount = Main.getBuildTeamTools().getProxyModule().getBuildTeam().getWarpGroups().size();

            if(warpGroupCount == 0){
                player.sendMessage(ChatHelper.error("This server does not have any warps yet!"));
                return true;
            }else if(warpGroupCount == 1)
                new WarpMenu(player, Main.getBuildTeamTools().getProxyModule().getBuildTeam().getWarpGroups().get(0), false);
            else
                new WarpGroupMenu(player, Main.getBuildTeamTools().getProxyModule().getBuildTeam(), false);

            return true;
        }

        // WARP CREATE
        if (args[0].equalsIgnoreCase("create")) {
            // Check if the command has only one argument
            if (args.length > 1){
                player.sendMessage(ChatHelper.error("Usage: /warp create"));
                return true;
            }

            // Check if the player has the required permissions
            if (!player.hasPermission(Permissions.WARP_CREATE)) {
                player.sendMessage(ChatHelper.error("You don't have the required %s to %s warps.", "permission", "create"));
                return true;
            }

            player.sendActionBar(ChatHelper.standard(false, "Creating the warp..."));

            WarpModule.createWarp(player);
            return true;
        }


        // Combine the args to one warp name
        String key = String.join(" ", args);

        // Check if the player has the required permission
        if (!player.hasPermission(Permissions.WARP_USE)) {
            player.sendMessage(ChatHelper.error("You don't have the required %s to %s warps.", "permission", "use"));
            return true;
        }

        // Find the warp with the given key
        Warp warp = WarpModule.getWarpByName(key);

        if(warp == null) {
            player.sendMessage(ChatHelper.error("The warp with the name %s does not exist in this team!", key));
            return true;
        }

        WarpModule.warpPlayer(player, warp);

        return true;
    }
}

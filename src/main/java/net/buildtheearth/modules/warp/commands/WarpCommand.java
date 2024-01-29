package net.buildtheearth.modules.warp.commands;

import net.buildtheearth.Main;
import net.buildtheearth.modules.network.api.OpenStreetMapAPI;
import net.buildtheearth.modules.utils.ChatHelper;
import net.buildtheearth.modules.utils.geo.CoordinateConversion;
import net.buildtheearth.modules.warp.WarpManager;
import net.buildtheearth.modules.warp.menu.WarpGroupMenu;
import net.buildtheearth.modules.warp.menu.WarpMenu;
import net.buildtheearth.modules.warp.menu.WarpEditMenu;
import net.buildtheearth.modules.warp.model.Warp;
import net.buildtheearth.modules.warp.model.WarpGroup;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class WarpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatHelper.error("This command can only be used by a player!"));
            return true;
        }

        // Check if the build team is loaded
        if(Main.getBuildTeamTools().getProxyManager().getBuildTeam() == null){
            sender.sendMessage(ChatHelper.error("The Warp Module is currently disabled because the Build Team failed to load!"));
            return true;
        }

        Player player = (Player) sender;

        // If no arguments were supplied assume the player wants to open the warp menu
        if (args.length == 0) {
            int warpGroupCount = Main.getBuildTeamTools().getProxyManager().getBuildTeam().getWarpGroups().size();

            if(warpGroupCount == 0){
                player.sendMessage(ChatHelper.error("This server does not have any warps yet!"));
                return true;
            }else if(warpGroupCount == 1)
                new WarpMenu(player, Main.getBuildTeamTools().getProxyManager().getBuildTeam().getWarpGroups().get(0), false);
            else
                new WarpGroupMenu(player, Main.getBuildTeamTools().getProxyManager().getBuildTeam(), false);

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
            if (!player.hasPermission("btt.warp.create")) {
                player.sendMessage(ChatHelper.error("You don't have the required %s to %s warps.", "permission", "create"));
                return true;
            }

            player.sendActionBar(ChatHelper.standard(false, "Creating the warp..."));

            // Get the geographic coordinates of the player's location.
            Location location = player.getLocation();
            double[] coordinates = CoordinateConversion.convertToGeo(location.getX(), location.getZ());

            //Get the country belonging to the coordinates
            CompletableFuture<String[]> future = OpenStreetMapAPI.getCountryFromLocationAsync(coordinates);

            future.thenAccept(result -> {
                String regionName = result[0];
                String countryCodeCCA2 = result[1].toUpperCase();

                //Check if the team owns this region/country
                boolean ownsRegion = Main.getBuildTeamTools().getProxyManager().ownsRegion(regionName, countryCodeCCA2);

                if(!ownsRegion) {
                    player.sendMessage(ChatHelper.error("This team does not own the country %s!", result[0]));
                    return;
                }

                // Get the Other Group for default warp group
                WarpGroup group = Main.getBuildTeamTools().getProxyManager().getBuildTeam().getWarpGroups().stream().filter(warpGroup -> warpGroup.getName().equalsIgnoreCase("Other")).findFirst().orElse(null);

                // Create a default name for the warp
                String name = player.getName() + "'s Warp";

                // Create an instance of the warp POJO
                Warp warp = new Warp(group, name, countryCodeCCA2, "cca2", null, location.getWorld().getName(), coordinates[0], coordinates[1], location.getY(), location.getYaw(), location.getPitch(), false);

                // Create the actual warp
                new WarpEditMenu(player, warp, false);

            }).exceptionally(e -> {
                player.sendMessage(ChatHelper.error("An error occurred while creating the warp!"));
                e.printStackTrace();
                return null;
            });
            return true;
        }


        //WARP DELETE <KEY>
        if (args[0].equalsIgnoreCase("delete")) {
            // Check if the required arguments are provided & send usage otherwise
            if (args.length != 2) return false;

            // Extract the required data from the command arguments
            String key = args[1];

            // Check if the user has the required permission
            if (!player.hasPermission("btt.warp.delete")) {
                player.sendMessage(ChatHelper.error("You don't have the required %s to %s warps.", "permission", "delete"));
                return true;
            }

            // Find the warp with the given key
            Warp warp = WarpManager.getWarpByName(key);

            if(warp == null) {
                player.sendMessage(ChatHelper.error("The warp with the name %s does not exist in this team!", key));
                return true;
            }

            Main.buildTeamTools.getProxyManager().getBuildTeam().deleteWarp(player, warp);
            return true;
        }


        if (args.length >= 1) {

            // Combine the args to one warp name
            String key = String.join(" ", args);

            // Check if the player has the required permission
            if (!player.hasPermission("btt.warp.use")) {
                player.sendMessage(ChatHelper.error("You don't have the required %s to %s warps.", "permission", "use"));
                return true;
            }

            // Find the warp with the given key
            Warp warp = WarpManager.getWarpByName(key);

            if(warp == null) {
                player.sendMessage(ChatHelper.error("The warp with the name %s does not exist in this team!", key));
                return true;
            }

            WarpManager.warpPlayer(player, warp);

            return true;
        }

        return false;
    }
}

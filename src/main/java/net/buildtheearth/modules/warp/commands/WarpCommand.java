package net.buildtheearth.modules.warp.commands;

import net.buildtheearth.Main;
import net.buildtheearth.modules.network.ProxyManager;
import net.buildtheearth.modules.network.api.NetworkAPI;
import net.buildtheearth.modules.network.api.OpenStreetMapAPI;
import net.buildtheearth.modules.network.model.Region;
import net.buildtheearth.modules.utils.ChatHelper;
import net.buildtheearth.modules.utils.geo.projection.Airocean;
import net.buildtheearth.modules.utils.geo.projection.ModifiedAirocean;
import net.buildtheearth.modules.utils.io.ConfigPaths;
import net.buildtheearth.modules.warp.WarpManager;
import net.buildtheearth.modules.warp.menu.WarpMainMenu;
import net.buildtheearth.modules.warp.model.Warp;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class WarpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatHelper.highlight("This command can only be used by a player!"));
            return true;
        }

        Player player = (Player) sender;

        // If no arguments were supplied assume the player wants to open the warp menu
        if (args == null) {
            new WarpMainMenu(player, null);
            return true;
        }

        // WARP CREATE <KEY>
        if (args[0].equalsIgnoreCase("create")) {
            // Check if the required arguments are provided & send usage otherwise
            if (args.length < 2 || args.length > 3) return false;

            // Extract the required data from the arguments
            String key = args[1];
            Location location = player.getLocation();
            boolean highlight = false;
            if (args.length == 3) {
                highlight = Boolean.parseBoolean(args[2]);
            }

            // Check if the player has the required permissions
            if (!player.hasPermission("btt.warp.create")) {
                player.sendMessage(ChatHelper.highlight("You don't have the required %s to %s warps.", "permission", "create"));
                return true;
            }

            // Get the geographic coordinates of the player's location.
            Airocean projection = new ModifiedAirocean();
            double[] coordinates = projection.toGeo(location.getX(), location.getY());

            //Get the country, region & city belonging to the coordinates.
            String[] locationInfo = new String[0];
            try {
                locationInfo = OpenStreetMapAPI.getCountryAndSubRegionsFromLocationAsync(coordinates).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            if (locationInfo == null) {
                player.sendMessage(ChatHelper.highlight("Failed to get the location of this warp."));
                return true;
            }

            //Get the owned regions of this team
            List<Region> ownedRegions = Main.getBuildTeamTools().getProxyManager().getBuildTeam().getRegions();

            //Check if the team owns the country they are trying to create a warp in.
            String[] finalLocationInfo = locationInfo;
            boolean ownsRegion = ownedRegions.stream().anyMatch(region -> region.getCountryCodeCca2().equals(finalLocationInfo[3]));

            if (!ownsRegion) {
                player.sendMessage(ChatHelper.highlight("This team does not own the country %s!", locationInfo[0]));
                return true;
            }

            // Create an instance of the warp POJO
            Warp warp = new Warp(
                    key,
                    finalLocationInfo[3],
                    finalLocationInfo[1],
                    finalLocationInfo[2],
                    location.getWorld().getName(),
                    coordinates[0],
                    coordinates[1],
                    location.getY(),
                    location.getPitch(),
                    location.getYaw(),
                    highlight
            );

            NetworkAPI.createWarp(warp);
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
                player.sendMessage(ChatHelper.highlight("You don't have the required %s to %s warps.", "permission", "delete"));
                return true;
            }

            // TODO Remove the warp from the database using a DELETE request


            return true;
        }


        if (args.length == 1) {

            // Extract the required data from the command arguments
            String key = args[0];

            // Check if the player has the required permission
            if (!player.hasPermission("btt.warp.use")) {
                player.sendMessage(ChatHelper.highlight("You don't have the required %s to %s warps.", "permission", "use"));
                return true;
            }

            WarpManager.warpPlayer(player, key);

            return true;
        }

        return false;
    }
}

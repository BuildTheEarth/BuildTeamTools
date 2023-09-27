package net.buildtheearth.buildteam.components.warps.commands;

import net.buildtheearth.Main;
import net.buildtheearth.buildteam.NetworkAPI;
import net.buildtheearth.buildteam.components.universal.universal_tpll.projection.Airocean;
import net.buildtheearth.buildteam.components.universal.universal_tpll.projection.ModifiedAirocean;
import net.buildtheearth.buildteam.components.warps.model.Warp;
import net.buildtheearth.utils.ChatHelper;
import net.buildtheearth.utils.geo.OpenStreetMapAPI;
import net.buildtheearth.utils.io.ConfigPaths;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

public class WarpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatHelper.highlight("This command can only be used by a player!"));
            return true;
        }

        Player player = (Player) sender;

        // If no arguments were supplied assume the player wants to open the warp menu
        if(args == null) {
            //TODO OPEN WARP MENU
            return true;
        }

        // WARP CREATE <KEY>
        if(args[0].equalsIgnoreCase("create")) {
            // Check if the required arguments are provided & send usage otherwise
            if(args.length < 2 || args.length > 3) return false;

            // Extract the required data from the arguments
            String key = args[1];
            Location location = player.getLocation();
            boolean highlight = false;
            if(args.length == 3) {
                highlight = Boolean.parseBoolean(args[2]);
            }

            // Check if the player has the required permissions
            if(!player.hasPermission("btt.warp.create")) {
                player.sendMessage(ChatHelper.highlight("You don't have the required %s to %s warps.", "permission", "create"));
                return true;
            }

            // Get the geographic coordinates of the player's location.
            Airocean projection = new ModifiedAirocean();
            double[] coordinates = projection.toGeo(location.getX(), location.getY());

            //Get the country, region & city belonging to the coordinates.
            String[] locationInfo = OpenStreetMapAPI.getCountryAndSubRegionsFromLocation(coordinates);
            if(locationInfo == null) {
                player.sendMessage(ChatHelper.highlight("Failed to get the location of this warp."));
                return true;
            }

            // Perform a GET request to get the owned countries of this team
            Collection<String> ownedCountries = NetworkAPI.getCountryCodesByKey(Main.instance.getConfig().getString(ConfigPaths.API_KEY));

            // Check if the that list contains the country the warp is in, else return
            if(ownedCountries == null || locationInfo[0] == null || !ownedCountries.contains(locationInfo[0])) {
                player.sendMessage(ChatHelper.highlight("This team does not own the country %s!", locationInfo[0]));
                return true;
            }

            // Create an instance of the warp POJO
            Warp warp = new Warp(
                    key,
                    locationInfo[0],
                    locationInfo[1],
                    locationInfo[2],
                    coordinates[0],
                    coordinates[1],
                    location.getY(),
                    location.getPitch(),
                    location.getYaw(),
                    highlight
            );

            // TODO Add the warp to the database using a POST request


            return true;
        }




        //WARP DELETE <KEY>
        if(args[0].equalsIgnoreCase("delete")) {
            // Check if the required arguments are provided & send usage otherwise
            if(args.length != 2) return false;

            // Extract the required data from the command arguments
            String key = args[1];

            // Check if the user has the required permission
            if(!player.hasPermission("btt.warp.delete")) {
                player.sendMessage(ChatHelper.highlight("You don't have the required %s to %s warps.", "permission", "delete"));
                return true;
            }

            // TODO Remove the warp from the database using a DELETE request


            return true;
        }


        if(args.length == 1) {

            // Extract the required data from the command arguments
            String key = args[0];

            // Check if the player has the required permission
            if(!player.hasPermission("btt.warp.use")) {
                player.sendMessage(ChatHelper.highlight("You don't have the required %s to %s warps.", "permission", "use"));
                return true;
            }

            // TODO Perform a GET request to get the information of this warp


            // TODO Warp the player to this location


            return true;
        }

        return false;
    }
}

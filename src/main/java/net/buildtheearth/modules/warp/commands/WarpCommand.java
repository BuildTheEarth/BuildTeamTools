package net.buildtheearth.modules.warp.commands;

import net.buildtheearth.Main;
import net.buildtheearth.modules.network.api.API;
import net.buildtheearth.modules.network.api.NetworkAPI;
import net.buildtheearth.modules.network.api.OpenStreetMapAPI;
import net.buildtheearth.modules.utils.ChatHelper;
import net.buildtheearth.modules.utils.geo.CoordinateConversion;
import net.buildtheearth.modules.warp.WarpManager;
import net.buildtheearth.modules.warp.menu.WarpMainMenu;
import net.buildtheearth.modules.warp.model.Warp;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

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

        // WARP CREATE <KEY> <HIGHLIGHT> <GROUP>
        if (args[0].equalsIgnoreCase("create")) {
            // Check if the required arguments are provided & send usage otherwise
            if (args.length < 2) return false;

            // Extract the required data from the arguments
            String key = args[1];
            Location location = player.getLocation();

            boolean highlight = false;
            String group = null;
            if (args.length >= 3) {
                if(args[2].equals("true") || args[2].equals("false")) {
                    highlight = Boolean.parseBoolean(args[2]);
                } else {
                    group = args[2];
                    if(args.length >= 4) {
                        highlight = Boolean.parseBoolean(args[3]);
                    }
                }
            }

            // Check if the player has the required permissions
            if (!player.hasPermission("btt.warp.create")) {
                player.sendMessage(ChatHelper.highlight("You don't have the required %s to %s warps.", "permission", "create"));
                return true;
            }

            // Get the geographic coordinates of the player's location.
            double[] coordinates = CoordinateConversion.convertToGeo(location.getX(), location.getZ());

            //Get the country belonging to the coordinates
            String finalGroup = group;
            boolean finalHighlight = highlight;
            OpenStreetMapAPI.getCountryFromLocationAsync(coordinates).whenComplete((result, throwable) -> {
                if(throwable != null) {
                    player.sendMessage(ChatHelper.highlight("Failed to get the country belonging to your location."));
                    return;
                }

                //Check if the team owns this region/country
                boolean ownsRegion = Main.getBuildTeamTools().getProxyManager().ownsRegion(result[0], result[1]);

                if(!ownsRegion) {
                    player.sendMessage(ChatHelper.highlight("This team does not own the country %s!", result[0]));
                    return;
                }

                // Create an instance of the warp POJO
                Warp warp = new Warp(key, result[1], "cca2", finalGroup == null ? "null" : finalGroup, location.getWorld().getName(), coordinates[0], coordinates[1], location.getY(), location.getYaw(), location.getPitch(), finalHighlight);

                // Create the actual warp
                NetworkAPI.createWarp(warp, new API.ApiResponseCallback() {
                    @Override
                    public void onResponse(String response) {
                        player.sendMessage(ChatHelper.successful("Successfully created the warp %s!", key));
                    }

                    @Override
                    public void onFailure(IOException e) {
                        player.sendMessage(ChatHelper.highlight("Something went wrong while creating the warp %s!", key));
                    }
                });
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
                player.sendMessage(ChatHelper.highlight("You don't have the required %s to %s warps.", "permission", "delete"));
                return true;
            }

            //TODO CHECK IF THE TEAM OWNS THE WARP WITH THE GIVEN KEY

            NetworkAPI.deleteWarp(key, new API.ApiResponseCallback() {
                @Override
                public void onResponse(String response) {
                    player.sendMessage(ChatHelper.successful("Successfully deleted the warp %s!", key));
                }

                @Override
                public void onFailure(IOException e) {
                    player.sendMessage(ChatHelper.highlight("Something went wrong while deleting the warp %s!", key));
                }
            });
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

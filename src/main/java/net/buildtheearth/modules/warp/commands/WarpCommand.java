package net.buildtheearth.modules.warp.commands;

import net.buildtheearth.Main;
import net.buildtheearth.modules.network.api.API;
import net.buildtheearth.modules.network.api.NetworkAPI;
import net.buildtheearth.modules.network.api.OpenStreetMapAPI;
import net.buildtheearth.modules.utils.ChatHelper;
import net.buildtheearth.modules.utils.geo.CoordinateConversion;
import net.buildtheearth.modules.warp.WarpManager;
import net.buildtheearth.modules.warp.menu.WarpGroupMenu;
import net.buildtheearth.modules.warp.menu.WarpMenu;
import net.buildtheearth.modules.warp.model.Warp;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.UUID;
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

        // WARP CREATE <NAME> <HIGHLIGHT> <GROUP ID>
        if (args[0].equalsIgnoreCase("create")) {
            // Check if the required arguments are provided & send usage otherwise
            if (args.length < 2){
                player.sendMessage(ChatHelper.error("Usage: /warp create <name> [highlight] [group_id]"));
                return true;
            }

            // Check if the player has the required permissions
            if (!player.hasPermission("btt.warp.create")) {
                player.sendMessage(ChatHelper.error("You don't have the required %s to %s warps.", "permission", "create"));
                return true;
            }

            // Extract the required data from the arguments
            String name = args[1];
            Location location = player.getLocation();

            boolean highlight = false;
            UUID groupID = null;
            if (args.length >= 3) {
                if(args[2].equals("true") || args[2].equals("false")) {
                    highlight = Boolean.parseBoolean(args[2]);
                } else {
                    groupID = Main.getBuildTeamTools().getProxyManager().getBuildTeam().getWarpGroups().stream().filter(warpGroup -> warpGroup.getId().equals(UUID.fromString(args[2]))).findFirst().orElse(null).getId();
                    if(args.length >= 4) {
                        highlight = Boolean.parseBoolean(args[3]);
                    }
                }
            }

            // Send the player a message that the warp is being created
            player.sendMessage(ChatHelper.standard("Creating the warp %s...", name));

            // Get the geographic coordinates of the player's location.
            double[] coordinates = CoordinateConversion.convertToGeo(location.getX(), location.getZ());

            //Get the country belonging to the coordinates
            UUID finalGroup = groupID;
            boolean finalHighlight = highlight;
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

                // Create an instance of the warp POJO
                Warp warp = new Warp(finalGroup, name, countryCodeCCA2, "cca2", null, location.getWorld().getName(), coordinates[0], coordinates[1], location.getY(), location.getYaw(), location.getPitch(), finalHighlight);

                // Create the actual warp
                NetworkAPI.createWarp(warp, new API.ApiResponseCallback() {
                    @Override
                    public void onResponse(String response) {
                        // Update the cache
                        Main.buildTeamTools.getProxyManager().updateCache().thenRun(() ->
                            player.sendMessage(ChatHelper.successful("Successfully created the warp %s!", name))
                        );
                    }

                    @Override
                    public void onFailure(IOException e) {
                        player.sendMessage(ChatHelper.error("Something went wrong while creating the warp %s! Please take a look at the console.", name));
                        e.printStackTrace();
                    }
                });
            }).exceptionally(e -> {
                player.sendMessage(ChatHelper.error("Failed to get the country belonging to your location while creating the warp %s!", name));
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

            //TODO CHECK IF THE TEAM OWNS THE WARP WITH THE GIVEN KEY

            NetworkAPI.deleteWarp(key, new API.ApiResponseCallback() {
                @Override
                public void onResponse(String response) {
                    player.sendMessage(ChatHelper.successful("Successfully deleted the warp %s!", key));
                }

                @Override
                public void onFailure(IOException e) {
                    player.sendMessage(ChatHelper.error("Something went wrong while deleting the warp %s!", key));
                }
            });
            return true;
        }


        if (args.length == 1) {

            // Extract the required data from the command arguments
            String key = args[0];

            // Check if the player has the required permission
            if (!player.hasPermission("btt.warp.use")) {
                player.sendMessage(ChatHelper.error("You don't have the required %s to %s warps.", "permission", "use"));
                return true;
            }

            WarpManager.warpPlayer(player, key);

            return true;
        }

        return false;
    }
}

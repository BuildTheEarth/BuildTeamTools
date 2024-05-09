package net.buildtheearth.modules.navigation.components.warps;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.Component;
import net.buildtheearth.modules.network.NetworkModule;
import net.buildtheearth.modules.network.api.NetworkAPI;
import net.buildtheearth.modules.network.api.OpenStreetMapAPI;
import net.buildtheearth.modules.network.model.BuildTeam;
import net.buildtheearth.utils.ChatHelper;
import net.buildtheearth.utils.GeometricUtils;
import net.buildtheearth.utils.geo.CoordinateConversion;
import net.buildtheearth.modules.navigation.components.warps.menu.WarpEditMenu;
import net.buildtheearth.modules.navigation.components.warps.menu.WarpGroupEditMenu;
import net.buildtheearth.modules.navigation.components.warps.model.Warp;
import net.buildtheearth.modules.navigation.components.warps.model.WarpGroup;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class WarpsComponent extends Component {

    public WarpsComponent() {
        super("Warps");
    }

    /**
     * Stores a List of the warp operations that need to happen on join
     */
    private final HashMap<UUID, Location> warpQueue = new HashMap<>();

    /**
     * Adds a warp operation to the queue
     *
     * @param in     The ByteArray received through the PluginMessageChannel
     * @param player The player to whom the warp operation belongs
     */
    public void addWarpToQueue(ByteArrayDataInput in, Player player) {
        //Check the target server
        String targetServer = in.readUTF();
        if (targetServer.equals(NetworkModule.getInstance().getBuildTeam().getServerName())) {
            //Extracts the warp key from the plugin message
            String warpKey = in.readUTF();

            Warp warp = NetworkAPI.getWarpByKey(warpKey);

            if (warp == null) {
                player.sendMessage(ChatHelper.getErrorString("The warp you tried to warp to does not exist anymore."));
                return;
            }

            Location targetWarpLocation = GeometricUtils.getLocationFromCoordinatesYawPitch(new double[]{warp.getLat(), warp.getLon()}, warp.getYaw(), warp.getPitch());
            targetWarpLocation.setY(warp.getY());
            targetWarpLocation.setWorld(Bukkit.getWorld(warp.getWorldName()));

            // Adds the event to the list, to be dealt with by the join listener
            warpQueue.put(player.getUniqueId(), targetWarpLocation);
        }
    }

    /**
     * Checks if there is a warp in the queue of the current server and teleports the player if this is the case
     * @param player the player to check the queue for
     */
    public void processQueueForPlayer(Player player) {
        Location targetWarpLocation = warpQueue.get(player.getUniqueId());

        if (targetWarpLocation == null) {
            return;
        }

        if(player.teleport(targetWarpLocation)) {
            ChatHelper.sendSuccessfulMessage(player, "Successfully warped you to the desired location!");
        } else {
            player.sendMessage(ChatHelper.getErrorString("Something went wrong trying to warp you to the desired location."));
        }

        warpQueue.remove(player.getUniqueId());
    }


    /**
     * Sends a plugin message to add the warp to the queue of the target server
     * Then switches the player to that server
     * @param player The player to warp
     * @param warp The warp to teleport the player to
     */
    public void warpPlayer(Player player, Warp warp) {
        // If the warp is in the same team, just teleport the player
        if(warp.getWarpGroup().getBuildTeam().getID().equals(NetworkModule.getInstance().getBuildTeam().getID())) {
            Location loc = GeometricUtils.getLocationFromCoordinatesYawPitch(new double[]{warp.getLat(), warp.getLon()}, warp.getYaw(), warp.getPitch());

            if(loc.getWorld() == null) {
                World world = Bukkit.getWorld(warp.getWorldName()) == null ? player.getWorld() : Bukkit.getWorld(warp.getWorldName());
                loc.setWorld(world);
            }

            loc.setY(warp.getY());

            player.teleport(loc);
            ChatHelper.sendSuccessfulMessage(player, "Successfully warped you to %s.", warp.getName());

            return;
        }

        // Get the server the warp is on
        String targetServer = "Empty"; // NetworkAPI.getServerNameByCountryCode(warp.getCountryCode());

        // Send a plugin message to that server which adds the warp to the queue
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("UniversalWarps");
        out.writeUTF(targetServer);
        out.writeUTF(warp.getId().toString());

        player.sendPluginMessage(BuildTeamTools.getInstance(), "btt:buildteam", out.toByteArray());

        // Switch the player to the target server
        NetworkModule.getInstance().switchServer(player, targetServer);
    }



    /** Creates a warp at the player's location and opens the warp edit menu.
     *
     * @param creator The player that is creating the warp
     */
    public void createWarp(Player creator){
        // Get the geographic coordinates of the player's location.
        Location location = creator.getLocation();
        double[] coordinates = CoordinateConversion.convertToGeo(location.getX(), location.getZ());

        //Get the country belonging to the coordinates
        CompletableFuture<String[]> future = OpenStreetMapAPI.getCountryFromLocationAsync(coordinates);

        future.thenAccept(result -> {
            String regionName = result[0];
            String countryCodeCCA2 = result[1].toUpperCase();

            //Check if the team owns this region/country
            boolean ownsRegion = NetworkModule.getInstance().ownsRegion(regionName, countryCodeCCA2);

            if(!ownsRegion) {
                creator.sendMessage(ChatHelper.getErrorString("This team does not own the country %s!", result[0]));
                return;
            }

            // Get the Other Group for default warp group
            WarpGroup group = NetworkModule.getInstance().getBuildTeam().getWarpGroups().stream().filter(warpGroup -> warpGroup.getName().equalsIgnoreCase("Other")).findFirst().orElse(null);

            // Create a default name for the warp
            String name = creator.getName() + "'s Warp";

            // Create an instance of the warp POJO
            Warp warp = new Warp(group, name, countryCodeCCA2, "cca2", null, null, null, location.getWorld().getName(), coordinates[0], coordinates[1], location.getY(), location.getYaw(), location.getPitch(), false);

            // Create the actual warp
            new WarpEditMenu(creator, warp, false, true);

        }).exceptionally(e -> {
            creator.sendMessage(ChatHelper.getErrorString("An error occurred while creating the warp!"));
            e.printStackTrace();
            return null;
        });
    }


    public void createWarpGroup(Player creator){
        // Create a default name for the warp
        String name = creator.getName() + "'s Warp Group";
        String description = "This is a warp group.";
        int slot = -1;

        WarpGroup warpGroup = new WarpGroup(NetworkModule.getInstance().getBuildTeam(), name, description, slot, null);

        new WarpGroupEditMenu(creator, warpGroup, false, true);
    }


    // ------------------------- //
    //          GETTER           //
    // ------------------------- //

    public Warp getWarpByName(String name){
        return getWarpByName(NetworkModule.getInstance().getBuildTeam(), name);
    }

    public Warp getWarpByName(BuildTeam buildTeam, String name) {
        return buildTeam.getWarpGroups().stream().flatMap(warpGroup -> warpGroup.getWarps().stream())
                .filter(warp1 -> warp1.getName() != null && warp1.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}

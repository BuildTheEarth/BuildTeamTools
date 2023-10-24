package net.buildtheearth.modules.warp;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.buildtheearth.Main;
import net.buildtheearth.modules.network.api.NetworkAPI;
import net.buildtheearth.modules.utils.ChatHelper;
import net.buildtheearth.modules.utils.GeometricUtils;
import net.buildtheearth.modules.utils.geo.LatLng;
import net.buildtheearth.modules.warp.model.Warp;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class WarpManager {

    /**
     * Stores a List of the warp operations that need to happen on join
     */
    private static final HashMap<UUID, Location> warpQueue = new HashMap<>();

    /**
     * Adds a warp operation to the queue
     *
     * @param in     The ByteArray received through the PluginMessageChannel
     * @param player The player to whom the warp operation belongs
     */
    public static void addWarpToQueue(ByteArrayDataInput in, Player player) {
        //Check the target server
        String targetServer = in.readUTF();
        if (targetServer.equals(Main.getBuildTeamTools().getProxyManager().getServerID())) {
            //Extracts the warp key from the plugin message
            String warpKey = in.readUTF();

            Warp warp = NetworkAPI.getWarpByKey(warpKey);

            Location targetWarpLocation = GeometricUtils.getLocationFromCoordinatesYawPitch(new LatLng(warp.getLat(), warp.getLon()), warp.getYaw(), warp.getPitch());
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
    public static void processQueueForPlayer(Player player) {
        Location targetWarpLocation = warpQueue.get(player.getUniqueId());

        if (targetWarpLocation == null) {
            return;
        }

        if(player.teleport(targetWarpLocation)) {
            player.sendMessage(ChatHelper.successful("Successfully warped you to the desired location!"));
        } else {
            player.sendMessage(ChatHelper.highlight("Something went wrong trying to warp you to the desired location."));
        }

        warpQueue.remove(player.getUniqueId());
    }

    /**
     * Sends a plugin message to add the warp to the queue of the target server
     * Then switches the player to that server
     * @param player The player to warp
     * @param key The key of the warp to teleport the player to
     */
    public static void warpPlayer(Player player, String key) {
        // Get the warp using a GET request
        Warp warp = NetworkAPI.getWarpByKey(key);

        // Get the server the warp is on
        String targetServer = NetworkAPI.getServerNameByCountryCode(warp.getCountryCode());

        // Send a plugin message to that server which adds the warp to the queue
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("UniversalWarps");
        out.writeUTF(targetServer);
        out.writeUTF(warp.getKey());

        player.sendPluginMessage(Main.instance, "BuildTeam", out.toByteArray());

        // Switch the player to the target server
        Main.getBuildTeamTools().getProxyManager().switchServer(player, targetServer);
    }
}

package net.buildtheearth.modules.warp;

import com.google.common.io.ByteArrayDataInput;
import net.buildtheearth.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
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
        String szServer = in.readUTF();
        if (szServer.equals(Main.getBuildTeamTools().getProxyManager().getServerID())) {
            //Extracts the coordinates from the plugin message
            double targetX = Double.parseDouble(in.readUTF());
            double targetY = Double.parseDouble(in.readUTF());
            double targetZ = Double.parseDouble(in.readUTF());
            float targetYaw = Float.parseFloat(in.readUTF());
            float targetPitch = Float.parseFloat(in.readUTF());

            // Creates a bukkit location for the warp target
            World targetWorld = Bukkit.getWorld(Main.instance.getConfig().getString("universal_tpll.earth_world"));
            Location targetWarpLocation = new Location(targetWorld, targetX, targetY, targetZ, targetYaw, targetPitch);

            // Adds the event to the list, to be dealt with by the join listener
            warpQueue.put(player.getUniqueId(), targetWarpLocation);
        }
    }

    public static void processQueueForPlayer(Player player) {
        Location targetWarpLocation = warpQueue.get(player.getUniqueId());

        if (targetWarpLocation == null) {
            return;
        }

        player.teleport(targetWarpLocation);
        warpQueue.remove(player.getUniqueId());
    }
}

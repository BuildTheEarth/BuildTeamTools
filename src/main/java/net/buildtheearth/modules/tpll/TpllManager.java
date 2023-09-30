package net.buildtheearth.modules.tpll;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.buildtheearth.Main;
import net.buildtheearth.modules.utils.ChatHelper;
import net.buildtheearth.modules.utils.GeometricUtils;
import net.buildtheearth.modules.utils.geo.LatLng;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

/**
 * Manages all things related to universal tpll
 */
public class TpllManager {

    /**
     * Stores a List of the tpll operations that need to happen on join
     */
    private static final HashMap<UUID, Location> tpllQueue = new HashMap<>();

    /**
     * Adds a tpll operation to the queue
     *
     * @param in     The ByteArray received through the PluginMessageChannel
     * @param player The player to whom the tpll operation belongs
     */
    public static void addTpllToQueue(ByteArrayDataInput in, Player player) {
        //Check the target server
        String szServer = in.readUTF();
        if (szServer.equals(Main.getBuildTeamTools().getProxyManager().getServerID())) {
            //Extracts the coordinates from the plugin message
            double targetLatitude = Double.parseDouble(in.readUTF());
            double targetLongitude = Double.parseDouble(in.readUTF());
            LatLng coordinates = new LatLng(targetLatitude, targetLongitude);

            // Creates a bukkit location for this tpll target
            Location targetTpllLocation = GeometricUtils.getLocationFromCoordinates(coordinates);
            // Location may contain a null world, this is checked for when the tpll event needs to be run
            // so that the player can be informed that the earth world was not specified

            // Adds the event to the list
            tpllQueue.put(player.getUniqueId(), targetTpllLocation);
        }
    }

    public static void processQueueForPlayer(Player player) {
        Location tpllTarget = tpllQueue.get(player.getUniqueId());
        if (tpllTarget == null) return;

        if (tpllTarget.getWorld() == null) {
            player.sendMessage(ChatHelper.highlight("The %s world of this server is %s.", "earth", "unknown"));
            tpllQueue.remove(player.getUniqueId());
            return;
        }

        player.teleport(tpllTarget);
        tpllQueue.remove(player.getUniqueId());
    }

    public static void tpllPlayer(Player player, double[] coordinates, String targetServerID) {

        // Send a plugin message to that server which adds the warp to the queue
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Tpll");
        out.writeUTF(targetServerID);
        out.writeUTF(String.valueOf(coordinates[0]));
        out.writeUTF(String.valueOf(coordinates[1]));

        player.sendPluginMessage(Main.instance, "BuildTeam", out.toByteArray());

        // Switch the player to the target server
        Main.getBuildTeamTools().getProxyManager().switchServer(player, targetServerID);
    }
}

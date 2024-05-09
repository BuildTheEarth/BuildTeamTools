package net.buildtheearth.modules.navigation.components.tpll;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.Component;
import net.buildtheearth.modules.network.NetworkModule;
import net.buildtheearth.utils.ChatHelper;
import net.buildtheearth.utils.GeometricUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class TpllComponent extends Component {

    public TpllComponent() {
        super("Tpll");
    }

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
    public void addTpllToQueue(ByteArrayDataInput in, Player player) {
        ChatHelper.logDebug("Adding tpll event to queue... %s for %s" + in, player.getDisplayName());
        //Check the target server
        String targetServerName = in.readUTF();
        ChatHelper.logDebug("The name of the target server is: %s", targetServerName);
        if (targetServerName.equals(NetworkModule.getInstance().getBuildTeam().getServerName())) {
            ChatHelper.logDebug("The target server equals the current server");
            //Extracts the coordinates from the plugin message
            double targetLatitude = Double.parseDouble(in.readUTF());
            double targetLongitude = Double.parseDouble(in.readUTF());
            double[] coordinates = new double[]{targetLatitude, targetLongitude};
            ChatHelper.logDebug("The coordinates of the tpll event are: %s %s", targetLatitude, targetLongitude);

            // Creates a bukkit location for this tpll target
            Location targetTpllLocation = GeometricUtils.getLocationFromCoordinates(coordinates);
            ChatHelper.logDebug("Created a bukkit location for this event");
            // Location may contain a null world, this is checked for when the tpll event needs to be run
            // so that the player can be informed that the earth world was not specified

            // Adds the event to the list
            tpllQueue.put(player.getUniqueId(), targetTpllLocation);
            ChatHelper.logDebug("Successfully added the tpll event to the queue.");
        }
    }

    public void processQueueForPlayer(Player player) {
        ChatHelper.logDebug("Trying to process tpll queue for player: %s", player.getDisplayName());
        if(!tpllQueue.containsKey(player.getUniqueId())) return;
        Location tpllTarget = tpllQueue.get(player.getUniqueId());
        ChatHelper.logDebug("The tpll target is: %s", tpllTarget.toString());
        if (tpllTarget == null) return;

        if (tpllTarget.getWorld() == null) {
            player.sendMessage(ChatHelper.getErrorString("The %s world of this server is %s.", "earth", "unknown"));
            tpllQueue.remove(player.getUniqueId());
            return;
        }

        player.teleport(tpllTarget);
        ChatHelper.logDebug("Teleported the player to the tpll target.");
        tpllQueue.remove(player.getUniqueId());
        ChatHelper.logDebug("Successfully processed the tpll queue for this player.");
    }

    /**
     * Sends a plugin message with a tpll request to the target server and sends the player there.
     * @param player The player to send to the new server.
     * @param coordinates The coordinates to send the player to on join.
     * @param targetServerName The server to send the player to.
     */
    public void tpllPlayer(Player player, double[] coordinates, String targetServerName) {
        ChatHelper.logDebug("Starting universal tpll teleportation for %s to %s.", player.getDisplayName(), targetServerName);
        // Send a plugin message to the target server which adds the tpll to the queue
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("TPLL");
        out.writeUTF(targetServerName);
        out.writeUTF(player.getUniqueId().toString());
        out.writeUTF(String.valueOf(coordinates[0] + coordinates[1]));
        player.sendPluginMessage(BuildTeamTools.getInstance(), "btt:buildteam", out.toByteArray());

        // Switch the player to the target server
        ChatHelper.logDebug("Teleported player to the target server.");
    }

}

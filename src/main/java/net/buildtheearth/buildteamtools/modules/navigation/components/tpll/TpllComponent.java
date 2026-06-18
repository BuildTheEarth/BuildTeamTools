package net.buildtheearth.buildteamtools.modules.navigation.components.tpll;

import com.alpsbte.alpslib.utils.ChatHelper;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.ModuleComponent;
import net.buildtheearth.buildteamtools.modules.navigation.NavUtils;
import net.buildtheearth.buildteamtools.modules.network.NetworkModule;
import net.buildtheearth.buildteamtools.modules.network.model.BuildTeam;
import net.buildtheearth.model.GeographicalCoordinate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.UUID;

public class TpllComponent extends ModuleComponent {

    public TpllComponent() {
        super("Tpll");
    }

    public static final NamespacedKey TPLL_COOKIE_KEY = NamespacedKey.minecraft("btt_buildteam_tpll");

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
    public void addTpllToQueue(@NonNull ByteArrayDataInput in, @NonNull Player player) {
        ChatHelper.logDebug("Adding tpll event to queue... %s for %s" + in, player.displayName());
        //Check the target server
        String targetServerName = in.readUTF();
        ChatHelper.logDebug("The name of the target server is: %s", targetServerName);
        BuildTeam buildTeam = NetworkModule.getInstance().getBuildTeam();
        if (buildTeam != null && targetServerName.equals(buildTeam.getServerName())) {
            ChatHelper.logDebug("The target server equals the current server");
            //Extracts the coordinates from the plugin message
            double targetLatitude = Double.parseDouble(in.readUTF());
            double targetLongitude = Double.parseDouble(in.readUTF());

            ChatHelper.logDebug("The coordinates of the tpll event are: %s %s", targetLatitude, targetLongitude);

            // Creates a bukkit location for this tpll target
            Location targetTpllLocation = NavUtils.getLocationFromCoordinates(new GeographicalCoordinate(targetLatitude, targetLongitude));
            ChatHelper.logDebug("Created a bukkit location for this event");
            // Location may contain a null world, this is checked for when the tpll event needs to be run
            // so that the player can be informed that the earth world was not specified

            // Adds the event to the list
            tpllQueue.put(player.getUniqueId(), targetTpllLocation);
            ChatHelper.logDebug("Successfully added the tpll event to the queue.");
        }
    }

    public void processQueueForPlayer(@NonNull Player player) {
        ChatHelper.logDebug("Trying to process tpll queue for player: %s", player.displayName());
        if (!tpllQueue.containsKey(player.getUniqueId())) return;
        Location tpllTarget = tpllQueue.get(player.getUniqueId());
        if (tpllTarget == null) return;

        ChatHelper.logDebug("The tpll target is: %s", tpllTarget.toString());

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
     *
     * @param player           The player to send to the new server.
     * @param coordinate      The coordinate to send the player to on join.
     * @param targetServerName The server to send the player to.
     */
    public void tpllPlayer(@NotNull Player player, @NotNull GeographicalCoordinate coordinate, String targetServerName) {
        ChatHelper.logDebug("Starting universal tpll teleportation for %s to %s.", player.getDisplayName(), targetServerName);
        // Send a plugin message to the target server which adds the tpll to the queue
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("TPLL");
        out.writeUTF(targetServerName);
        out.writeUTF(player.getUniqueId().toString());
        out.writeUTF(String.valueOf(coordinate.latitude()));
        out.writeUTF(String.valueOf(coordinate.longitude()));
        player.sendPluginMessage(BuildTeamTools.getInstance(), "btt:buildteam", out.toByteArray());

        // Switch the player to the target server
        ChatHelper.logDebug("Teleported player to the target server.");
    }

    public void tpllPlayerTransfer(@NotNull Player player, @NotNull GeographicalCoordinate coordinate, String ip) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeDouble(coordinate.latitude());
        out.writeDouble(coordinate.longitude());
        player.storeCookie(TPLL_COOKIE_KEY, out.toByteArray());
        NavUtils.transferPlayer(player, ip);
    }

    public void processCookie(@NotNull Player player, byte[] cookie) {
        ByteArrayDataInput in = ByteStreams.newDataInput(cookie);
        double targetLatitude = in.readDouble();
        double targetLongitude = in.readDouble();
        ChatHelper.logDebug("Processing cookie for tpll event: lat: %s lon: %s", targetLatitude, targetLongitude);
        Bukkit.getScheduler().runTask(BuildTeamTools.getInstance(), // Needs to be delayed if not exception will be trown and nothing happens
                () -> player.performCommand("tpll " + targetLatitude + " " + targetLongitude));
    }
}

package net.buildtheearth.modules.tpll.listeners;

import net.buildtheearth.Main;
import net.buildtheearth.modules.network.ProxyManager;
import net.buildtheearth.modules.network.api.NetworkAPI;
import net.buildtheearth.modules.tpll.TpllManager;
import net.buildtheearth.modules.network.api.OpenStreetMapAPI;
import net.buildtheearth.modules.utils.ChatHelper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.concurrent.CompletableFuture;


/**
 * Listener for handling Teleportation (TPLL) related events.
 * This class checks and intercepts TPLL commands, performs necessary checks,
 * and teleports the player to the specified location on a different server if required.
 */
public class TpllListener implements Listener {

    // Proxy manager to handle network-related operations
    private final ProxyManager proxyManager = Main.getBuildTeamTools().getProxyManager();

    // Latitude and longitude coordinates for teleportation
    private double lon;
    private double lat;

    // Target server name for teleportation
    private String targetServerName;
    
    @EventHandler
    public void onTpll(PlayerCommandPreprocessEvent event) {
        // Check if the command is a TPLL command
        if (!isTpllCommand(event)) return;

        // Check if teleportation interception is required
        shouldIntercept(event).thenAcceptAsync(shouldIntercept -> {
            // If interception is required, cancel the event and perform cross teleportation
            if (shouldIntercept) {
                event.setCancelled(true);
                TpllManager.tpllPlayer(event.getPlayer(), new double[]{lat, lon}, targetServerName);
            }
        });
    }

    /**
     * Checks if a command is a TPLL command and extracts the latitude and longitude coordinates.
     *
     * @param event The event triggered when a player processes a command.
     * @return True if the command is a TPLL command and coordinates are extracted sucessfully, false otherwise.
     */
    private boolean isTpllCommand(PlayerCommandPreprocessEvent event) {
        // Check if the command starts with "tpll"
        if (!event.getMessage().startsWith("tpll")) return false;
        ChatHelper.logDebug("Intercepted tpll command.");

        // Split the command to extract coordinates
        String[] splitMessage = event.getMessage().split(" ");
        if (splitMessage.length < 3) return false;
        ChatHelper.logDebug("Command had the correct length (%s).", splitMessage.length);

        // Extract and set latitude and longitude coordinates
        this.lat = Double.parseDouble(splitMessage[1]);
        this.lon = Double.parseDouble(splitMessage[2]);
        return true;
    }

    /**
     * Determines if teleportation interception is required based on the player's location and network status.
     *
     * @param event The event triggered when a player processes a command.
     * @return A CompletableFuture representing whether teleportation interception is required.
     */
    private CompletableFuture<Boolean> shouldIntercept(PlayerCommandPreprocessEvent event) {
        return OpenStreetMapAPI.getCountryAndSubRegionsFromLocationAsync(new double[]{lat, lon})
                .thenComposeAsync(address -> {
                    if (address == null) return CompletableFuture.completedFuture(false);

                    String countryName = address[0];

                    // If not connected to the network, inform the player of the IP and return false
                    if (!proxyManager.isConnected()) {
                        return NetworkAPI.getTeamIPByCountryAsync(countryName)
                                .thenAcceptAsync(ip -> {
                                    event.getPlayer().sendMessage(ChatHelper.highlight("The current server is %s connected to the network! Teleporting here is %s available on this server.", "not"));
                                    event.getPlayer().sendMessage(ChatHelper.highlight("Connect to %s instead.", ip));
                                }).thenApplyAsync(v -> false);
                    }

                    // Check if the current server name differs from the target server name
                    return proxyManager.getServerNameAsync()
                            .thenComposeAsync(currentServerName ->
                                    NetworkAPI.getTeamIdByCountryAsync(countryName)
                                            .thenComposeAsync(teamID ->
                                                    NetworkAPI.getServerNameByTeamId(teamID)
                                                            .thenApplyAsync(newTargetServerName -> {
                                                                this.targetServerName = newTargetServerName;
                                                                return !currentServerName.equals(newTargetServerName);
                                                            })
                                            )
                            );
                });
    }
}


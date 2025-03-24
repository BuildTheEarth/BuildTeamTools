package net.buildtheearth.modules.navigation.components.tpll.listeners;

import net.buildtheearth.modules.network.NetworkModule;
import net.buildtheearth.modules.network.api.OpenStreetMapAPI;
import net.buildtheearth.modules.network.model.Region;
import net.buildtheearth.modules.navigation.NavigationModule;
import net.buildtheearth.utils.ChatUtil;
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
    private final NetworkModule networkModule = NetworkModule.getInstance();

    // Latitude and longitude coordinates for teleportation
    private double lon;
    private double lat;

    // Target server name for teleportation
    private String targetServerName;
    
    @EventHandler
    public void onTpll(PlayerCommandPreprocessEvent event) {
        // Check if the NavigationModule is enabled
        if (!NavigationModule.getInstance().isEnabled()) return;

        // Check if the command is a TPLL command
        if (!isTpllCommand(event)) return;

        // Check if teleportation interception is required
        shouldIntercept(event).thenAcceptAsync(shouldIntercept -> {
            // If interception is required, cancel the event and perform cross teleportation
            if (shouldIntercept) {
                event.setCancelled(true);
                NavigationModule.getInstance().getTpllComponent().tpllPlayer(event.getPlayer(), new double[]{lat, lon}, targetServerName);
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
        ChatUtil.logDebug(event.getMessage());
        if (!event.getMessage().startsWith("/tpll")) return false;
        ChatUtil.logDebug("Intercepted tpll command.");

        // Split the command to extract coordinates
        String[] splitMessage = event.getMessage().split(" ");
        splitMessage[1] = splitMessage[1].replaceAll(",", " ").trim();
        if (splitMessage.length < 3) return false;
        ChatUtil.logDebug("Command had the correct length (%s).", splitMessage.length);

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
        return OpenStreetMapAPI.getCountryFromLocationAsync(new double[]{lat, lon})
                .thenComposeAsync(address -> {
                    if (address == null) return CompletableFuture.completedFuture(false);

                    String countryName = address[0];
                    Region region = Region.getByName(countryName);

                    if (!networkModule.getBuildTeam().isConnected() || !region.isConnected()) {
                        event.getPlayer().sendMessage(ChatUtil.getErrorString("Either this server or the receiving server isn't connected to the network."));
                        event.setCancelled(true);
                        return CompletableFuture.completedFuture(false);
                    }

                    if (region.getBuildTeam().getID() != networkModule.getBuildTeam().getID()) {
                        targetServerName = region.getBuildTeam().getServerName();
                        return CompletableFuture.completedFuture(true);
                    }

                    return CompletableFuture.completedFuture(false);
                });
    }

}


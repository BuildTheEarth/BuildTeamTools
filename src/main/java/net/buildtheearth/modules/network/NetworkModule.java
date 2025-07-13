package net.buildtheearth.modules.network;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import lombok.Setter;
import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.Module;
import net.buildtheearth.modules.network.api.NetworkAPI;
import net.buildtheearth.modules.network.listeners.NetworkJoinListener;
import net.buildtheearth.modules.network.listeners.NetworkQuitListener;
import net.buildtheearth.modules.network.model.BuildTeam;
import net.buildtheearth.modules.network.model.Region;
import net.buildtheearth.modules.network.model.RegionType;
import net.buildtheearth.utils.io.ConfigPaths;
import net.buildtheearth.utils.io.Constants;
import net.buildtheearth.utils.io.Errors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicBoolean;

public class NetworkModule extends Module {

    public static final String GLOBAL_PLOT_SYSTEM_SERVER = "NYC-1";
    public static final int CACHE_UPLOAD_SPEED = 20 * 60 * 10 + 20;

    /** Information about the build team of this server */
    @Getter @Setter
    private BuildTeam buildTeam;

    /** A list of players that are communicating with this server. */
    @Getter
    private final List<UUID> communicators = new ArrayList<>();

    /** A list of all build teams of BuildTheEarth. */
    @Getter
    private final List<BuildTeam> buildTeams = new ArrayList<>();

    /** A list of all regions of BuildTheEarth. */
    @Getter
    private final List<Region> regions = new ArrayList<>();



    private static NetworkModule instance = null;

    public NetworkModule() {
        super("Network");
    }

    public static NetworkModule getInstance() {
        return instance == null ? instance = new NetworkModule() : instance;
    }

    @Override
    public void enable() {
        String apiKey = BuildTeamTools.getInstance().getConfig().getString(ConfigPaths.API_KEY);
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals(Constants.DEFAULT_API_KEY)) {
            shutdown(Errors.API_KEY_NOT_CONFIGURED);
            return;
        }

        pingAllOnlinePlayers();

        try {
            // Updates the network cache and waits for it to complete
            updateCache().join();

            // After completion check if the build team is loaded
            if (getBuildTeam() == null) {
                shutdown(Errors.BUILD_TEAM_NOT_LOADED);
                return;
            }
        } catch (CompletionException e) {
            shutdown(Errors.BUILD_TEAM_NOT_LOADED + " " + e.getCause().getMessage());
        }

        super.enable();
    }

    @Override
    public void registerListeners(){
        super.registerListeners(new NetworkJoinListener(), new NetworkQuitListener());
    }







    /** Updates the cache of the proxy. */
    public CompletableFuture<Boolean> updateCache() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        try {

            NetworkAPI.getBuildTeamInformation().thenRun(() -> NetworkAPI.setupCurrentServerData()
                    .thenRun(() ->
                            future.complete(null))
                    .exceptionally(e -> {
                        future.completeExceptionally(e);
                        return null;
                    })).exceptionally(e -> {
                future.completeExceptionally(e);
                return null;
            });

        } catch (Exception e) {
            future.completeExceptionally(e);
        }

        return future;
    }

    /**
     * Sends a ping to the network.
     * If the player is playing on a server connected to the network the proxy will answer with another ping message.
     * Afterwards the player will be added to the communicators list.
     *
     * @param p: Player to ping.
     */
    public void ping(Player p) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Ping");
        out.writeUTF(p.getUniqueId().toString());
        out.writeUTF("Version: " + BuildTeamTools.getInstance().getDescription().getVersion());
        p.sendPluginMessage(BuildTeamTools.getInstance(), "btt:buildteam", out.toByteArray());
    }

    /**
     * Sends a network ping for every player currently connected to the server.
     * {@link #ping(Player)}
     */
    public void pingAllOnlinePlayers() {
        Bukkit.getOnlinePlayers().forEach(this::ping);
    }

    /**
     * Sends a player server switch request to the proxy. The player will then be sent to the target server.
     *
     * @param player   The player aiming to switch server
     * @param targetServer The name of the server the player must switch to
     */
    public void switchServer(Player player, String targetServer) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(targetServer);
        player.sendPluginMessage(BuildTeamTools.getInstance(), "BungeeCord", out.toByteArray());
    }

    /** Returns all regions of the given region type.
     *
     * @param regionType The region type to get the regions of
     * @return A list of all regions of the given region type
     */
    public static @NotNull List<Region> getRegionsByRegionType(RegionType regionType) {
        ArrayList<Region> regions = new ArrayList<>();
        for(Region region : NetworkModule.getInstance().getRegions())
            if(region.getType().equals(regionType))
                regions.add(region);

        return regions;
    }

    public boolean ownsRegion(String regionName, String countryCodeCca2) {
        AtomicBoolean ownsRegion = new AtomicBoolean(false);
        buildTeam.getRegions().forEach(region -> {
            if(region.getName().equals(regionName) || region.getCountryCodeCca2().equals(countryCodeCca2)) ownsRegion.set(true);
        });

        return ownsRegion.get();
    }


    // Getters & Setters

    /** Returns the BuildTeam of the given teamID.
     *
     * @param teamID The ID of the BuildTeam
     * @return The BuildTeam with the given ID
     */
    public BuildTeam getBuildTeamByID(String teamID) {
        for (BuildTeam team : buildTeams)
            if (team.getID() != null && team.getID().equals(teamID))
                return team;

        return null;
    }
}

package net.buildtheearth.modules.network;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.buildtheearth.Main;
import net.buildtheearth.modules.network.api.NetworkAPI;
import net.buildtheearth.modules.network.model.Continent;
import net.buildtheearth.modules.network.model.Country;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProxyManager {

    public static String GLOBAL_PLOT_SYSTEM_SERVER = "Plot1";

    public static int CACHE_UPLOAD_SPEED = 20 * 60 * 10 + 20;

    /**
     * Information about the current server
     */
    private String buildTeamID;
    private String serverName;
    private boolean isConnected;

    /**
     * A List of players that are communicating with this server.
     * If isConnected is true, that means the players are also communicating with the network.
     */
    private final List<UUID> communicators = new ArrayList<>();

    public ProxyManager() {
        pingAllOnlinePlayers();
        NetworkAPI.getConnectedRegions();
        setupCurrentServerData();
    }

    // Methods

    private void setupCurrentServerData() {
        for(Continent continent : Continent.values()) {
            for(Country country : continent.getCountries()) {
                if(!country.getIP().equals(Bukkit.getServer().getIp())) continue;
                buildTeamID = country.getTeamID();
                serverName = country.getServerName();
                isConnected = country.isConnected();
            }
        }
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
        out.writeUTF("Version: " + Main.instance.getDescription().getVersion());
        p.sendPluginMessage(Main.instance, "BuildTeam", out.toByteArray());
    }

    /**
     * Sends a network ping for every player currently connected to the server.
     * {@link #ping(Player)}
     */
    public void pingAllOnlinePlayers() {
        Bukkit.getOnlinePlayers().forEach(player -> ping(player));
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
        player.sendPluginMessage(Main.instance, "BungeeCord", out.toByteArray());
    }

    // Getters & Setters


    public String getBuildTeamID() {
        return buildTeamID;
    }

    public String getServerName() {
        return serverName;
    }

    public List<UUID> getCommunicators() {
        return communicators;
    }

    public boolean isConnected() {
        return isConnected;
    }
}

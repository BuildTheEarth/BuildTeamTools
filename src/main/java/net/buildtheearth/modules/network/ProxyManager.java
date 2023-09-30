package net.buildtheearth.modules.network;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.buildtheearth.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProxyManager {

    public static int CACHE_UPLOAD_SPEED = 20 * 60 * 10 + 20;

    public static String PLOT_SERVER_NAME = "Plot1";

    /**
     * The buildTeamID corresponding to this server.
     */
    private String buildTeamID;
    /**
     * The serverID corresponding to this server.
     */
    private String serverID;

    /**
     * A List of players that are communicating with this server.
     * If isConnected is true, that means the players are also communicating with the network.
     */
    private final List<UUID> communicators = new ArrayList<>();

    /**
     * True if the server is connected to the network
     * False if it isn't
     */
    private boolean isConnected;

    public ProxyManager() {
        pingAllOnlinePlayers();
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

    /**
     * Get a list of all the countries of all servers that are currently connected to the network
     * @return A list off all active countries
     */
    public List<String> getActiveCountries() {
        //TODO IMPLEMENT METHOD
        return null;
    }


    // Getters & Setters

    public String getBuildTeamID() {
        return buildTeamID;
    }

    public void setBuildTeamID(String buildTeamID) {
        this.buildTeamID = buildTeamID;
    }

    public String getServerID() {
        return serverID;
    }

    public void setServerID(String serverID) {
        this.serverID = serverID;
    }

    public List<UUID> getCommunicators() {
        return communicators;
    }

    public boolean isConnected() {
        return isConnected;
    }
}

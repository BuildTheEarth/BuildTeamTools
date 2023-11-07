package net.buildtheearth.modules.network;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.buildtheearth.Main;
import net.buildtheearth.modules.network.api.NetworkAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ProxyManager {

    public static int CACHE_UPLOAD_SPEED = 20 * 60 * 10 + 20;

    /**
     * Information about the current server
     */
    private String buildTeamID;
    private String serverName;
    private String serverID;

    /**
     * A List of players that are communicating with this server.
     * If isConnected is true, that means the players are also communicating with the network.
     */
    private final List<UUID> communicators = new ArrayList<>();

    /**
     * A List of servers that are currently communicating with the network
     */
    private final List<String> activeServers = new ArrayList<>();

    /**
     * True if the server is connected to the network
     * False if it isn't
     */
    private boolean isConnected;

    public ProxyManager() {
        pingAllOnlinePlayers();
        sendServerPing();
    }

    // Methods

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
     * Sends a plugin message to check the status of all servers
     */
    public void sendServerPing() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ServerPing");
        out.writeUTF("requesting");
        Bukkit.getServer().sendPluginMessage(Main.instance, "BuildTeam", out.toByteArray());
    }

    public void handleServerPing(ByteArrayDataInput in) {
        String requestType = in.readUTF();
        if(requestType.equals("requesting")) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("ServerPing");
            out.writeUTF("responding");
            out.writeUTF(serverName);
            Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
            if(player != null) player.sendPluginMessage(Main.instance, "BuildTeam", out.toByteArray());
            activeServers.clear();
        } else if (requestType.equals("responding")) {
            String pingServerName = in.readUTF();
            activeServers.add(pingServerName);
        }
    }

    /**
     * Get a list of all the countries of all servers that are currently connected to the network
     * @return A list off all active countries or null if an exception occurs
     */
    public List<String> getActiveCountries() {
       sendServerPing();
        try {
            return NetworkAPI.getCountriesByActiveServers().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }


    // Getters & Setters

    public String getBuildTeamID() {
        return buildTeamID;
    }

    public void setBuildTeamID(String buildTeamID) {
        this.buildTeamID = buildTeamID;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public CompletableFuture<String> getServerNameAsync() {
        if(serverName != null) return CompletableFuture.completedFuture(serverName);
        return NetworkAPI.getCurrentServerNameAsync();
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

    public void setConnected(boolean connected) {
        isConnected = connected;
        if(serverName == null) NetworkAPI.getCurrentServerNameAsync().thenAccept(newServerName -> serverName = newServerName);
    }

    public List<String> getActiveServers() {
        return activeServers;
    }
}

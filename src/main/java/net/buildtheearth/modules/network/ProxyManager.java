package net.buildtheearth.modules.network;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import lombok.Setter;
import net.buildtheearth.Main;
import net.buildtheearth.modules.network.api.NetworkAPI;
import net.buildtheearth.modules.network.model.BuildTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProxyManager {

    public static String GLOBAL_PLOT_SYSTEM_SERVER = "Plot1";

    public static int CACHE_UPLOAD_SPEED = 20 * 60 * 10 + 20;

    /**
     * Information about the build team of this server
     */
    @Getter @Setter
    private BuildTeam buildTeam;

    /**
     * A List of players that are communicating with this server.
     * If isConnected is true, that means the players are also communicating with the network.
     */
    @Getter
    private final List<UUID> communicators = new ArrayList<>();

    /**
     * A List of all build teams of BuildTheEarth.
     */
    @Getter
    private final List<BuildTeam> buildTeams = new ArrayList<>();

    public ProxyManager() {
        pingAllOnlinePlayers();
        NetworkAPI.getBuildTeamInformation();
        NetworkAPI.setupCurrentServerData();
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

    /** Returns the BuildTeam of the given teamID.
     *
     * @param teamID The ID of the BuildTeam
     * @return The BuildTeam with the given ID
     */
    public BuildTeam getBuildTeamByID(String teamID) {
        for(BuildTeam buildTeam : buildTeams) {
            if(buildTeam.getID().equals(teamID)) {
                return buildTeam;
            }
        }
        return null;
    }
}

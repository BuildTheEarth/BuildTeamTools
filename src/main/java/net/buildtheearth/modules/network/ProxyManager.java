package net.buildtheearth.modules.network;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import lombok.Setter;
import net.buildtheearth.Main;
import net.buildtheearth.modules.network.api.NetworkAPI;
import net.buildtheearth.modules.network.model.BuildTeam;
import net.buildtheearth.modules.network.model.Region;
import net.buildtheearth.modules.network.model.RegionType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProxyManager {

    public static String GLOBAL_PLOT_SYSTEM_SERVER = "NYC-1";

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

    /**
     * A List of all regions of BuildTheEarth.
     */
    @Getter
    private final List<Region> regions = new ArrayList<>();

    public ProxyManager() {
        pingAllOnlinePlayers();
        updateCache();
    }

    /** Updates the cache of the proxy. */
    public void updateCache() {
        NetworkAPI.getBuildTeamInformation().thenRun(NetworkAPI::setupCurrentServerData);
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

    /** Sends a message to the player that the server is not connected to the network yet.
     *  Instead of the server name the server IP will be displayed so the player can join the other server ip manually.
     *
     * @param player The player to send the message to
     * @param serverIP The IP of the server the player should join
     */
    public static void sendNotConnectedMessage(Player player, String serverIP) {
        TextComponent comp = new TextComponent("§e" + serverIP + " §7(Click to copy)");
        comp.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, serverIP));
        comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§eClick to copy").create()));

        String notConnected = "§cThis server is not connected to the network yet and has a different Server IP:";

        if(!Main.getBuildTeamTools().getProxyManager().getBuildTeam().isConnected())
            notConnected = "§cThis server has a different Server IP:";

        player.closeInventory();
        player.sendMessage(notConnected);
        player.sendMessage("");
        player.spigot().sendMessage(comp);
        player.sendMessage("");
        player.sendMessage("§cClick on the IP to copy it. Then enter it in your Minecraft Server List. (Paste with CTRL + V)");
    }

    /** Returns all regions of the given region type.
     *
     * @param regionType The region type to get the regions of
     * @return A list of all regions of the given region type
     */
    public static ArrayList<Region> getRegionsByRegionType(RegionType regionType){
        ArrayList<Region> regions = new ArrayList<>();
        for(Region region : Main.getBuildTeamTools().getProxyManager().getRegions())
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
        for(BuildTeam buildTeam : buildTeams)
            if (buildTeam.getID() != null && buildTeam.getID().equals(teamID))
                return buildTeam;

        return null;
    }
}

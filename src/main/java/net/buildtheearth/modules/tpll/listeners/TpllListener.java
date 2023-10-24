package net.buildtheearth.modules.tpll.listeners;

import net.buildtheearth.Main;
import net.buildtheearth.modules.network.ProxyManager;
import net.buildtheearth.modules.network.api.NetworkAPI;
import net.buildtheearth.modules.tpll.TpllManager;
import net.buildtheearth.modules.network.api.OpenStreetMapAPI;
import net.buildtheearth.modules.utils.ChatHelper;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class TpllListener implements Listener {

    @EventHandler
    public void onTpll(PlayerCommandPreprocessEvent event) {
        if(!event.getMessage().startsWith("tpll")) return;
        Bukkit.getLogger().info(ChatHelper.successful("Intercepted Tpll command"));

        String[] splitMessage = event.getMessage().split(" ");
        if(splitMessage.length < 3) return;
        Bukkit.getLogger().info(ChatHelper.successful("Message had the correct length (%s)", splitMessage.length));

        double lat = Double.parseDouble(splitMessage[1]);
        double lon = Double.parseDouble(splitMessage[2]);

        ProxyManager proxyManager = Main.getBuildTeamTools().getProxyManager();
        String[] address = OpenStreetMapAPI.getCountryAndSubRegionsFromLocation(new double[]{lat, lon});
        Bukkit.getLogger().info(ChatHelper.successful("Got the address from OSM API:\n %s", address));
        String countryCode = address[0];
        Bukkit.getLogger().info(ChatHelper.successful("Got the country code from address:\n %s", countryCode));

        // Check if the current server is connected and get the name of the server
        if(!proxyManager.isConnected()) {
            event.getPlayer().sendMessage(ChatHelper.highlight("The current server is %s connected to the network! Teleporting here is %s available on this server.", "not"));
        }
        String currentServerName = proxyManager.getServerName();
        Bukkit.getLogger().info(ChatHelper.successful("Got current server name from proxy manager:\n %s", currentServerName));


        String targetServerName = NetworkAPI.getServerNameByCountryCode(countryCode);
        Bukkit.getLogger().info(ChatHelper.successful("Got target server name:\n %s", targetServerName));

        if(targetServerName.equals(currentServerName)) {
            Bukkit.getLogger().info(ChatHelper.highlight("The id of the target server is the same as the current servers id"));
            return;
        }
        event.setCancelled(true);
        Bukkit.getLogger().info(ChatHelper.successful("Cancelled default tpll event"));

        TpllManager.tpllPlayer(event.getPlayer(), new double[]{lat, lon}, targetServerName);
        Bukkit.getLogger().info(ChatHelper.successful("Executed custom tpll method"));
    }
}

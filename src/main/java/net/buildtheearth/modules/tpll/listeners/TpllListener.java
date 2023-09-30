package net.buildtheearth.modules.tpll.listeners;

import net.buildtheearth.Main;
import net.buildtheearth.modules.network.api.NetworkAPI;
import net.buildtheearth.modules.tpll.TpllManager;
import net.buildtheearth.modules.network.api.OpenStreetMapAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class TpllListener implements Listener {

    @EventHandler
    public void onTpll(PlayerCommandPreprocessEvent event) {
        if(!event.getMessage().startsWith("tpll")) return;

        String[] splitMessage = event.getMessage().split(" ");
        if(splitMessage.length < 3) return;

        double lat = Double.parseDouble(splitMessage[1]);
        double lon = Double.parseDouble(splitMessage[2]);

        String[] address = OpenStreetMapAPI.getCountryAndSubRegionsFromLocation(new double[]{lat, lon});
        String countryCode = address[0];

        String currentServerID = Main.getBuildTeamTools().getProxyManager().getServerID();
        String targetServerID = NetworkAPI.getServerByCountryCode(countryCode);

        if(targetServerID == currentServerID) return;
        event.setCancelled(true);

        TpllManager.tpllPlayer(event.getPlayer(), new double[]{lat, lon}, targetServerID);
    }
}

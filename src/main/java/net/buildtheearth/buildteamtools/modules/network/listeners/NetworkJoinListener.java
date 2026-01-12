package net.buildtheearth.buildteamtools.modules.network.listeners;

import net.buildtheearth.buildteamtools.modules.common.CommonModule;
import net.buildtheearth.buildteamtools.modules.network.NetworkModule;
import net.buildtheearth.buildteamtools.modules.network.api.NetworkAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class NetworkJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        //Send ping to the proxy to add player to the communicators list if response to ping is received
        NetworkModule.getInstance().ping(player);

        //Notify the admins if a new update got installed
        CommonModule.getInstance().getUpdaterComponent().notifyUpdate(player);

        // Sync the playerlist
        NetworkAPI.syncPlayerList();
    }
}

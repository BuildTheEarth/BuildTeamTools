package net.buildtheearth.modules.network.listeners;

import net.buildtheearth.modules.network.NetworkModule;
import net.buildtheearth.modules.network.api.NetworkAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class NetworkQuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        NetworkModule.getInstance().getCommunicators().remove(p.getUniqueId());

        // Sync the playerlist
        NetworkAPI.syncPlayerList();
    }
}

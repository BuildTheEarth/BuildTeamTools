package net.buildtheearth.modules.network.listeners;

import net.buildtheearth.Main;
import net.buildtheearth.modules.network.api.NetworkAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class NetworkJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        //Send ping to the proxy to add player to the communicators list if response to ping is received
        Main.getBuildTeamTools().getProxyModule().ping(player);

        //Notify the admins if a new update got installed
        Main.buildTeamTools.notifyUpdate(player);

        // Sync the playerlist
        NetworkAPI.syncPlayerList();
    }
}

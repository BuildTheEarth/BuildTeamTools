package net.buildtheearth.modules.navigation.components.warps.listeners;

import net.buildtheearth.modules.navigation.NavigationModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class WarpJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        NavigationModule.getInstance().getWarpsComponent().processQueueForPlayer(event.getPlayer());
    }
}

package net.buildtheearth.modules.warp.listeners;

import net.buildtheearth.modules.warp.WarpModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class WarpJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        WarpModule.processQueueForPlayer(event.getPlayer());
    }
}

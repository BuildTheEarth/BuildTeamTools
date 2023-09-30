package net.buildtheearth.modules.tpll.listeners;

import net.buildtheearth.modules.tpll.TpllManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class TpllJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        TpllManager.processQueueForPlayer(event.getPlayer());
    }
}

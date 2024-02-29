package net.buildtheearth.modules.tpll.listeners;

import net.buildtheearth.modules.tpll.TpllModule;
import net.buildtheearth.modules.utils.ChatHelper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class TpllJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        ChatHelper.logDebug("Player joined. Processing tpll queue for them. Event fired.");
        TpllModule.processQueueForPlayer(event.getPlayer());
    }
}

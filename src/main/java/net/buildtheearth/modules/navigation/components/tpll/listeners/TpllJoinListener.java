package net.buildtheearth.modules.navigation.components.tpll.listeners;

import net.buildtheearth.modules.navigation.NavigationModule;
import net.buildtheearth.utils.ChatUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class TpllJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        ChatUtil.logDebug("Player joined. Processing tpll queue for them. Event fired.");
        NavigationModule.getInstance().getTpllComponent().processQueueForPlayer(event.getPlayer());
    }
}

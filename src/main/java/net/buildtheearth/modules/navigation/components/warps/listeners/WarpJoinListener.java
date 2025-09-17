package net.buildtheearth.modules.navigation.components.warps.listeners;

import net.buildtheearth.modules.navigation.NavigationModule;
import net.buildtheearth.modules.navigation.components.warps.WarpsComponent;
import net.buildtheearth.utils.ChatHelper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class WarpJoinListener implements Listener {

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        ChatHelper.logDebug("Player joined. Processing warp queue for them. Event fired.");
        NavigationModule.getInstance().getWarpsComponent().processQueueForPlayer(event.getPlayer());
        event.getPlayer().retrieveCookie(WarpsComponent.WARP_COOKIE_KEY).thenAcceptAsync(cookie -> {
            if (cookie != null) {
                ChatHelper.logDebug("Player has a warp cookie, processing it.");
                NavigationModule.getInstance().getWarpsComponent().processCookie(event.getPlayer(), cookie);
            } else {
                ChatHelper.logDebug("Player does not have a warp cookie.");
            }
        });
    }
}

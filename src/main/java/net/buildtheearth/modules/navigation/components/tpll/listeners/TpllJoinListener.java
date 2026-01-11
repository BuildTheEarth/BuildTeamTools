package net.buildtheearth.modules.navigation.components.tpll.listeners;

import com.alpsbte.alpslib.utils.ChatHelper;
import net.buildtheearth.modules.navigation.NavigationModule;
import net.buildtheearth.modules.navigation.components.tpll.TpllComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class TpllJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        ChatHelper.logDebug("Player joined. Processing tpll queue for them. Event fired.");
        NavigationModule.getInstance().getTpllComponent().processQueueForPlayer(event.getPlayer());
        event.getPlayer().retrieveCookie(TpllComponent.TPLL_COOKIE_KEY).thenAcceptAsync(cookie -> {
            if (cookie != null) {
                ChatHelper.logDebug("Player has a TPLL cookie, processing it.");
                NavigationModule.getInstance().getTpllComponent().processCookie(event.getPlayer(), cookie);
                event.getPlayer().storeCookie(TpllComponent.TPLL_COOKIE_KEY, new byte[0]); // Reset the cookie
            } else {
                ChatHelper.logDebug("Player does not have a TPLL cookie.");
            }
        });
    }
}

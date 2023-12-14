package net.buildtheearth.modules.navigator.listeners;

import net.buildtheearth.Main;
import net.buildtheearth.modules.navigator.Navigator;
import net.buildtheearth.modules.utils.io.ConfigPaths;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class NavigatorJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        boolean isEnabled = Main.instance.getConfig().getBoolean(ConfigPaths.NAVIGATOR_ITEM_ENABLED, false);

        if(!isEnabled) return;

        // Set the navigator item
        Player player = event.getPlayer();
        player.getInventory().setItem(Navigator.getSlot(), Navigator.getItem());
    }
}

package net.buildtheearth.modules.navigation.components.navigator.listeners;

import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.navigation.NavigationModule;
import net.buildtheearth.modules.utils.io.ConfigPaths;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class NavigatorJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        boolean isEnabled = BuildTeamTools.getInstance().getConfig().getBoolean(ConfigPaths.NAVIGATOR_ITEM_ENABLED, false);

        if(!isEnabled) return;

        // Set the navigator item
        Player player = event.getPlayer();
        player.getInventory().setItem(NavigationModule.getInstance().getNavigatorComponent().getSlot(), NavigationModule.getInstance().getNavigatorComponent().getItem());
    }
}

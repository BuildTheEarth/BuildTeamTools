package net.buildtheearth.buildteamtools.modules.navigation.components.navigator.listeners;

import net.buildtheearth.buildteamtools.modules.navigation.components.navigator.NavigatorComponent;
import net.buildtheearth.buildteamtools.utils.io.NavigationConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public final class NavigatorJoinListener implements Listener {

    private final NavigatorComponent navigator;
    private final NavigationConfig config;

    public NavigatorJoinListener(@NonNull NavigatorComponent navigator, NavigationConfig config) {
        this.navigator = navigator;
        this.config = config;
    }

    @EventHandler
    public void onJoin(@NonNull PlayerJoinEvent event) {
        Inventory inventory = event.getPlayer().getInventory();
        ItemStack navigatorItem = navigator.getItem();
        int navigatorSlot = navigator.getSlot();

        inventory.removeItem(navigatorItem);
        if (config.navigatorHotbarItem().navEnabled()) inventory.setItem(navigatorSlot, navigatorItem);
    }
}

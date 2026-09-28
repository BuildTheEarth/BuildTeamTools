package net.buildtheearth.buildteamtools.modules.navigation.components.navigator.listeners;

import net.buildtheearth.buildteamtools.modules.navigation.components.navigator.NavigatorComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

public final class NavigatorJoinListener implements Listener {

    private final NavigatorComponent navigator;
    private final boolean giveNavigatorOnJoin;

    public NavigatorJoinListener(@NonNull NavigatorComponent navigator, boolean giveNavigatorOnJoin) {
        this.navigator = navigator;
        this.giveNavigatorOnJoin = giveNavigatorOnJoin;
    }

    @EventHandler
    public void onJoin(@NonNull PlayerJoinEvent event) {
        Inventory inventory = event.getPlayer().getInventory();
        ItemStack navigatorItem = navigator.getItem();
        int navigatorSlot = navigator.getSlot();

        inventory.removeItem(navigatorItem);
        if (giveNavigatorOnJoin) inventory.setItem(navigatorSlot, navigatorItem);
    }
}

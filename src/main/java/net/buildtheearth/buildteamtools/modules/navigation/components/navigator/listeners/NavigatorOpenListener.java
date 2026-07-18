package net.buildtheearth.buildteamtools.modules.navigation.components.navigator.listeners;

import net.buildtheearth.buildteamtools.modules.navigation.NavigationModule;
import net.buildtheearth.buildteamtools.modules.navigation.menu.MainMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.PlayerInventory;
import org.jspecify.annotations.NonNull;

public class NavigatorOpenListener implements Listener {

    @EventHandler
    public void interactEvent(@NonNull PlayerInteractEvent event) {
        if (event.getItem() == null) return;

        if (NavigationModule.getInstance().getNavigatorComponent().isItem(event.getItem())) {
            event.setCancelled(true);
            new MainMenu(event.getPlayer());
        }
    }

    // If the player clicks on the navigator in their inventory, open the gui.
    @EventHandler
    public void onClick(@NonNull InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;

        if (!(NavigationModule.getInstance().getNavigatorComponent().isItem(event.getCurrentItem()) && event.getInventory() instanceof PlayerInventory))
            return;

        if (!(event.getWhoClicked() instanceof Player player)) return;

        event.setCancelled(true);

        // If item is not in the correct slot, delete it.
        if (event.getSlot() != NavigationModule.getInstance().getNavigatorComponent().getSlot()) {
            player.getInventory().clear(event.getSlot());
            return;
        }

        // Opens the navigator.
        new MainMenu(player);
    }


    /*
    The following events are to prevent the navigator being moved in the inventory,
    causing duplicate items which are difficult to remove.
     */
    @EventHandler
    public void swapHands(@NonNull PlayerSwapHandItemsEvent e) {
        if (NavigationModule.getInstance().getNavigatorComponent().isItem(e.getOffHandItem())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void dropItem(@NonNull PlayerDropItemEvent e) {
        if (NavigationModule.getInstance().getNavigatorComponent().isItem(e.getItemDrop().getItemStack())) {
            e.setCancelled(true);

            e.getPlayer().getInventory().setItem(NavigationModule.getInstance().getNavigatorComponent().getSlot(), null);
            e.getPlayer().updateInventory();
        }
    }

    @EventHandler
    public void moveItem(@NonNull InventoryMoveItemEvent e) {
        if (e.getItem().equals(NavigationModule.getInstance().getNavigatorComponent().getItem())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void moveItem(@NonNull InventoryDragEvent e) {
        if (e.getOldCursor().equals(NavigationModule.getInstance().getNavigatorComponent().getItem())) {
            e.setCancelled(true);
        }

        if (e.getCursor() == null) {
            return;
        }

        if (e.getCursor().equals(NavigationModule.getInstance().getNavigatorComponent().getItem())) {
            e.setCancelled(true);
        }
    }
}

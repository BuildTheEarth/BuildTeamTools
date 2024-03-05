package net.buildtheearth.modules.navigation.components.navigator.listeners;

import net.buildtheearth.modules.navigation.menu.MainMenu;
import net.buildtheearth.modules.navigation.NavigationModule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class NavigatorOpenListener implements Listener {

    @EventHandler
    public void interactEvent(PlayerInteractEvent event) {
        if (event.getItem() == null) return;

        if (event.getItem().getType() == NavigationModule.getInstance().getNavigatorComponent().getItem().getType()) {
            event.setCancelled(true);
            //Open navigator.
            new MainMenu(event.getPlayer());
        }
    }

    //If the player clicks on the navigator in their inventory, open the gui.
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;

        //Checks to see if the navigator item was clicked on
        if (event.getCurrentItem().getType() != NavigationModule.getInstance().getNavigatorComponent().getItem().getType()) return;

        //Extract the player
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        //Cancel the event
        event.setCancelled(true);

        //If item is not in the correct slot, delete it.
        if (event.getSlot() != NavigationModule.getInstance().getNavigatorComponent().getSlot()) {
            player.getInventory().clear(event.getSlot());
            return;
        }

        //Opens the navigator.
        new MainMenu(player);
    }


    /*

    The following events are to prevent the navigator being moved in the inventory,
    causing duplicate items which are difficult to remove.

     */

    @EventHandler
    public void swapHands(PlayerSwapHandItemsEvent e) {

        if (e.getOffHandItem() == null) {
            return;
        }

        if (e.getOffHandItem().equals(NavigationModule.getInstance().getNavigatorComponent().getItem())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void dropItem(PlayerDropItemEvent e) {

        if (e.getItemDrop().getItemStack().equals(NavigationModule.getInstance().getNavigatorComponent().getItem())) {
            e.setCancelled(true);

            e.getPlayer().getInventory().setItem(NavigationModule.getInstance().getNavigatorComponent().getSlot(), null);
            e.getPlayer().updateInventory();
        }

    }

    @EventHandler
    public void moveItem(InventoryMoveItemEvent e) {
        if (e.getItem().equals(NavigationModule.getInstance().getNavigatorComponent().getItem())) {
            e.setCancelled(true);
        }

    }

    @EventHandler
    public void moveItem(InventoryDragEvent e) {
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

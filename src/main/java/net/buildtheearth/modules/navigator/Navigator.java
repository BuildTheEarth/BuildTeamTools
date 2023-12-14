package net.buildtheearth.modules.navigator;

import net.buildtheearth.Main;
import net.buildtheearth.modules.utils.ChatHelper;
import net.buildtheearth.modules.utils.io.ConfigPaths;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Navigator {

    /*
    Toggles the navigator on or off based on the current state
     */
    public static void toggle(Player player) {
        Inventory inventory = player.getInventory();

        if(!inventory.contains(getItem())) {
            inventory.setItem(getSlot(), getItem());
            player.sendMessage(ChatHelper.successful("You turned the navigator %s.", "on"));
        } else {
            inventory.remove(getItem());
            player.sendMessage(ChatHelper.successful("You turned the navigator %s.", "off"));
        }
    }

    //TODO
    public static ItemStack getItem() {
        return new ItemStack(Material.NETHER_STAR);
    }

    public static short getSlot() {
        return (short) Main.instance.getConfig().getInt(ConfigPaths.NAVIGATOR_ITEM_SLOT);
    }
}

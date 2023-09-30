package net.buildtheearth.modules.navigator;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Navigator {

    /**
     * For every currently online player, checks if the navigator is in the correct slot.
     * This should be done based on the user's preference.
     */
    public static void checkNavigatorSlot() {
        //TODO IMPLEMENT

        /*
        if (!proxyManager.isConnected()) return;
		if (!Main.instance.getConfig().getBoolean("navigator.enabled")) return;
		ItemStack navLocation;
		//Cycles through all players
		for (Player player: Bukkit.getOnlinePlayers()) {
			//If navigator is enabled check if they have it in the relevant slot.
			if ((Boolean) userPreferences.get(PreferenceType.NavigatorEnabled).get(player.getUniqueId()))
			{
				//Gets what's in the relevant slot
				navLocation = player.getInventory().getItem(this.iSlot);

				//Give the player the inventory
				if (navLocation == null)
				{
					player.getInventory().setItem(this.iSlot, this.navigator);
				}
				else if (!(navLocation.equals(this.navigator)))
				{
					player.getInventory().setItem(this.iSlot, this.navigator);
				}
			}
		}
        */
    }

    //TODO
    public static ItemStack getItem() {
        return new ItemStack(Material.NETHER_STAR);
    }

    public static short getSlot(Player player) {
        return 8;
    }
}

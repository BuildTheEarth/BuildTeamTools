package net.buildtheearth.modules.navigator.menu;


import com.alpsbte.alpslib.utils.item.ItemBuilder;
import net.buildtheearth.modules.utils.Item;
import net.buildtheearth.modules.utils.menus.AbstractMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.ArrayList;
import java.util.Collections;

/**
 * The ExploreMenu for the BTE universal navigator.<br>
 * <br>
 * Accessed from here is the warp menu, the plot system, the region menu and the building tools (generator) menu.
 * All of these icons can be enabled and disabled.<br>
 * <br>
 * The menu has 3 rows and the centre row is the only occupied row. The layout depends on what icons are enabled in config.
 */
public class ExploreMenu extends AbstractMenu {
    private static final String inventoryName = "Explore Menu";

    public ExploreMenu(Player menuPlayer) {
        super(4, inventoryName, menuPlayer);
    }

    @Override
    protected void setPreviewItems() {
        // Create the continent items
        for (Continent continent : Continent.values()) {
            ArrayList<String> continentLore = new ArrayList<>(Collections.singletonList(ChatColor.GRAY + "Visit countries in " + continent.label));
            getMenu().getSlot(continent.slot).setItem(Item.create(Material.COMPASS,"§e§l" + continent.label, 1, continentLore));
        }

        super.setPreviewItems();
    }

    @Override
    protected void setItemClickEventsAsync() {
        // Set click events for the continent items
        for(Continent continent : Continent.values()) {
            getMenu().getSlot(continent.slot).setClickHandler((clickPlayer, clickInformation) -> {
                clickPlayer.closeInventory();

                if(continent.equals(Continent.Africa))
                    ;// TODO implement that the player gets information about the BTE Africa server when clicking on Africa
                else
                    // TODO implement a country menu
                    ;// new CountrySelectorMenu(continents[iSlot], player, bNetworkConnected);

            });
        }
    }

    @Override
    protected void setMenuItemsAsync() {}


    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 7).setName(" ").build())
                .pattern("111111111")
                .pattern("010101010")
                .pattern("111101111")
                .pattern("111111111")
                .build();
    }

    public enum Continent {
        North_America("North America", 9),
        South_America("South America", 11),
        Europe("Europe", 13),
        Africa("Africa", 15),
        Asia("Asia", 17),
        Other("Other", 22);

        public final String label;
        public final int slot;

        Continent(String label, int slot) {
            this.label = label;
            this.slot = slot;
        }
    }

}


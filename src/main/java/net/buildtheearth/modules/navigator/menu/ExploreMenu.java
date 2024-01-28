package net.buildtheearth.modules.navigator.menu;


import com.alpsbte.alpslib.utils.item.ItemBuilder;
import net.buildtheearth.modules.network.model.Continent;
import net.buildtheearth.modules.utils.ChatHelper;
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
    private final Player menuPlayer;

    public ExploreMenu(Player menuPlayer) {
        super(4, inventoryName, menuPlayer);
        this.menuPlayer = menuPlayer;
    }

    @Override
    protected void setPreviewItems() {
        // Create the continent items
        for (Continent continent : Continent.values()) {
            ArrayList<String> continentLore = new ArrayList<>(Collections.singletonList(ChatHelper.colorize(ChatColor.GRAY, ChatColor.GRAY, "Visit countries in %s", continent.getLabel())));
            getMenu().getSlot(continent.getSlot()).setItem(Item.create(Material.COMPASS,"§e§l" + continent.getLabel(), 1, continentLore));
        }

        super.setPreviewItems();
    }

    @Override
    protected void setItemClickEventsAsync() {
        // Set click events for the continent items
        for(Continent continent : Continent.values()) {
            getMenu().getSlot(continent.getSlot()).setClickHandler((clickPlayer, clickInformation) -> {
                clickPlayer.closeInventory();

                System.out.println("Continent before creating CountrySelectorMenu: " + continent); // Add this line

                if(continent.equals(Continent.AFRICA)) {
                    // TODO implement that the player gets information about the BTE Africa server when clicking on Africa
                } else {
                    new CountrySelectorMenu(clickPlayer, continent);
                }
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

}


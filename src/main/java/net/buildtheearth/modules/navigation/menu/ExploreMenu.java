package net.buildtheearth.modules.navigation.menu;


import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.modules.network.model.Continent;
import net.buildtheearth.utils.ChatHelper;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.MenuItems;
import net.buildtheearth.utils.menus.AbstractMenu;
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

    public ExploreMenu(Player menuPlayer, boolean autoLoad) {
        super(4, inventoryName, menuPlayer, autoLoad);
    }

    @Override
    protected void setPreviewItems() {
        // Create the continent items
        for (Continent continent : Continent.values()) {
            ArrayList<String> continentLore = new ArrayList<>(Collections.singletonList(ChatHelper.getStandardString(false, "Visit countries in %s", continent.getLabel())));
            getMenu().getSlot(continent.getSlot()).setItem(Item.create(XMaterial.COMPASS.get(), "§e§l" + continent.getLabel(), 1, continentLore));
        }

        super.setPreviewItems();
    }

    @Override
    protected void setItemClickEventsAsync() {
        // Set click events for the continent items
        for(Continent continent : Continent.values()) {
            getMenu().getSlot(continent.getSlot()).setClickHandler((clickPlayer, clickInformation) -> {
                clickPlayer.closeInventory();

                ChatHelper.logDebug("Clicked Continent before creating CountrySelectorMenu: %s", continent.getLabel());

                new CountrySelectorMenu(clickPlayer, continent, true);
            });
        }
    }


    @Override
    protected void setMenuItemsAsync() {}


    @Override
    protected Mask getMask() {


        return BinaryMask.builder(getMenu())
                .item(MenuItems.ITEM_BACKGROUND)
                .pattern("111111111")
                .pattern("010101010")
                .pattern("111101111")
                .pattern("111111111")
                .build();
    }

}


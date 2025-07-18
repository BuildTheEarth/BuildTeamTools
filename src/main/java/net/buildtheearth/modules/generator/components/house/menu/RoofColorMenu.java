package net.buildtheearth.modules.generator.components.house.menu;

import net.buildtheearth.modules.generator.GeneratorModule;
import net.buildtheearth.modules.generator.components.house.HouseFlag;
import net.buildtheearth.modules.generator.components.house.HouseSettings;
import net.buildtheearth.modules.generator.components.house.RoofType;
import net.buildtheearth.modules.generator.model.Settings;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.MenuItems;
import net.buildtheearth.utils.menus.BlockListMenu;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RoofColorMenu extends BlockListMenu {

    public static final String ROOF_TYPE_INV_NAME = "Choose a Roof Color";

    public RoofColorMenu(Player player, boolean autoLoad) {
        super(player, ROOF_TYPE_INV_NAME, getRoofBlocks(player), new RoofTypeMenu(player, false), autoLoad);
    }

    @Override
    protected void setItemClickEventsAsync() {
        setSwitchPageItemClickEvents(SWITCH_PAGE_ITEM_SLOT);

        // Set click event for next item
        if(canProceed())
            getMenu().getSlot(NEXT_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
                Settings settings = GeneratorModule.getInstance().getHouse().getPlayerSettings().get(clickPlayer.getUniqueId());

                if(!(settings instanceof HouseSettings))
                    return;

                HouseSettings houseSettings = (HouseSettings) settings;
                houseSettings.setValue(HouseFlag.ROOF_COLOR, Item.createStringFromItemStringList(selectedMaterials));

                clickPlayer.closeInventory();
                clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

                new BaseColorMenu(clickPlayer, true);
            });
    }


    /**
     * Get the roof blocks for the menu.
     * Depending on the roof type, the blocks will be different.
     *
     * @param player - the player to get the roof blocks for
     * @return the roof blocks
     */
    private static List<ItemStack> getRoofBlocks(Player player) {
        RoofType roofType = (RoofType) GeneratorModule.getInstance().getHouse().getPlayerSettings().get(player.getUniqueId()).getValues().get(HouseFlag.ROOF_TYPE);

        if(roofType == null)
            return new ArrayList<>();

        switch (roofType){
            case FLATTER_SLABS:
            case MEDIUM_SLABS:
            case STEEP_SLABS:
                return MenuItems.getSlabs();

            case STAIRS:
                return MenuItems.getStairs();

            case FLAT:
                ArrayList<ItemStack> items = new ArrayList<>();
                items.addAll(MenuItems.getCarpets());
                items.addAll(MenuItems.getSlabs());

                return items;

            default:
                return new ArrayList<>();
        }
    }
}

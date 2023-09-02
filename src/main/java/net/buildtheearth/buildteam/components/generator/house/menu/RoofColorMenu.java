package net.buildtheearth.buildteam.components.generator.house.menu;

import net.buildtheearth.Main;
import net.buildtheearth.buildteam.components.generator.Settings;
import net.buildtheearth.buildteam.components.generator.house.HouseFlag;
import net.buildtheearth.buildteam.components.generator.house.HouseSettings;
import net.buildtheearth.buildteam.components.generator.house.RoofType;
import net.buildtheearth.utils.*;
import net.buildtheearth.utils.menus.BlockListMenu;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RoofColorMenu extends BlockListMenu {

    public static final String ROOF_TYPE_INV_NAME = "Choose a Roof Color";

    public RoofColorMenu(Player player) {
        super(player, ROOF_TYPE_INV_NAME, getRoofBlocks(player));
    }

    @Override
    protected void setItemClickEventsAsync() {
        setSwitchPageItemClickEvents(SWITCH_PAGE_ITEM_SLOT);

        // Set click event for next item
        if(canProceed())
            getMenu().getSlot(NEXT_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
                Settings settings = Main.buildTeamTools.getGenerator().getHouse().getPlayerSettings().get(clickPlayer.getUniqueId());

                if(!(settings instanceof HouseSettings))
                    return;

                HouseSettings houseSettings = (HouseSettings) settings;
                houseSettings.setValue(HouseFlag.ROOF_COLOR, Item.createStringFromItemList(selectedMaterials));

                clickPlayer.closeInventory();
                clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

                new BaseColorMenu(clickPlayer);
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
        RoofType roofType = RoofType.byString(Main.buildTeamTools.getGenerator().getHouse().getPlayerSettings().get(player.getUniqueId()).getValues().get(HouseFlag.ROOF_TYPE));

        if(roofType == null)
            return new ArrayList<>();

        switch (roofType){
            case FLATTER_SLABS:
            case MEDIUM_SLABS:
            case STEEP_SLABS:
                return Arrays.asList(MenuItems.SLABS);

            case STAIRS:
                return Arrays.asList(MenuItems.STAIRS);

            case FLAT:
                ArrayList<ItemStack> items = new ArrayList<>();
                for(int i = 0; i <= 15; i++)
                    items.add(Item.create(Material.CARPET,null, (short) i, null));

                items.addAll(Arrays.asList(MenuItems.SLABS));

                return items;

            default: return new ArrayList<>();
        }
    }
}

package net.buildtheearth.modules.generator.components.house.menu;

import net.buildtheearth.modules.generator.GeneratorModule;
import net.buildtheearth.modules.generator.menu.GeneratorMenu;
import net.buildtheearth.modules.generator.model.Settings;
import net.buildtheearth.modules.generator.components.house.HouseFlag;
import net.buildtheearth.modules.generator.components.house.HouseSettings;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.MenuItems;
import net.buildtheearth.utils.menus.BlockListMenu;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class WallColorMenu extends BlockListMenu {

    public static final String WALL_COLOR_INV_NAME = "Choose a Wall Block";

    public WallColorMenu(Player player, boolean autoLoad) {
        super(player, WALL_COLOR_INV_NAME, MenuItems.getWallBlocks(), new GeneratorMenu(player, false), autoLoad);
    }

    @Override
    protected void setItemClickEventsAsync() {
        super.setItemClickEventsAsync();

        // Set click event for next item
        if(canProceed())
            getMenu().getSlot(NEXT_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
                Settings settings = GeneratorModule.getInstance().getHouse().getPlayerSettings().get(clickPlayer.getUniqueId());

                if(!(settings instanceof HouseSettings))
                    return;

                HouseSettings houseSettings = (HouseSettings) settings;
                houseSettings.setValue(HouseFlag.WALL_COLOR, Item.createStringFromItemList(selectedMaterials));

                clickPlayer.closeInventory();
                clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
                new RoofTypeMenu(clickPlayer, true);
            });
    }
}

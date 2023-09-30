package net.buildtheearth.modules.generator.modules.house.menu;

import net.buildtheearth.Main;
import net.buildtheearth.modules.generator.model.Settings;
import net.buildtheearth.modules.generator.modules.house.HouseFlag;
import net.buildtheearth.modules.generator.modules.house.HouseSettings;
import net.buildtheearth.modules.utils.Item;
import net.buildtheearth.modules.utils.MenuItems;
import net.buildtheearth.modules.utils.menus.BlockListMenu;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class WallColorMenu extends BlockListMenu {

    public static String WALL_COLOR_INV_NAME = "Choose a Wall Block";


    public WallColorMenu(Player player) {
        super(player, WALL_COLOR_INV_NAME, MenuItems.getBlocksByColor());
    }

    @Override
    protected void setItemClickEventsAsync() {
        super.setItemClickEventsAsync();

        // Set click event for next item
        if(canProceed())
            getMenu().getSlot(NEXT_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
                Settings settings = Main.buildTeamTools.getGenerator().getHouse().getPlayerSettings().get(clickPlayer.getUniqueId());

                if(!(settings instanceof HouseSettings))
                    return;

                HouseSettings houseSettings = (HouseSettings) settings;
                houseSettings.setValue(HouseFlag.WALL_COLOR, Item.createStringFromItemList(selectedMaterials));

                clickPlayer.closeInventory();
                clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
                new RoofTypeMenu(clickPlayer);
            });
    }
}

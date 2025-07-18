package net.buildtheearth.modules.generator.components.road.menu;

import net.buildtheearth.modules.generator.GeneratorModule;
import net.buildtheearth.modules.generator.model.Settings;
import net.buildtheearth.modules.generator.components.road.RoadFlag;
import net.buildtheearth.modules.generator.components.road.RoadSettings;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.MenuItems;
import net.buildtheearth.utils.menus.BlockListMenu;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SidewalkColorMenu extends BlockListMenu {

    public static final String ROAD_COLOR_INV_NAME = "Choose a Sidewalk Block";


    public SidewalkColorMenu(Player player, boolean autoLoad) {
        super(player, ROAD_COLOR_INV_NAME, MenuItems.getSolidBlocks(), new RoadColorMenu(player, false), autoLoad);
    }

    @Override
    protected void setItemClickEventsAsync() {
        super.setItemClickEventsAsync();

        // Set click event for next item
        if(canProceed())
            getMenu().getSlot(NEXT_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
                Settings settings = GeneratorModule.getInstance().getRoad().getPlayerSettings().get(clickPlayer.getUniqueId());

                if(!(settings instanceof RoadSettings))
                    return;

                RoadSettings roadSettings = (RoadSettings) settings;
                roadSettings.setValue(RoadFlag.SIDEWALK_MATERIAL, Item.createStringFromItemStringList(selectedMaterials));

                clickPlayer.closeInventory();
                clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
                new AdvancedSettingsMenu(clickPlayer, true);
            });
    }
}

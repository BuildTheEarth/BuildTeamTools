package net.buildtheearth.buildteamtools.modules.generator.components.road.menu;

import net.buildtheearth.buildteamtools.i18n.Messages;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import com.alpsbte.alpslib.utils.item.Item;
import net.buildtheearth.buildteamtools.modules.generator.GeneratorModule;
import net.buildtheearth.buildteamtools.modules.generator.components.road.RoadFlag;
import net.buildtheearth.buildteamtools.modules.generator.components.road.RoadSettings;
import net.buildtheearth.buildteamtools.modules.generator.model.Settings;
import net.buildtheearth.buildteamtools.utils.MenuItems;
import net.buildtheearth.buildteamtools.utils.menus.BlockListMenu;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SidewalkColorMenu extends BlockListMenu {


    public SidewalkColorMenu(Player player, boolean autoLoad) {
        super(player, Messages.plain(player, MessageKeys.Generator.Road.SIDEWALK_TITLE), MenuItems.getSolidBlocks(), new RoadColorMenu(player, false), autoLoad);
    }

    @Override
    protected void setItemClickEventsAsync() {
        super.setItemClickEventsAsync();

        // Set click event for next item
        if (canProceed())
            getMenu().getSlot(NEXT_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
                Settings settings = GeneratorModule.getInstance().getRoad().getPlayerSettings().get(clickPlayer.getUniqueId());

                if (!(settings instanceof RoadSettings roadSettings))
                    return;

                roadSettings.setValue(RoadFlag.SIDEWALK_MATERIAL, Item.createStringFromItemStringList(selectedMaterials));

                clickPlayer.closeInventory();
                clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
                new AdvancedSettingsMenu(clickPlayer, true);
            });
    }
}

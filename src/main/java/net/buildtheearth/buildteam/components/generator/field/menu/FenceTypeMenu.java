package net.buildtheearth.buildteam.components.generator.field.menu;

import net.buildtheearth.Main;
import net.buildtheearth.buildteam.components.generator.Settings;
import net.buildtheearth.buildteam.components.generator.field.FieldFlag;
import net.buildtheearth.buildteam.components.generator.field.FieldSettings;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.MenuItems;
import net.buildtheearth.utils.menus.BlockListMenu;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class FenceTypeMenu extends BlockListMenu {

    public static String FENCE_TYPE_INV_NAME = "Choose a Fence Type";


    public FenceTypeMenu(Player player) {
        super(player, FENCE_TYPE_INV_NAME, MenuItems.getFences());
    }

    @Override
    protected void setItemClickEventsAsync() {
        super.setItemClickEventsAsync();

        // Set click event for next item
        if (canProceed())
            getMenu().getSlot(NEXT_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
                Settings settings = Main.buildTeamTools.getGenerator().getField().getPlayerSettings().get(clickPlayer.getUniqueId());

                if (!(settings instanceof FieldSettings))
                    return;

                FieldSettings fieldSettings = (FieldSettings) settings;
                fieldSettings.setValue(FieldFlag.FENCE, Item.createStringFromItemList(selectedMaterials));

                clickPlayer.closeInventory();
                clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

                Main.getBuildTeam().getGenerator().getField().generate(clickPlayer);
            });
    }
}

package net.buildtheearth.buildteamtools.modules.generator.components.field.menu;


import com.alpsbte.alpslib.utils.item.Item;
import net.buildtheearth.buildteamtools.modules.generator.GeneratorModule;
import net.buildtheearth.buildteamtools.modules.generator.components.field.FieldFlag;
import net.buildtheearth.buildteamtools.modules.generator.components.field.FieldSettings;
import net.buildtheearth.buildteamtools.modules.generator.model.Settings;
import net.buildtheearth.buildteamtools.utils.MenuItems;
import net.buildtheearth.buildteamtools.utils.menus.BlockListMenu;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class FenceTypeMenu extends BlockListMenu {

    public static String FENCE_TYPE_INV_NAME = "Choose a Fence Type";


    public FenceTypeMenu(Player player, boolean autoLoad) {
        super(player, FENCE_TYPE_INV_NAME, MenuItems.getFences(), new CropTypeMenu(player, false), autoLoad);
    }

    @Override
    protected void setItemClickEventsAsync() {
        super.setItemClickEventsAsync();

        // Set click event for next item
        if (canProceed())
            getMenu().getSlot(NEXT_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
                Settings settings = GeneratorModule.getInstance().getField().getPlayerSettings().get(clickPlayer.getUniqueId());

                if (!(settings instanceof FieldSettings))
                    return;

                FieldSettings fieldSettings = (FieldSettings) settings;
                fieldSettings.setValue(FieldFlag.FENCE, Item.createStringFromItemStringList(selectedMaterials));

                clickPlayer.closeInventory();
                clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

                GeneratorModule.getInstance().getField().generate(clickPlayer);
            });
    }
}

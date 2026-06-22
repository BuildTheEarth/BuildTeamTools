package net.buildtheearth.buildteamtools.modules.generator.components.rail.menu;

import com.alpsbte.alpslib.utils.item.Item;
import net.buildtheearth.buildteamtools.modules.generator.GeneratorModule;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.RailFlag;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.RailSettings;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.RailType;
import net.buildtheearth.buildteamtools.modules.generator.menu.GeneratorMenu;
import net.buildtheearth.buildteamtools.modules.generator.model.Settings;
import net.buildtheearth.buildteamtools.utils.menus.NameListMenu;
import org.apache.commons.lang3.tuple.MutablePair;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RailTypeMenu extends NameListMenu {

    public static final String RAIL_TYPE_INV_NAME = "Choose a Rail Type";

    public RailTypeMenu(Player player, boolean autoLoad) {
        super(player, RAIL_TYPE_INV_NAME, getRailTypes(), new GeneratorMenu(player, false), autoLoad);
    }

    private static @NonNull List<MutablePair<ItemStack, String>> getRailTypes() {
        List<MutablePair<ItemStack, String>> railTypes = new ArrayList<>();

        for (RailType railType : RailType.values()) {
            railTypes.add(new MutablePair<>(
                    Item.create(Objects.requireNonNull(railType.getIcon().get()), railType.getDisplayName()),
                    railType.getIdentifier()
            ));
        }

        return railTypes;
    }

    @Override
    protected void setItemClickEventsAsync() {
        super.setItemClickEventsAsync();

        if (canProceed())
            getMenu().getSlot(NEXT_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
                Settings settings = GeneratorModule.getInstance().getRail().getPlayerSettings().get(clickPlayer.getUniqueId());

                if (!(settings instanceof RailSettings railSettings))
                    return;

                railSettings.setValue(RailFlag.RAIL_TYPE, selectedNames.getFirst());

                clickPlayer.closeInventory();
                clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

                GeneratorModule.getInstance().getRail().generate(clickPlayer);
            });
    }
}

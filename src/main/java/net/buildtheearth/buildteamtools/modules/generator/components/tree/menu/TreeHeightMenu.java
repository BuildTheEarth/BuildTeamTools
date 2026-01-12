package net.buildtheearth.buildteamtools.modules.generator.components.tree.menu;

import com.alpsbte.alpslib.utils.item.Item;
import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.buildteamtools.modules.generator.GeneratorModule;
import net.buildtheearth.buildteamtools.modules.generator.components.tree.TreeFlag;
import net.buildtheearth.buildteamtools.modules.generator.components.tree.TreeSettings;
import net.buildtheearth.buildteamtools.modules.generator.model.Settings;
import net.buildtheearth.buildteamtools.utils.menus.NameListMenu;
import net.daporkchop.lib.common.misc.Tuple;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class TreeHeightMenu extends NameListMenu {

    public static final String TREE_TYPE_INV_NAME = "Choose a Tree Width";

    public TreeHeightMenu(Player player, boolean autoLoad) {
        super(player, TREE_TYPE_INV_NAME, getTreeHeights(), new TreeTypeMenu(player, false), autoLoad);
    }

    /**
     * Get a list of all tree widths
     */
    private static @NonNull List<Tuple<ItemStack, String>> getTreeHeights() {
        List<Tuple<ItemStack, String>> treeHeights = new ArrayList<>();

        treeHeights.add(new Tuple<>(new Item(XMaterial.LIME_CONCRETE.parseItem()).setDisplayName("Any").build(), "Any"));

        GeneratorModule.getInstance().getTree().getHeights().forEach(h ->
                treeHeights.add(new Tuple<>(Item.create(XMaterial.PAPER.get(), StringUtils.capitalize(h)), h)));

        return treeHeights;
    }

    @Override
    protected void setItemClickEventsAsync() {
        super.setItemClickEventsAsync();

        // Set click event for next item
        if (canProceed())
            getMenu().getSlot(NEXT_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
                Settings settings = GeneratorModule.getInstance().getTree().getPlayerSettings().get(clickPlayer.getUniqueId());

                if (!(settings instanceof TreeSettings treeSettings))
                    return;

                treeSettings.setValue(TreeFlag.HEIGHT, selectedNames.getFirst());

                clickPlayer.closeInventory();
                clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

                new TreeWidthMenu(clickPlayer, true);
            });
    }
}

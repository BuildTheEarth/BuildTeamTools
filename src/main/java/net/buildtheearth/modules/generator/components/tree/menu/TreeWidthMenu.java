package net.buildtheearth.modules.generator.components.tree.menu;

import com.alpsbte.alpslib.utils.item.Item;
import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.modules.generator.GeneratorModule;
import net.buildtheearth.modules.generator.components.tree.TreeFlag;
import net.buildtheearth.modules.generator.components.tree.TreeSettings;
import net.buildtheearth.modules.generator.components.tree.TreeWidth;
import net.buildtheearth.modules.generator.model.Settings;
import net.buildtheearth.utils.menus.NameListMenu;
import net.daporkchop.lib.common.misc.Tuple;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class TreeWidthMenu extends NameListMenu {

    public static final String TREE_TYPE_INV_NAME = "Choose a Tree Width";

    public TreeWidthMenu(Player player, boolean autoLoad) {
        super(player, TREE_TYPE_INV_NAME, getTreeWidths(), new TreeTypeMenu(player, false), autoLoad);
    }

    /**
     * Get a list of all tree widths
     */
    private static @NonNull List<Tuple<ItemStack, String>> getTreeWidths() {
        List<Tuple<ItemStack, String>> treeWidths = new ArrayList<>();

        treeWidths.add(new Tuple<>(new Item(XMaterial.LIME_CONCRETE.parseItem()).setDisplayName("Any").build(), "Any"));

        for (TreeWidth treeWidth : TreeWidth.values())
            treeWidths.add(new Tuple<>(Item.create(XMaterial.JUNGLE_SAPLING.get(), StringUtils.capitalize(treeWidth.getName())), treeWidth.getName()));

        return treeWidths;
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

                treeSettings.setValue(TreeFlag.WIDTH, selectedNames.getFirst());

                clickPlayer.closeInventory();
                clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

                GeneratorModule.getInstance().getTree().generate(clickPlayer);
            });
    }
}

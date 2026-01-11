package net.buildtheearth.modules.generator.components.tree.menu;

import com.alpsbte.alpslib.utils.item.Item;
import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.modules.generator.GeneratorModule;
import net.buildtheearth.modules.generator.components.tree.TreeFlag;
import net.buildtheearth.modules.generator.components.tree.TreeSettings;
import net.buildtheearth.modules.generator.components.tree.TreeType;
import net.buildtheearth.modules.generator.menu.GeneratorMenu;
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

public class TreeTypeMenu extends NameListMenu {

    public static final String TREE_TYPE_INV_NAME = "Choose a Tree Type";

    public TreeTypeMenu(Player player, boolean autoLoad) {
        super(player, TREE_TYPE_INV_NAME, getTreeTypes(), new GeneratorMenu(player, false), autoLoad);
    }

    /** Get a list of all tree types */
    private static @NonNull List<Tuple<ItemStack, String>> getTreeTypes() {
        List<Tuple<ItemStack, String>> treeTypes = new ArrayList<>();

        treeTypes.add(new Tuple<>(new Item(XMaterial.LIME_CONCRETE.parseItem()).setDisplayName("Any").build(), "Any"));

        for(TreeType treeType : TreeType.values())
            treeTypes.add(new Tuple<>(Item.create(XMaterial.OAK_SAPLING.get(), StringUtils.capitalize(treeType.getName())), treeType.getName()));

        return treeTypes;
    }

    @Override
    protected void setItemClickEventsAsync() {
        super.setItemClickEventsAsync();

        // Set click event for next item
        if(canProceed())
            getMenu().getSlot(NEXT_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
                Settings settings = GeneratorModule.getInstance().getTree().getPlayerSettings().get(clickPlayer.getUniqueId());

                if (!(settings instanceof TreeSettings treeSettings))
                    return;

                treeSettings.setValue(TreeFlag.TYPE, selectedNames.getFirst());

                clickPlayer.closeInventory();
                clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

                new TreeHeightMenu(clickPlayer, true);
            });
    }
}

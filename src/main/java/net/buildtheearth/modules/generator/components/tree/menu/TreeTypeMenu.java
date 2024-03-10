package net.buildtheearth.modules.generator.components.tree.menu;

import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.modules.generator.GeneratorModule;
import net.buildtheearth.modules.generator.menu.GeneratorMenu;
import net.buildtheearth.modules.generator.model.Settings;
import net.buildtheearth.modules.generator.components.road.RoadFlag;
import net.buildtheearth.modules.generator.components.road.RoadSettings;
import net.buildtheearth.modules.generator.components.tree.TreeType;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.menus.BlockListMenu;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TreeTypeMenu extends BlockListMenu {

    public static final String TREE_TYPE_INV_NAME = "Choose a Tree Type";

    public TreeTypeMenu(Player player, boolean autoLoad) {
        super(player, TREE_TYPE_INV_NAME, getTreeTypes(), new GeneratorMenu(player, false), autoLoad);
    }

    /** Get a list of all tree types */
    private static List<ItemStack> getTreeTypes() {
        List<ItemStack> treeTypes = new ArrayList<>();

        treeTypes.add(new Item(XMaterial.LIME_CONCRETE.parseItem()).setDisplayName("Any").build());

        for(TreeType treeType : TreeType.values())
            treeTypes.add(Item.create(XMaterial.OAK_SAPLING.parseMaterial(), StringUtils.capitalize(treeType.getName())));

        return treeTypes;
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
                roadSettings.setValue(RoadFlag.ROAD_MATERIAL, Item.createStringFromItemList(selectedMaterials));

                clickPlayer.closeInventory();
                clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
            });
    }
}

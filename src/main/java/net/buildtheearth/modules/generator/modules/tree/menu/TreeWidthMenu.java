package net.buildtheearth.modules.generator.modules.tree.menu;

import net.buildtheearth.Main;
import net.buildtheearth.modules.generator.model.Settings;
import net.buildtheearth.modules.generator.modules.road.RoadFlag;
import net.buildtheearth.modules.generator.modules.road.RoadSettings;
import net.buildtheearth.modules.generator.modules.road.menu.SidewalkColorMenu;
import net.buildtheearth.modules.generator.modules.tree.TreeWidth;
import net.buildtheearth.modules.utils.Item;
import net.buildtheearth.modules.utils.menus.BlockListMenu;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TreeWidthMenu extends BlockListMenu {

    public static String TREE_TYPE_INV_NAME = "Choose a Tree Width";

    public TreeWidthMenu(Player player) {
        super(player, TREE_TYPE_INV_NAME, getTreeWidths());
    }

    /** Get a list of all tree widths */
    private static List<ItemStack> getTreeWidths() {
        List<ItemStack> treeTypes = new ArrayList<>();

        treeTypes.add(Item.create(Material.CONCRETE, "Any", (byte) 5));

        for(TreeWidth treeWidth : TreeWidth.values())
            treeTypes.add(Item.create(Material.PAPER, StringUtils.capitalize(treeWidth.getName())));

        return treeTypes;
    }

    @Override
    protected void setItemClickEventsAsync() {
        super.setItemClickEventsAsync();

        // Set click event for next item
        if(canProceed())
            getMenu().getSlot(NEXT_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
                Settings settings = Main.buildTeamTools.getGenerator().getRoad().getPlayerSettings().get(clickPlayer.getUniqueId());

                if(!(settings instanceof RoadSettings))
                    return;

                RoadSettings roadSettings = (RoadSettings) settings;
                roadSettings.setValue(RoadFlag.ROAD_MATERIAL, Item.createStringFromItemList(selectedMaterials));

                clickPlayer.closeInventory();
                clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

                new SidewalkColorMenu(clickPlayer);
            });
    }
}

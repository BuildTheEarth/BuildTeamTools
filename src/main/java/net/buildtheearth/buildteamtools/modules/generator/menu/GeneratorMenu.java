package net.buildtheearth.buildteamtools.modules.generator.menu;

import com.alpsbte.alpslib.utils.item.Item;
import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.buildteamtools.modules.common.CommonModule;
import net.buildtheearth.buildteamtools.modules.generator.GeneratorModule;
import net.buildtheearth.buildteamtools.modules.generator.components.house.House;
import net.buildtheearth.buildteamtools.modules.generator.components.house.HouseSettings;
import net.buildtheearth.buildteamtools.modules.generator.components.house.RoofType;
import net.buildtheearth.buildteamtools.modules.generator.components.house.menu.WallColorMenu;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.Rail;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.RailSettings;
import net.buildtheearth.buildteamtools.modules.generator.components.road.Road;
import net.buildtheearth.buildteamtools.modules.generator.components.road.RoadSettings;
import net.buildtheearth.buildteamtools.modules.generator.components.road.menu.RoadColorMenu;
import net.buildtheearth.buildteamtools.modules.generator.components.tree.Tree;
import net.buildtheearth.buildteamtools.modules.generator.components.tree.TreeSettings;
import net.buildtheearth.buildteamtools.modules.generator.components.tree.menu.TreeTypeMenu;
import net.buildtheearth.buildteamtools.modules.generator.model.GeneratorCollections;
import net.buildtheearth.buildteamtools.modules.generator.model.GeneratorType;
import net.buildtheearth.buildteamtools.utils.ListUtil;
import net.buildtheearth.buildteamtools.utils.MenuItems;
import net.buildtheearth.buildteamtools.utils.menus.AbstractMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;

public class GeneratorMenu extends AbstractMenu {

    public static final String GENERATOR_INV_NAME = "What do you want to generate?";

    public static final int HOUSE_ITEM_SLOT = 9;

    public static final int ROAD_ITEM_SLOT = 11;

    public static final int RAILWAY_ITEM_SLOT = 13;

    public static final int TREE_ITEM_SLOT = 15;

    public static final int FIELD_ITEM_SLOT = 17;

    public GeneratorMenu(Player player, boolean autoLoad) {
        super(3, GENERATOR_INV_NAME, player, autoLoad);
    }

    @Override
    protected void setPreviewItems() {
        ArrayList<String> houseLore = ListUtil.createList(
                "",
                "§eDescription:",
                "Generate basic building shells",
                "with multiple floors, windows and roofs",
                "",
                "§eFeatures:",
                "- " + RoofType.values().length + " Roof Types",
                "- Custom Wall, Base and Roof Color",
                "- Custom Floor and Window Sizes",
                "",
                "§8Left-click to generate",
                "§8Right-click for Tutorial"
        );

        ItemStack houseItem = Item.create(XMaterial.BIRCH_DOOR.get(), "§cGenerate House", houseLore);
        getMenu().getSlot(HOUSE_ITEM_SLOT).setItem(houseItem);

        ArrayList<String> roadLore = ListUtil.createList(
                "",
                "§eDescription:",
                "Generate roads and highways",
                "with multiple lanes and sidewalks",
                "",
                "§eFeatures:",
                "- Custom Road Width and Color",
                "- Custom Sidewalk Width and Color",
                "- Custom Lane Count",
                "",
                "§8Left-click to generate",
                "§8Right-click for Tutorial"
        );

        ItemStack roadItem = new Item(XMaterial.SMOOTH_STONE_SLAB.parseItem())
                .setDisplayName("§bGenerate Road")
                .setLore(roadLore)
                .build();

        getMenu().getSlot(ROAD_ITEM_SLOT).setItem(roadItem);

        ArrayList<String> railwayLore = ListUtil.createList(
                "",
                "§eDescription:",
                "Generate a predefined railway",
                "from your active WorldEdit selection",
                "",
                "§eSupported selections:",
                "- Cuboid",
                "- Polygonal",
                "- Convex",
                "",
                "§eFeatures:",
                "- Straight rail sections",
                "- Curves and corners",
                "- Automatic rail orientation",
                "",
                "§8Left-click to generate",
                "§8Right-click for Tutorial"
        );

        ItemStack railwayItem = Item.create(XMaterial.RAIL.get(), "§9Generate Railway", railwayLore);
        getMenu().getSlot(RAILWAY_ITEM_SLOT).setItem(railwayItem);

        if (!CommonModule.getInstance().getDependencyComponent().isSchematicBrushEnabled()) {
            ArrayList<String> treeLore = ListUtil.createList(
                    "",
                    "§cPlugin §eSchematicBrush §cis not installed",
                    "§cTree Generator is disabled",
                    "",
                    "§8Leftclick for Installation Instructions"
            );

            ItemStack treeItem = Item.create(XMaterial.OAK_SAPLING.get(), "§aGenerate Tree & Forest §c(DISABLED)", treeLore);
            getMenu().getSlot(TREE_ITEM_SLOT).setItem(treeItem);
        } else if (!GeneratorCollections.hasUpdatedGeneratorCollections(getMenuPlayer())) {
            ArrayList<String> treeLore = ListUtil.createList(
                    "",
                    "§cThe §eTree Pack " + Tree.TREE_PACK_VERSION + " §cis not installed",
                    "§cTree Generator is disabled",
                    "",
                    "§8Leftclick for Installation Instructions"
            );

            ItemStack treeItem = Item.create(XMaterial.OAK_SAPLING.get(), "§aGenerate Tree & Forest §c(DISABLED)", treeLore);
            getMenu().getSlot(TREE_ITEM_SLOT).setItem(treeItem);
        } else {
            ArrayList<String> treeLore = ListUtil.createList(
                    "",
                    "§eDescription:",
                    "Generate trees from a set of",
                    "hundreds of different types",
                    "",
                    "§eFeatures:",
                    "- Custom Tree Type",
                    "",
                    "§8Left-click to generate",
                    "§8Right-click for Tutorial"
            );

            ItemStack treeItem = Item.create(XMaterial.OAK_SAPLING.get(), "§aGenerate Tree & Forest", treeLore);
            getMenu().getSlot(TREE_ITEM_SLOT).setItem(treeItem);
        }

        ArrayList<String> fieldLore = ListUtil.createList(
                "",
                "§cThis §eGenerator §cis currently broken",
                "§cField Generator is disabled",
                "",
                "§8If you want to help fixing ask on Dev Hub!"
        );

        ItemStack fieldItem = Item.create(XMaterial.WHEAT.get(), "§6Generate Field §c(DISABLED)", fieldLore);
        getMenu().getSlot(FIELD_ITEM_SLOT).setItem(fieldItem);

        super.setPreviewItems();
    }

    @Override
    protected void setMenuItemsAsync() {
        // No Async / DB Items
    }

    @Override
    protected void setItemClickEventsAsync() {
        getMenu().getSlot(HOUSE_ITEM_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if (clickInformation.getClickType().equals(ClickType.RIGHT)) {
                sendMoreInformation(clickPlayer, GeneratorType.HOUSE);
                return;
            }

            House house = GeneratorModule.getInstance().getHouse();
            house.getPlayerSettings().put(clickPlayer.getUniqueId(), new HouseSettings(clickPlayer));

            if (!house.checkForPlayer(clickPlayer))
                return;

            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
            new WallColorMenu(clickPlayer, true);
        }));

        getMenu().getSlot(ROAD_ITEM_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if (clickInformation.getClickType().equals(ClickType.RIGHT)) {
                sendMoreInformation(clickPlayer, GeneratorType.ROAD);
                return;
            }

            Road road = GeneratorModule.getInstance().getRoad();
            road.getPlayerSettings().put(clickPlayer.getUniqueId(), new RoadSettings(clickPlayer));

            if (!road.checkForPlayer(clickPlayer))
                return;

            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
            new RoadColorMenu(clickPlayer, true);
        }));

        getMenu().getSlot(RAILWAY_ITEM_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if (clickInformation.getClickType().equals(ClickType.RIGHT)) {
                sendMoreInformation(clickPlayer, GeneratorType.RAILWAY);
                return;
            }

            Rail rail = GeneratorModule.getInstance().getRail();
            rail.getPlayerSettings().put(clickPlayer.getUniqueId(), new RailSettings(clickPlayer));

            if (!rail.checkForPlayer(clickPlayer))
                return;

            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
            rail.generate(clickPlayer);
        }));

        getMenu().getSlot(TREE_ITEM_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if (clickInformation.getClickType().equals(ClickType.RIGHT)) {
                sendMoreInformation(clickPlayer, GeneratorType.TREE);
                return;
            }

            Tree tree = GeneratorModule.getInstance().getTree();
            tree.getPlayerSettings().put(clickPlayer.getUniqueId(), new TreeSettings(clickPlayer));

            if (!tree.checkForPlayer(clickPlayer))
                return;

            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
            new TreeTypeMenu(clickPlayer, true);
        }));

        getMenu().getSlot(FIELD_ITEM_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if (clickInformation.getClickType().equals(ClickType.RIGHT)) {
                sendMoreInformation(clickPlayer, GeneratorType.FIELD);
                return;
            }

            sendMoreInformation(clickPlayer, GeneratorType.FIELD);
        }));
    }

    private void sendMoreInformation(@NonNull Player clickPlayer, @NonNull GeneratorType generator) {
        clickPlayer.sendMessage(Component.text(generator.getWikiPage(), NamedTextColor.RED));
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(MenuItems.ITEM_BACKGROUND)
                .pattern("111111111")
                .pattern("010101010")
                .pattern("111111111")
                .build();
    }
}
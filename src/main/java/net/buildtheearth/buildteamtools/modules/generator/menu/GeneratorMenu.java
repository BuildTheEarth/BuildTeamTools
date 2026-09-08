package net.buildtheearth.buildteamtools.modules.generator.menu;

import com.alpsbte.alpslib.utils.ChatHelper;
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
import net.buildtheearth.buildteamtools.modules.generator.components.rail.menu.RailTypeMenu;
import net.buildtheearth.buildteamtools.modules.generator.components.road.Road;
import net.buildtheearth.buildteamtools.modules.generator.components.road.RoadSettings;
import net.buildtheearth.buildteamtools.modules.generator.components.road.menu.RoadColorMenu;
import net.buildtheearth.buildteamtools.modules.generator.components.tree.Tree;
import net.buildtheearth.buildteamtools.modules.generator.components.tree.TreeSettings;
import net.buildtheearth.buildteamtools.modules.generator.components.tree.menu.TreeTypeMenu;
import net.buildtheearth.buildteamtools.modules.generator.model.GeneratorCollections;
import net.buildtheearth.buildteamtools.modules.generator.model.GeneratorComponent;
import net.buildtheearth.buildteamtools.modules.generator.model.GeneratorType;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import net.buildtheearth.buildteamtools.utils.ListUtil;
import net.buildtheearth.buildteamtools.utils.MenuItems;
import net.buildtheearth.buildteamtools.utils.menus.AbstractMenu;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Objects;

public class GeneratorMenu extends AbstractMenu {

    public static final String GENERATOR_INV_NAME = "What do you want to generate?";
    private static final String DESCRIPTION_LABEL = color(NamedTextColor.YELLOW, "Description:");
    private static final String FEATURES_LABEL = color(NamedTextColor.YELLOW, "Features:");
    private static final String LEFT_CLICK_TO_GENERATE = color(NamedTextColor.DARK_GRAY, "Left-click to generate");
    private static final String RIGHT_CLICK_FOR_TUTORIAL = color(NamedTextColor.DARK_GRAY, "Right-click for Tutorial");

    public static final int HOUSE_ITEM_SLOT = 9;
    public static final int ROAD_ITEM_SLOT = 11;
    public static final int RAIL_ITEM_SLOT = 13;
    public static final int TREE_ITEM_SLOT = 15;
    public static final int FIELD_ITEM_SLOT = 17;

    public GeneratorMenu(Player player, boolean autoLoad) {
        super(3, GENERATOR_INV_NAME, player, autoLoad);
    }

    @Override
    protected void setPreviewItems() {
        ArrayList<String> houseLore = ListUtil.createList(
                "",
                DESCRIPTION_LABEL,
                "Generate basic building shells",
                "with multiple floors, windows and roofs",
                "",
                FEATURES_LABEL,
                "- " + RoofType.values().length + " Roof Types",
                "- Custom Wall, Base and Roof Color",
                "- Custom Floor and Window Sizes",
                "",
                LEFT_CLICK_TO_GENERATE,
                RIGHT_CLICK_FOR_TUTORIAL
        );

        ItemStack houseItem = Item.create(
                Objects.requireNonNull(XMaterial.BIRCH_DOOR.get()),
                color(NamedTextColor.RED, "Generate House"),
                houseLore
        );
        getMenu().getSlot(HOUSE_ITEM_SLOT).setItem(houseItem);

        ArrayList<String> roadLore = ListUtil.createList(
                "",
                DESCRIPTION_LABEL,
                "Generate roads and highways",
                "with multiple lanes and sidewalks",
                "",
                FEATURES_LABEL,
                "- Custom Road Width and Color",
                "- Custom Sidewalk Width and Color",
                "- Custom Lane Count",
                "",
                LEFT_CLICK_TO_GENERATE,
                RIGHT_CLICK_FOR_TUTORIAL
        );

        ItemStack roadItem = Item.create(
                Objects.requireNonNull(XMaterial.SMOOTH_STONE_SLAB.get()),
                color(NamedTextColor.AQUA, "Generate Road"),
                roadLore
        );
        getMenu().getSlot(ROAD_ITEM_SLOT).setItem(roadItem);

        ArrayList<String> railwayLore = ListUtil.createList(
                "",
                DESCRIPTION_LABEL,
                "Generate a predefined railway",
                "from your active WorldEdit selection",
                "",
                color(NamedTextColor.YELLOW, "Supported selections:"),
                "- Cuboid",
                "- Polygonal",
                "- Convex",
                "",
                FEATURES_LABEL,
                "- Rail Type selection",
                "- Straight sections",
                "- Direction changes",
                "- Automatic side block orientation",
                "",
                LEFT_CLICK_TO_GENERATE,
                RIGHT_CLICK_FOR_TUTORIAL
        );

        ItemStack railwayItem = Item.create(
                Objects.requireNonNull(XMaterial.RAIL.get()),
                color(NamedTextColor.BLUE, "Generate Railway"),
                railwayLore
        );
        getMenu().getSlot(RAIL_ITEM_SLOT).setItem(railwayItem);

        if (!CommonModule.getInstance().getDependencyComponent().isSchematicBrushEnabled()) {
            ArrayList<String> treeLore = ListUtil.createList(
                    "",
                    color(NamedTextColor.RED, "Plugin ")
                            + color(NamedTextColor.YELLOW, "SchematicBrush ")
                            + color(NamedTextColor.RED, "is not installed"),
                    color(NamedTextColor.RED, "Tree Generator is disabled"),
                    "",
                    color(NamedTextColor.DARK_GRAY, "Leftclick for Installation Instructions")
            );

            ItemStack treeItem = createTreeItem(color(NamedTextColor.RED, " (DISABLED)"), treeLore);
            getMenu().getSlot(TREE_ITEM_SLOT).setItem(treeItem);
        } else if (!GeneratorCollections.hasUpdatedGeneratorCollections(getMenuPlayer())) {
            ArrayList<String> treeLore = ListUtil.createList(
                    "",
                    color(NamedTextColor.RED, "The ")
                            + color(NamedTextColor.YELLOW, "Tree Pack " + Tree.TREE_PACK_VERSION + " ")
                            + color(NamedTextColor.RED, "is not installed"),
                    color(NamedTextColor.RED, "Tree Generator is disabled"),
                    "",
                    color(NamedTextColor.DARK_GRAY, "Leftclick for Installation Instructions")
            );

            ItemStack treeItem = createTreeItem(color(NamedTextColor.RED, " (DISABLED)"), treeLore);
            getMenu().getSlot(TREE_ITEM_SLOT).setItem(treeItem);
        } else {
            ArrayList<String> treeLore = ListUtil.createList(
                    "",
                    DESCRIPTION_LABEL,
                    "Generate trees from a set of",
                    "hundreds of different types",
                    "",
                    FEATURES_LABEL,
                    "- Custom Tree Type",
                    "",
                    LEFT_CLICK_TO_GENERATE,
                    RIGHT_CLICK_FOR_TUTORIAL
            );

            ItemStack treeItem = createTreeItem("", treeLore);
            getMenu().getSlot(TREE_ITEM_SLOT).setItem(treeItem);
        }

        ArrayList<String> fieldLore = ListUtil.createList(
                "",
                color(NamedTextColor.RED, "This ")
                        + color(NamedTextColor.YELLOW, "Generator ")
                        + color(NamedTextColor.RED, "is currently broken"),
                color(NamedTextColor.RED, "Field Generator is disabled"),
                "",
                color(NamedTextColor.DARK_GRAY, "If you want to help fixing ask on Dev Hub!")
        );

        ItemStack fieldItem = Item.create(
                Objects.requireNonNull(XMaterial.WHEAT.get()),
                color(NamedTextColor.GOLD, "Generate Field ") + color(NamedTextColor.RED, "(DISABLED)"),
                fieldLore
        );
        getMenu().getSlot(FIELD_ITEM_SLOT).setItem(fieldItem);

        super.setPreviewItems();
    }

    @Override
    protected void setMenuItemsAsync() {
        // No async or database items.
    }

    @Override
    protected void setItemClickEventsAsync() {
        getMenu().getSlot(HOUSE_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) ->
                handleHouseClick(clickPlayer, clickInformation.getClickType()));
        getMenu().getSlot(ROAD_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) ->
                handleRoadClick(clickPlayer, clickInformation.getClickType()));
        getMenu().getSlot(RAIL_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) ->
                handleRailClick(clickPlayer, clickInformation.getClickType()));
        getMenu().getSlot(TREE_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) ->
                handleTreeClick(clickPlayer, clickInformation.getClickType()));
        getMenu().getSlot(FIELD_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) ->
                sendMoreInformation(clickPlayer, GeneratorType.FIELD));
    }

    private void handleHouseClick(Player player, ClickType clickType) {
        if (showTutorialForRightClick(player, clickType, GeneratorType.HOUSE))
            return;

        House house = GeneratorModule.getInstance().getHouse();
        house.getPlayerSettings().put(player.getUniqueId(), new HouseSettings(player));

        if (!house.checkForPlayer(player))
            return;

        closeMenuWithClickSound(player);
        new WallColorMenu(player, true);
    }

    private void handleRoadClick(Player player, ClickType clickType) {
        if (showTutorialForRightClick(player, clickType, GeneratorType.ROAD))
            return;

        Road road = GeneratorModule.getInstance().getRoad();
        road.getPlayerSettings().put(player.getUniqueId(), new RoadSettings(player));

        if (!road.checkForPlayer(player))
            return;

        closeMenuWithClickSound(player);
        new RoadColorMenu(player, true);
    }

    private void handleRailClick(Player player, ClickType clickType) {
        if (showTutorialForRightClick(player, clickType, GeneratorType.RAIL)
                || !Permissions.checkPermission(player, Permissions.RAIL_TYPE_MENU))
            return;

        Rail rail = GeneratorModule.getInstance().getRail();
        rail.getPlayerSettings().put(player.getUniqueId(), new RailSettings(player));

        if (!rail.checkForPlayer(player))
            return;

        closeMenuWithClickSound(player);
        new RailTypeMenu(player, true);
    }

    private void handleTreeClick(Player player, ClickType clickType) {
        if (showTutorialForRightClick(player, clickType, GeneratorType.TREE))
            return;

        Tree tree = GeneratorModule.getInstance().getTree();
        tree.getPlayerSettings().put(player.getUniqueId(), new TreeSettings(player));

        if (!tree.checkForPlayer(player))
            return;

        closeMenuWithClickSound(player);
        new TreeTypeMenu(player, true);
    }

    private boolean showTutorialForRightClick(Player player, ClickType clickType, GeneratorType generatorType) {
        if (clickType != ClickType.RIGHT)
            return false;

        sendMoreInformation(player, generatorType);
        return true;
    }

    private void closeMenuWithClickSound(Player player) {
        player.closeInventory();
        player.playSound(player, Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
    }

    private ItemStack createTreeItem(String suffix, ArrayList<String> lore) {
        return Item.create(
                Objects.requireNonNull(XMaterial.OAK_SAPLING.get()),
                color(NamedTextColor.GREEN, "Generate Tree & Forest") + suffix,
                lore
        );
    }

    private static String color(NamedTextColor color, String text) {
        return ChatHelper.getColorizedString(color, text, false);
    }

    private void sendMoreInformation(@NonNull Player clickPlayer, @NonNull GeneratorType generator) {
        GeneratorComponent.sendMoreInformation(clickPlayer, generator);
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu()).item(MenuItems.ITEM_BACKGROUND).pattern(BinaryMask.FULL_PATTERN).pattern("010101010").pattern(BinaryMask.FULL_PATTERN).build();
    }
}

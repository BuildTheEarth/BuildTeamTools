package net.buildtheearth.modules.generator.menu;

import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.modules.common.CommonModule;
import net.buildtheearth.modules.generator.GeneratorModule;
import net.buildtheearth.modules.generator.components.field.Field;
import net.buildtheearth.modules.generator.components.field.FieldSettings;
import net.buildtheearth.modules.generator.components.field.menu.CropTypeMenu;
import net.buildtheearth.modules.generator.components.house.House;
import net.buildtheearth.modules.generator.components.house.HouseSettings;
import net.buildtheearth.modules.generator.components.house.RoofType;
import net.buildtheearth.modules.generator.components.house.menu.WallColorMenu;
import net.buildtheearth.modules.generator.components.road.Road;
import net.buildtheearth.modules.generator.components.road.RoadSettings;
import net.buildtheearth.modules.generator.components.road.menu.RoadColorMenu;
import net.buildtheearth.modules.generator.components.tree.Tree;
import net.buildtheearth.modules.generator.components.tree.TreeSettings;
import net.buildtheearth.modules.generator.components.tree.menu.TreeTypeMenu;
import net.buildtheearth.modules.generator.model.GeneratorCollections;
import net.buildtheearth.modules.generator.model.GeneratorType;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.ListUtil;
import net.buildtheearth.utils.MenuItems;
import net.buildtheearth.utils.menus.AbstractMenu;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.ArrayList;

public class GeneratorMenu extends AbstractMenu {

    public static final String GENERATOR_INV_NAME = "What do you want to generate?";

    public static final int HOUSE_ITEM_SLOT = 9;

    public static final int ROAD_ITEM_SLOT = 11;

    public static final int RAILWAY_ITEM_SLOT = 13;

    public static final int TREE_ITEM_SLOT = 15;

    public static int FIELD_ITEM_SLOT = 17;


    public GeneratorMenu(Player player, boolean autoLoad) {
        super(3, GENERATOR_INV_NAME, player, autoLoad);
    }

    @Override
    protected void setPreviewItems() {
        // HOUSE ITEM
        ArrayList<String> houseLore = ListUtil.createList("",
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
                "§8Right-click for Tutorial");

        ItemStack houseItem = Item.create(XMaterial.BIRCH_DOOR.parseMaterial(), "§cGenerate House", houseLore);

        // Set navigator item
        getMenu().getSlot(HOUSE_ITEM_SLOT).setItem(houseItem);


        // ROAD ITEM
        ArrayList<String> roadLore = ListUtil.createList("",
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
                "§8Right-click for Tutorial");


        ItemStack roadItem = new Item(XMaterial.SMOOTH_STONE_SLAB.parseItem()).setDisplayName("§bGenerate Road").setLore(roadLore).build();

        // Set navigator item
        getMenu().getSlot(ROAD_ITEM_SLOT).setItem(roadItem);


        // RAILWAY ITEM
        ArrayList<String> railwayLore = ListUtil.createList("",
                "§eDescription:",
                "Generate railways with multiple tracks",
                "and many different designs",
                "",
                "§eFeatures:",
                "- Custom Railway Width and Color (TODO)",
                "- Custom Track Count (TODO)",
                "",
                "§8Left-click to generate",
                "§8Right-click for Tutorial");

        railwayLore = ListUtil.createList("", "§cThis §eGenerator §cis currently broken", "§cRailway Generator is disabled", "", "§8If you want to help fixing ask on Dev Hub!");

        ItemStack railwayItem = Item.create(XMaterial.RAIL.parseMaterial(), "§9Generate Railway §c(DISABLED)", railwayLore);

        // Set navigator item
        getMenu().getSlot(RAILWAY_ITEM_SLOT).setItem(railwayItem);


        if (!CommonModule.getInstance().getDependencyComponent().isSchematicBrushEnabled()) {
            // TREE ITEM
            ArrayList<String> treeLore = ListUtil.createList("", "§cPlugin §eSchematicBrush §cis not installed", "§cTree Generator is disabled", "", "§8Leftclick for Installation Instructions");

            ItemStack treeItem = Item.create(XMaterial.OAK_SAPLING.parseMaterial(), "§aGenerate Tree & Forest §c(DISABLED)", treeLore);

            // Set navigator item
            getMenu().getSlot(TREE_ITEM_SLOT).setItem(treeItem);
        }else if(GeneratorCollections.checkIfGeneratorCollectionsIsInstalled(getMenuPlayer())){
            // TREE ITEM
            ArrayList<String> treeLore = ListUtil.createList("", "§cThe §eTree Pack " + Tree.TREE_PACK_VERSION + " §cis not installed", "§cTree Generator is disabled", "", "§8Leftclick for Installation Instructions");

            ItemStack treeItem = Item.create(XMaterial.OAK_SAPLING.parseMaterial(), "§aGenerate Tree & Forest §c(DISABLED)", treeLore);

            // Set navigator item
            getMenu().getSlot(TREE_ITEM_SLOT).setItem(treeItem);
        }else{
            // TREE ITEM
            ArrayList<String> treeLore = ListUtil.createList("",
                    "§eDescription:",
                    "Generate trees from a set of",
                    "hundreds of different types",
                    "",
                    "§eFeatures:",
                    "- Custom Tree Type",
                    "",
                    "§8Left-click to generate",
                    "§8Right-click for Tutorial");

            ItemStack treeItem = Item.create(XMaterial.OAK_SAPLING.parseMaterial(), "§aGenerate Tree & Forest", treeLore);

            // Set navigator item
            getMenu().getSlot(TREE_ITEM_SLOT).setItem(treeItem);
        }


        // FIELD ITEM
        ArrayList<String> fieldLore = ListUtil.createList("",
                "§eDescription:",
                "Generate fields with different",
                "crops and plants",
                "",
                "§eFeatures:",
                "- Custom Crop Type",
                "- Custom Crop Size",
                "",
                "§8Left-click to generate",
                "§8Right-click for Tutorial");

        fieldLore = ListUtil.createList("", "§cThis §eGenerator §cis currently broken", "§cField Generator is disabled", "", "§8If you want to help fixing ask on Dev Hub!");

        ItemStack fieldItem = Item.create(XMaterial.WHEAT.get(), "§6Generate Field §c(DISABLED)", fieldLore);

        // Set navigator item
        getMenu().getSlot(FIELD_ITEM_SLOT).setItem(fieldItem);


        super.setPreviewItems();
    }

    @Override
    protected void setMenuItemsAsync() {}

    @Override
    protected void setItemClickEventsAsync() {
        // Set click event for house item
        getMenu().getSlot(HOUSE_ITEM_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if(clickInformation.getClickType().equals(ClickType.RIGHT)) {
                sendMoreInformation(clickPlayer, GeneratorType.HOUSE);
                return;
            }

            House house = GeneratorModule.getInstance().getHouse();
            house.getPlayerSettings().put(clickPlayer.getUniqueId(), new HouseSettings(clickPlayer));

            if(!house.checkForPlayer(clickPlayer))
                return;

            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
            new WallColorMenu(clickPlayer, true);
        }));

        // Set click event for road item
        getMenu().getSlot(ROAD_ITEM_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if(clickInformation.getClickType().equals(ClickType.RIGHT)) {
                sendMoreInformation(clickPlayer, GeneratorType.ROAD);
                return;
            }

            Road road = GeneratorModule.getInstance().getRoad();
            road.getPlayerSettings().put(clickPlayer.getUniqueId(), new RoadSettings(clickPlayer));

            if(!road.checkForPlayer(clickPlayer))
                return;

            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
            new RoadColorMenu(clickPlayer, true);
        }));

        // Set click event for railway item
        getMenu().getSlot(RAILWAY_ITEM_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if(clickInformation.getClickType().equals(ClickType.RIGHT)) {
                sendMoreInformation(clickPlayer, GeneratorType.RAILWAY);
                return;
            }
            sendMoreInformation(clickPlayer, GeneratorType.RAILWAY);

            /*Rail rail = GeneratorModule.getInstance().getRail();
            rail.getPlayerSettings().put(clickPlayer.getUniqueId(), new RailSettings(clickPlayer));

            if(!rail.checkForPlayer(clickPlayer))
                return;

            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

            GeneratorModule.getInstance().getRail().generate(clickPlayer);*/
        }));

        // Set click event for tree item
        getMenu().getSlot(TREE_ITEM_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if(clickInformation.getClickType().equals(ClickType.RIGHT)) {
                sendMoreInformation(clickPlayer, GeneratorType.TREE);
                return;
            }

            Tree tree = GeneratorModule.getInstance().getTree();
            tree.getPlayerSettings().put(clickPlayer.getUniqueId(), new TreeSettings(clickPlayer));

            if(!tree.checkForPlayer(clickPlayer))
                return;

            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
            new TreeTypeMenu(clickPlayer, true);
        }));

        // Set click event for field item
        getMenu().getSlot(FIELD_ITEM_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if(clickInformation.getClickType().equals(ClickType.RIGHT)) {
                sendMoreInformation(clickPlayer, GeneratorType.FIELD);
                return;
            }

            Field field = GeneratorModule.getInstance().getField();
            field.getPlayerSettings().put(clickPlayer.getUniqueId(), new FieldSettings(clickPlayer));

            if(!field.checkForPlayer(clickPlayer))
                return;

            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
            new CropTypeMenu(clickPlayer, true);
        }));
    }

    private void sendMoreInformation(Player clickPlayer, GeneratorType generator) {
        switch (generator) {
            case FIELD:
                clickPlayer.sendMessage(ChatColor.RED + "https://github.com/BuildTheEarth/BuildTeamTools/wiki/Field-Command");
                break;
            case HOUSE:
                clickPlayer.sendMessage(ChatColor.RED + "https://github.com/BuildTheEarth/BuildTeamTools/wiki/House-Command");
                break;
            case RAILWAY:
                clickPlayer.sendMessage(ChatColor.RED + "https://github.com/BuildTheEarth/BuildTeamTools/wiki/Rail-Command");
                break;
            case ROAD:
                clickPlayer.sendMessage(ChatColor.RED + "https://github.com/BuildTheEarth/BuildTeamTools/wiki/Road-Command");
                break;
        }
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
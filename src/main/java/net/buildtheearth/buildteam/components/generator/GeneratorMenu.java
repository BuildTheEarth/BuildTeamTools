package net.buildtheearth.buildteam.components.generator;

import net.buildtheearth.Main;
import net.buildtheearth.buildteam.BuildTeamTools;
import net.buildtheearth.buildteam.components.generator.house.House;
import net.buildtheearth.buildteam.components.generator.house.HouseSettings;
import net.buildtheearth.buildteam.components.generator.house.RoofType;
import net.buildtheearth.buildteam.components.generator.house.menu.WallColorMenu;
import net.buildtheearth.buildteam.components.generator.rail.Rail;
import net.buildtheearth.buildteam.components.generator.rail.RailSettings;
import net.buildtheearth.buildteam.components.generator.road.Road;
import net.buildtheearth.buildteam.components.generator.road.RoadSettings;
import net.buildtheearth.buildteam.components.generator.road.menu.RoadColorMenu;
import net.buildtheearth.buildteam.components.generator.tree.Tree;
import net.buildtheearth.utils.menus.AbstractMenu;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.Liste;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import java.util.ArrayList;

public class GeneratorMenu extends AbstractMenu {

    public static String GENERATOR_INV_NAME = "What do you want to generate?";

    public static int HOUSE_ITEM_SLOT = 9;

    public static int ROAD_ITEM_SLOT = 11;

    public static int RAILWAY_ITEM_SLOT = 13;

    public static int TREE_ITEM_SLOT = 15;

    public static int FIELD_SLOT = 17;



    public GeneratorMenu(Player player) {
        super(3, GENERATOR_INV_NAME, player);
    }

    @Override
    protected void setPreviewItems() {

        // HOUSE ITEM
        ArrayList<String> houseLore = Liste.createList("",
                "§eDescription:",
                "Generate basic building shells",
                "with multiple floors, windows and roofs",
                "",
                "§eFeatures:",
                "- " + RoofType.values().length + " Roof Types",
                "- Custom Wall, Base and Roof Color",
                "- Custom Floor and Window Sizes",
                "",
                "§8Leftclick to generate",
                "§8Rightclick for Tutorial");

        ItemStack houseItem = Item.create(Material.BIRCH_DOOR_ITEM, "§cGenerate House", houseLore);

        // Set navigator item
        getMenu().getSlot(HOUSE_ITEM_SLOT).setItem(houseItem);


        // ROAD ITEM
        ArrayList<String> roadLore = Liste.createList("",
                "§eDescription:",
                "Generate roads and highways",
                "with multiple lanes and sidewalks",
                "",
                "§eFeatures:",
                "- Custom Road Width and Color",
                "- Custom Sidewalk Width and Color",
                "- Custom Lane Count",
                "",
                "§8Leftclick to generate",
                "§8Rightclick for Tutorial");


        ItemStack roadItem =  Item.create(Material.STEP, "§bGenerate Road", (short) 0, roadLore);

        // Set navigator item
        getMenu().getSlot(ROAD_ITEM_SLOT).setItem(roadItem);


        // RAILWAY ITEM
        ArrayList<String> railwayLore = Liste.createList("",
                "§eDescription:",
                "Generate railways with multiple tracks",
                "and many different designs",
                "",
                "§eFeatures:",
                "- Custom Railway Width and Color (TODO)",
                "- Custom Track Count (TODO)",
                "",
                "§8Leftclick to generate",
                "§8Rightclick for Tutorial");

        ItemStack railwayItem = Item.create(Material.RAILS, "§9Generate Railway", railwayLore);

        // Set navigator item
        getMenu().getSlot(RAILWAY_ITEM_SLOT).setItem(railwayItem);


        if(!BuildTeamTools.DependencyManager.isSchematicBrushEnabled()){
            // TREE ITEM
            ArrayList<String> treeLore = Liste.createList("", "§cPlugin §eSchematicBrush §cis not installed", "§cTree Generator is disabled", "", "§8Leftclick for Installation Instructions");

            ItemStack treeItem = Item.create(Material.SAPLING, "§aGenerate Tree & Forest §c(DISABLED)", treeLore);

            // Set navigator item
            getMenu().getSlot(TREE_ITEM_SLOT).setItem(treeItem);
        }else if(!Generator.checkIfTreePackIsInstalled(getMenuPlayer(), false)){
            // TREE ITEM
            ArrayList<String> treeLore = Liste.createList("", "§cThe §eTree Pack " + Tree.TREE_PACK_VERSION + " §cis not installed", "§cTree Generator is disabled", "", "§8Leftclick for Installation Instructions");

            ItemStack treeItem = Item.create(Material.SAPLING, "§aGenerate Tree & Forest §c(DISABLED)", treeLore);

            // Set navigator item
            getMenu().getSlot(TREE_ITEM_SLOT).setItem(treeItem);
        }else{
            // TREE ITEM
            ArrayList<String> treeLore = Liste.createList("",
                    "§eDescription:",
                    "Generate trees from a set of",
                    "hundreds of different types",
                    "",
                    "§eFeatures:",
                    "- Custom Tree Type",
                    "",
                    "§8Leftclick to generate",
                    "§8Rightclick for Tutorial");

            ItemStack treeItem = Item.create(Material.SAPLING, "§aGenerate Tree & Forest", treeLore);

            // Set navigator item
            getMenu().getSlot(TREE_ITEM_SLOT).setItem(treeItem);
        }




        // FIELD ITEM
        ArrayList<String> fieldLore = Liste.createList("",
                "§eDescription:",
                "Generate fields with different",
                "crops and plants",
                "",
                "§eFeatures:",
                "- Custom Crop Type",
                "- Custom Crop Size",
                "",
                "§8Leftclick to generate",
                "§8Rightclick for Tutorial");

        ItemStack fieldItem = Item.create(Material.WHEAT, "§6Generate Field §c(TODO)", fieldLore);

        // Set navigator item
        getMenu().getSlot(FIELD_SLOT).setItem(fieldItem);



        super.setPreviewItems();
    }

    @Override
    protected void setMenuItemsAsync() {}

    @Override
    protected void setItemClickEventsAsync() {
        // Set click event for house item
        getMenu().getSlot(HOUSE_ITEM_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            House house = Main.buildTeamTools.getGenerator().getHouse();
            house.getPlayerSettings().put(clickPlayer.getUniqueId(), new HouseSettings(clickPlayer));

            if(!house.checkPlayer(clickPlayer))
                return;

            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
            new WallColorMenu(clickPlayer);
        }));

        // Set click event for road item
        getMenu().getSlot(ROAD_ITEM_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            Road road = Main.buildTeamTools.getGenerator().getRoad();
            road.getPlayerSettings().put(clickPlayer.getUniqueId(), new RoadSettings(clickPlayer));

            if(!road.checkPlayer(clickPlayer))
                return;

            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
            new RoadColorMenu(clickPlayer);
        }));

        // Set click event for railway item
        getMenu().getSlot(RAILWAY_ITEM_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            Rail rail = Main.buildTeamTools.getGenerator().getRail();
            rail.getPlayerSettings().put(clickPlayer.getUniqueId(), new RailSettings(clickPlayer));

            if(!rail.checkPlayer(clickPlayer))
                return;

            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

            Main.getBuildTeam().getGenerator().getRail().generate(clickPlayer);
        }));
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(Item.create(Material.STAINED_GLASS_PANE, " ", (short)15, null))
                .pattern("111111111")
                .pattern("010101010")
                .pattern("111111111")
                .build();
    }
}
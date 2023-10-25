package net.buildtheearth.buildteam.components.generator;

import net.buildtheearth.Main;
import net.buildtheearth.buildteam.BuildTeamTools;
import net.buildtheearth.buildteam.components.generator.field.Field;
import net.buildtheearth.buildteam.components.generator.field.FieldSettings;
import net.buildtheearth.buildteam.components.generator.field.menu.CropTypeMenu;
import net.buildtheearth.buildteam.components.generator.house.House;
import net.buildtheearth.buildteam.components.generator.house.HouseSettings;
import net.buildtheearth.buildteam.components.generator.house.RoofType;
import net.buildtheearth.buildteam.components.generator.house.menu.WallColorMenu;
import net.buildtheearth.buildteam.components.generator.rail.Rail;
import net.buildtheearth.buildteam.components.generator.rail.RailSettings;
import net.buildtheearth.buildteam.components.generator.road.Road;
import net.buildtheearth.buildteam.components.generator.road.RoadSettings;
import net.buildtheearth.buildteam.components.generator.road.menu.RoadColorMenu;
import net.buildtheearth.utils.menus.AbstractMenu;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.Liste;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
                "§8Left-click to generate",
                "§8Right-click for Tutorial");

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
                "§8Left-click to generate",
                "§8Right-click for Tutorial");


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
                "§8Left-click to generate",
                "§8Right-click for Tutorial");

        ItemStack railwayItem = Item.create(Material.RAILS, "§9Generate Railway", railwayLore);

        // Set navigator item
        getMenu().getSlot(RAILWAY_ITEM_SLOT).setItem(railwayItem);


        if(BuildTeamTools.DependencyManager.isSchematicBrushDisabled()) {
            // TREE ITEM
            ArrayList<String> treeLore = Liste.createList("", "§cPlugin §eSchematicBrush §cis not installed", "§cTree Generator is disabled", "", "§8Left-click for Installation Instructions");

            ItemStack treeItem = Item.create(Material.SAPLING, "§aGenerate Tree & Forest §c(DISABLED)", treeLore);

            // Set navigator item
            getMenu().getSlot(TREE_ITEM_SLOT).setItem(treeItem);
        }else if(!Generator.checkIfGeneratorCollectionsIsInstalled(getMenuPlayer())){
            // TREE ITEM
            ArrayList<String> treeLore = Liste.createList("", "§cThe §eGenerator Collections v" + Generator.GENERATOR_COLLECTIONS_VERSION + " §c package is not installed", "§cTree Generator is disabled", "", "§8Left-click for Installation Instructions");

            ItemStack treeItem = Item.create(Material.SAPLING, "§aGenerate Tree & Forest §c(DISABLED)", treeLore);

            // Set navigator item
            getMenu().getSlot(TREE_ITEM_SLOT).setItem(treeItem);
        } else {
            // TREE ITEM
            ArrayList<String> treeLore = Liste.createList("",
                    "§eDescription:",
                    "Generate trees from a set of",
                    "hundreds of different types",
                    "",
                    "§eFeatures:",
                    "- Custom Tree Type",
                    "",
                    "§8Left-click to generate",
                    "§8Right-click for Tutorial");

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
                "§8Left-click to generate",
                "§8Right-click for Tutorial");

        ItemStack fieldItem = Item.create(Material.WHEAT, "§6Generate Field", fieldLore);

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

            House house = Main.buildTeamTools.getGenerator().getHouse();
            house.getPlayerSettings().put(clickPlayer.getUniqueId(), new HouseSettings(clickPlayer));

            if(house.checkForNoPlayer(clickPlayer))
                return;

            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
            new WallColorMenu(clickPlayer);
        }));

        // Set click event for road item
        getMenu().getSlot(ROAD_ITEM_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if(clickInformation.getClickType().equals(ClickType.RIGHT)) {
                sendMoreInformation(clickPlayer, GeneratorType.ROAD);
                return;
            }

            Road road = Main.buildTeamTools.getGenerator().getRoad();
            road.getPlayerSettings().put(clickPlayer.getUniqueId(), new RoadSettings(clickPlayer));

            if(road.checkForNoPlayer(clickPlayer))
                return;

            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
            new RoadColorMenu(clickPlayer);
        }));

        // Set click event for railway item
        getMenu().getSlot(RAILWAY_ITEM_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if(clickInformation.getClickType().equals(ClickType.RIGHT)) {
                sendMoreInformation(clickPlayer, GeneratorType.RAILWAY);
                return;
            }

            Rail rail = Main.buildTeamTools.getGenerator().getRail();
            rail.getPlayerSettings().put(clickPlayer.getUniqueId(), new RailSettings(clickPlayer));

            if(rail.checkForNoPlayer(clickPlayer))
                return;

            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

            Main.getBuildTeam().getGenerator().getRail().generate(clickPlayer);
        }));

        // Set click event for field item
        getMenu().getSlot(FIELD_ITEM_SLOT).setClickHandler(((clickPlayer, clickInformation) -> {
            if(clickInformation.getClickType().equals(ClickType.RIGHT)) {
                sendMoreInformation(clickPlayer, GeneratorType.FIELD);
                return;
            }

            Field field = Main.buildTeamTools.getGenerator().getField();
            field.getPlayerSettings().put(clickPlayer.getUniqueId(), new FieldSettings(clickPlayer));

            if(field.checkForNoPlayer(clickPlayer))
                return;

            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
            new CropTypeMenu(clickPlayer);
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
                .item(Item.create(Material.STAINED_GLASS_PANE, " ", (short)15, null))
                .pattern("111111111")
                .pattern("010101010")
                .pattern("111111111")
                .build();
    }
}
package net.buildtheearth.modules.navigator.menu;

import net.buildtheearth.Main;
import net.buildtheearth.modules.generator.menu.GeneratorMenu;
import net.buildtheearth.modules.network.ProxyManager;
import net.buildtheearth.modules.utils.Item;
import net.buildtheearth.modules.utils.MenuItem;
import net.buildtheearth.modules.utils.Utils;
import net.buildtheearth.modules.utils.io.ConfigPaths;
import net.buildtheearth.modules.utils.menus.AbstractMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.mask.Mask;

import java.util.ArrayList;

/**
 * The build menu for the BTE universal navigator. <p>
 * <p>
 * <p> Accessed from here is the tutorials menu, the plot system, the region menu and the building tools (generator) menu.
 * All of these icons can be enabled and disabled. <p>
 * <p>
 * <p> The menu has 3 rows and the centre row is the only occupied row. The layout depends on what icons are enabled in config.
 */
public class BuildMenu extends AbstractMenu {
    private static final int iRows = 3;
    private static final String szInventoryName = "Build Menu";
    private static FileConfiguration config;
    private static final ArrayList<MenuItem> menuItems = getGui(Main.getBuildTeamTools().getProxyManager());

    public BuildMenu(Player player) {
        super(iRows, szInventoryName, player);
    }

    /**
     * Produces a list of Menu Items for the BuildMenu gui
     *
     * @return
     * @see MenuItem
     */
    public static ArrayList<MenuItem> getGui(ProxyManager proxyManager) {
        config = Main.instance.getConfig();

        //Initiates the list
        ArrayList<MenuItem> menuItems = new ArrayList<>();

        //Gets the list of items needed in the GUI
        int iItemsNeeded = 0;
        boolean[] bItemsNeeded = new boolean[4];
        int[] iSlotsToBeUsed;

        bItemsNeeded[0] = config.getBoolean(ConfigPaths.BUILD_ITEM_ENABLED);
        if (bItemsNeeded[0])
            iItemsNeeded++;

        bItemsNeeded[1] = config.getBoolean(ConfigPaths.PLOT_SYSTEM_ITEM_ENABLED) && proxyManager.isConnected();
        if (bItemsNeeded[1])
            iItemsNeeded++;

        bItemsNeeded[2] = config.getBoolean(ConfigPaths.REGIONS_ITEM_ENABLED) && proxyManager.isConnected();
        if (bItemsNeeded[2])
            iItemsNeeded++;

        bItemsNeeded[3] = config.getBoolean(ConfigPaths.TOOLS_ITEM_ENABLED);
        if (bItemsNeeded[3])
            iItemsNeeded++;

        //Deals with the number of items needed
        if (iItemsNeeded == 0)
            return menuItems;
        else
            iSlotsToBeUsed = MenuItem.getSlotIndexesMiddleRowOf3(iItemsNeeded);

        //---------------------------------------------------
        //------------------Creates the GUI------------------
        //---------------------------------------------------
        int iItem = 0;

        //--------------------------------------------
        //------------------Tutorial------------------
        //--------------------------------------------
        if (bItemsNeeded[0]) {
            //Creates the lore for tutorials
            ArrayList<String> tutorialsLore = new ArrayList<>();
            tutorialsLore.add(Utils.loreText("Learn more BTE skills"));

            //Creates the item for tutorials
            ItemStack tutorialItem = Item.create(Material.getMaterial(config.getString(ConfigPaths.TUTORIALS_ITEM_MATERIAL)), ChatColor.GREEN + "" + ChatColor.BOLD + "Learn", 1, tutorialsLore);

            //Creates the menu item, specifying the click actions
            MenuItem tutorials = new MenuItem(iSlotsToBeUsed[iItem], tutorialItem, player ->
            {
                //Opens the tutorials menu
                new TutorialsMenu(player);
            });
            menuItems.add(tutorials);

            iItem++;
        }

        //-------------------------------------------
        //----------------Plot System----------------
        //-------------------------------------------
        if (bItemsNeeded[1]) {
            //Creates the lore for plot system
            ArrayList<String> plotsLore = new ArrayList<>();
            plotsLore.add(Utils.loreText("Access your plots"));

            //Creates the item for plots
            ItemStack plotsItem = Item.create(Material.getMaterial(config.getString(ConfigPaths.PLOT_SYSTEM_ITEM_MATERIAL)), ChatColor.GREEN + "" + ChatColor.BOLD + "Plots", 1, plotsLore);

            //Creates the menu item, specifying the click actions
            MenuItem plotSystem = new MenuItem(iSlotsToBeUsed[iItem], plotsItem, player ->
            {
                //Connects the player to the plot server
                // Main.getBuildTeamTools().getProxyManager().switchServer(player, ProxyManager.PLOT_SERVER_NAME);
            });
            menuItems.add(plotSystem);

            iItem++;
        }

        //-------------------------------------------
        //------------------Regions------------------
        //-------------------------------------------
        if (bItemsNeeded[2]) {
            //Creates the lore for regions
            ArrayList<String> regionsLore = new ArrayList<>();
            regionsLore.add(Utils.loreText("Teleport to a specific region or team"));

            //Creates the item for regions
            ItemStack regionsItem = Item.create(Material.getMaterial(config.getString(ConfigPaths.REGIONS_ITEM_MATERIAL)), ChatColor.GREEN + "" + ChatColor.BOLD + "Regions", 1, regionsLore);

            //Creates the menu item, specifying the click actions
            MenuItem regions = new MenuItem(iSlotsToBeUsed[iItem], regionsItem, player ->
            {
                //Opens the regions menu
                // new RegionsList or possibly just the teams list menu. Explore should have regions list though
            });
            menuItems.add(regions);

            iItem++;
        }

        //-------------------------------------------
        //-------------------Tools-------------------
        //-------------------------------------------
        if (bItemsNeeded[3]) {
            //Creates the lore for regions
            ArrayList<String> toolsLore = new ArrayList<>();
            toolsLore.add(Utils.loreText("Access the BTE build tools"));

            //Creates the item for tools
            ItemStack toolsItem = Item.create(Material.getMaterial(config.getString(ConfigPaths.TOOLS_ITEM_MATERIAL)), ChatColor.GREEN + "" + ChatColor.BOLD + "Build Tools", 1, toolsLore);

            //Creates the menu item, specifying the click actions
            MenuItem tools = new MenuItem(iSlotsToBeUsed[iItem], toolsItem, player ->
            {
                //Opens the tools menu
                new GeneratorMenu(player);
            });
            menuItems.add(tools);
        }

        //--------------------------------------------
        //--------------------Back--------------------
        //--------------------------------------------

        //Creates the item for back button
        ItemStack backItem = MenuItem.backButton("Main Menu");

        //Creates the menu item, specifying the click actions
        MenuItem back = new MenuItem((iRows * 9) - 1, backItem, player ->
        {
            //Opens the main menu
            new MainMenu(player);
        });
        menuItems.add(back);

        return menuItems;
    }

    @Override
    protected void setMenuItemsAsync() {
        setMenuItemsAsyncViaMenuItems(menuItems);
    }

    @Override
    protected void setItemClickEventsAsync() {
        setMenuItemClickEventsAsyncViaMenuItems(menuItems);
    }

    @Override
    protected Mask getMask() {
        return null;
    }
}

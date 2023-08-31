package net.buildtheearth.buildteam.components.universal_experience.universal_navigator;

import net.buildtheearth.Main;
import net.buildtheearth.buildteam.components.universal_experience.Country;
import net.buildtheearth.buildteam.components.universal_experience.universal_navigator.explore_children.CountryMenu;
import net.buildtheearth.buildteam.components.universal_experience.universal_navigator.explore_children.CountrySelectorMenu;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.MenuItem;
import net.buildtheearth.utils.Utils;
import net.buildtheearth.utils.menus.AbstractMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.mask.Mask;

import java.util.ArrayList;

/**
 * The explore menu for the BTE universal navigator. <p>
 * <p>
 * <p> Accessed from here is the warp menu, the plot system, the region menu and the building tools (generator) menu.
 * All of these icons can be enabled and disabled. <p>
 * <p>
 * <p> The menu has 3 rows and the centre row is the only occupied row. The layout depends on what icons are enabled in config.
 */
public class ExploreMenu extends AbstractMenu
{
    private static final int iRows = 3;
    private static final String szInventoryName = "Explore Menu";
    private final ArrayList<MenuItem> menuItems;

    public ExploreMenu(Player menuPlayer, boolean bNetworkConnected)
    {
        super(iRows, szInventoryName, menuPlayer, false);
        this.menuItems = getGui(bNetworkConnected, Main.instance.getConfig());
        reloadMenuAsync();
    }

    public static ArrayList<MenuItem> getGui(boolean bNetworkConnected, FileConfiguration config)
    {
        //Initiates the list
        ArrayList<MenuItem> menuItems = new ArrayList<>();

        //Creates slot index
        int[] iSlots = new int[]{1, 3, 5, 7, 11, 13, 15};

        //--------------------------------------------
        //-----------Create continent items-----------
        //--------------------------------------------
        Continent[] continents = Continent.values();
        for (int i = 0 ; i < 7 ; i++)
        {
            //Creates the lore
            ArrayList<String> continentLore = new ArrayList<>();
            continentLore.add(Utils.loreText("Visit countries in " +continents[i].label));

            //Creates the item
            ItemStack continentItem = Item.create(Material.getMaterial(config.getString("navigator.main_menu_items.explore.material")),
                    ChatColor.YELLOW +"" +ChatColor.BOLD +"" +continents[i].label, 1, continentLore);

            MenuItem continent;
            int iSlot = i;
            if (continents[i].equals(Continent.Africa))
            {
                continent = new MenuItem(iSlots[i], continentItem, player ->
                {
                    //The actions for the continent item
                    new CountryMenu(player, bNetworkConnected, new Country("Africa", Continent.Africa));
                });
            }
            else
            {
                continent = new MenuItem(iSlots[i], continentItem, player ->
                {
                    //Opens a new country selector menu
                    new CountrySelectorMenu(continents[iSlot],  player, bNetworkConnected);
                });
            }

            menuItems.add(continent);
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
    protected void setMenuItemsAsync()
    {
        setMenuItemsAsyncViaMenuItems(menuItems);
    }

    @Override
    protected void setItemClickEventsAsync()
    {
        setMenuItemClickEventsAsyncViaMenuItems(menuItems);
    }

    @Override
    protected Mask getMask()
    {
        return null;
    }

    public enum Continent {
        North_America("North America"),
        South_America("South America"),
        Europe("Europe"),
        Africa("Africa"),
        Asia("Asia"),
        Oceania("Oceania"),
        Other("Asia");

        public final String label;

        Continent(String label) {
            this.label = label;
        }
    }

}


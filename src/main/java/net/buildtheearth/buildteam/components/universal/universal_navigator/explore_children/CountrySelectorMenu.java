package net.buildtheearth.buildteam.components.universal.universal_navigator.explore_children;

import net.buildtheearth.buildteam.NetworkAPI;
import net.buildtheearth.buildteam.components.universal.Country;
import net.buildtheearth.buildteam.components.universal.universal_navigator.ExploreMenu;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.MenuItem;
import net.buildtheearth.utils.menus.AbstractMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.mask.Mask;

import java.util.ArrayList;

/**
 * The continent or country selector menu for the BTE universal navigator. <p>
 * <p>
 * <p> Displayed on the continent menu is a list of all of the countries in the relevant continent. These are not configurable
 *  Clicking on a country will open a menu for that country, see {@link CountryMenu}.
 * <p> <p>
 * <p> The menu has 3 rows. The top two rows are filled with continents. There is a back button at the bottom right.
 */
public class CountrySelectorMenu extends AbstractMenu
{
    private static final int iRows = 5;
    private static String szInventoryName;
    private final ArrayList<MenuItem> menuItems;

    private ExploreMenu.Continent continent;

    public CountrySelectorMenu(ExploreMenu.Continent continent, Player player, boolean bNetworkConnected)
    {
        super(iRows, "Visit countries in " +continent.toString(), player, false);
        this.szInventoryName = "Visit countries in " +continent.toString();
        this.continent = continent;
        menuItems = getGui(bNetworkConnected, continent);
        reloadMenuAsync();
    }

    public static ArrayList<MenuItem> getGui(boolean bNetworkConnected, ExploreMenu.Continent continent)
    {
        //Initiates the list
        ArrayList<MenuItem> menuItems = new ArrayList<>();

        //Fetches a list of all countries on the specified continent
        Country[] countries = NetworkAPI.getAllCountriesInContinent(continent);
        int iNumCountries = countries.length;

        //Create country icons
        for (int i = 0 ; i < iNumCountries && i < 27; i++)
        {
            //Creates the lore
            ArrayList<String> countryLore = new ArrayList<>();
            countryLore.add(ChatColor.BLUE +"Explore " +countries[i].getName());

            //Creates the menu icon item
            String szCountryItemName = ChatColor.GREEN +"" +ChatColor.BOLD +"" +countries[i].getName();
            ItemStack countryItem = Item.createCustomHeadBase64(countries[i].getFlagHeadID(), szCountryItemName, countryLore);

            int iSlot = i;
            //Creates the menu item, specifies the slot, icon and action
            MenuItem country = new MenuItem(iSlot, countryItem, player ->
            {
                //Opens the country menu
                new CountryMenu(player, bNetworkConnected, countries[iSlot]);
            });

            menuItems.add(country);
        }

        //--------------------------------------------
        //--------------------Back--------------------
        //--------------------------------------------

        //Creates the item for back button
        ItemStack backItem = MenuItem.backButton("list of continents");

        //Creates the menu item, specifying the click actions
        MenuItem back = new MenuItem((iRows * 9) - 1, backItem, player ->
        {
            //Opens the main menu
            new ExploreMenu(player);
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
    protected Mask getMask() {
        return null;
    }
}

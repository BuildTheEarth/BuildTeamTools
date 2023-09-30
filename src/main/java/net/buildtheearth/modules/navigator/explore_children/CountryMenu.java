package net.buildtheearth.modules.navigator.explore_children;

import net.buildtheearth.modules.network.api.NetworkAPI;
import net.buildtheearth.buildteam.components.universal_experience.BuildTeam;
import net.buildtheearth.buildteam.components.universal_experience.Category;
import net.buildtheearth.buildteam.components.universal_experience.Country;
import net.buildtheearth.modules.navigator.menu.ExploreMenu;
import net.buildtheearth.modules.utils.Item;
import net.buildtheearth.modules.utils.MenuItems;
import net.buildtheearth.modules.utils.Utils;
import net.buildtheearth.modules.utils.MenuItem;
import net.buildtheearth.modules.utils.menus.AbstractMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.mask.Mask;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;

/**
 * The country menu for the BTE universal navigator. <p>
 * <p>
 * <p> The country menu is the home menu for navigating a specific country. From here you can access all of the warps,
 * sometimes these are nested in categories. Clicking on a location will teleport the player there if either the player and the location
 * are on the BTE network or if the player and location are on the same server. <p>
 * <p>
 * <p> The menu has 5 rows. The top three rows are filled with categories or locations. <p>
 * <p>
 * <p> The middle of the 5th row has an icon representing the team that the country belongs to. The item is a flag head of the country.
 * Displayed is the build team name, the build team description and the server IP if the build team or the player is not on the BTE network.
 * Clicking on this will either copy the IP of the team or send the player to their server if both are on the network<p>
 * <p>
 * <p> There is a back button at the bottom right (5th row 9th column)
 */
public class CountryMenu extends AbstractMenu
{
    private static final int iRows = 5;
    private static String szInventoryName;
    private final ArrayList<MenuItem> menuItems;
    private Country country;

    public CountryMenu(Player player, boolean bNetworkConnected, Country country)
    {
        super(iRows, "Explore " +country.getName(), player, false);
        this.szInventoryName = "Explore " +country.getName();
        this.country = country;
        menuItems = getGui(bNetworkConnected, country);
        reloadMenuAsync();
    }

    public static ArrayList<MenuItem> getGui(boolean bNetworkConnected, Country country)
    {
        //Initiates the list
        ArrayList<MenuItem> menuItems = new ArrayList<>();

        //--------------------------------------------
        //------------Locations/Categories------------
        //--------------------------------------------
        Category[] warpLocations = NetworkAPI.getAllLocationsForCountry(country.getName());

        for (Category category : warpLocations)
        {
            if (category.getCategoryName().equals("Uncategorised"))
            {
                //Do something
                //Note the index? if there are no categorised locations then we want all the uncategorised ones on display straight away
            }
            if (category.getLocations().length > 1)
            {
                //Create a category icon and link this to a category location menu
            }
        }

        //How should the locations be editable? A right click from a BTO brings up an add location menu?

        //--------------------------------------------
        //----------------Add location----------------
        //--------------------------------------------

        //A button shown to BT admins to add locations
        //Also shown to regular builders but this one would suggest a command


        //Only shows if the country the player is in and the location the menu is at match?

        boolean bAdmin = true;

        if (bAdmin)
        {
            //Creates the lore
            ArrayList<String> newLocationLore = new ArrayList<>(2);
            newLocationLore.add(Utils.loreText("Create a new location for this country "));
            newLocationLore.add(Utils.loreText("at the location you are standing"));

            ItemStack newLocationItem = MenuItems.getCounterPlusItem(MenuItems.SliderColor.WHITE, ChatColor.GREEN +"Add Location", 0, 1);
            newLocationItem.getItemMeta().setLore(newLocationLore);

            MenuItem newLocation = new MenuItem((iRows*9) - 9 , newLocationItem, player ->
            {
                new AddLocationMenu(player, country, bNetworkConnected);
            });

            menuItems.add(newLocation);
        }



        //---------------------------------------------
        //------------------Team info------------------
        //---------------------------------------------

        //Displays the information of the team of the country which menu is displayed

        BuildTeam buildTeam = country.getBuildTeam();

        //Creates the lore
        ArrayList<String> teamLore = new ArrayList<>();
        teamLore.add(buildTeam.getName() +ChatColor.GRAY +" - [" +ChatColor.YELLOW + buildTeam.getCode() + ChatColor.GRAY +"]");
        teamLore.add("");
        teamLore.add(ChatColor.YELLOW +"Description:");
        teamLore.add(ChatColor.GRAY +buildTeam.getDescription());
        teamLore.add("");
        if (bNetworkConnected && buildTeam.isOnNetwork())
            teamLore.add(ChatColor.YELLOW +"Click to connect !");
        else
        {
            teamLore.add(ChatColor.YELLOW +"Current IP:");
            teamLore.add(ChatColor.YELLOW +buildTeam.getServerIP());
            teamLore.add(ChatColor.GRAY +"Click the flag to copy the IP");
        }

        //Creates the item for team head
        String szTeamName = country.getBuildTeam().getName();
        szTeamName = ChatColor.translateAlternateColorCodes('&', szTeamName);
        ItemStack teamItem = Item.createCustomHeadBase64(country.getFlagHeadID(), szTeamName, teamLore);

        //Creates the menu item, specifying the click actions
        MenuItem team = new MenuItem((iRows * 9) - 5, teamItem, player ->
        {
            //Connects the player to the server of the team
            if (bNetworkConnected && buildTeam.isOnNetwork())
                buildTeam.connectPlayer(player);

            //Saves the ip in the player's clipboard
            else
            {
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                Clipboard clipboard = toolkit.getSystemClipboard();
                StringSelection strSel = new StringSelection(buildTeam.getServerIP());
                clipboard.setContents(strSel, null);
            }
        });
        menuItems.add(team);

        //--------------------------------------------
        //--------------------Back--------------------
        //--------------------------------------------

        MenuItem back;

        //Creates the menu item, specifying the click actions
        if (country.getName().equals("Africa")) //Africa continent has no continent menu, just one "Africa" country, so going back must go to the explore menu
        {
            //Creates the item for back button
            ItemStack backItem = MenuItem.backButton("Explore Menu");

            back = new MenuItem((iRows * 9) - 1, backItem, player ->
            {
                //Opens the explore menu (list of continents)
                new ExploreMenu(player, bNetworkConnected);
            });
        }
        else
        {
            ItemStack backItem = MenuItem.backButton("the list of countries");

            back = new MenuItem((iRows * 9) - 1, backItem, player ->
            {
                //Opens the continent menu (list of countries)
                new CountrySelectorMenu(country.getContinent(), player, bNetworkConnected);
            });
        }
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

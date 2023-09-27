package net.buildtheearth.buildteam.components.universal_experience.universal_navigator.explore_children;

import net.buildtheearth.Main;
import net.buildtheearth.buildteam.components.universal_experience.Country;
import net.buildtheearth.buildteam.components.universal_experience.Location;
import net.buildtheearth.buildteam.components.universal_experience.universal_navigator.ExploreMenu;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.MenuItem;
import net.buildtheearth.utils.MenuItems;
import net.buildtheearth.utils.Utils;
import net.buildtheearth.utils.menus.AbstractMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.mask.Mask;

import java.util.ArrayList;

/**
 * Note that builders will request just via a command
 */
public class AddLocationMenu extends AbstractMenu
{
    private static final int iRows = 3;
    private static String szInventoryName;
    private final ArrayList<MenuItem> menuItems;

    private Player player;
    private Country country;
    private Location location;

    private boolean bNetworkConnected;

    //Will automatically detect the country

    /**
     * Used for an admin creating a new location
     * @param player
     * @param country
     */
    public AddLocationMenu(Player player, Country country, boolean bNetworkConnected)
    {
        super(iRows, createInventoryName(false), player, false);
        this.szInventoryName = createInventoryName(false);

        this.player = player;
        this.country = country;
        this.location = new Location(player.getLocation());

        this.bNetworkConnected = bNetworkConnected;

        this.menuItems = getGuiAdminCreateNew();
        reloadMenuAsync();
    }

    /**
     * Used for an admin reviewing a location request
     * @param player
     * @param request
     * @param country
     */
//    public AddLocationMenu(Player player, LocationRequest request, Country country)
//    {
//        super(iRows, createInventoryName(false), player, false);
//        this.szInventoryName = createInventoryName(false);
//
//        this.player = player;
//        this.country = country;
//        this.location = new Location(request);
//
//        this.menuItems = getGuiForRequest();
//        reloadMenuAsync();
//    }

    /**
     * Used for an admin editing a location
     * @param player
     * @param country
     * @param location
     */
    public AddLocationMenu(Player player, Country country, Location location)
    {
        super(iRows, createInventoryName(true), player, false);
        this.szInventoryName = createInventoryName(true);

        this.player = player;
        this.country = country;
        this.location = location;

        this.menuItems = getGuiForEditing();
        reloadMenuAsync();
    }

    /**
     * Gets the gui for an admin creating a new location
     * @return A list of MenuItems
     */
    public ArrayList<MenuItem> getGuiAdminCreateNew()
    {
        //Initiates the list
        ArrayList<MenuItem> menuItems = new ArrayList<>();

        //Gets the slots
        int[] iSlots = MenuItem.getSlotIndexesMiddleRowOf3(4);

        //--------------------------------------------
        //---------(1) Country - display only---------
        //--------------------------------------------

        //Creates the lore
        ArrayList<String> countryDisplayIconLore = new ArrayList<>(1);
        countryDisplayIconLore.add(Utils.loreText("This location is being added to " +country.getName()));

        //Creates the icon
        ItemStack countryDisplayIcon = Item.createCustomHeadBase64(MenuItems.EARTH, Utils.menuIconTitle(country.getName()), countryDisplayIconLore);

        MenuItem countryDisplay = new MenuItem(iSlots[0], countryDisplayIcon, player1 -> {});
        menuItems.add(countryDisplay);


        //--------------------------------------------
        //---------(2) Edit name - opens chat---------
        //--------------------------------------------

        //Creates the lore
        ArrayList<String> locationNameLore = new ArrayList<>(1);
        locationNameLore.add(Utils.loreText("Click to edit the name of the location"));

        //Creates the icon
        ItemStack locationNameItem = Item.create(Material.SIGN, Utils.menuIconTitle(location.getName()), 1, locationNameLore);

        MenuItem locationName = new MenuItem(iSlots[1], locationNameItem, player1 -> {
            //Sends user the prompt
            player1.sendMessage(ChatColor.GREEN +"Enter the name of the location in chat");

            //Closes the current inventory
            player1.closeInventory(InventoryCloseEvent.Reason.PLUGIN);

            //Listens for the chat message
            new ChatListener(this, this.location, ChatListener.ChatType.LocationName);
        });
        menuItems.add(locationName);


        //---------------------------------------------------
        //(3) Choose category - opens a category chooser menu
        //---------------------------------------------------

        //Creates the lore
        ArrayList<String> categoryLore = new ArrayList<>(1);
        countryDisplayIconLore.add(Utils.loreText("Choose the category"));

        //Creates the icon
        ItemStack categoryIcon = ;


        //-------------------------------------------
        //----------------Confirm/add----------------
        //-------------------------------------------

        //Creates the item for confirm/add button
        ItemStack confirmItem;
        
        //Creates the menu item, specifying the click actions
        MenuItem back = new MenuItem((iRows * 9) - 1, backItem, player ->
        {
            //Opens the country menu
            new CountryMenu(player, bNetworkConnected, this.country);
        });
        menuItems.add(back);


        //--------------------------------------------
        //--------------------Back--------------------
        //--------------------------------------------

        //Creates the item for back button
        ItemStack backItem = MenuItem.backButton("list of locations in " +country.getName());

        //Creates the menu item, specifying the click actions
        MenuItem back = new MenuItem((iRows * 9) - 1, backItem, player ->
        {
            //Opens the country menu
            new CountryMenu(player, bNetworkConnected, this.country);
        });
        menuItems.add(back);

        return menuItems;
    }

    /**
     * Yet to edit - this is just copied
     * @return
     */
    public ArrayList<MenuItem> getGuiForRequest()
    {
        //Initiates the list
        ArrayList<MenuItem> menuItems = new ArrayList<>();

        //Gets the slots
        int[] iSlots = MenuItem.getSlotIndexesMiddleRowOf3(4);

        //Country - display only
        ArrayList<String> countryDisplayIconLore = new ArrayList<>(1);
        countryDisplayIconLore.add(Utils.loreText("This location is being added to " +country.getName()));

        ItemStack countryDisplayIcon = Item.createCustomHeadBase64(MenuItems.EARTH, Utils.menuIconTitle(country.getName()), countryDisplayIconLore);

        //Choose category - opens a category chooser menu
        ArrayList<String> categoryLore = new ArrayList<>(1);
        countryDisplayIconLore.add(Utils.loreText("Choose the category"));

        ItemStack categoryDisplayIcon = Item.createCustomHeadBase64(MenuItems.EARTH, Utils.menuIconTitle(country.getName()), categoryLore);


        //Edit name - opens a book

        //Set location option

        //Add
        //Checkmark one?

    }

    /**
     * Yet to edit - this is just copied
     * @return
     */
    public ArrayList<MenuItem> getGuiForEditing()
    {
        //Initiates the list
        ArrayList<MenuItem> menuItems = new ArrayList<>();

        //Gets the slots
        int[] iSlots = MenuItem.getSlotIndexesMiddleRowOf3(4);

        //(1) Country - display only
        ArrayList<String> countryDisplayIconLore = new ArrayList<>(1);
        countryDisplayIconLore.add(Utils.loreText("This location is being added to " +country.getName()));

        ItemStack countryDisplayIcon = Item.createCustomHeadBase64(MenuItems.EARTH, Utils.menuIconTitle(country.getName()), countryDisplayIconLore);

        MenuItem countryDisplay = new MenuItem(iSlots[0], countryDisplayIcon, player1 -> {});
        menuItems.add(countryDisplay);

        //(2) Edit name - opens chat
        ArrayList<String> locationNameLore = new ArrayList<>(1);
        locationNameLore.add(Utils.loreText("Click to edit the name of the location"));

        ItemStack locationNameItem = Item.createCustomHeadBase64(MenuItems.EARTH, Utils.menuIconTitle(country.getName()), countryDisplayIconLore);

        MenuItem locationName = new MenuItem(iSlots[1], locationNameItem, player1 -> {
            player1.sendMessage(ChatColor.GREEN +"Enter the name of the location in chat");

            player1.closeInventory(InventoryCloseEvent.Reason.PLUGIN);

            //Listens for the chat message
            new ChatListener(this, this.location, ChatListener.ChatType.LocationName);
        });
        menuItems.add(locationName);

        //Set location option

        //Add
        //Checkmark one?

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

    private static String createInventoryName(boolean bEditing)
    {
        if (bEditing)
            return "Edit a Location";
        else
            return "Add a Location";

    }

    /**
     * Recreates the menu icons and redisplays the menu to the player
     */
    public void refreshMenu()
    {
        getGuiAdminCreateNew();
        reloadMenuAsync();
    }
}

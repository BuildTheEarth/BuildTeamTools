package net.buildtheearth.utils.menus;

import net.buildtheearth.Main;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.MenuItem;
import net.buildtheearth.utils.MenuItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.mask.Mask;
import org.ipvp.canvas.type.ChestMenu;

import java.util.ArrayList;

public abstract class AbstractMenu {

    private final Menu menu;
    private final Player menuPlayer;

    public AbstractMenu(int rows, String title, Player menuPlayer) {
        this(rows, title, menuPlayer, true);
    }

    public AbstractMenu(int rows, String title, Player menuPlayer, boolean reload) {
        this.menuPlayer = menuPlayer;
        this.menu = ChestMenu.builder(rows).title(title).redraw(true).build();

        if (reload) reloadMenuAsync();
    }

    /**
     * Places items asynchronously in the menu after it is opened
     */
    protected abstract void setMenuItemsAsync();

    /**
     * Sets click events for the items placed in the menu async after it is opened
     */
    protected abstract void setItemClickEventsAsync();

    /**
     * Places pre-defined items in the menu before it is opened
     * @return Pre-defined mask
     * @see <a href=https://github.com/IPVP-MC/canvas#masks</a>
     */
    protected abstract Mask getMask();

    /**
     * Places items synchronously in the menu and opens it afterwards
     * NOTE: This method gets called before class is loaded!
     */
    protected void setPreviewItems() {
        if(getMask() != null) getMask().apply(getMenu());
        getMenu().open(getMenuPlayer());
    }

    /**
     * Reloads all menu items and click events in the menu asynchronously
     * {@link #setPreviewItems()}.{@link #setMenuItemsAsync()}.{@link #setItemClickEventsAsync()}
     */
    protected void reloadMenuAsync() {
        setPreviewItems();
        Bukkit.getScheduler().runTaskAsynchronously(Main.instance, () -> {
            setMenuItemsAsync();
            setItemClickEventsAsync();
        });
    }

    /**
     * @return Inventory
     */
    protected Menu getMenu() {
        return menu;
    }

    /**
     * @return Inventory player
     */
    protected Player getMenuPlayer() {
        return menuPlayer;
    }

    /**
     * Creates a horizontal counter with 3 items, a plus, a minus and a current value item.
     * This counter allows the player to increase or decrease the value of the item from minValue to maxValue.
     * It places the items in the open inventory of the player with the given slot as the current value item as a reference.
     * The plus and minus items are placed to the left and right of the current value item.
     *
     * @param sliderColor - Color of the slider
     * @param sliderItemSlot - Slot of the current value item in the center
     * @param sliderName - Name of the slider
     * @param value - Current value of the slider
     * @param minValue - Minimum value of the slider
     * @param maxValue - Maximum value of the slider
     * @param valueType - Type of the value (e.g. "m", "°C", "°F", "blocks", "chunks", "seconds", "minutes", "hours", "days", "weeks", "months", "years", ...)
     */
    protected void createCounter(MenuItems.SliderColor sliderColor, int sliderItemSlot, String sliderName, int value, int minValue, int maxValue, String valueType){
        // Set previous page item
        getMenu().getSlot(sliderItemSlot - 1).setItem(MenuItems.getCounterMinusItem(sliderColor, sliderName, value, minValue));

        // Set current page item
        getMenu().getSlot(sliderItemSlot).setItem(MenuItems.getCounterCurrentValueItem(sliderColor, sliderName, value, valueType));

        // Set next page item
        getMenu().getSlot(sliderItemSlot + 1).setItem(MenuItems.getCounterPlusItem(sliderColor, sliderName, value, maxValue));
    }

    /**
     * Creates an item that lets you select the right block from a list of blocks.
     * It places the items in the open inventory of the player with the given slot as the current block item as a reference.
     * To the left and right of the block item are toggle off items that let the player disable that feature if he doesn't want to use it.
     *
     * @param sliderColor - Color of the slider
     * @param sliderItemSlot - Slot of the current block item in the center
     * @param sliderName - Name of the slider
     * @param current - Current block item
     */
    protected void setColorChoiceItems(MenuItems.SliderColor sliderColor, int sliderItemSlot, String sliderName, ItemStack current){
        sliderName = "§e" + sliderName;

        if(current == null) {
            // Set previous page item
            getMenu().getSlot(sliderItemSlot - 1).setItem(MenuItems.getBlankItem(sliderColor, sliderName + ": §c§lOFF"));

            // Set current page item
            getMenu().getSlot(sliderItemSlot).setItem(Item.create(Material.BARRIER, sliderName + ": §c§lOFF"));

            // Set next page item
            getMenu().getSlot(sliderItemSlot + 1).setItem(MenuItems.getBlankItem(sliderColor, sliderName + ": §c§lOFF"));
        }else{
            ItemMeta meta = current.getItemMeta();
            meta.setDisplayName(sliderName);
            current.setItemMeta(meta);

            // Set previous page item
            getMenu().getSlot(sliderItemSlot - 1).setItem(MenuItems.getXItem(sliderColor, "§cDisable " + sliderName));

            // Set current page item
            getMenu().getSlot(sliderItemSlot).setItem(current);

            // Set next page item
            getMenu().getSlot(sliderItemSlot + 1).setItem(MenuItems.getXItem(sliderColor, "§cDisable " + sliderName));
        }
    }

    protected void setMenuItemsAsyncViaMenuItems(ArrayList<MenuItem> menuItems)
    {
        int i;
        int iNumIcons = menuItems.size();
        MenuItem menuItem;

        //Adds all of the icons
        for (i = 0 ; i < iNumIcons ; i++)
        {
            menuItem = menuItems.get(i);
            getMenu().getSlot(menuItem.getSlot()).setItem(menuItem.getDisplayIcon());
        }
    }

    protected void setMenuItemClickEventsAsyncViaMenuItems(ArrayList<MenuItem> menuItems)
    {
        int i;
        int iNumIcons = menuItems.size();
        MenuItem menuItem;

        //Adds all of the actions
        for (i = 0 ; i < iNumIcons ; i++)
        {
            menuItem = menuItems.get(i);
            MenuItem finalMenuItem = menuItem;
            getMenu().getSlot(menuItem.getSlot()).setClickHandler((player, clickInformation) -> finalMenuItem.getAction().click(player));
        }
    }

}

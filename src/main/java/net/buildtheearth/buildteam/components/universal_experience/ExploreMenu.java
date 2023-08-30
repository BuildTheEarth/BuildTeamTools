package net.buildtheearth.buildteam.components.universal_experience;

import net.buildtheearth.Main;
import net.buildtheearth.utils.MenuItem;
import net.buildtheearth.utils.menus.AbstractMenu;
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
    private static final ArrayList<MenuItem> menuItems = getGui();
    private static final FileConfiguration config = Main.instance.getConfig();

    public ExploreMenu(Player menuPlayer)
    {
        super(iRows, szInventoryName, menuPlayer);
    }

    public static ArrayList<MenuItem> getGui()
    {
        //Initiates the list
        ArrayList<MenuItem> menuItems = new ArrayList<>();

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
}

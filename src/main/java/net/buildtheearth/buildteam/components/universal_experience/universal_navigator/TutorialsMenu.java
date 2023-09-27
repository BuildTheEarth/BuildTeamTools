package net.buildtheearth.buildteam.components.universal_experience.universal_navigator;

import net.buildtheearth.Main;
import net.buildtheearth.utils.MenuItem;
import net.buildtheearth.utils.menus.AbstractMenu;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.mask.Mask;

import java.util.ArrayList;

public class TutorialsMenu extends AbstractMenu
{
    //Will utilise the Tutorials API once created and published

    //3 rows
    //Top and bottom blank, middle row filled with options, layout depends on what icons are enabled in config
    private static final int iRows = 3;
    private static final String szInventoryName = "Tutorials Menu";
    private static final ArrayList<MenuItem> menuItems = getGui();
    private static final FileConfiguration config = Main.instance.getConfig();

    public TutorialsMenu(Player player)
    {
        super(iRows, szInventoryName, player);
    }

    /**
     * Produces a list of Menu Items for the BuildMenu gui
     * @see MenuItem
     * @return
     */
    public static ArrayList<MenuItem> getGui()
    {
        //Initiates the list
        ArrayList<MenuItem> menuItems = new ArrayList<>();

        //--------------------------------------------
        //--------------------Back--------------------
        //--------------------------------------------

        //Creates the item for back button
        ItemStack backItem = MenuItem.backButton("Build Menu");

        //Creates the menu item, specifying the click actions
        MenuItem back = new MenuItem((iRows * 9) - 1, backItem, player ->
        {
            //Opens the build menu
            new BuildMenu(player);
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

package net.buildtheearth.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * Represents a Menu item on the universal navigator, holding the slot, the display icon and the action to be performed on click
 */
public class MenuItem
{
    /**
     * The slot that the Menu Item is to inhabit
     */
    private int iSlot;

    /**
     * The minecraft item displayed on the menu, including title, lore, material and amount
     */
    private ItemStack displayIcon;

    /**
     * Stores the action that is to be performed on clicking on item
     */
    private guiAction action;

    public interface guiAction
    {
        void click(Player player);
    }

    public MenuItem(int iSlot, ItemStack displayIcon, guiAction action)
    {
        this.iSlot = iSlot;
        this.displayIcon = displayIcon;
        this.action = action;
    }

    public int getSlot()
    {
        return iSlot;
    }

    public ItemStack getDisplayIcon()
    {
        return displayIcon;
    }

    public guiAction getAction()
    {
        return action;
    }

    /**
     * Returns a list of slot indexes to put menu icons in when creating a 3 row menu where items must be placed in the middle row.
     * This is suitable for 1-5 items only on a 3 row by 9 column menu
     * @param iNumItemsNeeded The number of items to go in the inventory
     * @return
     */
    public static int[] getSlotIndexesMiddleRowOf3(int iNumItemsNeeded)
    {
        //Info:
        // Slot 9 is the left most index of row 2
        // Slot 13 is the middle of row 2
        // Slot 17 is the right most index of row 2
        int[] iSlotsToBeUsed;

        switch (iNumItemsNeeded)
        {
            case 1:
                iSlotsToBeUsed = new int[]{13};
                break;
            case 2:
                iSlotsToBeUsed = new int[]{11, 15};
                break;
            case 3:
                iSlotsToBeUsed = new int[]{11, 13, 15};
                break;
            case 4:
                iSlotsToBeUsed = new int[]{10, 12, 14, 16};
                break;
            case 5:
            default:
                iSlotsToBeUsed = new int[]{9, 11, 13, 15, 17};
                break;
        }
        return iSlotsToBeUsed;
    }

    /**
     * Creates an item stack representing a back button in the universal navigator
     * @param szTargetMenuName The name of the menu which the back button goes to. In the format "main menu" or "build menu".
     * Do not include "the" at the start
     * @return An item stack with material spruce door and lore "back to the [menu name]"
     */
    public static ItemStack backButton(String szTargetMenuName)
    {
        //Creates the lore for back button
        ArrayList<String> backLore = new ArrayList<>();
        backLore.add(Utils.loreText("Back to the " +szTargetMenuName));

        //Creates the item for tools
        ItemStack backItem = Item.create(Material.SPRUCE_DOOR, ChatColor.GREEN +"" +ChatColor.BOLD +"Back", 1, backLore);

        return backItem;
    }
}

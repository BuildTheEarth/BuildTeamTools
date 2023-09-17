package net.buildtheearth.buildteam.components.universal.universal_navigator;

import com.alpsbte.alpslib.utils.item.ItemBuilder;
import com.sun.tools.javac.util.List;
import net.buildtheearth.Main;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.Utils;
import net.buildtheearth.utils.io.ConfigPaths;
import net.buildtheearth.utils.menus.AbstractMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The main menu for the BTE universal navigator. <p>
 * <p>
 * <p> Accessed from here is an explore menu, a build menu (plot system and tools) and a tutorials menu
 * All of these 3 icons can be enabled and disabled <p>
 * <p>
 * <p> Also in Main Menu is an option to toggle whether the navigator item is in the hotbar. A player can always use /navigator to open
 * the navigator <p>
 * <p>
 * <p> The menu has 3 rows. The centre row is occupied with Build, Explore and Tutorials (if enabled), and the last row holds the navigator hide option <p>
 *
 */
public class MainMenu extends AbstractMenu
{
    private static final int rows = 3;
    private static final String inventoryName = "Build the Earth";
    private static FileConfiguration config;

    public MainMenu(Player menuPlayer)
    {
        super(rows, inventoryName, menuPlayer);
    }

    @Override
    protected void setPreviewItems() {
        config = Main.instance.getConfig();
        int[] slots = getSlots();

        // Fill the blank slots with glass panes
        getMenu().getSlot(11).setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 7).setName(" ").build());
        getMenu().getSlot(13).setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 7).setName(" ").build());
        getMenu().getSlot(15).setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 7).setName(" ").build());


        // Set Build Item
        if(config.getBoolean(ConfigPaths.BUILD_ITEM_ENABLED)) {
            ArrayList<String> buildLore = new ArrayList<>(Arrays.asList(Utils.loreText(config.getString(ConfigPaths.BUILD_ITEM_LORE))));
            ItemStack buildItem = Item.create(Material.getMaterial(config.getString(ConfigPaths.BUILD_ITEM_MATERIAL)), ChatColor.GREEN + "" + ChatColor.BOLD + "Build", 1, buildLore);
            getMenu().getSlot(slots[0]).setItem(buildItem);
        }

        // Set Explore Item
        if(config.getBoolean(ConfigPaths.EXPLORE_ITEM_ENABLED)) {
            ArrayList<String> exploreLore = new ArrayList<>(Arrays.asList(Utils.loreText(config.getString(ConfigPaths.EXPLORE_ITEM_LORE))));
            ItemStack exploreItem = Item.create(Material.getMaterial(config.getString(ConfigPaths.EXPLORE_ITEM_MATERIAL)), ChatColor.YELLOW + "" + ChatColor.BOLD + "Explore", 1, exploreLore);
            getMenu().getSlot(slots[1]).setItem(exploreItem);
        }

        // Set Tutorials Item
        if(config.getBoolean(ConfigPaths.TUTORIALS_ITEM_ENABLED)) {
            ArrayList<String> tutorialsLore = new ArrayList<>(Arrays.asList(Utils.loreText(config.getString(ConfigPaths.TUTORIALS_ITEM_LORE))));
            ItemStack tutorialsItem = Item.create(Material.getMaterial(config.getString(ConfigPaths.TUTORIALS_ITEM_MATERIAL)), ChatColor.AQUA +"" +ChatColor.BOLD +"Tutorials", 1, tutorialsLore);
            getMenu().getSlot(slots[2]).setItem(tutorialsItem);
        }

        super.setPreviewItems();
    }

    @Override
    protected void setMenuItemsAsync() {}

    @Override
    protected void setItemClickEventsAsync() {
        int[] slots = getSlots();

        // Set Build Item Click Event
        if(config.getBoolean(ConfigPaths.BUILD_ITEM_ENABLED)) {
            getMenu().getSlot(slots[0]).setClickHandler((clickPlayer, clickInformation) -> {
                boolean overwriteDefault = false;
                clickPlayer.closeInventory();

                if(config.getBoolean(ConfigPaths.BUILD_ITEM_MESSAGE)){
                    overwriteDefault = true;
                    clickPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(ConfigPaths.BUILD_ITEM_MESSAGE)));
                }
                if(config.getBoolean(ConfigPaths.BUILD_ITEM_COMMAND)){
                    overwriteDefault = true;
                    clickPlayer.performCommand(config.getString(ConfigPaths.BUILD_ITEM_COMMAND));
                }
                if(!overwriteDefault)
                    new BuildMenu(clickPlayer);
            });
        }

        // Set Explore Item Click Event
        if(config.getBoolean(ConfigPaths.EXPLORE_ITEM_ENABLED)) {
            getMenu().getSlot(slots[1]).setClickHandler((clickPlayer, clickInformation) -> {
                boolean overwriteDefault = false;
                clickPlayer.closeInventory();

                if(config.getBoolean(ConfigPaths.EXPLORE_ITEM_MESSAGE)){
                    overwriteDefault = true;
                    clickPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(ConfigPaths.EXPLORE_ITEM_MESSAGE)));
                }
                if(config.getBoolean(ConfigPaths.EXPLORE_ITEM_COMMAND)){
                    overwriteDefault = true;
                    clickPlayer.performCommand(config.getString(ConfigPaths.EXPLORE_ITEM_COMMAND));
                }
                if(!overwriteDefault)
                    new ExploreMenu(clickPlayer);
            });
        }

        // Set Tutorials Item Click Event
        if(config.getBoolean(ConfigPaths.TUTORIALS_ITEM_ENABLED)) {
            getMenu().getSlot(slots[2]).setClickHandler((clickPlayer, clickInformation) -> {
                boolean overwriteDefault = false;
                clickPlayer.closeInventory();

                if(config.getBoolean(ConfigPaths.TUTORIALS_ITEM_MESSAGE)){
                    overwriteDefault = true;
                    clickPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString(ConfigPaths.TUTORIALS_ITEM_MESSAGE)));
                }
                if(config.getBoolean(ConfigPaths.TUTORIALS_ITEM_COMMAND)){
                    overwriteDefault = true;
                    clickPlayer.performCommand(config.getString(ConfigPaths.TUTORIALS_ITEM_COMMAND));
                }
                if(!overwriteDefault)
                    new TutorialsMenu(clickPlayer);
            });
        }
    }

    @Override
    protected Mask getMask()
    {
        return BinaryMask.builder(getMenu())
                .item(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 7).setName(" ").build())
                .pattern("111111111")
                .pattern("110101011")
                .pattern("111111111")
                .build();
    }



    private int[] getSlots() {
        int[] slots = new int[3];

        boolean buildEnabled = config.getBoolean(ConfigPaths.BUILD_ITEM_ENABLED);
        boolean exploreEnabled = config.getBoolean(ConfigPaths.EXPLORE_ITEM_ENABLED);
        boolean tutorialsEnabled = config.getBoolean(ConfigPaths.TUTORIALS_ITEM_ENABLED);

        int buildSlot = 11;
        int exploreSlot = 13;
        int tutorialsSlot = 15;

        int enabledItemCount = (buildEnabled ? 1 : 0) + (exploreEnabled ? 1 : 0) + (tutorialsEnabled ? 1 : 0);

        // Depending on how many items are enabled, set the slots to the correct positions
        if (enabledItemCount == 1) {
            if (buildEnabled)
                buildSlot = 13;
            else if (exploreEnabled)
                exploreSlot = 13;
            else if (tutorialsEnabled)
                tutorialsSlot = 13;

        } else if (enabledItemCount == 2) {
            if (buildEnabled && exploreEnabled)
                exploreSlot = 15;
            else if (exploreEnabled && tutorialsEnabled)
                exploreSlot = 11;

        }else if(enabledItemCount == 0 || enabledItemCount > 3)
            return null;

        slots[0] = buildSlot;
        slots[1] = exploreSlot;
        slots[2] = tutorialsSlot;

        return slots;
    }
}

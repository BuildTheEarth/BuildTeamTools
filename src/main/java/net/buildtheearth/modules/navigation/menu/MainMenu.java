package net.buildtheearth.modules.navigation.menu;

import com.alpsbte.alpslib.utils.item.ItemBuilder;
import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.network.NetworkModule;
import net.buildtheearth.utils.ChatHelper;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.Utils;
import net.buildtheearth.utils.io.ConfigPaths;
import net.buildtheearth.utils.menus.AbstractMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.ArrayList;
import java.util.Collections;

/**
 * The main menu for the BTE universal navigator. <br>
 * <br>
 * Accessed from here is an explore menu, a build menu (plot system and tools) and a tutorials menu
 * The build and tutorials item can be enabled and disabled <br>
 * <br>
 * The Main Menu also contains an option to toggle whether the navigator item is in the hotbar. A player can always use /navigator to open
 * the navigator <br>
 * <br>
 * The menu has 3 rows. The centre row is occupied with Build, Explore and Tutorials (if enabled), and the last row holds the navigator hide option.
 */
public class MainMenu extends AbstractMenu {

    private static final String inventoryName = "BuildTheEarth Navigator";
    private static FileConfiguration config;

    public MainMenu(Player menuPlayer) {
        super(3, inventoryName, menuPlayer);
    }

    @Override
    protected void setPreviewItems() {
        config = BuildTeamTools.getInstance().getConfig();
        int[] slots = getSlots();

        // Fill the blank slots with glass panes
        getMenu().getSlot(11).setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 7).setName(" ").build());
        getMenu().getSlot(13).setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 7).setName(" ").build());
        getMenu().getSlot(15).setItem(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 7).setName(" ").build());

        // Set Explore Item
        ArrayList<String> exploreLore = new ArrayList<>(Collections.singletonList(ChatHelper.colorize(ChatColor.GRAY, "Click to explore the project!", false)));
        getMenu().getSlot(slots[0]).setItem(Item.create(Material.BOAT_SPRUCE, ChatHelper.colorize(ChatColor.YELLOW, "Explore", true), 1, exploreLore));


        // Set Build Item
        if(config.getBoolean(ConfigPaths.BUILD_ITEM_ENABLED)) {
            ArrayList<String> buildLore = new ArrayList<>(Collections.singletonList(ChatHelper.colorize(ChatColor.GRAY, "Click to build for the project!", false)));
            getMenu().getSlot(slots[1]).setItem(Item.create(Material.DIAMOND_PICKAXE, ChatHelper.colorize(ChatColor.GREEN, "Build", true), 1, buildLore));
        }

        // Set Tutorials Item
        if(config.getBoolean(ConfigPaths.TUTORIALS_ITEM_ENABLED)) {
            ArrayList<String> tutorialsLore = new ArrayList<>(Collections.singletonList(ChatHelper.colorize(ChatColor.GRAY, "Click to do some tutorials!", false)));
            getMenu().getSlot(slots[2]).setItem(Item.create(Material.KNOWLEDGE_BOOK, ChatHelper.colorize(ChatColor.AQUA, "Tutorials", true), 1, tutorialsLore));
        }

        super.setPreviewItems();
    }

    @Override
    protected void setMenuItemsAsync() {}

    @Override
    protected void setItemClickEventsAsync() {
        int[] slots = getSlots();

        // Set Explore Item Click Event
        getMenu().getSlot(slots[0]).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.closeInventory();
            new ExploreMenu(clickPlayer, true);
        });

        // Set Build Item Click Event
        if(config.getBoolean(ConfigPaths.BUILD_ITEM_ENABLED)) {
            getMenu().getSlot(slots[1]).setClickHandler((clickPlayer, clickInformation) -> {
                clickPlayer.closeInventory();
                String action = config.getString(ConfigPaths.BUILD_ITEM_ACTION);

                // If no command or message is set, teleport player to the plot system server
                if(action == null || action.equals("/command") || action.equals("message")) {
                    clickPlayer.sendMessage(ChatHelper.standard("Teleporting you to the plot system server..."));
                    Utils.sendPlayerToServer(clickPlayer, NetworkModule.GLOBAL_PLOT_SYSTEM_SERVER);
                    return;
                }

                performClickAction(clickPlayer, action.replace("&", "§"));
            });
        }

        // Set Tutorials Item Click Event
        if(config.getBoolean(ConfigPaths.TUTORIALS_ITEM_ENABLED)) {
            getMenu().getSlot(slots[2]).setClickHandler((clickPlayer, clickInformation) -> {
                clickPlayer.closeInventory();
                String action = config.getString(ConfigPaths.TUTORIALS_ITEM_ACTION);

                // If no command or message is set, open the tutorial menu
                if(action == null || action.equals("/command") || action.equals("message")) {
                    new TutorialsMenu(clickPlayer);
                    return;
                }

                performClickAction(clickPlayer, action.replace("&", "§"));
            });
        }
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 7).setName(" ").build())
                .pattern("111111111")
                .pattern("110101011")
                .pattern("111111111")
                .build();
    }

    /** Returns the slots for the Build, Explore and Tutorials items depending on which items are enabled in the config
     *
     * @return int[] - Slots of the Explore [0], Build [1] and Tutorials [2] items
     */
    private int[] getSlots() {
        int[] slots = new int[3];

        boolean buildEnabled = config.getBoolean(ConfigPaths.BUILD_ITEM_ENABLED);
        boolean tutorialsEnabled = config.getBoolean(ConfigPaths.TUTORIALS_ITEM_ENABLED);

        int exploreSlot = 11;
        int buildSlot = 13;
        int tutorialsSlot = 15;

        int enabledItemCount = (buildEnabled ? 1 : 0) + 1 + (tutorialsEnabled ? 1 : 0);

        // Depending on how many items are enabled, set the slots to the correct positions
        if (enabledItemCount == 2) {
            if (buildEnabled)
                buildSlot = 15;
            else
                buildSlot = 11;
        }

        slots[0] = exploreSlot;
        slots[1] = buildSlot;
        slots[2] = tutorialsSlot;

        return slots;
    }

    private void performClickAction(Player p, String action) {
        // Check if an action is set in the config
        if(!action.equals("/command") &&! action.equals("message"))
            p.chat(action);
        else
            p.sendMessage(ChatHelper.error("No action is set for the %s in the config yet! Please contact an %s.", "build item", "admin"));
    }
}
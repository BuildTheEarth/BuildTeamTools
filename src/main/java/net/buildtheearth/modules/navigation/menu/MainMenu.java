package net.buildtheearth.modules.navigation.menu;

import com.alpsbte.alpslib.utils.ChatHelper;
import com.alpsbte.alpslib.utils.item.Item;
import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.navigation.NavUtils;
import net.buildtheearth.modules.navigation.components.warps.WarpsComponent;
import net.buildtheearth.modules.network.NetworkModule;
import net.buildtheearth.utils.MenuItems;
import net.buildtheearth.utils.io.ConfigPaths;
import net.buildtheearth.utils.io.ConfigUtil;
import net.buildtheearth.utils.menus.AbstractMenu;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.commons.lang3.BooleanUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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

    private static final String INVENTORY_NAME = "BuildTheEarth Navigator";
    private static FileConfiguration config;

    public MainMenu(Player menuPlayer) {
        super(3, INVENTORY_NAME, menuPlayer);
    }

    @Override
    protected void setPreviewItems() {
        config = BuildTeamTools.getInstance().getConfig(ConfigUtil.NAVIGATION);
        @NotNull Deque<@NotNull Integer> slots = getSlots();

        // Fill the blank slots with glass panes
        for (int i = 10; i <= 16; i++) {
            getMenu().getSlot(i).setItem(MenuItems.ITEM_BACKGROUND);
        }

        // Set Build Item
        if (config.getBoolean(ConfigPaths.Navigation.BUILD_ITEM_ENABLED)) {
            ArrayList<String> buildLore = new ArrayList<>(Collections.singletonList(ChatHelper.getColorizedString(NamedTextColor.GRAY, "Click to build for the project!", false)));
            getMenu().getSlot(Objects.requireNonNull(slots.pollFirst())).setItem(Item.edit(Objects.requireNonNull(XMaterial.DIAMOND_PICKAXE.parseItem()), 1, ChatHelper.getColorizedString(NamedTextColor.GREEN, "Terra Server", true), buildLore));
        }

        // Set Plotsystem Item Click Event
        if (config.getBoolean(ConfigPaths.Navigation.PLOTSYSTEM_ITEM_ENABLED)) {
            ArrayList<String> tutorialsLore = new ArrayList<>(Collections.singletonList(ChatHelper.getColorizedString(NamedTextColor.GRAY, "Click to start your journey!", false)));
            getMenu().getSlot(Objects.requireNonNull(slots.pollFirst())).setItem(Item.edit(Objects.requireNonNull(XMaterial.KNOWLEDGE_BOOK.parseItem()), 1, ChatHelper.getColorizedString(NamedTextColor.AQUA, "Plot System", true), tutorialsLore));
        }

        if (config.getBoolean(ConfigPaths.Navigation.EXPLORE_ITEM_ENABLED)) {
            // Set Explore Item
            List<String> exploreLore = List.of(ChatHelper.getColorizedString(NamedTextColor.GRAY, "Click to explore the warps!", false), ChatHelper.getColorizedString(NamedTextColor.LIGHT_PURPLE, "Right click to explore other build teams.", false));
            getMenu().getSlot(Objects.requireNonNull(slots.pollFirst())).setItem(Item.edit(Objects.requireNonNull(XMaterial.SPRUCE_BOAT.parseItem()), 1, ChatHelper.getColorizedString(NamedTextColor.YELLOW, "Explore", true), exploreLore));
        }

        // Set Tutorials Item
        if (config.getBoolean(ConfigPaths.Navigation.TUTORIALS_ITEM_ENABLED)) {
            ArrayList<String> tutorialsLore = new ArrayList<>(Collections.singletonList(ChatHelper.getColorizedString(NamedTextColor.GRAY, "Click to do some tutorials!", false)));
            getMenu().getSlot(Objects.requireNonNull(slots.pollFirst())).setItem(Item.edit(Objects.requireNonNull(XMaterial.KNOWLEDGE_BOOK.parseItem()), 1, ChatHelper.getColorizedString(NamedTextColor.AQUA, "Tutorials", true), tutorialsLore));
        }

        super.setPreviewItems();
    }

    @Override
    protected void setMenuItemsAsync() { /* No async Items set */}

    @Override
    protected void setItemClickEventsAsync() {
        Deque<Integer> slots = getSlots();

        // Set Build Item Click Event
        if (config.getBoolean(ConfigPaths.Navigation.BUILD_ITEM_ENABLED)) {
            getMenu().getSlot(Objects.requireNonNull(slots.pollFirst()))
                    .setClickHandler((clickPlayer, clickInformation) -> {
                        clickPlayer.closeInventory();
                        String action = config.getString(ConfigPaths.Navigation.BUILD_ITEM_ACTION);
                        performClickAction(clickPlayer, Objects.requireNonNull(action).replace("&", "§"), "build");
                    });
        }

        // Set Plotsystem Item Click Event
        if (config.getBoolean(ConfigPaths.Navigation.PLOTSYSTEM_ITEM_ENABLED)) {
            getMenu().getSlot(Objects.requireNonNull(slots.pollFirst()))
                    .setClickHandler((clickPlayer, clickInformation) -> {
                        clickPlayer.closeInventory();
                        String action = config.getString(ConfigPaths.Navigation.PLOTSYSTEM_ITEM_ACTION);
                        performClickAction(clickPlayer, Objects.requireNonNull(action).replace("&", "§"), "plotsystem");
                    });
        }

        if (config.getBoolean(ConfigPaths.Navigation.EXPLORE_ITEM_ENABLED)) {
            // Set Explore Item Click Event
            getMenu().getSlot(Objects.requireNonNull(slots.pollFirst())).setClickHandler((clickPlayer, clickInformation) -> {
                clickPlayer.closeInventory();
                if (clickInformation.getClickType().isRightClick()) {
                    new ExploreMenu(clickPlayer, true);
                } else {
                    WarpsComponent.openWarpMenu(clickPlayer, NetworkModule.getInstance().getBuildTeam(), this);
                }
            });
        }

        // Set Tutorials Item Click Event
        if (config.getBoolean(ConfigPaths.Navigation.TUTORIALS_ITEM_ENABLED)) {
            getMenu().getSlot(Objects.requireNonNull(slots.pollFirst())).setClickHandler((clickPlayer, clickInformation) -> {
                clickPlayer.closeInventory();
                String action = config.getString(ConfigPaths.Navigation.TUTORIALS_ITEM_ACTION);

                // If no command or message is set, open the tutorial menu
                if (action == null || action.equals("/command") || action.equals("message")) {
                    new TutorialsMenu(clickPlayer);
                    return;
                }

                performClickAction(clickPlayer, action.replace("&", "§"), "tutorial");
            });
        }
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(MenuItems.ITEM_BACKGROUND)
                .pattern("111111111")
                .pattern("100000001")
                .pattern("111111111")
                .build();
    }

    /**
     * Returns the slots for the Build, Explore and Tutorials items depending on which items are enabled in the config
     *
     * @return int[] - Slots of the enabled items
     */
    private @NotNull Deque<@NotNull Integer> getSlots() {
        Deque<Integer> slots = new ArrayDeque<>();
        boolean buildEnabled = config.getBoolean(ConfigPaths.Navigation.BUILD_ITEM_ENABLED);
        boolean tutorialsEnabled = config.getBoolean(ConfigPaths.Navigation.TUTORIALS_ITEM_ENABLED);
        boolean plotsystemEnabled = config.getBoolean(ConfigPaths.Navigation.PLOTSYSTEM_ITEM_ENABLED);
        boolean exploreEnabled = config.getBoolean(ConfigPaths.Navigation.EXPLORE_ITEM_ENABLED);

        int enabledItemCount = BooleanUtils.toInteger(buildEnabled) + BooleanUtils.toInteger(tutorialsEnabled) +
                BooleanUtils.toInteger(plotsystemEnabled) + BooleanUtils.toInteger(exploreEnabled);

        // Depending on how many items are enabled, set the slots to the correct positions
        switch (enabledItemCount) {
            case 1:
                slots.add(13);
                break;
            case 2:
                slots.add(11);
                slots.add(15);
                break;
            case 3:
                slots.add(11);
                slots.add(13);
                slots.add(15);
                break;
            case 4:
                slots.add(10);
                slots.add(12);
                slots.add(14);
                slots.add(16);
                break;
            default:
                throw new IllegalStateException("Unexpected enabled items value: " + enabledItemCount);
        }

        return slots;
    }

    private void performClickAction(Player p, @NotNull String action, @NotNull String type) {
        // Check if an action is set in the config
        if (action.startsWith("transfer:")) {
            NavUtils.transferPlayer(p, action.substring(9));
        } else if (action.startsWith("switch:")) {
            NavUtils.sendPlayerToConnectedServer(p, action.substring(7));
        } else if (!action.equals("/command") && !action.equals("message")) {
            p.chat(action);
        } else {
            p.sendMessage(ChatHelper.getErrorString("No action is set for the %s in the config yet! Please contact an %s.", type + " item", "admin"));
        }
    }
}
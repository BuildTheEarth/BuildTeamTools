package net.buildtheearth.buildteamtools.modules.navigation.menu;

import com.alpsbte.alpslib.utils.ChatHelper;
import com.alpsbte.alpslib.utils.item.Item;
import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.buildteamtools.modules.navigation.NavUtils;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.WarpsComponent;
import net.buildtheearth.buildteamtools.modules.network.NetworkModule;
import net.buildtheearth.buildteamtools.modules.network.model.BuildTeam;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import net.buildtheearth.buildteamtools.utils.MenuItems;
import net.buildtheearth.buildteamtools.utils.io.NavigationConfig;
import net.buildtheearth.buildteamtools.modules.navigation.NavigationModule;
import net.buildtheearth.buildteamtools.utils.menus.AbstractMenu;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.commons.lang3.BooleanUtils;
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
    public MainMenu(Player menuPlayer) {
        super(3, INVENTORY_NAME, menuPlayer);
    }

    @Override
    protected void setPreviewItems() {
        @NotNull Deque<@NotNull Integer> slots = getSlots();

        // Fill the blank slots with glass panes
        for (int i = 10; i <= 16; i++) {
            getMenu().getSlot(i).setItem(MenuItems.ITEM_BACKGROUND);
        }

        // Set Build Item
        if (config().mainMenuItems().buildItem().enabled()) {
            ArrayList<String> buildLore = new ArrayList<>(Collections.singletonList(ChatHelper.getColorizedString(NamedTextColor.GRAY, "Click to build for the project!", false)));
            getMenu().getSlot(Objects.requireNonNull(slots.pollFirst())).setItem(Item.edit(Objects.requireNonNull(XMaterial.DIAMOND_PICKAXE.parseItem()), 1, ChatHelper.getColorizedString(NamedTextColor.GREEN, "Terra Server", true), buildLore));
        }

        // Set Plotsystem Item Click Event
        if (config().mainMenuItems().plotsystemItem().enabled()) {
            ArrayList<String> tutorialsLore = new ArrayList<>(Collections.singletonList(ChatHelper.getColorizedString(NamedTextColor.GRAY, "Click to start your journey!", false)));
            getMenu().getSlot(Objects.requireNonNull(slots.pollFirst())).setItem(Item.edit(Objects.requireNonNull(XMaterial.KNOWLEDGE_BOOK.parseItem()), 1, ChatHelper.getColorizedString(NamedTextColor.AQUA, "Plot System", true), tutorialsLore));
        }

        if (config().mainMenuItems().exploreItem().enabled()) {
            // Set Explore Item
            List<String> exploreLore = List.of(getMenuPlayer().hasPermission(Permissions.WARP_USE) ? ChatHelper.getColorizedString(NamedTextColor.GRAY, "Click to explore the warps!", false) : "", ChatHelper.getColorizedString(NamedTextColor.LIGHT_PURPLE, "Right click to explore other build teams.", false));
            getMenu().getSlot(Objects.requireNonNull(slots.pollFirst())).setItem(Item.edit(Objects.requireNonNull(XMaterial.SPRUCE_BOAT.parseItem()), 1, ChatHelper.getColorizedString(NamedTextColor.YELLOW, "Explore", true), exploreLore));
        }

        // Set Tutorials Item
        if (config().mainMenuItems().tutorialItem().enabled()) {
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
        if (config().mainMenuItems().buildItem().enabled()) {
            getMenu().getSlot(Objects.requireNonNull(slots.pollFirst()))
                    .setClickHandler((clickPlayer, clickInformation) -> {
                        clickPlayer.closeInventory();
                        performClickAction(clickPlayer, config().mainMenuItems().buildItem().action().replace("&", "§"), "build");
                    });
        }

        // Set Plotsystem Item Click Event
        if (config().mainMenuItems().plotsystemItem().enabled()) {
            getMenu().getSlot(Objects.requireNonNull(slots.pollFirst()))
                    .setClickHandler((clickPlayer, clickInformation) -> {
                        clickPlayer.closeInventory();
                        performClickAction(clickPlayer, config().mainMenuItems().plotsystemItem().action().replace("&", "§"), "plotsystem");
                    });
        }

        if (config().mainMenuItems().exploreItem().enabled()) {
            // Set Explore Item Click Event
            getMenu().getSlot(Objects.requireNonNull(slots.pollFirst())).setClickHandler((clickPlayer, clickInformation) -> {
                clickPlayer.closeInventory();
                BuildTeam buildTeam = NetworkModule.getInstance().getBuildTeam();
                if (!clickInformation.getClickType().isRightClick() && buildTeam != null && buildTeam.getWarpGroups() != null
                        && !buildTeam.getWarpGroups().isEmpty() && clickPlayer.hasPermission(Permissions.WARP_USE)) {
                    WarpsComponent.openWarpMenu(clickPlayer, buildTeam, this);
                } else {
                    new ExploreMenu(clickPlayer, true);
                }
            });
        }

        // Set Tutorials Item Click Event
        if (config().mainMenuItems().tutorialItem().enabled()) {
            getMenu().getSlot(Objects.requireNonNull(slots.pollFirst())).setClickHandler((clickPlayer, clickInformation) -> {
                clickPlayer.closeInventory();
                String action = config().mainMenuItems().tutorialItem().action();

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
                .pattern(BinaryMask.FULL_PATTERN)
                .pattern("100000001")
                .pattern(BinaryMask.FULL_PATTERN)
                .build();
    }

    /**
     * Returns the slots for the Build, Explore and Tutorials items depending on which items are enabled in the config
     *
     * @return int[] - Slots of the enabled items
     */
    private @NotNull Deque<@NotNull Integer> getSlots() {
        Deque<Integer> slots = new ArrayDeque<>();
        boolean buildEnabled = config().mainMenuItems().buildItem().enabled();
        boolean tutorialsEnabled = config().mainMenuItems().tutorialItem().enabled();
        boolean plotsystemEnabled = config().mainMenuItems().plotsystemItem().enabled();
        boolean exploreEnabled = config().mainMenuItems().exploreItem().enabled();

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

    private @NotNull NavigationConfig config() {
        NavigationModule navigationModule = NavigationModule.getInstance();
        if (navigationModule == null) {
            throw new IllegalStateException("Navigation module is not initialized");
        }
        return navigationModule.getConfig();
    }

    private void performClickAction(Player p, String action, String type) {
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

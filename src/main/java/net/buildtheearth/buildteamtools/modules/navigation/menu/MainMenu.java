package net.buildtheearth.buildteamtools.modules.navigation.menu;

import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import net.buildtheearth.buildteamtools.i18n.Messages;
import net.buildtheearth.buildteamtools.modules.navigation.NavUtils;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.WarpsComponent;
import net.buildtheearth.buildteamtools.modules.network.NetworkModule;
import net.buildtheearth.buildteamtools.modules.network.model.BuildTeam;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import net.buildtheearth.buildteamtools.utils.ItemComponents;
import net.buildtheearth.buildteamtools.utils.MenuItems;
import net.buildtheearth.buildteamtools.utils.io.NavigationConfig;
import net.buildtheearth.buildteamtools.modules.navigation.NavigationModule;
import net.buildtheearth.buildteamtools.utils.menus.AbstractMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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

    public MainMenu(Player menuPlayer) {
        this(menuPlayer, true);
    }

    public MainMenu(Player menuPlayer, boolean autoLoad) {
        super(3, Messages.component(MessageKeys.Navigation.MainMenu.TITLE), menuPlayer, autoLoad);
    }

    @Override
    protected void setPreviewItems() {
        @NotNull Deque<@NotNull Integer> slots = getSlots();
        Player player = getMenuPlayer();

        // Fill the blank slots with glass panes
        for (int i = 10; i <= 16; i++) {
            getMenu().getSlot(i).setItem(MenuItems.ITEM_BACKGROUND);
        }

        // Set Build Item
        if (config().mainMenuItems().buildItem().enabled()) {
            getMenu().getSlot(Objects.requireNonNull(slots.pollFirst())).setItem(ItemComponents.of(
                    Objects.requireNonNull(XMaterial.DIAMOND_PICKAXE.get()),
                    1,
                    Messages.render(player, MessageKeys.Navigation.MainMenu.BUILD_NAME),
                    Messages.loreComponents(player, MessageKeys.Navigation.MainMenu.BUILD_LORE)));
        }

        // Set Plotsystem Item
        if (config().mainMenuItems().plotsystemItem().enabled()) {
            getMenu().getSlot(Objects.requireNonNull(slots.pollFirst())).setItem(ItemComponents.of(
                    Objects.requireNonNull(XMaterial.KNOWLEDGE_BOOK.get()),
                    1,
                    Messages.render(player, MessageKeys.Navigation.MainMenu.PLOTSYSTEM_NAME),
                    Messages.loreComponents(player, MessageKeys.Navigation.MainMenu.PLOTSYSTEM_LORE)));
        }

        if (config().mainMenuItems().exploreItem().enabled()) {
            List<Component> exploreLore = new ArrayList<>();
            if (player.hasPermission(Permissions.WARP_USE)) {
                exploreLore.addAll(Messages.loreComponents(player, MessageKeys.Navigation.MainMenu.EXPLORE_LORE_WARPS));
            }
            exploreLore.addAll(Messages.loreComponents(player, MessageKeys.Navigation.MainMenu.EXPLORE_LORE_TEAMS));
            getMenu().getSlot(Objects.requireNonNull(slots.pollFirst())).setItem(ItemComponents.of(
                    Objects.requireNonNull(XMaterial.SPRUCE_BOAT.get()),
                    1,
                    Messages.render(player, MessageKeys.Navigation.MainMenu.EXPLORE_NAME),
                    exploreLore));
        }

        // Set Tutorials Item
        if (config().mainMenuItems().tutorialItem().enabled()) {
            getMenu().getSlot(Objects.requireNonNull(slots.pollFirst())).setItem(ItemComponents.of(
                    Objects.requireNonNull(XMaterial.KNOWLEDGE_BOOK.get()),
                    1,
                    Messages.render(player, MessageKeys.Navigation.MainMenu.TUTORIALS_NAME),
                    Messages.loreComponents(player, MessageKeys.Navigation.MainMenu.TUTORIALS_LORE)));
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
                        openBuild(clickPlayer);
                    });
        }

        // Set Plotsystem Item Click Event
        if (config().mainMenuItems().plotsystemItem().enabled()) {
            getMenu().getSlot(Objects.requireNonNull(slots.pollFirst()))
                    .setClickHandler((clickPlayer, clickInformation) -> {
                        clickPlayer.closeInventory();
                        openPlotSystem(clickPlayer);
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
                openTutorials(clickPlayer);
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
     * Returns the slots for enabled main menu items, or an empty deque when all items are disabled.
     *
     * @return Slots of the enabled items
     */
    private @NotNull Deque<@NotNull Integer> getSlots() {
        return getSlots(config().mainMenuItems());
    }

    static @NotNull Deque<@NotNull Integer> getSlots(NavigationConfig.MainMenuItems items) {
        Deque<Integer> slots = new ArrayDeque<>();
        boolean buildEnabled = items.buildItem().enabled();
        boolean tutorialsEnabled = items.tutorialItem().enabled();
        boolean plotsystemEnabled = items.plotsystemItem().enabled();
        boolean exploreEnabled = items.exploreItem().enabled();

        int enabledItemCount = BooleanUtils.toInteger(buildEnabled) + BooleanUtils.toInteger(tutorialsEnabled) +
                BooleanUtils.toInteger(plotsystemEnabled) + BooleanUtils.toInteger(exploreEnabled);

        // Depending on how many items are enabled, set the slots to the correct positions
        switch (enabledItemCount) {
            case 0:
                break;
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

    private static @NotNull NavigationConfig config() {
        NavigationModule navigationModule = NavigationModule.getInstance();
        if (navigationModule == null) {
            throw new IllegalStateException("Navigation module is not initialized");
        }
        return navigationModule.getConfig();
    }

    public static void openBuild(Player player) {
        openConfiguredItem(player, config().mainMenuItems().buildItem(), "build");
    }

    public static void openPlotSystem(Player player) {
        openConfiguredItem(player, config().mainMenuItems().plotsystemItem(), "plotsystem");
    }

    public static void openTutorials(Player player) {
        openConfiguredItem(player, config().mainMenuItems().tutorialItem(), "tutorial");
    }

    static void openConfiguredItem(Player player, NavigationConfig.MenuItem item, String type) {
        if (!item.enabled()) {
            player.sendMessage(Messages.component(MessageKeys.Navigation.MainMenu.ITEM_DISABLED, Component.text(type)));
            return;
        }
        String action = item.action();
        if (type.equals("tutorial") && (action == null || action.equals("/command") || action.equals("message"))) {
            new TutorialsMenu(player);
        } else if (action == null) {
            player.sendMessage(Messages.component(
                    MessageKeys.Navigation.MainMenu.NO_ACTION,
                    Component.text(type),
                    Component.text("admin")));
        } else {
            performClickAction(player, action, type);
        }
    }

    private static void performClickAction(Player p, String action, String type) {
        // Check if an action is set in the config
        if (action.startsWith("transfer:")) {
            NavUtils.transferPlayer(p, action.substring(9));
        } else if (action.startsWith("switch:")) {
            NavUtils.sendPlayerToConnectedServer(p, action.substring(7));
        } else if (!action.equals("/command") && !action.equals("message")) {
            if (action.startsWith("/")) {
                p.chat(action);
            } else {
                // Config actions may still use legacy & color codes.
                p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(action));
            }
        } else {
            p.sendMessage(Messages.component(
                    MessageKeys.Navigation.MainMenu.NO_ACTION_GENERIC,
                    Component.text(type + " item"),
                    Component.text("admin")));
        }
    }
}

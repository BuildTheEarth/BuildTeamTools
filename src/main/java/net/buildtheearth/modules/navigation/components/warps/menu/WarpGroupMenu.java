package net.buildtheearth.modules.navigation.components.warps.menu;

import com.alpsbte.alpslib.utils.ChatHelper;
import com.alpsbte.alpslib.utils.item.Item;
import com.google.gson.Gson;
import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.navigation.NavigationModule;
import net.buildtheearth.modules.navigation.components.warps.model.WarpGroup;
import net.buildtheearth.modules.network.NetworkModule;
import net.buildtheearth.modules.network.model.BuildTeam;
import net.buildtheearth.modules.network.model.Permissions;
import net.buildtheearth.utils.CustomHeads;
import net.buildtheearth.utils.ListUtil;
import net.buildtheearth.utils.MenuItems;
import net.buildtheearth.utils.io.ConfigPaths;
import net.buildtheearth.utils.io.ConfigUtil;
import net.buildtheearth.utils.menus.AbstractMenu;
import net.buildtheearth.utils.menus.AbstractPaginatedMenu;
import org.bukkit.entity.Player;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WarpGroupMenu extends AbstractPaginatedMenu {

    public static final int BACK_ITEM_SLOT = 27;

    public static final int ALTERNATE_PLUS_SLOT = 35;

    private final boolean hasBackItem;
    private final BuildTeam buildTeam;
    private int plusSlot = ALTERNATE_PLUS_SLOT;
    private AbstractMenu backMenue;

    /** In this menu the player can select a warp group to view the warps in each warp group.
     *
     * @param menuPlayer The player that is viewing the menu
     * @param buildTeam The build team that the menu is for
     * @param hasBackItem Whether the menu has a back item - only true on overrides or when country selector menu
     */
    public WarpGroupMenu(Player menuPlayer, BuildTeam buildTeam, boolean hasBackItem, boolean autoLoad) {
        super(4, 3, "Warp Menu", menuPlayer, autoLoad);
        this.hasBackItem = hasBackItem;
        this.buildTeam = buildTeam;
    }

    public WarpGroupMenu(Player menuPlayer, BuildTeam buildTeam, boolean hasBackItem, boolean autoLoad, AbstractMenu menu) {
        this(menuPlayer, buildTeam, hasBackItem, autoLoad);
        this.backMenue = menu;
    }

    @Override
    protected void setMenuItemsAsync() {
        if (hasBackItem) {
            ChatHelper.logDebug("Setting back item for warp group menu");
            setBackItem(BACK_ITEM_SLOT, backMenue);
        }
    }

    @Override
    protected void setItemClickEventsAsync() {
        // No items need to be set here
    }

    @Override
    protected void setPaginatedPreviewItems(@NotNull List<?> source) {
        List<WarpGroup> warpGroups = source.stream().map(l -> (WarpGroup) l).toList();

        getMenu().getSlot(ALTERNATE_PLUS_SLOT).setItem(MenuItems.ITEM_BACKGROUND);
        final int nextSlot = recalculateAutoSlots(warpGroups);

        // Create the country items
        for (WarpGroup warpGroup : warpGroups) {
            // Create a create warp group item if the player has permission
            if(isPlusItem(warpGroup)){
                setPlusSlot(warpGroup.getInternalSlot(), nextSlot);
                getMenu().getSlot(plusSlot).setItem(
                        Item.createCustomHeadBase64(
                                CustomHeads.GREEN_PLUS, "§a§lCreate a new Warp Group",
                                ListUtil.createList("§8Click to create a new warp group.")
                        )
                );
                continue;
            }

            getMenu().getSlot(getWarpGroupSlot(warpGroup)).setItem(warpGroup.getMaterialItem());
        }
    }

    @Override
    protected void setPaginatedMenuItemsAsync(List<?> source) {
        // All Items set above in setPaginatedPreviewItems
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
            .item(MenuItems.ITEM_BACKGROUND)
            .pattern("000000000")
            .pattern("000000000")
            .pattern("000000000")
            .pattern("111111110")
            .build();
    }

    @Override
    protected List<?> getSource() {
        // Get the warp groups in the build team sorted by name
        List<WarpGroup> warpGroups;

        String mode = BuildTeamTools.getInstance().getConfig(ConfigUtil.NAVIGATION)
                .getString(ConfigPaths.Navigation.WARPS_GROUP_SORTING_MODE, "");

        if (mode.equalsIgnoreCase("name")) {
            warpGroups = buildTeam.getWarpGroups().stream().sorted((warpGroup1, warpGroup2) -> warpGroup1.getName().compareToIgnoreCase(warpGroup2.getName())).collect(Collectors.toList());
        } else warpGroups = new ArrayList<>(buildTeam.getWarpGroups());

        // If the warp group "Other" has no warps, remove it from the list
        WarpGroup otherWarpGroup = warpGroups.stream().filter(warpGroup -> warpGroup.getName().equalsIgnoreCase("Other")).findFirst().orElse(null);
        if (otherWarpGroup != null && otherWarpGroup.getWarps().isEmpty())
            warpGroups.remove(otherWarpGroup);

        // Add a create warp group item if the player has permission
        if (getMenuPlayer().hasPermission(Permissions.WARP_GROUP_CREATE)
                && NetworkModule.getInstance().getBuildTeam().equals(buildTeam))
            warpGroups.add(new WarpGroup(null, "%create-warp-group%", null, -1, null));

        return warpGroups;
    }

    @Override
    protected void setPaginatedItemClickEventsAsync(@NotNull List<?> source) {
        List<WarpGroup> warpGroups = source.stream().map(l -> (WarpGroup) l).toList();

        for (WarpGroup warpGroup : warpGroups) {
            setClickEventForSlot(warpGroup);
        }
    }

    protected void setClickEventForSlot(@NotNull WarpGroup warpGroup) {
        final int _slot = isPlusItem(warpGroup) ? plusSlot : getWarpGroupSlot(warpGroup);

        getMenu().getSlot(_slot).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.closeInventory();

            // Create a click action for the "Create Warp" item if the player has permission
            if(isPlusItem(warpGroup)){
                NavigationModule.getInstance().getWarpsComponent().createWarpGroup(clickPlayer);
                return;
            }

            if(clickInformation.getClickType().isRightClick() && clickPlayer.hasPermission(Permissions.WARP_GROUP_EDIT))
                new WarpGroupEditMenu(clickPlayer, warpGroup, true, true);
            else
                leftClickAction(clickPlayer, warpGroup);
        });
    }

    protected void leftClickAction(Player clickPlayer, @NotNull WarpGroup warpGroup) {
        new WarpMenu(clickPlayer, warpGroup, true, true);
    }

    protected boolean isPlusItem(@NotNull WarpGroup warpGroup){
        return warpGroup.getName().equals("%create-warp-group%") && getMenuPlayer().hasPermission(Permissions.WARP_GROUP_CREATE);
    }

    protected int getWarpGroupSlot(@NotNull WarpGroup g) {
        int s = g.getSlot();
        if (s >= 0 && s <= 26) return s;
        int a = g.getInternalSlot();
        return a >= 0 && a <= 26 ? a : -1;
    }

    protected void setPlusSlot(int internalSlot, int freeSlot){
        if(internalSlot >= 0) {
            plusSlot = internalSlot;
            return;
        }

        if(freeSlot < 0) return;
        plusSlot = freeSlot;
    }

    /**
     * Recalculates automatic slot assignments for the given warp groups.
     * <p>
     * Preserves valid explicit slots (0..26). For groups with an invalid slot,
     * assigns the first available slot in ascending order; if none remain, sets -1.
     * Any group with a valid explicit slot has its internal auto slot cleared (-1).
     *
     * @param warpGroups groups to update
     * @return The next free slot or -1 if it's outside the range
     */
    private static int recalculateAutoSlots(@NotNull List<WarpGroup> warpGroups) {
        final int MAX = 27;

        // 1) Calculate all free slots
        ArrayDeque<Integer> free = getFreeSlots(warpGroups, MAX);

        // 2) Assign auto slots only where needed
        for (WarpGroup g : warpGroups) {
            int s = g.getSlot();
            if (s >= 0 && s < MAX) {
                g.setInternalSlot(-1); // explicit slot → clear auto
            } else {
                Integer next = free.pollFirst(); // next free, or null if none
                g.setInternalSlot(next != null ? next : -1);
            }
        }

        // 3) Return next free slot - mainly used for plus slot
        Integer next = free.pollFirst(); // next free, or null if none

        if (BuildTeamTools.getInstance().isDebug() && BuildTeamTools.getInstance().getComponentLogger().isInfoEnabled()) {
            BuildTeamTools.getInstance().getComponentLogger().info("Free slot: {}.", free);
            BuildTeamTools.getInstance().getComponentLogger().info("Auto slots: {}.",
                    new Gson().toJson(warpGroups.stream().map(warpGroup -> new WarpGropSlotDebug(
                            warpGroup.getName(),
                            warpGroup.getSlot(),
                            warpGroup.getInternalSlot())).toList()));
        }

        return next != null ? next : -1;
    }

    private static @NotNull ArrayDeque<Integer> getFreeSlots(@NotNull List<WarpGroup> warpGroups, int max) {
        // 1) Mark occupied (explicit) slots
        boolean[] taken = new boolean[max];
        for (WarpGroup g : warpGroups) {
            int s = g.getSlot();
            if (s >= 0 && s < max) taken[s] = true;
        }

        // 2) Build the free-slot queue (ascending)
        ArrayDeque<Integer> free = new ArrayDeque<>();
        for (int i = 0; i < max; i++) {
            if (!taken[i]) free.add(i);
        }

        return free;
    }

    private record WarpGropSlotDebug(String name, int slot, int internalSlot) {}
}

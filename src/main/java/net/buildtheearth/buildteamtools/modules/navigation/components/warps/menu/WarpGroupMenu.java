package net.buildtheearth.buildteamtools.modules.navigation.components.warps.menu;

import com.google.gson.Gson;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.navigation.NavigationModule;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.WarpGroup;
import net.buildtheearth.buildteamtools.modules.network.NetworkModule;
import net.buildtheearth.buildteamtools.modules.network.model.BuildTeam;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import net.buildtheearth.buildteamtools.utils.ListUtil;
import net.buildtheearth.buildteamtools.utils.MenuItems;
import net.buildtheearth.buildteamtools.utils.heads.HeadFactory;
import net.buildtheearth.buildteamtools.utils.heads.HeadTexture;
import net.buildtheearth.buildteamtools.utils.io.ConfigPaths;
import net.buildtheearth.buildteamtools.utils.io.ConfigUtil;
import net.buildtheearth.buildteamtools.utils.menus.AbstractMenu;
import net.buildtheearth.buildteamtools.utils.menus.AbstractPaginatedMenu;
import org.bukkit.entity.Player;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class WarpGroupMenu extends AbstractPaginatedMenu {

    // Bottom bar (row 4, slots 27-35):
    // 27=back  28=bg  29=prev  30=bg  31=bg  32=bg  33=next  34=bg  35=create(admin)
    // setSwitchPageItems(slot) places prev at slot-1 and next at slot+1 — so center=31 gives prev=30, next=32.
    // But we want prev=29 and next=33 to leave room for the plus at 35.
    // We call setSwitchPageItems(31) which uses 30 and 32 — that's fine, 35 is untouched.

    public static final int BACK_ITEM_SLOT = 27;
    public static final int SWITCH_PAGE_ITEM_SLOT = 31;
    public static final int PLUS_SLOT = 35;
    private static final int ITEMS_PER_PAGE = 27;

    private final boolean hasBackItem;
    private final BuildTeam buildTeam;
    private AbstractMenu backMenu;
    private final boolean showPlusItem;

    public WarpGroupMenu(Player menuPlayer, BuildTeam buildTeam, boolean hasBackItem, boolean autoLoad) {
        super(4, 3, "Warp Menu", menuPlayer, autoLoad);
        this.hasBackItem = hasBackItem;
        this.buildTeam = buildTeam;
        BuildTeam currentTeam = NetworkModule.getInstance().getBuildTeam();
        this.showPlusItem = getMenuPlayer().hasPermission(Permissions.WARP_GROUP_CREATE)
                && currentTeam != null && currentTeam.equals(buildTeam);
    }

    public WarpGroupMenu(Player menuPlayer, BuildTeam buildTeam, boolean hasBackItem, boolean autoLoad, AbstractMenu menu) {
        this(menuPlayer, buildTeam, hasBackItem, autoLoad);
        this.backMenu = menu;
    }

    @Override
    protected void setPreviewItems() {
        if (hasBackItem)
            setBackItem(BACK_ITEM_SLOT, backMenu);
        else
            getMenu().getSlot(BACK_ITEM_SLOT).setItem(MenuItems.ITEM_BACKGROUND);

        for (int i = 28; i <= 35; i++)
            getMenu().getSlot(i).setItem(MenuItems.ITEM_BACKGROUND);

        if (isPaginated())
            setSwitchPageItems(SWITCH_PAGE_ITEM_SLOT);

        super.setPreviewItems();
    }

    @Override
    protected void setMenuItemsAsync() {
    }

    @Override
    protected void setItemClickEventsAsync() {
        if (isPaginated())
            setSwitchPageItemClickEvents(SWITCH_PAGE_ITEM_SLOT);
    }

    @Override
    protected void setPaginatedPreviewItems(@NotNull List<?> source) {
        List<WarpGroup> warpGroups = source.stream().map(l -> (WarpGroup) l).toList();

        for (int i = 0; i < ITEMS_PER_PAGE; i++)
            getMenu().getSlot(i).setItem(MenuItems.ITEM_BACKGROUND);

        getMenu().getSlot(PLUS_SLOT).setItem(MenuItems.ITEM_BACKGROUND);

        if (isPaginated()) {
            int slot = 0;
            for (WarpGroup warpGroup : warpGroups) {
                getMenu().getSlot(slot).setItem(warpGroup.getMaterialItem());
                slot++;
            }
        } else {
            List<WarpGroup> all = getSource().stream().map(l -> (WarpGroup) l).toList();
            recalculateAutoSlots(all);
            for (WarpGroup warpGroup : warpGroups) {
                int slot = getWarpGroupSlot(warpGroup);
                if (slot >= 0) getMenu().getSlot(slot).setItem(warpGroup.getMaterialItem());
            }
        }

        if (showPlusItem && !hasNextPage()) {
            getMenu().getSlot(PLUS_SLOT).setItem(
                    HeadFactory.head(HeadTexture.GREEN_PLUS, "§a§lCreate a new Warp Group",
                            ListUtil.createList("§8Click to create a new warp group."))
            );
        }
    }

    @Override
    protected void setPaginatedMenuItemsAsync(List<?> source) {
    }

    @Override
    protected void setPaginatedItemClickEventsAsync(@NotNull List<?> source) {
        List<WarpGroup> warpGroups = source.stream().map(l -> (WarpGroup) l).toList();

        for (int i = 0; i < ITEMS_PER_PAGE; i++)
            getMenu().getSlot(i).setClickHandler(null);
        getMenu().getSlot(PLUS_SLOT).setClickHandler(null);

        if (isPaginated()) {
            int slot = 0;
            for (WarpGroup warpGroup : warpGroups) {
                setClickHandlerForSlot(slot, warpGroup);
                slot++;
            }
        } else {
            for (WarpGroup warpGroup : warpGroups) {
                int slot = getWarpGroupSlot(warpGroup);
                if (slot >= 0) setClickHandlerForSlot(slot, warpGroup);
            }
        }

        if (showPlusItem && !hasNextPage()) {
            getMenu().getSlot(PLUS_SLOT).setClickHandler((clickPlayer, clickInformation) ->
                    NavigationModule.getInstance().getWarpsComponent().createWarpGroup(clickPlayer));
        }
    }

    private void setClickHandlerForSlot(int slot, @NotNull WarpGroup warpGroup) {
        getMenu().getSlot(slot).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.closeInventory();
            if (clickInformation.getClickType().isRightClick() && clickPlayer.hasPermission(Permissions.WARP_GROUP_EDIT))
                new WarpGroupEditMenu(clickPlayer, warpGroup, true, true);
            else
                leftClickAction(clickPlayer, warpGroup);
        });
    }

    protected void leftClickAction(Player clickPlayer, @NotNull WarpGroup warpGroup) {
        new WarpMenu(clickPlayer, warpGroup, true, true);
    }

    protected int getWarpGroupSlot(@NotNull WarpGroup g) {
        int s = g.getSlot();
        if (s >= 0 && s < ITEMS_PER_PAGE) return s;
        int a = g.getInternalSlot();
        return (a >= 0 && a < ITEMS_PER_PAGE) ? a : -1;
    }

    private boolean isPaginated() {
        return getSource().size() > ITEMS_PER_PAGE;
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(MenuItems.ITEM_BACKGROUND)
                .pattern(BinaryMask.EMPTY_PATTERN)
                .pattern(BinaryMask.EMPTY_PATTERN)
                .pattern(BinaryMask.EMPTY_PATTERN)
                .pattern("111111111")
                .build();
    }

    @Override
    protected List<?> getSource() {
        String mode = BuildTeamTools.getInstance().getConfig(ConfigUtil.NAVIGATION)
                .getString(ConfigPaths.Navigation.WARPS_GROUP_SORTING_MODE, "");

        List<WarpGroup> warpGroups;
        if (mode.equalsIgnoreCase("name")) {
            warpGroups = buildTeam.getWarpGroups().stream()
                    .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                    .toList();
        } else {
            warpGroups = new ArrayList<>(buildTeam.getWarpGroups());
        }
        return warpGroups;
    }

    private static int recalculateAutoSlots(@NotNull List<WarpGroup> warpGroups) {
        final int MAX = ITEMS_PER_PAGE;
        ArrayDeque<Integer> free = getFreeSlots(warpGroups, MAX);

        for (WarpGroup g : warpGroups) {
            int s = g.getSlot();
            if (s >= 0 && s < MAX) {
                g.setInternalSlot(-1);
            } else {
                Integer next = free.pollFirst();
                g.setInternalSlot(next != null ? next : -1);
            }
        }

        if (BuildTeamTools.getInstance().isDebug()) {
            BuildTeamTools.getInstance().getComponentLogger().info("Auto slots: {}.",
                    new Gson().toJson(warpGroups.stream().map(wg ->
                            new WarpGroupSlotDebug(wg.getName(), wg.getSlot(), wg.getInternalSlot())).toList()));
        }

        Integer next = free.pollFirst();
        return next != null ? next : -1;
    }

    private static @NotNull ArrayDeque<Integer> getFreeSlots(@NotNull List<WarpGroup> warpGroups, int max) {
        boolean[] taken = new boolean[max];
        for (WarpGroup g : warpGroups) {
            int s = g.getSlot();
            if (s >= 0 && s < max) taken[s] = true;
        }
        ArrayDeque<Integer> free = new ArrayDeque<>();
        for (int i = 0; i < max; i++)
            if (!taken[i]) free.add(i);
        return free;
    }

    private record WarpGroupSlotDebug(String name, int slot, int internalSlot) {
    }
}

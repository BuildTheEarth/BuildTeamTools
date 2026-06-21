package net.buildtheearth.buildteamtools.modules.navigation.components.warps.menu;

import net.buildtheearth.buildteamtools.modules.navigation.NavigationModule;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.WarpsComponent;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.Warp;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.WarpGroup;
import net.buildtheearth.buildteamtools.modules.network.NetworkModule;
import net.buildtheearth.buildteamtools.modules.network.model.BuildTeam;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import net.buildtheearth.buildteamtools.utils.ListUtil;
import net.buildtheearth.buildteamtools.utils.MenuItems;
import net.buildtheearth.buildteamtools.utils.heads.HeadFactory;
import net.buildtheearth.buildteamtools.utils.heads.HeadTexture;
import net.buildtheearth.buildteamtools.utils.menus.AbstractPaginatedMenu;
import net.buildtheearth.model.GeographicalCoordinate;
import org.bukkit.entity.Player;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class WarpMenu extends AbstractPaginatedMenu {

    public static final int BACK_ITEM_SLOT = 27;
    public static final int SWITCH_PAGE_ITEM_SLOT = 34;

    private final boolean hasBackItem;
    private final WarpGroup warpGroup;

    /**
     * In this menu the player can view the warps in a warp group.
     * He can then select a warp to edit it or teleport to the warp's location.
     *
     * @param menuPlayer  The player that is viewing the menu
     * @param warpGroup   The warp group that the menu is for
     * @param hasBackItem Whether the menu has a back item
     */
    public WarpMenu(Player menuPlayer, WarpGroup warpGroup, boolean hasBackItem, boolean autoLoad) {
        super(4, 3, "Warp Menu", menuPlayer, autoLoad);
        this.hasBackItem = hasBackItem;
        this.warpGroup = warpGroup;
    }

    @Override
    protected void setPreviewItems() {
        if (hasBackItem)
            setBackItem(BACK_ITEM_SLOT, new WarpGroupMenu(getMenuPlayer(), warpGroup.getBuildTeam(), false, false));

        List<?> source = getSource();
        if (source.size() > 27)
            setSwitchPageItems(SWITCH_PAGE_ITEM_SLOT);
        else
            for (int i = -1; i < 2; i++)
                getMenu().getSlot(SWITCH_PAGE_ITEM_SLOT + i).setItem(MenuItems.ITEM_BACKGROUND);

        super.setPreviewItems();
    }

    @Override
    protected void setMenuItemsAsync() {
    }

    @Override
    protected void setItemClickEventsAsync() {
        if (getSource().size() > 27)
            setSwitchPageItemClickEvents(SWITCH_PAGE_ITEM_SLOT);
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(MenuItems.ITEM_BACKGROUND)
                .pattern(BinaryMask.EMPTY_PATTERN)
                .pattern(BinaryMask.EMPTY_PATTERN)
                .pattern(BinaryMask.EMPTY_PATTERN)
                .pattern("011111000")
                .build();
    }

    @Override
    protected List<?> getSource() {
        List<Warp> warps = warpGroup.getWarps().stream()
                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                .collect(Collectors.toList());

        BuildTeam currentTeam = NetworkModule.getInstance().getBuildTeam();
        if (getMenuPlayer().hasPermission(Permissions.WARP_CREATE)
                && currentTeam != null && currentTeam.equals(warpGroup.getBuildTeam()))
            warps.add(new Warp(warpGroup, "%create-warp%", null, null, null, null, null, null,
                    new GeographicalCoordinate(0, 0), 0.0, 0.0f, 0.0f, false));

        return warps;
    }

    @Override
    protected void setPaginatedPreviewItems(@NotNull List<?> source) {
        List<Warp> warps = source.stream().map(l -> (Warp) l).toList();

        int slot = 0;
        for (Warp warp : warps) {
            if (isCreateWarpPlaceholder(warp, slot, warps.size())) {
                getMenu().getSlot(slot).setItem(HeadFactory.head(HeadTexture.GREEN_PLUS, "§a§lCreate a new Warp",
                        ListUtil.createList("§8Click to create a new warp.")));
            } else {
                getMenu().getSlot(slot).setItem(warp.getMaterialItem());
            }
            slot++;
        }
    }

    @Override
    protected void setPaginatedMenuItemsAsync(List<?> source) {
    }

    @Override
    protected void setPaginatedItemClickEventsAsync(@NotNull List<?> source) {
        List<Warp> warps = source.stream().map(l -> (Warp) l).toList();

        int slot = 0;
        for (Warp warp : warps) {
            final int currentSlot = slot;
            getMenu().getSlot(currentSlot).setClickHandler((clickPlayer, clickInformation) -> {
                clickPlayer.closeInventory();

                if (isCreateWarpPlaceholder(warp, currentSlot, warps.size())) {
                    WarpsComponent.createWarp(clickPlayer, warpGroup);
                    return;
                }

                if (clickInformation.getClickType().isRightClick()
                        && clickPlayer.hasPermission(Permissions.WARP_EDIT)
                        && warp.getWarpGroup().getBuildTeam().equals(NetworkModule.getInstance().getBuildTeam()))
                    new WarpEditMenu(clickPlayer, warp, true, true);
                else
                    NavigationModule.getInstance().getWarpsComponent().warpPlayer(clickPlayer, warp);
            });
            slot++;
        }
    }

    private boolean isCreateWarpPlaceholder(@NotNull Warp warp, int slot, int totalSize) {
        return warp.getName().equals("%create-warp%")
                && getMenuPlayer().hasPermission(Permissions.WARP_CREATE)
                && slot == totalSize - 1;
    }
}

package net.buildtheearth.modules.navigation.components.warps.menu;

import net.buildtheearth.modules.navigation.NavigationModule;
import net.buildtheearth.modules.network.NetworkModule;
import net.buildtheearth.modules.network.model.Permissions;
import net.buildtheearth.utils.*;
import net.buildtheearth.utils.menus.AbstractPaginatedMenu;
import net.buildtheearth.modules.navigation.components.warps.model.Warp;
import net.buildtheearth.modules.navigation.components.warps.model.WarpGroup;
import org.bukkit.entity.Player;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WarpMenu extends AbstractPaginatedMenu {

    public static final int BACK_ITEM_SLOT = 27;

    private final boolean hasBackItem;
    private final WarpGroup warpGroup;

    /** In this menu the player can view the warps in a warp group.
     * He can then select a warp to edit it or teleport to the warp's location.
     *
     * @param menuPlayer The player that is viewing the menu
     * @param warpGroup The warp group that the menu is for
     * @param hasBackItem Whether the menu has a back item
     */
    public WarpMenu(Player menuPlayer, WarpGroup warpGroup, boolean hasBackItem, boolean autoLoad) {
        super(4, 3, "Warp Menu", menuPlayer, autoLoad);
        this.hasBackItem = hasBackItem;
        this.warpGroup = warpGroup;
    }

    @Override
    protected void setMenuItemsAsync() {
        if(hasBackItem)
            setBackItem(BACK_ITEM_SLOT, new WarpGroupMenu(getMenuPlayer(), warpGroup.getBuildTeam(), false, false));
    }

    @Override
    protected void setItemClickEventsAsync() { /* Not needed */ }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(MenuItems.ITEM_BACKGROUND)
                .pattern("000000000")
                .pattern("000000000")
                .pattern("000000000")
                .pattern("011111111")
                .build();
    }

    @Override
    protected List<?> getSource() {
        // Get the warps in the warp group sorted by name
        List<Warp> warps = warpGroup.getWarps().stream().sorted((warp1, warp2) -> warp1.getName().compareToIgnoreCase(warp2.getName())).collect(Collectors.toList());

        // Add a "create warp" item if the player has permission
        if (getMenuPlayer().hasPermission(Permissions.WARP_CREATE)
                && NetworkModule.getInstance().getBuildTeam().equals(warpGroup.getBuildTeam()))
            warps.add(new Warp(null, "%create-warp%", null, null, null, null, null, null, 0, 0, 0, 0, 0, false));

        return warps;
    }

    @Override
    protected void setPaginatedPreviewItems(@NotNull List<?> source) {
        List<Warp> warps = source.stream().map(l -> (Warp) l).toList();

        // Create the country items
        int slot = 0;

        for (Warp warp : warps) {

            // Create a "create warp" item if the player has permission
            if(warp.getName().equals("%create-warp%") && getMenuPlayer().hasPermission(Permissions.WARP_CREATE) && slot == warps.size() - 1){
                getMenu().getSlot(slot).setItem(Item.createCustomHeadBase64(CustomHeads.GREEN_PLUS, "§a§lCreate a new Warp", ListUtil.createList("§8Click to create a new warp.")));
                slot++;
                continue;
            }

            ArrayList<String> loreLines = ListUtil.createList("", "§eAddress:");
            loreLines.addAll(Arrays.asList(Utils.splitStringByLineLength(warp.getAddress(), 30, ", ")));
            loreLines.addAll(ListUtil.createList("", "§8Left-Click to warp to this location.", "§8Right-Click to edit this warp."));

            getMenu().getSlot(slot).setItem(warp.getMaterialItem());
            slot++;
        }


    }

    @Override
    protected void setPaginatedMenuItemsAsync(List<?> source) {/* Not needed */}

    @Override
    protected void setPaginatedItemClickEventsAsync(@NotNull List<?> source) {
        List<Warp> warps = source.stream().map(l -> (Warp) l).toList();

        int slot = 0;
        for (Warp warp : warps) {
            final int _slot = slot;
            getMenu().getSlot(_slot).setClickHandler((clickPlayer, clickInformation) -> {
                clickPlayer.closeInventory();

                // Create a click action for the "Create Warp" item if the player has permission
                if(warp.getName().equals("%create-warp%") && getMenuPlayer().hasPermission(Permissions.WARP_CREATE) && _slot == warps.size() - 1){
                    NavigationModule.getInstance().getWarpsComponent().createWarp(clickPlayer, warpGroup);
                    return;
                }

                if (clickInformation.getClickType().isRightClick() && clickPlayer.hasPermission(Permissions.WARP_EDIT)
                        && warp.getWarpGroup().getBuildTeam().equals(NetworkModule.getInstance().getBuildTeam()))
                    new WarpEditMenu(clickPlayer, warp, true, true);
                else
                    NavigationModule.getInstance().getWarpsComponent().warpPlayer(clickPlayer, warp);
            });
            slot++;
        }
    }
}

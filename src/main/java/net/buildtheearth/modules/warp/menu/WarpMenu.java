package net.buildtheearth.modules.warp.menu;

import net.buildtheearth.modules.network.model.Permissions;
import net.buildtheearth.modules.utils.*;
import net.buildtheearth.modules.utils.menus.AbstractPaginatedMenu;
import net.buildtheearth.modules.warp.WarpManager;
import net.buildtheearth.modules.warp.model.Warp;
import net.buildtheearth.modules.warp.model.WarpGroup;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WarpMenu extends AbstractPaginatedMenu {

    public final int BACK_ITEM_SLOT = 27;

    private final boolean hasBackItem;
    private final WarpGroup warpGroup;

    /** In this menu the player can view the warps in a warp group.
     * He can then select a warp to edit it or teleport to the warp's location.
     *
     * @param menuPlayer The player that is viewing the menu
     * @param warpGroup The warp group that the menu is for
     * @param hasBackItem Whether the menu has a back item
     */
    public WarpMenu(Player menuPlayer, WarpGroup warpGroup, boolean hasBackItem) {
        super(4, 3, "Warp Menu", menuPlayer);
        this.hasBackItem = hasBackItem;
        this.warpGroup = warpGroup;
    }

    @Override
    protected void setMenuItemsAsync() {
        setBackItem(BACK_ITEM_SLOT, hasBackItem);
    }

    @Override
    protected void setItemClickEventsAsync() {
        if(hasBackItem)
            getMenu().getSlot(BACK_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
                clickPlayer.closeInventory();
                new WarpGroupMenu(clickPlayer, warpGroup.getBuildTeam(), false);
            });
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(Item.create(Material.STAINED_GLASS_PANE, " ", (short) 15, null))
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
        if (getMenuPlayer().hasPermission(Permissions.WARP_CREATE))
            warps.add(new Warp(null, "%create-warp%", null, null, null, null, null, null, 0, 0, 0, 0, 0, false));

        return warps;
    }

    @Override
    protected void setPaginatedPreviewItems(List<?> source) {
        List<Warp> warps = source.stream().map(l -> (Warp) l).collect(Collectors.toList());

        // Create the country items
        int slot = 0;

        for (Warp warp : warps) {

            // Create a "create warp" item if the player has permission
            if(warp.getName().equals("%create-warp%") && getMenuPlayer().hasPermission(Permissions.WARP_CREATE) && slot == warps.size() - 1){
                getMenu().getSlot(slot).setItem(Item.createCustomHeadBase64(MenuItems.GREEN_PLUS, "§a§lCreate a new Warp", ListUtil.createList("§8Click to create a new warp.")));
                slot++;
                continue;
            }

            ArrayList<String> loreLines = ListUtil.createList("", "§eAddress:");
            loreLines.addAll(Arrays.asList(Utils.splitStringByLineLength(warp.getAddress(), 30, ", ")));
            loreLines.addAll(ListUtil.createList("", "§8Left-Click to warp to this location.", "§8Right-Click to edit this warp."));

            ArrayList<String> warpLore = ListUtil.createList(loreLines.toArray(new String[0]));
            getMenu().getSlot(slot).setItem(warp.getMaterialItem());
            slot++;
        }


    }

    @Override
    protected void setPaginatedMenuItemsAsync(List<?> source) {

    }

    @Override
    protected void setPaginatedItemClickEventsAsync(List<?> source) {
        List<Warp> warps = source.stream().map(l -> (Warp) l).collect(Collectors.toList());

        int slot = 0;
        for (Warp warp : warps) {
            final int _slot = slot;
            getMenu().getSlot(_slot).setClickHandler((clickPlayer, clickInformation) -> {
                clickPlayer.closeInventory();

                // Create a click action for the "Create Warp" item if the player has permission
                if(warp.getName().equals("%create-warp%") && getMenuPlayer().hasPermission(Permissions.WARP_CREATE) && _slot == warps.size() - 1){
                    WarpManager.createWarp(clickPlayer);
                    return;
                }

                if(clickInformation.getClickType().isRightClick())
                    new WarpEditMenu(clickPlayer, warp, true);
                else if(clickInformation.getClickType().isLeftClick())
                    WarpManager.warpPlayer(clickPlayer, warp);
            });
            slot++;
        }
    }
}

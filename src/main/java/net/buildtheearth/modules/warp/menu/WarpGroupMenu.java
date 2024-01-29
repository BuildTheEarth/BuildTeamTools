package net.buildtheearth.modules.warp.menu;

import net.buildtheearth.Main;
import net.buildtheearth.modules.navigator.menu.CountrySelectorMenu;
import net.buildtheearth.modules.navigator.menu.StateSelectorMenu;
import net.buildtheearth.modules.network.ProxyManager;
import net.buildtheearth.modules.network.model.BuildTeam;
import net.buildtheearth.modules.network.model.Region;
import net.buildtheearth.modules.utils.*;
import net.buildtheearth.modules.utils.menus.AbstractPaginatedMenu;
import net.buildtheearth.modules.warp.model.WarpGroup;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WarpGroupMenu extends AbstractPaginatedMenu {

    public final int BACK_ITEM_SLOT = 27;

    private boolean hasBackItem;
    private BuildTeam buildTeam;

    /** Create a new warp group menu
     *
     * @param menuPlayer The player that is viewing the menu
     * @param buildTeam The build team that the menu is for
     * @param hasBackItem Whether the menu has a back item
     */
    public WarpGroupMenu(Player menuPlayer, BuildTeam buildTeam, boolean hasBackItem) {
        super(4, 3, "Warp Menu", menuPlayer);
        this.hasBackItem = hasBackItem;
        this.buildTeam = buildTeam;
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
            new CountrySelectorMenu(clickPlayer, buildTeam.getContinent());
        });
    }

    @Override
    protected void setPaginatedPreviewItems(List<?> source) {
        List<WarpGroup> warpGroups = source.stream().map(l -> (WarpGroup) l).collect(Collectors.toList());

        // Create the country items
        int slot = 0;

        for (WarpGroup warpGroup : warpGroups) {
            ArrayList<String> warpGroupLore = ListUtil.createList("", "§eDescription:", warpGroup.getDescription());
            getMenu().getSlot(slot).setItem(
                MenuItems.getLetterHead(
                    warpGroup.getName().substring(0, 1),
                    MenuItems.LetterType.WOODEN,
                    "§6§l" + warpGroup.getName(),
                    warpGroupLore
                )
            );
            slot++;
        }
    }

    @Override
    protected void setPaginatedMenuItemsAsync(List<?> source) {

    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
            .item(Item.create(Material.STAINED_GLASS_PANE, " ", (short) 15, null))
            .pattern("000000000")
            .pattern("000000000")
            .pattern("000000000")
            .pattern("111111111")
            .build();
    }

    @Override
    protected List<?> getSource() {
        // Get the warp groups in the build team sorted by name
        List<WarpGroup> warpGroups = buildTeam.getWarpGroups().stream().sorted((warpGroup1, warpGroup2) -> warpGroup1.getName().compareToIgnoreCase(warpGroup2.getName())).collect(Collectors.toList());

        // If the warp group "Other" has no warps, remove it from the list
        WarpGroup otherWarpGroup = warpGroups.stream().filter(warpGroup -> warpGroup.getName().equalsIgnoreCase("Other")).findFirst().orElse(null);
        if(otherWarpGroup != null && otherWarpGroup.getWarps().size() == 0)
            warpGroups.remove(otherWarpGroup);

        return warpGroups;
    }

    @Override
    protected void setPaginatedItemClickEventsAsync(List<?> source) {
        List<WarpGroup> warpGroups = source.stream().map(l -> (WarpGroup) l).collect(Collectors.toList());

        int slot = 0;
        for (WarpGroup warpGroup : warpGroups) {
            final int _slot = slot;
            getMenu().getSlot(_slot).setClickHandler((clickPlayer, clickInformation) -> {
                clickPlayer.closeInventory();

                new WarpMenu(clickPlayer, warpGroup, true);
            });
            slot++;
        }
    }
}
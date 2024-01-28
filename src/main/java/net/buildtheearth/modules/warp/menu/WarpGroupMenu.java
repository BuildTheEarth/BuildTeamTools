package net.buildtheearth.modules.warp.menu;

import net.buildtheearth.Main;
import net.buildtheearth.modules.navigator.menu.CountrySelectorMenu;
import net.buildtheearth.modules.navigator.menu.StateSelectorMenu;
import net.buildtheearth.modules.network.ProxyManager;
import net.buildtheearth.modules.network.model.BuildTeam;
import net.buildtheearth.modules.network.model.Region;
import net.buildtheearth.modules.utils.ChatHelper;
import net.buildtheearth.modules.utils.Item;
import net.buildtheearth.modules.utils.ListUtil;
import net.buildtheearth.modules.utils.Utils;
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
                    Item.createCustomHeadBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFkYzA0OGE3Y2U3OGY3ZGFkNzJhMDdkYTI3ZDg1YzA5MTY4ODFlNTUyMmVlZWQxZTNkYWYyMTdhMzhjMWEifX19",
                            "§6§l" + warpGroup.getName(),
                            warpGroupLore)
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
        return buildTeam.getWarpGroups();
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

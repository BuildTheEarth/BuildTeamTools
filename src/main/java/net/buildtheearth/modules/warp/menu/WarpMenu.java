package net.buildtheearth.modules.warp.menu;

import net.buildtheearth.Main;
import net.buildtheearth.modules.navigator.menu.ExploreMenu;
import net.buildtheearth.modules.network.ProxyManager;
import net.buildtheearth.modules.utils.ChatHelper;
import net.buildtheearth.modules.utils.Item;
import net.buildtheearth.modules.utils.ListUtil;
import net.buildtheearth.modules.utils.Utils;
import net.buildtheearth.modules.utils.menus.AbstractPaginatedMenu;
import net.buildtheearth.modules.warp.model.Warp;
import net.buildtheearth.modules.warp.model.WarpGroup;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class WarpMenu extends AbstractPaginatedMenu {

    public final int BACK_ITEM_SLOT = 27;

    private boolean hasBackItem;
    private WarpGroup warpGroup;

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
        return warpGroup.getWarps();
    }

    @Override
    protected void setPaginatedPreviewItems(List<?> source) {
        List<Warp> warps = source.stream().map(l -> (Warp) l).collect(Collectors.toList());

        // Create the country items
        int slot = 0;

        for (Warp warp : warps) {
            ArrayList<String> loreLines = new ArrayList<>(Arrays.asList("", "§eAddress:"));
            loreLines.addAll(Arrays.asList(Utils.splitStringByLineLength(warp.getAddress(), 30)));

            ArrayList<String> warpLore = ListUtil.createList(loreLines.toArray(new String[loreLines.size()]));
            getMenu().getSlot(slot).setItem(
                    Item.createCustomHeadBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFkYzA0OGE3Y2U3OGY3ZGFkNzJhMDdkYTI3ZDg1YzA5MTY4ODFlNTUyMmVlZWQxZTNkYWYyMTdhMzhjMWEifX19",
                            "§6§l" + warp.getName(),
                            warpLore)
            );
            slot++;
        }
    }

    @Override
    protected void setPaginatedMenuItemsAsync(List<?> source) {

    }

    @Override
    protected void setPaginatedItemClickEventsAsync(List<?> source) {

    }

    // Methods

    private ItemStack getPlaceHolderItem(String countryCode) {
        return Item.create(Material.BARRIER, countryCode);
    }
}

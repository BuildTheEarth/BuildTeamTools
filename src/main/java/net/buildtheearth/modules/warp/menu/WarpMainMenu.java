package net.buildtheearth.modules.warp.menu;

import net.buildtheearth.Main;
import net.buildtheearth.modules.utils.Item;
import net.buildtheearth.modules.utils.MenuItems;
import net.buildtheearth.modules.utils.menus.AbstractPaginatedMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.Arrays;
import java.util.List;

public class WarpMainMenu extends AbstractPaginatedMenu {

    private final Inventory lastInventory;

    public WarpMainMenu(Player menuPlayer, Inventory lastInventory) {
        super(6, 4, ChatColor.GREEN + "Warp Menu", menuPlayer);
        this.lastInventory = lastInventory;
    }

    @Override
    protected void setMenuItemsAsync() {
        getMenu().getSlot(49).setItem(getCloseOrBackItem());
    }

    @Override
    protected void setItemClickEventsAsync() {
        getMenu().getSlot(49).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.closeInventory();
            if(lastInventory != null) clickPlayer.openInventory(lastInventory);
        });
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(Item.create(Material.STAINED_GLASS_PANE, " ", (short) 15, null))
                .pattern("111111111")
                .pattern("000000000")
                .pattern("000000000")
                .pattern("000000000")
                .pattern("000000000")
                .pattern("111101111")
                .build();
    }

    @Override
    protected List<?> getSource() {
        return null;
    }

    @Override
    protected void setPaginatedPreviewItems(List<?> source) {
        for(int slot = 9; slot <= 44; slot++) {
            getMenu().getSlot(slot).setItem(getPlaceHolderItem((String) source.get((slot-9)+(54*(getPage()-1)))));
        }
    }

    @Override
    protected void setPaginatedMenuItemsAsync(List<?> source) {

    }

    @Override
    protected void setPaginatedItemClickEventsAsync(List<?> source) {

    }

    // Methods

    private ItemStack getCloseOrBackItem() {
        boolean previousInventory = lastInventory != null;
        return Item.create(Material.BARRIER, previousInventory ? "§c§lBack" : "§c§lClose");
    }

    private ItemStack getPlaceHolderItem(String countryCode) {
        return Item.create(Material.BARRIER, countryCode);
    }
}

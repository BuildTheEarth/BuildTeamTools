package net.buildtheearth.buildteamtools.utils.menus;

import com.alpsbte.alpslib.utils.item.Item;
import net.buildtheearth.buildteamtools.utils.HeadUtil;
import net.buildtheearth.buildteamtools.utils.MenuItems;
import org.apache.commons.lang3.tuple.MutablePair;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A menu that allows the player to select a block from a list of blocks. It is possible to switch pages and to proceed to the next menu once a block has been selected. It is also possible to select multiple blocks.
 * To change the items that are displayed in the menu, override the {@link #getSource()} method.
 * To perform an action when a block is selected, override the {@link #setItemClickEventsAsync()} method.
 */
public class NameListMenu extends AbstractPaginatedMenu {

    public static final int SWITCH_PAGE_ITEM_SLOT = 31;
    public static final int NEXT_ITEM_SLOT = 35;
    public static final int BACK_ITEM_SLOT = 27;

    protected final List<String> selectedNames;
    private final List<MutablePair<ItemStack, String>> items;

    private final AbstractMenu backMenu;


    public NameListMenu(Player player, String invName, List<MutablePair<ItemStack, String>> items, AbstractMenu backMenu, boolean autoLoad) {
        super(4, 3, invName, player, autoLoad);

        this.items = items;
        this.backMenu = backMenu;
        selectedNames = new ArrayList<>();
    }

    @Override
    protected void setPreviewItems() {
        if (backMenu != null)
            setBackItem(BACK_ITEM_SLOT, backMenu);

        setSwitchPageItems(SWITCH_PAGE_ITEM_SLOT);

        if (canProceed())
            getMenu().getSlot(NEXT_ITEM_SLOT).setItem(HeadUtil.getCheckmarkItem("§eNext"));
        else
            getMenu().getSlot(NEXT_ITEM_SLOT).setItem(MenuItems.ITEM_BACKGROUND);

        super.setPreviewItems();
    }

    @Override
    protected void setMenuItemsAsync() {
        //
    }

    @Override
    protected void setItemClickEventsAsync() {
        setSwitchPageItemClickEvents(SWITCH_PAGE_ITEM_SLOT);
    }

    @Override
    protected Mask getMask() {
        String backSlot = backMenu == null ? "1" : "0";

        return BinaryMask.builder(getMenu())
                .item(MenuItems.ITEM_BACKGROUND)
                .pattern("000000000")
                .pattern("000000000")
                .pattern("000000000")
                .pattern(backSlot + "11000110")
                .build();
    }

    @Override
    protected List<?> getSource() {
        return items;
    }

    @Override
    protected void setPaginatedPreviewItems(List<?> source) {
        // Set pagignated items
        List<MutablePair<ItemStack, String>> pagItems = source.stream().map(l -> (MutablePair<ItemStack, String>) l).toList();
        int slot = 0;
        for (MutablePair<ItemStack, String> item : pagItems) {
            if (selectedNames.contains(item.getRight()))
                item.setLeft(new Item(item.getLeft()).setAmount(1).addEnchantment(Enchantment.LUCK_OF_THE_SEA, 1).hideEnchantments(true).build());

            getMenu().getSlot(slot).setItem(item.getLeft());
            slot++;
        }
    }

    @Override
    protected void setPaginatedMenuItemsAsync(List<?> source) {
        // No Async / DB Items
    }

    @Override
    protected void setPaginatedItemClickEventsAsync(@NonNull List<?> source) {
        List<MutablePair<ItemStack, String>> pagItems = source.stream().map(l -> (MutablePair<ItemStack, String>) l).toList();
        int slot = 0;
        for (MutablePair<ItemStack, String> item : pagItems) {
            final int _slot = slot;
            getMenu().getSlot(_slot).setClickHandler((clickPlayer, clickInformation) -> {
                String type = (item.getRight().toLowerCase());

                if (selectedNames.contains(type))
                    selectedNames.remove(type);
                else
                    selectedNames.add(type);

                reloadMenuAsync();
            });
            slot++;
        }
    }

    /**
     * Checks if the player has selected at least one block.
     *
     * @return true if the player has selected at least one block, false otherwise.
     */
    protected boolean canProceed() {
        return !selectedNames.isEmpty();
    }
}


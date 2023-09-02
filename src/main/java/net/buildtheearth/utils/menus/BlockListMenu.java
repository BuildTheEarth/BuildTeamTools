package net.buildtheearth.utils.menus;

import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.MenuItems;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A menu that allows the player to select a block from a list of blocks. It is possible to switch pages and to proceed to the next menu once a block has been selected. It is also possible to select multiple blocks.
 * To change the items that are displayed in the menu, override the {@link #getSource()} method.
 * To perform an action when a block is selected, override the {@link #setItemClickEventsAsync()} method.
 */
public class BlockListMenu extends AbstractPaginatedMenu {

    public static int SWITCH_PAGE_ITEM_SLOT = 31;
    public static int NEXT_ITEM_SLOT = 35;

    public ArrayList<String> selectedMaterials;
    private final List<ItemStack> items;


    public BlockListMenu(Player player, String invName, List<ItemStack> items) {
        super(4, 3, invName, player);

        this.items = items;
    }

    @Override
    protected void setPreviewItems() {
        setSwitchPageItems(SWITCH_PAGE_ITEM_SLOT);

        if(canProceed())
            getMenu().getSlot(NEXT_ITEM_SLOT).setItem(MenuItems.getNextItem());
        else
            getMenu().getSlot(NEXT_ITEM_SLOT).setItem(MenuItems.ITEM_BACKGROUND);

        super.setPreviewItems();
    }

    @Override
    protected void setMenuItemsAsync() {}

    @Override
    protected void setItemClickEventsAsync() {
        setSwitchPageItemClickEvents(SWITCH_PAGE_ITEM_SLOT);
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(Item.create(Material.STAINED_GLASS_PANE, " ", (short)15, null))
                .pattern("000000000")
                .pattern("000000000")
                .pattern("000000000")
                .pattern("111000110")
                .build();
    }

    @Override
    protected List<?> getSource() {
        return items;
    }

    @Override
    protected void setPaginatedPreviewItems(List<?> source) {
        if(selectedMaterials == null)
            selectedMaterials = new ArrayList<>();

        // Set pagignated items
        List<ItemStack> items = source.stream().map(l -> (ItemStack) l).collect(Collectors.toList());
        int slot = 0;
        for (ItemStack item : items) {
            if(selectedMaterials.contains(Item.getUniqueMaterialString(item)))
                item = new Item(item).setAmount(1).addEnchantment(Enchantment.LUCK, 1).hideEnchantments(true).build();

            getMenu().getSlot(slot).setItem(item);
            slot++;
        }
    }

    @Override
    protected void setPaginatedMenuItemsAsync(List<?> source) {
    }

    @Override
    protected void setPaginatedItemClickEventsAsync(List<?> source) {
        List<ItemStack> items = source.stream().map(l -> (ItemStack) l).collect(Collectors.toList());
        int slot = 0;
        for (ItemStack item : items) {
            final int _slot = slot;
            getMenu().getSlot(_slot).setClickHandler((clickPlayer, clickInformation) -> {
                String type = Item.getUniqueMaterialString(getMenu().getSlot(_slot).getItem(getMenuPlayer()));

                if(selectedMaterials.contains(type))
                    selectedMaterials.remove(type);
                else
                    selectedMaterials.add(type);

                reloadMenuAsync();
            });
            slot++;
        }
    }

    /**
     * Checks if the player has selected at least one block.
     * @return true if the player has selected at least one block, false otherwise.
     */
    protected boolean canProceed(){
        return !selectedMaterials.isEmpty();
    }
}


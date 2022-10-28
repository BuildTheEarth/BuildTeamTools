package net.buildtheearth.buildteam.components.generator.house.menu;

import net.buildtheearth.buildteam.components.generator.house.House;
import net.buildtheearth.buildteam.components.generator.house.HouseFlag;
import net.buildtheearth.buildteam.components.generator.house.menu.AdvancedSettingsMenu;
import net.buildtheearth.utils.AbstractPaginatedMenu;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.MenuItems;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BaseColorMenu extends AbstractPaginatedMenu {

    public static String BASE_TYPE_INV_NAME = "Choose a Base Floor Color";

    public static int SWITCH_PAGE_ITEM_SLOT = 31;
    public static int NEXT_ITEM_SLOT = 35;


    public ArrayList<String> selectedMaterials;

    public BaseColorMenu(Player player) {
        super(4, 3, BASE_TYPE_INV_NAME, player);
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

        // Set click event for next item
        if(canProceed())
            getMenu().getSlot(NEXT_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
                House.playerHouseSettings.get(clickPlayer.getUniqueId()).setValue(HouseFlag.BASE_COLOR, Item.createStringFromItemList(selectedMaterials));

                clickPlayer.closeInventory();
                clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

                new AdvancedSettingsMenu(clickPlayer);
            });
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
        return new ArrayList<>(Arrays.asList(MenuItems.BLOCKS_BY_COLOR_1_12));
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

    private boolean canProceed(){
        return selectedMaterials.size() > 0;
    }
}

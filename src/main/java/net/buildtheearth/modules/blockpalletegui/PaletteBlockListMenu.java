package net.buildtheearth.modules.blockpalletegui;

import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.MenuItems;
import net.buildtheearth.utils.menus.AbstractPaginatedMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PaletteBlockListMenu extends AbstractPaginatedMenu {
    private final BlockPalletManager manager;
    private final JavaPlugin plugin;
    private final boolean useDefaultBlocks;

    public PaletteBlockListMenu(BlockPalletManager manager, Player player, JavaPlugin plugin, boolean useDefaultBlocks) {
        super(6, BlockPalletManager.PAGE_SIZE / 9, "View Palette Menu", player, true);
        this.manager = manager;
        this.plugin = plugin;
        this.useDefaultBlocks = useDefaultBlocks;
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(Item.create(XMaterial.GRAY_STAINED_GLASS_PANE.parseMaterial(), " "))
                .pattern("111111111")
                .pattern("000000000")
                .pattern("000000000")
                .pattern("000000000")
                .pattern("000000000")
                .pattern("111111111")
                .build();
    }

    @Override
    protected List<ItemStack> getSource() {
        if (useDefaultBlocks) {
            List<ItemStack> blocks = new ArrayList<>(MenuItems.getBlocksByColor());
            blocks.removeIf(i -> i == null || !i.getType().isBlock());
            return blocks;
        } else {
            List<String> filters = manager.getFilters(getMenuPlayer());
            if (filters.isEmpty()) filters = Arrays.asList("color");
            ItemStack[] items = manager.getItemsForFilters(filters);
            return new ArrayList<>(Arrays.asList(items));
        }
    }

    @Override
    protected void setPaginatedPreviewItems(List<?> pageItems) {
        setupBorderAndControls();

        @SuppressWarnings("unchecked")
        List<ItemStack> items = (List<ItemStack>) pageItems;
        for (int i = 0; i < items.size(); i++) {
            int slot = 9 + i;
            if (slot >= 45) break;
            getMenu().getSlot(slot).setItem(items.get(i));
        }

        int totalPages = (int) Math.ceil((double) getSource().size() / BlockPalletManager.PAGE_SIZE);
        String pageText = getPage() + "/" + Math.max(totalPages, 1);
        getMenu().getSlot(49)
                .setItem(Item.createCustomHeadBase64(BlockPalletManager.HEAD_BETWEEN_ARROWS, pageText, null));
    }

    @Override
    protected void setPaginatedMenuItemsAsync(List<?> pageItems) {
        setPaginatedPreviewItems(pageItems);
    }

    @Override
    protected void setPaginatedItemClickEventsAsync(List<?> pageItems) {
        @SuppressWarnings("unchecked")
        List<ItemStack> items = (List<ItemStack>) pageItems;
        for (int i = 0; i < items.size(); i++) {
            int slot = 9 + i;
            if (slot >= 45) break;
            final ItemStack item = items.get(i);
            getMenu().getSlot(slot).setClickHandler((p, info) -> p.getInventory().addItem(item.clone()));
        }
    }

    private void setupBorderAndControls() {
        Menu menu = getMenu();
        getMask().apply(menu);

        // Filter / Palette chooser
        menu.getSlot(4).setItem(manager.createMenuItem(XMaterial.HOPPER, "Choose Palette Menu"));
        menu.getSlot(4).setClickHandler((p, info) -> new ChoosePalleteMenu(manager, p, plugin).open());

        // Prev/Next
        menu.getSlot(48).setItem(hasPreviousPage()
                ? Item.createCustomHeadBase64(BlockPalletManager.LEFT_ARROW, "§ePrevious Page", null)
                : Item.create(XMaterial.BARRIER.parseMaterial(), "§cNo Previous Page", null));

        menu.getSlot(50).setItem(hasNextPage()
                ? Item.createCustomHeadBase64(BlockPalletManager.RIGHT_ARROW, "§eNext Page", null)
                : Item.create(XMaterial.BARRIER.parseMaterial(), "§cNo Next Page", null));

        // Page indicator
        menu.getSlot(49).setItem(Item.createCustomHeadBase64(BlockPalletManager.HEAD_BETWEEN_ARROWS, "", null));
        setSwitchPageItemClickEvents(49);
    }

    public static void open(BlockPalletManager manager, Player player, JavaPlugin plugin, boolean useDefaultBlocks) {
        PaletteBlockListMenu menu = new PaletteBlockListMenu(manager, player, plugin, useDefaultBlocks);
        menu.setMenuItemsAsync();
        menu.setItemClickEventsAsync();
        menu.getMenu().open(player);
    }


    @Override
    protected void setItemClickEventsAsync() {
        List<?> sources = getSource();
        int from = (getPage() - 1) * BlockPalletManager.PAGE_SIZE;
        int to = Math.min(from + BlockPalletManager.PAGE_SIZE, sources.size());
        setPaginatedItemClickEventsAsync(sources.subList(from, to));
    }

    @Override
    protected void setMenuItemsAsync() {
        List<?> sources = getSource();
        int from = (getPage() - 1) * BlockPalletManager.PAGE_SIZE;
        int to = Math.min(from + BlockPalletManager.PAGE_SIZE, sources.size());
        setPaginatedMenuItemsAsync(sources.subList(from, to));
    }
}

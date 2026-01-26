package net.buildtheearth.buildteamtools.modules.miscellaneous.blockpalettegui;

import com.alpsbte.alpslib.utils.item.Item;
import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.buildteamtools.utils.MenuItems;
import net.buildtheearth.buildteamtools.utils.menus.AbstractPaginatedMenu;
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
    private final BlockPaletteManager manager;
    private final JavaPlugin plugin;
    private final boolean useDefaultBlocks;

    public PaletteBlockListMenu(BlockPaletteManager manager, Player player, JavaPlugin plugin, boolean useDefaultBlocks) {
        super(6, BlockPaletteManager.PAGE_SIZE / 9, "View Palette Menu", player, true);
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

        int totalPages = (int) Math.ceil((double) getSource().size() / BlockPaletteManager.PAGE_SIZE);
        String pageText = getPage() + "/" + Math.max(totalPages, 1);
        getMenu().getSlot(49)
                .setItem(Item.createCustomHeadBase64(BlockPaletteManager.HEAD_BETWEEN_ARROWS, pageText, null));
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
        menu.getSlot(4).setClickHandler((p, info) -> new ChoosePaletteMenu(manager, p, plugin).open());

        // Prev/Next
        menu.getSlot(48).setItem(hasPreviousPage()
                ? Item.createCustomHeadBase64(BlockPaletteManager.LEFT_ARROW, "§ePrevious Page", null)
                : Item.create(XMaterial.BARRIER.parseMaterial(), "§cNo Previous Page", null));

        menu.getSlot(50).setItem(hasNextPage()
                ? Item.createCustomHeadBase64(BlockPaletteManager.RIGHT_ARROW, "§eNext Page", null)
                : Item.create(XMaterial.BARRIER.parseMaterial(), "§cNo Next Page", null));

        // Page indicator
        menu.getSlot(49).setItem(Item.createCustomHeadBase64(BlockPaletteManager.HEAD_BETWEEN_ARROWS, "", null));
        setSwitchPageItemClickEvents(49);
    }

    public static void open(BlockPaletteManager manager, Player player, JavaPlugin plugin, boolean useDefaultBlocks) {
        PaletteBlockListMenu menu = new PaletteBlockListMenu(manager, player, plugin, useDefaultBlocks);
        menu.setMenuItemsAsync();
        menu.setItemClickEventsAsync();
        menu.getMenu().open(player);
    }


    @Override
    protected void setItemClickEventsAsync() {
        List<?> sources = getSource();
        int from = (getPage() - 1) * BlockPaletteManager.PAGE_SIZE;
        int to = Math.min(from + BlockPaletteManager.PAGE_SIZE, sources.size());
        setPaginatedItemClickEventsAsync(sources.subList(from, to));
    }

    @Override
    protected void setMenuItemsAsync() {
        List<?> sources = getSource();
        int from = (getPage() - 1) * BlockPaletteManager.PAGE_SIZE;
        int to = Math.min(from + BlockPaletteManager.PAGE_SIZE, sources.size());
        setPaginatedMenuItemsAsync(sources.subList(from, to));
    }
}

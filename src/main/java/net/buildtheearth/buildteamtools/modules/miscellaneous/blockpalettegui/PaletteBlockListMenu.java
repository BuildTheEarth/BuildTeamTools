package net.buildtheearth.buildteamtools.modules.miscellaneous.blockpalettegui;

import com.alpsbte.alpslib.utils.item.Item;
import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import net.buildtheearth.buildteamtools.i18n.Messages;
import net.buildtheearth.buildteamtools.utils.ItemComponents;
import net.buildtheearth.buildteamtools.utils.MenuItems;
import net.buildtheearth.buildteamtools.utils.menus.AbstractPaginatedMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PaletteBlockListMenu extends AbstractPaginatedMenu {
    private final BlockPaletteManager manager;
    private final JavaPlugin plugin;
    private final boolean useDefaultBlocks;

    public PaletteBlockListMenu(BlockPaletteManager manager, Player player, JavaPlugin plugin, boolean useDefaultBlocks) {
        super(6, BlockPaletteManager.PAGE_SIZE / 9, Messages.component(MessageKeys.BlockPalette.VIEW_TITLE), player, true);
        this.manager = manager;
        this.plugin = plugin;
        this.useDefaultBlocks = useDefaultBlocks;
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(ItemComponents.of(
                        Objects.requireNonNull(XMaterial.GRAY_STAINED_GLASS_PANE.get()),
                        Component.text(" ")))
                .pattern(BinaryMask.FULL_PATTERN)
                .pattern(BinaryMask.EMPTY_PATTERN)
                .pattern(BinaryMask.EMPTY_PATTERN)
                .pattern(BinaryMask.EMPTY_PATTERN)
                .pattern(BinaryMask.EMPTY_PATTERN)
                .pattern(BinaryMask.FULL_PATTERN)
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
            if (filters.isEmpty()) filters = List.of("color");
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
        Player player = getMenuPlayer();
        getMenu().getSlot(49)
                .setItem(ItemComponents.named(
                        Item.createCustomHeadBase64(BlockPaletteManager.HEAD_BETWEEN_ARROWS, " ", null),
                        Component.text(pageText)));
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
        Player player = getMenuPlayer();
        getMask().apply(menu);

        // Filter / Palette chooser
        menu.getSlot(4).setItem(ItemComponents.of(
                Objects.requireNonNull(XMaterial.HOPPER.get()),
                Messages.render(player, MessageKeys.BlockPalette.CHOOSE_PALETTE_ITEM)));
        menu.getSlot(4).setClickHandler((p, info) -> new ChoosePaletteMenu(manager, p, plugin).open());

        // Prev/Next
        menu.getSlot(48).setItem(hasPreviousPage()
                ? ItemComponents.named(
                Item.createCustomHeadBase64(BlockPaletteManager.LEFT_ARROW, " ", null),
                Messages.render(player, MessageKeys.Ui.PREVIOUS_PAGE_PLAIN))
                : ItemComponents.of(
                Objects.requireNonNull(XMaterial.BARRIER.get()),
                Messages.render(player, MessageKeys.Ui.NO_PREVIOUS_PAGE)));

        menu.getSlot(50).setItem(hasNextPage()
                ? ItemComponents.named(
                Item.createCustomHeadBase64(BlockPaletteManager.RIGHT_ARROW, " ", null),
                Messages.render(player, MessageKeys.Ui.NEXT_PAGE_PLAIN))
                : ItemComponents.of(
                Objects.requireNonNull(XMaterial.BARRIER.get()),
                Messages.render(player, MessageKeys.Ui.NO_NEXT_PAGE)));

        // Page indicator placeholder (updated in setPaginatedPreviewItems)
        menu.getSlot(49).setItem(ItemComponents.named(
                Item.createCustomHeadBase64(BlockPaletteManager.HEAD_BETWEEN_ARROWS, " ", null),
                Component.empty()));
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

package net.buildtheearth.buildteamtools.modules.miscellaneous.blockpalettegui;

import com.alpsbte.alpslib.utils.item.Item;
import com.cryptomorin.xseries.XMaterial;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import net.buildtheearth.buildteamtools.i18n.Messages;
import net.buildtheearth.buildteamtools.utils.ItemComponents;
import net.buildtheearth.buildteamtools.utils.menus.AbstractPaginatedMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Paginated block picker used by Create/Edit palette menus.
 */
public class ChoosePaletteBlocksMenu extends AbstractPaginatedMenu {

    // Layout & controls (matches the common 6-row layout)
    private static final int BACK_SLOT = 45;
    private static final int PREVIOUS_SLOT = 48;
    private static final int PAGE_SLOT = 49;
    private static final int NEXT_SLOT = 50;
    private static final int APPLY_SLOT = 53;

    // Content grid: slots 9..44 (36 items = page size)
    private static final int CONTENT_START = 9;
    private static final int CONTENT_END = 44;
    private static final int PAGE_SIZE = CONTENT_END - CONTENT_START + 1; // 36

    private static final String GREEN_CHECK_HEAD =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90"
                    + "ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDMxMmNh"
                    + "NDYzMmRlZjVmZmFmMmViMGQ5ZDdjYzdiNTVhNTBjNGUzOTIw"
                    + "ZDkwMzcyYWFiMTQwNzgxZjVkZmJjNCJ9fX0=";

    private final Logger logger;

    /**
     * Selected block material names, e.g. "STONE".
     */
    private final List<String> selectedBlocks;

    /**
     * Callback invoked when user clicks Back/Apply (returns current selection).
     */
    private final Consumer<List<String>> onApply;

    /**
     * Debounce per player to avoid double-fire on same physical click.
     */
    private final Map<UUID, Long> clickDebounce = new HashMap<>();

    public ChoosePaletteBlocksMenu(BlockPaletteManager manager,
                                   Player player,
                                   JavaPlugin plugin,
                                   List<String> selectedBlocks,
                                   Consumer<List<String>> onApply) {
        // rows=6, columns=4? (second arg is the "content rows" in your base), title, player, fillMask=true
        super(6, 4, Messages.component(MessageKeys.BlockPalette.CHOOSE_BLOCKS_TITLE), player, true);
        this.logger = plugin.getLogger();
        this.selectedBlocks = (selectedBlocks != null) ? new ArrayList<>(selectedBlocks) : new ArrayList<>();
        this.onApply = onApply;
    }

    /* =========================
       Public API
       ========================= */

    public void open() {
        logger.info("[ChoosePaletteBlocksMenu] open for " + getMenuPlayer().getName()
                + " (preselected: " + selectedBlocks.size() + ")");
        reloadMenuAsync(true);
        getMenu().open(getMenuPlayer());
    }

    /* =========================
       AbstractPaginatedMenu impl
       ========================= */

    @Override
    protected Mask getMask() {
        ItemStack glass = ItemComponents.of(
                safeMat(XMaterial.GRAY_STAINED_GLASS_PANE, Material.GRAY_STAINED_GLASS_PANE),
                Component.text(" "));

        return BinaryMask.builder(getMenu())
                .item(glass)
                .pattern(BinaryMask.FULL_PATTERN)
                .pattern(BinaryMask.EMPTY_PATTERN)
                .pattern(BinaryMask.EMPTY_PATTERN)
                .pattern(BinaryMask.EMPTY_PATTERN)
                .pattern(BinaryMask.EMPTY_PATTERN)
                .pattern(BinaryMask.FULL_PATTERN)
                .build();
    }

    /**
     * Build the full source list of candidate blocks (unique by Material), sorted by name.
     */
    @Override
    protected List<ItemStack> getSource() {
        Set<Material> seen = new LinkedHashSet<>();
        List<ItemStack> out = new ArrayList<>();

        for (BlockPaletteMenuType type : BlockPaletteMenuType.values()) {
            try {
                ItemStack[] supplied = (type.getItemSupplier() != null) ? type.getItemSupplier().get() : null;
                if (supplied == null) continue;
                for (ItemStack it : supplied) {
                    if (it == null) continue;
                    Material m = it.getType();
                    if (m.isAir() || !m.isBlock()) continue;
                    if (seen.add(m)) out.add(new ItemStack(m));
                }
            } catch (Exception t) {
                logger.log(Level.SEVERE, "[ChoosePaletteBlocksMenu] supplier failed for " + type.name() + ": " + t.getMessage(), t);
            }
        }

        // Sort by pretty name
        out.sort(Comparator.comparing(i -> pretty(i.getType().name())));
        return out;
    }

    /**
     * Render current page items + border/controls.
     */
    @Override
    protected void setPaginatedMenuItemsAsync(List<?> pageItems) {
        setupBorderAndControls();

        @SuppressWarnings("unchecked")
        List<ItemStack> items = (List<ItemStack>) pageItems;
        for (int i = 0; i < items.size(); i++) {
            int slot = CONTENT_START + i;
            if (slot > CONTENT_END) break;

            ItemStack base = items.get(i);
            if (base == null || base.getType() == Material.AIR) continue;

            getMenu().getSlot(slot).setItem(createBlockItem(base.getType()));
        }

        // Page indicator
        int totalPages = Math.max(1, (int) Math.ceil((double) getSource().size() / PAGE_SIZE));
        String pageText = getPage() + "/" + totalPages;
        Player player = getMenuPlayer();
        getMenu().getSlot(PAGE_SLOT)
                .setItem(ItemComponents.named(
                        Item.createCustomHeadBase64(BlockPaletteManager.HEAD_BETWEEN_ARROWS, " ", null),
                        Messages.render(player, MessageKeys.Ui.PAGE_INDICATOR, Component.text(pageText))));

        // This helper from your base wires the page head to change pages
        setSwitchPageItemClickEvents(PAGE_SLOT);
    }

    /**
     * Register click handlers for items shown on current page.
     */
    @Override
    protected void setPaginatedItemClickEventsAsync(List<?> pageItems) {
        @SuppressWarnings("unchecked")
        List<ItemStack> items = (List<ItemStack>) pageItems;

        for (int i = 0; i < items.size(); i++) {
            int slot = CONTENT_START + i;
            if (slot > CONTENT_END) break;

            final ItemStack base = items.get(i);
            if (base == null || base.getType() == Material.AIR) continue;

            final Material mat = base.getType();
            final String blockName = mat.name();
            final int targetSlot = slot;

            getMenu().getSlot(targetSlot).setClickHandler((p, info) -> {
                // Debounce ~150 ms to prevent duplicate toggles for one physical click
                long now = System.currentTimeMillis();
                Long last = clickDebounce.get(p.getUniqueId());
                if (last != null && (now - last) < 150) return;
                clickDebounce.put(p.getUniqueId(), now);

                ClickType type = info.getClickType();
                if (!(type == ClickType.LEFT || type == ClickType.SHIFT_LEFT)) return;

                if (selectedBlocks.contains(blockName)) {
                    selectedBlocks.remove(blockName);
                } else {
                    selectedBlocks.add(blockName);
                }

                // Update the slot in place (no reopen/rebind), and refresh preview
                getMenu().getSlot(targetSlot).setItem(createBlockItem(mat));
                setPaginatedPreviewItems(Collections.emptyList());
                p.updateInventory();
            });
        }
    }

    /**
     * Optional preview/summary area required by AbstractPaginatedMenu.
     * We’ll use it to show a small “Selected: N” indicator at slot 46.
     */
    @Override
    protected void setPaginatedPreviewItems(List<?> ignored) {
        Player player = getMenuPlayer();
        int count = selectedBlocks.size();
        List<Component> lore = new ArrayList<>();
        lore.add(Messages.render(player, MessageKeys.BlockPalette.SELECTION_COUNT, Component.text(count)));
        if (count > 0) {
            // show up to 6 names as a teaser
            int shown = 0;
            for (String n : selectedBlocks) {
                if (shown++ >= 6) {
                    lore.add(Messages.render(player, MessageKeys.BlockPalette.SELECTION_MORE));
                    break;
                }
                lore.add(Messages.render(player, MessageKeys.BlockPalette.SELECTION_ITEM, Component.text(pretty(n))));
            }
        }
        getMenu().getSlot(46).setItem(ItemComponents.of(
                Objects.requireNonNull(XMaterial.BOOK.get()),
                Messages.render(player, MessageKeys.BlockPalette.SELECTION),
                lore));
    }

    /**
     * Base menu may call these, but we keep explicit control to ensure the
     * correct items/handlers per page (mirrors your other paginated menus).
     */
    @Override
    protected void setMenuItemsAsync() {
        List<?> src = getSource();
        int from = Math.max(0, (getPage() - 1) * PAGE_SIZE);
        int to = Math.min(from + PAGE_SIZE, src.size());
        setPaginatedMenuItemsAsync(src.subList(from, to));
        setPaginatedPreviewItems(Collections.emptyList());
    }

    @Override
    protected void setItemClickEventsAsync() {
        List<?> src = getSource();
        int from = Math.max(0, (getPage() - 1) * PAGE_SIZE);
        int to = Math.min(from + PAGE_SIZE, src.size());
        setPaginatedItemClickEventsAsync(src.subList(from, to));
    }

    /* =========================
       Helpers / UI wiring
       ========================= */

    private void setupBorderAndControls() {
        Menu menu = getMenu();
        Player player = getMenuPlayer();
        getMask().apply(menu);

        // Back — return to caller with current selection
        menu.getSlot(BACK_SLOT).setItem(ItemComponents.named(
                Item.createCustomHeadBase64(BlockPaletteManager.LEFT_ARROW, " ", null),
                Messages.render(player, MessageKeys.Ui.BACK_RED)));
        menu.getSlot(BACK_SLOT).setClickHandler((p, info) -> {
            p.closeInventory();
            if (onApply != null) onApply.accept(new ArrayList<>(selectedBlocks));
        });

        // Apply — explicit confirm (use green check head)
        menu.getSlot(APPLY_SLOT).setItem(ItemComponents.named(
                Item.createCustomHeadBase64(GREEN_CHECK_HEAD, " ", null),
                Messages.render(player, MessageKeys.Ui.APPLY)));
        menu.getSlot(APPLY_SLOT).setClickHandler((p, info) -> {
            p.closeInventory();
            if (onApply != null) onApply.accept(new ArrayList<>(selectedBlocks));
        });


        // Prev/Next arrows (visuals only; page head handles switching)
        menu.getSlot(PREVIOUS_SLOT).setItem(hasPreviousPage()
                ? ItemComponents.named(
                Item.createCustomHeadBase64(BlockPaletteManager.LEFT_ARROW, " ", null),
                Messages.render(player, MessageKeys.Ui.PREVIOUS_PAGE_PLAIN))
                : ItemComponents.of(
                Objects.requireNonNull(XMaterial.BARRIER.get()),
                Messages.render(player, MessageKeys.Ui.NO_PREVIOUS_PAGE)));

        menu.getSlot(NEXT_SLOT).setItem(hasNextPage()
                ? ItemComponents.named(
                Item.createCustomHeadBase64(BlockPaletteManager.RIGHT_ARROW, " ", null),
                Messages.render(player, MessageKeys.Ui.NEXT_PAGE_PLAIN))
                : ItemComponents.of(
                Objects.requireNonNull(XMaterial.BARRIER.get()),
                Messages.render(player, MessageKeys.Ui.NO_NEXT_PAGE)));
    }

    /**
     * Build a visual item for a material reflecting selected/unselected state.
     */
    private ItemStack createBlockItem(Material mat) {
        boolean selected = selectedBlocks.contains(mat.name());
        Player player = getMenuPlayer();

        List<Component> lore = List.of(Messages.render(player,
                selected ? MessageKeys.BlockPalette.BLOCK_SELECTED : MessageKeys.BlockPalette.BLOCK_SELECT_HINT));
        ItemStack stack = ItemComponents.of(mat, Messages.render(player,
                selected ? MessageKeys.BlockPalette.BLOCK_ON : MessageKeys.BlockPalette.BLOCK_OFF,
                Component.text(pretty(mat.name()))), lore);

        if (selected) {
            ItemMeta meta = stack.getItemMeta();
            if (meta != null) {
                applyGlow(meta);
                stack.setItemMeta(meta);
            }
        }
        return stack;
    }

    /**
     * Version-safe "glow": find a harmless enchant dynamically and hide it.
     */
    private void applyGlow(ItemMeta meta) {
        try {
            var reg = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
            Enchantment ench = reg.get(NamespacedKey.minecraft("unbreaking"));
            if (ench == null) ench = reg.get(NamespacedKey.minecraft("durability"));      // alias
            if (ench == null) ench = reg.get(NamespacedKey.minecraft("luck_of_the_sea")); // fallback
            if (ench != null) {
                meta.addEnchant(ench, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        } catch (Exception ignored) {
            // skip glow if nothing found
        }
    }

    private String pretty(String constant) {
        return PaletteTexts.toTitleCase(constant);
    }

    private Material safeMat(XMaterial xmat, Material fallback) {
        return PaletteTexts.safeMat(xmat, fallback);
    }
}

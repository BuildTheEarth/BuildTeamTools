package net.buildtheearth.modules.miscellaneous.blockpalettegui;

import com.alpsbte.alpslib.utils.item.Item;
import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.utils.menus.AbstractPaginatedMenu;
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

/** Paginated block picker used by Create/Edit palette menus. */
public class ChoosePaletteBlocksMenu extends AbstractPaginatedMenu {

    // Layout & controls (matches the common 6-row layout)
    private static final int BACK_SLOT     = 45;
    private static final int PREVIOUS_SLOT = 48;
    private static final int PAGE_SLOT     = 49;
    private static final int NEXT_SLOT     = 50;
    private static final int APPLY_SLOT    = 53;

    // Content grid: slots 9..44 (36 items = page size)
    private static final int CONTENT_START = 9;
    private static final int CONTENT_END   = 44;
    private static final int PAGE_SIZE     = CONTENT_END - CONTENT_START + 1; // 36

    private static final String GREEN_CHECK_HEAD =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90"
                    + "ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDMxMmNh"
                    + "NDYzMmRlZjVmZmFmMmViMGQ5ZDdjYzdiNTVhNTBjNGUzOTIw"
                    + "ZDkwMzcyYWFiMTQwNzgxZjVkZmJjNCJ9fX0=";

    private final BlockPaletteManager manager;
    private final JavaPlugin plugin;
    private final Logger logger;

    /** Selected block material names, e.g. "STONE". */
    private final List<String> selectedBlocks;

    /** Callback invoked when user clicks Back/Apply (returns current selection). */
    private final Consumer<List<String>> onApply;

    /** Debounce per player to avoid double-fire on same physical click. */
    private final Map<UUID, Long> clickDebounce = new HashMap<>();

    public ChoosePaletteBlocksMenu(BlockPaletteManager manager,
                                   Player player,
                                   JavaPlugin plugin,
                                   List<String> selectedBlocks,
                                   Consumer<List<String>> onApply) {
        // rows=6, columns=4? (second arg is the “content rows” in your base), title, player, fillMask=true
        super(6, 4, "Choose Palette Blocks", player, true);
        this.manager = manager;
        this.plugin  = plugin;
        this.logger  = plugin.getLogger();
        this.selectedBlocks = (selectedBlocks != null) ? new ArrayList<>(selectedBlocks) : new ArrayList<>();
        this.onApply = onApply;
    }

    /* =========================
       Public API
       ========================= */

    public void open() {
        logger.info("[ChoosePalleteBlocksMenu] open for " + getMenuPlayer().getName()
                + " (preselected: " + selectedBlocks.size() + ")");
        reloadMenuAsync(true);
        getMenu().open(getMenuPlayer());
    }

    /* =========================
       AbstractPaginatedMenu impl
       ========================= */

    @Override
    protected Mask getMask() {
        ItemStack glass = new ItemStack(safeMat(XMaterial.GRAY_STAINED_GLASS_PANE, Material.GRAY_STAINED_GLASS_PANE));
        ItemMeta m = glass.getItemMeta();
        if (m != null) { m.setDisplayName(" "); glass.setItemMeta(m); }

        return BinaryMask.builder(getMenu())
                .item(glass)
                .pattern("111111111")
                .pattern("000000000")
                .pattern("000000000")
                .pattern("000000000")
                .pattern("000000000")
                .pattern("111111111")
                .build();
    }

    /** Build the full source list of candidate blocks (unique by Material), sorted by name. */
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
                    if (m == null || m.isAir() || !m.isBlock()) continue;
                    if (seen.add(m)) out.add(new ItemStack(m));
                }
            } catch (Throwable t) {
                logger.log(Level.SEVERE, "[ChoosePalleteBlocksMenu] supplier failed for " + type.name() + ": " + t.getMessage(), t);
            }
        }

        // Sort by pretty name
        out.sort(Comparator.comparing(i -> pretty(i.getType().name())));
        return out;
    }

    /** Render current page items + border/controls. */
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
        getMenu().getSlot(PAGE_SLOT)
                .setItem(Item.createCustomHeadBase64(BlockPaletteManager.HEAD_BETWEEN_ARROWS, "§e" + pageText, new ArrayList<>()));

        // This helper from your base wires the page head to change pages
        setSwitchPageItemClickEvents(PAGE_SLOT);
    }

    /** Register click handlers for items shown on current page. */
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
        int count = selectedBlocks.size();
        List<String> lore = new ArrayList<>();
        lore.add("§7Selected blocks: §f" + count);
        if (count > 0) {
            // show up to 6 names as a teaser
            int shown = 0;
            for (String n : selectedBlocks) {
                if (shown++ >= 6) { lore.add("§7…"); break; }
                lore.add("§8- §f" + pretty(n));
            }
        }
        getMenu().getSlot(46).setItem(Item.create(XMaterial.BOOK.parseMaterial(), "§bSelection", new ArrayList<>(lore)));
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
        getMask().apply(menu);

        // Back — return to caller with current selection
        menu.getSlot(BACK_SLOT).setItem(Item.createCustomHeadBase64(BlockPaletteManager.LEFT_ARROW, "§cBack", new ArrayList<>()));
        menu.getSlot(BACK_SLOT).setClickHandler((p, info) -> {
            p.closeInventory();
            if (onApply != null) onApply.accept(new ArrayList<>(selectedBlocks));
        });

        // Apply — explicit confirm (use green check head)
        menu.getSlot(APPLY_SLOT).setItem(
                Item.createCustomHeadBase64(GREEN_CHECK_HEAD, "§aApply", new ArrayList<>())
        );
        menu.getSlot(APPLY_SLOT).setClickHandler((p, info) -> {
            p.closeInventory();
            if (onApply != null) onApply.accept(new ArrayList<>(selectedBlocks));
        });


        // Prev/Next arrows (visuals only; page head handles switching)
        menu.getSlot(PREVIOUS_SLOT).setItem(hasPreviousPage()
                ? Item.createCustomHeadBase64(BlockPaletteManager.LEFT_ARROW, "§ePrevious Page", new ArrayList<>())
                : Item.create(XMaterial.BARRIER.parseMaterial(), "§cNo Previous Page", new ArrayList<>()));

        menu.getSlot(NEXT_SLOT).setItem(hasNextPage()
                ? Item.createCustomHeadBase64(BlockPaletteManager.RIGHT_ARROW, "§eNext Page", new ArrayList<>())
                : Item.create(XMaterial.BARRIER.parseMaterial(), "§cNo Next Page", new ArrayList<>()));
    }

    /** Build a visual item for a material reflecting selected/unselected state. */
    private ItemStack createBlockItem(Material mat) {
        boolean selected = selectedBlocks.contains(mat.name());

        ItemStack stack = new ItemStack(mat);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName((selected ? "§a✔ " : "§c✘ ") + pretty(mat.name()));

            List<String> lore = new ArrayList<>();
            lore.add(selected ? "§aSelected" : "§7Left-click: §fselect");
            meta.setLore(lore);

            if (selected) {
                applyGlow(meta); // version-safe glow (no compile-time constant)
            }
            stack.setItemMeta(meta);
        }
        return stack;
    }

    /** Version-safe "glow": find a harmless enchant dynamically and hide it. */
    private void applyGlow(ItemMeta meta) {
        try {
            Enchantment ench = Enchantment.getByKey(NamespacedKey.minecraft("unbreaking"));
            if (ench == null) ench = Enchantment.getByKey(NamespacedKey.minecraft("durability"));      // alias
            if (ench == null) ench = Enchantment.getByKey(NamespacedKey.minecraft("luck_of_the_sea")); // fallback
            if (ench == null) ench = Enchantment.getByName("UNBREAKING");   // legacy
            if (ench == null) ench = Enchantment.getByName("DURABILITY");   // legacy alias
            if (ench == null) ench = Enchantment.getByName("LUCK");         // very old
            if (ench != null) {
                meta.addEnchant(ench, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        } catch (Throwable ignored) {
            // skip glow if nothing found
        }
    }

    private String pretty(String constant) {
        String lower = constant.toLowerCase(Locale.ROOT).replace('_', ' ');
        String[] parts = lower.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (p.isEmpty()) continue;
            sb.append(Character.toUpperCase(p.charAt(0)))
                    .append(p.length() > 1 ? p.substring(1) : "")
                    .append(' ');
        }
        return sb.toString().trim();
    }

    private Material safeMat(XMaterial xmat, Material fallback) {
        try {
            Material m = xmat.parseMaterial();
            return (m != null) ? m : fallback;
        } catch (Throwable t) {
            return fallback;
        }
    }
}

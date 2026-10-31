package net.buildtheearth.buildteamtools.modules.miscellaneous.blockpalettegui;

import com.alpsbte.alpslib.utils.item.Item;
import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import net.buildtheearth.buildteamtools.i18n.Messages;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import net.buildtheearth.buildteamtools.utils.ItemComponents;
import net.buildtheearth.buildteamtools.utils.menus.AbstractMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.*;
import java.util.logging.Level;

public class ChoosePaletteMenu extends AbstractMenu {

    private static final int BACK_SLOT = 36;
    private static final int ADD_PALETTE_SLOT = 44;

    private static final String BACK_HEAD =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90"
                    + "ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RjOWU0"
                    + "ZGNmYTQyMjFhMWZhZGMxYjViMmIxMWQ4YmVlYjU3ODc5YWYx"
                    + "YzQyMzYyMTQyYmFlMWVkZDUifX19";

    private static final String PLUS_HEAD =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90"
                    + "ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA1NmJj"
                    + "MTI0NGZjZmY5OTM0NGYxMmFiYTQyYWMyM2ZlZTZlZjZlMzM1"
                    + "MWQyN2QyNzNjMTU3MjUzMWYifX19";

    private final BlockPaletteManager manager;
    private final JavaPlugin plugin;
    private final Map<Integer, String> slotToFilter = new HashMap<>();

    // NEW: per-player debounce to prevent double-fire (on->off) for one physical click
    private final Map<UUID, Long> clickDebounce = new HashMap<>();

    public ChoosePaletteMenu(BlockPaletteManager manager, Player player, JavaPlugin plugin) {
        super(5, Messages.component(MessageKeys.BlockPalette.CHOOSE_TITLE), player);
        this.manager = manager;
        this.plugin = plugin;
    }

    @Override
    protected Mask getMask() {
        ItemStack glass = ItemComponents.of(
                safeMat(XMaterial.GRAY_STAINED_GLASS_PANE, Material.GRAY_STAINED_GLASS_PANE),
                Component.text(" "));
        return BinaryMask.builder(getMenu())
                .item(glass)
                .pattern(BinaryMask.FULL_PATTERN)
                .pattern("100000001")
                .pattern("100000001")
                .pattern("100000001")
                .pattern("011111110")
                .build();
    }

    @Override
    protected void setMenuItemsAsync() {
        try {
            slotToFilter.clear();
            Player player = getMenuPlayer();

            int cursor = 10;
            Set<String> active = manager.getPlayerFilters(player);

            // Custom palettes
            for (Map.Entry<String, BlockPaletteManager.Palette> entry : manager.getPalettes().entrySet()) {
                String key = normalizeKey(entry.getKey());
                BlockPaletteManager.Palette palette = entry.getValue();
                boolean on = active.contains(key);

                String baseName = palette != null && palette.getName() != null ? palette.getName() : toTitleCase(key);
                Component displayName = Messages.render(player,
                        on ? MessageKeys.BlockPalette.FILTER_ON : MessageKeys.BlockPalette.FILTER_OFF,
                        Component.text(baseName));

                ItemStack icon = ItemComponents.of(
                        safeMat(XMaterial.PAINTING, Material.PAPER),
                        displayName,
                        buildCustomPaletteLore(player, palette));

                getMenu().getSlot(cursor).setItem(icon);
                slotToFilter.put(cursor, key);

                cursor++;
                if ((cursor + 1) % 9 == 0) cursor += 2;
                if (cursor >= BACK_SLOT) break;
            }

            // Predefined filters
            for (BlockPaletteMenuType type : BlockPaletteMenuType.values()) {
                String key = normalizeKey(type.getReadableName());
                if (slotToFilter.containsValue(key)) continue;

                boolean on = active.contains(key);
                Component title = Messages.render(player,
                        on ? MessageKeys.BlockPalette.FILTER_ON : MessageKeys.BlockPalette.FILTER_OFF,
                        Component.text(type.getReadableName()));

                ItemStack[] supplied = null;
                try {
                    supplied = (type.getItemSupplier() != null) ? type.getItemSupplier().get() : null;
                } catch (Exception e) {
                    plugin.getLogger().log(Level.SEVERE, "Supplier failed for " + key, e);
                }

                ItemStack icon;
                if (supplied != null && supplied.length > 0 && supplied[0] != null) {
                    icon = ItemComponents.of(supplied[0].getType(), title);
                } else {
                    icon = ItemComponents.of(safeMat(XMaterial.BARRIER, Material.BARRIER), title);
                }

                getMenu().getSlot(cursor).setItem(icon);
                slotToFilter.put(cursor, key);

                cursor++;
                if ((cursor + 1) % 9 == 0) cursor += 2;
                if (cursor >= BACK_SLOT) break;
            }

            // Back & Add
            getMenu().getSlot(BACK_SLOT).setItem(ItemComponents.named(
                    Item.createCustomHeadBase64(BACK_HEAD, " ", null),
                    Messages.render(player, MessageKeys.Ui.BACK)));
            if (hasEditPermission()) {
                getMenu().getSlot(ADD_PALETTE_SLOT).setItem(ItemComponents.named(
                        Item.createCustomHeadBase64(PLUS_HEAD, " ", null),
                        Messages.render(player, MessageKeys.BlockPalette.ADD_NEW)));
            }
        } catch (Exception ex) {
            logError("setMenuItemsAsync", ex);
            getMenuPlayer().sendMessage(Messages.component(MessageKeys.BlockPalette.MENU_BUILD_FAILED));
        }
    }

    @Override
    protected void setItemClickEventsAsync() {
        try {
            slotToFilter.forEach((slot, key) -> getMenu().getSlot(slot).setClickHandler((p, info) -> {
                try {
                    // DEBOUNCE: ignore duplicate firings within ~150 ms for this player
                    long now = System.currentTimeMillis();
                    Long last = clickDebounce.get(p.getUniqueId());
                    if (last != null && (now - last) < 150) return;
                    clickDebounce.put(p.getUniqueId(), now);

                    ClickType click = info.getClickType();

                    if (click == ClickType.LEFT || click == ClickType.SHIFT_LEFT) {
                        Set<String> filters = new HashSet<>(manager.getPlayerFilters(p));
                        final boolean nowOn;
                        if (filters.contains(key)) {
                            filters.remove(key);
                            nowOn = false;
                        } else {
                            filters.add(key);
                            nowOn = true;
                        }

                        // optional exclusivity vs "color"
                        if (!"color".equals(key)) filters.remove("color");

                        manager.updatePlayerFilters(p, filters);

                        // Update clicked slot title correctly (palette name if custom)
                        ItemStack cur = getMenu().getSlot(slot).getItem();
                        if (cur != null) {
                            String baseTitle;
                            BlockPaletteManager.Palette pal = manager.getPalette(key);
                            if (pal != null && pal.getName() != null && !pal.getName().isEmpty()) {
                                baseTitle = pal.getName();
                            } else {
                                baseTitle = toTitleCase(key.replace('_', ' '));
                            }
                            Component newTitle = Messages.render(p,
                                    nowOn ? MessageKeys.BlockPalette.FILTER_ON : MessageKeys.BlockPalette.FILTER_OFF,
                                    Component.text(baseTitle));

                            List<Component> lore = (pal != null)
                                    ? buildCustomPaletteLore(p, pal)
                                    : null;
                            ItemStack updated = lore != null
                                    ? ItemComponents.of(cur.getType(), newTitle, lore)
                                    : ItemComponents.of(cur.getType(), newTitle);
                            getMenu().getSlot(slot).setItem(updated);
                            p.updateInventory();
                        }
                        return;
                    }

                    if (click == ClickType.RIGHT && hasEditPermission()) {
                        BlockPaletteManager.Palette palette = manager.getPalette(key);
                        p.closeInventory();
                        if (palette != null) {
                            new EditPaletteMenu(manager, p, plugin, key,
                                    palette.getName(), palette.getDescription(), palette.getBlocks()).open();
                        } else {
                            for (BlockPaletteMenuType type : BlockPaletteMenuType.values()) {
                                String typeKey = normalizeKey(type.getReadableName());
                                if (typeKey.equals(key)) {
                                    ItemStack[] items = (type.getItemSupplier() != null) ? type.getItemSupplier().get() : null;
                                    List<String> blockNames = new ArrayList<>();
                                    if (items != null)
                                        for (ItemStack it : items) if (it != null) blockNames.add(it.getType().name());
                                    new EditPaletteMenu(manager, p, plugin, key, type.getReadableName(), "", blockNames).open();
                                    return;
                                }
                            }
                            p.sendMessage(Messages.component(MessageKeys.BlockPalette.FILTER_NOT_FOUND));
                        }
                    }
                } catch (Exception e) {
                    logError("clickHandler slot=" + slot + " key=" + key, e);
                    p.sendMessage(Messages.component(MessageKeys.BlockPalette.CLICK_ERROR));
                }
            }));

            getMenu().getSlot(BACK_SLOT).setClickHandler((p, i) -> manager.openBlockMenu(p));

            if (hasEditPermission()) {
                getMenu().getSlot(ADD_PALETTE_SLOT).setClickHandler((p, i) -> {
                    p.closeInventory();
                    new CreatePaletteMenu(manager, p, plugin).open();
                });
            }
        } catch (Exception ex) {
            logError("setItemClickEventsAsync", ex);
            getMenuPlayer().sendMessage(Messages.component(MessageKeys.BlockPalette.HANDLERS_FAILED));
        }
    }

    private boolean hasEditPermission() {
        try {
            return getMenuPlayer().hasPermission(Permissions.BLOCK_PALETTE_EDIT);
        } catch (Exception e) {
            return false;
        }
    }

    public void open() {
        setMenuItemsAsync();
        setItemClickEventsAsync();
        getMenu().open(getMenuPlayer());
    }

    /* helpers */

    private List<Component> buildCustomPaletteLore(Player player, BlockPaletteManager.Palette palette) {
        int blockCount = (palette != null && palette.getBlocks() != null) ? palette.getBlocks().size() : 0;
        String desc = (palette != null ? palette.getDescription() : "");
        String descText = (desc == null || desc.isEmpty())
                ? Messages.plain(player, MessageKeys.BlockPalette.NONE)
                : desc;
        return List.of(
                Messages.render(player, MessageKeys.BlockPalette.LORE_BLOCKS, Component.text(blockCount)),
                Messages.render(player, MessageKeys.BlockPalette.LORE_DESCRIPTION, Component.text(descText))
        );
    }

    private void logError(String ctx, Throwable t) {
        try {
            plugin.getLogger().log(Level.SEVERE, "[ChoosePalleteMenu] " + ctx + " failed: " + t.getMessage(), t);
        } catch (Exception ignore) {
        }
    }

    private String toTitleCase(String s) {
        if (s == null || s.isEmpty()) return "";
        String[] parts = s.trim().toLowerCase(Locale.ROOT).split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) continue;
            sb.append(Character.toUpperCase(part.charAt(0)))
                    .append(part.length() > 1 ? part.substring(1) : "")
                    .append(' ');
        }
        return sb.toString().trim();
    }

    private String normalizeKey(String input) {
        if (input == null) return "";
        String s = input.trim().toLowerCase(Locale.ROOT).replace(' ', '_');
        while (s.contains("__")) s = s.replace("__", "_");
        return s;
    }

    private Material safeMat(XMaterial xmat, Material fallback) {
        try {
            Material m = xmat.get();
            return (m != null) ? m : fallback;
        } catch (Exception t) {
            return fallback;
        }
    }
}

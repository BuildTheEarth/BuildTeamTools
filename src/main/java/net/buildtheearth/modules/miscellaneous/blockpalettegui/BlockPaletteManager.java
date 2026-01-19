package net.buildtheearth.modules.miscellaneous.blockpalettegui;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BlockPaletteManager {
    public static final int PAGE_SIZE = 36;

    private final Map<UUID, List<String>> playerFilterMap = new HashMap<>();
    private final Map<UUID, Integer> playerPageMap = new HashMap<>();
    private final Map<String, Palette> palettes = new HashMap<>();
    private final JavaPlugin plugin;
    private final File paletteFile;
    private final FileConfiguration paletteConfig;

    public static final String HEAD_BETWEEN_ARROWS =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6"
                    + "Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv"
                    + "MmRjYTYwZTRlMjNjNjI3OTc5YWJiMjZmMjhiYjkxODNh"
                    + "ZThjMmM2ZmViZTU0YjNjODliOGZjNDYzZjNhNDAwNSJ9"
                    + "fX0=";

    public static final String LEFT_ARROW =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6"
                    + "Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv"
                    + "Y2RjOWU0ZGNmYTQyMjFhMWZhZGMxYjViMmIxMWQ4YmVl"
                    + "YjU3ODc5YWYxYzQyMzYyMTQyYmFlMWVkZDUifX19";

    public static final String RIGHT_ARROW =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv"
                    + "OTU2YTM2MTg0NTllNDNiMjg3YjIyYjdlMjM1ZWM2OTk1OTQ1NDZjNmZjZDZkYzg0YmZjYTRjZjMwYWI5MzExIn19fQ==";

    public BlockPaletteManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.paletteFile = plugin.getDataPath().resolve("modules").resolve("miscellaneous").resolve("blockpalettegui").resolve("palettes.yml").toFile();
        this.paletteConfig = YamlConfiguration.loadConfiguration(paletteFile);
        loadPalettes();
    }

    /* =======================
       Palette model
       ======================= */
    @Getter
    @Setter
    public static class Palette {
        private String name;
        private String description;
        private List<String> blocks;

        public Palette(String name, String description, List<String> blocks) {
            this.name = name;
            this.description = description != null ? description : "";
            this.blocks = new ArrayList<>(blocks != null ? blocks : new ArrayList<>());
        }

        public List<String> getBlocks() { return new ArrayList<>(blocks); }
        public void setBlocks(List<String> blocks) { this.blocks = new ArrayList<>(blocks); }
    }

    /* =======================
       Palette CRUD
       ======================= */
    public void createPalette(String key, String name, String description, List<String> blocks) {
        key = normalizeKey(key);
        palettes.put(key, new Palette(name, description, blocks));
        savePalettes();
        plugin.getLogger().info("[BlockPalletManager] Created palette: " + key);
    }

    public void updatePalette(String key, String name, String description, List<String> blocks) {
        key = normalizeKey(key);
        Palette p = palettes.get(key);
        if (p == null) {
            // upsert: bestond niet, dus aanmaken
            p = new Palette(name, description, blocks);
            palettes.put(key, p);
        } else {
            p.setName(name);
            p.setDescription(description);
            p.setBlocks(blocks);
        }
        savePalettes();
        plugin.getLogger().info("[BlockPalletManager] Upsert palette: " + key);
    }


    public void deletePalette(String key) {
        key = normalizeKey(key);
        palettes.remove(key);
        savePalettes();
        plugin.getLogger().info("[BlockPalletManager] Deleted palette: " + key);
    }

    public Map<String, Palette> getPalettes() {
        // return a copy to avoid external mutation
        return new HashMap<>(palettes);
    }

    public Palette getPalette(String key) {
        return palettes.get(normalizeKey(key));
    }

    /* =======================
       Menus & paging
       ======================= */
    /** Open de standaard blocklist (die filters respecteert). */
    public void openBlockMenu(Player player) {
        PaletteBlockListMenu.open(this, player, plugin, false);
    }

    public void handlePageClick(Player player, boolean isNext) {
        int current = playerPageMap.getOrDefault(player.getUniqueId(), 0);
        current += isNext ? 1 : -1;
        current = Math.max(current, 0);
        playerPageMap.put(player.getUniqueId(), current);
        openBlockMenu(player);
    }

    int getPlayerPage(Player player) { return playerPageMap.getOrDefault(player.getUniqueId(), 0); }
    void setPlayerPage(Player player, int page) { playerPageMap.put(player.getUniqueId(), page); }

    /* =======================
       Filters (normalized!)
       ======================= */
    /** Always returns a *normalized* set of the player's filters (lowercase_with_underscores). */
    public Set<String> getPlayerFilters(Player player) {
        List<String> raw = playerFilterMap.getOrDefault(player.getUniqueId(), Collections.emptyList());
        Set<String> normalized = new LinkedHashSet<>();
        for (String f : raw) normalized.add(normalizeKey(f));
        return normalized;
    }

    /** Returns a *normalized* list of filters, with "color" as default if none set. */
    List<String> getFilters(Player player) {
        Set<String> s = getPlayerFilters(player);
        if (s.isEmpty()) return Collections.singletonList("color");
        return new ArrayList<>(s);
    }

    /** Store a normalized copy of the filter set for this player. */
    public void updatePlayerFilters(Player player, Set<String> filters) {
        List<String> normalized = new ArrayList<>();
        for (String f : filters) normalized.add(normalizeKey(f));
        playerFilterMap.put(player.getUniqueId(), normalized);
        plugin.getLogger().info("[BlockPalletManager] Filters for " + player.getName() + " -> " + normalized);
    }

    /** Convenience method used by commands; normalizes filters and opens block menu. */
    public void setPlayerFiltersAndOpen(Player player, String... filters) {
        List<String> normalized = new ArrayList<>();
        if (filters == null || filters.length == 0) {
            normalized.add("color");
        } else {
            for (String f : filters) normalized.add(normalizeKey(f));
        }
        playerFilterMap.put(player.getUniqueId(), normalized);
        playerPageMap.put(player.getUniqueId(), 0);
        plugin.getLogger().info("[BlockPalletManager] (set+open) Filters for " + player.getName() + " -> " + normalized);
        openBlockMenu(player);
    }

    /** Combineer custom palettes + predefined filters (via BlockPalletMenuType). */
    ItemStack[] getItemsForFilters(java.util.List<String> filters) {
        // Normalize incoming filters defensively
        java.util.List<String> normalized = new java.util.ArrayList<>();
        for (String f : filters) {
            if (f != null) normalized.add(normalizeKey(f));
        }

        java.util.Set<Material> seen = new java.util.LinkedHashSet<>();
        java.util.List<ItemStack> out = new java.util.ArrayList<>();

        for (String filter : normalized) {
            // Custom palette?
            if (palettes.containsKey(filter)) {
                Palette pal = palettes.get(filter);
                if (pal != null) {
                    for (String name : pal.getBlocks()) {
                        Material m = Material.getMaterial(name);
                        if (m != null && seen.add(m)) out.add(new ItemStack(m));
                    }
                }
                continue;
            }

            // Predefined (use enum key)
            for (BlockPaletteMenuType type : BlockPaletteMenuType.values()) {
                String key = type.getFilterKey(); // <— canonical key
                if (key.equalsIgnoreCase(filter)) {
                    ItemStack[] items = null;
                    try {
                        items = (type.getItemSupplier() != null) ? type.getItemSupplier().get() : null;
                    } catch (Throwable t) {
                        plugin.getLogger().log(java.util.logging.Level.SEVERE,
                                "[BlockPalletManager] Supplier failed for " + key + ": " + t.getMessage(), t);
                    }
                    if (items != null) {
                        for (ItemStack it : items) {
                            if (it != null && seen.add(it.getType())) out.add(it);
                        }
                    }
                    break;
                }
            }
        }
        return out.toArray(new ItemStack[0]);
    }


    ItemStack createMenuItem(XMaterial material, String name) {
        ItemStack item = material.parseItem();
        if (item == null) item = new ItemStack(Material.BARRIER);
        ItemMeta m = item.getItemMeta();
        if (m != null) {
            m.setDisplayName(name);
            item.setItemMeta(m);
        }
        return item;
    }

    /* =======================
       Persistence
       ======================= */
    private void savePalettes() {
        paletteConfig.set("palettes", null);
        for (Map.Entry<String, Palette> entry : palettes.entrySet()) {
            String key = entry.getKey();
            Palette palette = entry.getValue();
            paletteConfig.set("palettes." + key + ".name", palette.getName());
            paletteConfig.set("palettes." + key + ".description", palette.getDescription());
            paletteConfig.set("palettes." + key + ".blocks", palette.getBlocks());
        }
        try {
            paletteConfig.save(paletteFile);
            plugin.getLogger().info("Palettes saved to palettes.yml");
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save palettes to palettes.yml: " + e.getMessage());
        }
    }

    private void loadPalettes() {
        if (!paletteFile.exists()) {
            try {
                File parent = paletteFile.getParentFile();
                if (parent != null) parent.mkdirs();

                paletteFile.createNewFile();
                plugin.getLogger().info("Created new palettes.yml file at: " + paletteFile.getPath());
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create palettes.yml: " + e.getMessage());
                return;
            }
        }

        if (paletteConfig.getConfigurationSection("palettes") == null) {
            plugin.getLogger().info("No palettes found in palettes.yml");
            return;
        }

        for (String key : paletteConfig.getConfigurationSection("palettes").getKeys(false)) {
            String name = paletteConfig.getString("palettes." + key + ".name", "Unnamed");
            String description = paletteConfig.getString("palettes." + key + ".description", "");
            List<String> blocks = new ArrayList<>();
            List<?> rawBlocks = paletteConfig.getList("palettes." + key + ".blocks", Collections.emptyList());
            for (Object block : rawBlocks) {
                if (block instanceof String) blocks.add((String) block);
                else plugin.getLogger().warning("Invalid block entry in palette " + key + ": " + block + " is not a string");
            }
            palettes.put(normalizeKey(key), new Palette(name, description, blocks));
        }
        plugin.getLogger().info("Loaded " + palettes.size() + " palettes from palettes.yml");
    }

    /* =======================
       Key normalization
       ======================= */
    /** Normalize keys to lowercase_with_underscores so GUI + commands match. */
    private String normalizeKey(String input) {
        if (input == null) return "";
        // 1) lowercase, 2) trim, 3) replace spaces with underscores, 4) collapse multiple underscores
        String s = input.trim().toLowerCase(Locale.ROOT).replace(' ', '_');
        // remove accidental double-underscores from weird names
        while (s.contains("__")) s = s.replace("__", "_");
        return s;
    }
}

package net.buildtheearth.modules.blockpalletegui;

import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

public enum BlockPalletMenuType {
    SLABS("Slabs", "slabs", MenuItems::getSlabs),
    STAIRS("Stairs", "stairs", MenuItems::getStairs),
    WALLS("Walls", "walls", MenuItems::getWalls),
    COLOR("Color", "color", MenuItems::getBlocksByColor),
    LOGS("Logs", "logs", MenuItems::getLogs),
    LEAVES("Leaves", "leaves", MenuItems::getLeaves),
    FENCES("Fences", "fences", MenuItems::getFences),
    CARPET("Carpet", "carpet", MenuItems::getCarpet),
    WOOL("Wool", "wool", MenuItems::getWool),
    TERRACOTTA("Terracotta", "terracotta", MenuItems::getTerracotta),
    CONCRETE("Concrete", "concrete", MenuItems::getConcrete),
    CONCRETE_POWDER("Concrete Powder", "concrete_powder", MenuItems::getConcretePowder),
    BED("Bed", "bed", MenuItems::getBeds),
    CANDLE("Candle", "candle", MenuItems::getCandles),
    BANNER("Banner", "banner", MenuItems::getBanners),
    GLASS_PANE("Glass Pane", "glass_pane", MenuItems::getGlassPanes),
    GLASS("Glass", "glass", MenuItems::getGlass);

    private final String readableName;
    private final String filterKey;
    private final Supplier<ItemStack[]> itemSupplier;

    private static final Map<String, BlockPalletMenuType> keyToType = new HashMap<>();
    static {
        for (BlockPalletMenuType type : values()) {
            keyToType.put(type.filterKey, type);
        }
    }

    /**
     * Complete list of all supported filter keys (for tab completion).
     */
    public static final List<String> FILTER_OPTIONS =
            Collections.unmodifiableList(
                    Arrays.stream(values())
                            .map(BlockPalletMenuType::getFilterKey)
                            .collect(Collectors.toList())
            );

    BlockPalletMenuType(String readableName, String filterKey, Supplier<ItemStack[]> itemSupplier) {
        this.readableName = readableName;
        this.filterKey = filterKey;
        this.itemSupplier = itemSupplier;
    }

    /**
     * @return human-readable menu title
     */
    public String getReadableName() {
        return readableName;
    }

    /**
     * @return the key used for filtering/tab-completion
     */
    public String getFilterKey() {
        // If you already have a key field, return that instead.
        return getReadableName().toLowerCase().trim().replace(' ', '_');
    }


    /**
     * @return supplier for the menu's ItemStack array
     */
    public Supplier<ItemStack[]> getItemSupplier() {
        return itemSupplier;
    }

    /**
     * Lookup enum by filter key (case-insensitive)
     */
    public static BlockPalletMenuType getMenuType(String key) {
        return keyToType.get(key.toLowerCase());
    }

}

package net.buildtheearth.modules.miscellaneous.blockpalettegui;

import net.buildtheearth.utils.MenuItems;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

public enum BlockPaletteMenuType {
    SLABS("Slabs", "slabs", MenuItems::getSlabsArray),
    STAIRS("Stairs", "stairs", MenuItems::getStairsArray),
    WALLS("Walls", "walls", MenuItems::getWallsArray),
    COLOR("Color", "color", MenuItems::getBlocksByColorArray),
    LOGS("Logs", "logs", MenuItems::getLogsArray),
    LEAVES("Leaves", "leaves", MenuItems::getLeavesArray),
    FENCES("Fences", "fences", MenuItems::getFencesArray),
    CARPET("Carpet", "carpet", MenuItems::getCarpetArray),
    WOOL("Wool", "wool", MenuItems::getWoolArray),
    TERRACOTTA("Terracotta", "terracotta", MenuItems::getTerracottaArray),
    CONCRETE("Concrete", "concrete", MenuItems::getConcreteArray),
    CONCRETE_POWDER("Concrete Powder", "concrete_powder", MenuItems::getConcretePowderArray),
    BED("Bed", "bed", MenuItems::getBedsArray),
    CANDLE("Candle", "candle", MenuItems::getCandlesArray),
    BANNER("Banner", "banner", MenuItems::getBannersArray),
    GLASS_PANE("Glass Pane", "glass_pane", MenuItems::getGlassPanesArray),
    GLASS("Glass", "glass", MenuItems::getGlassArray);

    private final String readableName;
    private final String filterKey;
    private final Supplier<ItemStack[]> itemSupplier;

    private static final Map<String, BlockPaletteMenuType> keyToType = new HashMap<>();
    static {
        for (BlockPaletteMenuType type : values()) {
            keyToType.put(type.filterKey.toLowerCase(), type);
        }
    }

    /**
     * Complete list of all supported filter keys (for tab completion).
     */
    public static final List<String> FILTER_OPTIONS =
            Collections.unmodifiableList(
                    Arrays.stream(values())
                            .map(BlockPaletteMenuType::getFilterKey)
                            .collect(Collectors.toList())
            );

    BlockPaletteMenuType(String readableName, String filterKey, Supplier<ItemStack[]> itemSupplier) {
        this.readableName = readableName;
        this.filterKey = filterKey;
        this.itemSupplier = itemSupplier;
    }

    /** @return human-readable menu title */
    public String getReadableName() {
        return readableName;
    }

    /** @return the key used for filtering/tab-completion */
    public String getFilterKey() {
        return filterKey;
    }

    /** @return supplier for the menu's ItemStack array */
    public Supplier<ItemStack[]> getItemSupplier() {
        return itemSupplier;
    }

    /** Lookup enum by filter key (case-insensitive) */
    public static BlockPaletteMenuType getMenuType(String key) {
        if (key == null) return null;
        return keyToType.get(key.toLowerCase());
    }
}

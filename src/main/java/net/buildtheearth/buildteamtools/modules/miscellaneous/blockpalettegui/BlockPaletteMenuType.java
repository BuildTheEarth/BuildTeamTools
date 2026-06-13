package net.buildtheearth.buildteamtools.modules.miscellaneous.blockpalettegui;

import lombok.Getter;
import net.buildtheearth.buildteamtools.utils.MenuItems;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Getter
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

    /**
     * Human-readable menu title
     */
    private final String readableName;
    /**
     * The key used for filtering/tab-completion
     */
    private final String filterKey;
    /**
     * Supplier for the menu's ItemStack array
     */
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

    /**
     * Lookup enum by filter key (case-insensitive)
     */
    public static BlockPaletteMenuType getMenuType(String key) {
        if (key == null) return null;
        return keyToType.get(key.toLowerCase());
    }
}

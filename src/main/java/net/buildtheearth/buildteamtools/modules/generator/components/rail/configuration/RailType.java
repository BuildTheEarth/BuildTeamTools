package net.buildtheearth.buildteamtools.modules.generator.components.rail.configuration;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import net.buildtheearth.buildteamtools.modules.generator.GeneratorModule;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.Rail;
import org.bukkit.Material;
import org.jspecify.annotations.Nullable;

import java.util.List;

@Getter
public final class RailType {

    public static final String STANDARD_IDENTIFIER = "standard";

    public static final int MIN_TRACK_COUNT = 1;
    public static final int MAX_TRACK_COUNT = 8;
    public static final int MIN_TRACK_SPACING = 3;
    public static final int MAX_TRACK_SPACING = 32;
    // A sleeper spacing of 0 disables sleepers entirely.
    public static final int MIN_SLEEPER_SPACING = 0;
    public static final int MAX_SLEEPER_SPACING = 16;
    public static final int MIN_OVERHEAD_POLE_SPACING = 1;
    public static final int MAX_OVERHEAD_POLE_SPACING = 512;
    public static final int MIN_OVERHEAD_POLE_OFFSET = 1;
    public static final int MAX_OVERHEAD_POLE_OFFSET = 16;
    public static final int MIN_OVERHEAD_POLE_HEIGHT = 3;
    public static final int MAX_OVERHEAD_POLE_HEIGHT = 16;
    public static final int MAX_IDENTIFIER_LENGTH = 32;
    public static final int MAX_DISPLAY_NAME_LENGTH = 48;

    public static final int DEFAULT_TRACK_COUNT = 2;
    public static final int DEFAULT_TRACK_SPACING = 5;
    public static final int DEFAULT_OVERHEAD_POLE_SPACING = 16;
    public static final int DEFAULT_OVERHEAD_POLE_OFFSET = 3;
    public static final int DEFAULT_OVERHEAD_POLE_HEIGHT = 6;

    private static final String IDENTIFIER_PATTERN = "[a-z0-9_-]+";

    private final String identifier;
    private final String displayName;
    private final XMaterial icon;
    private final XMaterial railBlock;
    private final List<XMaterial> blocksBelow;
    private final @Nullable XMaterial sleeperBlock;
    private final int sleeperSpacing;
    private final int trackCount;
    private final int trackSpacing;
    private final List<Integer> trackSpacings;
    private final boolean overheadPolesEnabled;
    private final @Nullable XMaterial overheadPoleBlock;
    private final @Nullable XMaterial overheadSupportBlock;
    private final int overheadPoleSpacing;
    private final int overheadPoleOffset;
    private final int overheadPoleHeight;
    private final boolean overheadWiresEnabled;
    private final @Nullable XMaterial overheadWireBlock;
    private final boolean trackSwitchesEnabled;
    private final boolean builtIn;

    RailType(Configuration configuration, boolean builtIn) {
        List<XMaterial> configuredBlocksBelow = configuration.blocksBelow();

        this.identifier = configuration.identifier();
        this.displayName = configuration.displayName();
        this.icon = configuration.icon();
        this.railBlock = configuration.railBlock();
        this.blocksBelow = configuredBlocksBelow == null ? List.of() : List.copyOf(configuredBlocksBelow);
        this.sleeperBlock = configuration.sleeperBlock();
        this.sleeperSpacing = configuration.sleeperSpacing();
        this.trackCount = configuration.trackCount();
        this.trackSpacing = configuration.trackSpacing();
        this.trackSpacings = normalizeTrackSpacings(
                configuration.trackSpacings(),
                configuration.trackCount(),
                configuration.trackSpacing()
        );
        this.overheadPolesEnabled = configuration.overheadPolesEnabled();
        this.overheadPoleBlock = configuration.overheadPoleBlock();
        this.overheadSupportBlock = configuration.overheadSupportBlock();
        this.overheadPoleSpacing = configuration.overheadPoleSpacing();
        this.overheadPoleOffset = configuration.overheadPoleOffset();
        this.overheadPoleHeight = configuration.overheadPoleHeight();
        this.overheadWiresEnabled = configuration.overheadWiresEnabled();
        this.overheadWireBlock = configuration.overheadWireBlock();
        this.trackSwitchesEnabled = configuration.trackSwitchesEnabled();
        this.builtIn = builtIn;
    }

    public boolean hasSleepers() {
        return sleeperSpacing > 0 && sleeperBlock != null;
    }

    public boolean hasOverheadPoles() {
        return overheadPolesEnabled && overheadPoleBlock != null;
    }

    public boolean hasOverheadWires() {
        return overheadWiresEnabled && overheadWireBlock != null;
    }

    /**
     * Creates a custom rail type with the rail block as menu icon.
     * Call {@link RailTypeManager#saveRailType(RailType)} to validate and persist it.
     */
    public static RailType createCustom(Configuration configuration) {
        return new RailType(configuration, false);
    }

    static RailType createStandard() {
        return new RailType(new Configuration()
                .identifier(STANDARD_IDENTIFIER)
                .displayName("Standard")
                .icon(XMaterial.RAIL)
                .railBlock(XMaterial.ANVIL)
                .blocksBelow(List.of(XMaterial.DEAD_FIRE_CORAL_BLOCK, XMaterial.STONE, XMaterial.COBBLESTONE))
                .sleeperBlock(XMaterial.SPRUCE_PLANKS)
                .sleeperSpacing(0)
                .trackCount(DEFAULT_TRACK_COUNT)
                .trackSpacing(DEFAULT_TRACK_SPACING)
                .trackSpacings(List.of(DEFAULT_TRACK_SPACING))
                .overheadPolesEnabled(true)
                .overheadPoleBlock(XMaterial.LIGHT_GRAY_CONCRETE)
                .overheadSupportBlock(XMaterial.LIGHT_GRAY_CONCRETE)
                .overheadPoleSpacing(DEFAULT_OVERHEAD_POLE_SPACING)
                .overheadPoleOffset(DEFAULT_OVERHEAD_POLE_OFFSET)
                .overheadPoleHeight(DEFAULT_OVERHEAD_POLE_HEIGHT)
                .overheadWiresEnabled(true)
                .overheadWireBlock(XMaterial.IRON_BARS)
                .trackSwitchesEnabled(false), true);
    }

    /**
     * Validates all configurable rail type values.
     *
     * @return A human-readable error message, or null if all values are valid.
     */
    public static @Nullable String validate(Configuration configuration) {
        String error = validateIdentifier(configuration.identifier());

        if (error != null)
            return error;

        error = validateDisplayName(configuration.displayName());

        if (error != null)
            return error;

        error = validateBlocks(configuration);

        if (error != null)
            return error;

        error = validateSleeper(configuration);

        if (error != null)
            return error;

        error = validateTracks(configuration);

        if (error != null)
            return error;

        return validateOverheadWires(configuration);
    }

    private static @Nullable String validateIdentifier(@Nullable String identifier) {
        if (!hasInvalidIdentifier(identifier))
            return null;

        return "Rail type name must be %s characters or less and may only contain letters, numbers, '-' and '_'."
                .formatted(MAX_IDENTIFIER_LENGTH);
    }

    private static @Nullable String validateDisplayName(@Nullable String displayName) {
        if (displayName != null && !displayName.isBlank() && displayName.length() <= MAX_DISPLAY_NAME_LENGTH)
            return null;

        return "Rail type display name must not be empty and must be %s characters or less."
                .formatted(MAX_DISPLAY_NAME_LENGTH);
    }

    private static @Nullable String validateBlocks(Configuration configuration) {
        if (!isPlaceableBlock(configuration.railBlock()))
            return "Rail block must be a valid placeable Minecraft block.";

        List<XMaterial> blocksBelow = configuration.blocksBelow();

        if (blocksBelow == null || blocksBelow.isEmpty())
            return "Rail type needs at least one block below the rails.";

        for (XMaterial blockBelow : blocksBelow)
            if (!isPlaceableBlock(blockBelow))
                return "Blocks below the rails must be valid placeable Minecraft blocks.";

        return null;
    }

    private static @Nullable String validateSleeper(Configuration configuration) {
        int sleeperSpacing = configuration.sleeperSpacing();

        if (sleeperSpacing < MIN_SLEEPER_SPACING || sleeperSpacing > MAX_SLEEPER_SPACING)
            return "Sleeper spacing must be between %s and %s. Use 0 to disable sleepers."
                    .formatted(MIN_SLEEPER_SPACING, MAX_SLEEPER_SPACING);

        if (sleeperSpacing > 0 && !isPlaceableBlock(configuration.sleeperBlock()))
            return "Sleeper block must be a valid placeable Minecraft block.";

        return null;
    }

    private static @Nullable String validateTracks(Configuration configuration) {
        int trackCount = configuration.trackCount();

        if (trackCount < MIN_TRACK_COUNT || trackCount > MAX_TRACK_COUNT)
            return "Track count must be between %s and %s.".formatted(MIN_TRACK_COUNT, MAX_TRACK_COUNT);

        int trackSpacing = configuration.trackSpacing();

        if (trackSpacing < MIN_TRACK_SPACING || trackSpacing > MAX_TRACK_SPACING)
            return "Track spacing must be between %s and %s.".formatted(MIN_TRACK_SPACING, MAX_TRACK_SPACING);

        List<Integer> trackSpacings = configuration.trackSpacings();

        if (trackSpacings == null)
            return null;

        if (trackSpacings.size() != Math.max(0, trackCount - 1))
            return "Rail type needs exactly one spacing value between every pair of tracks.";

        for (Integer spacing : trackSpacings)
            if (spacing == null || spacing < MIN_TRACK_SPACING || spacing > MAX_TRACK_SPACING)
                return "Every track spacing must be between %s and %s."
                        .formatted(MIN_TRACK_SPACING, MAX_TRACK_SPACING);

        return null;
    }

    private static List<Integer> normalizeTrackSpacings(@Nullable List<Integer> configured, int trackCount, int fallback) {
        int spacingCount = Math.max(0, trackCount - 1);

        if (configured != null && configured.size() == spacingCount)
            return List.copyOf(configured);

        return java.util.Collections.nCopies(spacingCount, fallback);
    }

    private static @Nullable String validateOverheadWires(Configuration configuration) {
        String poleError = validateOverheadPoles(configuration);

        if (poleError != null)
            return poleError;

        if (configuration.overheadWiresEnabled() && !isPlaceableBlock(configuration.overheadWireBlock()))
            return "Overhead wire block must be a valid placeable Minecraft block.";

        return null;
    }

    private static @Nullable String validateOverheadPoles(Configuration configuration) {
        if (!configuration.overheadPolesEnabled())
            return null;

        if (!isPlaceableBlock(configuration.overheadPoleBlock()))
            return "Overhead pole block must be a valid placeable Minecraft block.";

        if (!isPlaceableBlock(configuration.overheadSupportBlock()))
            return "Overhead support block must be a valid placeable Minecraft block.";

        return validateOverheadPoleMeasurements(configuration);
    }

    private static @Nullable String validateOverheadPoleMeasurements(Configuration configuration) {
        if (configuration.overheadPoleSpacing() < MIN_OVERHEAD_POLE_SPACING
                || configuration.overheadPoleSpacing() > MAX_OVERHEAD_POLE_SPACING)
            return "Overhead pole spacing must be between %s and %s."
                    .formatted(MIN_OVERHEAD_POLE_SPACING, MAX_OVERHEAD_POLE_SPACING);

        if (configuration.overheadPoleOffset() < MIN_OVERHEAD_POLE_OFFSET
                || configuration.overheadPoleOffset() > MAX_OVERHEAD_POLE_OFFSET)
            return "Overhead pole offset must be between %s and %s."
                    .formatted(MIN_OVERHEAD_POLE_OFFSET, MAX_OVERHEAD_POLE_OFFSET);

        if (configuration.overheadPoleHeight() < MIN_OVERHEAD_POLE_HEIGHT
                || configuration.overheadPoleHeight() > MAX_OVERHEAD_POLE_HEIGHT)
            return "Overhead pole height must be between %s and %s."
                    .formatted(MIN_OVERHEAD_POLE_HEIGHT, MAX_OVERHEAD_POLE_HEIGHT);

        return null;
    }

    private static boolean hasInvalidIdentifier(@Nullable String identifier) {
        return identifier == null
                || identifier.isBlank()
                || identifier.length() > MAX_IDENTIFIER_LENGTH
                || !identifier.toLowerCase().matches(IDENTIFIER_PATTERN);
    }

    private static boolean isPlaceableBlock(@Nullable XMaterial material) {
        if (material == null)
            return false;

        Material bukkitMaterial = material.get();
        return bukkitMaterial != null && bukkitMaterial.isBlock();
    }

    public static @Nullable RailType byString(String value) {
        Rail rail = GeneratorModule.getInstance().getRail();

        return rail == null ? null : rail.getRailTypeManager().byString(value);
    }

    public static RailType getDefault() {
        Rail rail = GeneratorModule.getInstance().getRail();

        return rail == null ? createStandard() : rail.getRailTypeManager().getDefault();
    }

    public static final class Configuration {

        private @Nullable String identifier;
        private @Nullable String displayName;
        private @Nullable XMaterial icon;
        private @Nullable XMaterial railBlock;
        private @Nullable List<XMaterial> blocksBelow;
        private @Nullable XMaterial sleeperBlock;
        private int sleeperSpacing;
        private int trackCount;
        private int trackSpacing;
        private @Nullable List<Integer> trackSpacings;
        private boolean overheadPolesEnabled;
        private @Nullable XMaterial overheadPoleBlock;
        private @Nullable XMaterial overheadSupportBlock;
        private int overheadPoleSpacing = DEFAULT_OVERHEAD_POLE_SPACING;
        private int overheadPoleOffset = DEFAULT_OVERHEAD_POLE_OFFSET;
        private int overheadPoleHeight = DEFAULT_OVERHEAD_POLE_HEIGHT;
        private boolean overheadWiresEnabled;
        private @Nullable XMaterial overheadWireBlock;
        private boolean trackSwitchesEnabled;

        public @Nullable String identifier() {
            return identifier;
        }

        public Configuration identifier(@Nullable String identifier) {
            this.identifier = identifier;
            return this;
        }

        public @Nullable String displayName() {
            return displayName;
        }

        public Configuration displayName(@Nullable String displayName) {
            this.displayName = displayName;
            return this;
        }

        public @Nullable XMaterial icon() {
            return icon;
        }

        public Configuration icon(@Nullable XMaterial icon) {
            this.icon = icon;
            return this;
        }

        public @Nullable XMaterial railBlock() {
            return railBlock;
        }

        public Configuration railBlock(@Nullable XMaterial railBlock) {
            this.railBlock = railBlock;
            return this;
        }

        public @Nullable List<XMaterial> blocksBelow() {
            return blocksBelow;
        }

        public Configuration blocksBelow(@Nullable List<XMaterial> blocksBelow) {
            this.blocksBelow = blocksBelow;
            return this;
        }

        public @Nullable XMaterial sleeperBlock() {
            return sleeperBlock;
        }

        public Configuration sleeperBlock(@Nullable XMaterial sleeperBlock) {
            this.sleeperBlock = sleeperBlock;
            return this;
        }

        public int sleeperSpacing() {
            return sleeperSpacing;
        }

        public Configuration sleeperSpacing(int sleeperSpacing) {
            this.sleeperSpacing = sleeperSpacing;
            return this;
        }

        public int trackCount() {
            return trackCount;
        }

        public Configuration trackCount(int trackCount) {
            this.trackCount = trackCount;
            return this;
        }

        public int trackSpacing() {
            return trackSpacing;
        }

        public Configuration trackSpacing(int trackSpacing) {
            this.trackSpacing = trackSpacing;
            return this;
        }

        public @Nullable List<Integer> trackSpacings() {
            return trackSpacings;
        }

        public Configuration trackSpacings(@Nullable List<Integer> trackSpacings) {
            this.trackSpacings = trackSpacings;
            return this;
        }

        public boolean overheadPolesEnabled() {
            return overheadPolesEnabled;
        }

        public Configuration overheadPolesEnabled(boolean overheadPolesEnabled) {
            this.overheadPolesEnabled = overheadPolesEnabled;
            return this;
        }

        public @Nullable XMaterial overheadPoleBlock() {
            return overheadPoleBlock;
        }

        public Configuration overheadPoleBlock(@Nullable XMaterial overheadPoleBlock) {
            this.overheadPoleBlock = overheadPoleBlock;
            return this;
        }

        public @Nullable XMaterial overheadSupportBlock() {
            return overheadSupportBlock;
        }

        public Configuration overheadSupportBlock(@Nullable XMaterial overheadSupportBlock) {
            this.overheadSupportBlock = overheadSupportBlock;
            return this;
        }

        public int overheadPoleSpacing() {
            return overheadPoleSpacing;
        }

        public Configuration overheadPoleSpacing(int overheadPoleSpacing) {
            this.overheadPoleSpacing = overheadPoleSpacing;
            return this;
        }

        public int overheadPoleOffset() {
            return overheadPoleOffset;
        }

        public Configuration overheadPoleOffset(int overheadPoleOffset) {
            this.overheadPoleOffset = overheadPoleOffset;
            return this;
        }

        public int overheadPoleHeight() {
            return overheadPoleHeight;
        }

        public Configuration overheadPoleHeight(int overheadPoleHeight) {
            this.overheadPoleHeight = overheadPoleHeight;
            return this;
        }

        public boolean overheadWiresEnabled() {
            return overheadWiresEnabled;
        }

        public Configuration overheadWiresEnabled(boolean overheadWiresEnabled) {
            this.overheadWiresEnabled = overheadWiresEnabled;
            return this;
        }

        public @Nullable XMaterial overheadWireBlock() {
            return overheadWireBlock;
        }

        public Configuration overheadWireBlock(@Nullable XMaterial overheadWireBlock) {
            this.overheadWireBlock = overheadWireBlock;
            return this;
        }

        public boolean trackSwitchesEnabled() {
            return trackSwitchesEnabled;
        }

        public Configuration trackSwitchesEnabled(boolean trackSwitchesEnabled) {
            this.trackSwitchesEnabled = trackSwitchesEnabled;
            return this;
        }
    }
}

package net.buildtheearth.buildteamtools.modules.generator.components.rail.configuration;

import com.alpsbte.alpslib.utils.ChatHelper;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Holds all available rail types and persists player-created rail types to disk.
 * The built-in standard rail type is always available and cannot be overwritten or deleted.
 */
public class RailTypeManager {

    private static final String FILE_PATH = "modules/generator/rail-types.yml";
    private static final String SCHEMA_VERSION_KEY = "schema-version";
    static final int CURRENT_SCHEMA_VERSION = 5;
    private static final String TYPES_SECTION = "rail-types";

    private static final String KEY_DISPLAY_NAME = "display-name";
    private static final String KEY_ICON = "icon";
    private static final String KEY_RAIL_BLOCK = "rail-block";
    private static final String KEY_BLOCKS_BELOW = "blocks-below";
    private static final String KEY_SLEEPER_BLOCK = "sleeper-block";
    private static final String KEY_SLEEPER_SPACING = "sleeper-spacing";
    private static final String KEY_TRACK_COUNT = "track-count";
    private static final String KEY_TRACK_SPACING = "track-spacing";
    private static final String KEY_TRACK_SPACINGS = "track-spacings";
    private static final String KEY_OVERHEAD_POLES_ENABLED = "overhead.poles.enabled";
    private static final String KEY_OVERHEAD_POLE_BLOCK = "overhead.poles.block";
    private static final String KEY_OVERHEAD_SUPPORT_BLOCK = "overhead.support.block";
    private static final String KEY_OVERHEAD_POLE_SPACING = "overhead.poles.spacing";
    private static final String KEY_OVERHEAD_POLE_OFFSET = "overhead.poles.offset";
    private static final String KEY_OVERHEAD_POLE_HEIGHT = "overhead.poles.height";
    private static final String KEY_OVERHEAD_WIRES_ENABLED = "overhead.wires.enabled";
    private static final String KEY_OVERHEAD_WIRE_BLOCK = "overhead.wires.block";
    private static final String KEY_TRACK_SWITCHES_ENABLED = "track-switches.enabled";

    private final Map<String, RailType> railTypes = new LinkedHashMap<>();
    private final File file;

    public RailTypeManager(File dataFolder) {
        this.file = new File(dataFolder, FILE_PATH);
        reload();
    }

    public void reload() {
        railTypes.clear();

        RailType standard = RailType.createStandard();
        railTypes.put(standard.getIdentifier(), standard);

        if (!file.exists())
            return;

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        migrateStorage(config);
        ConfigurationSection typesSection = config.getConfigurationSection(TYPES_SECTION);

        if (typesSection == null)
            return;

        for (String identifier : typesSection.getKeys(false))
            loadRailType(typesSection.getConfigurationSection(identifier), identifier);
    }

    public Collection<RailType> getRailTypes() {
        return Collections.unmodifiableCollection(railTypes.values());
    }

    public RailType getDefault() {
        return railTypes.get(RailType.STANDARD_IDENTIFIER);
    }

    /**
     * @return The first free "custom-N" identifier for a new player-created rail type.
     */
    public String getNextCustomIdentifier() {
        int index = 1;

        while (railTypes.containsKey("custom-" + index))
            index++;

        return "custom-" + index;
    }

    /** Names are allocated separately from stable identifiers, so renaming frees a display name. */
    public String getNextCustomDisplayName() {
        return getNextCustomDisplayName(railTypes.values().stream().map(RailType::getDisplayName).toList());
    }

    static String getNextCustomDisplayName(Collection<String> displayNames) {
        Set<String> names = new HashSet<>();
        for (String displayName : displayNames)
            names.add(displayName.toLowerCase(Locale.ROOT));

        int index = 1;
        while (names.contains("custom rail " + index))
            index++;
        return "Custom Rail " + index;
    }

    public @Nullable RailType byString(@Nullable String value) {
        if (value == null)
            return null;

        RailType byIdentifier = railTypes.get(value.toLowerCase(Locale.ROOT));

        if (byIdentifier != null)
            return byIdentifier;

        for (RailType railType : railTypes.values())
            if (railType.getDisplayName().equalsIgnoreCase(value))
                return railType;

        return null;
    }

    /**
     * Validates and stores a custom rail type and saves it to disk.
     *
     * @return A human-readable error message, or null if the rail type was saved successfully.
     */
    public @Nullable String saveRailType(RailType railType) {
        String error = RailType.validate(toConfiguration(railType));

        if (error != null)
            return error;

        RailType existingRailType = railTypes.get(railType.getIdentifier());

        if (existingRailType != null && existingRailType.isBuiltIn())
            return "The rail type '%s' is built-in and cannot be overwritten.".formatted(railType.getIdentifier());

        railTypes.put(railType.getIdentifier(), railType);
        String saveError = save();

        if (saveError == null)
            return null;

        if (existingRailType == null)
            railTypes.remove(railType.getIdentifier());
        else
            railTypes.put(existingRailType.getIdentifier(), existingRailType);

        return saveError;
    }

    /**
     * Deletes a custom rail type and saves the change to disk.
     *
     * @return A human-readable error message, or null if the rail type was deleted successfully.
     */
    public @Nullable String deleteRailType(String identifier) {
        RailType railType = railTypes.get(identifier.toLowerCase(Locale.ROOT));

        if (railType == null)
            return "The rail type '%s' does not exist.".formatted(identifier);

        if (railType.isBuiltIn())
            return "The rail type '%s' is built-in and cannot be deleted.".formatted(identifier);

        railTypes.remove(railType.getIdentifier());
        String saveError = save();

        if (saveError == null)
            return null;

        railTypes.put(railType.getIdentifier(), railType);
        return saveError;
    }

    /**
     * Deletes multiple custom types atomically from the in-memory collection and
     * writes the storage file once. Built-in or missing identifiers abort the operation.
     */
    public @Nullable String deleteRailTypes(Collection<String> identifiers) {
        List<RailType> typesToDelete = new ArrayList<>();

        for (String identifier : identifiers) {
            RailType railType = railTypes.get(identifier.toLowerCase(Locale.ROOT));

            if (railType == null)
                return "The rail type '%s' does not exist.".formatted(identifier);

            if (railType.isBuiltIn())
                return "The rail type '%s' is built-in and cannot be deleted.".formatted(identifier);

            typesToDelete.add(railType);
        }

        for (RailType railType : typesToDelete)
            railTypes.remove(railType.getIdentifier());

        String error = save();

        if (error == null)
            return null;

        for (RailType railType : typesToDelete)
            railTypes.put(railType.getIdentifier(), railType);

        return error;
    }

    private void loadRailType(@Nullable ConfigurationSection section, String identifier) {
        if (section == null)
            return;

        String normalizedIdentifier = identifier.toLowerCase(Locale.ROOT);
        String displayName = section.getString(KEY_DISPLAY_NAME, identifier);
        List<XMaterial> railBlocks = parsePalette(section.get(KEY_RAIL_BLOCK));
        List<XMaterial> blocksBelow = parseMaterials(section.getStringList(KEY_BLOCKS_BELOW));
        List<XMaterial> sleeperBlocks = parsePalette(section.get(KEY_SLEEPER_BLOCK));
        int sleeperSpacing = section.getInt(KEY_SLEEPER_SPACING, RailType.MIN_SLEEPER_SPACING);
        int trackCount = section.getInt(KEY_TRACK_COUNT, RailType.DEFAULT_TRACK_COUNT);
        int trackSpacing = section.getInt(KEY_TRACK_SPACING, RailType.DEFAULT_TRACK_SPACING);
        List<Integer> trackSpacings = section.contains(KEY_TRACK_SPACINGS)
                ? section.getIntegerList(KEY_TRACK_SPACINGS)
                : Collections.nCopies(Math.max(0, trackCount - 1), trackSpacing);
        boolean overheadPolesEnabled = section.getBoolean(KEY_OVERHEAD_POLES_ENABLED, false);
        List<XMaterial> overheadPoleBlocks = parsePalette(section.get(KEY_OVERHEAD_POLE_BLOCK));
        List<XMaterial> overheadSupportBlocks = parsePalette(section.get(KEY_OVERHEAD_SUPPORT_BLOCK));
        int overheadPoleSpacing = section.getInt(
                KEY_OVERHEAD_POLE_SPACING,
                RailType.DEFAULT_OVERHEAD_POLE_SPACING
        );
        int overheadPoleOffset = section.getInt(
                KEY_OVERHEAD_POLE_OFFSET,
                RailType.DEFAULT_OVERHEAD_POLE_OFFSET
        );
        int overheadPoleHeight = section.getInt(
                KEY_OVERHEAD_POLE_HEIGHT,
                RailType.DEFAULT_OVERHEAD_POLE_HEIGHT
        );
        boolean overheadWiresEnabled = section.getBoolean(KEY_OVERHEAD_WIRES_ENABLED, false);
        List<XMaterial> overheadWireBlocks = parsePalette(section.get(KEY_OVERHEAD_WIRE_BLOCK));
        boolean trackSwitchesEnabled = section.getBoolean(KEY_TRACK_SWITCHES_ENABLED, false);

        String error = RailType.validate(new RailType.Configuration()
                .identifier(normalizedIdentifier)
                .displayName(displayName)
                .icon(railBlocks.isEmpty() ? null : railBlocks.getFirst())
                .railBlocks(railBlocks)
                .blocksBelow(blocksBelow)
                .sleeperBlocks(sleeperBlocks)
                .sleeperSpacing(sleeperSpacing)
                .trackCount(trackCount)
                .trackSpacing(trackSpacing)
                .trackSpacings(trackSpacings)
                .overheadPolesEnabled(overheadPolesEnabled)
                .overheadPoleBlocks(overheadPoleBlocks)
                .overheadSupportBlocks(overheadSupportBlocks)
                .overheadPoleSpacing(overheadPoleSpacing)
                .overheadPoleOffset(overheadPoleOffset)
                .overheadPoleHeight(overheadPoleHeight)
                .overheadWiresEnabled(overheadWiresEnabled)
                .overheadWireBlocks(overheadWireBlocks)
                .trackSwitchesEnabled(trackSwitchesEnabled));

        if (error != null) {
            ChatHelper.logError("Skipping invalid rail type '%s' in %s: %s", identifier, FILE_PATH, error);
            return;
        }

        if (railTypes.containsKey(normalizedIdentifier)) {
            ChatHelper.logError("Skipping duplicate rail type '%s' in %s.", identifier, FILE_PATH);
            return;
        }

        XMaterial icon = parseMaterial(section.getString(KEY_ICON));

        railTypes.put(normalizedIdentifier, new RailType(new RailType.Configuration()
                .identifier(normalizedIdentifier)
                .displayName(displayName)
                .icon(icon == null ? railBlocks.getFirst() : icon)
                .railBlocks(railBlocks)
                .blocksBelow(blocksBelow)
                .sleeperBlocks(sleeperBlocks)
                .sleeperSpacing(sleeperSpacing)
                .trackCount(trackCount)
                .trackSpacing(trackSpacing)
                .trackSpacings(trackSpacings)
                .overheadPolesEnabled(overheadPolesEnabled)
                .overheadPoleBlocks(overheadPoleBlocks)
                .overheadSupportBlocks(overheadSupportBlocks)
                .overheadPoleSpacing(overheadPoleSpacing)
                .overheadPoleOffset(overheadPoleOffset)
                .overheadPoleHeight(overheadPoleHeight)
                .overheadWiresEnabled(overheadWiresEnabled)
                .overheadWireBlocks(overheadWireBlocks)
                .trackSwitchesEnabled(trackSwitchesEnabled), false));
    }

    private @Nullable String save() {
        YamlConfiguration config = new YamlConfiguration();
        config.set(SCHEMA_VERSION_KEY, CURRENT_SCHEMA_VERSION);

        for (RailType railType : railTypes.values()) {
            if (railType.isBuiltIn())
                continue;

            String path = TYPES_SECTION + "." + railType.getIdentifier();

            config.set(path + "." + KEY_DISPLAY_NAME, railType.getDisplayName());
            config.set(path + "." + KEY_ICON, railType.getIcon().name());
            config.set(path + "." + KEY_RAIL_BLOCK, railType.getRailBlocks().stream().map(XMaterial::name).toList());
            config.set(path + "." + KEY_BLOCKS_BELOW, railType.getBlocksBelow().stream().map(XMaterial::name).toList());

            config.set(path + "." + KEY_SLEEPER_BLOCK, railType.getSleeperBlocks().stream().map(XMaterial::name).toList());

            config.set(path + "." + KEY_SLEEPER_SPACING, railType.getSleeperSpacing());
            config.set(path + "." + KEY_TRACK_COUNT, railType.getTrackCount());
            config.set(path + "." + KEY_TRACK_SPACING, railType.getTrackSpacing());
            config.set(path + "." + KEY_TRACK_SPACINGS, railType.getTrackSpacings());
            config.set(path + "." + KEY_OVERHEAD_POLES_ENABLED, railType.isOverheadPolesEnabled());
            config.set(path + "." + KEY_OVERHEAD_POLE_SPACING, railType.getOverheadPoleSpacing());
            config.set(path + "." + KEY_OVERHEAD_POLE_OFFSET, railType.getOverheadPoleOffset());
            config.set(path + "." + KEY_OVERHEAD_POLE_HEIGHT, railType.getOverheadPoleHeight());
            config.set(path + "." + KEY_OVERHEAD_WIRES_ENABLED, railType.isOverheadWiresEnabled());
            config.set(path + "." + KEY_TRACK_SWITCHES_ENABLED, railType.isTrackSwitchesEnabled());

            config.set(path + "." + KEY_OVERHEAD_POLE_BLOCK, railType.getOverheadPoleBlocks().stream().map(XMaterial::name).toList());

            config.set(path + "." + KEY_OVERHEAD_SUPPORT_BLOCK, railType.getOverheadSupportBlocks().stream().map(XMaterial::name).toList());

            config.set(path + "." + KEY_OVERHEAD_WIRE_BLOCK, railType.getOverheadWireBlocks().stream().map(XMaterial::name).toList());
        }

        try {
            if (file.getParentFile() != null && !file.getParentFile().exists() && !file.getParentFile().mkdirs())
                throw new IOException("Could not create directory " + file.getParentFile());

            config.save(file);
            return null;
        } catch (IOException exception) {
            ChatHelper.logError("Could not save rail types to %s.", exception, FILE_PATH);
            return "Rail types could not be saved. Please check the server console.";
        }
    }

    private @Nullable XMaterial parseMaterial(@Nullable String value) {
        if (value == null || value.isBlank())
            return null;

        return XMaterial.matchXMaterial(value).orElse(null);
    }

    private List<XMaterial> parseMaterials(List<String> values) {
        List<XMaterial> materials = new ArrayList<>();

        for (String value : values) {
            XMaterial material = parseMaterial(value);

            // Keep invalid entries as null so validation reports them instead of silently dropping them.
            materials.add(material);
        }

        return materials;
    }

    private void migrateStorage(YamlConfiguration config) {
        int schemaVersion = config.getInt(SCHEMA_VERSION_KEY, 1);

        if (schemaVersion > CURRENT_SCHEMA_VERSION) {
            ChatHelper.logError(
                    "Rail type storage %s uses unsupported schema version %s. Loading known fields without rewriting it.",
                    FILE_PATH,
                    schemaVersion
            );
            return;
        }

        if (schemaVersion == CURRENT_SCHEMA_VERSION)
            return;

        migrateVersionOneToTwo(config);
        migrateVersionTwoToThree(config);
        migrateVersionThreeToFour(config);
        migrateVersionFourToFive(config);
        config.set(SCHEMA_VERSION_KEY, CURRENT_SCHEMA_VERSION);

        try {
            config.save(file);
        } catch (IOException exception) {
            ChatHelper.logError("Could not migrate rail types in %s.", exception, FILE_PATH);
        }
    }

    private void migrateVersionOneToTwo(YamlConfiguration config) {
        ConfigurationSection typesSection = config.getConfigurationSection(TYPES_SECTION);

        if (typesSection == null)
            return;

        for (String identifier : typesSection.getKeys(false)) {
            ConfigurationSection section = typesSection.getConfigurationSection(identifier);

            if (section == null)
                continue;

            setDefault(section, KEY_OVERHEAD_POLES_ENABLED, false);
            setDefault(section, KEY_OVERHEAD_POLE_BLOCK, XMaterial.LIGHT_GRAY_CONCRETE.name());
            setDefault(section, KEY_OVERHEAD_POLE_SPACING, RailType.DEFAULT_OVERHEAD_POLE_SPACING);
            setDefault(section, KEY_OVERHEAD_POLE_OFFSET, RailType.DEFAULT_OVERHEAD_POLE_OFFSET);
            setDefault(section, KEY_OVERHEAD_POLE_HEIGHT, RailType.DEFAULT_OVERHEAD_POLE_HEIGHT);
            setDefault(section, KEY_OVERHEAD_WIRES_ENABLED, false);
            setDefault(section, KEY_OVERHEAD_WIRE_BLOCK, XMaterial.IRON_BARS.name());
            setDefault(section, KEY_TRACK_SWITCHES_ENABLED, false);
        }
    }

    private void migrateVersionTwoToThree(YamlConfiguration config) {
        ConfigurationSection typesSection = config.getConfigurationSection(TYPES_SECTION);

        if (typesSection == null)
            return;

        for (String identifier : typesSection.getKeys(false)) {
            ConfigurationSection section = typesSection.getConfigurationSection(identifier);

            if (section == null)
                continue;

            setDefault(section, KEY_OVERHEAD_SUPPORT_BLOCK, section.getString(
                    KEY_OVERHEAD_POLE_BLOCK,
                    XMaterial.LIGHT_GRAY_CONCRETE.name()
            ));
        }
    }

    private void migrateVersionThreeToFour(YamlConfiguration config) {
        ConfigurationSection typesSection = config.getConfigurationSection(TYPES_SECTION);

        if (typesSection == null)
            return;

        for (String identifier : typesSection.getKeys(false)) {
            ConfigurationSection section = typesSection.getConfigurationSection(identifier);

            if (section == null || section.contains(KEY_TRACK_SPACINGS))
                continue;

            int trackCount = section.getInt(KEY_TRACK_COUNT, RailType.DEFAULT_TRACK_COUNT);
            int trackSpacing = section.getInt(KEY_TRACK_SPACING, RailType.DEFAULT_TRACK_SPACING);
            section.set(KEY_TRACK_SPACINGS, Collections.nCopies(Math.max(0, trackCount - 1), trackSpacing));
        }
    }

    static List<XMaterial> parsePalette(Object value) {
        if (value == null)
            return List.of();
        List<?> values = value instanceof List<?> list ? list : List.of(value);
        List<XMaterial> materials = new ArrayList<>();
        for (Object entry : values)
            materials.add(entry instanceof String name ? XMaterial.matchXMaterial(name).orElse(null) : null);
        return materials;
    }

    static void migrateVersionFourToFive(YamlConfiguration config) {
        ConfigurationSection types = config.getConfigurationSection(TYPES_SECTION);
        if (types == null)
            return;
        for (String identifier : types.getKeys(false)) {
            ConfigurationSection section = types.getConfigurationSection(identifier);
            if (section == null)
                continue;
            for (String key : List.of(KEY_RAIL_BLOCK, KEY_SLEEPER_BLOCK, KEY_OVERHEAD_POLE_BLOCK,
                    KEY_OVERHEAD_SUPPORT_BLOCK, KEY_OVERHEAD_WIRE_BLOCK)) {
                Object value = section.get(key);
                if (value instanceof String)
                    section.set(key, List.of(value));
            }
        }
    }
    private void setDefault(ConfigurationSection section, String path, Object value) {
        if (!section.contains(path))
            section.set(path, value);
    }

    static RailType.Configuration toConfiguration(RailType railType) {
        return new RailType.Configuration()
                .identifier(railType.getIdentifier())
                .displayName(railType.getDisplayName())
                .icon(railType.getIcon())
                .railBlocks(railType.getRailBlocks())
                .blocksBelow(railType.getBlocksBelow())
                .sleeperBlocks(railType.getSleeperBlocks())
                .sleeperSpacing(railType.getSleeperSpacing())
                .trackCount(railType.getTrackCount())
                .trackSpacing(railType.getTrackSpacing())
                .trackSpacings(railType.getTrackSpacings())
                .overheadPolesEnabled(railType.isOverheadPolesEnabled())
                .overheadPoleBlocks(railType.getOverheadPoleBlocks())
                .overheadSupportBlocks(railType.getOverheadSupportBlocks())
                .overheadPoleSpacing(railType.getOverheadPoleSpacing())
                .overheadPoleOffset(railType.getOverheadPoleOffset())
                .overheadPoleHeight(railType.getOverheadPoleHeight())
                .overheadWiresEnabled(railType.isOverheadWiresEnabled())
                .overheadWireBlocks(railType.getOverheadWireBlocks())
                .trackSwitchesEnabled(railType.isTrackSwitchesEnabled());
    }
}

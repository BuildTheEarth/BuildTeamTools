package net.buildtheearth.buildteamtools.modules.generator.components.rail;

import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.utils.io.ConfigPaths;
import net.buildtheearth.buildteamtools.utils.io.ConfigUtil;
import org.bukkit.configuration.file.FileConfiguration;

record RailLimits(
        int maxControlPoints,
        int maxPathPoints,
        int maxBlockPlacements,
        long maxPreparedRegionVolume,
        int maxPreparedRegionAxisLength,
        int blockPlacementBatchSize
) {

    // Default amount of WorldEdit control points accepted before path interpolation starts.
    private static final int DEFAULT_MAX_CONTROL_POINTS = 1_000;
    // Default amount of interpolated center-path blocks a rail may contain.
    private static final int DEFAULT_MAX_PATH_POINTS = 24_000;
    // Default amount of final center, side and support blocks queued for placement.
    private static final int DEFAULT_MAX_BLOCK_PLACEMENTS = 150_000;
    // Default volume of the prepared terrain lookup region around the selected rail path.
    private static final long DEFAULT_MAX_PREPARED_REGION_VOLUME = 1_500_000L;
    // Default maximum width, height or depth of the prepared terrain lookup region.
    private static final int DEFAULT_MAX_PREPARED_REGION_AXIS_LENGTH = 1_024;
    // Default amount of block changes applied per scheduler tick during execution.
    private static final int DEFAULT_BLOCK_PLACEMENT_BATCH_SIZE = 750;

    // Hard cap for control points from config to prevent extreme path fan-out.
    private static final int MAX_CONTROL_POINTS = 2_000;
    // Hard cap for interpolated path points to keep path building bounded.
    private static final int MAX_PATH_POINTS = 75_000;
    // Hard cap for final block placements to protect memory and undo history.
    private static final int MAX_BLOCK_PLACEMENTS = 300_000;
    // Hard cap for terrain lookup volume to avoid preparing very large selections.
    private static final long MAX_PREPARED_REGION_VOLUME = 6_000_000L;
    // Hard cap for one prepared-region axis to avoid long, thin scans over huge distances.
    private static final int MAX_PREPARED_REGION_AXIS_LENGTH = 2_048;
    // Hard cap for per-tick placement batches to keep the server responsive.
    private static final int MAX_BLOCK_PLACEMENT_BATCH_SIZE = 2_000;

    static RailLimits fromConfig() {
        FileConfiguration config = BuildTeamTools.getInstance().getConfig(ConfigUtil.GENERATOR);

        return new RailLimits(
                getBoundedInt(config, ConfigPaths.Generator.Rail.MAX_CONTROL_POINTS, DEFAULT_MAX_CONTROL_POINTS, 2, MAX_CONTROL_POINTS),
                getBoundedInt(config, ConfigPaths.Generator.Rail.MAX_PATH_POINTS, DEFAULT_MAX_PATH_POINTS, 2, MAX_PATH_POINTS),
                getBoundedInt(config, ConfigPaths.Generator.Rail.MAX_BLOCK_PLACEMENTS, DEFAULT_MAX_BLOCK_PLACEMENTS, 1, MAX_BLOCK_PLACEMENTS),
                getBoundedLong(config, ConfigPaths.Generator.Rail.MAX_PREPARED_REGION_VOLUME, DEFAULT_MAX_PREPARED_REGION_VOLUME, 1L, MAX_PREPARED_REGION_VOLUME),
                getBoundedInt(config, ConfigPaths.Generator.Rail.MAX_PREPARED_REGION_AXIS_LENGTH, DEFAULT_MAX_PREPARED_REGION_AXIS_LENGTH, 1, MAX_PREPARED_REGION_AXIS_LENGTH),
                getBoundedInt(config, ConfigPaths.Generator.Rail.BLOCK_PLACEMENT_BATCH_SIZE, DEFAULT_BLOCK_PLACEMENT_BATCH_SIZE, 1, MAX_BLOCK_PLACEMENT_BATCH_SIZE)
        );
    }

    private static int getBoundedInt(FileConfiguration config, String path, int fallback, int minimum, int maximum) {
        return Math.max(minimum, Math.min(maximum, config.getInt(path, fallback)));
    }

    private static long getBoundedLong(FileConfiguration config, String path, long fallback, long minimum, long maximum) {
        return Math.max(minimum, Math.min(maximum, config.getLong(path, fallback)));
    }
}

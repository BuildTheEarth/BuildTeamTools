package net.buildtheearth.buildteamtools.modules.generator.components.rail.generation;

import net.buildtheearth.buildteamtools.utils.io.GeneratorConfig;

record RailLimits(
        int maxControlPoints,
        int maxPathPoints,
        int maxBlockPlacements,
        long maxPreparedRegionVolume,
        int maxPreparedRegionAxisLength,
        int blockPlacementBatchSize
) {

    // Default amount of WorldEdit control points accepted before path interpolation starts.
    private static final int DEFAULT_MAX_CONTROL_POINTS = 2_000;
    // Default amount of interpolated center-path blocks a rail may contain.
    private static final int DEFAULT_MAX_PATH_POINTS = 75_000;
    // Default amount of final center, side and support blocks queued for placement.
    private static final int DEFAULT_MAX_BLOCK_PLACEMENTS = 300_000;
    // Default volume of the prepared terrain lookup region around the selected rail path.
    private static final long DEFAULT_MAX_PREPARED_REGION_VOLUME = 6_000_000L;
    // Default maximum width, height or depth of the prepared terrain lookup region.
    private static final int DEFAULT_MAX_PREPARED_REGION_AXIS_LENGTH = 2_048;
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

    static RailLimits fromConfig(GeneratorConfig config) {

        return new RailLimits(
                getBoundedInt(
                        config.rail().maxControlPoints(),
                        DEFAULT_MAX_CONTROL_POINTS,
                        2,
                        MAX_CONTROL_POINTS
                ),
                getBoundedInt(
                        config.rail().maxPathPoints(),
                        DEFAULT_MAX_PATH_POINTS,
                        2,
                        MAX_PATH_POINTS
                ),
                getBoundedInt(
                        config.rail().maxBlockPlacements(),
                        DEFAULT_MAX_BLOCK_PLACEMENTS,
                        1,
                        MAX_BLOCK_PLACEMENTS
                ),
                getBoundedLong(
                        config.rail().maxPreparedRegionVolume(),
                        DEFAULT_MAX_PREPARED_REGION_VOLUME,
                        1L,
                        MAX_PREPARED_REGION_VOLUME
                ),
                getBoundedInt(
                        config.rail().maxPreparedRegionAxisLength(),
                        DEFAULT_MAX_PREPARED_REGION_AXIS_LENGTH,
                        1,
                        MAX_PREPARED_REGION_AXIS_LENGTH
                ),
                getBoundedInt(
                        config.rail().blockPlacementBatchSize(),
                        DEFAULT_BLOCK_PLACEMENT_BATCH_SIZE,
                        1,
                        MAX_BLOCK_PLACEMENT_BATCH_SIZE
                )
        );
    }

    private static int getBoundedInt(int value, int fallback, int minimum, int maximum) {
        return Math.clamp(value == 0 ? fallback : value, minimum, maximum);
    }

    private static long getBoundedLong(long value, long fallback, long minimum, long maximum) {
        return Math.clamp(value == 0 ? fallback : value, minimum, maximum);
    }
}

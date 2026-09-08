package net.buildtheearth.buildteamtools.modules.generator.components.rail.generation;

import com.alpsbte.alpslib.utils.ChatHelper;
import com.alpsbte.alpslib.utils.GeneratorUtils;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BlockState;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.RailFlag;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.RailSettings;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.configuration.RailType;
import net.buildtheearth.buildteamtools.modules.generator.model.GeneratorComponent;
import net.buildtheearth.buildteamtools.modules.generator.model.Script;
import net.buildtheearth.buildteamtools.modules.generator.model.Settings;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RailScripts extends Script {

    private static final int SELECTION_PADDING = 4;
    private static final int SELECTION_VERTICAL_PADDING = RailType.MAX_OVERHEAD_POLE_HEIGHT + 2;
    private static final int PREPARE_SELECTION_EXPANSION = 8;

    private static final long BLOCK_PLACEMENT_START_PERCENTAGE = 95L;
    private static final long PROGRESS_UPDATE_INTERVAL_TICKS = 4L;

    private static final long CONTROL_POINTS_PROGRESS = 3L;
    private static final long PATH_PROGRESS = 10L;
    private static final long SAFETY_CHECK_PROGRESS = 18L;
    private static final long TERRAIN_PREPARE_PROGRESS = 68L;
    private static final long TERRAIN_ADJUST_PROGRESS = 78L;
    private static final long RAIL_BLOCK_BUILD_PROGRESS = 92L;
    private static final long QUEUE_OPERATIONS_PROGRESS = BLOCK_PLACEMENT_START_PERCENTAGE;

    private static final long CONTROL_POINTS_ESTIMATED_MILLIS = 500L;
    private static final long PATH_ESTIMATED_MILLIS = 750L;
    private static final long SAFETY_CHECK_ESTIMATED_MILLIS = 500L;
    private static final long TERRAIN_PREPARE_ESTIMATED_MILLIS = 4_500L;
    private static final long TERRAIN_ADJUST_ESTIMATED_MILLIS = 1_200L;
    private static final long RAIL_BLOCK_BUILD_ESTIMATED_MILLIS = 1_800L;
    private static final long QUEUE_OPERATIONS_ESTIMATED_MILLIS = 300L;

    private Block[][][] blocks;
    private List<Vector> controlPoints = new ArrayList<>();
    private List<Vector> centerPath = new ArrayList<>();
    private RailTerrainResolver terrainResolver;
    private RailType railType = RailType.getDefault();
    private int trackCount = RailType.DEFAULT_TRACK_COUNT;
    private List<Integer> trackSpacings = List.of(RailType.DEFAULT_TRACK_SPACING);
    private final RailLimits limits;
    private final RailPreparationProgress preparationProgress;
    private final Runnable preparationFinishedCallback;
    private int railReferenceY;
    private Region generationRegion;

    public RailScripts(Player player, GeneratorComponent generatorComponent, Runnable preparationFinishedCallback) {
        super(player, generatorComponent);
        this.limits = RailLimits.fromConfig();
        this.preparationProgress = new RailPreparationProgress(
                player,
                BLOCK_PLACEMENT_START_PERCENTAGE,
                PROGRESS_UPDATE_INTERVAL_TICKS
        );
        this.preparationFinishedCallback = preparationFinishedCallback;

        preparationProgress.start();
        preparationProgress.startStage(0L, CONTROL_POINTS_PROGRESS, CONTROL_POINTS_ESTIMATED_MILLIS);
        sendRailInfo("Rail Generator is validating your selection...");

        Bukkit.getScheduler().runTaskAsynchronously(BuildTeamTools.getInstance(), () -> {
            boolean queuedGeneration = false;

            try {
                if (!canContinue()) return;
                if (!prepareSession()) return;

                queuedGeneration = queueRailGeneration();
            } catch (Exception exception) {
                getGeneratorComponent().sendError(getPlayer());
                ChatHelper.logError("Rail Generator failed while preparing or generating.", exception);
            } finally {
                if (!queuedGeneration) {
                    runOnMainThread(() -> {
                        preparationProgress.stop();
                        runPreparationFinishedCallbackSafely();
                    });
                }
            }
        });
    }

    private boolean prepareSession() {
        if (!canContinue()) return false;

        railType = getRailType();

        if (!resolveTrackLayout()) return false;

        controlPoints = getControlPoints();
        railReferenceY = getRailReferenceY(controlPoints);
        preparationProgress.completeStage(CONTROL_POINTS_PROGRESS);

        if (!hasValidControlPoints()) return false;

        preparationProgress.startStage(CONTROL_POINTS_PROGRESS, PATH_PROGRESS, PATH_ESTIMATED_MILLIS);
        List<Vector> railSelectionPoints = createRailSelectionPoints(controlPoints);
        centerPath = createCenterPath(controlPoints);
        preparationProgress.completeStage(PATH_PROGRESS);

        if (!hasValidCenterPath()) return false;

        preparationProgress.startStage(PATH_PROGRESS, SAFETY_CHECK_PROGRESS, SAFETY_CHECK_ESTIMATED_MILLIS);

        int selectionMinY = getSelectionMinY(controlPoints);
        int selectionMaxY = getSelectionMaxY(controlPoints);

        if (!hasSafePreparedSelection(railSelectionPoints, selectionMinY, selectionMaxY)) return false;

        preparationProgress.completeStage(SAFETY_CHECK_PROGRESS);
        sendRailInfo("Rail Generator is preparing terrain data...");
        preparationProgress.startStage(SAFETY_CHECK_PROGRESS, TERRAIN_PREPARE_PROGRESS, TERRAIN_PREPARE_ESTIMATED_MILLIS);

        GeneratorUtils.createPolySelection(
                getPlayer(),
                railSelectionPoints,
                selectionMinY,
                selectionMaxY
        );
        generationRegion = GeneratorUtils.getWorldEditSelection(getPlayer());

        blocks = GeneratorUtils.prepareScriptSession(
                localSession,
                actor,
                getPlayer(),
                weWorld,
                PREPARE_SELECTION_EXPANSION,
                true,
                false,
                false
        );
        if (blocks == null) {
            sendRailError("Region not readable. Please report this to the developers of the BuildTeamTool plugin.");
            return false;
        }

        terrainResolver = new RailTerrainResolver(blocks);
        preparationProgress.completeStage(TERRAIN_PREPARE_PROGRESS);

        if (!canContinue()) return false;

        preparationProgress.startStage(TERRAIN_PREPARE_PROGRESS, TERRAIN_ADJUST_PROGRESS, TERRAIN_ADJUST_ESTIMATED_MILLIS);
        snapMissingControlPointHeightsToTerrain(controlPoints);
        centerPath = createCenterPath(controlPoints);
        adjustCenterPathToTerrain();
        preparationProgress.completeStage(TERRAIN_ADJUST_PROGRESS);

        return true;
    }

    private boolean queueRailGeneration() {
        if (!canContinue()) return false;

        if (!hasValidCenterPath())
            return false;

        preparationProgress.startStage(TERRAIN_ADJUST_PROGRESS, RAIL_BLOCK_BUILD_PROGRESS, RAIL_BLOCK_BUILD_ESTIMATED_MILLIS);
        List<List<Vector>> railCenterPaths = new RailLanePathBuilder(controlPoints, terrainResolver, trackCount, trackSpacings)
                .createRailCenterPaths(centerPath);

        if (railCenterPaths.size() < trackCount) {
            sendRailError(
                    "Rail Generator could not create %s parallel tracks with spacing %s along this path. "
                            + "Reduce the track count or spacings, or use a less sharp curve.",
                    trackCount,
                    trackSpacings
            );
            return false;
        }

        if (new RailPathOverlapValidator().hasOverlap(railCenterPaths)) {
            sendRailError(
                    "The parallel tracks overlap in this curve. Reduce the track count or spacing, "
                            + "or make the selected path less sharp."
            );
            return false;
        }

        Map<PositionKey, BlockState> railBlocks = buildRailBlocks(railCenterPaths);
        new RailOverheadBuilder(terrainResolver, railType).addTo(railBlocks, railCenterPaths);
        preparationProgress.completeStage(RAIL_BLOCK_BUILD_PROGRESS);

        if (!isInsideGenerationRegion(railBlocks))
            return false;

        if (railBlocks.size() > limits.maxBlockPlacements()) {
            sendRailError(
                    "Rail Generator would place %s blocks. The limit is %s. Split the rail into smaller selections.",
                    railBlocks.size(),
                    limits.maxBlockPlacements()
            );
            return false;
        }

        sendRailInfo(
                "Rail Generator queued %s block changes over %s path points.",
                railBlocks.size(),
                centerPath.size()
        );

        preparationProgress.startStage(RAIL_BLOCK_BUILD_PROGRESS, QUEUE_OPERATIONS_PROGRESS, QUEUE_OPERATIONS_ESTIMATED_MILLIS);
        queueRailBlockPlacements(railBlocks);
        preparationProgress.completeStage(QUEUE_OPERATIONS_PROGRESS);

        setProgressRange(BLOCK_PLACEMENT_START_PERCENTAGE, 100L);

        preparationProgress.stop();
        finishOnMainThread();
        return true;
    }

    private boolean isInsideGenerationRegion(Map<PositionKey, BlockState> railBlocks) {
        if (generationRegion != null && railBlocks.keySet().stream().allMatch(position -> generationRegion.contains(
                BlockVector3.at(position.x(), position.y(), position.z())
        )))
            return true;

        sendRailError(
                "The generated railway does not fit inside its safe generation area. "
                        + "Reduce the track count or spacing, disable overhead poles, or use a less sharp curve."
        );
        return false;
    }

    private void queueRailBlockPlacements(Map<PositionKey, BlockState> railBlocks) {
        List<Vector> positions = new ArrayList<>(limits.blockPlacementBatchSize());
        List<BlockState> blockStates = new ArrayList<>(limits.blockPlacementBatchSize());


        for (Map.Entry<PositionKey, BlockState> entry : railBlocks.entrySet()) {
            positions.add(entry.getKey().toVector());
            blockStates.add(entry.getValue());

            if (positions.size() == limits.blockPlacementBatchSize()) {
                setBlockStatesAtPositions(new ArrayList<>(positions), new ArrayList<>(blockStates));
                positions.clear();
                blockStates.clear();
            }
        }

        if (!positions.isEmpty())
            setBlockStatesAtPositions(positions, blockStates);
    }

    private void finishOnMainThread() {
        runOnMainThread(() -> {
            try {
                finish(blocks, controlPoints);
            } catch (Exception exception) {
                getGeneratorComponent().sendError(getPlayer());
                ChatHelper.logError("Rail Generator failed while finishing.", exception);
            } finally {
                runPreparationFinishedCallbackSafely();
            }
        });
    }

    private void runPreparationFinishedCallbackSafely() {
        try {
            preparationFinishedCallback.run();
        } catch (Exception exception) {
            ChatHelper.logError("Rail Generator preparation callback failed.", exception);
        }
    }

    private boolean hasValidControlPoints() {
        if (controlPoints.size() < 2) {
            sendRailError("Rail Generator needs at least two points.");
            return false;
        }

        if (controlPoints.size() > limits.maxControlPoints()) {
            sendRailError("Rail Generator has too many points. Please use fewer points.");
            return false;
        }

        return true;
    }

    private boolean hasValidCenterPath() {
        if (centerPath.size() < 2) {
            sendRailError("Rail Generator could not create a valid rail path. Select at least two different blocks.");
            return false;
        }

        if (centerPath.size() > limits.maxPathPoints()) {
            sendRailError(
                    "Rail Generator path has %s points. The limit is %s. Split the rail into smaller selections.",
                    centerPath.size(),
                    limits.maxPathPoints()
            );
            return false;
        }

        return true;
    }

    private boolean hasSafePreparedSelection(List<Vector> selectionPoints, int minY, int maxY) {
        if (selectionPoints.size() < 2) {
            sendRailError("Rail Generator could not create a safe preparation selection.");
            return false;
        }

        int minX = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;

        for (Vector point : selectionPoints) {
            minX = Math.min(minX, point.getBlockX());
            minZ = Math.min(minZ, point.getBlockZ());
            maxX = Math.max(maxX, point.getBlockX());
            maxZ = Math.max(maxZ, point.getBlockZ());
        }

        long width = (long) maxX - minX + 1L;
        long height = (long) maxY - minY + 1L + PREPARE_SELECTION_EXPANSION * 2L;
        long length = (long) maxZ - minZ + 1L;
        long volume = width * height * length;

        if (width > limits.maxPreparedRegionAxisLength() || length > limits.maxPreparedRegionAxisLength()) {
            sendRailError("Rail Generator selection is too wide to prepare safely. Split the rail into smaller selections.");
            return false;
        }

        if (volume > limits.maxPreparedRegionVolume()) {
            sendRailError(
                    "Rail Generator selection would prepare %s blocks. The limit is %s. Split the rail into smaller selections.",
                    volume,
                    limits.maxPreparedRegionVolume()
            );
            return false;
        }

        return true;
    }

    private void sendRailInfo(String message, Object... values) {
        if (!canContinue()) return;

        getPlayer().sendMessage(ChatHelper.getStandardComponent(true, message, values));
    }

    private void sendRailError(String message, Object... values) {
        if (!canContinue()) return;

        getPlayer().sendMessage(ChatHelper.PREFIX_COMPONENT.append(ChatHelper.getErrorComponent(message, values)));
    }

    private void runOnMainThread(Runnable runnable) {
        if (!BuildTeamTools.getInstance().isEnabled())
            return;

        if (Bukkit.isPrimaryThread()) {
            runnable.run();
            return;
        }

        Bukkit.getScheduler().runTask(BuildTeamTools.getInstance(), runnable);
    }

    private boolean canContinue() {
        return BuildTeamTools.getInstance().isEnabled()
                && getPlayer() != null
                && getPlayer().isOnline();
    }

    private List<Vector> getControlPoints() {
        List<Vector> selectionPoints = GeneratorUtils.getSelectionPointsFromRegion(getRegion());

        if (selectionPoints == null) return Collections.emptyList();

        return GeneratorUtils.copyToBlockVectors(selectionPoints);
    }

    private List<Vector> createRailSelectionPoints(List<Vector> points) {
        List<Vector> selectionLine = new ArrayList<>(points);

        if (selectionLine.size() >= 2)
            selectionLine = GeneratorUtils.extendPolyLine(selectionLine);

        List<Vector> shiftedPoints = GeneratorUtils.shiftPoints(selectionLine, getSelectionPadding(), true);

        if (shiftedPoints == null || shiftedPoints.size() < 3)
            return GeneratorUtils.createBoundsSelectionPoints(points, getSelectionPadding());

        return shiftedPoints;
    }

    private int getSelectionPadding() {
        int totalTrackSpan = trackSpacings.stream().mapToInt(Integer::intValue).sum();
        int maxLaneOffset = (int) Math.ceil(totalTrackSpan / 2.0D);
        int overheadPoleOffset = railType.hasOverheadPoles() ? railType.getOverheadPoleOffset() : 0;
        return SELECTION_PADDING + maxLaneOffset + overheadPoleOffset + 2;
    }

    private List<Vector> createCenterPath(List<Vector> points) {
        return GeneratorUtils.removeOrthogonalCorners(GeneratorUtils.createShortestBlockPath(points));
    }

    private Map<PositionKey, BlockState> buildRailBlocks(List<List<Vector>> railCenterPaths) {
        return new RailBlockBuilder(
                terrainResolver,
                railType,
                preparationProgress,
                TERRAIN_ADJUST_PROGRESS,
                RAIL_BLOCK_BUILD_PROGRESS
        ).build(railCenterPaths);
    }

    private boolean resolveTrackLayout() {
        trackCount = railType.getTrackCount();
        int trackSpacing = railType.getTrackSpacing();
        trackSpacings = railType.getTrackSpacings();

        Integer trackCountFlag = getIntegerSetting(RailFlag.TRACK_COUNT);
        Integer trackSpacingFlag = getIntegerSetting(RailFlag.TRACK_SPACING);

        if (trackCountFlag != null)
            trackCount = trackCountFlag;

        if (trackSpacingFlag != null) {
            trackSpacing = trackSpacingFlag;
            trackSpacings = Collections.nCopies(Math.max(0, trackCount - 1), trackSpacing);
        } else if (trackCount != railType.getTrackCount()) {
            trackSpacings = Collections.nCopies(Math.max(0, trackCount - 1), trackSpacing);
        }

        if (trackCount < RailType.MIN_TRACK_COUNT || trackCount > RailType.MAX_TRACK_COUNT) {
            sendRailError("Track count must be between %s and %s.", RailType.MIN_TRACK_COUNT, RailType.MAX_TRACK_COUNT);
            return false;
        }

        if (trackSpacing < RailType.MIN_TRACK_SPACING || trackSpacing > RailType.MAX_TRACK_SPACING) {
            sendRailError("Track spacing must be between %s and %s.", RailType.MIN_TRACK_SPACING, RailType.MAX_TRACK_SPACING);
            return false;
        }

        if (trackSpacings.size() != Math.max(0, trackCount - 1)
                || trackSpacings.stream().anyMatch(spacing -> spacing < RailType.MIN_TRACK_SPACING
                || spacing > RailType.MAX_TRACK_SPACING)) {
            sendRailError("Every track spacing must be between %s and %s.",
                    RailType.MIN_TRACK_SPACING, RailType.MAX_TRACK_SPACING);
            return false;
        }

        return trackCount == RailType.MIN_TRACK_COUNT
                || Permissions.checkPermission(getPlayer(), Permissions.RAIL_MULTIPLE_TRACKS);
    }

    private Integer getIntegerSetting(RailFlag flag) {
        Settings settings = getGeneratorComponent().getPlayerSettings().get(getPlayer().getUniqueId());

        if (!(settings instanceof RailSettings railSettings))
            return null;

        Object value = railSettings.getValues().get(flag);
        return value instanceof Integer intValue ? intValue : null;
    }

    private void snapMissingControlPointHeightsToTerrain(List<Vector> points) {
        if (terrainResolver == null)
            return;

        terrainResolver.snapMissingHeightsToTerrain(points, railReferenceY);
    }

    private void adjustCenterPathToTerrain() {
        if (terrainResolver == null || centerPath.isEmpty()) return;

        for (int index = 0; index < centerPath.size(); index++) {
            Vector point = centerPath.get(index);
            point.setY(terrainResolver.getNearestRailSurfaceY(point.getBlockX(), point.getBlockZ(), point.getBlockY()));
            preparationProgress.update(preparationProgress.scale(
                    index + 1,
                    centerPath.size(),
                    TERRAIN_PREPARE_PROGRESS,
                    TERRAIN_ADJUST_PROGRESS
            ));
        }
    }

    private int getRailReferenceY(List<Vector> points) {
        if (points.isEmpty())
            return getPlayer().getWorld().getMinHeight();

        if (!RailTerrainResolver.hasAnyMissingHeights(points))
            return points.getFirst().getBlockY();

        int referenceY = getRegion() != null ? getRegion().getMinimumY() : GeneratorUtils.getMinHeight(points);
        return Math.min(getPlayer().getWorld().getMaxHeight() - 1, referenceY + 1);
    }

    private int getSelectionMinY(List<Vector> points) {
        int minY = getRegion() != null ? getRegion().getMinimumY() : GeneratorUtils.getMinHeight(points);
        return Math.max(getPlayer().getWorld().getMinHeight(), minY - SELECTION_VERTICAL_PADDING);
    }

    private int getSelectionMaxY(List<Vector> points) {
        int maxY = getRegion() != null ? getRegion().getMaximumY() : GeneratorUtils.getMaxHeight(points);
        return Math.min(getPlayer().getWorld().getMaxHeight() - 1, maxY + SELECTION_VERTICAL_PADDING);
    }

    private RailType getRailType() {
        Settings settings = getGeneratorComponent().getPlayerSettings().get(getPlayer().getUniqueId());

        if (!(settings instanceof RailSettings railSettings))
            return RailType.getDefault();

        Object value = railSettings.getValues().get(RailFlag.RAIL_TYPE);
        return value instanceof RailType selectedRailType ? selectedRailType : RailType.getDefault();
    }

}

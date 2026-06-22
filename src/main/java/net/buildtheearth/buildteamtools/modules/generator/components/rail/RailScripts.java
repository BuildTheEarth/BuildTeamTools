package net.buildtheearth.buildteamtools.modules.generator.components.rail;

import com.alpsbte.alpslib.utils.ChatHelper;
import com.alpsbte.alpslib.utils.GeneratorUtils;
import com.sk89q.worldedit.world.block.BlockState;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.generator.model.GeneratorComponent;
import net.buildtheearth.buildteamtools.modules.generator.model.Script;
import net.buildtheearth.buildteamtools.modules.generator.model.Settings;
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
    private static final int SELECTION_VERTICAL_PADDING = 12;
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

    private static final int DEFAULT_RAIL_LANE_COUNT = 1;
    private static final int DEFAULT_RAIL_LANE_SPACING = 5;

    private Block[][][] blocks;
    private List<Vector> controlPoints = new ArrayList<>();
    private List<Vector> centerPath = new ArrayList<>();
    private RailTerrainResolver terrainResolver;
    private RailType railType = RailType.STANDARD;
    private final RailLimits limits;
    private final RailPreparationProgress preparationProgress;
    private final Runnable preparationFinishedCallback;
    private int railReferenceY;

    public RailScripts(Player player, GeneratorComponent generatorComponent, Runnable preparationFinishedCallback) {
        super(player, generatorComponent);
        this.limits = RailLimits.fromConfig();
        this.preparationProgress = new RailPreparationProgress(player, BLOCK_PLACEMENT_START_PERCENTAGE, PROGRESS_UPDATE_INTERVAL_TICKS);
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
        if (!hasSafeEstimatedBlockCount(centerPath)) return false;

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
        terrainResolver = new RailTerrainResolver(blocks);
        preparationProgress.completeStage(TERRAIN_PREPARE_PROGRESS);

        if (!canContinue()) return false;

        preparationProgress.startStage(TERRAIN_PREPARE_PROGRESS, TERRAIN_ADJUST_PROGRESS, TERRAIN_ADJUST_ESTIMATED_MILLIS);
        snapMissingControlPointHeightsToTerrain(controlPoints);
        centerPath = createCenterPath(controlPoints);
        adjustCenterPathToTerrain();
        railType = getRailType();
        preparationProgress.completeStage(TERRAIN_ADJUST_PROGRESS);

        return true;
    }

    private boolean queueRailGeneration() {
        if (!canContinue()) return false;

        if (!hasValidCenterPath())
            return false;

        preparationProgress.startStage(TERRAIN_ADJUST_PROGRESS, RAIL_BLOCK_BUILD_PROGRESS, RAIL_BLOCK_BUILD_ESTIMATED_MILLIS);
        Map<PositionKey, BlockState> railBlocks = buildRailBlocks(centerPath);
        preparationProgress.completeStage(RAIL_BLOCK_BUILD_PROGRESS);

        if (railBlocks.size() > limits.maxBlockPlacements()) {
            sendRailError("Rail Generator would place " + railBlocks.size() + " blocks. The limit is "
                    + limits.maxBlockPlacements() + ". Split the rail into smaller selections.");
            return false;
        }

        sendRailInfo("Rail Generator queued " + railBlocks.size() + " block changes over "
                + centerPath.size() + " path points.");

        preparationProgress.startStage(RAIL_BLOCK_BUILD_PROGRESS, QUEUE_OPERATIONS_PROGRESS, QUEUE_OPERATIONS_ESTIMATED_MILLIS);
        queueRailBlockPlacements(railBlocks);
        preparationProgress.completeStage(QUEUE_OPERATIONS_PROGRESS);

        setProgressRange(BLOCK_PLACEMENT_START_PERCENTAGE, 100L);

        preparationProgress.stop();
        finishOnMainThread();
        return true;
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
            sendRailError("Rail Generator path has " + centerPath.size() + " points. The limit is "
                    + limits.maxPathPoints() + ". Split the rail into smaller selections.");
            return false;
        }

        return true;
    }

    private boolean hasSafeEstimatedBlockCount(List<Vector> path) {
        long estimatedBlocks = (long) path.size() * getRailLaneCount() * 5L;

        if (estimatedBlocks <= limits.maxBlockPlacements())
            return true;

        sendRailError("Rail Generator would likely place too many blocks. Split the rail into smaller selections.");
        return false;
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
            sendRailError("Rail Generator selection would prepare " + volume + " blocks. The limit is "
                    + limits.maxPreparedRegionVolume() + ". Split the rail into smaller selections.");
            return false;
        }

        return true;
    }

    private void sendRailInfo(String message) {
        if (!canContinue()) return;

        getPlayer().sendMessage(ChatHelper.getStandardComponent(true, message));
    }

    private void sendRailError(String message) {
        if (!canContinue()) return;

        getPlayer().sendMessage(ChatHelper.PREFIX_COMPONENT.append(ChatHelper.getErrorComponent(message)));
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
        int sideLaneCount = (getRailLaneCount() - 1) / 2;
        return SELECTION_PADDING + (getRailLaneSpacing() * sideLaneCount) + 2;
    }

    private List<Vector> createCenterPath(List<Vector> points) {
        return GeneratorUtils.removeOrthogonalCorners(GeneratorUtils.createShortestBlockPath(points));
    }

    private Map<PositionKey, BlockState> buildRailBlocks(List<Vector> path) {
        return new RailBlockBuilder(
                controlPoints,
                terrainResolver,
                railType,
                preparationProgress,
                getRailLaneCount(),
                getRailLaneSpacing(),
                TERRAIN_ADJUST_PROGRESS,
                RAIL_BLOCK_BUILD_PROGRESS
        ).build(path);
    }

    private int getRailLaneCount() {
        return DEFAULT_RAIL_LANE_COUNT;
    }

    private int getRailLaneSpacing() {
        return DEFAULT_RAIL_LANE_SPACING;
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
            preparationProgress.update(preparationProgress.scale(index + 1, centerPath.size(), TERRAIN_PREPARE_PROGRESS, TERRAIN_ADJUST_PROGRESS));
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
            return RailType.STANDARD;

        Object value = railSettings.getValues().get(RailFlag.RAIL_TYPE);
        return value instanceof RailType selectedRailType ? selectedRailType : RailType.STANDARD;
    }

}

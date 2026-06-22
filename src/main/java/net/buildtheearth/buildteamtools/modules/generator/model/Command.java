package net.buildtheearth.buildteamtools.modules.generator.model;

import com.alpsbte.alpslib.utils.ChatHelper;
import com.alpsbte.alpslib.utils.GeneratorUtils;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import lombok.Getter;
import net.kyori.adventure.text.format.NamedTextColor;
import net.buildtheearth.buildteamtools.modules.common.CommonModule;
import net.buildtheearth.buildteamtools.modules.generator.listeners.GeneratorListener;
import net.buildtheearth.buildteamtools.utils.MenuItems;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Command {

    public static final int MAX_COMMANDS_PER_SERVER_TICK = 10;

    @Getter
    private final Player player;
    @Getter
    private final GeneratorComponent generatorComponent;
    @Getter
    private final List<Operation> operations;

    @Getter
    private final Block[][][] blocks;

    @Getter
    private final int changes;

    @Getter
    private final Region region;

    @Getter
    private final World weWorld;

    @Getter
    private final Actor actor;

    @Getter
    private final LocalSession localSession;

    private final long totalProgressWeight;
    private final long progressStartPercentage;
    private final long progressEndPercentage;
    private final long progressRangePercentage;
    private long completedProgressWeight;

    @Getter
    private long percentage;

    private final Vector[] minMax;
    private boolean breakPointActive;
    private boolean threadActive;
    private RegionSelector tempRegionSelector;
    private Material oldMaterial;
    private BlockData oldBlockData;
    private boolean failed;

    @Getter
    private boolean isFinished;

    public Command(Script script, Block[][][] blocks) {
        this.player = script.getPlayer();
        this.generatorComponent = script.getGeneratorComponent();
        this.operations = script.getOperations();
        this.changes = script.getChanges();
        this.region = script.getRegion();
        this.weWorld = script.weWorld;
        this.actor = script.actor;
        this.localSession = script.localSession;
        this.blocks = blocks;

        this.totalProgressWeight = Math.max(1L, operations.stream().mapToLong(Operation::getProgressWeight).sum());
        this.progressStartPercentage = script.getProgressStartPercentage();
        this.progressEndPercentage = script.getProgressEndPercentage();
        this.progressRangePercentage = progressEndPercentage - progressStartPercentage;
        this.percentage = progressStartPercentage;

        minMax = GeneratorUtils.getMinMaxPoints(getRegion());
    }

    /** Processes the commands from the command queue to prevent the server from freezing. */
    public void tick() {
        if (operations.isEmpty()) {
            if (!isFinished && !failed)
                finish();
            return;
        }

        percentage = calculateProgressPercentage();

        sendProgressActionBar(!breakPointActive && !threadActive ? NamedTextColor.GREEN : NamedTextColor.YELLOW);

        if (threadActive)
            return;

        // Process commands in batches of MAX_COMMANDS_PER_SERVER_TICK
        for (int i = 0; i < MAX_COMMANDS_PER_SERVER_TICK;) {
            if (operations.isEmpty()) {
                if (!isFinished && !failed)
                    finish();
                break;
            }

            Operation command = operations.get(0);
            processOperation(command);

            if (breakPointActive || threadActive || command.getProgressWeight() > 1L)
                break;

            // Skip WorldEdit commands that take no time to execute
            if (command.getOperationType() == Operation.OperationType.COMMAND) {
                String commandString = command.get(0, String.class);

                if (commandString.startsWith("//gmask")
                        || commandString.startsWith("//mask")
                        || commandString.startsWith("//pos")
                        || commandString.startsWith("//sel")
                        || commandString.startsWith("//expand"))
                    continue;
            }

            i++;
        }
    }

    /** Processes a single command. */
    public void processOperation(Operation operation) {
        CompletableFuture<Void> future = null;

        try {
            switch (operation.getOperationType()) {
                case COMMAND:
                    String command = (String) operation.getValues().get(0);

                    if (command.contains("%%XYZ/"))
                        command = convertXYZ(command);

                    runInternalGeneratorCommand(command);
                    break;

                case BREAKPOINT:
                    if (!CommonModule.getInstance().getDependencyComponent().isFastAsyncWorldEditEnabled())
                        break;

                    Vector point = minMax[0];
                    Block block = getPlayer().getWorld().getBlockAt(point.getBlockX(), point.getBlockY(), point.getBlockZ());

                    // If the block is a barrier, remove it and continue with the next command
                    if (breakPointActive && block.getType() == Material.BARRIER) {
                        block.setType(oldMaterial);
                        block.setBlockData(oldBlockData);
                        block.getState().update();
                        GeneratorUtils.restoreSelection(getPlayer(), tempRegionSelector);

                        breakPointActive = false;
                        break;
                    }

                    if (!breakPointActive) {
                        oldMaterial = block.getType();
                        oldBlockData = block.getBlockData();
                        BlockType blockType = BlockTypes.BARRIER;

                        if (blockType == null)
                            break;

                        GeneratorUtils.createCuboidSelection(getPlayer(), point, point);
                        future = GeneratorUtils.replaceBlocks(localSession, actor, weWorld, null, new BlockState[]{blockType.getDefaultState()});
                        breakPointActive = true;
                    }

                    break;

                case REPLACE_BLOCKSTATES_WITH_MASKS:
                    future = GeneratorUtils.replaceBlocksWithMasks(
                            localSession,
                            actor,
                            weWorld,
                            toList(operation.get(0, String[].class)),
                            operation.get(1, BlockState.class),
                            operation.get(2, BlockState[].class),
                            operation.get(3, Integer.class)
                    );
                    break;

                case REPLACE_BLOCKSTATES:
                    future = GeneratorUtils.replaceBlocks(
                            localSession,
                            actor,
                            weWorld,
                            operation.get(0, BlockState[].class),
                            operation.get(1, BlockState[].class)
                    );
                    break;

                case SET_BLOCKSTATES_AT_POSITIONS:
                    future = GeneratorUtils.setBlockStatesAtPositions(
                            localSession,
                            actor,
                            weWorld,
                            toList(operation.get(0, Vector[].class)),
                            toList(operation.get(1, BlockState[].class))
                    );
                    break;

                case DRAW_CURVE_WITH_MASKS:
                    future = GeneratorUtils.drawCurveWithMasks(
                            localSession,
                            actor,
                            weWorld,
                            blocks,
                            toList(operation.get(0, String[].class)),
                            toList(operation.get(1, Vector[].class)),
                            operation.get(2, BlockState[].class),
                            operation.get(3, Boolean.class)
                    );
                    break;

                case DRAW_POLY_LINE_WITH_MASKS:
                    future = GeneratorUtils.drawPolyLineWithMasks(
                            localSession,
                            actor,
                            weWorld,
                            blocks,
                            toList(operation.get(0, String[].class)),
                            toList(operation.get(1, Vector[].class)),
                            operation.get(2, BlockState[].class),
                            operation.get(3, Boolean.class),
                            operation.get(4, Boolean.class)
                    );
                    break;

                case DRAW_LINE_WITH_MASKS:
                    future = GeneratorUtils.drawLineWithMasks(
                            localSession,
                            actor,
                            weWorld,
                            blocks,
                            toList(operation.get(0, String[].class)),
                            operation.get(1, Vector.class),
                            operation.get(2, Vector.class),
                            operation.get(3, BlockState[].class),
                            operation.get(4, Boolean.class)
                    );
                    break;

                case PASTE_SCHEMATIC:
                    future = GeneratorUtils.pasteSchematic(
                            localSession,
                            actor,
                            weWorld,
                            blocks,
                            operation.get(0, String.class),
                            operation.get(1, Location.class),
                            operation.get(2, Double.class)
                    );
                    break;

                case CUBOID_SELECTION:
                    GeneratorUtils.createCuboidSelection(
                            getPlayer(),
                            operation.get(0, Vector.class),
                            operation.get(1, Vector.class)
                    );
                    break;

                case POLYGONAL_SELECTION:
                    GeneratorUtils.createPolySelection(getPlayer(), toList(operation.get(0, Vector[].class)), blocks);
                    break;

                case CLEAR_HISTORY:
                    GeneratorUtils.clearHistory(localSession);
                    break;

                case SET_GMASK:
                    GeneratorUtils.setGmask(localSession, operation.get(0, String.class));
                    break;

                case EXPAND_SELECTION:
                    GeneratorUtils.expandSelection(localSession, operation.get(0, Vector.class));
                    break;
            }
        } catch (Exception e) {
            ChatHelper.logError(
                    "Generator command processing failed: %s - %s",
                    e,
                    operation.getOperationType(),
                    operation.getValuesAsString()
            );
            failGeneration();
            return;
        }

        if (future != null) {
            threadActive = true;

            // Ensure we clear threadActive and remove the operation regardless of success or exception
            future.whenComplete((v, ex) -> {
                threadActive = false;

                if (ex != null) {
                    ChatHelper.logError(
                            "Generator async operation failed: %s - %s",
                            new Exception(ex),
                            operation.getOperationType(),
                            operation.getValuesAsString()
                    );
                    failGeneration();
                    return;
                }

                completeOperation(operation);
            });
        } else if (!breakPointActive) {
            completeOperation(operation);
        }
    }

    private long calculateProgressPercentage() {
        if (operations.isEmpty())
            return progressEndPercentage;

        long scaledProgress = progressStartPercentage + Math.round(
                (double) completedProgressWeight / (double) totalProgressWeight * progressRangePercentage
        );
        long maxVisibleProgress = Math.max(progressStartPercentage, progressEndPercentage - 1L);

        return Math.max(progressStartPercentage, Math.min(scaledProgress, maxVisibleProgress));
    }

    private void completeOperation(Operation operation) {
        completedProgressWeight += operation.getProgressWeight();
        operations.removeFirst();
    }

    private void failGeneration() {
        if (isFinished)
            return;

        failed = true;
        isFinished = true;
        operations.clear();
        sendGeneratorError();
        sendFailureActionBar();
    }

    private void sendGeneratorError() {
        generatorComponent.sendError(player);
    }

    private void sendFailureActionBar() {
        player.sendActionBar(ChatHelper.getErrorComponent("Generator failed."));
    }

    private void sendProgressActionBar(NamedTextColor color) {
        player.sendActionBar(ChatHelper.getStandardComponent(false, "Generator Progress: %s", percentage + "%").color(color));
    }

    private void runInternalGeneratorCommand(String command) {
        GeneratorListener.queueInternalGeneratorCommand(player, command);

        try {
            player.chat(command);
        } finally {
            GeneratorListener.removeInternalGeneratorCommand(player, command);
        }
    }

    private static <T> List<T> toList(T[] values) {
        List<T> list = new ArrayList<>(values.length);
        Collections.addAll(list, values);
        return list;
    }

    /** Converts the XYZ coordinates in a command to the highest block at that location while skipping certain blocks. */
    public String convertXYZ(String command) {
        String[] commandParts = command.split("%%XYZ/", 2);
        String[] xyzParts = commandParts[1].split("/%%", 2);
        String xyz = xyzParts[0];

        String[] xyzSplit = xyz.split(",");
        int x = Integer.parseInt(xyzSplit[0]);
        int y = Integer.parseInt(xyzSplit[1]);
        int z = Integer.parseInt(xyzSplit[2]);

        int maxHeight = y;

        if (blocks != null)
            maxHeight = GeneratorUtils.getMaxHeight(blocks, x, z, MenuItems.getIgnoredMaterials());

        if (maxHeight == 0)
            maxHeight = y;

        String commandSuffix = xyzParts.length > 1 ? xyzParts[1] : "";

        return commandParts[0] + x + "," + maxHeight + "," + z + commandSuffix;
    }

    /** Called when the command queue is finished. */
    public void finish() {
        percentage = progressEndPercentage;
        sendProgressActionBar(NamedTextColor.GREEN);
        isFinished = true;
        generatorComponent.sendSuccessMessage(player);
    }
}

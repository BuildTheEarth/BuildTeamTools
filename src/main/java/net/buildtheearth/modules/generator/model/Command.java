package net.buildtheearth.modules.generator.model;

import com.cryptomorin.xseries.XMaterial;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import lombok.Getter;
import net.buildtheearth.modules.common.CommonModule;
import net.buildtheearth.modules.generator.utils.GeneratorUtils;
import net.buildtheearth.utils.ChatHelper;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.MenuItems;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

public class Command {

    public static final int MAX_COMMANDS_PER_SERVER_TICK = 10;
    public static final int INVENTORY_SLOT = 27;

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

    private final int totalCommands;

    @Getter
    private long percentage;

    private final Vector[] minMax;
    private boolean breakPointActive;
    private RegionSelector tempRegionSelector;
    private Material oldMaterial;
    private BlockData oldBlockData;

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

        this.totalCommands = operations.size();
        minMax = GeneratorUtils.getMinMaxPoints(getRegion());

        player.getInventory().setItem(INVENTORY_SLOT, null);
    }

    /** Processes the commands from the command queue to prevent the server from freezing. */
    public void tick(){
        if(operations.isEmpty())
            return;

        // As long as the player has the barrier in their inventory, we know that the command queue is still processing so we can skip this tick.
        // Reason for using the inventory is that the server can only remove the item if its not frozen. That way we can ensure that the server is ready for more commands once the item is removed.
        // TODO: This is a bit of a hacky way to do this, but it works for the beta. We should find a better way to find out if the command queue is still processing without letting the server freeze because of too many commands.
        if (player.getInventory().getItem(INVENTORY_SLOT) != null && player.getInventory().getItem(INVENTORY_SLOT).getType() == Material.BARRIER)
            return;

        player.getInventory().setItem(INVENTORY_SLOT, Item.create(XMaterial.BARRIER.parseMaterial(), "§c§lGenerator processing commands..."));

        percentage = (int) Math.round((double) (totalCommands - operations.size()) / (double) totalCommands * 100);

        if(!breakPointActive)
            player.sendActionBar("§a§lGenerator Progress: §e" + percentage + "%");
        else
            player.sendActionBar("§a§lGenerator Progress: §e" + percentage + "% §7[§c§lPROCESSING§7]");

        // Process commands in batches of MAX_COMMANDS_PER_SERVER_TICK
        for(int i = 0; i < MAX_COMMANDS_PER_SERVER_TICK;){
            if(operations.isEmpty()){
                finish();
                break;
            }


            Operation command = operations.get(0);
            processOperation(command);

            if(breakPointActive)
                break;

            // Skip WorldEdit commands that take no time to execute
            if(command.getOperationType() == Operation.OperationType.COMMAND){
                String commandString = (String) command.getValues().get(0);
                if(commandString.startsWith("//gmask")
                || commandString.startsWith("//mask")
                || commandString.startsWith("//pos")
                || commandString.startsWith("//sel")
                || commandString.startsWith("//expand"))
                    continue;
            }

            i++;
        }

        player.getInventory().setItem(INVENTORY_SLOT, null);
    }

    /** Processes a single command. */
    public void processOperation(Operation operation){
        try {
            switch (operation.getOperationType()) {
                case COMMAND:
                    String command = (String) operation.getValues().get(0);

                    if (command.contains("%%XYZ/"))
                        command = convertXYZ(command);

                    player.chat(command);
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
                        tempRegionSelector = GeneratorUtils.getCurrentRegionSelector(getPlayer());
                        BlockType blockType = BlockTypes.BARRIER;

                        if(blockType == null)
                            break;

                        GeneratorUtils.createCuboidSelection(getPlayer(), point, point);
                        GeneratorUtils.replaceBlocks(localSession, actor, weWorld, null, new BlockState[]{blockType.getDefaultState()});
                        breakPointActive = true;
                    }

                    break;

                case REPLACE_BLOCKSTATES_WITH_MASKS:
                    GeneratorUtils.replaceBlocksWithMasks(localSession, actor, weWorld, Arrays.asList((String[]) operation.get(0)), (BlockState) operation.get(1), (BlockState[]) operation.get(2), (Integer) operation.get(3));
                    break;

                case REPLACE_BLOCKSTATES:
                    GeneratorUtils.replaceBlocks(localSession, actor, weWorld, (BlockState) operation.get(0), (BlockState[]) operation.get(1));
                    break;

                case DRAW_CURVE_WITH_MASKS:
                    GeneratorUtils.drawCurveWithMasks(localSession, actor, weWorld, blocks, Arrays.asList((String[]) operation.get(0)), Arrays.asList((Vector[]) operation.get(1)), (BlockState[]) operation.get(2), (Boolean) operation.get(3));
                    break;

                case DRAW_POLY_LINE_WITH_MASKS:
                    GeneratorUtils.drawPolyLineWithMasks(localSession, actor, weWorld, blocks, Arrays.asList((String[]) operation.get(0)), Arrays.asList((Vector[]) operation.get(1)), (BlockState[]) operation.get(2), (Boolean) operation.get(3));
                    break;

                case DRAW_LINE_WITH_MASKS:
                    GeneratorUtils.drawLineWithMasks(localSession, actor, weWorld, blocks, Arrays.asList((String[]) operation.get(0)), (Vector) operation.get(1), (Vector) operation.get(2), (BlockState[]) operation.get(3), (Boolean) operation.get(4));
                    break;

                case PASTE_SCHEMATIC:
                    GeneratorUtils.pasteSchematic(localSession, actor, weWorld, blocks, (String) operation.get(0), (Location) operation.get(1), (double) operation.get(2));
                    break;

                case CUBOID_SELECTION:
                    GeneratorUtils.createCuboidSelection(getPlayer(), (Vector) operation.get(0), (Vector) operation.get(1));
                    break;

                case POLYGONAL_SELECTION:
                    GeneratorUtils.createPolySelection(getPlayer(), Arrays.asList((Vector[]) operation.get(0)), blocks);
                    break;

                case CONVEX_SELECTION:
                    GeneratorUtils.createConvexSelection(getPlayer(), Arrays.asList((Vector[]) operation.get(0)), blocks);
                    break;

                case CLEAR_HISTORY:
                    GeneratorUtils.clearHistory(localSession);
                    break;

                case SET_GMASK:
                    GeneratorUtils.setGmask(localSession, (String) operation.get(0));
                    break;

                case EXPAND_SELECTION:
                    GeneratorUtils.expandSelection(localSession, (Vector) operation.get(0));
                    break;
            }
        }catch (Exception e){
            ChatHelper.logError("Error while processing command: " + operation.getOperationType() + " - " + operation.getValuesAsString());
            e.printStackTrace();
        }

        if(!breakPointActive)
            operations.remove(0);
    }

    /** Converts the XYZ coordinates in a command to the highest block at that location while skipping certain blocks. */
    public String convertXYZ(String command){
        String xyz = command.split("%%XYZ/")[1].split("/%%")[0];

        String[] xyzSplit = xyz.split(",");
        int x = Integer.parseInt(xyzSplit[0]);
        int y = Integer.parseInt(xyzSplit[1]);
        int z = Integer.parseInt(xyzSplit[2]);

        int maxHeight = y;

        if(blocks != null)
            maxHeight = GeneratorUtils.getMaxHeight(blocks, x, z, MenuItems.getIgnoredMaterials());
        if(maxHeight == 0)
            maxHeight = y;

        String commandSuffix = "";
        if(command.split("/%%").length > 1)
            commandSuffix = command.split("/%%")[1];

        return command.split("%%XYZ/")[0] + x + "," + maxHeight + "," + z + commandSuffix;
    }



    /** Called when the command queue is finished. */
    public void finish(){
        generatorComponent.sendSuccessMessage(player);
    }
}

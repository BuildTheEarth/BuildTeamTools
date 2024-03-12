package net.buildtheearth.modules.generator.model;

import com.cryptomorin.xseries.XMaterial;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import lombok.Getter;
import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.common.CommonModule;
import net.buildtheearth.modules.generator.utils.GeneratorUtils;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.MenuItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
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

    private final int totalCommands;

    @Getter
    private long percentage;

    private Vector[] minMax;
    private boolean breakPointActive;
    private RegionSelector tempRegionSelector;
    private Material oldMaterial;
    private BlockData oldBlockData;

    public Command(Script script, Block[][][] blocks) {
        this.player = script.getPlayer();
        this.generatorComponent = script.getGeneratorComponent();
        this.operations = script.getOperations();
        this.changes = script.getChanges();
        this.blocks = blocks;

        this.totalCommands = operations.size();
        minMax = GeneratorUtils.getMinMaxPoints(script.getRegion());

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
                String commandString = command.getValue();
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
        switch(operation.getOperationType()){
            case COMMAND:
                String command = operation.getValue();

                if(operation.getValue().contains("%%XYZ/"))
                    command = convertXYZ(command);

                player.chat(command);
                break;

            case BREAKPOINT:
                if(!CommonModule.getInstance().getDependencyComponent().isFastAsyncWorldEditEnabled())
                    break;

                Vector point = minMax[0];
                Block block = getPlayer().getWorld().getBlockAt(point.getBlockX(), point.getBlockY(), point.getBlockZ());

                // If the block is a barrier, remove it and continue with the next command
                if(breakPointActive && block.getType() == Material.BARRIER) {
                    block.setType(oldMaterial);
                    block.setBlockData(oldBlockData);
                    block.getState().update();
                    GeneratorUtils.restoreSelection(getPlayer(), tempRegionSelector);

                    breakPointActive = false;
                    break;
                }

                if(!breakPointActive) {
                    oldMaterial = block.getType();
                    oldBlockData = block.getBlockData();
                    tempRegionSelector = GeneratorUtils.getCurrentSelection(getPlayer());
                    player.chat("//sel cuboid");
                    player.chat("//pos1 " + point.getBlockX() + "," + point.getBlockY() + "," + point.getBlockZ());
                    player.chat("//pos2 " + point.getBlockX() + "," + point.getBlockY() + "," + point.getBlockZ());
                    player.chat("//gmask");
                    player.chat("//set barrier");
                    breakPointActive = true;
                }

                break;

            case PASTE_SCHEMATIC:
                pasteSchematic(operation);
                break;

            case CUBOID_SELECTION:
                createCuboidSelection(operation);
                break;

            case POLYGONAL_SELECTION:
                createPolySelection(operation);
                break;

            case CONVEX_SELECTION:
                createConvexSelection(operation);
                break;
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

    private void createCuboidSelection(Operation operation){
        String value = operation.getValue();
        String[] valueSplit = value.split(",");

        Vector vector1 = new Vector(Integer.parseInt(valueSplit[0]), Integer.parseInt(valueSplit[1]), Integer.parseInt(valueSplit[2]));
        Vector vector2 = new Vector(Integer.parseInt(valueSplit[3]), Integer.parseInt(valueSplit[4]), Integer.parseInt(valueSplit[5]));

        GeneratorUtils.createCuboidSelection(getPlayer(), vector1, vector2);
    }

    private void createPolySelection(Operation operation){
        String value = operation.getValue();
        String[] valueSplit = value.split(";");

        List<Vector> points = new ArrayList<>();

        for(String point : valueSplit){
            String[] pointSplit = point.split(",");
            points.add(new Vector(Integer.parseInt(pointSplit[0]), Integer.parseInt(pointSplit[1]), Integer.parseInt(pointSplit[2])));
        }

        GeneratorUtils.createPolySelection(getPlayer(), points, blocks);
    }

    private void createConvexSelection(Operation operation){
        String value = operation.getValue();
        String[] valueSplit = value.split(";");

        List<Vector> points = new ArrayList<>();

        for(String point : valueSplit){
            String[] pointSplit = point.split(",");
            points.add(new Vector(Integer.parseInt(pointSplit[0]), Integer.parseInt(pointSplit[1]), Integer.parseInt(pointSplit[2])));
        }

        GeneratorUtils.createConvexSelection(getPlayer(), points, blocks);
    }

    public void pasteSchematic(Operation operation) {
        String schematic = operation.getValue();

        String[] schematicSplit = schematic.split(",");
        String schematicPath = schematicSplit[0];
        org.bukkit.World world = Bukkit.getWorld(schematicSplit[1]);
        int x = Integer.parseInt(schematicSplit[2]);
        int y = Integer.parseInt(schematicSplit[3]);
        int z = Integer.parseInt(schematicSplit[4]);
        double rotation = Double.parseDouble(schematicSplit[5]);
        int offsetY = Integer.parseInt(schematicSplit[6]);

        int maxHeight = y;

        if(blocks != null)
            maxHeight = GeneratorUtils.getMaxHeight(blocks, x, z, MenuItems.getIgnoredMaterials());
        if(maxHeight == 0)
            maxHeight = y;



        if(CommonModule.getInstance().getDependencyComponent().isWorldEditEnabled()) {
            WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
            World weWorld = new BukkitWorld(world);
            com.sk89q.worldedit.entity.Player wePlayer = worldEditPlugin.wrapPlayer(player);

            LocalSession localSession = WorldEdit.getInstance().getSessionManager().get(wePlayer);
            EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(weWorld, -1, wePlayer);
            File schematicFile = new File(BuildTeamTools.getInstance().getDataFolder().getAbsolutePath() + "/../WorldEdit/schematics/" + schematicPath);

            ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);
            ClipboardReader reader;

            if(format == null)
                return;

            try {
                reader = format.getReader(Files.newInputStream(schematicFile.toPath()));
                Clipboard clipboard = reader.read();

                AffineTransform transform = new AffineTransform();
                transform = transform.rotateY(rotation);

                ClipboardHolder holder = new ClipboardHolder(clipboard);
                holder.setTransform(transform);

                com.sk89q.worldedit.function.operation.Operation op = holder
                        .createPaste(editSession)
                        .to(BlockVector3.at(x, maxHeight + offsetY, z))
                        .ignoreAirBlocks(true)
                        .build();
                Operations.complete(op);

            } catch (IOException | WorldEditException e) {
                throw new RuntimeException(e);
            }

            localSession.remember(editSession);
            editSession.commit();
            editSession.close();
        }
    }


    /** Called when the command queue is finished. */
    public void finish(){
        generatorComponent.sendSuccessMessage(player);
    }
}

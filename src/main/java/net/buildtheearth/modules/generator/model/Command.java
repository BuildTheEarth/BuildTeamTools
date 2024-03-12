package net.buildtheearth.modules.generator.model;

import com.cryptomorin.xseries.XMaterial;
import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import lombok.Getter;
import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.common.CommonModule;
import net.buildtheearth.modules.common.components.dependency.DependencyComponent;
import net.buildtheearth.modules.generator.utils.GeneratorUtils;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.MenuItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class Command {

    public static final int MAX_COMMANDS_PER_SERVER_TICK = 10;
    public static final int INVENTORY_SLOT = 27;
    @Getter
    private final Player player;
    @Getter
    private final GeneratorComponent module;
    @Getter
    private final List<String> commands;

    @Getter
    private final Block[][][] blocks;

    @Getter
    private final int operations;

    private final int totalCommands;

    @Getter
    private long percentage;

    public Command(Player player, GeneratorComponent module, List<String> commands, int operations, Block[][][] blocks) {
        this.player = player;
        this.module = module;
        this.commands = commands;
        this.operations = operations;
        this.blocks = blocks;
        this.totalCommands = commands.size();
    }

    /** Processes the commands from the command queue to prevent the server from freezing. */
    public void tick(){
        if(commands.isEmpty())
            return;

        // As long as the player has the barrier in their inventory, we know that the command queue is still processing so we can skip this tick.
        // Reason for using the inventory is that the server can only remove the item if its not frozen. That way we can ensure that the server is ready for more commands once the item is removed.
        // TODO: This is a bit of a hacky way to do this, but it works for the beta. We should find a better way to find out if the command queue is still processing without letting the server freeze because of too many commands.
        if (player.getInventory().getItem(INVENTORY_SLOT) != null && player.getInventory().getItem(INVENTORY_SLOT).getType() == Material.BARRIER)
            return;

        player.getInventory().setItem(INVENTORY_SLOT, Item.create(XMaterial.BARRIER.parseMaterial(), "§c§lGenerator processing commands..."));

        percentage = (int) Math.round((double) (totalCommands - commands.size()) / (double) totalCommands * 100);
        player.sendActionBar("§a§lGenerator Progress: §e" + percentage + "%");

        // Process commands in batches of MAX_COMMANDS_PER_SERVER_TICK
        for(int i = 0; i < MAX_COMMANDS_PER_SERVER_TICK;){
            if(commands.isEmpty()){
                finish();
                break;
            }

            String command = commands.get(0);
            processCommand(command);
            commands.remove(0);

            // Skip WorldEdit commands that take no time to execute
            if(!command.startsWith("//gmask")
            && !command.startsWith("//mask")
            && !command.startsWith("//pos")
            && !command.startsWith("//sel")
            && !command.startsWith("//expand"))
                i++;
        }

        player.getInventory().setItem(INVENTORY_SLOT, null);
    }

    /** Processes a single command. */
    public void processCommand(String command){
        if(command.contains("%%XYZ/"))
            command = convertXYZ(command);

        if(command.contains("%%SCHEMATIC/"))
            pasteSchematic(command);

        player.chat(command);
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

    public void pasteSchematic(String command) {
        String schematic = command.split("%%SCHEMATIC/")[1].split("/%%")[0];

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

                Operation operation = holder
                        .createPaste(editSession)
                        .to(BlockVector3.at(x, maxHeight + offsetY, z))
                        .ignoreAirBlocks(true)
                        .build();
                Operations.complete(operation);

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
        module.sendSuccessMessage(player);
    }
}

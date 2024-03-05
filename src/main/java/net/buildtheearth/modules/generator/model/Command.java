package net.buildtheearth.modules.generator.model;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import lombok.Getter;
import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.generator.utils.GeneratorUtils;
import net.buildtheearth.utils.Item;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

        player.getInventory().setItem(INVENTORY_SLOT, Item.create(Material.BARRIER, "§c§lGenerator processing commands..."));

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
            maxHeight = GeneratorUtils.getMaxHeight(blocks, x, z, Material.LOG, Material.LOG_2, Material.LEAVES, Material.LEAVES_2, Material.WOOL, Material.SNOW);
        if(maxHeight == 0)
            maxHeight = y;

        String commandSuffix = "";
        if(command.split("/%%").length > 1)
            commandSuffix = command.split("/%%")[1];

        return command.split("%%XYZ/")[0] + x + "," + maxHeight + "," + z + commandSuffix;
    }

    public void pasteSchematic(String command) {
        WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if (worldEditPlugin == null) {
            throw new IllegalStateException("WorldEdit is not installed or not loaded properly.");
        }

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
            maxHeight = GeneratorUtils.getMaxHeight(blocks, x, z, GeneratorUtils.IGNORED_MATERIALS);
        if(maxHeight == 0)
            maxHeight = y;

        Vector vector = new Vector(x, maxHeight + offsetY, z);

        World weWorld = new BukkitWorld(world);
        com.sk89q.worldedit.entity.Player wePlayer = worldEditPlugin.wrapPlayer(player);

        LocalSession localSession = WorldEdit.getInstance().getSessionManager().get(wePlayer);
        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(weWorld, -1, wePlayer);
        File schematicFile = new File(BuildTeamTools.getInstance().getDataFolder().getAbsolutePath() + "/../WorldEdit/schematics/" + schematicPath);

        ClipboardFormat format = ClipboardFormat.findByFile(schematicFile);
        ClipboardReader reader;

        try {
            reader = format.getReader(new FileInputStream(schematicFile));
            Clipboard clipboard = reader.read(weWorld.getWorldData());

            AffineTransform transform = new AffineTransform();
            transform = transform.rotateY(rotation);

            ClipboardHolder holder = new ClipboardHolder(clipboard, weWorld.getWorldData());
            holder.setTransform(transform);

            Operation operation = holder
                    .createPaste(editSession, weWorld.getWorldData())
                    .to(vector)
                    .ignoreAirBlocks(true)
                    .build();
            Operations.complete(operation);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (WorldEditException e) {
            throw new RuntimeException(e);
        }

        localSession.remember(editSession);
        editSession.flushQueue();
    }


    /** Called when the command queue is finished. */
    public void finish(){
        module.sendSuccessMessage(player);
    }
}

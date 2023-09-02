package net.buildtheearth.buildteam.components.generator;

import lombok.Getter;
import net.buildtheearth.utils.Item;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;

public class Command {

    public static int MAX_COMMANDS_PER_SERVER_TICK = 10;
    public static int INVENTORY_SLOT = 27;
    @Getter
    private final Player player;
    @Getter
    private final GeneratorModule module;
    @Getter
    private final List<String> commands;

    @Getter
    private final Block[][][] blocks;

    @Getter
    private final int operations;

    private final int totalCommands;

    @Getter
    private long percentage;

    public Command(Player player, GeneratorModule module, List<String> commands, int operations, Block[][][] blocks){
        this.player = player;
        this.module = module;
        this.commands = commands;
        this.operations = operations;
        this.blocks = blocks;
        this.totalCommands = commands.size();
    }

    /** Proecesses the commands from the command queue to prevent the server from freezing. */
    public void tick(){
        if(commands.isEmpty())
            return;

        // As long as the player has the barrier in their inventory, we know that the command queue is still processing so we can skip this tick.
        // Reason for using the inventory is that the server can only remove the item if its not frozen. That way we can ensure that the server is ready for more commands once the item is removed.
        // TODO: This is a bit of a hacky way to do this, but it works for the beta. We should find a better way to find out if the command queue is still processing without letting the server freeze because of too many commands.
        if(player.getInventory().getItem(INVENTORY_SLOT) != null && player.getInventory().getItem(INVENTORY_SLOT).getType() == Material.BARRIER)
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
            processCommand(player, command);
            commands.remove(0);

            // Skip worldedit commands that take no time to execute
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
    public void processCommand(Player p, String command){
        if(command.contains("%%XYZ/"))
            command = convertXYZ(command);

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
            maxHeight = Generator.getMaxHeight(blocks, x, z, Material.LOG, Material.LOG_2, Material.LEAVES, Material.LEAVES_2, Material.WOOL, Material.SNOW);
        if(maxHeight == 0)
            maxHeight = y;

        String commandSuffix = "";
        if(command.split("/%%").length > 1)
            commandSuffix = command.split("/%%")[1];

        return command.split("%%XYZ/")[0] + x + "," + maxHeight + "," + z + commandSuffix;
    }

    /** Called when the command queue is finished. */
    public void finish(){
        module.sendSuccessMessage(player);
    }
}

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
    private Player player;
    @Getter
    private GeneratorModule module;
    @Getter
    private List<String> commands;

    @Getter
    private Block[][][] blocks;

    @Getter
    private int operations;

    public Command(Player player, GeneratorModule module, List<String> commands, int operations, Block[][][] blocks){
        this.player = player;
        this.module = module;
        this.commands = commands;
        this.operations = operations;
        this.blocks = blocks;
    }

    public void tick(){
        if(commands.size() == 0)
            return;

        if(player.getInventory().getItem(INVENTORY_SLOT) != null && player.getInventory().getItem(INVENTORY_SLOT).getType() == Material.BARRIER)
            return;

        player.getInventory().setItem(INVENTORY_SLOT, Item.create(Material.BARRIER, "§c§lGenerator processing commands..."));

        for(int i = 0; i < MAX_COMMANDS_PER_SERVER_TICK;){
            if(commands.size() == 0){
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

    public void processCommand(Player p, String command){
        if(command.contains("%%XYZ/"))
            command = convertXYZ(command);

        player.chat(command);
    }

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

    public void finish(){
    }
}

package net.buildtheearth.buildteam.components.generator.field;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.regions.Region;
import net.buildtheearth.Main;
import net.buildtheearth.buildteam.components.generator.Command;
import net.buildtheearth.buildteam.components.generator.Generator;
import net.buildtheearth.buildteam.components.generator.GeneratorType;
import net.buildtheearth.buildteam.components.generator.History;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FieldScripts {

    public static void fieldscript_v_1_0(Player p, Field field, Region region) {
        List<String> commands = new ArrayList<>();
        HashMap<Object, String> flags = field.getPlayerSettings().get(p.getUniqueId()).getValues();

        Crop crop = Crop.getByIdentifier(flags.get(FieldFlag.CROP));
        CropStage type = CropStage.getByIdentifier(flags.get(FieldFlag.TYPE));
        String direction = flags.get(FieldFlag.DIRECTION);
        String fence = flags.get(FieldFlag.FENCE);







        int operations = 0;

        if(crop == null) return; //TODO TEMPORARY NULL SAFETY

        commands.add("/clearhistory");
        commands.add("//gmask");

        // ----------- PREPARATION 01 ----------
        // Replace all non-solid blocks with air

        commands.add("//expand 10 up");
        commands.add("//expand 10 down");

        // Remove all non-solid blocks
        commands.add("//gmask !#solid");
        commands.add("//replace 0");
        operations++;

        // Remove all trees and pumpkins
        commands.add("//gmask");
        commands.add("//replace leaves,log,pumpkin 0");
        operations++;

        Block[][][] blocks = Generator.analyzeRegion(p, p.getWorld());

        // Replace the field area by yellow wool
        commands.add("//gmask <0");
        commands.add("//set 35:4");
        operations++;

        // ----------- PREPARATION 02 ----------
        // Drawing lines if the crop requires it

        if(crop.isLinesRequired()){
            commands.add("//gmask 35:4");
            commands.add("//sel cuboid");

            // Draw lines
            if(direction.equals("rl")) {
                // /gen field -c POTATO -t TALL -d rl
            } else {

                BlockVector topLeft = region.getMinimumPoint().toBlockVector(); // NW
                BlockVector bottomRight = region.getMaximumPoint().toBlockVector(); // SE

                BlockVector bottomLeft = new BlockVector(topLeft.getX(), topLeft.getY(), bottomRight.getZ());
                BlockVector topRight = new BlockVector(bottomRight.getX(), topLeft.getY(), topLeft.getZ());

                p.sendMessage(ChatColor.AQUA + topLeft.toString());
                p.sendMessage(ChatColor.AQUA + bottomLeft.toString());
                p.sendMessage(ChatColor.AQUA + topRight.toString());
                p.sendMessage(ChatColor.AQUA + bottomRight.toString());

                int shiftX = 0;
                int shiftZ = 0;

                for (int x = bottomLeft.getBlockX(); x < bottomRight.getBlockX();) {
                    int z;
                    boolean drawLine = true;

                    for (z = bottomLeft.getBlockZ(); z > topLeft.getBlockZ();) {
                        if (drawLine) {
                            commands.add("//pos1 " + (x + shiftX) + "," + topLeft.getBlockY() + "," + (z + shiftZ));
                            commands.add("//pos2 " + (x + 3 + shiftX) + "," + bottomRight.getBlockY() + "," + (z + shiftZ));
                            commands.add("//set 35:3");
                            operations++;

                            commands.add("//pos1 " + (x + 3 + shiftX) + "," + bottomRight.getBlockY() + "," + (z - 1 + shiftZ));
                            commands.add("//pos2 " + (x + 3 + shiftX) + "," + topLeft.getBlockY() + "," + (z - 1 + shiftZ));
                            commands.add("//set 35:3");
                            operations++;
                        }
                        drawLine = !drawLine;
                        z -= 2;
                    }
                    // z -= 1;
                    x += 5;
                    shiftX -= 1;
                    shiftZ -= 2;
                }
            }
        }




        Main.buildTeamTools.getGenerator().getCommands().add(new Command(p, field, commands, operations, blocks));
        Generator.getPlayerHistory(p).addHistoryEntry(new History.HistoryEntry(GeneratorType.FIELD, operations));
    }
}

package net.buildtheearth.buildteam.components.generator.field;

import com.sk89q.worldedit.regions.Region;
import net.buildtheearth.Main;
import net.buildtheearth.buildteam.components.generator.Command;
import net.buildtheearth.buildteam.components.generator.Generator;
import net.buildtheearth.buildteam.components.generator.GeneratorType;
import net.buildtheearth.buildteam.components.generator.History;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FieldScripts {

    public static final String LINE_WIKI = "a"; //TODO ADD WIKI PAGE

    public static void fieldscript_v_1_0(Player p, Field field, Region region) {
        List<String> commands = new ArrayList<>();
        HashMap<Object, String> flags = field.getPlayerSettings().get(p.getUniqueId()).getValues();

        Crop crop = Crop.getByIdentifier(flags.get(FieldFlag.CROP));
        CropStage type = CropStage.getByIdentifier(flags.get(FieldFlag.TYPE));
        String direction = flags.get(FieldFlag.DIRECTION);
        String fence = flags.get(FieldFlag.FENCE);







        int operations = 0;

        if(crop == null) return; //TODO TEMPORARY NULL SAFETY
        if(type == null) return; //TODO EVEN MORE TEMPORARY NULL SAFETY

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
        int maxHeight = Generator.getMaxHeight(blocks);

        if(!crop.isLinesRequired()) {
            commands.add("//replace 35:4 0");
        }

        // Replace the field area by lime wool
        commands.add("//gmask <0,35:4");
        commands.add("//replace !35:4 35:5");
        operations++;

        // Set remembering blocks 5 blocks below lime blocks
        commands.add("//gmask =queryRel(0,5,0,35,5)");
        commands.add("//set 7");
        operations++;

        // ----------- PREPARATION 02 ----------
        // Drawing lines if the crop requires it

        if(crop.isLinesRequired()){
            // Return if there aren't at least 2 yellow wool blocks inside the selection
            if(!Generator.containsBlock(blocks, Material.WOOL, (byte) 4, 2)) {
                p.sendMessage("§cYou need to place at least 2 yellow wool blocks inside your selection!");
                p.sendMessage(" ");
                p.sendMessage("§cFor more information, please see the wiki:");
                p.sendMessage("§c" + LINE_WIKI);
                return;
            }

            // Get the most west and most east yellow block to draw a line.
            List<Block> yellowWoolBlocks = Generator.getBlocksOfMaterial(blocks, Material.WOOL, (byte) 4);
            Block westernMost = null;
            Block easternMost = null;
            double currentLowest = Double.MAX_VALUE;
            double currentHighest = Double.MIN_VALUE;

            for(Block yellowWoolBlock : yellowWoolBlocks) {
                if(yellowWoolBlock.getLocation().getBlockX() < currentLowest) {
                    currentLowest = yellowWoolBlock.getLocation().getBlockX();
                    westernMost = yellowWoolBlock;
                }
            }

            for(Block yellowWoolBlock : yellowWoolBlocks) {
                if(yellowWoolBlock.getLocation().getBlockX() > currentHighest) {
                    currentHighest = yellowWoolBlock.getLocation().getBlockX();
                    easternMost = yellowWoolBlock;
                }
            }

            if(westernMost == null || easternMost == null) {
                p.sendMessage("§cSomething went wrong while processing line data!");
                p.sendMessage(" ");
                p.sendMessage("§cPlease contact one of the developers!");
                return;
            }

            int xTargetLeft = region.getMinimumPoint().getBlockX();
            int xTargetRight = region.getMaximumPoint().getBlockX();

            int zLeft = (int) interpolateCoordinates(westernMost.getX(), westernMost.getZ(), easternMost.getX(), easternMost.getZ(), xTargetLeft);
            int zRight = (int) interpolateCoordinates(westernMost.getX(), westernMost.getZ(), easternMost.getX(), easternMost.getZ(), xTargetRight);

            //Draw the first line
            commands.add("//sel cuboid");
            commands.add("//gmask !air"); // Else the line height can't be properly adjusted

            commands.add("//pos1 " + xTargetLeft + "," + (westernMost.getY() - 2) + "," + zLeft); // New western most
            commands.add("//pos2 " + xTargetRight + "," + (easternMost.getY() - 2) + "," + zRight); // New eastern most
            commands.add("//line 35:4");
            operations++;


            commands.add("//expand 10 up");
            commands.add("//expand 10 down");

            commands.add("//gmask !7,0");
            for(int i = maxHeight; i < maxHeight + 5; i++) {
                commands.add("//replace >35:4 35:4");
                commands.add("//replace <35:4 35:4");
                operations++;
            }

            int requiredRepetitions = (region.getMaximumPoint().getBlockZ() - region.getMinimumPoint().getBlockZ());


            //Select original region
            commands.add("//pos1 " + region.getMaximumPoint().getBlockX() + "," + region.getMaximumPoint().getBlockY() + "," + region.getMaximumPoint().getBlockZ());
            commands.add("//pos2 " + region.getMinimumPoint().getBlockX() + "," + region.getMinimumPoint().getBlockY() + "," + region.getMinimumPoint().getBlockZ());

            //Remove extra non solid blocks
            commands.add("//replace !#solid 0");
            operations++;

            //Make original line correct shape
            commands.add("//gmask =queryRel(1,0,0,35,4)&&queryRel(-1,0,+1,35,4)");
            commands.add("//set 35:4");
            operations++;

            //Make the line pattern extend over the field
            for(int i = 0; i <= requiredRepetitions; i++) {
                //Orange wool
                commands.add("//gmask =queryRel(0,0,-1,35,4)||queryRel(0,0,+1,35,4)||queryRel(0,1,-1,35,4)||queryRel(0,1,+1,35,4)||queryRel(0,-1,-1,35,4)||queryRel(0,-1,+1,35,4)");
                commands.add("//replace !35:4,0 35:1");
                operations++;
                //Yellow wool
                commands.add("//gmask =queryRel(0,0,-1,35,1)||queryRel(-1,0,-1,35,1)||queryRel(0,0,+1,35,1)||queryRel(+1,1,+1,35,1)||queryRel(0,0,+1,35,1)||queryRel(+1,0,+1,35,1)||queryRel(+1,-1,+1,35,1)||queryRel(-1,1,-1,35,1)||queryRel(-1,-1,-1,35,1)");
                commands.add("//replace !35:1,0 35:4");
                operations++;
            }

            // Restore field to original shape
            commands.add("//gmask !=queryRel(0,-5,0,7,0)");
            commands.add("//replace !0 2");
            operations++;

            // Remove original yellow wool blocks
            //TODO FIX THESE 2 LINES
            commands.add("//gmask =queryRel(0,-6,0,7,0)");
            commands.add("//replace 2 0");
            operations++;
        }




        Main.buildTeamTools.getGenerator().getCommands().add(new Command(p, field, commands, operations, blocks));
        Generator.getPlayerHistory(p).addHistoryEntry(new History.HistoryEntry(GeneratorType.FIELD, operations));
    }


    public static double interpolateCoordinates(double x1, double z1, double x2, double z2, double targetX) {
        if (x1 == x2) {
            // If the start and end points have the same X-coordinate, return the Z-coordinate of the start point
            return z1;
        }

        // Calculate the proportional distance between c1 and c2 for the target X value
        double t = (targetX - x1) / (x2 - x1);

        // Interpolate the coordinate based on the target X value
        return z1 + (z2 - z1) * t;
    }
}

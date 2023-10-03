package net.buildtheearth.buildteam.components.generator.field;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import net.buildtheearth.Main;
import net.buildtheearth.buildteam.components.generator.Command;
import net.buildtheearth.buildteam.components.generator.Generator;
import net.buildtheearth.buildteam.components.generator.GeneratorType;
import net.buildtheearth.buildteam.components.generator.History;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class FieldScripts {

    public static void fieldscript_v_1_0(Player p, Field field, Region region) {
        List<String> commands = new ArrayList<>();
        HashMap<Object, String> flags = field.getPlayerSettings().get(p.getUniqueId()).getValues();

        // Settings
        Crop crop = Crop.getByIdentifier(flags.get(FieldFlag.CROP));
        CropStage type = CropStage.getByIdentifier(flags.get(FieldFlag.TYPE));
        String fence = flags.get(FieldFlag.FENCE);

        // Information for later restoring original selection
        List<Vector> points = new ArrayList<>();

        float yaw = p.getLocation().getYaw();


        if (region instanceof Polygonal2DRegion) {
            Polygonal2DRegion polyRegion = (Polygonal2DRegion) region;

            for (BlockVector2D blockVector2D : polyRegion.getPoints()) points.add(blockVector2D.toVector());

        } else if (region instanceof CuboidRegion) {
            CuboidRegion cuboidRegion = (CuboidRegion) region;

            points.add(cuboidRegion.getMinimumPoint());
            points.add(cuboidRegion.getMaximumPoint());
        } else {
            p.sendMessage("§c§lERROR: §cRegion type not supported!");
            return;
        }

        // ----------- FIELD GENERATOR SCRIPT ----------
        // Used to generate fields

        int operations = 0;

        commands.add("/clearhistory");
        commands.add("//gmask");

        // ----------- PREPARATION 01 ----------
        // Preparing the field area

        p.chat("//expand 30 30 up");

        Block[][][] blocks = Generator.analyzeRegion(p, p.getWorld());
        int maxHeight = Generator.getMaxHeight(blocks);

        // Remove all non-solid blocks
        commands.add("//gmask !#solid");
        commands.add("//replace 0");
        operations++;

        // Remove all trees and pumpkins
        commands.add("//gmask");
        commands.add("//replace leaves,log,pumpkin 0");
        operations++;

        // ----------- PREPARATION 02 ----------
        // Drawing lines if the crop requires it

        if (crop.isLinesRequired()) {
            // Prepare for line drawing
            boolean requiresAlternatingLines = crop.equals(Crop.VINEYARD) || crop.equals(Crop.PEAR);

            // Get the directory containing all schematic files.
            File directory = new File(Main.instance.getDataFolder().getAbsolutePath() + "/../WorldEdit/schematics/GeneratorCollections/fieldpack/" + (requiresAlternatingLines ? "striped/" : "normal/"));
            Bukkit.getLogger().info(directory.toString());

            // Get some information based on the list of schematics
            short schematicAmount = (short) directory.getAbsoluteFile().listFiles().length;
            File[] schematics = directory.getAbsoluteFile().listFiles();
            short[] availableDirections = new short[schematicAmount];

            // Get an array with all available schematic line directions
            for(int i = 0; i < schematicAmount; i++) {
                availableDirections[i] = Short.parseShort(schematics[i].getName().replace(".schematic", ""));
            }

            // Calculate which direction should be used
            short directionToUse = availableDirections[0]; // Assume the first element is the closest initially
            short minDifference = (short) Math.abs(yaw - availableDirections[0]); // Initial difference

            for (short current : availableDirections) {
                short difference = (short) Math.abs(yaw - current);
                if (difference < minDifference) {
                    minDifference = difference;
                    directionToUse = current;
                }
            }

            commands.add("//schem load /GeneratorCollections/fieldpack/"+(requiresAlternatingLines ? "striped/" : "normal/")+(directionToUse < 100 ? "0"+directionToUse : directionToUse)+".schematic");

            commands.add("//gmask <0");
            commands.add("//replace #solid #copy");
            operations++;
        }

        // ----------- PLACING CROPS ----------
        // Placing the crops

        commands.add("//gmask");

        if (crop == Crop.POTATO) {
            if (type == CropStage.TALL) {
                commands.add("//replace 251:0 24%3,24%3:1,1%17:4,1%5:1");
                operations++;
                commands.add("//replace 251:15 1%3,1%3:1,24%17:4,24%5:1");
                operations++;

                commands.add("//shift 1 up");
                commands.add("//gmask 0");

                commands.add("//replace >3 251:15,251:4,31:1,31:2");
                operations++;

                commands.add("//gmask");

                commands.add("//replace 251:15 175:3");
                operations++;
                commands.add("//replace 251:4 175:2");
                operations++;

                commands.add("//shift 1 up");

                commands.add("//replace >175:3 175:15");
                operations++;
                commands.add("//replace >175:2 175:14");
                operations++;

            } else {
                commands.add("//replace 251:0 208,5");
                operations++;
                commands.add("//replace 251:15 252:13,2");
                operations++;

                commands.add("//shift 1 up");
                commands.add("//gmask 0");

                commands.add("//replace >2 31:1,31:2");
                operations++;

            }
        }

        if (crop == Crop.HARVESTED) {
            if (type == CropStage.DRY) {
                commands.add("//replace 251:0 5%208,95%5");
                operations++;
                commands.add("//replace 251:15 95%208,5%5");
                operations++;

            } else {
                commands.add("//replace 251:0 47%5:1,47%3:1,5%60");
                operations++;
                commands.add("//replace 251:15 95%60,2%3:1,2%5:1");
                operations++;

            }
        }

        if (crop == Crop.OTHER) {
            if (type == CropStage.DRY) {
                commands.add("//setbiome MESA");

                commands.add("//replace 251:0 208,5");
                operations++;
                commands.add("//replace 251:15 252:13,2");
                operations++;

                commands.add("//shift 1 up");
                commands.add("//gmask 0");

                commands.add("//replace >2 31:1,31:2");
                operations++;

            } else {
                commands.add("//setbiome SWAMPLAND");

                commands.add("//replace 251:0 24%3,24%3:1,1%17:4,1%5:1");
                operations++;
                commands.add("//replace 251:15 1%3,1%3:1,24%17:4,24%5:1");
                operations++;

                commands.add("//shift 1 up");
                commands.add("//gmask 0");

                commands.add("//replace >3 251:15,251:4,31:1,31:2");
                operations++;

                commands.add("//gmask");

                commands.add("//replace 251:15 175:3");
                operations++;
                commands.add("//replace 251:4 175:2");
                operations++;

                commands.add("//shift 1 up");

                commands.add("//replace >175:3 175:15");
                operations++;
                commands.add("//replace >175:2 175:14");
                operations++;

            }
        }

        if (crop == Crop.VINEYARD || crop == Crop.PEAR) {
            commands.add("//replace >251:4 15%188,85%22");
            operations++;
            commands.add("//replace >188,22 251:13");
            operations++;

            commands.add("//replace 251:15 5,208:0");
            operations++;
            commands.add("//replace 251:0 208:0,5,3,3:1");
            operations++;
            commands.add("//replace 251:4 3,3:1");
            operations++;

            commands.add("//replace 22 0");
            operations++;
            commands.add("//replace 251:13 18,18:2");
            operations++;

        }

        if (crop == Crop.CORN) {
            if (type == CropStage.HARVESTED) {
                commands.add("//replace <air 60,3,5:1");
                operations++;

                commands.add("//fast");
                commands.add("//replace >60,3,5:1 104:6,104:7");
                operations++;

                commands.add("//fast");

            } else {
                commands.add("//replace <air 60,3,5:1");
                operations++;

                commands.add("//fast");
                commands.add("//replace >60,3,5:1 175");
                operations++;

                commands.add("//replace >175 175");
                operations++;

                commands.add("//fast");

            }
        }

        if (crop == Crop.WHEAT) {
            if (type == CropStage.LIGHT) {
                commands.add("//replace <air 3,3:1");
                operations++;

                commands.add("//replace >3,3:1 107:4,107:5,107:6,107:7,184:4,184:5,184:6,184:7");
                operations++;

            } else {
                commands.add("//replace <air 3,3:1,5,5:3");
                operations++;

                commands.add("//fast");

                commands.add("//replace >3,3:1 31:1,175");
                operations++;

                commands.add("//fast");
            }
        }

        if (crop == Crop.CATTLE || crop == Crop.MEADOW) {
            commands.add("//gmask 0");

            operations += Generator.createPolyLine(commands, points, "41", true, blocks, 1);
            commands.add("//gmask");

            Generator.createPolySelection(commands, points);

            List<Vector> oneMeterPoints = new ArrayList<>(points);
            oneMeterPoints = Generator.populatePoints(oneMeterPoints, 1);
            List<Vector> fencePoints = new ArrayList<>(oneMeterPoints);
            fencePoints = Generator.reducePoints(fencePoints, 3 + 1, 3 - 1);

            commands.add("//sel cuboid");
            commands.add("//expand 10 10 west");
            commands.add("//expand 10 10 north");

            for (Vector vector : fencePoints) {
                commands.add("//pos1 " + Generator.getXYZWithVerticalOffset(vector, blocks, 1));
                commands.add("//pos2 " + Generator.getXYZWithVerticalOffset(vector, blocks, 1));
                commands.add("//set " + fence);
            }

            Generator.createPolySelection(commands, points);

            commands.add("//sel cuboid");
            commands.add("//gmask");

            commands.add("//expand 10 10 up");
            commands.add("//expand 10 10 west");
            commands.add("//expand 10 10 north");

            commands.add("//replace >41 77:5");
            operations++;
            commands.add("//replace 41 166");
            operations++;

            commands.add("//gmask !" + fence + ",77,166");
            commands.add("//replace >#solid 70%0,30%31:1");
            operations++;

            if (crop == Crop.CATTLE) commands.add("//replace <air 60%3,20%2,20%3:1");
            if (crop == Crop.MEADOW) commands.add("//replace <air 70%2,20%3,10%3:1");
            operations++;

        }


        Main.buildTeamTools.getGenerator().getCommands().add(new Command(p, field, commands, operations, blocks));
        Generator.getPlayerHistory(p).addHistoryEntry(new History.HistoryEntry(GeneratorType.FIELD, operations));
    }
}

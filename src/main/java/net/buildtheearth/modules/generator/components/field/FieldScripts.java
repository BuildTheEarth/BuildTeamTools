package net.buildtheearth.modules.generator.components.field;

import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.generator.GeneratorModule;
import net.buildtheearth.modules.generator.model.Command;
import net.buildtheearth.modules.generator.model.Flag;
import net.buildtheearth.modules.generator.model.GeneratorType;
import net.buildtheearth.modules.generator.model.History;
import net.buildtheearth.modules.generator.utils.GeneratorUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FieldScripts {

    public static void fieldScript_v_1_0(Player p, Field field, Region region) {
        List<String> commands = new ArrayList<>();
        HashMap<Flag, String> flags = field.getPlayerSettings().get(p.getUniqueId()).getValues();

        // Settings
        Crop crop = Crop.getByIdentifier(flags.get(FieldFlag.CROP_TYPE));
        CropStage type = CropStage.getByIdentifier(flags.get(FieldFlag.CROP_STAGE));
        String fence = flags.get(FieldFlag.FENCE);

        // Information for later restoring original selection
        List<Vector> points = GeneratorUtils.getSelectionPointsFromRegion(region);

        float yaw = p.getLocation().getYaw();
        if(yaw < 0) yaw += 360;
        if(yaw > 360) yaw -= 360;



        // ----------- FIELD GENERATOR SCRIPT ----------
        // Used to generate fields

        int operations = 0;

        commands.add("/clearhistory");
        commands.add("//gmask");

        // ----------- PREPARATION 01 ----------
        // Preparing the field area

        // Create a cuboid selection of the field area
        GeneratorUtils.createCuboidSelection(p,
                new Vector(region.getMinimumPoint().getX(), region.getMinimumPoint().getY(), region.getMinimumPoint().getZ()),
                new Vector(region.getMaximumPoint().getX(), region.getMaximumPoint().getY(), region.getMaximumPoint().getZ())
        );

        p.chat("//expand 20 20 up");
        p.chat("//expand 10 10 north");
        p.chat("//expand 10 10 west");

        Block[][][] blocks = GeneratorUtils.analyzeRegion(p, p.getWorld());


        // Recreate the original polygon selection
        GeneratorUtils.createPolySelection(p, points, blocks);


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

        // ----------- PREPARATION 02 ----------
        // Drawing lines if the crop requires it

        if (crop.isLinesRequired()) {
            // Prepare for line drawing
            boolean requiresAlternatingLines = crop.equals(Crop.VINEYARD) || crop.equals(Crop.PEAR);

            // Get the directory containing all schematic files.
            File directory = new File(BuildTeamTools.getInstance().getDataFolder().getAbsolutePath() + "/../WorldEdit/schematics/GeneratorCollections/fieldpack/" + (requiresAlternatingLines ? "striped/" : "normal/"));

            File[] schematics = directory.getAbsoluteFile().listFiles();

            if(schematics == null) {
                p.sendMessage("§c§lERROR: §cNo schematics found!");
                return;
            }

            // Get some information based on the list of schematics
            short schematicAmount = (short) schematics.length;
            short[] availableDirections = new short[schematicAmount];

            // Get an array with all available schematic line directions
            for(int i = 0; i < schematicAmount; i++) {
                availableDirections[i] = Short.parseShort(schematics[i].getName().replace(".schematic", ""));
            }

            // Calculate which direction should be used
            short directionToUse = availableDirections[0]; // Assume the first element is the closest initially
            short minDifference = Short.MAX_VALUE; // Initial difference

            for (short current : availableDirections) {
                short difference1 = (short) Math.abs(yaw - current);
                if (difference1 < minDifference) {
                    minDifference = difference1;
                    directionToUse = current;
                }

                short difference2 = (short) Math.abs(yaw - (current + 180));
                if (difference2 < minDifference) {
                    minDifference = difference2;
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
                commands.add("//replace >175:2 175:14");
                operations+=2;

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
                commands.add("//replace 251:15 95%208,5%5");
                operations+=2;

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
                commands.add("//replace 251:15 3:1,2");

                commands.add("//shift 1 up");
                commands.add("//gmask 0");

                commands.add("//replace >2 31:1,31:2");
                operations+=2;

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
            commands.add("//gmask !#solid");

            List<Vector> oneMeterPoints = new ArrayList<>(points);
            oneMeterPoints.add(points.get(0));
            oneMeterPoints = GeneratorUtils.populatePoints(oneMeterPoints, 1);
            List<Vector> fencePoints = new ArrayList<>(oneMeterPoints);
            fencePoints = GeneratorUtils.reducePoints(fencePoints, 3 + 1, 3 - 1);

            operations += GeneratorUtils.createPolyLine(commands, fencePoints, "41", true, blocks, 1);
            commands.add("//gmask");

            GeneratorUtils.createPolySelection(commands, points);

            commands.add("//sel cuboid");
            commands.add("//expand 10 10 west");
            commands.add("//expand 10 10 north");

            for (Vector vector : fencePoints) {
                commands.add("//pos1 " + GeneratorUtils.getXYZWithVerticalOffset(vector, blocks, 1));
                commands.add("//pos2 " + GeneratorUtils.getXYZWithVerticalOffset(vector, blocks, 1));
                commands.add("//set " + fence);
            }

            GeneratorUtils.createPolySelection(commands, points);

            commands.add("//sel cuboid");
            commands.add("//gmask");

            commands.add("//expand 10 10 up");
            commands.add("//expand 10 10 west");
            commands.add("//expand 10 10 north");

            commands.add("//replace >41 77:5");
            operations++;
            commands.add("//replace 41 166");
            operations++;

            GeneratorUtils.createPolySelection(commands, points);

            commands.add("//gmask !" + fence + ",77,166");
            commands.add("//expand 10 10 up");

            if (crop == Crop.CATTLE) commands.add("//replace <air 60%3,20%2,20%3:1");
            if (crop == Crop.MEADOW) commands.add("//replace <air 70%2,20%3,10%3:1");
            operations++;

            commands.add("//replace >#solid 70%0,30%31:1");
            operations++;

            // Make sure that the poly selection afterwards is the same as before
            commands.add("//sel cuboid");

        }

        // Depending on the selection type, the selection needs to be recreated
        if(region instanceof Polygonal2DRegion || region instanceof ConvexPolyhedralRegion)
            GeneratorUtils.createPolySelection(commands, points);
        else if(region instanceof CuboidRegion){
            CuboidRegion cuboidRegion = (CuboidRegion) region;
            Vector pos1 = new Vector(cuboidRegion.getPos1().getX(), cuboidRegion.getPos1().getY(), cuboidRegion.getPos1().getZ());
            Vector pos2 = new Vector(cuboidRegion.getPos2().getX(), cuboidRegion.getPos2().getY(), cuboidRegion.getPos2().getZ());
            GeneratorUtils.createCuboidSelection(commands, pos1, pos2);
        }

        GeneratorModule.getInstance().getGeneratorCommands().add(new Command(p, field, commands, operations, blocks));
        GeneratorModule.getInstance().getPlayerHistory(p).addHistoryEntry(new History.HistoryEntry(GeneratorType.FIELD, operations));
    }
}

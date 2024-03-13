package net.buildtheearth.modules.generator.components.field;

import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.generator.model.Flag;
import net.buildtheearth.modules.generator.model.GeneratorComponent;
import net.buildtheearth.modules.generator.model.Script;
import net.buildtheearth.modules.generator.utils.GeneratorUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FieldScripts extends Script {

    public FieldScripts(Player player, GeneratorComponent generatorComponent) {
        super(player, generatorComponent);
        
        fieldScript_v_1_0();
    }
    
    public void fieldScript_v_1_0() {
        HashMap<Flag, Object> flags = getGeneratorComponent().getPlayerSettings().get(getPlayer().getUniqueId()).getValues();

        // Settings
        CropType cropType = (CropType) flags.get(FieldFlag.CROP_TYPE);
        CropStage type = (CropStage) flags.get(FieldFlag.CROP_STAGE);
        String fence = (String) flags.get(FieldFlag.FENCE);

        // Information for later restoring original selection
        List<Vector> points = GeneratorUtils.getSelectionPointsFromRegion(getRegion());

        float yaw = getPlayer().getLocation().getYaw();
        if(yaw < 0) yaw += 360;
        if(yaw > 360) yaw -= 360;



        // ----------- FIELD GENERATOR SCRIPT ----------
        // Used to generate fields

        createCommand("/clearhistory");
        createCommand("//gmask");

        // ----------- PREPARATION 01 ----------
        // Preparing the field area

        // Create a cuboid selection of the field area
        GeneratorUtils.createCuboidSelection(getPlayer(),
                new Vector(getRegion().getMinimumPoint().getX(), getRegion().getMinimumPoint().getY(), getRegion().getMinimumPoint().getZ()),
                new Vector(getRegion().getMaximumPoint().getX(), getRegion().getMaximumPoint().getY(), getRegion().getMaximumPoint().getZ())
        );

        getPlayer().chat("//expand 20 20 up");
        getPlayer().chat("//expand 10 10 north");
        getPlayer().chat("//expand 10 10 west");

        Block[][][] blocks = GeneratorUtils.analyzeRegion(getPlayer(), getPlayer().getWorld());


        // Recreate the original polygon selection
        GeneratorUtils.createPolySelection(getPlayer(), points, blocks);


        createCommand("//expand 10 up");
        createCommand("//expand 10 down");

        // Remove all non-solid blocks
        createCommand("//gmask !#solid");
        createCommand("//replace 0");
        changes++;

        // Remove all trees and pumpkins
        createCommand("//gmask");
        createCommand("//replace leaves,log,pumpkin 0");
        changes++;

        // ----------- PREPARATION 02 ----------
        // Drawing lines if the crop requires it

        if (cropType.isLinesRequired()) {
            // Prepare for line drawing
            boolean requiresAlternatingLines = cropType.equals(CropType.VINEYARD) || cropType.equals(CropType.PEAR);

            // Get the directory containing all schematic files.
            File directory = new File(BuildTeamTools.getInstance().getDataFolder().getAbsolutePath() + "/../WorldEdit/schematics/GeneratorCollections/fieldpack/" + (requiresAlternatingLines ? "striped/" : "normal/"));

            File[] schematics = directory.getAbsoluteFile().listFiles();

            if(schematics == null) {
                getPlayer().sendMessage("§c§lERROR: §cNo schematics found!");
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

            createCommand("//schem load /GeneratorCollections/fieldpack/"+(requiresAlternatingLines ? "striped/" : "normal/")+(directionToUse < 100 ? "0"+directionToUse : directionToUse)+".schematic");

            createCommand("//gmask <0");
            createCommand("//replace #solid #copy");
            changes++;
        }

        // ----------- PLACING CROPS ----------
        // Placing the crops

        createCommand("//gmask");

        if (cropType == CropType.POTATO) {
            if (type == CropStage.TALL) {
                createCommand("//replace 251:0 24%3,24%3:1,1%17:4,1%5:1");
                changes++;
                createCommand("//replace 251:15 1%3,1%3:1,24%17:4,24%5:1");
                changes++;

                createCommand("//shift 1 up");
                createCommand("//gmask 0");

                createCommand("//replace >3 251:15,251:4,31:1,31:2");
                changes++;

                createCommand("//gmask");

                createCommand("//replace 251:15 175:3");
                changes++;
                createCommand("//replace 251:4 175:2");
                changes++;

                createCommand("//shift 1 up");

                createCommand("//replace >175:3 175:15");
                createCommand("//replace >175:2 175:14");
                changes+=2;

            } else {
                createCommand("//replace 251:0 208,5");
                changes++;
                createCommand("//replace 251:15 252:13,2");
                changes++;

                createCommand("//shift 1 up");
                createCommand("//gmask 0");

                createCommand("//replace >2 31:1,31:2");
                changes++;

            }
        }

        if (cropType == CropType.HARVESTED) {
            if (type == CropStage.DRY) {
                createCommand("//replace 251:0 5%208,95%5");
                createCommand("//replace 251:15 95%208,5%5");
                changes+=2;

            } else {
                createCommand("//replace 251:0 47%5:1,47%3:1,5%60");
                changes++;
                createCommand("//replace 251:15 95%60,2%3:1,2%5:1");
                changes++;

            }
        }

        if (cropType == CropType.OTHER) {
            if (type == CropStage.DRY) {
                createCommand("//setbiome MESA");

                createCommand("//replace 251:0 208,5");
                changes++;
                createCommand("//replace 251:15 3:1,2");

                createCommand("//shift 1 up");
                createCommand("//gmask 0");

                createCommand("//replace >2 31:1,31:2");
                changes+=2;

            } else {
                createCommand("//setbiome SWAMPLAND");

                createCommand("//replace 251:0 24%3,24%3:1,1%17:4,1%5:1");
                changes++;
                createCommand("//replace 251:15 1%3,1%3:1,24%17:4,24%5:1");
                changes++;

                createCommand("//shift 1 up");
                createCommand("//gmask 0");

                createCommand("//replace >3 251:15,251:4,31:1,31:2");
                changes++;

                createCommand("//gmask");

                createCommand("//replace 251:15 175:3");
                changes++;
                createCommand("//replace 251:4 175:2");
                changes++;

                createCommand("//shift 1 up");

                createCommand("//replace >175:3 175:15");
                changes++;
                createCommand("//replace >175:2 175:14");
                changes++;

            }
        }

        if (cropType == CropType.VINEYARD || cropType == CropType.PEAR) {
            createCommand("//replace >251:4 15%188,85%22");
            changes++;
            createCommand("//replace >188,22 251:13");
            changes++;

            createCommand("//replace 251:15 5,208:0");
            changes++;
            createCommand("//replace 251:0 208:0,5,3,3:1");
            changes++;
            createCommand("//replace 251:4 3,3:1");
            changes++;

            createCommand("//replace 22 0");
            changes++;
            createCommand("//replace 251:13 18,18:2");
            changes++;

        }

        if (cropType == CropType.CORN) {
            if (type == CropStage.HARVESTED) {
                createCommand("//replace <air 60,3,5:1");
                changes++;

                createCommand("//fast");
                createCommand("//replace >60,3,5:1 104:6,104:7");
                changes++;

                createCommand("//fast");

            } else {
                createCommand("//replace <air 60,3,5:1");
                changes++;

                createCommand("//fast");
                createCommand("//replace >60,3,5:1 175");
                changes++;

                createCommand("//replace >175 175");
                changes++;

                createCommand("//fast");

            }
        }

        if (cropType == CropType.WHEAT) {
            if (type == CropStage.LIGHT) {
                createCommand("//replace <air 3,3:1");
                changes++;

                createCommand("//replace >3,3:1 107:4,107:5,107:6,107:7,184:4,184:5,184:6,184:7");
                changes++;

            } else {
                createCommand("//replace <air 3,3:1,5,5:3");
                changes++;

                createCommand("//fast");

                createCommand("//replace >3,3:1 31:1,175");
                changes++;

                createCommand("//fast");
            }
        }

        if (cropType == CropType.CATTLE || cropType == CropType.MEADOW) {
            createCommand("//gmask !#solid");

            List<Vector> oneMeterPoints = new ArrayList<>(points);
            oneMeterPoints.add(points.get(0));
            oneMeterPoints = GeneratorUtils.populatePoints(oneMeterPoints, 1);
            List<Vector> fencePoints = new ArrayList<>(oneMeterPoints);
            fencePoints = GeneratorUtils.reducePoints(fencePoints, 3 + 1, 3 - 1);

            changes += GeneratorUtils.createPolyLine(this, fencePoints, "41", true, blocks, 1);
            createCommand("//gmask");

            createPolySelection(operations, points);

            createCommand("//sel cuboid");
            createCommand("//expand 10 10 west");
            createCommand("//expand 10 10 north");

            for (Vector vector : fencePoints) {
                createCommand("//pos1 " + GeneratorUtils.getXYZWithVerticalOffset(vector, blocks, 1));
                createCommand("//pos2 " + GeneratorUtils.getXYZWithVerticalOffset(vector, blocks, 1));
                createCommand("//set " + fence);
            }

            createPolySelection(operations, points);

            createCommand("//sel cuboid");
            createCommand("//gmask");

            createCommand("//expand 10 10 up");
            createCommand("//expand 10 10 west");
            createCommand("//expand 10 10 north");

            createCommand("//replace >41 77:5");
            changes++;
            createCommand("//replace 41 166");
            changes++;

            createPolySelection(operations, points);

            createCommand("//gmask !" + fence + ",77,166");
            createCommand("//expand 10 10 up");

            if (cropType == CropType.CATTLE) createCommand("//replace <air 60%3,20%2,20%3:1");
            if (cropType == CropType.MEADOW) createCommand("//replace <air 70%2,20%3,10%3:1");
            changes++;

            createCommand("//replace >#solid 70%0,30%31:1");
            changes++;

            // Make sure that the poly selection afterwards is the same as before
            createCommand("//sel cuboid");

        }

        // Depending on the selection type, the selection needs to be recreated
        if(getRegion() instanceof Polygonal2DRegion || getRegion() instanceof ConvexPolyhedralRegion)
            createPolySelection(operations, points);
        else if(getRegion() instanceof CuboidRegion){
            CuboidRegion cuboidRegion = (CuboidRegion) getRegion();
            Vector pos1 = new Vector(cuboidRegion.getPos1().getX(), cuboidRegion.getPos1().getY(), cuboidRegion.getPos1().getZ());
            Vector pos2 = new Vector(cuboidRegion.getPos2().getX(), cuboidRegion.getPos2().getY(), cuboidRegion.getPos2().getZ());
            createCuboidSelection(pos1, pos2);
        }

        // Finish the script
        finish(blocks);
    }
}

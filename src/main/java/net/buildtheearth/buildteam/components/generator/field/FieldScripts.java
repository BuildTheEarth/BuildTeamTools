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

        Vector minPoint = region.getMinimumPoint();
        Vector maxPoint = region.getMaximumPoint();


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

        // Create a cuboid selection of the field area
        Generator.createCuboidSelection(p, maxPoint, minPoint);

        Block[][][] blocks = Generator.analyzeRegion(p, p.getWorld());
        int maxHeight = Generator.getMaxHeight(blocks);

        // Recreate the original polygon selection
        Generator.createPolySelection(p, points, blocks);


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

        p.chat("//expand 40 40 up"); // IMPORTANT! Doing it this way to fix yellow wool detection

        // In case the player placed yellow wool for a crop which doesn't require it
        if (!crop.isLinesRequired()) {
            commands.add("//replace 35:4 0");
            operations++;
        }

        // Replaces all underground yellow wool blocks with grass
        commands.add("//gmask !<0");
        commands.add("//replace 35:4 2");
        operations++;


        commands.add("//expand 10 10 up");


        // ----------- PREPARATION 02 ----------
        // Drawing lines if the crop requires it

        if (crop.isLinesRequired()) {
            // Make sure there are at least 2 yellow wool blocks inside the selection
            if (!Generator.containsBlock(blocks, Material.WOOL, (byte) 4, 2)) {
                // Get two points of the selection. If selection points is > 2 take a point from the middle of all points.
                Vector point1 = points.get(0);
                Vector point2 = points.get(1);

                if(points.size() > 2)
                    point2 = points.get((int) Math.ceil(points.size()/2.0));

                // Get two points that are half the size than the original points
                Vector[] lineBlocks = interpolateVectors(point1, point2, point1.distance(point2) / 2);

                // Get the elevation of the new points
                int y1 = Generator.getMaxHeight(blocks, lineBlocks[0].getBlockX(), lineBlocks[0].getBlockZ(), Generator.IGNORED_MATERIALS) + 1;
                int y2 = Generator.getMaxHeight(blocks, lineBlocks[1].getBlockX(), lineBlocks[1].getBlockZ(), Generator.IGNORED_MATERIALS) + 1;

                // Create yellow wool on the new points
                Location location1 = new Location(p.getWorld(), lineBlocks[0].getBlockX(), y1, lineBlocks[0].getBlockZ());
                Location location2 = new Location(p.getWorld(), lineBlocks[1].getBlockX(), y2, lineBlocks[1].getBlockZ());

                location1.getBlock().setType(Material.WOOL);
                location1.getBlock().setData((byte) 4);

                location2.getBlock().setType(Material.WOOL);
                location2.getBlock().setData((byte) 4);
            }

            // Get the most Western and most Eastern yellow block.
            List<Block> yellowWoolBlocks = Generator.getBlocksOfMaterial(blocks, Material.WOOL, (byte) 4);
            Block westernMost = null;
            Block easternMost = null;
            double currentLowest = Double.MAX_VALUE;
            double currentHighest = -Double.MAX_VALUE;

            for (Block yellowWoolBlock : yellowWoolBlocks) {
                if (yellowWoolBlock.getLocation().getBlockX() < currentLowest) {
                    currentLowest = yellowWoolBlock.getLocation().getBlockX();
                    westernMost = yellowWoolBlock;
                }

                if (yellowWoolBlock.getLocation().getBlockX() > currentHighest) {
                    currentHighest = yellowWoolBlock.getLocation().getBlockX();
                    easternMost = yellowWoolBlock;
                }
            }

            // Check to make sure 2 yellow wool blocks were correctly found
            if (westernMost == null || easternMost == null) {
                p.sendMessage("§cSomething went wrong while processing line data!");
                p.sendMessage(" ");
                p.sendMessage("§cPlease contact one of the developers!");
                return;
            }


            // Get the target length of the new line
            double targetLength = 1;

            double diagonal1 = maxPoint.subtract(minPoint).length();
            Vector boundingBoxPoint3 = new Vector(minPoint.getX(), maxPoint.getY(), maxPoint.getZ());
            Vector boundingBoxPoint4 = new Vector(maxPoint.getX(), minPoint.getY(), minPoint.getZ());
            double diagonal2 = boundingBoxPoint4.subtract(boundingBoxPoint3).length();

            targetLength = Math.max(diagonal1, diagonal2);


            // Get two new points of the extended line
            Vector point1 = new Vector(westernMost.getX(), westernMost.getY(), westernMost.getZ());
            Vector point2 = new Vector(easternMost.getX(), easternMost.getY(), easternMost.getZ());

            Vector[] extendedVectors = interpolateVectors(point1, point2, targetLength);
            Vector extendedPoint1 = extendedVectors[0];
            Vector extendedPoint2 = extendedVectors[1];


            // Draw the first line in orange
            commands.add("//sel cuboid");
            commands.add("//gmask !air");

            commands.add("//pos1 " + extendedPoint1.getBlockX() + "," + (extendedPoint1.getBlockY() - 1) + "," + extendedPoint1.getBlockZ());
            commands.add("//pos2 " + extendedPoint2.getBlockX() + "," + (extendedPoint2.getBlockY() - 1) + "," + extendedPoint2.getBlockZ());
            commands.add("//line 35:8");
            operations++;

            commands.add("//expand 10 up");
            commands.add("//expand 10 down");

            // Make sure the line completely covers the required surface
            commands.add("//gmask !0");
            for (int i = maxHeight; i < maxHeight + 5; i++) {
                commands.add("//replace >35:4 35:4");
                commands.add("//replace <35:4 35:4");
                operations++;
            }

            // Reselect original region as cuboid
            Generator.createCuboidSelection(commands, maxPoint, minPoint);

            commands.add("//expand 40 40 up");

            // Remove all non-solid blocks
            commands.add("//gmask !#solid");
            commands.add("//replace 0");
            operations++;

            // Find out what the highest block in the cuboid selection is to find out at which y value the air starts.
            // This is needed to ensure that no blocks are overwritten.
            int currentHighestY = Integer.MIN_VALUE;
            int currentLowestY = Integer.MAX_VALUE;
            int yDifference = 0;
            for (Block[][]block2D : blocks) {
                for(Block[] block1D : block2D) {
                    for(Block block : block1D) {
                        if(block != null) {
                            int highestY = p.getWorld().getHighestBlockYAt(block.getX(), block.getZ()) + 5;
                            if(highestY > currentHighestY) currentHighestY = highestY + 5;
                            if(highestY < currentLowestY) currentLowestY = highestY;
                        }
                    }
                }
            }
            yDifference = currentHighestY - currentLowestY;

            // Make original line correct shape
            int absoluteXDistance = Math.abs(extendedPoint1.getBlockX() - extendedPoint2.getBlockX());
            int absoluteZDistance = Math.abs(extendedPoint1.getBlockZ() - extendedPoint2.getBlockZ());
            if(absoluteXDistance > absoluteZDistance) {
                commands.add("//gmask =queryRel(1,0,0,35,5)&&queryRel(-1,0,+1,35,5)");
            } else {
                commands.add("//gmask =queryRel(0,0,1,35,5)&&queryRel(+1,0,-1,35,5)");
            }
            commands.add("//set 35:5");
            operations++;

            // Move everything up & delete what's on the floor
            commands.add("//expand 50 50 up");

            // Move the line + yDifference blocks up and change the color from orange to yellow
            commands.add("//gmask =queryRel(0,"+(-yDifference)+",0,35,4)");
            commands.add("//set 35:5");

            // Replace the orange wool blocks with grass
            commands.add("//gmask");
            commands.add("//replace 35:4 2");


            // Make the line pattern extend over the field
            for (int i = 0; i <= targetLength; i++) {
                if (i % 2 == 0 || (crop != Crop.VINEYARD && crop != Crop.PEAR)) {
                    // All blocks that are ydifference above the ground and one block next to lime wool are replaced with pink wool
                    commands.add("//gmask =queryRel(0,-" + yDifference + ",0,0,0)&&!queryRel(0,-" + (yDifference+1) + ",0,0,0)&&(queryRel(0,0,-1,35,5)||queryRel(0,0,+1,35,5)||queryRel(0,1,-1,35,5)||queryRel(0,1,+1,35,5)||queryRel(0,-1,-1,35,5)||queryRel(0,-1,+1,35,5))");
                    commands.add("//replace !35:5,35:7 35:6");
                    operations++;
                    commands.add("//gmask =queryRel(0,-1,0,35,6)");
                    commands.add("//replace 35:6 0");
                } else {
                    //Magenta wool
                    commands.add("//gmask =queryRel(0,-" + yDifference + ",0,0,0)&&!queryRel(0,-" + (yDifference+1) + ",0,0,0)&&(queryRel(0,0,-1,35,5)||queryRel(0,0,+1,35,5)||queryRel(0,1,-1,35,5)||queryRel(0,1,+1,35,5)||queryRel(0,-1,-1,35,5)||queryRel(0,-1,+1,35,5))");
                    commands.add("//replace !35:5,35:6 35:7");
                    operations++;
                    commands.add("//gmask =queryRel(0,-1,0,35,7)");
                    commands.add("//replace 35:7 0");
                }
                //Yellow wool
                commands.add("//gmask =queryRel(0,-" + yDifference + ",0,0,0)&&!queryRel(0,-" + (yDifference+1) + ",0,0,0)&&(queryRel(0,0,-1,35,6)||queryRel(-1,0,-1,35,6)||queryRel(0,0,+1,35,6)||queryRel(+1,1,+1,35,6)||queryRel(0,0,+1,35,6)||queryRel(+1,0,+1,35,6)||queryRel(+1,-1,+1,35,6)||queryRel(-1,1,-1,35,6)||queryRel(-1,-1,-1,35,6)||queryRel(0,0,-1,35,7)||queryRel(-1,0,-1,35,7)||queryRel(0,0,+1,35,7)||queryRel(+1,1,+1,35,7)||queryRel(0,0,+1,35,7)||queryRel(+1,0,+1,35,7)||queryRel(+1,-1,+1,35,7)||queryRel(-1,1,-1,35,7)||queryRel(-1,-1,-1,35,7))");
                commands.add("//replace !35:6,35:7 35:5");
                operations++;
                commands.add("//gmask =queryRel(0,-1,0,35,5)");
                commands.add("//replace 35:5 0");
                operations++;
            }


            // Create a Polygon Selection
            Generator.createPolySelection(commands, points);

            commands.add("//expand 40 40 up");

            // Move down the lines & clean up
            commands.add("//gmask =queryRel(1,"+ (yDifference+1) +",0,35,5)");
            commands.add("//set 35:4");

            commands.add("//gmask =queryRel(1,"+ (yDifference+1) +",0,35,6)");
            commands.add("//set 35:1");

            commands.add("//gmask =queryRel(1,"+ (yDifference+1) +",0,35,7)");
            commands.add("//set 35:2");


            // Create a Cuboid Selection
            Generator.createCuboidSelection(commands, maxPoint, minPoint);

            commands.add("//expand 40 40 up");

            // Replace blocks in the air with nothing
            commands.add("//gmask");
            commands.add("//replace 35:5,35:6,35:7 0");
        }


        // ----------- PLACING CROPS ----------
        // Placing the crops

        commands.add("//gmask");

        // First reselect the original poly region

        Generator.createPolySelection(commands, points);

        commands.add("//expand 40 40 up");

        // Increase height to make sure the entire field height is inside the selection
        commands.add("//expand 20 20 up");

        if (crop == Crop.POTATO) {
            if (type == CropStage.TALL) {
                commands.add("//replace 35:4 24%3,24%3:1,1%17:4,1%5:1");
                operations++;
                commands.add("//replace 35:1 1%3,1%3:1,24%17:4,24%5:1");
                operations++;

                commands.add("//shift 1 up");
                commands.add("//gmask 0");

                commands.add("//replace >3 35:1,35:2,31:1,31:2");
                operations++;

                commands.add("//gmask");

                commands.add("//replace 35:1 175:3");
                operations++;
                commands.add("//replace 35:2 175:2");
                operations++;

                commands.add("//shift 1 up");

                commands.add("//replace >175:3 175:15");
                operations++;
                commands.add("//replace >175:2 175:14");
                operations++;

            } else {
                commands.add("//replace 35:4 208,5");
                operations++;
                commands.add("//replace 35:1 252:13,2");
                operations++;

                commands.add("//shift 1 up");
                commands.add("//gmask 0");

                commands.add("//replace >2 31:1,31:2");
                operations++;

            }
        }

        if (crop == Crop.HARVESTED) {
            if (type == CropStage.DRY) {
                commands.add("//replace 35:4 5%208,95%5");
                operations++;
                commands.add("//replace 35:1 95%208,5%5");
                operations++;

            } else {
                commands.add("//replace 35:4 47%5:1,47%3:1,5%60");
                operations++;
                commands.add("//replace 35:1 95%60,2%3:1,2%5:1");
                operations++;

            }
        }

        if (crop == Crop.OTHER) {
            if (type == CropStage.DRY) {
                commands.add("//setbiome MESA");

                commands.add("//replace 35:4 208,5");
                operations++;
                commands.add("//replace 35:1 252:13,2");
                operations++;

                commands.add("//shift 1 up");
                commands.add("//gmask 0");

                commands.add("//replace >2 31:1,31:2");
                operations++;

            } else {
                commands.add("//setbiome SWAMPLAND");

                commands.add("//replace 35:4 24%3,24%3:1,1%17:4,1%5:1");
                operations++;
                commands.add("//replace 35:1 1%3,1%3:1,24%17:4,24%5:1");
                operations++;

                commands.add("//shift 1 up");
                commands.add("//gmask 0");

                commands.add("//replace >3 35:1,35:2,31:1,31:2");
                operations++;

                commands.add("//gmask");

                commands.add("//replace 35:1 175:3");
                operations++;
                commands.add("//replace 35:2 175:2");
                operations++;

                commands.add("//shift 1 up");

                commands.add("//replace >175:3 175:15");
                operations++;
                commands.add("//replace >175:2 175:14");
                operations++;

            }
        }

        if (crop == Crop.VINEYARD || crop == Crop.PEAR) {
            commands.add("//replace >35:2 15%188,85%22");
            operations++;
            commands.add("//replace >188,22 251:13");
            operations++;

            commands.add("//replace 35:1 5,208:0");
            operations++;
            commands.add("//replace 35:4 208:0,5,3,3:1");
            operations++;
            commands.add("//replace 35:2 3,3:1");
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

        Generator.createPolySelection(commands, points);


        Main.buildTeamTools.getGenerator().getCommands().add(new Command(p, field, commands, operations, blocks));
        Generator.getPlayerHistory(p).addHistoryEntry(new History.HistoryEntry(GeneratorType.FIELD, operations));
    }

    /**
     * Extends the length of a line between two vectors.
     *
     * @param point1       - The first point of the current line
     * @param point2       - The second point of the current line
     * @param targetLength - Define how long the new line should be
     * @return - Two new points of the new line
     */
    public static Vector[] interpolateVectors(Vector point1, Vector point2, double targetLength) {
        // Distance Vector between point1 and point2
        Vector distanceVector = point2.subtract(point1);
        double distanceVectorSize = distanceVector.length();

        // Middle Vector between point1 and point2
        Vector middle = point1.add(distanceVector.multiply(0.5));

        // Factor how much the distance Vector has to be multiplied in order to create the new line
        double multiplicationFactor = targetLength / distanceVectorSize / 2;
        Vector extendedDistanceVector = distanceVector.multiply(multiplicationFactor);

        // Extended points by adding and subtracting the extended Distance Vector to the Middle Vector
        Vector point1Extended = middle.add(extendedDistanceVector);
        Vector point2Extended = middle.subtract(extendedDistanceVector);

        return new Vector[]{point1Extended, point2Extended};
    }
}

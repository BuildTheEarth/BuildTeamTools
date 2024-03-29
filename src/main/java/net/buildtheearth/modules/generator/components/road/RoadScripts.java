package net.buildtheearth.modules.generator.components.road;


import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import net.buildtheearth.modules.generator.GeneratorModule;
import net.buildtheearth.modules.generator.model.Command;
import net.buildtheearth.modules.generator.model.Flag;
import net.buildtheearth.modules.generator.model.GeneratorType;
import net.buildtheearth.modules.generator.model.History;
import net.buildtheearth.modules.generator.utils.GeneratorUtils;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RoadScripts {
    
    public static void roadScript_v_2_0(Player p, Road road, Region region) {
        List<String> commands = new ArrayList<>();
        HashMap <Flag, String > flags = road.getPlayerSettings().get(p.getUniqueId()).getValues();

        String roadMaterial = flags.get(RoadFlag.ROAD_MATERIAL);
        String markingMaterial = flags.get(RoadFlag.MARKING_MATERIAL);
        String sidewalkMaterial = flags.get(RoadFlag.SIDEWALK_MATERIAL);
        String sidewalkSlabMaterial = flags.get(RoadFlag.SIDEWALK_SLAB_COLOR);
        String roadSlabMaterial = flags.get(RoadFlag.ROAD_SLAB_COLOR);
        String streetLampType = flags.get(RoadFlag.STREET_LAMP_TYPE);

        int laneCount = Integer.parseInt(flags.get(RoadFlag.LANE_COUNT));
        int laneWidth = Integer.parseInt(flags.get(RoadFlag.LANE_WIDTH));
        int laneGap = Integer.parseInt(flags.get(RoadFlag.LANE_GAP));
        int markingLength = Integer.parseInt(flags.get(RoadFlag.MARKING_LENGTH));
        int markingGap = Integer.parseInt(flags.get(RoadFlag.MARKING_GAP));
        int sidewalkWidth = Integer.parseInt(flags.get(RoadFlag.SIDEWALK_WIDTH));
        int streetLampDistance = Integer.parseInt(flags.get(RoadFlag.STREET_LAMP_DISTANCE));
        int roadSide = Integer.parseInt(flags.get(RoadFlag.ROAD_SIDE));

        boolean isCrosswalk = flags.get(RoadFlag.CROSSWALK).equals(Flag.ENABLED);


        int operations = 0;
        p.chat("/clearhistory");

        // Is there a sidewalk?
        boolean isSidewalk = sidewalkWidth > 1;

        // Are there streetlamps?
        boolean isStreetLamp = streetLampType != null && !streetLampType.equalsIgnoreCase(Flag.DISABLED);

        // Calculate current width from centre of road
        int road_width = laneWidth*laneCount;
        int max_width = road_width + sidewalkWidth*2 + laneGap + roadSide;
        int road_height = region.getHeight();

        // Get the points of the region
        List<Vector> points = new ArrayList<>();

        // Add all points of the region to the list no matter what type of region it is
        if (region instanceof Polygonal2DRegion) {
            Polygonal2DRegion polyRegion = (Polygonal2DRegion) region;

            for (BlockVector2D blockVector2D : polyRegion.getPoints())
                points.add(blockVector2D.toVector());

        } else if (region instanceof ConvexPolyhedralRegion) {
            ConvexPolyhedralRegion convexRegion = (ConvexPolyhedralRegion) region;
            points.addAll(convexRegion.getVertices());

        }else if (region instanceof CuboidRegion) {
            CuboidRegion cuboidRegion = (CuboidRegion) region;

            points.add(cuboidRegion.getPos1());
            points.add(cuboidRegion.getPos2());
        } else {
            p.sendMessage("§c§lERROR: §cRegion type not supported!");
            return;
        }

        points = GeneratorUtils.populatePoints(points, laneWidth);

        List<Vector> oneMeterPoints = new ArrayList<>(points);
        oneMeterPoints = GeneratorUtils.populatePoints(oneMeterPoints, 1);

        List<Vector> roadMarkingPoints = new ArrayList<>(oneMeterPoints);
        roadMarkingPoints = GeneratorUtils.reducePoints(roadMarkingPoints, markingGap + 1, markingLength - 1);

        List<Vector> streetLampPointsMid = new ArrayList<>(oneMeterPoints);
        streetLampPointsMid = GeneratorUtils.reducePoints(streetLampPointsMid, streetLampDistance + 1, streetLampDistance + 1);
        List<List<Vector>> streetLampPoints = GeneratorUtils.shiftPointsAll(streetLampPointsMid, road_width + sidewalkWidth*2);


        // ----------- PREPARATION 01 ----------
        // Replace all unnecessary blocks with air

        List<Vector> polyRegionLine = new ArrayList<>(points);
        polyRegionLine = GeneratorUtils.extendPolyLine(polyRegionLine);
        List<Vector> polyRegionPoints = GeneratorUtils.shiftPoints(polyRegionLine, max_width + 2, true);
        List<Vector> polyRegionPointsExact = GeneratorUtils.shiftPoints(polyRegionLine, max_width, true);

        // Create a region from the points
        GeneratorUtils.createPolySelection(p, polyRegionPoints, null);

        p.chat("//expand 30 up");
        p.chat("//expand 10 down");

        // Remove non-solid blocks
        p.chat("//gmask !#solid");
        p.chat("//replace 0");
        operations++;

        // Remove all trees and pumpkins
        p.chat("//gmask");
        p.chat("//replace leaves,log,pumpkin 0");
        operations++;

        p.chat("//gmask");


        Block[][][] regionBlocks = GeneratorUtils.analyzeRegion(p, p.getWorld());
        GeneratorUtils.adjustHeight(points, regionBlocks);

        List<Vector> innerPoints = new ArrayList<>(points);
        innerPoints = GeneratorUtils.shortenPolyLine(innerPoints, 2);


        // ----------- ROAD ----------

        // Draw the road

        GeneratorUtils.createConvexSelection(commands, points);
        commands.add("//gmask !solid," + roadMaterial + "," + markingMaterial + "," + sidewalkMaterial + "," + sidewalkSlabMaterial + "," + roadSlabMaterial);
        commands.add("//curve 35:4");

        // Add additional yellow wool markings to spread the road material faster and everywhere on the road.
        for (int i = 2; i < laneCount; i += 2) {
            List<List<Vector>> yellowWoolLine = GeneratorUtils.shiftPointsAll(innerPoints, (laneWidth * (i - 1)));

            for (List<Vector> path : yellowWoolLine) {
                GeneratorUtils.createConvexSelection(commands, path);
                commands.add("//curve 35:4");
                operations++;

                // Close the circles (curves are not able to end at the beginning)
                commands.add("//sel cuboid");
                commands.add("//pos1 " + GeneratorUtils.getXYZ(path.get(0)));
                commands.add("//pos2 " + GeneratorUtils.getXYZ(path.get(path.size() - 1)));
                commands.add("//line 35:4");
                operations++;
            }
        }

        // Draw another yellow line close to the sidewalk to spread the yellow wool faster and everywhere on the road.
        if (road_width > 10) {
            List<List<Vector>> yellowWoolLineNearSidewalk = GeneratorUtils.shiftPointsAll(innerPoints, road_width - 4);
            for (List<Vector> path : yellowWoolLineNearSidewalk)
                operations += GeneratorUtils.createPolyLine(commands, path, "35:4", true, regionBlocks, 0);
        }

        commands.add("//gmask");


        // ----------- SIDEWALK ----------
        // Draw the sidewalk
        if(isSidewalk) {
            // The outer sidewalk edge lines
            List<List<Vector>> sidewalkPointsOut = GeneratorUtils.shiftPointsAll(points, road_width + sidewalkWidth * 2);
            List<List<Vector>> sidewalkPointsMid = GeneratorUtils.shiftPointsAll(innerPoints, road_width + sidewalkWidth);
            List<List<Vector>> sidewalkPointsIn = GeneratorUtils.shiftPointsAll(points, road_width);


            // Draw the sidewalk middle lines
            commands.add("//gmask !solid," + roadMaterial + "," + markingMaterial);
            for(List<Vector> path : sidewalkPointsMid)
                operations += GeneratorUtils.createPolyLine(commands, path, "35:1", true, regionBlocks, 0);

            commands.add("//gmask !" + roadMaterial + "," + markingMaterial);
            // Create the outer sidewalk edge lines
            for(List<Vector> path : sidewalkPointsOut)
                operations += GeneratorUtils.createPolyLine(commands, path, "35:3", true, regionBlocks, 0);

            // Create the inner sidewalk edge lines
            for(List<Vector> path : sidewalkPointsIn)
                operations += GeneratorUtils.createPolyLine(commands, path, "35:3", true, regionBlocks, 0);
            commands.add("//gmask");

            if(isCrosswalk){
                // Draw the sidewalk middle lines
                commands.add("//gmask " + roadMaterial + "," + markingMaterial);
                for(List<Vector> path : sidewalkPointsMid)
                    operations += GeneratorUtils.createPolyLine(commands, path, "35:2", true, regionBlocks, 0);

                // Create the outer sidewalk edge lines
                for(List<Vector> path : sidewalkPointsOut)
                    operations += GeneratorUtils.createPolyLine(commands, path, "35:11", true, regionBlocks, 0);

                // Create the inner sidewalk edge lines
                for(List<Vector> path : sidewalkPointsIn)
                    operations += GeneratorUtils.createPolyLine(commands, path, "35:11", true, regionBlocks, 0);
                commands.add("//gmask");
            }
        }




        // ----------- OLD ROAD REPLACEMENTS ----------
        // Replace the existing road material with wool

        // Create the poly selection
        GeneratorUtils.createPolySelection(commands, polyRegionPointsExact);
        commands.add("//expand 10 up");
        commands.add("//expand 10 down");
        commands.add("//gmask !air");

        // Replace the current road material with light green wool
        commands.add("//replace " + roadMaterial + " 35:5");
        operations++;
        if(!roadSlabMaterial.equalsIgnoreCase(Flag.DISABLED)) {
            commands.add("//replace " + roadSlabMaterial + " 35:5");
            operations++;
        }
        commands.add("//replace " + markingMaterial + " 35:5");
        operations++;

        // Replace the current sidewalk material with pink wool
        commands.add("//replace " + sidewalkMaterial + " 35:6");
        operations++;
        if(!sidewalkSlabMaterial.equalsIgnoreCase(Flag.DISABLED)) {
            commands.add("//replace " + sidewalkSlabMaterial + " 35:6");
            operations++;
        }


        // ----------- FILLINGS ----------
        // Fill the road with the materials

        GeneratorUtils.createPolySelection(commands, polyRegionPoints);
        commands.add("//expand 10 up");
        commands.add("//expand 10 down");
        commands.add("//gmask !air,35:3,35:11");

        // Bring all lines to the top
        for(int i = 0; i < road_height + 5; i++) {
            commands.add("//replace >35:1 35:1");
            operations++;
            commands.add("//replace >35:2 35:2");
            operations++;
            commands.add("//replace >35:4 35:4");
            operations++;
        }

        commands.add("//gmask !air");

        // Bring the light blue and blue wool to the top at last to prevent the others from creating leaks
        for(int i = 0; i < road_height + 5; i++) {
            commands.add("//replace >35:3 35:3");
            operations++;
            commands.add("//replace >35:11 35:11");
            operations++;
        }

        // Bring all lines further down
        commands.add("//gmask =queryRel(0,1,0,35,3)");
        for(int i = 0; i < 3; i++){
            commands.add("//set 35:3");
            operations++;
        }
        commands.add("//gmask =queryRel(0,1,0,35,11)");
        for(int i = 0; i < 3; i++){
            commands.add("//set 35:11");
            operations++;
        }

        // Spread the yellow wool
        commands.add("//gmask =(queryRel(1,0,0,35,4)||queryRel(-1,0,0,35,4)||queryRel(0,0,1,35,4)||queryRel(0,0,-1,35,4)||queryRel(0,-1,0,35,4)||queryRel(0,1,0,35,4))&&queryRel(0,3,0,0,0)");
        for(int i = 0; i < laneWidth*2; i++){
            // Replace all blocks around yellow wool with yellow wool until it reaches the light blue wool
            commands.add("//replace !35:3,35:5,35:6,solid 35:4");
            operations++;
        }

        // Spread the orange wool
        commands.add("//gmask =(queryRel(1,0,0,35,1)||queryRel(-1,0,0,35,1)||queryRel(0,0,1,35,1)||queryRel(0,0,-1,35,1)||queryRel(0,-1,0,35,1)||queryRel(0,1,0,35,1))&&queryRel(0,3,0,0,0)");
        for(int i = 0; i < laneWidth*2; i++){
            // Replace all blocks around orange wool with orange wool until it reaches the light blue wool
            commands.add("//replace !35:3,35:2,35:11,35:5,solid 35:1");
            operations++;
        }

        // Replace all orange wool with light blue wool
        commands.add("//gmask");
        commands.add("//replace 35:1 35:3");


        // Spread the magenta wool
        commands.add("//gmask =(queryRel(1,0,0,35,2)||queryRel(-1,0,0,35,2)||queryRel(0,0,1,35,2)||queryRel(0,0,-1,35,2)||queryRel(0,-1,0,35,2)||queryRel(0,1,0,35,2))&&queryRel(0,3,0,0,0)");
        for(int i = 0; i < laneWidth*2; i++){
            // Replace all blocks around orange wool with orange wool until it reaches the light blue wool
            commands.add("//replace !35:11,35:3,solid 35:2");
            operations++;
        }

        // Replace all magenta wool with pink wool
        commands.add("//gmask");
        commands.add("//replace 35:2 35:6");
        operations++;
        commands.add("//replace 35:11 35:6");
        operations++;

        // In case there are some un-replaced blocks left, replace everything above light blue wool that is not air or yellow wool with light blue wool
        for(int i = 0; i < road_height; i++) {
            commands.add("//replace >35:3,!air,35:4 35:3");
            operations++;
        }


        // ----------- CROSSWALK ----------
        // Draw the crosswalk

        if(isCrosswalk){
            commands.add("//gmask =queryRel(1,0,0,35,3)||queryRel(-1,0,0,35,3)||queryRel(0,0,1,35,3)||queryRel(0,0,-1,35,3)");
            commands.add("//replace 35:6 35:9");
            operations++;

            for(int i = 0; i < road_width; i++) {
                commands.add("//gmask =queryRel(1,0,0,35,9)||queryRel(-1,0,0,35,9)||queryRel(0,0,1,35,9)||queryRel(0,0,-1,35,9)");
                commands.add("//replace 35:6 35:10");
                operations++;

                commands.add("//gmask =queryRel(1,0,0,35,10)||queryRel(-1,0,0,35,10)||queryRel(0,0,1,35,10)||queryRel(0,0,-1,35,10)");
                commands.add("//replace 35:6 35:9");
                operations++;
            }
        }else{
            commands.add("//replace 35:6 35:4");
            operations++;
        }

        commands.add("//gmask");


        // ----------- ROAD AND SIDEWALK SLABS ----------
        // Create the road and sidewalk slabs

        // Create the road slabs
        if(!roadSlabMaterial.equalsIgnoreCase(Flag.DISABLED)){
            commands.add("//gmask =queryRel(0,-1,0,35,4)&&queryRel(0,0,0,0,0)&&(queryRel(1,0,0,35,4)||queryRel(-1,0,0,35,4)||queryRel(0,0,1,35,4)||queryRel(0,0,-1,35,4))");
            commands.add("//set " + roadSlabMaterial);
            operations++;
        }


        // Create the sidewalk slabs
        if(!sidewalkSlabMaterial.equalsIgnoreCase(Flag.DISABLED)){
            commands.add("//gmask =queryRel(0,-1,0,35,3)&&queryRel(0,0,0,0,0)&&(queryRel(1,0,0,35,3)||queryRel(-1,0,0,35,3)||queryRel(0,0,1,35,3)||queryRel(0,0,-1,35,3))");
            commands.add("//set " + sidewalkSlabMaterial);
            operations++;
        }


        // ----------- ROAD MARKINGS ----------
        // Draw the road markings

        if(laneCount > 1) {

            commands.add("//gmask 35:4");

            // Check if langeCount is even or odd
            boolean isEven = laneCount % 2 == 0;

            // Create the road markings in the middle of the road
            if(isEven)
                operations += createRoadMarkingLine(commands, roadMarkingPoints, markingMaterial, regionBlocks);


            // Create the road markings for the other lanes
            if(laneCount >= 3)
                for(int i = 0; i < (laneCount-1)/2; i++) {
                    int distance = (i+1) * laneWidth * 2 - (!isEven ? laneWidth : 0);

                    List<List<Vector>> roadMarkingPointsList = GeneratorUtils.shiftPointsAll(points, distance);
                    for(List<Vector> path : roadMarkingPointsList) {

                        List<Vector> markingsOneMeterPoints = new ArrayList<>(path);
                        markingsOneMeterPoints = GeneratorUtils.populatePoints(markingsOneMeterPoints, 1);

                        List<Vector> shiftedRoadMarkingPoints = new ArrayList<>(markingsOneMeterPoints);
                        shiftedRoadMarkingPoints = GeneratorUtils.reducePoints(shiftedRoadMarkingPoints, markingGap + 1, markingLength - 1);

                        operations += createRoadMarkingLine(commands, shiftedRoadMarkingPoints, markingMaterial, regionBlocks);
                    }
                }

            commands.add("//gmask");
        }


        // ----------- MATERIAL ----------
        // Replace all light blue wool with the sidewalk material
        GeneratorUtils.createPolySelection(commands, polyRegionPoints);
        commands.add("//replace 35:3 " + sidewalkMaterial);
        operations++;

        // Replace all yellow,lime and cyan wool with the road material
        commands.add("//replace 35:4,35:5,35:9 " + roadMaterial);
        operations++;

        // Replace all purple wool with the marking material
        commands.add("//replace 35:10 " + markingMaterial);
        operations++;

        commands.add("//gmask");



        // ----------- STREET LAMPS ----------
        // Draw the street lamps

        if(isStreetLamp){
            for(List<Vector> path : streetLampPoints)
                for(int i = 0; i < path.size(); i++) {
                    Vector point = path.get(i);

                    // Find the closest distance to all other points
                    double closestDistance = Double.MAX_VALUE;
                    for(List<Vector> otherPoints : streetLampPoints)
                        for(int i2 = 0; i2 < otherPoints.size(); i2++) {
                            Vector otherPoint = otherPoints.get(i2);

                            if(point.equals(otherPoint))
                                continue;

                            if(i < i2 && path.equals(otherPoints))
                                continue;

                            closestDistance = Math.min(closestDistance, point.distance(otherPoint));
                        }

                    // If the distance is too small, skip this point to prevent streetlamps from being too close to each other
                    if(closestDistance < 5)
                        continue;


                    Location loc = new Location(p.getWorld(), point.getBlockX(), point.getBlockY(), point.getBlockZ());
                    Vector closest = GeneratorUtils.getClosestVector(streetLampPointsMid, point);

                    // Vector pointing from the lamp to the closest point on the middle line
                    Vector toMiddle = closest.subtract(point).normalize();

                    // Calculate the angle with respect to the negative Z-axis (north)
                    double angle = Math.toDegrees(Math.atan2(-toMiddle.getX(), -toMiddle.getZ()));

                    // Adjust the angle to Minecraft's coordinate system
                    angle = (angle + 360) % 360; // Normalize angle to be between 0 and 360


                    commands.add(GeneratorUtils.getPasteSchematicString("GeneratorCollections/roadpack/streetlamp" + streetLampType + ".schematic", loc, angle, 1));
                    operations++;
                }
        }



        // Depending on the selection type, the selection needs to be restored correctly
        if(region instanceof Polygonal2DRegion || region instanceof ConvexPolyhedralRegion)
            GeneratorUtils.createConvexSelection(commands, points);
        else if(region instanceof CuboidRegion)
            GeneratorUtils.createCuboidSelection(commands, ((CuboidRegion) region).getPos1(), ((CuboidRegion) region).getPos2());


        GeneratorModule.getInstance().getGeneratorCommands().add(new Command(p, road, commands, operations, regionBlocks));
        GeneratorModule.getInstance().getPlayerHistory(p).addHistoryEntry(new History.HistoryEntry(GeneratorType.ROAD, operations));
    }


    public static int createRoadMarkingLine(List<String> commands, List<Vector> points, String lineMaterial, Block[][][] blocks) {
        commands.add("//sel cuboid");
        int operations = 0;

        List<String> positions = new ArrayList<>();
        for (Vector point : points) positions.add(GeneratorUtils.getXYZ(point, blocks));

        for(int i = 0; i < points.size(); i++){
            if(i%2 == 0)
                commands.add("//pos1 " + positions.get(i));
            else {
                commands.add("//pos2 " + positions.get(i));
                commands.add("//line " + lineMaterial);
                operations++;
            }
        }

        return operations;
    }
}

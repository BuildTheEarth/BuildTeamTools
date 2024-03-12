package net.buildtheearth.modules.generator.components.road;


import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import net.buildtheearth.modules.generator.model.Flag;
import net.buildtheearth.modules.generator.model.GeneratorComponent;
import net.buildtheearth.modules.generator.model.Operation;
import net.buildtheearth.modules.generator.model.Script;
import net.buildtheearth.modules.generator.utils.GeneratorUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RoadScripts extends Script {

    public RoadScripts(Player player, GeneratorComponent generatorComponent) {
        super(player, generatorComponent);
        
        roadScript_v_2_0();
    }

    public void roadScript_v_2_0() {
        HashMap <Flag, String > flags = getGeneratorComponent().getPlayerSettings().get(getPlayer().getUniqueId()).getValues();

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


        getPlayer().chat("/clearhistory");

        // Is there a sidewalk?
        boolean isSidewalk = sidewalkWidth > 1;

        // Are there streetlamps?
        boolean isStreetLamp = streetLampType != null && !streetLampType.equalsIgnoreCase(Flag.DISABLED);

        // Calculate current width from centre of road
        int road_width = laneWidth*laneCount;
        int max_width = road_width + sidewalkWidth*2 + laneGap + roadSide;
        int road_height = getRegion().getHeight();

        // Get the points of the region
        List<Vector> points = GeneratorUtils.getSelectionPointsFromRegion(getRegion());

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
        GeneratorUtils.createPolySelection(getPlayer(), polyRegionPoints, null);

        getPlayer().chat("//expand 30 up");
        getPlayer().chat("//expand 10 down");

        // Remove non-solid blocks
        getPlayer().chat("//gmask !#solid");
        getPlayer().chat("//replace 0");
        changes++;

        // Remove all trees and pumpkins
        getPlayer().chat("//gmask");
        getPlayer().chat("//replace leaves,log,pumpkin 0");
        changes++;

        getPlayer().chat("//gmask");


        Block[][][] blocks = GeneratorUtils.analyzeRegion(getPlayer(), getPlayer().getWorld());
        GeneratorUtils.adjustHeight(points, blocks);

        List<Vector> innerPoints = new ArrayList<>(points);
        innerPoints = GeneratorUtils.shortenPolyLine(innerPoints, 2);


        // ----------- ROAD ----------

        // Draw the road

        GeneratorUtils.createConvexSelection(operations, points);
        operations.add(new Operation("//gmask !solid," + roadMaterial + "," + markingMaterial + "," + sidewalkMaterial + "," + sidewalkSlabMaterial + "," + roadSlabMaterial));
        operations.add(new Operation("//curve 35:4"));

        // Add additional yellow wool markings to spread the road material faster and everywhere on the road.
        for (int i = 2; i < laneCount; i += 2) {
            List<List<Vector>> yellowWoolLine = GeneratorUtils.shiftPointsAll(innerPoints, (laneWidth * (i - 1)));

            for (List<Vector> path : yellowWoolLine) {
                GeneratorUtils.createConvexSelection(operations, path);
                operations.add(new Operation("//curve 35:4"));
                changes++;

                // Close the circles (curves are not able to end at the beginning)
                operations.add(new Operation("//sel cuboid"));
                operations.add(new Operation("//pos1 " + GeneratorUtils.getXYZ(path.get(0))));
                operations.add(new Operation("//pos2 " + GeneratorUtils.getXYZ(path.get(path.size() - 1))));
                operations.add(new Operation("//line 35:4"));
                changes++;
            }
        }

        // Draw another yellow line close to the sidewalk to spread the yellow wool faster and everywhere on the road.
        if (road_width > 10) {
            List<List<Vector>> yellowWoolLineNearSidewalk = GeneratorUtils.shiftPointsAll(innerPoints, road_width - 4);
            for (List<Vector> path : yellowWoolLineNearSidewalk)
                changes += GeneratorUtils.createPolyLine(operations, path, "35:4", true, blocks, 0);
        }

        operations.add(new Operation("//gmask"));


        // ----------- SIDEWALK ----------
        // Draw the sidewalk
        if(isSidewalk) {
            // The outer sidewalk edge lines
            List<List<Vector>> sidewalkPointsOut = GeneratorUtils.shiftPointsAll(points, road_width + sidewalkWidth * 2);
            List<List<Vector>> sidewalkPointsMid = GeneratorUtils.shiftPointsAll(innerPoints, road_width + sidewalkWidth);
            List<List<Vector>> sidewalkPointsIn = GeneratorUtils.shiftPointsAll(points, road_width);


            // Draw the sidewalk middle lines
            operations.add(new Operation("//gmask !solid," + roadMaterial + "," + markingMaterial));
            for(List<Vector> path : sidewalkPointsMid)
                changes += GeneratorUtils.createPolyLine(operations, path, "35:1", true, blocks, 0);

            operations.add(new Operation("//gmask !" + roadMaterial + "," + markingMaterial));
            // Create the outer sidewalk edge lines
            for(List<Vector> path : sidewalkPointsOut)
                changes += GeneratorUtils.createPolyLine(operations, path, "35:3", true, blocks, 0);

            // Create the inner sidewalk edge lines
            for(List<Vector> path : sidewalkPointsIn)
                changes += GeneratorUtils.createPolyLine(operations, path, "35:3", true, blocks, 0);
            operations.add(new Operation("//gmask"));

            if(isCrosswalk){
                // Draw the sidewalk middle lines
                operations.add(new Operation("//gmask " + roadMaterial + "," + markingMaterial));
                for(List<Vector> path : sidewalkPointsMid)
                    changes += GeneratorUtils.createPolyLine(operations, path, "35:2", true, blocks, 0);

                // Create the outer sidewalk edge lines
                for(List<Vector> path : sidewalkPointsOut)
                    changes += GeneratorUtils.createPolyLine(operations, path, "35:11", true, blocks, 0);

                // Create the inner sidewalk edge lines
                for(List<Vector> path : sidewalkPointsIn)
                    changes += GeneratorUtils.createPolyLine(operations, path, "35:11", true, blocks, 0);
                operations.add(new Operation("//gmask"));
            }
        }




        // ----------- OLD ROAD REPLACEMENTS ----------
        // Replace the existing road material with wool

        // Create the poly selection
        GeneratorUtils.createPolySelection(operations, polyRegionPointsExact);
        operations.add(new Operation("//expand 10 up"));
        operations.add(new Operation("//expand 10 down"));
        operations.add(new Operation("//gmask !air"));

        // Replace the current road material with light green wool
        operations.add(new Operation("//replace " + roadMaterial + " 35:5"));
        changes++;
        if(!roadSlabMaterial.equalsIgnoreCase(Flag.DISABLED)) {
            operations.add(new Operation("//replace " + roadSlabMaterial + " 35:5"));
            changes++;
        }
        operations.add(new Operation("//replace " + markingMaterial + " 35:5"));
        changes++;

        // Replace the current sidewalk material with pink wool
        operations.add(new Operation("//replace " + sidewalkMaterial + " 35:6"));
        changes++;
        if(!sidewalkSlabMaterial.equalsIgnoreCase(Flag.DISABLED)) {
            operations.add(new Operation("//replace " + sidewalkSlabMaterial + " 35:6"));
            changes++;
        }


        // ----------- FILLINGS ----------
        // Fill the road with the materials

        GeneratorUtils.createPolySelection(operations, polyRegionPoints);
        operations.add(new Operation("//expand 10 up"));
        operations.add(new Operation("//expand 10 down"));
        operations.add(new Operation("//gmask !air,35:3,35:11"));

        // Bring all lines to the top
        for(int i = 0; i < road_height + 5; i++) {
            operations.add(new Operation("//replace >35:1 35:1"));
            changes++;
            operations.add(new Operation("//replace >35:2 35:2"));
            changes++;
            operations.add(new Operation("//replace >35:4 35:4"));
            changes++;
        }

        operations.add(new Operation("//gmask !air"));

        // Bring the light blue and blue wool to the top at last to prevent the others from creating leaks
        for(int i = 0; i < road_height + 5; i++) {
            operations.add(new Operation("//replace >35:3 35:3"));
            changes++;
            operations.add(new Operation("//replace >35:11 35:11"));
            changes++;
        }

        // Bring all lines further down
        operations.add(new Operation("//gmask =queryRel(0,1,0,35,3)"));
        for(int i = 0; i < 3; i++){
            operations.add(new Operation("//set 35:3"));
            changes++;
        }
        operations.add(new Operation("//gmask =queryRel(0,1,0,35,11)"));
        for(int i = 0; i < 3; i++){
            operations.add(new Operation("//set 35:11"));
            changes++;
        }

        // Spread the yellow wool
        operations.add(new Operation("//gmask =(queryRel(1,0,0,35,4)||queryRel(-1,0,0,35,4)||queryRel(0,0,1,35,4)||queryRel(0,0,-1,35,4)||queryRel(0,-1,0,35,4)||queryRel(0,1,0,35,4))&&queryRel(0,3,0,0,0)"));
        for(int i = 0; i < laneWidth*2; i++){
            // Replace all blocks around yellow wool with yellow wool until it reaches the light blue wool
            operations.add(new Operation("//replace !35:3,35:5,35:6,solid 35:4"));
            changes++;
        }

        // Spread the orange wool
        operations.add(new Operation("//gmask =(queryRel(1,0,0,35,1)||queryRel(-1,0,0,35,1)||queryRel(0,0,1,35,1)||queryRel(0,0,-1,35,1)||queryRel(0,-1,0,35,1)||queryRel(0,1,0,35,1))&&queryRel(0,3,0,0,0)"));
        for(int i = 0; i < laneWidth*2; i++){
            // Replace all blocks around orange wool with orange wool until it reaches the light blue wool
            operations.add(new Operation("//replace !35:3,35:2,35:11,35:5,solid 35:1"));
            changes++;
        }

        // Replace all orange wool with light blue wool
        operations.add(new Operation("//gmask"));
        operations.add(new Operation("//replace 35:1 35:3"));


        // Spread the magenta wool
        operations.add(new Operation("//gmask =(queryRel(1,0,0,35,2)||queryRel(-1,0,0,35,2)||queryRel(0,0,1,35,2)||queryRel(0,0,-1,35,2)||queryRel(0,-1,0,35,2)||queryRel(0,1,0,35,2))&&queryRel(0,3,0,0,0)"));
        for(int i = 0; i < laneWidth*2; i++){
            // Replace all blocks around orange wool with orange wool until it reaches the light blue wool
            operations.add(new Operation("//replace !35:11,35:3,solid 35:2"));
            changes++;
        }

        // Replace all magenta wool with pink wool
        operations.add(new Operation("//gmask"));
        operations.add(new Operation("//replace 35:2 35:6"));
        changes++;
        operations.add(new Operation("//replace 35:11 35:6"));
        changes++;

        // In case there are some un-replaced blocks left, replace everything above light blue wool that is not air or yellow wool with light blue wool
        for(int i = 0; i < road_height; i++) {
            operations.add(new Operation("//replace >35:3,!air,35:4 35:3"));
            changes++;
        }


        // ----------- CROSSWALK ----------
        // Draw the crosswalk

        if(isCrosswalk){
            operations.add(new Operation("//gmask =queryRel(1,0,0,35,3)||queryRel(-1,0,0,35,3)||queryRel(0,0,1,35,3)||queryRel(0,0,-1,35,3)"));
            operations.add(new Operation("//replace 35:6 35:9"));
            changes++;

            for(int i = 0; i < road_width; i++) {
                operations.add(new Operation("//gmask =queryRel(1,0,0,35,9)||queryRel(-1,0,0,35,9)||queryRel(0,0,1,35,9)||queryRel(0,0,-1,35,9)"));
                operations.add(new Operation("//replace 35:6 35:10"));
                changes++;

                operations.add(new Operation("//gmask =queryRel(1,0,0,35,10)||queryRel(-1,0,0,35,10)||queryRel(0,0,1,35,10)||queryRel(0,0,-1,35,10)"));
                operations.add(new Operation("//replace 35:6 35:9"));
                changes++;
            }
        }else{
            operations.add(new Operation("//replace 35:6 35:4"));
            changes++;
        }

        operations.add(new Operation("//gmask"));


        // ----------- ROAD AND SIDEWALK SLABS ----------
        // Create the road and sidewalk slabs

        // Create the road slabs
        if(!roadSlabMaterial.equalsIgnoreCase(Flag.DISABLED)){
            operations.add(new Operation("//gmask =queryRel(0,-1,0,35,4)&&queryRel(0,0,0,0,0)&&(queryRel(1,0,0,35,4)||queryRel(-1,0,0,35,4)||queryRel(0,0,1,35,4)||queryRel(0,0,-1,35,4))"));
            operations.add(new Operation("//set " + roadSlabMaterial));
            changes++;
        }


        // Create the sidewalk slabs
        if(!sidewalkSlabMaterial.equalsIgnoreCase(Flag.DISABLED)){
            operations.add(new Operation("//gmask =queryRel(0,-1,0,35,3)&&queryRel(0,0,0,0,0)&&(queryRel(1,0,0,35,3)||queryRel(-1,0,0,35,3)||queryRel(0,0,1,35,3)||queryRel(0,0,-1,35,3))"));
            operations.add(new Operation("//set " + sidewalkSlabMaterial));
            changes++;
        }


        // ----------- ROAD MARKINGS ----------
        // Draw the road markings

        if(laneCount > 1) {

            operations.add(new Operation("//gmask 35:4"));

            // Check if langeCount is even or odd
            boolean isEven = laneCount % 2 == 0;

            // Create the road markings in the middle of the road
            if(isEven)
                changes += createRoadMarkingLine(roadMarkingPoints, markingMaterial, blocks);


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

                        changes += createRoadMarkingLine(shiftedRoadMarkingPoints, markingMaterial, blocks);
                    }
                }

            operations.add(new Operation("//gmask"));
        }


        // ----------- MATERIAL ----------
        // Replace all light blue wool with the sidewalk material
        GeneratorUtils.createPolySelection(operations, polyRegionPoints);
        operations.add(new Operation("//replace 35:3 " + sidewalkMaterial));
        changes++;

        // Replace all yellow,lime and cyan wool with the road material
        operations.add(new Operation("//replace 35:4,35:5,35:9 " + roadMaterial));
        changes++;

        // Replace all purple wool with the marking material
        operations.add(new Operation("//replace 35:10 " + markingMaterial));
        changes++;

        operations.add(new Operation("//gmask"));



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


                    Location loc = new Location(getPlayer().getWorld(), point.getBlockX(), point.getBlockY(), point.getBlockZ());
                    Vector closest = GeneratorUtils.getClosestVector(streetLampPointsMid, point);

                    // Vector pointing from the lamp to the closest point on the middle line
                    Vector toMiddle = closest.subtract(point).normalize();

                    // Calculate the angle with respect to the negative Z-axis (north)
                    double angle = Math.toDegrees(Math.atan2(-toMiddle.getX(), -toMiddle.getZ()));

                    // Adjust the angle to Minecraft's coordinate system
                    angle = (angle + 360) % 360; // Normalize angle to be between 0 and 360


                    operations.add(new Operation(GeneratorUtils.getPasteSchematicString("GeneratorCollections/roadpack/streetlamp" + streetLampType + ".schematic", loc, angle, 1)));
                    changes++;
                }
        }



        // Depending on the selection type, the selection needs to be restored correctly
        if(getRegion() instanceof Polygonal2DRegion || getRegion() instanceof ConvexPolyhedralRegion)
            GeneratorUtils.createConvexSelection(operations, points);
        else if(getRegion() instanceof CuboidRegion){
            CuboidRegion cuboidRegion = (CuboidRegion) getRegion();
            Vector pos1 = new Vector(cuboidRegion.getPos1().getX(), cuboidRegion.getPos1().getY(), cuboidRegion.getPos1().getZ());
            Vector pos2 = new Vector(cuboidRegion.getPos2().getX(), cuboidRegion.getPos2().getY(), cuboidRegion.getPos2().getZ());
            GeneratorUtils.createCuboidSelection(operations, pos1, pos2);
        }

        // Finish the script
        finish(blocks);
    }


    public int createRoadMarkingLine(List<Vector> points, String lineMaterial, Block[][][] blocks) {
        operations.add(new Operation("//sel cuboid"));
        int changes = 0;

        List<String> positions = new ArrayList<>();
        for (Vector point : points) positions.add(GeneratorUtils.getXYZ(point, blocks));

        for(int i = 0; i < points.size(); i++){
            if(i%2 == 0)
                operations.add(new Operation("//pos1 " + positions.get(i)));
            else {
                operations.add(new Operation("//pos2 " + positions.get(i)));
                operations.add(new Operation("//line " + lineMaterial));
                changes++;
            }
        }

        return changes;
    }
}

package net.buildtheearth.modules.generator.components.road;


import com.cryptomorin.xseries.XMaterial;
import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import net.buildtheearth.modules.generator.model.Flag;
import net.buildtheearth.modules.generator.model.GeneratorComponent;
import net.buildtheearth.modules.generator.model.Script;
import net.buildtheearth.modules.generator.utils.GeneratorUtils;
import net.buildtheearth.utils.Item;
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
        HashMap <Flag, Object> flags = getGeneratorComponent().getPlayerSettings().get(getPlayer().getUniqueId()).getValues();

        XMaterial[] roadMaterial = (XMaterial[]) flags.get(RoadFlag.ROAD_MATERIAL);
        XMaterial[] sidewalkMaterial = (XMaterial[]) flags.get(RoadFlag.SIDEWALK_MATERIAL);
        XMaterial[] sidewalkSlabMaterial = (XMaterial[]) flags.get(RoadFlag.SIDEWALK_SLAB_COLOR);
        XMaterial[] roadSlabMaterial = (XMaterial[]) flags.get(RoadFlag.ROAD_SLAB_COLOR);
        XMaterial[] streetLampType = (XMaterial[]) flags.get(RoadFlag.STREET_LAMP_TYPE);
        XMaterial markingMaterial = (XMaterial) flags.get(RoadFlag.MARKING_MATERIAL);

        String roadMaterialIDs = Item.getUniqueMaterialString(roadMaterial);
        String sidewalkMaterialIDs = Item.getUniqueMaterialString(sidewalkMaterial);
        String sidewalkSlabMaterialIDs = Item.getUniqueMaterialString(sidewalkSlabMaterial);
        String roadSlabMaterialIDs = Item.getUniqueMaterialString(roadSlabMaterial);
        String streetLampTypeIDs = Item.getUniqueMaterialString(streetLampType);
        String markingMaterialID = Item.getUniqueMaterialString(markingMaterial);

        int laneCount = (int) flags.get(RoadFlag.LANE_COUNT);
        int laneWidth = (int) flags.get(RoadFlag.LANE_WIDTH);
        int laneGap = (int) flags.get(RoadFlag.LANE_GAP);
        int markingLength = (int) flags.get(RoadFlag.MARKING_LENGTH);
        int markingGap = (int) flags.get(RoadFlag.MARKING_GAP);
        int sidewalkWidth = (int) flags.get(RoadFlag.SIDEWALK_WIDTH);
        int streetLampDistance = (int) flags.get(RoadFlag.STREET_LAMP_DISTANCE);
        int roadSide = (int) flags.get(RoadFlag.ROAD_SIDE);

        // Is there a crosswalk?
        boolean isCrosswalk = (boolean) flags.get(RoadFlag.CROSSWALK);

        // Is there a sidewalk?
        boolean isSidewalk = sidewalkWidth > 1;

        // Are there streetlamps?
        boolean isStreetLamp = streetLampType != null;

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

        List<Vector> polyRegionLine = new ArrayList<>(points);
        polyRegionLine = GeneratorUtils.extendPolyLine(polyRegionLine);
        List<Vector> polyRegionPoints = GeneratorUtils.shiftPoints(polyRegionLine, max_width + 2, true);
        List<Vector> polyRegionPointsExact = GeneratorUtils.shiftPoints(polyRegionLine, max_width, true);



        // ----------- PREPARATION 01 ----------
        // Replace all unnecessary blocks with air

        // Create a region from the points
        createPolySelection(polyRegionPoints);

        // Prepare the script session
        Block[][][] blocks = GeneratorUtils.prepareScriptSession(localSession, actor, getPlayer(),weWorld, 30, true);

        // Bring all points to the ground
        GeneratorUtils.adjustHeight(points, blocks);

        // Shorten the points to prevent the road from being too long
        List<Vector> innerPoints = new ArrayList<>(points);
        innerPoints = GeneratorUtils.shortenPolyLine(innerPoints, 2);


        // ----------- ROAD ----------

        // Draw the road
        drawCurveWithMask("!solid," + roadMaterialIDs + "," + markingMaterialID + "," + sidewalkMaterialIDs + "," + sidewalkSlabMaterialIDs + "," + roadSlabMaterialIDs,
                points, XMaterial.YELLOW_WOOL, true);

        // Add additional yellow wool markings to spread the road material faster and everywhere on the road.
        for (int i = 2; i < laneCount; i += 2) {
            List<List<Vector>> yellowWoolLine = GeneratorUtils.shiftPointsAll(innerPoints, (laneWidth * (i - 1)));

            for (List<Vector> path : yellowWoolLine) {
                drawCurve(path, XMaterial.YELLOW_WOOL, true);

                // Close the circles (curves are not able to end at the beginning)
                drawLine(path.get(0), path.get(path.size() - 1), XMaterial.YELLOW_WOOL, true);
            }
        }

        // Draw another yellow line close to the sidewalk to spread the yellow wool faster and everywhere on the road.
        if (road_width > 10) {
            List<List<Vector>> yellowWoolLineNearSidewalk = GeneratorUtils.shiftPointsAll(innerPoints, road_width - 4);
            for (List<Vector> path : yellowWoolLineNearSidewalk)
                drawPolyLine(path, XMaterial.YELLOW_WOOL, true);
        }

        createCommand("//gmask");


        // ----------- SIDEWALK ----------
        // Draw the sidewalk
        if(isSidewalk) {
            // The outer sidewalk edge lines
            List<List<Vector>> sidewalkPointsOut = GeneratorUtils.shiftPointsAll(points, road_width + sidewalkWidth * 2);
            List<List<Vector>> sidewalkPointsMid = GeneratorUtils.shiftPointsAll(innerPoints, road_width + sidewalkWidth);
            List<List<Vector>> sidewalkPointsIn = GeneratorUtils.shiftPointsAll(points, road_width);


            // Draw the sidewalk middle lines
            createCommand("//gmask !solid," + roadMaterial + "," + markingMaterial);
            for(List<Vector> path : sidewalkPointsMid)
                GeneratorUtils.createPolyLine(this, path, "35:1", true, blocks, 0);

            createCommand("//gmask !" + roadMaterial + "," + markingMaterial);
            // Create the outer sidewalk edge lines
            for(List<Vector> path : sidewalkPointsOut)
                GeneratorUtils.createPolyLine(this, path, "35:3", true, blocks, 0);

            // Create the inner sidewalk edge lines
            for(List<Vector> path : sidewalkPointsIn)
                GeneratorUtils.createPolyLine(this, path, "35:3", true, blocks, 0);
            createCommand("//gmask");

            if(isCrosswalk){
                // Draw the sidewalk middle lines
                createCommand("//gmask " + roadMaterial + "," + markingMaterial);
                for(List<Vector> path : sidewalkPointsMid)
                    GeneratorUtils.createPolyLine(this, path, "35:2", true, blocks, 0);

                // Create the outer sidewalk edge lines
                for(List<Vector> path : sidewalkPointsOut)
                    GeneratorUtils.createPolyLine(this, path, "35:11", true, blocks, 0);

                // Create the inner sidewalk edge lines
                for(List<Vector> path : sidewalkPointsIn)
                    GeneratorUtils.createPolyLine(this, path, "35:11", true, blocks, 0);
                createCommand("//gmask");
            }
        }




        // ----------- OLD ROAD REPLACEMENTS ----------
        // Replace the existing road material with wool

        // Create the poly selection
        createPolySelection(polyRegionPointsExact);
        createCommand("//expand 10 up");
        createCommand("//expand 10 down");
        createCommand("//gmask !air");

        // Replace the current road material with light green wool
        createCommand("//replace " + roadMaterial + " 35:5");

        if(roadSlabMaterial != null)
            createCommand("//replace " + roadSlabMaterial + " 35:5");

        createCommand("//replace " + markingMaterial + " 35:5");

        // Replace the current sidewalk material with pink wool
        createCommand("//replace " + sidewalkMaterial + " 35:6");
        if(sidewalkSlabMaterial != null)
            createCommand("//replace " + sidewalkSlabMaterial + " 35:6");


        // ----------- FILLINGS ----------
        // Fill the road with the materials

        createPolySelection(polyRegionPoints);
        createCommand("//expand 10 up");
        createCommand("//expand 10 down");
        createCommand("//gmask !air,35:3,35:11");

        // Bring all lines to the top
        for(int i = 0; i < road_height + 5; i++) {
            createCommand("//replace >35:1 35:1");
            createCommand("//replace >35:2 35:2");
            createCommand("//replace >35:4 35:4");
        }

        createCommand("//gmask !air");

        // Bring the light blue and blue wool to the top at last to prevent the others from creating leaks
        for(int i = 0; i < road_height + 5; i++) {
            createCommand("//replace >35:3 35:3");
            createCommand("//replace >35:11 35:11");
        }

        // Bring all lines further down
        createCommand("//gmask =queryRel(0,1,0,35,3)");
        for(int i = 0; i < 3; i++){
            createCommand("//set 35:3");
        }
        createCommand("//gmask =queryRel(0,1,0,35,11)");
        for(int i = 0; i < 3; i++){
            createCommand("//set 35:11");
        }

        // Spread the yellow wool
        createCommand("//gmask =(queryRel(1,0,0,35,4)||queryRel(-1,0,0,35,4)||queryRel(0,0,1,35,4)||queryRel(0,0,-1,35,4)||queryRel(0,-1,0,35,4)||queryRel(0,1,0,35,4))&&queryRel(0,3,0,0,0)");
        for(int i = 0; i < laneWidth*2; i++){
            // Replace all blocks around yellow wool with yellow wool until it reaches the light blue wool
            createCommand("//replace !35:3,35:5,35:6,solid 35:4");
        }

        // Spread the orange wool
        createCommand("//gmask =(queryRel(1,0,0,35,1)||queryRel(-1,0,0,35,1)||queryRel(0,0,1,35,1)||queryRel(0,0,-1,35,1)||queryRel(0,-1,0,35,1)||queryRel(0,1,0,35,1))&&queryRel(0,3,0,0,0)");
        for(int i = 0; i < laneWidth*2; i++){
            // Replace all blocks around orange wool with orange wool until it reaches the light blue wool
            createCommand("//replace !35:3,35:2,35:11,35:5,solid 35:1");
        }

        // Replace all orange wool with light blue wool
        createCommand("//gmask");
        createCommand("//replace 35:1 35:3");


        // Spread the magenta wool
        createCommand("//gmask =(queryRel(1,0,0,35,2)||queryRel(-1,0,0,35,2)||queryRel(0,0,1,35,2)||queryRel(0,0,-1,35,2)||queryRel(0,-1,0,35,2)||queryRel(0,1,0,35,2))&&queryRel(0,3,0,0,0)");
        for(int i = 0; i < laneWidth*2; i++){
            // Replace all blocks around orange wool with orange wool until it reaches the light blue wool
            createCommand("//replace !35:11,35:3,solid 35:2");
        }

        // Replace all magenta wool with pink wool
        createCommand("//gmask");
        createCommand("//replace 35:2 35:6");
        createCommand("//replace 35:11 35:6");

        // In case there are some un-replaced blocks left, replace everything above light blue wool that is not air or yellow wool with light blue wool
        for(int i = 0; i < road_height; i++)
            createCommand("//replace >35:3,!air,35:4 35:3");


        // ----------- CROSSWALK ----------
        // Draw the crosswalk

        if(isCrosswalk){
            createCommand("//gmask =queryRel(1,0,0,35,3)||queryRel(-1,0,0,35,3)||queryRel(0,0,1,35,3)||queryRel(0,0,-1,35,3)");
            createCommand("//replace 35:6 35:9");

            for(int i = 0; i < road_width; i++) {
                createCommand("//gmask =queryRel(1,0,0,35,9)||queryRel(-1,0,0,35,9)||queryRel(0,0,1,35,9)||queryRel(0,0,-1,35,9)");
                createCommand("//replace 35:6 35:10");

                createCommand("//gmask =queryRel(1,0,0,35,10)||queryRel(-1,0,0,35,10)||queryRel(0,0,1,35,10)||queryRel(0,0,-1,35,10)");
                createCommand("//replace 35:6 35:9");
            }
        }else
            createCommand("//replace 35:6 35:4");


        createCommand("//gmask");


        // ----------- ROAD AND SIDEWALK SLABS ----------
        // Create the road and sidewalk slabs

        // Create the road slabs
        if(roadSlabMaterial != null){
            createCommand("//gmask =queryRel(0,-1,0,35,4)&&queryRel(0,0,0,0,0)&&(queryRel(1,0,0,35,4)||queryRel(-1,0,0,35,4)||queryRel(0,0,1,35,4)||queryRel(0,0,-1,35,4))");
            createCommand("//set " + roadSlabMaterial);
        }


        // Create the sidewalk slabs
        if(sidewalkSlabMaterial != null){
            createCommand("//gmask =queryRel(0,-1,0,35,3)&&queryRel(0,0,0,0,0)&&(queryRel(1,0,0,35,3)||queryRel(-1,0,0,35,3)||queryRel(0,0,1,35,3)||queryRel(0,0,-1,35,3))");
            createCommand("//set " + sidewalkSlabMaterial);
        }


        // ----------- ROAD MARKINGS ----------
        // Draw the road markings

        if(laneCount > 1) {

            createCommand("//gmask 35:4");

            // Check if langeCount is even or odd
            boolean isEven = laneCount % 2 == 0;

            // Create the road markings in the middle of the road
            if(isEven)
                createRoadMarkingLine(roadMarkingPoints, markingMaterial, blocks);


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

                        createRoadMarkingLine(shiftedRoadMarkingPoints, markingMaterial, blocks);
                    }
                }

            createCommand("//gmask");
        }


        // ----------- MATERIAL ----------
        // Replace all light blue wool with the sidewalk material
        createPolySelection(polyRegionPoints);
        createCommand("//replace 35:3 " + sidewalkMaterial);

        // Replace all yellow,lime and cyan wool with the road material
        createCommand("//replace 35:4,35:5,35:9 " + roadMaterial);

        // Replace all purple wool with the marking material
        createCommand("//replace 35:10 " + markingMaterial);

        createCommand("//gmask");



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


                    createPasteSchematic("GeneratorCollections/roadpack/streetlamp" + streetLampType + ".schematic", loc, angle);
                }
        }


        // Finish the script
        finish(blocks, points);
    }


    public int createRoadMarkingLine(List<Vector> points, XMaterial lineMaterial, Block[][][] blocks) {
        createCommand("//sel cuboid");
        int changes = 0;

        List<String> positions = new ArrayList<>();
        for (Vector point : points) positions.add(GeneratorUtils.getXYZ(point, blocks));

        for(int i = 0; i < points.size(); i++){
            if(i%2 == 0)
                createCommand("//pos1 " + positions.get(i));
            else {
                createCommand("//pos2 " + positions.get(i));
                createCommand("//line " + lineMaterial);
                changes++;
            }
        }

        return changes;
    }
}

package net.buildtheearth.modules.generator.components.road;


import com.cryptomorin.xseries.XMaterial;
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

        XMaterial[] roadMaterials = (XMaterial[]) flags.get(RoadFlag.ROAD_MATERIAL);
        XMaterial[] sidewalkMaterials = (XMaterial[]) flags.get(RoadFlag.SIDEWALK_MATERIAL);
        XMaterial[] sidewalkSlabMaterials = (XMaterial[]) flags.get(RoadFlag.SIDEWALK_SLAB_COLOR);
        XMaterial[] roadSlabMaterials = (XMaterial[]) flags.get(RoadFlag.ROAD_SLAB_COLOR);
        XMaterial markingMaterial = (XMaterial) flags.get(RoadFlag.MARKING_MATERIAL);

        String streetLampType = (String) flags.get(RoadFlag.STREET_LAMP_TYPE);

        String roadMaterialIDs = Item.getUniqueMaterialString(roadMaterials);
        String sidewalkMaterialIDs = Item.getUniqueMaterialString(sidewalkMaterials);
        String sidewalkSlabMaterialIDs = Item.getUniqueMaterialString(sidewalkSlabMaterials);
        String roadSlabMaterialIDs = Item.getUniqueMaterialString(roadSlabMaterials);
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

        // Draw the road
        drawCurve(points, XMaterial.YELLOW_WOOL, true);


        // Prepare the script session
        Block[][][] blocks = GeneratorUtils.prepareScriptSession(localSession, actor, getPlayer(),weWorld, 30, true);

        finish(blocks, points);

        /*
        // Bring all points to the ground
        GeneratorUtils.adjustHeight(points, blocks);

        // Shorten the points to prevent the road from being too long
        List<Vector> innerPoints = new ArrayList<>(points);
        innerPoints = GeneratorUtils.shortenPolyLine(innerPoints, 2);


        // ----------- ROAD ----------

        // Draw the road
        String mask = "!#solid";

        if(roadMaterialIDs != null)
            mask += "," + roadMaterialIDs;
        if(sidewalkMaterialIDs != null)
            mask += "," + sidewalkMaterialIDs;
        if(sidewalkSlabMaterialIDs != null)
            mask += "," + sidewalkSlabMaterialIDs;
        if(roadSlabMaterialIDs != null)
            mask += "," + roadSlabMaterialIDs;
        mask += "," + markingMaterialID;

        drawCurveWithMask(mask, points, XMaterial.YELLOW_WOOL, true);

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



        // ----------- SIDEWALK ----------
        // Draw the sidewalk
        if(isSidewalk) {
            // The outer sidewalk edge lines
            List<List<Vector>> sidewalkPointsOut = GeneratorUtils.shiftPointsAll(points, road_width + sidewalkWidth * 2);
            List<List<Vector>> sidewalkPointsMid = GeneratorUtils.shiftPointsAll(innerPoints, road_width + sidewalkWidth);
            List<List<Vector>> sidewalkPointsIn = GeneratorUtils.shiftPointsAll(points, road_width);


            // Draw the sidewalk middle lines
            for(List<Vector> path : sidewalkPointsMid)
                drawPolyLineWithMask("!#solid," + roadMaterialIDs + "," + markingMaterialID, path, XMaterial.ORANGE_WOOL, true);

            // Create the outer sidewalk edge lines
            for(List<Vector> path : sidewalkPointsOut)
                drawPolyLineWithMask("!" + roadMaterialIDs + "," + markingMaterialID, path, XMaterial.LIGHT_BLUE_WOOL, true);

            // Create the inner sidewalk edge lines
            for(List<Vector> path : sidewalkPointsIn)
                drawPolyLineWithMask("!" + roadMaterialIDs + "," + markingMaterialID, path, XMaterial.LIGHT_BLUE_WOOL, true);


            if(isCrosswalk){
                // Draw the sidewalk middle lines
                for(List<Vector> path : sidewalkPointsMid)
                    drawPolyLineWithMask(roadMaterialIDs + "," + markingMaterialID, path, XMaterial.MAGENTA_WOOL, true);

                // Create the outer sidewalk edge lines
                for(List<Vector> path : sidewalkPointsOut)
                    drawPolyLineWithMask("!" + roadMaterialIDs + "," + markingMaterialID, path, XMaterial.BLUE_WOOL, true);

                // Create the inner sidewalk edge lines
                for(List<Vector> path : sidewalkPointsIn)
                    drawPolyLineWithMask("!" + roadMaterialIDs + "," + markingMaterialID, path, XMaterial.BLUE_WOOL, true);
            }
        }




        // ----------- OLD ROAD REPLACEMENTS ----------
        // Replace the existing road material with wool

        // Create the poly selection
        createPolySelection(polyRegionPointsExact);
        expandSelection(new Vector(0, 10, 0));
        expandSelection(new Vector(0, -10, 0));

        setGmask("!air");

        // Replace the current road material with lime wool
        for(XMaterial roadMaterial : roadMaterials)
            replaceBlocks(roadMaterial, XMaterial.LIME_WOOL);

        for(XMaterial roadSlabMaterial : roadSlabMaterials)
            replaceBlocks(roadSlabMaterial, XMaterial.LIME_WOOL);

        replaceBlocks(markingMaterial, XMaterial.LIME_WOOL);

        // Replace the current sidewalk material with pink wool
        for(XMaterial sidewalkMaterial : sidewalkMaterials)
            replaceBlocks(sidewalkMaterial, XMaterial.PINK_WOOL);

        for(XMaterial sidewalkSlabMaterial : sidewalkSlabMaterials)
            replaceBlocks(sidewalkSlabMaterial, XMaterial.PINK_WOOL);

        setGmask(null);


        // ----------- FILLINGS ----------
        // Fill the road with the materials

        createPolySelection(polyRegionPoints);
        expandSelection(new Vector(0, 10, 0));
        expandSelection(new Vector(0, -10, 0));


        setGmask("!air,35:3,35:11");

        // Bring all lines to the top
        for(int i = 0; i < road_height + 5; i++) {
            setBlocksWithMask(">35:1", XMaterial.ORANGE_WOOL);
            setBlocksWithMask(">35:2", XMaterial.MAGENTA_WOOL);
            setBlocksWithMask(">35:4", XMaterial.YELLOW_WOOL);
        }

        setGmask("!air");

        // Bring the light blue and blue wool to the top at last to prevent the others from creating leaks
        for(int i = 0; i < road_height + 5; i++) {
            setBlocksWithMask(">35:3", XMaterial.LIGHT_BLUE_WOOL);
            setBlocksWithMask(">35:11", XMaterial.BLUE_WOOL);
        }

        setGmask(null);


        // Bring all lines further down
        for(int i = 0; i < 3; i++) {
            setBlocksWithMask("=queryRel(0,1,0,35,3)", XMaterial.LIGHT_BLUE_WOOL);
            setBlocksWithMask("=queryRel(0,1,0,35,11)", XMaterial.BLUE_WOOL);
        }

        // Spread the yellow wool
        setGmask("!35:3,35:5,35:6,#solid");
        for(int i = 0; i < laneWidth*2; i++){
            // Replace all blocks around yellow wool with yellow wool until it reaches the light blue wool
            setBlocksWithMask("=(queryRel(1,0,0,35,4)||queryRel(-1,0,0,35,4)||queryRel(0,0,1,35,4)||queryRel(0,0,-1,35,4)||queryRel(0,-1,0,35,4)||queryRel(0,1,0,35,4))&&queryRel(0,3,0,0,0)", XMaterial.YELLOW_WOOL);
        }

        // Spread the orange wool
        setGmask("!35:3,35:2,35:11,35:5,#solid");
        for(int i = 0; i < laneWidth*2; i++){
            // Replace all blocks around orange wool with orange wool until it reaches the light blue wool
            setBlocksWithMask("=queryRel(0,3,0,0,0)&&(queryRel(1,0,0,35,1)||queryRel(-1,0,0,35,1)||queryRel(0,0,1,35,1)||queryRel(0,0,-1,35,1)||queryRel(0,-1,0,35,1)||queryRel(0,1,0,35,1))", XMaterial.ORANGE_WOOL);
        }

        // Replace all orange wool with light blue wool
        setGmask(null);
        replaceBlocks(XMaterial.ORANGE_WOOL, XMaterial.LIGHT_BLUE_WOOL);


        // Spread the magenta wool
        setGmask("!35:11,35:3,#solid");
        for(int i = 0; i < laneWidth*2; i++){
            // Replace all blocks around orange wool with orange wool until it reaches the light blue wool
            setBlocksWithMask("=(queryRel(1,0,0,35,2)||queryRel(-1,0,0,35,2)||queryRel(0,0,1,35,2)||queryRel(0,0,-1,35,2)||queryRel(0,-1,0,35,2)||queryRel(0,1,0,35,2))&&queryRel(0,3,0,0,0)", XMaterial.MAGENTA_WOOL);
        }

        // Replace all magenta wool with pink wool
        setGmask(null);
        replaceBlocks(XMaterial.MAGENTA_WOOL, XMaterial.PINK_WOOL);
        replaceBlocks(XMaterial.BLUE_WOOL, XMaterial.PINK_WOOL);


        // In case there are some un-replaced blocks left, replace everything above light blue wool that is not air or yellow wool with light blue wool
        for(int i = 0; i < road_height; i++)
            setBlocksWithMask(">35:3,!air,35:4", XMaterial.LIGHT_BLUE_WOOL);



        // ----------- CROSSWALK ----------
        // Draw the crosswalk

        if(isCrosswalk){
            replaceBlocksWithMask("=queryRel(1,0,0,35,3)||queryRel(-1,0,0,35,3)||queryRel(0,0,1,35,3)||queryRel(0,0,-1,35,3)",
                    XMaterial.PINK_WOOL, XMaterial.CYAN_WOOL);

            for(int i = 0; i < road_width; i++) {
                replaceBlocksWithMask("=queryRel(1,0,0,35,9)||queryRel(-1,0,0,35,9)||queryRel(0,0,1,35,9)||queryRel(0,0,-1,35,9)",
                        XMaterial.PINK_WOOL, XMaterial.PURPLE_WOOL);

                replaceBlocksWithMask("=queryRel(1,0,0,35,10)||queryRel(-1,0,0,35,10)||queryRel(0,0,1,35,10)||queryRel(0,0,-1,35,10)",
                        XMaterial.PINK_WOOL, XMaterial.PURPLE_WOOL);
            }
        }else
            replaceBlocks(XMaterial.PINK_WOOL, XMaterial.YELLOW_WOOL);



        // ----------- ROAD AND SIDEWALK SLABS ----------
        // Create the road and sidewalk slabs

        // Create the road slabs
        for(XMaterial roadSlabMaterial : roadSlabMaterials)
            setBlocksWithMask("=queryRel(0,-1,0,35,4)&&queryRel(0,0,0,0,0)&&(queryRel(1,0,0,35,4)||queryRel(-1,0,0,35,4)||queryRel(0,0,1,35,4)||queryRel(0,0,-1,35,4))",
                    roadSlabMaterial);

        // Create the sidewalk slabs
        for(XMaterial sidewalkSlabMaterial : sidewalkSlabMaterials)
            setBlocksWithMask("=queryRel(0,-1,0,35,3)&&queryRel(0,0,0,0,0)&&(queryRel(1,0,0,35,3)||queryRel(-1,0,0,35,3)||queryRel(0,0,1,35,3)||queryRel(0,0,-1,35,3))",
                    sidewalkSlabMaterial);



        // ----------- ROAD MARKINGS ----------
        // Draw the road markings

        if(laneCount > 1) {
            // Check if langeCount is even or odd
            boolean isEven = laneCount % 2 == 0;

            // Create the road markings in the middle of the road
            if(isEven)
                for(int i = 1; i < roadMarkingPoints.size(); i+=2)
                    drawLineWithMask("35:4", roadMarkingPoints.get(i-1), roadMarkingPoints.get(i), markingMaterial, true);


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

                        for(int i2 = 1; i2 < shiftedRoadMarkingPoints.size(); i2+=2)
                            drawLineWithMask("35:4", shiftedRoadMarkingPoints.get(i2-1), shiftedRoadMarkingPoints.get(i2), markingMaterial, true);
                    }
                }
        }


        // ----------- MATERIAL ----------
        // Replace all light blue wool with the sidewalk material
        createPolySelection(polyRegionPoints);
        replaceBlocks(XMaterial.LIGHT_BLUE_WOOL, sidewalkMaterials);

        // Replace all yellow,lime and cyan wool with the road material
        replaceBlocks(XMaterial.YELLOW_WOOL, roadMaterials);
        replaceBlocks(XMaterial.LIME_WOOL, roadMaterials);
        replaceBlocks(XMaterial.CYAN_WOOL, roadMaterials);

        // Replace all purple wool with the marking material
        replaceBlocks(XMaterial.PURPLE_WOOL, markingMaterial);




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


                    pasteSchematic("GeneratorCollections/roadpack/streetlamp" + streetLampType + ".schematic", loc, angle);
                }
        }


        // Finish the script
        finish(blocks, points);
        */
    }
}

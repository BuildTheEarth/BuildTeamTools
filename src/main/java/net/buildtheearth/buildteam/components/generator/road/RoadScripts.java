package net.buildtheearth.buildteam.components.generator.road;


import clipper2.Clipper;
import clipper2.core.*;
import clipper2.offset.EndType;
import clipper2.offset.JoinType;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import net.buildtheearth.buildteam.components.generator.Generator;
import net.buildtheearth.buildteam.components.generator.GeneratorType;
import net.buildtheearth.buildteam.components.generator.History;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

public class RoadScripts {

    public static void roadscript_v_1_3(Player p, Road road, ConvexPolyhedralRegion region) {
        HashMap<Object, String> flags = road.getPlayerSettings().get(p.getUniqueId()).getValues();

        String roadMaterial = flags.get(RoadFlag.ROAD_MATERIAL);
        String markingMaterial = flags.get(RoadFlag.MARKING_MATERIAL);
        String sidewalkMaterial = flags.get(RoadFlag.SIDEWALK_MATERIAL);

        int laneCount = Integer.parseInt(flags.get(RoadFlag.LANE_COUNT));
        int laneWidth = Integer.parseInt(flags.get(RoadFlag.LANE_WIDTH));
        int laneGap = Integer.parseInt(flags.get(RoadFlag.LANE_GAP));
        int markingLength = Integer.parseInt(flags.get(RoadFlag.MARKING_LENGTH));
        int markingGap = Integer.parseInt(flags.get(RoadFlag.MARKING_GAP));
        int sidewalkWidth = Integer.parseInt(flags.get(RoadFlag.SIDEWALK_WIDTH));

        int operations = 0;
        p.chat("/clearhistory");

        // Is there a sidewalk?
        boolean isSidewalk = false;
        if(sidewalkWidth>0)
            isSidewalk = true;

        // Calculate max width from centre of road
        int current_width = ((laneWidth + 1)*laneCount) + sidewalkWidth + (isSidewalk ? 1 : 0) + laneGap;


        // Clear the surrounding area (grass, fern, dead bush, flowers, mushrooms, pumpkin, tall grass, leaves)
        p.chat("//gmask 31,32,37,38,39,40,83,86,175,18,161");
        p.chat("//curve 0 " + current_width);
        operations++;


        // Draw sidewalk
        p.chat("//gmask <0");
        p.chat("//curve 43:0 " + current_width);
        operations++;


        //TODO: fix excessively wide sidewalk bug
        current_width -=(isSidewalk ? 1 : 0);
        p.chat("//curve 35:1 " + current_width);
        operations++;

        current_width -= sidewalkWidth;
        p.chat("//curve 43:0 " + current_width);
        operations++;


        // Draw road
        for(int i=0;i<laneCount;i++) {
            current_width--;
            p.chat("//curve 35:2 " + current_width);
            operations++;

            current_width -= laneWidth;
            p.chat("//curve 35:3 " + current_width);
            operations++;

        }

        if(laneCount>1) {
            p.chat("//curve " + markingMaterial);
            operations++;
        }

        // Separation
        if(laneGap > 0) {
            p.chat("//curve 43:8 " + current_width);
            operations++;

            current_width--;
            p.chat("//curve 2 " + current_width);
            operations++;
        }

        // Markings
        int xPos = p.getLocation().getBlockX();
        int zPos = p.getLocation().getBlockZ();
        int markingSum = markingLength + markingGap;
        p.chat("//gmask \"=(sqrt((x-(" + xPos + "))^2+(z-(" + zPos + "))^2)%" + markingSum + ")-" + markingGap + " 35:3\"");
        int roadWidth = ((laneWidth+1)*laneCount)+sidewalkWidth+1+laneGap;


        p.chat("//curve " + markingMaterial + " " + roadWidth);
        operations++;

        p.chat("//gmask 35:2,35:3");
        p.chat("//curve " + roadMaterial + " " + roadWidth);
        operations++;

        p.chat("//gmask 35:1");
        p.chat("//curve " + sidewalkMaterial + " " + roadWidth);
        operations++;

        p.chat("//gmask");



        Generator.getPlayerHistory(p).addHistoryEntry(new History.HistoryEntry(GeneratorType.ROAD, operations));
    }

    public static void roadscript_v_2_0(Player p, Road road, ConvexPolyhedralRegion region) {
        HashMap<Object, String> flags = road.getPlayerSettings().get(p.getUniqueId()).getValues();

        String roadMaterial = flags.get(RoadFlag.ROAD_MATERIAL);
        String markingMaterial = flags.get(RoadFlag.MARKING_MATERIAL);
        String sidewalkMaterial = flags.get(RoadFlag.SIDEWALK_MATERIAL);

        int laneCount = Integer.parseInt(flags.get(RoadFlag.LANE_COUNT));
        int laneWidth = Integer.parseInt(flags.get(RoadFlag.LANE_WIDTH));
        int laneGap = Integer.parseInt(flags.get(RoadFlag.LANE_GAP));
        int markingLength = Integer.parseInt(flags.get(RoadFlag.MARKING_LENGTH));
        int markingGap = Integer.parseInt(flags.get(RoadFlag.MARKING_GAP));
        int sidewalkWidth = Integer.parseInt(flags.get(RoadFlag.SIDEWALK_WIDTH));

        int operations = 0;
        p.chat("/clearhistory");

        // Is there a sidewalk?
        boolean isSidewalk = false;
        if(sidewalkWidth>1)
            isSidewalk = true;

        List<Vector> points = new ArrayList<>(region.getVertices());
        List<Vector> orthogonals = getOrthogonals(points);

        p.chat("//gmask");
        p.chat("//curve 35:4");

        // Calculate current width from centre of road
        int max_width = ((laneWidth + 1)*laneCount) + sidewalkWidth + (isSidewalk ? 1 : 0) + laneGap;
        int road_height = region.getHeight();




        // ----------- PREPARATION 01 ----------
        // Replace all non-solid blocks with air

        List<List<Vector>> shiftedPoints = shiftPoints(points, orthogonals, max_width + 2);
        Bukkit.broadcastMessage("Shifted points: " + shiftedPoints.size());
        List<Vector> shiftedPointsAll = new ArrayList<>();
        for(List<Vector> shiftedPoint : shiftedPoints)
            shiftedPointsAll.addAll(shiftedPoint);
        Bukkit.broadcastMessage("Shifted points all: " + shiftedPointsAll.size());

        List<Vector> polyPoints = new ArrayList<>(shiftedPointsAll);

        // Create a region from the points
        createPolySelection(p, polyPoints);

        p.chat("//expand 10 up");
        p.chat("//expand 10 down");

        p.chat("//gmask !#solid");
        p.chat("//replace 0");
        operations++;

        p.chat("//gmask");

        Block[][][] regionBlocks = Generator.analyzeRegion(p, p.getWorld());

        if(1 == 1)
            return;

        /*

        double width = 0;

        // ----------- SIDEWALK ----------
        // Draw the sidewalk
        if(isSidewalk) {
            // The outer sidewalk edge lines
            width = laneWidth/2 + sidewalkWidth;
            List<Vector> sidewalkPointsLeftOut = shiftPoints(points, orthogonals, width);
            List<Vector> sidewalkPointsRightOut = shiftPoints(points, orthogonals, -width);

            // The inner sidewalk edge lines
            width = laneWidth/2 + 1;
            List<Vector> sidewalkPointsLeftIn = shiftPoints(points, orthogonals, width);
            List<Vector> sidewalkPointsRightIn = shiftPoints(points, orthogonals, -width);

            // The middle sidewalk edge lines
            width = laneWidth/2 + sidewalkWidth/2;
            List<Vector> sidewalkPointsLeftMid = shiftPoints(points, orthogonals, width);
            List<Vector> sidewalkPointsRightMid = shiftPoints(points, orthogonals, -width);

            // Draw the sidewalk right and left and middle edge lines
            p.chat("//gmask !solid");
            createConvexSelection(p, sidewalkPointsLeftMid, regionBlocks);
            p.chat("//curve 35:1");
            operations++;
            createConvexSelection(p, sidewalkPointsRightMid, regionBlocks);
            p.chat("//curve 35:1");
            operations++;
            p.chat("//gmask");

            createConvexSelection(p, sidewalkPointsLeftOut, regionBlocks);
            p.chat("//curve 35:3");
            operations++;
            createConvexSelection(p, sidewalkPointsRightOut, regionBlocks);
            p.chat("//curve 35:3");
            operations++;

            createConvexSelection(p, sidewalkPointsLeftIn, regionBlocks);
            p.chat("//curve 35:3");
            operations++;
            createConvexSelection(p, sidewalkPointsRightIn, regionBlocks);
            p.chat("//curve 35:3");
            operations++;

            // Draw the sidewalk end lines
            p.chat("//pos1 " + getXYZ(sidewalkPointsLeftOut.get(0), regionBlocks));
            p.chat("//pos2 " + getXYZ(sidewalkPointsLeftMid.get(0), regionBlocks));
            p.chat("//pos2 " + getXYZ(sidewalkPointsLeftIn.get(0), regionBlocks));
            p.chat("//curve 35:3");
            operations++;

            p.chat("//pos1 " + getXYZ(sidewalkPointsRightOut.get(0), regionBlocks));
            p.chat("//pos2 " + getXYZ(sidewalkPointsRightMid.get(0), regionBlocks));
            p.chat("//pos2 " + getXYZ(sidewalkPointsRightIn.get(0), regionBlocks));
            p.chat("//curve 35:3");
            operations++;

            p.chat("//pos1 " + getXYZ(sidewalkPointsLeftOut.get(sidewalkPointsLeftOut.size()-1), regionBlocks));
            p.chat("//pos2 " + getXYZ(sidewalkPointsLeftMid.get(sidewalkPointsLeftMid.size()-1), regionBlocks));
            p.chat("//pos2 " + getXYZ(sidewalkPointsLeftIn.get(sidewalkPointsLeftIn.size()-1), regionBlocks));
            p.chat("//curve 35:3");
            operations++;

            p.chat("//pos1 " + getXYZ(sidewalkPointsRightOut.get(sidewalkPointsRightOut.size()-1), regionBlocks));
            p.chat("//pos2 " + getXYZ(sidewalkPointsRightMid.get(sidewalkPointsRightMid.size()-1), regionBlocks));
            p.chat("//pos2 " + getXYZ(sidewalkPointsRightIn.get(sidewalkPointsRightIn.size()-1), regionBlocks));
            p.chat("//curve 35:3");
            operations++;



            // Bring all lines to the top
            createPolySelection(p, polyPoints);
            p.chat("//gmask");
            p.chat("//expand 10 up");
            p.chat("//expand 10 down");

            for(int i = 0; i < road_height; i++) {
                p.chat("//replace >35:3,!air 35:3");
                operations++;
            }

            // Bring all lines further down
            p.chat("//gmask =queryRel(0,1,0,35,3)");
            for(int i = 0; i < 3; i++){
                p.chat("//replace 35:3");
                operations++;
            }

            // Spread the orange wool
            p.chat("//gmask =queryRel(1,0,0,35,1)||queryRel(-1,0,0,35,1)||queryRel(0,0,1,35,1)||queryRel(0,0,-1,35,1)");
            for(int i = 0; i < 20; i++){
                // Replace all blocks around orange wool with orange wool until it reaches the light blue wool
                p.chat("//replace !35:3,solid 35:1");
                operations++;
            }

            // Replace all orange wool with light blue wool
            p.chat("//gmask");
            p.chat("//replace 35:1 35:3");

            // In case there are some un-replaced blocks left, replace everything above light blue wool with light blue wool
            for(int i = 0; i < road_height; i++) {
                p.chat("//replace >35:3,!air 35:3");
                operations++;
            }
        }

        if(1==1)
            return;


        // ----------- ROAD ----------
        // Draw the road

        // Draw the road right and left edge lines
        width = laneWidth/2;
        List<Vector> roadPoints1 = shiftPoints(points, orthogonals, width);
        List<Vector> roadPoints2 = shiftPoints(points, orthogonals, -width);
        createConvexSelection(p, roadPoints1, regionBlocks);
        p.chat("//curve 35:2");
        operations++;
        createConvexSelection(p, roadPoints2, regionBlocks);
        p.chat("//curve 35:2");
        operations++;

        // Draw the road end lines
        p.chat("//pos1 " + getXYZ(roadPoints1.get(0), regionBlocks));
        p.chat("//pos2 " + getXYZ(points.get(0), regionBlocks));
        p.chat("//pos2 " + getXYZ(roadPoints2.get(0), regionBlocks));
        p.chat("//curve 35:2");
        operations++;

        p.chat("//pos1 " + getXYZ(roadPoints1.get(roadPoints1.size()-1), regionBlocks));
        p.chat("//pos2 " + getXYZ(points.get(points.size()-1), regionBlocks));
        p.chat("//pos2 " + getXYZ(roadPoints2.get(roadPoints2.size()-1), regionBlocks));
        p.chat("//curve 35:2");
        operations++;
        */

        p.chat("//gmask");
        createConvexSelection(p, points, null);

        Generator.getPlayerHistory(p).addHistoryEntry(new History.HistoryEntry(GeneratorType.ROAD, operations));
    }

    public static List<Vector> getOrthogonals(List<Vector> points){
        List<Vector> orthogonals = new ArrayList<>();
        for(int i = 0; i < points.size()-1; i++){
            Vector p1 = points.get(i);
            Vector p2 = points.get(i+1);

            Vector v1 = p2.subtract(p1);
            Vector v2 = new Vector(-v1.getZ(), 0, v1.getX()).normalize();

            orthogonals.add(v2);


            // If its the last point, add the normal twice
            if(i == points.size()-2)
                orthogonals.add(v2);
        }

        return orthogonals;
    }

    public static Path64 convertVectorListToPath64(List<Vector> vectors, Vector reference){
        List<Point64> points = new ArrayList<>();
        for(Vector vector : vectors)
            points.add(new Point64(vector.getBlockX() - reference.getBlockX(), vector.getBlockZ() - reference.getBlockZ()));

        return new Path64(points);
    }

    public static List<List<Vector>> convertPathsToVectorList(Paths64 pathsD, Vector reference){
        List<List<Vector>> vectors = new ArrayList<>();

        for(Path64 path : new ArrayList<>(pathsD)) {
            List<Vector> vectorList = new ArrayList<>();

            for(Point64 point : new ArrayList<>(path))
                vectorList.add(new Vector(point.x + reference.getX(), reference.getY(), point.y + reference.getZ()));

            vectors.add(vectorList);
        }

        Bukkit.broadcastMessage("Converted " + pathsD.size() + " paths to " + vectors.size() + " vectors");

        return vectors;
    }

    public static List<List<Vector>> shiftPoints(List<Vector> vectors, List<Vector> normals, double shift) {
        /*
        List<Vector> shiftedPoints = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            Vector point = points.get(i);
            Vector normal = normals.get(i);

            Vector shiftedPoint = point.add(normal.multiply(shift));
            shiftedPoints.add(shiftedPoint);
        }

        // Check if one of the shifted points gets too close to one of the original points. If so, remove it.
        for(Vector vector : points)
        for(int i = 1; i < shiftedPoints.size(); i++)
            if (vector.distance(shiftedPoints.get(i)) < shift - 0.5) {
                shiftedPoints.set(i, shiftedPoints.get(i - 1));
            }
         */


        Vector reference = vectors.get(0);
        Paths64 paths = new Paths64();
        paths.add(convertVectorListToPath64(vectors, reference));
        Paths64 inflatedPath = Clipper.InflatePaths(paths, shift, JoinType.Round, EndType.Butt,  2);

        return convertPathsToVectorList(inflatedPath, reference);
    }


    public static void createConvexSelection(Player p, List<Vector> points, Block[][][] blocks){
        p.chat("//sel convex");
        p.chat("//pos1 " + getXYZ(points.get(0), blocks));

        for(int i = 1; i < points.size(); i++)
            p.chat("//pos2 " + getXYZ(points.get(i), blocks));
    }

    public static void createPolySelection(Player p, List<Vector> points){
        p.chat("//sel poly");
        p.chat("//pos1 " + getXYZ(points.get(0), null));

        for(int i = 1; i < points.size(); i++){
            p.chat("//pos2 " + getXYZ(points.get(i), null));
        }
    }

    public static String getXYZ(Vector vector, Block[][][] blocks){
        int maxHeight = vector.getBlockY();

        if(blocks != null)
            maxHeight = Generator.getMaxHeight(blocks, vector.getBlockX(), vector.getBlockZ(), Material.LOG, Material.LOG_2, Material.LEAVES, Material.LEAVES_2, Material.WOOL);
        if(maxHeight == 0)
            maxHeight = vector.getBlockY();

        return vector.getBlockX() + "," + maxHeight + "," + vector.getBlockZ();
    }

}

package net.buildtheearth.buildteam.components.generator.road;


import clipper2.Clipper;
import clipper2.core.*;
import clipper2.offset.EndType;
import clipper2.offset.JoinType;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
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

        HashMap < Object, String > flags = road.getPlayerSettings().get(p.getUniqueId()).getValues();

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

        // Calculate current width from centre of road
        int road_width = laneWidth*laneCount;
        int max_width = road_width + sidewalkWidth*2 + laneGap;
        int road_height = region.getHeight();

        // Get the points of the region
        List<Vector> points = new ArrayList<>(region.getVertices());
        points = populatePoints(points, road_width);

        List<Vector> innerPoints = new ArrayList<>(points);
        innerPoints = shortenPolyLine(innerPoints, 2);




        p.chat("//gmask");
        p.chat("//curve 35:4");


        // ----------- PREPARATION 01 ----------
        // Replace all non-solid blocks with air

        List<Vector> polyline = new ArrayList<>(points);
        polyline = extendPolyLine(polyline);
        List<Vector> polyPoints = shiftPoints(polyline, max_width + 2, true);

        // Create a region from the points
        createPolySelection(p, polyPoints, null);

        p.chat("//expand 10 up");
        p.chat("//expand 10 down");
        p.chat("//gmask !#solid");
        p.chat("//replace 0");
        operations++;

        p.chat("//gmask");


        Block[][][] regionBlocks = Generator.analyzeRegion(p, p.getWorld());


        // ----------- ROAD ----------

        // Add additional yellow wool markings to spread the road material faster and everywhere on the road.
        for(int i = 2; i < laneCount; i+=2){
            List<List<Vector>> yellowWoolLine = shiftPointsAll(innerPoints, (laneWidth*(i-1)));

            p.chat("//gmask !solid");
            for(List<Vector> path : yellowWoolLine) {
                createConvexSelection(p, path, regionBlocks);
                p.chat("//curve 35:4");
                operations++;

                // Close the circles (curves are not able to end at the beginning)
                p.chat("//sel cuboid");
                p.chat("//pos1 " + getXYZ(path.get(0), regionBlocks));
                p.chat("//pos2 " + getXYZ(path.get(path.size()-1), regionBlocks));
                p.chat("//line 35:4");
                operations++;
            }
            p.chat("//gmask");
        }

        // Draw another yellow line close to the sidewalk to spread the yellow wool faster and everywhere on the road.
        if(road_width > 10) {
            List<List<Vector>> yellowWoolLineNearSidewalk = shiftPointsAll(innerPoints, road_width - 4);
            for (List<Vector> path : yellowWoolLineNearSidewalk)
                operations += createPolyLine(p, path, "35:4", true, regionBlocks);
        }


        // ----------- SIDEWALK ----------
        // Draw the sidewalk
        if(isSidewalk) {
            // The outer sidewalk edge lines
            List<List<Vector>> sidewalkPointsOut = shiftPointsAll(points, road_width + sidewalkWidth*2);
            List<List<Vector>> sidewalkPointsMid = shiftPointsAll(innerPoints, road_width + sidewalkWidth);
            List<List<Vector>> sidewalkPointsIn = shiftPointsAll(points, road_width);


            // Draw the sidewalk middle lines
            p.chat("//gmask !solid");
            for(List<Vector> path : sidewalkPointsMid)
                operations += createPolyLine(p, path, "35:1", true, regionBlocks);
            p.chat("//gmask");

            // Create the outer sidewalk edge lines
            for(List<Vector> path : sidewalkPointsOut)
                operations += createPolyLine(p, path, "35:3", true, regionBlocks);


            // Create the inner sidewalk edge lines
            for(List<Vector> path : sidewalkPointsIn)
                operations += createPolyLine(p, path, "35:3", true, regionBlocks);
        }


        // ----------- ROAD ----------
        // Draw the road

        /*
        // Draw the road right and left edge lines
        width = laneWidth/2;
        List<Vector> roadPoints = shiftPointsAll(points, orthogonals, width);
        createConvexSelection(p, roadPoints, regionBlocks);
        p.chat("//curve 35:2");
        operations++;
        */


        // ----------- FILLINGS ----------

        // Bring all lines to the top
        createPolySelection(p, polyPoints, null);
        p.chat("//gmask");
        p.chat("//expand 10 up");
        p.chat("//expand 10 down");

        for(int i = 0; i < road_height + 5; i++) {
            p.chat("//replace >35:3,!air 35:1");
            operations++;
            p.chat("//replace >35:3,!air 35:3");
            operations++;
            p.chat("//replace >35:3,!air 35:4");
            operations++;
        }

        // Bring all lines further down
        p.chat("//gmask =queryRel(0,1,0,35,3)");
        for(int i = 0; i < 3; i++){
            p.chat("//set 35:3");
            operations++;
        }

        // Spread the yellow wool
        p.chat("//gmask =queryRel(1,0,0,35,4)||queryRel(-1,0,0,35,4)||queryRel(0,0,1,35,4)||queryRel(0,0,-1,35,4)");
        for(int i = 0; i < laneWidth*2; i++){
            // Replace all blocks around yellow wool with yellow wool until it reaches the light blue wool
            p.chat("//replace !35:3,solid 35:4");
            operations++;
        }

        // Spread the orange wool
        p.chat("//gmask =queryRel(1,0,0,35,1)||queryRel(-1,0,0,35,1)||queryRel(0,0,1,35,1)||queryRel(0,0,-1,35,1)");
        for(int i = 0; i < road_width; i++){
            // Replace all blocks around orange wool with orange wool until it reaches the light blue wool
            p.chat("//replace !35:3,solid 35:1");
            operations++;
        }

        // Replace all orange wool with light blue wool
        p.chat("//gmask");
        p.chat("//replace 35:1 35:3");

        // In case there are some un-replaced blocks left, replace everything above light blue wool that is not air or yellow wool with light blue wool
        for(int i = 0; i < road_height; i++) {
            p.chat("//replace >35:3,!air,35:4 35:3");
            operations++;
        }


        // ----------- MATERIAL ----------
        // Replace all light blue wool with the sidwalk material
        p.chat("//replace 35:3 " + sidewalkMaterial);
        operations++;

        // Replace all yellow wool with the road material
        p.chat("//replace 35:4 " + roadMaterial);
        operations++;

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

        return vectors;
    }

    public static List<Vector> shiftPoints(List<Vector> vectors, double shift, boolean useLongestPathOnly) {
        List<List<Vector>> resultVectors = shiftPointsAll(vectors, shift);

        // If we only want the longest path, find it and return it
        if(useLongestPathOnly){
            int longestPathIndex = 0;
            int longestPathLength = 0;
            for(int i = 0; i < resultVectors.size(); i++){
                if(resultVectors.get(i).size() > longestPathLength){
                    longestPathIndex = i;
                    longestPathLength = resultVectors.get(i).size();
                }
            }

            return resultVectors.get(longestPathIndex);

        // Otherwise, return all paths
        }else{
            List<Vector> result = new ArrayList<>();
            for(List<Vector> vectorList : resultVectors)
                result.addAll(vectorList);

            return result;
        }
    }

    public static List<List<Vector>> shiftPointsAll(List<Vector> vectors, double shift) {
        Vector reference = vectors.get(0);
        Paths64 paths = new Paths64();
        paths.add(convertVectorListToPath64(vectors, reference));
        Paths64 inflatedPath = Clipper.InflatePaths(paths, shift, JoinType.Round, EndType.Butt, 2);

        return convertPathsToVectorList(inflatedPath, reference);
    }

    /** Extends a polyline by taking the first two points and the last two points of the polyline and extending them
     *
     * @param vectors:  The polyline to extend
     * @return:         The extended polyline
     */
    public static List<Vector> extendPolyLine(List<Vector> vectors){
        List<Vector> result = new ArrayList<>();

        // Get the first two points
        Vector p1 = vectors.get(0);
        Vector p2 = vectors.get(1);

        // Get the last two points
        Vector p3 = vectors.get(vectors.size()-2);
        Vector p4 = vectors.get(vectors.size()-1);

        // Get the vectors between the points
        Vector v1 = p1.subtract(p2);
        Vector v2 = p4.subtract(p3);

        result.add(p1.add(v1));
        result.addAll(vectors);
        result.add(p4.add(v2));

        return result;
    }

    /** Shortens a polyline by taking the first two points and the last two points of the polyline and shortening them
     *
     * @param vectors:  The polyline to shorten
     * @return:         The shortened polyline
     */
    public static List<Vector> shortenPolyLine(List<Vector> vectors, int distance){
        List<Vector> result = new ArrayList<>();

        if(vectors.size() < 4)
            return vectors;

        // Get the first two points
        Vector p1 = vectors.get(0);
        Vector p2 = vectors.get(1);

        // Get the last two points
        Vector p3 = vectors.get(vectors.size()-2);
        Vector p4 = vectors.get(vectors.size()-1);

        // Get the vectors between the points
        Vector v1 = p2.subtract(p1);
        Vector v2 = p3.subtract(p4);

        // Shorten the vectors
        v1 = v1.normalize().multiply(distance);
        v2 = v2.normalize().multiply(distance);

        // Remove the first and last points
        vectors.remove(0);
        vectors.remove(vectors.size() - 1);

        // Add the shortened vectors
        result.add(p1.add(v1));
        result.addAll(vectors);
        result.add(p4.add(v2));

        return result;
    }

    public static void createConvexSelection(Player p, List<Vector> points, Block[][][] blocks){
        p.chat("//sel convex");
        p.chat("//pos1 " + getXYZ(points.get(0), blocks));

        for(int i = 1; i < points.size(); i++)
            p.chat("//pos2 " + getXYZ(points.get(i), blocks));
    }

    public static void createPolySelection(Player p, List<Vector> points, Block[][][] blocks){
        p.chat("//sel poly");
        p.chat("//pos1 " + getXYZ(points.get(0), blocks));

        for(int i = 1; i < points.size(); i++){
            p.chat("//pos2 " + getXYZ(points.get(i), blocks));
        }
    }

    public static int createPolyLine(Player p, List<Vector> points, String lineMaterial, boolean connectLineEnds, Block[][][] blocks){
        p.chat("//sel cuboid");
        p.chat("//pos1 " + getXYZ(points.get(0), blocks));
        int operations = 0;

        List<String> positions = new ArrayList<>();
        for(int i = 1; i < points.size(); i++)
            positions.add(getXYZ(points.get(i), blocks));
        String pos2 = getXYZ(points.get(0), blocks);

        for(int i = 1; i < points.size(); i++){
            p.chat("//pos2 " + positions.get(i-1));
            p.chat("//line " + lineMaterial);
            operations++;
            p.chat("//pos1 " + positions.get(i-1));
        }

        if(connectLineEnds){
            p.chat("//pos2 " + pos2);
            p.chat("//line " + lineMaterial);
            operations++;
        }

        return operations;
    }

    public static String getXYZ(Vector vector, Block[][][] blocks){
        int maxHeight = vector.getBlockY();

        if(blocks != null)
            maxHeight = Generator.getMaxHeight(blocks, vector.getBlockX(), vector.getBlockZ(), Material.LOG, Material.LOG_2, Material.LEAVES, Material.LEAVES_2, Material.WOOL);
        if(maxHeight == 0)
            maxHeight = vector.getBlockY();

        return vector.getBlockX() + "," + maxHeight + "," + vector.getBlockZ();
    }

    /** As long as two neighboring vectors are further than a given distance of blocks apart, add a new vector in between them
     *
     * @param points
     * @return
     */
    public static List<Vector> populatePoints(List<Vector> points, int distance){
        List<Vector> result = new ArrayList<>();

        // Go through all points
        boolean found = true;
        while(found){
            found = false;
            for(int i = 0; i < points.size()-1; i++){
                Vector p1 = points.get(i);
                Vector p2 = points.get(i+1);

                // Add the first point
                result.add(p1);

                // If the distance between the two points is greater than the given distance, add a new point in between them
                if(p1.distance(p2) > distance){
                    Vector v1 = p2.subtract(p1);
                    Vector v2 = v1.multiply(0.5);
                    Vector v3 = p1.add(v2);

                    // Add the new point
                    result.add(v3);
                    found = true;
                }
            }

            result.add(points.get(points.size()-1));
            points = result;
            result = new ArrayList<>();
        }

        return points;
    }

}

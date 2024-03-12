package net.buildtheearth.modules.generator.utils;

import clipper2.Clipper;
import clipper2.core.Path64;
import clipper2.core.Paths64;
import clipper2.core.Point64;
import clipper2.offset.EndType;
import clipper2.offset.JoinType;
import com.cryptomorin.xseries.XMaterial;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.*;
import com.sk89q.worldedit.regions.selector.ConvexPolyhedralRegionSelector;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import com.sk89q.worldedit.regions.selector.Polygonal2DRegionSelector;
import com.sk89q.worldedit.session.SessionManager;
import net.buildtheearth.modules.common.CommonModule;
import net.buildtheearth.modules.generator.GeneratorModule;
import net.buildtheearth.modules.generator.model.Operation;
import net.buildtheearth.utils.MenuItems;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/** This class contains static utility methods for the generator module.
 *
 * @author MineFact
 */
public class GeneratorUtils {


    /**
     * Returns the WorldEdit selection Vector from a player no matter which type of selection the player made.
     *
     * @param region The WorldEdit region to get the selection from
     * @return A list of vectors representing the selection
     */
    public static List<Vector> getSelectionPointsFromRegion(Region region) {
        List<Vector> points = new ArrayList<>();

        if (region instanceof Polygonal2DRegion) {
            Polygonal2DRegion polyRegion = (Polygonal2DRegion) region;

            // In latest FAWE, the points are stored as BlockVector2
            // In 1.12 WorldEdit, the points are stored as BlockVector2D
            // Both classes have the same methods, so we can use reflection to get the methods
            for (Object blockVectorObj : polyRegion.getPoints()) {
                try {
                    Class<?> blockVectorClass = blockVectorObj.getClass();
                    Method getXMethod = blockVectorClass.getMethod("getBlockX");
                    Method getZMethod = blockVectorClass.getMethod("getBlockZ");

                    int x = (Integer) getXMethod.invoke(blockVectorObj);
                    int z = (Integer) getZMethod.invoke(blockVectorObj);

                    points.add(new Vector(x, 0, z));
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

        } else if (region instanceof ConvexPolyhedralRegion) {
            ConvexPolyhedralRegion convexRegion = (ConvexPolyhedralRegion) region;

            // In latest FAWE, the points are stored as BlockVector2
            // In 1.12 WorldEdit, the points are stored as BlockVector2D
            // Both classes have the same methods, so we can use reflection to get the methods
            for (Object blockVectorObj : convexRegion.getVertices()) {
                try {
                    Class<?> blockVectorClass = blockVectorObj.getClass();
                    Method getXMethod = blockVectorClass.getMethod("getBlockX");
                    Method getYMethod = blockVectorClass.getMethod("getBlockY");
                    Method getZMethod = blockVectorClass.getMethod("getBlockZ");

                    int x = (Integer) getXMethod.invoke(blockVectorObj);
                    int y = (Integer) getYMethod.invoke(blockVectorObj);
                    int z = (Integer) getZMethod.invoke(blockVectorObj);

                    points.add(new Vector(x, y, z));
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

        }else if (region instanceof CuboidRegion) {
            CuboidRegion cuboidRegion = (CuboidRegion) region;

            try {
                Class<?> regionClass = cuboidRegion.getClass();
                Method getPos1Method = regionClass.getMethod("getPos1");
                Method getPos2Method = regionClass.getMethod("getPos2");

                Class<?> vectorClass = getPos1Method.getReturnType();

                Method getXMethod = vectorClass.getMethod("getBlockX");
                Method getYMethod = vectorClass.getMethod("getBlockY");
                Method getZMethod = vectorClass.getMethod("getBlockZ");

                Object pos1 = getPos1Method.invoke(region);
                Object pos2 = getPos2Method.invoke(region);

                int x1 = (Integer) getXMethod.invoke(pos1);
                int y1 = (Integer) getYMethod.invoke(pos1);
                int z1 = (Integer) getZMethod.invoke(pos1);

                int x2 = (Integer) getXMethod.invoke(pos2);
                int y2 = (Integer) getYMethod.invoke(pos2);
                int z2 = (Integer) getZMethod.invoke(pos2);

                points.add(new Vector(x1, y1, z1));
                points.add(new Vector(x2, y2, z2));
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else
            return null;

        return points;
    }

    public static Vector[] getMinMaxPoints(Region region){
        Vector[] minMax = new Vector[2];

        try{
            Class<?> regionClass = region.getClass();
            Method getMinimumPointMethod = regionClass.getMethod("getMinimumPoint");
            Method getMaximumPointMethod = regionClass.getMethod("getMaximumPoint");

            Class<?> vectorClass = getMinimumPointMethod.getReturnType();

            Method getXMethod = vectorClass.getMethod("getBlockX");
            Method getYMethod = vectorClass.getMethod("getBlockY");
            Method getZMethod = vectorClass.getMethod("getBlockZ");

            Object minPoint = getMinimumPointMethod.invoke(region);
            Object maxPoint = getMaximumPointMethod.invoke(region);

            int minX = (Integer) getXMethod.invoke(minPoint);
            int minY = (Integer) getYMethod.invoke(minPoint);
            int minZ = (Integer) getZMethod.invoke(minPoint);

            int maxX = (Integer) getXMethod.invoke(maxPoint);
            int maxY = (Integer) getYMethod.invoke(maxPoint);
            int maxZ = (Integer) getZMethod.invoke(maxPoint);

            minMax[0] = new Vector(minX, minY, minZ);
            minMax[1] = new Vector(maxX, maxY, maxZ);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return minMax;
    }

    /**
     * Converts a String[] of arguments to a String[] of flags.
     *
     * @param args The arguments to be converted.
     * @return The converted flags.
     */
    public static String[] convertArgsToFlags(String[] args) {
        String argsString = " " + StringUtils.join(Arrays.copyOfRange(args, 1, args.length), " ");
        String[] argsArray = argsString.split(" -");

        return Arrays.copyOfRange(argsArray, 1, argsArray.length);
    }

    /** Converts a string with all flags and values to a string array with the flag name and the flag value.
     *
     * @param flagAndValue The string with all flags and values. Example is a command like /gen house -w 10 -h 10
     * @param p The player who should receive an error message if the flag value is invalid.
     * @return The string array with the flag name and the flag value.
     */
    public static String[] convertToFlagAndValue(String flagAndValue, Player p){
        String[] values = flagAndValue.split(" ");
        String flagName = values[0];
        String flagValue;

        try {
            flagValue = StringUtils.join(Arrays.copyOfRange(values, 1, values.length), " ");
        } catch (ArrayIndexOutOfBoundsException e) {
            p.sendMessage("§cInvalid flag value: -" + flagName + "§n§c ???");
            return null;
        }

        return new String[]{flagName, flagValue};
    }

    /** Analyzes a region and returns a three-dimensional array of all blocks in the region.
     * The size of the array is defined by the width, height and length of the region from WorldEdit of the player.
     * By saving the blocks in an array, the generation can be done much faster later once the region blocks need to be inspected.
     *
     * @param p     The player whose selection should be analyzed.
     * @param world The world in which the region is located.
     * @return A three-dimensional array of all blocks in the region.
     */
    public static Block[][][] analyzeRegion(Player p, World world) {
        // Get WorldEdit selection of player
        Region region = getWorldEditSelection(p);

        if(region == null)
            return null;

        Block[][][] blocks = new Block[region.getWidth()][region.getHeight()][region.getLength()];

        try {
            Class<?> regionClass = region.getClass();

            // Reflectively access the minimum and maximum points
            Method getMinimumPoint = regionClass.getMethod("getMinimumPoint");
            Method getMaximumPoint = regionClass.getMethod("getMaximumPoint");
            Method contains;

            if(CommonModule.getInstance().getDependencyComponent().isLegacyWorldEdit())
                contains = regionClass.getMethod("contains", Vector.class);
            else
                contains = regionClass.getMethod("contains", com.sk89q.worldedit.math.BlockVector3.class);

            Object minPoint = getMinimumPoint.invoke(region);
            Object maxPoint = getMaximumPoint.invoke(region);

            Class<?> vectorClass = minPoint.getClass();

            // Assume getBlockX/Y/Z methods exist in both Vector and BlockVector3
            Method getBlockX = vectorClass.getMethod("getBlockX");
            Method getBlockY = vectorClass.getMethod("getBlockY");
            Method getBlockZ = vectorClass.getMethod("getBlockZ");

            int minX = (Integer) getBlockX.invoke(minPoint);
            int minY = (Integer) getBlockY.invoke(minPoint);
            int minZ = (Integer) getBlockZ.invoke(minPoint);
            int maxX = (Integer) getBlockX.invoke(maxPoint);
            int maxY = (Integer) getBlockY.invoke(maxPoint);
            int maxZ = (Integer) getBlockZ.invoke(maxPoint);

            for (int i = minX; i <= maxX; i++)
                for (int j = minY; j <= maxY; j++)
                    for (int k = minZ; k <= maxZ; k++) {
                        // Dynamically create vector instance to use in contains method
                        Object vectorInstance;

                        // First, try using BlockVector3.at(int, int, int) - common in FAWE and newer WorldEdit
                        try {
                            Class<?> blockVector3Class = Class.forName("com.sk89q.worldedit.math.BlockVector3");
                            Method atMethod = blockVector3Class.getMethod("at", int.class, int.class, int.class);
                            vectorInstance = atMethod.invoke(null, i, j, k);
                        } catch (ClassNotFoundException | NoSuchMethodException e) {

                            // If BlockVector3 or at method is not found, fallback to WorldEdit's Vector
                            vectorInstance = vectorClass.getConstructor(int.class, int.class, int.class).newInstance(i, j, k);
                        }

                        if(vectorInstance != null) {
                            boolean regionContains = (Boolean) contains.invoke(region, vectorInstance);

                            if (regionContains) {
                                Block block = world.getBlockAt(i, j, k);
                                blocks[i - minX][j - minY][k - minZ] = block;
                            }
                        }
                    }


            return blocks;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Checks the maximum height of a polygon region
     *
     * @param blocks List of blocks in polygon region
     * @return Maximum height of polygon region
     */
    public static int getMaxHeight(Block[][][] blocks, Material... ignoreMaterials){
        int maxHeight = 0;
        List<Material> ignoreMaterialsList = Arrays.asList(ignoreMaterials);

        for (Block[][] block2D : blocks)
            for (Block[] block1D : block2D)
                for (Block block : block1D)
                    if (block != null && !ignoreMaterialsList.contains(block.getType()) && block.getType().isSolid() && !block.isLiquid() && block.getY() > maxHeight)
                        maxHeight = block.getY();

        return maxHeight;
    }

    /**
     * Checks the max height of a polygon region given a certain x and z coordinate
     *
     * @param blocks List of blocks in polygon region
     * @return Maximum height of polygon region
     */
    public static int getMaxHeight(Block[][][] blocks, int x, int z, Material... ignoreMaterials){
        int maxHeight = 0;
        List<Material> ignoreMaterialsList = Arrays.asList(ignoreMaterials);

        for (Block[][] block2D : blocks)
            for (Block[] block1D : block2D)
                for (Block block : block1D)
                    if (block != null && block.getX() == x && block.getZ() == z && block.getY() > maxHeight && !ignoreMaterialsList.contains(block.getType()) && block.getType().isSolid() && !block.isLiquid())
                        maxHeight = block.getY();

        return maxHeight;
    }

    /**
     * Checks if polygon region contains a block of a certain type
     *
     * @param blocks   List of blocks in polygon region
     * @param xMaterial Material to check for (e.g. XMaterial.WALL_SIGN)
     * @return true if polygon region contains the block, false otherwise
     */
    public static boolean containsBlock(Block[][][] blocks, XMaterial xMaterial){
        return containsBlock(blocks, xMaterial, 1);
    }

    /**
     * Checks if polygon region contains a minimum amount of blocks of a certain type
     * @param blocks List of blocks in polygon region
     * @param xMaterial Material to check for (e.g. XMaterial.WALL_SIGN)
     * @param requiredAmount The minimum amount required to return true
     * @return true if polygon region contains the required amount of the block, false otherwise
     */
    public static boolean containsBlock(Block[][][] blocks, XMaterial xMaterial, int requiredAmount){
        int amountFound = 0;
        for (Block[][] block2D : blocks)
            for (Block[] block1D : block2D)
                for (Block block : block1D)
                    if (CommonModule.getInstance().getVersionComponent().is_1_12()) {
                        if (block != null && block.getType() == xMaterial.parseMaterial() && block.getData() == xMaterial.getData())
                            amountFound++;
                    }else {
                        if (block != null && block.getType() == xMaterial.parseMaterial())
                            amountFound++;
                    }

        return amountFound >= requiredAmount;
    }

    /**
     * Adjusts the height of a list of vectors so that they are on the surface of the terrain.
     *
     * @param points List of vectors to adjust
     * @param blocks List of blocks in polygon region
     */
    public static void adjustHeight(List<Vector> points, Block[][][] blocks){
        for(int i = 0; i < points.size(); i++) {
            Vector point = points.get(i);
            point = point.setY(getMaxHeight(blocks, point.getBlockX(), point.getBlockZ(), MenuItems.getIgnoredMaterials()));
            points.set(i, point);
        }
    }

    /** As long as two neighboring vectors are further than a given distance of blocks apart, add a new vector in between them
     *
     * @param points   The points to populate
     * @return         The populated points
     */
    public static List<Vector> populatePoints(List<Vector> points, int distance){
        List<Vector> result = new ArrayList<>();

        // Go through all points
        boolean found = true;
        while(found){
            found = false;
            for (int i = 0; i < points.size() - 1; i++) {
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

    /** As long as two neighboring vectors are closer than a given distance of blocks apart, remove the second point. The distances switch between distance1 and distance2
     *
     * @param points    The points to reduce
     * @return          The reduced points
     */
    public static List<Vector> reducePoints(List<Vector> points, int distance1, int distance2){
        points = new ArrayList<>(points);

        // Go through all points
        boolean found = true;
        while(found){
            found = false;
            for(int i = 0; i < points.size()-1; i++){
                Vector p1 = points.get(i);
                Vector p2 = points.get(i+1);

                int distance = distance1;
                // Switch between distance1 and distance2
                if(i%2 == 0)
                    distance = distance2;

                // If the distance between the two points is less than the given distance, remove the second point
                if(p1.distance(p2) < distance){
                    points.remove(p2);
                    found = true;
                    break;
                }
            }
        }

        return points;
    }

    /** Extends a polyline by taking the first two points and the last two points of the polyline and extending them
     *
     * @param vectors The polyline to extend
     * @return The extended polyline
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
     * @param vectors The polyline to shorten
     * @return The shortened polyline
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

    /** Returns the closest vector from a list of vectors to a reference vector
     *
     * @param vectors List of vectors to search through
     * @param reference Reference vector to compare to
     * @return Closest vector to the reference vector
     */
    public static Vector getClosestVector(List<Vector> vectors, Vector reference){
        Vector closestVector = null;
        double closestDistance = Double.MAX_VALUE;

        for(Vector vector : vectors){
            double distance = vector.distance(reference);
            if(distance < closestDistance){
                closestVector = vector;
                closestDistance = distance;
            }
        }

        return closestVector;
    }

    /**
     * Returns a temporary XYZ String that indicates that the height of the point should be inspected later to match the surface of the terrain.
     *
     * @param vector The vector to get the XYZ String from
     * @return The temporary XYZ String
     */
    public static String getXYZ(Vector vector){
        return "%%XYZ/" + vector.getBlockX() + "," + vector.getBlockY() + "," + vector.getBlockZ() + "/%%";
    }

    /**
     * Returns a XYZ String with the height of the point matching the surface of the terrain.
     *
     * @param vector The vector to get the XYZ String from
     * @param blocks The dataset to get the height from
     * @return The XYZ String
     */
    public static String getXYZ(Vector vector, Block[][][] blocks){
        int maxHeight = vector.getBlockY();

        if(blocks != null)
            maxHeight = getMaxHeight(blocks, vector.getBlockX(), vector.getBlockZ(), MenuItems.getIgnoredMaterials());
        if(maxHeight == 0)
            maxHeight = vector.getBlockY();

        return vector.getBlockX() + "," + maxHeight + "," + vector.getBlockZ();
    }

    /**
     * Returns a XYZ String with the height of the point matching the surface of the terrain.
     *
     * @param vector The vector to get the XYZ String from
     * @param blocks The dataset to get the height from
     * @return The XYZ String
     */
    public static String getXYZWithVerticalOffset(Vector vector, Block[][][] blocks, int offset){
        int maxHeight = vector.getBlockY();

        if(blocks != null)
            maxHeight = getMaxHeight(blocks, vector.getBlockX(), vector.getBlockZ(), MenuItems.getIgnoredMaterials()) + offset;
        if(maxHeight == 0)
            maxHeight = vector.getBlockY();

        return vector.getBlockX() + "," + maxHeight + "," + vector.getBlockZ();
    }

    public static Path64 convertVectorListToPath64(List<Vector> vectors, Vector reference){
        List<Point64> points = new ArrayList<>();
        for(Vector vector : vectors)
            points.add(new Point64(vector.getBlockX() - reference.getBlockX(), vector.getBlockZ() - reference.getBlockZ()));

        return new Path64(points);
    }

    public static List<List<Vector>> convertPathsToVectorList(Paths64 pathsD, Vector reference, int minHeight, int maxHeight){
        List<List<Vector>> vectors = new ArrayList<>();

        for(Path64 path : new ArrayList<>(pathsD)) {
            List<Vector> vectorList = new ArrayList<>();

            for(Point64 point : new ArrayList<>(path))
                vectorList.add(new Vector(point.x + reference.getX(), minHeight, point.y + reference.getZ()));

            Vector vector = vectorList.get(vectorList.size() - 1).setY(maxHeight);
            vectorList.set(vectorList.size() - 1, vector);

            vectors.add(vectorList);
        }

        return vectors;
    }

    /**
     * Shifts the points in a polyline by a given amount.
     * Sometimes during shifting there are multiple paths created.
     * For example, if you have a polyline that intersects itself, there is an inner and outer path.
     * This method can either return all the paths or just the longest one.
     *
     * @see #shiftPointsAll(List, double)
     *
     * @param vectors The polyline to shift
     * @param shift The amount to shift the points by
     * @param useLongestPathOnly Whether to only return the longest path
     * @return The shifted polyline
     */
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

        // Otherwise, return all paths combined into one
        }else{
            List<Vector> result = new ArrayList<>();
            for(List<Vector> vectorList : resultVectors)
                result.addAll(vectorList);

            return result;
        }
    }

    /**
     * Shifts the points in a polyline by a given amount.
     * Sometimes during shifting there are multiple paths created.
     * For example, if you have a polyline that intersects itself, there is an inner and outer path.
     * This method returns a list of all the paths.
     *
     * @see #shiftPoints(List, double, boolean)
     *
     * @param vectors The polyline to shift
     * @param shift The amount to shift the points by
     * @return The shifted polyline
     */
    public static List<List<Vector>> shiftPointsAll(List<Vector> vectors, double shift) {
        Vector reference = vectors.get(0);
        int minHeight = getMinHeight(vectors);
        int maxHeight = getMaxHeight(vectors);
        Paths64 paths = new Paths64();
        paths.add(convertVectorListToPath64(vectors, reference));
        Paths64 inflatedPath = Clipper.InflatePaths(paths, shift, JoinType.Round, EndType.Butt, 2);

        return convertPathsToVectorList(inflatedPath, reference, minHeight, maxHeight);
    }

    /**
     * Gets the minimum height of a list of vectors
     *
     * @param vectors The list of vectors to get the minimum height of
     * @return The minimum height
     */
    public static int getMinHeight(List<Vector> vectors){
        int minHeight = Integer.MAX_VALUE;
        for(Vector vector : vectors)
            minHeight = Math.min(minHeight, vector.getBlockY());

        return minHeight;
    }

    /**
     * Gets the maximum height of a list of vectors
     *
     * @param vectors The list of vectors to get the maximum height of
     * @return The maximum height
     */
    public static int getMaxHeight(List<Vector> vectors){
        int maxHeight = Integer.MIN_VALUE;
        for(Vector vector : vectors)
            maxHeight = Math.max(maxHeight, vector.getBlockY());

        return maxHeight;
    }


    /**
     * Returns the current WorldEdit selection of a player.
     *
     * @param p The player to get the selection from
     */
    public static RegionSelector getCurrentSelection(Player p){
        WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

        if(worldEditPlugin == null)
            return null;

        Actor actor = worldEditPlugin.wrapPlayer(p);
        SessionManager sessionManager = WorldEdit.getInstance().getSessionManager();
        com.sk89q.worldedit.world.World world = sessionManager.get(actor).getSelectionWorld();

        return sessionManager.get(actor).getRegionSelector(world);
    }

    /**
     * Returns the WorldEdit selection of a player.
     *
     * @param p The player to get the selection from
     * @param regionSelector The region selector to get the selection from
     */
    public static void restoreSelection(Player p, RegionSelector regionSelector){
        WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

        if(worldEditPlugin == null)
            return;

        Actor actor = worldEditPlugin.wrapPlayer(p);
        SessionManager sessionManager = WorldEdit.getInstance().getSessionManager();
        com.sk89q.worldedit.world.World world = sessionManager.get(actor).getSelectionWorld();

        sessionManager.get(actor).setRegionSelector(world, regionSelector);
    }

    /**
     * Creates a Cuboid WorldEdit selection from a list of points and execute it right away.
     *
     * @param p The player to create the selection for
     * @param vector1 Position 1
     * @param vector2 Position 2
     */
    public static void createCuboidSelection(Player p, Vector vector1, Vector vector2){
        WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

        if(worldEditPlugin == null)
            return;

        Actor actor = worldEditPlugin.wrapPlayer(p);
        SessionManager sessionManager = WorldEdit.getInstance().getSessionManager();
        com.sk89q.worldedit.world.World world = sessionManager.get(actor).getSelectionWorld();

        sessionManager.get(actor).setRegionSelector(world,
            new CuboidRegionSelector(world,
                BlockVector3.at(vector1.getBlockX(), vector1.getBlockY(), vector1.getBlockZ()),
                BlockVector3.at(vector2.getBlockX(), vector2.getBlockY(), vector2.getBlockZ())
            )
        );
    }


    /**
     * Creates a Polygon WorldEdit selection from a list of points and execute it right away.
     * This functions determines the current surface height of each vector directly.
     *
     * @param p The player to create the selection for
     * @param points The list of points to create the selection from
     */
    public static void createPolySelection(Player p, List<Vector> points, Block[][][] blocks){
        WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

        if(worldEditPlugin == null)
            return;

        Actor actor = worldEditPlugin.wrapPlayer(p);
        SessionManager sessionManager = WorldEdit.getInstance().getSessionManager();
        com.sk89q.worldedit.world.World world = sessionManager.get(actor).getSelectionWorld();

        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        List<BlockVector2> blockVector2List = new ArrayList<>();
        for(Vector vector : points) {
            blockVector2List.add(BlockVector2.at(vector.getBlockX(), vector.getBlockZ()));

            int y = getMaxHeight(blocks, vector.getBlockX(), vector.getBlockZ(), MenuItems.getIgnoredMaterials());

            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
        }

        sessionManager.get(actor).setRegionSelector(world,
            new Polygonal2DRegionSelector(world, blockVector2List, minY, maxY)
        );
    }

    /**
     * Creates a Convex WorldEdit selection from a list of points and execute it right away.
     * This functions determines the current surface height of each vector directly.
     *
     * @param p The player to create the selection for
     * @param points The list of points to create the selection from
     */
    public static void createConvexSelection(Player p, List<Vector> points, Block[][][] blocks){
        p.chat("//sel convex");
        p.chat("//pos1 " + getXYZ(points.get(0), blocks));

        for(int i = 1; i < points.size(); i++)
            p.chat("//pos2 " + getXYZ(points.get(i), blocks));
    }



    /**
     * Draws a curved poly line from a list of points and adds it to the list of commands to execute.
     * It draws the curve by drawing a straight line between each of the points.
     *
     * @param commands The list of commands to add the selection to
     * @param points The list of points to create the selection from
     * @param lineMaterial The material to draw the line with
     * @param connectLineEnds Whether to connect the line ends in case the line is a circle
     * @param blocks The blocks to get the surface height from
     * @param offset The vertical offset you want the PolyLine to be created at
     * @return The amount of operations used to accomplish this
     */
    public static int createPolyLine(List<Operation> commands, List<Vector> points, String lineMaterial, boolean connectLineEnds, Block[][][] blocks, int offset){
        commands.add(new Operation("//sel cuboid"));

        commands.add(new Operation("//pos1 " + getXYZWithVerticalOffset(points.get(0), blocks, offset)));

        int operations = 0;

        List<String> positions = new ArrayList<>();
        for(int i = 1; i < points.size(); i++)
            positions.add(getXYZWithVerticalOffset(points.get(i), blocks, offset));
        String pos2 = getXYZWithVerticalOffset(points.get(0), blocks, offset);

        for(int i = 1; i < points.size(); i++){
            commands.add(new Operation("//pos2 " + positions.get(i-1)));
            commands.add(new Operation("//line " + lineMaterial));
            operations++;
            commands.add(new Operation("//pos1 " + positions.get(i-1)));
        }

        if(connectLineEnds){
            commands.add(new Operation("//pos2 " + pos2));
            commands.add(new Operation("//line " + lineMaterial));
            operations++;
        }

        return operations;
    }

    /**
     * Returns the current WorldEdit selection of a player.
     *
     * @param p The player whose selection should be returned.
     * @return The current WorldEdit selection of the player.
     */
    public static Region getWorldEditSelection(Player p){
        Region plotRegion;

        try {
            plotRegion = Objects.requireNonNull(WorldEdit.getInstance().getSessionManager().findByName(p.getName())).getSelection(
                    Objects.requireNonNull(WorldEdit.getInstance().getSessionManager().findByName(p.getName())).getSelectionWorld());
        } catch (NullPointerException | IncompleteRegionException ex) {
            return null;
        }

        return plotRegion;
    }

    /**
     * Checks if Schematic Brush is installed and sends the player a message if it isn't.
     *
     * @param p The player to check for
     * @return Whether Schematic Brush is installed
     */
    public static boolean checkIfSchematicBrushIsInstalled(Player p){
        // Check if WorldEdit is enabled
        if (!CommonModule.getInstance().getDependencyComponent().isSchematicBrushEnabled()) {
            p.sendMessage("§cPlease install Schematic Brush to use this tool. You can ask the server administrator to install it.");
            p.sendMessage(" ");
            p.sendMessage("§cFor more installation help, please see the wiki:");
            p.sendMessage("§c" + GeneratorModule.INSTALL_WIKI);
            return false;
        }
        return true;
    }


    /**
     * Checks if the player has a WorldEdit selection and sends them a message if they don't.
     *
     * @param p The player to check for
     * @return Whether the player has a WorldEdit selection
     */
    public static boolean checkForNoWorldEditSelection(Player p){
        // Get WorldEdit selection of player
        Region polyRegion = getWorldEditSelection(p);

        if(polyRegion != null)
            return false;

        p.sendMessage("§cPlease make a WorldEdit Selection first.");
        p.closeInventory();
        p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
        return true;
    }


    /**
     * Checks if the player has a brick block in their selection and sends them a message if they don't.
     *
     * @param p The player to check for
     * @return Whether the player has a brick block in their selection
     */
    public static boolean checkForBrickOutline(Block[][][] blocks, Player p){
        if(!containsBlock(blocks, XMaterial.BRICKS)){
            p.sendMessage("§cPlease make a selection around an outline.");
            p.closeInventory();
            p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            GeneratorModule.getInstance().sendWikiLink(p);

            return false;
        }

        return true;
    }

    /**
     * Checks if the player has a yellow wool block in their selection and sends them a message if they don't.
     *
     * @param p The player to check for
     * @return Whether the player has a yellow wool block in their selection
     */
    public static boolean checkForWoolBlock(Block[][][] blocks, Player p){
        if(!containsBlock(blocks, XMaterial.YELLOW_WOOL)){
            p.sendMessage("§cPlease place a yellow wool block inside the outline.");
            p.closeInventory();
            p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            GeneratorModule.getInstance().sendWikiLink(p);

            ItemStack yellowWool = XMaterial.YELLOW_WOOL.parseItem();
            if(!p.getInventory().contains(yellowWool)) {
                p.getInventory().setItem(4, yellowWool);
                p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
            }

            return false;
        }
        return true;
    }
}
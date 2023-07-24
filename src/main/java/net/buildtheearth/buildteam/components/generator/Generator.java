package net.buildtheearth.buildteam.components.generator;

import clipper2.Clipper;
import clipper2.core.Path64;
import clipper2.core.Paths64;
import clipper2.core.Point64;
import clipper2.offset.EndType;
import clipper2.offset.JoinType;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import lombok.Getter;
import net.buildtheearth.Main;
import net.buildtheearth.buildteam.BuildTeamTools;
import net.buildtheearth.buildteam.components.generator.field.Field;
import net.buildtheearth.buildteam.components.generator.house.House;
import net.buildtheearth.buildteam.components.generator.rail.Rail;
import net.buildtheearth.buildteam.components.generator.road.Road;
import net.buildtheearth.buildteam.components.generator.tree.Tree;
import net.buildtheearth.utils.Item;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class Generator {

    public static String WIKI_PAGE = "https://github.com/BuildTheEarth/BuildTeamTools/wiki/Generator";
    public static String INSTALL_WIKI = "https://github.com/BuildTheEarth/BuildTeamTools/wiki/Installation";

    private static HashMap<UUID, History> playerHistory = new HashMap<>();

    @Getter
    private List<Command> commands = new ArrayList<>();

    @Getter
    private House house;
    @Getter
    private Road road;
    @Getter
    private Rail rail;

    @Getter
    private Tree tree;
    @Getter
    private Field field;

    public Generator(){
        house = new House();
        road = new Road();
        rail = new Rail();
        tree = new Tree();
        field = new Field();
    }

    /** Processes the command queues one after another and lets the waiting players know their position in the queue and the percentage of the current generation.
     *
     * Relations:
     * @see Command
     * @see Command#tick()
     */
    public void tick(){
        if(commands.size() == 0)
            return;

        if(commands.get(0).getCommands().size() == 0){
            commands.remove(0);
            return;
        }

        for(int i = 1; i < commands.size(); i++) {
            boolean isInQueue = false;

            for(int j = i-1; j > 0; j--)
                if(commands.get(i).getPlayer().getUniqueId().equals(commands.get(j).getPlayer().getUniqueId()))
                    isInQueue = true;

            if(commands.get(0).getPlayer().getUniqueId().equals(commands.get(i).getPlayer().getUniqueId()))
               isInQueue = true;

            if(isInQueue)
                continue;

            commands.get(i).getPlayer().sendActionBar("§c§lOther Generation in Progress. Position: §e" + i + "/" + commands.size() + " (" + commands.get(0).getPercentage() + "%)");
        }

        commands.get(0).tick();
    }



    /** Returns the Generator History of a player.
     *
     * @param p The player whose history should be returned.
     * @return The Generator History of the player.
     */
    public static History getPlayerHistory(Player p){
        if(!playerHistory.containsKey(p.getUniqueId()))
            playerHistory.put(p.getUniqueId(), new History(p));

        return playerHistory.get(p.getUniqueId());
    }

    /** Sends more information about the generator to a player.
     *  The WIKI_PAGE varies depending on the generator.
     *
     * @param p The player who should receive the information.
     */
    public static void sendMoreInfo(Player p){
        p.sendMessage(" ");
        p.sendMessage("§cFor more information take a look here:");
        p.sendMessage("§c" + WIKI_PAGE);
    }

    /** Converts a String[] of arguments to a String[] of flags.
     *
     * @param args The arguments to be converted.
     * @return The converted flags.
     */
    public static String[] convertArgsToFlags(String[] args){
        String argsString = " " + StringUtils.join(Arrays.copyOfRange(args, 1, args.length), " ");
        String[] argsArray = argsString.split(" -");
        String[] flagsArray = Arrays.copyOfRange(argsArray, 1, argsArray.length);

        return flagsArray;
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
     * @param p The player whose selection should be analyzed.
     * @param world The world in which the region is located.
     * @return A three-dimensional array of all blocks in the region.
     */
    public static Block[][][] analyzeRegion(Player p, World world) {
        // Get WorldEdit selection of player
        Region polyRegion = Generator.getWorldEditSelection(p);

        Block[][][] blocks = new Block[polyRegion.getWidth()][polyRegion.getHeight()][polyRegion.getLength()];

        for (int i = polyRegion.getMinimumPoint().getBlockX(); i <= polyRegion.getMaximumPoint().getBlockX(); i++)
            for (int j = polyRegion.getMinimumPoint().getBlockY(); j <= polyRegion.getMaximumPoint().getBlockY(); j++)
                for (int k = polyRegion.getMinimumPoint().getBlockZ(); k <= polyRegion.getMaximumPoint().getBlockZ(); k++)
                    if (polyRegion.contains(new Vector(i, j, k))) {
                        Block block = world.getBlockAt(i, j, k);
                        blocks[i - polyRegion.getMinimumPoint().getBlockX()][j - polyRegion.getMinimumPoint().getBlockY()][k - polyRegion.getMinimumPoint().getBlockZ()] = block;
                    }

        return blocks;
    }

    /**
     * Checks the maximum height of a polygon region
     * @param blocks List of blocks in polygon region
     * @return Maximum height of polygon region
     */
    public static int getMaxHeight(Block[][][] blocks, Material... ignoreMaterials){
        int maxHeight = 0;
        List<Material> ignoreMaterialsList = Arrays.asList(ignoreMaterials);

        for (Block[][] block2D : blocks)
            for (Block[] block1D : block2D)
                for (Block block : block1D)
                    if (block != null &&! ignoreMaterialsList.contains(block.getType()) && block.getType().isSolid() &&! block.isLiquid() && block.getY() > maxHeight)
                        maxHeight = block.getY();

        return maxHeight;
    }

    /**
     * Checks the max height of a polygon region given a certain x and z coordinate
     * @param blocks List of blocks in polygon region
     * @return Maximum height of polygon region
     */
    public static int getMaxHeight(Block[][][] blocks, int x, int z, Material... ignoreMaterials){
        int maxHeight = 0;
        List<Material> ignoreMaterialsList = Arrays.asList(ignoreMaterials);

        for (Block[][] block2D : blocks)
            for (Block[] block1D : block2D)
                for (Block block : block1D)
                    if (block != null && block.getX() == x && block.getZ() == z && block.getY() > maxHeight &&! ignoreMaterialsList.contains(block.getType()) && block.getType().isSolid() &&! block.isLiquid())
                        maxHeight = block.getY();

        return maxHeight;
    }

    /**
     * Checks if polygon region contains a block of a certain type
     * @param blocks List of blocks in polygon region
     * @param material Material to check for (e.g. Material.WALL_SIGN)
     * @param data Data value of material to check for (0-15)
     * @return true if polygon region contains a sign, false otherwise
     */
    public static boolean containsBlock(Block[][][] blocks, Material material, byte data){
        for (Block[][] block2D : blocks)
            for (Block[] block1D : block2D)
                for (Block block : block1D)
                    if (block != null && block.getType() == material && block.getData() == data)
                        return true;

        return false;
    }

    /**
     * Adjusts the height of a list of vectors so that they are on the surface of the terrain.
     * @param points - List of vectors to adjust
     * @param blocks - List of blocks in polygon region
     * @return List of vectors with adjusted height
     */
    public static List<Vector> adjustHeight(List<Vector> points, Block[][][] blocks){

        for(int i = 0; i < points.size(); i++) {
            Vector point = points.get(i);
            point = point.setY(Generator.getMaxHeight(blocks, point.getBlockX(), point.getBlockZ(), Material.LOG, Material.LOG_2, Material.LEAVES, Material.LEAVES_2, Material.SNOW));
            points.set(i, point);
        }

        return points;
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

    /** As long as two neighboring vectors are closer than a given distance of blocks apart, remove the second point. The distances switches between distance1 and distance2
     *
     * @param points
     * @return
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

    /**
     * Returns a temporary XYZ String that indicates that the height of the point should be inspected later to match the surface of the terrain.
     *
     * @param vector - The vector to get the XYZ String from
     * @return The temporary XYZ String
     */
    public static String getXYZ(Vector vector){
        return "%%XYZ/" + vector.getBlockX() + "," + vector.getBlockY() + "," + vector.getBlockZ() + "/%%";
    }

    /**
     * Returns a XYZ String with the height of the point matching the surface of the terrain.
     *
     * @param vector - The vector to get the XYZ String from
     * @param blocks - The dataset to get the height from
     * @return The XYZ String
     */
    public static String getXYZ(Vector vector, Block[][][] blocks){
        int maxHeight = vector.getBlockY();

        if(blocks != null)
            maxHeight = Generator.getMaxHeight(blocks, vector.getBlockX(), vector.getBlockZ(), Material.LOG, Material.LOG_2, Material.LEAVES, Material.LEAVES_2, Material.WOOL, Material.SNOW);
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
     * @param vectors - The polyline to shift
     * @param shift - The amount to shift the points by
     * @param useLongestPathOnly - Whether to only return the longest path
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
     * @param vectors - The polyline to shift
     * @param shift - The amount to shift the points by
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
     * @param vectors - The list of vectors to get the minimum height of
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
     * @param vectors - The list of vectors to get the maximum height of
     * @return The maximum height
     */
    public static int getMaxHeight(List<Vector> vectors){
        int maxHeight = Integer.MIN_VALUE;
        for(Vector vector : vectors)
            maxHeight = Math.max(maxHeight, vector.getBlockY());

        return maxHeight;
    }


    /** Creates a Convex WorldEdit selection from a list of points and adds it to the list of commands to execute.
     *
     * @param commands - The list of commands to add the selection to
     * @param points - The list of points to create the selection from
     */
    public static void createConvexSelection(List<String> commands, List<Vector> points){
        commands.add("//sel convex");
        commands.add("//pos1 " + getXYZ(points.get(0)));

        for(int i = 1; i < points.size(); i++)
            commands.add("//pos2 " + getXYZ(points.get(i)));
    }

    /**
     * Creates a Poly WorldEdit selection from a list of points and adds it to the list of commands to execute.
     * This functions determines the surface height of each location later once it's processed by the command queue.
     *
     * @param commands - The list of commands to add the selection to
     * @param points - The list of points to create the selection from
     */
    public static void createPolySelection(List<String> commands, List<Vector> points){
        commands.add("//sel poly");
        commands.add("//pos1 " + getXYZ(points.get(0)));

        for(int i = 1; i < points.size(); i++)
            commands.add("//pos2 " + getXYZ(points.get(i)));
    }

    /**
     * Creates a Poly WorldEdit selection from a list of points and adds it to the list of commands to execute.
     * This functions determines the surface height of each location directly.
     *
     * @param p - The player to create the selection for
     * @param points - The list of points to create the selection from
     * @param blocks - The blocks to get the surface height from
     */
    public static void createPolySelection(Player p, List<Vector> points, Block[][][] blocks){
        p.chat("//sel poly");
        p.chat("//pos1 " + getXYZ(points.get(0), blocks));

        for(int i = 1; i < points.size(); i++)
            p.chat("//pos2 " + getXYZ(points.get(i), blocks));
    }

    /**
     * Draws a curved poly line from a list of points and adds it to the list of commands to execute.
     * It draws the curve by drawing a straight line between each of the points.
     *
     * @param commands - The list of commands to add the selection to
     * @param points - The list of points to create the selection from
     * @param lineMaterial - The material to draw the line with
     * @param connectLineEnds - Whether to connect the line ends in case the line is a circle
     * @param blocks - The blocks to get the surface height from
     * @return
     */
    public static int createPolyLine(List<String> commands, List<Vector> points, String lineMaterial, boolean connectLineEnds, Block[][][] blocks){
        commands.add("//sel cuboid");
        commands.add("//pos1 " + getXYZ(points.get(0)));
        int operations = 0;

        List<String> positions = new ArrayList<>();
        for(int i = 1; i < points.size(); i++)
            positions.add(getXYZ(points.get(i), blocks));
        String pos2 = getXYZ(points.get(0), blocks);

        for(int i = 1; i < points.size(); i++){
            commands.add("//pos2 " + positions.get(i-1));
            commands.add("//line " + lineMaterial);
            operations++;
            commands.add("//pos1 " + positions.get(i-1));
        }

        if(connectLineEnds){
            commands.add("//pos2 " + pos2);
            commands.add("//line " + lineMaterial);
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
     * Checks if WorldEdit is installed and sends the player a message if it isn't.
     *
     * @param p - The player to check for
     * @return Whether WorldEdit is installed
     */
    public static boolean checkIfWorldEditIsInstalled(Player p){
        // Check if WorldEdit is enabled
        if(!BuildTeamTools.DependencyManager.isWorldEditEnabled()){
            p.sendMessage("§cPlease install WorldEdit to use this tool. You can ask the server administrator to install it.");
            p.sendMessage(" ");
            p.sendMessage("§cFor more installation help, please see the wiki:");
            p.sendMessage("§c" + INSTALL_WIKI);
            sendMoreInfo(p);
            return false;
        }
        return true;
    }

    /**
     * Checks if Schematic Brush is installed and sends the player a message if it isn't.
     *
     * @param p - The player to check for
     * @return Whether Schematic Brush is installed
     */
    public static boolean checkIfSchematicBrushIsInstalled(Player p){
        // Check if WorldEdit is enabled
        if(!BuildTeamTools.DependencyManager.isSchematicBrushEnabled()){
            p.sendMessage("§cPlease install Schematic Brush to use this tool. You can ask the server administrator to install it.");
            p.sendMessage(" ");
            p.sendMessage("§cFor more installation help, please see the wiki:");
            p.sendMessage("§c" + INSTALL_WIKI);
            return false;
        }
        return true;
    }

    /**
     * Checks if the TreePack is installed and sends the player a message if it isn't.
     *
     * @param p - The player to check for
     * @return Whether the TreePack is installed
     */
    public static boolean checkIfTreePackIsInstalled(Player p, boolean sendError){
        // Load the schematic file
        try {
            String filepath = "newtrees/oak41.schematic";
            File myfile = new File(Main.instance.getDataFolder().getAbsolutePath() + "/../WorldEdit/schematics/" + filepath);

            if(!myfile.exists()) {
                if(sendError)
                    sendTreePackError(p);
                return false;
            }

            ClipboardFormat format = ClipboardFormat.findByFile(myfile);
            ClipboardReader reader = format.getReader(new FileInputStream(myfile));
            BukkitWorld bukkitWorld = new BukkitWorld(p.getWorld());
            Clipboard clipboard = reader.read(bukkitWorld.getWorldData());

            if(clipboard == null) {
                if(sendError)
                    sendTreePackError(p);

                return false;
            }else
                return true;

        } catch (Exception e) {
            if(sendError)
                sendTreePackError(p);

            return false;
        }
    }

    /**
     * Sends the player a message with more information about the tree pack in case it isn't installed.
     *
     * @see #checkIfTreePackIsInstalled(Player, boolean)
     *
     * @param p - The player to send the message to
     */
    public static void sendTreePackError(Player p){
        p.sendMessage("§cPlease install the Tree Pack " + Tree.TREE_PACK_VERSION +" to use this tool. You can ask the server administrator to install it.");
        p.sendMessage(" ");
        p.sendMessage("§cFor more installation help, please see the wiki:");
        p.sendMessage("§c" + INSTALL_WIKI);
    }


    /**
     * Checks if the player has a WorldEdit selection and sends them a message if they don't.
     *
     * @param p - The player to check for
     * @return Whether the player has a WorldEdit selection
     */
    public static boolean checkForWorldEditSelection(Player p){
        // Get WorldEdit selection of player
        Region polyRegion = Generator.getWorldEditSelection(p);

        if(polyRegion == null){
            p.sendMessage("§cPlease make a WorldEdit Selection first.");
            p.closeInventory();
            p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            sendMoreInfo(p);
            return false;
        }

        return true;
    }

    /**
     * Checks if the player has a WorldEdit 2D Polygonal selection and sends them a message if they don't.
     *
     * @param p - The player to check for
     * @return Whether the player has a WorldEdit Poly selection
     */
    public static boolean checkForPolySelection(Player p){

        Region polyRegion = Generator.getWorldEditSelection(p);

        if(!(polyRegion instanceof Polygonal2DRegion)){
            p.sendMessage("§cPlease make a WorldEdit 2D Polygonal Selection first (//sel poly).");
            p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            sendMoreInfo(p);
            return false;
        }
        return true;
    }

    /**
     * Checks if the player has a WorldEdit Convex selection and sends them a message if they don't.
     *
     * @param p - The player to check for
     * @return Whether the player has a WorldEdit Convex selection
     */
    public static boolean checkForConvexSelection(Player p){
        // Get WorldEdit selection of player
        Region polyRegion = Generator.getWorldEditSelection(p);

        if(!(polyRegion instanceof ConvexPolyhedralRegion)){
            p.sendMessage("§cPlease make a WorldEdit Convex Selection first (//sel convex).");
            p.closeInventory();
            p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            sendMoreInfo(p);
            return false;
        }

        return true;
    }

    /**
     * Checks if the player has a brick block in their selection and sends them a message if they don't.
     *
     * @param p - The player to check for
     * @return Whether the player has a brick block in their selection
     */
    public static boolean checkForBrickOutline(Block[][][] blocks, Player p){
        if(!containsBlock(blocks, Material.BRICK, (byte) 0)){
            p.sendMessage("§cPlease make a selection around an outline.");
            p.closeInventory();
            p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            sendMoreInfo(p);

            return false;
        }

        return true;
    }

    /**
     * Checks if the player has a yellow wool block in their selection and sends them a message if they don't.
     *
     * @param p - The player to check for
     * @return Whether the player has a yellow wool block in their selection
     */
    public static boolean checkForWoolBlock(Block[][][] blocks, Player p){
        if(!containsBlock(blocks, Material.WOOL, (byte) 4)){
            p.sendMessage("§cPlease place a yellow wool block inside the outline.");
            p.closeInventory();
            p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            sendMoreInfo(p);

            ItemStack yellowWool = Item.create(Material.WOOL, null, (short) 4, null);
            if(!p.getInventory().contains(yellowWool)) {
                p.getInventory().setItem(4, yellowWool);
                p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
            }

            return false;
        }
        return true;
    }
}
package net.buildtheearth.modules.generator.utils;

import clipper2.Clipper;
import clipper2.core.Path64;
import clipper2.core.Paths64;
import clipper2.core.Point64;
import clipper2.offset.EndType;
import clipper2.offset.JoinType;
import com.cryptomorin.xseries.XMaterial;
import com.fastasyncworldedit.core.limit.FaweLimit;
import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.factory.MaskFactory;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.inventory.BlockBag;
import com.sk89q.worldedit.function.mask.BlockMask;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.ClipboardPattern;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.regions.*;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import com.sk89q.worldedit.regions.selector.Polygonal2DRegionSelector;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.utils.ChatHelper;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;


/** This class contains static utility methods for the generator module.<br>
 * The following dependencies are needed for this class to work:
 * <ol>
 *     <li><b>WorldEdit</b></li>
 *     <li><b>FastAsyncWorldEdit</b></li>
 *     <li><b>XSeries</b></li>
 *     <li><b>Clipper</b></li>
 *     <li><b>ChatHelper (internal)</b></li>
 * </ol>
 * <br>
 * The functions are categorized into the following sections:
 *
 * <br><br><b>Setup Functions</b>:
 * These functions are plugin specific functions that need to be changed when copying the class to another plugin.
 *
 * <br><br><b>Script Helper Functions</b>:
 * <br>• {@link #convertArgsToFlags(String[])}
 * <br>• {@link #convertToFlagAndValue(String, Player)}
 * <br>• {@link #getBlockState(XMaterial)}
 *
 * <br><br><b>WorldEdit Helper Functions</b>:
 * <br>• {@link #getWorldEditSchematicsFolderPath()}
 *
 * <br><br><b>WorldEdit Region Functions</b>:
 * <br>• {@link #getSelectionPointsFromRegion(Region)}
 * <br>• {@link #getMinMaxPoints(Region)}
 * <br>• {@link #getMaxHeight(Block[][][], Material...)}
 * <br>• {@link #getMaxHeight(Block[][][], int, int, Material...)}
 * <br>• {@link #containsBlock(Block[][][], XMaterial)}
 * <br>• {@link #containsBlock(Block[][][], XMaterial, int)}
 * <br>• {@link #getWorldEditSelection(Player)}
 * <br>• {@link #restoreSelection(Player, RegionSelector)}
 * <br>• {@link #createCuboidSelection(Player, Vector, Vector)}
 * <br>• {@link #createPolySelection(Player, List, Block[][][])}
 * <br>• {@link #createPolySelection(Player, List, int, int)}
 * <br>• {@link #changeRegionWorld(Region, com.sk89q.worldedit.world.World)} 
 *
 * <br><br><b>WorldEdit Operation Functions</b>:
 * <br>• {@link #prepareScriptSession(LocalSession, Actor, Player, com.sk89q.worldedit.world.World, int, boolean, boolean, boolean)}
 * <br>• {@link #analyzeRegion(Player, World)}
 * <br>• {@link #copyRegion(LocalSession, Actor, Region, World, World)}
 * <br>• {@link #replaceBlocksWithMasks(LocalSession, Actor, com.sk89q.worldedit.world.World, List, BlockState, BlockState[], int)}
 * <br>• {@link #replaceBlocksWithSchematic(LocalSession, Actor, com.sk89q.worldedit.world.World, BlockState[], String)}
 * <br>• {@link #replaceBlocks(LocalSession, Actor, com.sk89q.worldedit.world.World, BlockState[], BlockState[])}
 * <br>• {@link #drawSplineWithMasks(LocalSession, Actor, com.sk89q.worldedit.world.World, Block[][][], List, List, BlockState[], boolean, double, double, double, double, double, boolean, boolean)}
 * <br>• {@link #drawCurveWithMasks(LocalSession, Actor, com.sk89q.worldedit.world.World, Block[][][], List, List, BlockState[], boolean)}
 * <br>• {@link #drawPolyLineWithMasks(LocalSession, Actor, com.sk89q.worldedit.world.World, Block[][][], List, List, BlockState[], boolean, boolean)}
 * <br>• {@link #drawLineWithMasks(LocalSession, Actor, com.sk89q.worldedit.world.World, Block[][][], List, Vector, Vector, BlockState[], boolean)}
 * <br>• {@link #pasteSchematicWithMasks(LocalSession, Actor, com.sk89q.worldedit.world.World, Block[][][], List, String, Location, double)}
 * <br>• {@link #pasteSchematic(LocalSession, Actor, com.sk89q.worldedit.world.World, Block[][][], String, Location, double)}
 * <br>• {@link #expandSelection(LocalSession, Vector)}
 * <br>• {@link #clearHistory(LocalSession)}
 * <br>• {@link #undo(LocalSession, Player, Actor, int)}
 * <br>• {@link #redo(LocalSession, Player, Actor, int)}
 * <br>• {@link #setGmask(LocalSession, String)}
 * <br>• {@link #saveEditSession(EditSession, LocalSession, Actor)}
 *
 * <br><br><b>Vector Helper Functions</b>:
 * <br>• {@link #adjustHeight(List, Block[][][])}
 * <br>• {@link #populatePoints(List, int)}
 * <br>• {@link #reducePoints(List, int, int)}
 * <br>• {@link #extendPolyLine(List)}
 * <br>• {@link #shortenPolyLine(List, int)}
 * <br>• {@link #getClosestVector(List, Vector)}
 * <br>• {@link #getXYZ(Vector, Block[][][])}
 * <br>• {@link #getXYZWithVerticalOffset(Vector, Block[][][], int)}
 * <br>• {@link #convertVectorListToPath64(List, Vector)}
 * <br>• {@link #convertPathsToVectorList(Paths64, Vector, int, int)}
 * <br>• {@link #shiftPoints(List, double, boolean)}
 * <br>• {@link #shiftPointsAll(List, double)}
 * <br>• {@link #getMinHeight(List)}
 * <br>• {@link #getMaxHeight(List)}
 *
 * <br><br><b>Preparation Check Functions</b>:
 * <br>• {@link #checkIfSchematicBrushIsInstalled(Player)}
 * <br>• {@link #checkForNoWorldEditSelection(Player)}
 * <br>• {@link #checkForBrickOutline(Block[][][], Player)}
 * <br>• {@link #checkForWoolBlock(Block[][][], Player)}
 *
 * @version 1.7.3
 * @author MineFact
 */
@SuppressWarnings("unused")
public class GeneratorUtils {


    /*=============================================**

                SETUP FUNCTIONS

     **=============================================*/

    /** Get the plugin instance. */
    private static Plugin getPlugin(){
        return BuildTeamTools.getInstance();
    }

    /** Checks if the plugin "FastAsyncWorldEdit" is enabled. */
    private static boolean isFastAsyncWorldEditEnabled(){
        return getPlugin().getServer().getPluginManager().isPluginEnabled("FastAsyncWorldEdit");
    }

    private static boolean isLegacyWorldEditEnabled() {
        if (isFastAsyncWorldEditEnabled())
            return false;

        PluginManager pluginManager = getPlugin().getServer().getPluginManager();
        if (!pluginManager.isPluginEnabled("WorldEdit"))
            return false;

        Plugin plugin = pluginManager.getPlugin("WorldEdit");
        if (plugin == null)
            return false;

        @SuppressWarnings("deprecation")
        PluginDescriptionFile description = plugin.getDescription();
        String version = description.getVersion();
        int major;
        try {
            major = Integer.parseInt(version.split("\\.")[0]);
        } catch (NumberFormatException e) {
            // Fallback or assume not legacy if version can't be parsed
            return false;
        }

        return major < 7;
    }


    /** Checks if the plugin "SchematicBrush" is enabled. */
    private static boolean isSchematicBrushEnabled(){
        return getPlugin().getServer().getPluginManager().isPluginEnabled("SchematicBrush");
    }        // e.g., "1.21.4"


    /** Checks if the server version is 1.12. */
    private static boolean isServerVersion1_12(){
        String rawVersion = Bukkit.getBukkitVersion().split("-")[0];
        return rawVersion.equals("1.12.2") || rawVersion.equals("1.12.1") || rawVersion.equals("1.12");
    }

    /** Get the ignored materials. */
    private static Material[] getIgnoredMaterials(){
        return new Material[]{};
    }
    
    private static void sendWikiLink(Player p){
        p.sendMessage("§cFor more information, see the Wiki.");
    }




    
    /*=============================================**
    
                SCRIPT HELPER FUNCTIONS
    
     **=============================================*/

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

    /**
     * Returns the blockState of a given XMaterial.
     *
     * @param xMaterial The XMaterial to get the blockState from
     * @return The blockState of the XMaterial
     */
    public static BlockState getBlockState(XMaterial xMaterial){
        if(xMaterial == null)
            return null;

        BlockType blockType = BlockTypes.get(xMaterial.getId() + "");
        ItemStack item = xMaterial.parseItem();
        if(blockType == null && item != null)
            blockType = BlockTypes.get(item.getType().getKey().asString());

        if(blockType == null && item != null)
            throw new IllegalArgumentException("Invalid block type: " + item.getType().name());
        else if(blockType == null)
            throw new IllegalArgumentException("Invalid block type: " + xMaterial.name());

        return blockType.getDefaultState();
    }
    public static BlockState[] getBlockState(XMaterial[] xMaterial){
        if(xMaterial == null)
            return null;

        BlockState[] blockStates = new BlockState[xMaterial.length];

        for(int i = 0; i < xMaterial.length; i++)
            blockStates[i] = getBlockState(xMaterial[i]);

        return blockStates;
    }
    


    /*=============================================**

                WORLDEDIT HELPER FUNCTIONS

     **=============================================*/


    /**
     * Returns the path to the WorldEdit schematics folder.
     *
     * @return The path to the WorldEdit schematics folder
     */
    public static String getWorldEditSchematicsFolderPath(){
        String worldEditFolder = "WorldEdit";

        if(isFastAsyncWorldEditEnabled())
            worldEditFolder = "FastAsyncWorldEdit";

        return getPlugin().getDataFolder().getAbsolutePath() + "/../" + worldEditFolder + "/schematics";
    }


    
    
    
    /*=============================================**
    
               WORLDEDIT REGION FUNCTIONS
    
     **=============================================*/
    
    
    
    /**
     * Returns the WorldEdit selection Vector from a player no matter which type of selection the player made.
     *
     * @param region The WorldEdit region to get the selection from
     * @return A list of vectors representing the selection
     */
    public static List<Vector> getSelectionPointsFromRegion(Region region) {
        List<Vector> points = new ArrayList<>();

        switch (region) {
            case Polygonal2DRegion polyRegion -> {
                ChatHelper.logDebug("Polygonal2DRegion found");
                // In latest FAWE, the points are stored as BlockVector2
                // In 1.12 WorldEdit, the points are stored as BlockVector2D
                // Both classes have the same methods, so we can use reflection to get the methods
                for (Object blockVectorObj : polyRegion.getPoints()) {
                    ChatHelper.logDebug("BlockVector2:" + blockVectorObj);
                    try {
                        Class<?> blockVectorClass = blockVectorObj.getClass();
                        Method getXMethod = blockVectorClass.getMethod("getBlockX");
                        Method getZMethod = blockVectorClass.getMethod("getBlockZ");

                        int x = (Integer) getXMethod.invoke(blockVectorObj);
                        int z = (Integer) getZMethod.invoke(blockVectorObj);

                        points.add(new Vector(x, 0, z));
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        ChatHelper.logDebug("Exception while processing region: " + e.getMessage());
                    }
                }
            }
            case ConvexPolyhedralRegion convexRegion -> {
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
                        ChatHelper.logDebug("Exception while processing region: " + e.getMessage());
                    }
                }
            }
            case CuboidRegion cuboidRegion -> {
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
                    ChatHelper.logDebug("Exception while processing region: " + e.getMessage());
                }
            }
            case null, default -> {
                return null;
            }
        }

        return points;
    }

    /**
     * Returns the minimum and maximum points of a region as a Vector array.
     * 
     * @param region The region to get the minimum and maximum points from
     * @return A Vector array with the minimum vector at index 0 and the maximum vector at index 1             
     */
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
            ChatHelper.logDebug("Exception while processing MinMax Points: " + e.getMessage());
        }

        return minMax;
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
                for (Block block : block1D) {
                    ItemStack item = xMaterial.parseItem();
                    if (block != null && item != null && block.getType() == item.getType())
                            amountFound++;
                }

        return amountFound >= requiredAmount;
    }

    /**
     * Returns the current WorldEdit selection of a player.
     *
     * @param p The player whose selection should be returned.
     * @return The current WorldEdit selection of the player.
     */
    public static Region getWorldEditSelection(Player p){
        try {
            Actor actor = BukkitAdapter.adapt(p);
            SessionManager sessionManager = WorldEdit.getInstance().getSessionManager();
            com.sk89q.worldedit.world.World world = sessionManager.get(actor).getSelectionWorld();

            return sessionManager.get(actor).getSelection(world);
        } catch (NullPointerException | IncompleteRegionException ex) {
            return null;
        }
    }

    /**
     * Returns the WorldEdit selection of a player.
     *
     * @param p The player to get the selection from
     * @param regionSelector The region selector to get the selection from
     */
    public static void restoreSelection(Player p, RegionSelector regionSelector){
        Actor actor = BukkitAdapter.adapt(p);
        SessionManager sessionManager = WorldEdit.getInstance().getSessionManager();
        com.sk89q.worldedit.world.World world = sessionManager.get(actor).getSelectionWorld();

        sessionManager.get(actor).setRegionSelector(world, regionSelector);

        ChatHelper.logDebug("Restored selection");
    }

    /**
     * Creates a Cuboid WorldEdit selection from a list of points and execute it right away.
     *
     * @param p The player to create the selection for
     * @param vector1 Position 1
     * @param vector2 Position 2
     */
    public static void createCuboidSelection(Player p, Vector vector1, Vector vector2){
        Actor actor = BukkitAdapter.adapt(p);
        SessionManager sessionManager = WorldEdit.getInstance().getSessionManager();
        com.sk89q.worldedit.world.World world = sessionManager.get(actor).getSelectionWorld();

        sessionManager.get(actor).setRegionSelector(world,
                new CuboidRegionSelector(world,
                        BlockVector3.at(vector1.getBlockX(), vector1.getBlockY(), vector1.getBlockZ()),
                        BlockVector3.at(vector2.getBlockX(), vector2.getBlockY(), vector2.getBlockZ())
                )
        );

        ChatHelper.logDebug("Created cuboid selection");
    }


    /**
     * Creates a Polygon WorldEdit selection from a list of points and execute it right away.
     * This functions determines the current surface height of each vector directly.
     *
     * @param p The player to create the selection for
     * @param points The list of points to create the selection from
     */
    public static void createPolySelection(Player p, List<Vector> points, Block[][][] blocks){
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        for(Vector vector : points) {
            int y = getMaxHeight(blocks, vector.getBlockX(), vector.getBlockZ(), getIgnoredMaterials());

            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
        }

        createPolySelection(p, points, minY, maxY);
    }

    /**
     * Creates a Polygon WorldEdit selection from a list of points and execute it right away.
     * This functions determines the current surface height of each vector directly.
     *
     * @param p The player to create the selection for
     * @param points The list of points to create the selection from
     * @param minY The minimum Y value of the selection
     * @param maxY The maximum Y value of the selection
     */
    public static void createPolySelection(Player p, List<Vector> points, int minY, int maxY) {
        Actor actor = BukkitAdapter.adapt(p);
        SessionManager sessionManager = WorldEdit.getInstance().getSessionManager();
        com.sk89q.worldedit.world.World world = sessionManager.get(actor).getSelectionWorld();

        List<BlockVector2> blockVector2List = new ArrayList<>();
        for (Vector vector : points){
            blockVector2List.add(BlockVector2.at(vector.getBlockX(), vector.getBlockZ()));
            ChatHelper.logDebug("Added point: " + vector);
        }

        sessionManager.get(actor).setRegionSelector(world,
                new Polygonal2DRegionSelector(world, blockVector2List, minY, maxY)
        );

        ChatHelper.logDebug("Created polygonal selection with " + points.size() + " points. minY: " + minY + " maxY: " + maxY);
    }

    /**
     * Rebuilds a region with a different world while keeping its coordinates.
     * Currently, supports {@link Polygonal2DRegion} and {@link CuboidRegion}.
     *
     * @param region The region to rebuild
     * @param world  The world that should be used for the new region
     * @return A new region with the supplied world or {@code null} if the type is unsupported
     */
    public static Region changeRegionWorld(Region region, com.sk89q.worldedit.world.World world) {
        if (region instanceof Polygonal2DRegion) {
            List<Vector> points = getSelectionPointsFromRegion(region);
            if (points == null)
                return null;

            List<BlockVector2> blockPoints = new ArrayList<>();
            for (Vector v : points)
                blockPoints.add(BlockVector2.at(v.getBlockX(), v.getBlockZ()));

            Vector[] minMax = getMinMaxPoints(region);
            int minY = minMax[0].getBlockY();
            int maxY = minMax[1].getBlockY();

            return new Polygonal2DRegion(world, blockPoints, minY, maxY);
        } else if (region instanceof CuboidRegion) {
            Vector[] minMax = getMinMaxPoints(region);
            BlockVector3 min = BlockVector3.at(minMax[0].getBlockX(), minMax[0].getBlockY(), minMax[0].getBlockZ());
            BlockVector3 max = BlockVector3.at(minMax[1].getBlockX(), minMax[1].getBlockY(), minMax[1].getBlockZ());
            return new CuboidRegion(world, min, max);
        }

        return null;
    }

    
    
    
    
    
    
    /*=============================================**
    
                WORLDEDIT OPERATION FUNCTIONS
    
     **=============================================*/
    
    

    /**
     * Prepares a script session by expanding the selection, removing non-solid blocks and ignored materials.
     * The blocks of the region are also analyzed and returned.
     *
     * @param localSession The local session of the actor
     * @param actor The actor who should perform the operation
     * @param player The player who should receive an error message if the flag value is invalid
     * @param world The world in which the region is located
     * @param expandSelection The amount of blocks to expand the selection
     * @param readBlocks Whether the blocks of the region should be read and returned
     * @param removeNonSolidBlocks Whether non-solid blocks should be removed
     * @param removeIgnoredMaterials Whether ignored materials should be removed
     * @return A three-dimensional array of all blocks in the region
     */
    public static Block[][][] prepareScriptSession(LocalSession localSession, Actor actor, Player player, com.sk89q.worldedit.world.World world, int expandSelection, boolean readBlocks, boolean removeNonSolidBlocks, boolean removeIgnoredMaterials){
        if(expandSelection > 0) {
            expandSelection(localSession, new Vector(0, expandSelection, 0));
            expandSelection(localSession, new Vector(0, -expandSelection, 0));
        }

        BlockType air = BlockTypes.AIR;

        if(air == null)
            return null;

        if(removeNonSolidBlocks)
            replaceBlocksWithMasks(localSession, actor, world, Collections.singletonList("!#solid"), null, new BlockState[]{air.getDefaultState()}, 1)
                .join();

        if(removeIgnoredMaterials) {
            Material[] materials = getIgnoredMaterials();
            for (Material material : materials) {
                BlockType blockType = BlockTypes.get(material.getKey().asString());

                if (blockType == null)
                    continue;

                List<BlockState> blockStates = blockType.getAllStates();
                BlockState[] blockStatesArray = blockStates.toArray(new BlockState[0]);
                replaceBlocks(localSession, actor, world, blockStatesArray, new BlockState[]{air.getDefaultState()})
                    .join();
            }
        }

        Block[][][] regionBlocks = null;

        if(readBlocks)
            regionBlocks = analyzeRegion(player, player.getWorld());

        return regionBlocks;
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

            if(isLegacyWorldEditEnabled())
                contains = regionClass.getMethod("contains", Class.forName("com.sk89q.worldedit.Vector"));
            else
                contains = regionClass.getMethod("contains", BlockVector3.class);

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
            ChatHelper.logDebug("Exception while analyzing region: " + e.getMessage());
            return null;
        }
    }

    /**
     * Copies all blocks inside a region from one world to another to the same coordinates.
     *
     * @param region     The region to copy.
     * @param fromWorld  The source world.
     * @param toWorld    The destination world.
     */
    public static CompletableFuture<Void> copyRegion(LocalSession localSession, Actor actor,
                                                     Region region, World fromWorld,
                                                     World toWorld) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            com.sk89q.worldedit.world.World weFrom = BukkitAdapter.adapt(fromWorld);
            com.sk89q.worldedit.world.World weTo = BukkitAdapter.adapt(toWorld);
            Region copyRegion = region;

            try (EditSession sourceEditSession = WorldEdit.getInstance().newEditSession(weFrom);
                 EditSession targetEditSession = WorldEdit.getInstance().newEditSession(weTo)) {

                if(copyRegion.getWorld() != null && !copyRegion.getWorld().getName().equals(fromWorld.getName()))
                    copyRegion = changeRegionWorld(region, weFrom);

                if(copyRegion == null) {
                    ChatHelper.logError("Failed to copy region from " + fromWorld.getName() + " to " + toWorld.getName() + " because region is null.");
                    return;
                }

                BlockArrayClipboard clipboard = new BlockArrayClipboard(copyRegion);
                ForwardExtentCopy forwardCopy = new ForwardExtentCopy(sourceEditSession, copyRegion,
                        clipboard, region.getMinimumPoint());
                forwardCopy.setCopyingEntities(true);
                forwardCopy.setCopyingBiomes(true);
                Operations.complete(forwardCopy);

                ChatHelper.log("Pasting at: " + region.getMinimumPoint() + " in world " + toWorld.getName());
                ChatHelper.log("Clipboard size: " + clipboard.getRegion().getVolume());

                Operation paste = new ClipboardHolder(clipboard)
                        .createPaste(targetEditSession)
                        .to(region.getMinimumPoint())
                        .copyEntities(true)
                        .copyBiomes(true)
                        .ignoreAirBlocks(false)
                        .build();
                Operations.complete(paste);
                targetEditSession.flushQueue();

                saveEditSession(targetEditSession, localSession, actor);
            } catch (WorldEditException e) {
                ChatHelper.logError("Failed to copy region: " + e.getMessage());
                future.completeExceptionally(e);
                return;
            }

            future.complete(null);
        });

        return future;
    }

    /**
     * Replaces all blocks in a region with the given masks and a pattern.
     *
     * @param localSession The local session of the actor
     * @param actor The actor who should perform the operation
     * @param weWorld The WorldEdit world in which the region is located
     * @param masks The masks to use
     * @param from The block to replace
     * @param to The blocks to replace with
     * @param iterations The number of iterations to perform
     *
     * @return A CompletableFuture that completes when the operation is finished
     */
    public static CompletableFuture<Void> replaceBlocksWithMasks(LocalSession localSession, Actor actor, com.sk89q.worldedit.world.World weWorld, List<String> masks, BlockState from, BlockState[] to, int iterations) {
        if(to == null || to.length == 0)
                throw new IllegalArgumentException("BlockState[] to is empty");

        CompletableFuture<Void> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            for (int i = 0; i < iterations; i++) {
                try (EditSession editSession = WorldEdit.getInstance().newEditSession(weWorld)) {

                    Region region = localSession.getSelection();

                    ParserContext parserContext = new ParserContext();
                    parserContext.setActor(actor);
                    parserContext.setWorld(weWorld);
                    parserContext.setSession(localSession);
                    parserContext.setExtent(editSession);

                    for (String maskString : masks) {
                        ChatHelper.logDebug("Replacing blocks with expression mask: " + maskString.replace("%", "'PCT'") + " from " + from + " to " + Arrays.toString(to) + " for " + iterations + " iterations");

                        Mask mask = new MaskFactory(WorldEdit.getInstance()).parseFromInput(maskString, parserContext);

                        if (from != null) {
                            BlockMask blockMask = new BlockMask(weWorld, from.toBaseBlock());
                            editSession.setMask(blockMask);
                        }

                        Pattern pattern;

                        if (to.length == 1)
                            pattern = to[0];
                        else {
                            RandomPattern randomPattern = new RandomPattern();
                            double chance = 100.0 / to.length;

                            for (BlockState blockState : to)
                                randomPattern.add(blockState, chance);

                            pattern = randomPattern;
                        }


                        editSession.replaceBlocks(region, mask, pattern);

                        saveEditSession(editSession, localSession, actor);
                    }
                } catch (IncompleteRegionException | MaxChangedBlocksException | InputParseException e) {
                    future.completeExceptionally(new RuntimeException(e));
                }
            }

            future.complete(null);
        });

        return future;
    }


    /**
     * Replaces all blocks in a region with a schematic pattern.
     *
     * @param weWorld The WorldEdit world in which the region is located
     * @param localSession The local session of the actor
     * @param from The block to replace
     * @param schematicPath The path of the schematic to replace with
     *
     * @return A CompletableFuture that completes when the operation is finished
     */
    public static CompletableFuture<Void> replaceBlocksWithSchematic(LocalSession localSession, Actor actor, com.sk89q.worldedit.world.World weWorld, BlockState[] from, String schematicPath) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            ChatHelper.logDebug("Replacing blocks from " + Arrays.toString(from) + " to " + schematicPath);

            try (EditSession editSession = WorldEdit.getInstance().newEditSession(weWorld)) {
                Region region = localSession.getSelection();

                File schematicFile = new File(GeneratorUtils.getWorldEditSchematicsFolderPath() + schematicPath);

                ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);
                ClipboardReader reader;

                if (format == null)
                    return;

                try {
                    reader = format.getReader(Files.newInputStream(schematicFile.toPath()));
                    Clipboard clipboard = reader.read();

                    Pattern pattern = new ClipboardPattern(clipboard);

                    if(from != null) {
                        BlockMask blockMask = new BlockMask(weWorld).add(from);
                        editSession.replaceBlocks(region, blockMask, pattern);
                    }else
                        editSession.setBlocks(region, pattern);
                } catch (IOException e) {
                    ChatHelper.logDebug("Exception while replacing blocks with schematic: " + e.getMessage());
                    return;
                }

                saveEditSession(editSession, localSession, actor);
            } catch (IncompleteRegionException | MaxChangedBlocksException e) {
                future.completeExceptionally(new RuntimeException(e));
            }

            future.complete(null);
        });

        return future;
    }


    /**
     * Replaces all blocks in a region with a given block.
     *
     * @param weWorld The WorldEdit world in which the region is located
     * @param localSession The local session of the actor
     * @param from The block to replace
     * @param to The block to replace with
     *
     * @return A CompletableFuture that completes when the operation is finished
     */
    public static CompletableFuture<Void> replaceBlocks(LocalSession localSession, Actor actor, com.sk89q.worldedit.world.World weWorld, BlockState[] from, BlockState[] to) {
        if(to.length == 0)
            throw new IllegalArgumentException("BlockState[] to is empty");

        CompletableFuture<Void> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            ChatHelper.logDebug("Replacing blocks from " + Arrays.toString(from) + " to " + Arrays.toString(to));

            try (EditSession editSession = WorldEdit.getInstance().newEditSession(weWorld)) {
                Region region = localSession.getSelection();
                Pattern pattern;

                if(to.length == 1)
                    pattern = to[0];
                else{
                    RandomPattern randomPattern = new RandomPattern();
                    double chance = 100.0 / to.length;

                    for(BlockState blockState : to)
                        randomPattern.add(blockState, chance);

                    pattern = randomPattern;
                }

                if(from != null) {
                    BlockMask blockMask = new BlockMask(weWorld).add(from);
                    editSession.replaceBlocks(region, blockMask, pattern);
                }else
                    editSession.setBlocks(region, pattern);


                saveEditSession(editSession, localSession, actor);
            } catch (IncompleteRegionException | MaxChangedBlocksException e) {
                future.completeExceptionally(new RuntimeException(e));
            }

            future.complete(null);
        });
        return future;
    }


    /**
     * Draws a spline in a region with the given masks and a pattern.
     *
     * @param localSession The local session of the actor
     * @param actor The actor who should perform the operation
     * @param weWorld The WorldEdit world in which the region is located
     * @param regionBlocks The blocks of the region
     * @param masks The masks to use. If no mask should be used, pass an empty list.
     * @param points The points of the curve
     * @param blocks The blocks to use for the curve
     * @param tension The tension of the curve - <a href="https://en.wikipedia.org/wiki/Kochanek%E2%80%93Bartels_spline">Kochanek–Bartels Spline</a>
     * @param bias The bias of the curve - <a href="https://en.wikipedia.org/wiki/Kochanek%E2%80%93Bartels_spline">Kochanek–Bartels Spline</a>
     * @param continuity The continuity of the curve - <a href="https://en.wikipedia.org/wiki/Kochanek%E2%80%93Bartels_spline">Kochanek–Bartels Spline</a>
     * @param quality The quality of the curve. The higher the quality, the more points the curve will have.
     * @param radius The radius of the curve. The higher the radius the thicker the curve will be.
     * @param filled Whether the curve should be filled or not.
     * @param matchElevation Whether the elevation of the points should be matched to the region
     */
    public static CompletableFuture<Void> drawSplineWithMasks(LocalSession localSession, Actor actor, com.sk89q.worldedit.world.World weWorld, Block[][][] regionBlocks, List<String> masks, List<Vector> points, BlockState[] blocks,
                                                              boolean matchElevation, double tension, double bias, double continuity, double quality, double radius, boolean filled, boolean connectLineEnds) {
        if(blocks == null || blocks.length == 0)
            throw new IllegalArgumentException("BlockState[] to is empty");

        // If no mask is provided, add an empty mask
        if(masks.isEmpty()) {
            masks = new ArrayList<>();
            masks.add("");
        }

        List<String> finalMasks = masks;
        CompletableFuture<Void> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {

            try (EditSession editSession = WorldEdit.getInstance().newEditSession(weWorld)) {

                ParserContext parserContext = new ParserContext();
                parserContext.setActor(actor);
                parserContext.setWorld(weWorld);
                parserContext.setSession(localSession);
                parserContext.setExtent(editSession);

                for (String maskString : finalMasks) {
                    ChatHelper.logDebug("Drawing spline with expression mask: " + maskString.replace("%", "'PCT'") + " with " + Arrays.toString(blocks));

                    // Set the mask
                    if (!maskString.isEmpty()) {
                        Mask mask = new MaskFactory(WorldEdit.getInstance()).parseFromInput(maskString, parserContext);
                        editSession.setMask(mask);
                    }

                    // Set the pattern
                    Pattern pattern;

                    if (blocks.length == 1)
                        pattern = blocks[0];
                    else {
                        RandomPattern randomPattern = new RandomPattern();
                        double chance = 100.0 / blocks.length;

                        for (BlockState blockState : blocks)
                            randomPattern.add(blockState, chance);

                        pattern = randomPattern;
                    }

                    // Set the block vectors
                    List<BlockVector3> blockVector3s = new ArrayList<>();
                    for (Vector point : points) {
                        if (matchElevation)
                            point = getXYZ(point, regionBlocks);

                        blockVector3s.add(BlockVector3.at(point.getBlockX(), point.getBlockY(), point.getBlockZ()));
                    }

                    editSession.drawSpline(pattern, blockVector3s, tension, bias, continuity, quality, radius, filled);

                    if (connectLineEnds && blockVector3s.size() > 1)
                        editSession.drawLine(pattern, blockVector3s.getFirst(), blockVector3s.getLast(), radius, true);

                    saveEditSession(editSession, localSession, actor);
                }
            } catch (IncompleteRegionException | MaxChangedBlocksException | InputParseException e) {
                throw new RuntimeException(e);
            }

            future.complete(null);
        });

        return future;
    }

    public static CompletableFuture<Void> drawCurveWithMasks(LocalSession localSession, Actor actor, com.sk89q.worldedit.world.World weWorld, Block[][][] regionBlocks, List<String> masks, List<Vector> points, BlockState[] blocks, boolean matchElevation){
        return drawSplineWithMasks(localSession, actor, weWorld, regionBlocks, masks, points, blocks, matchElevation, 0, 0, 0, 10, 0, true, false);
    }

    public static CompletableFuture<Void> drawPolyLineWithMasks(LocalSession localSession, Actor actor, com.sk89q.worldedit.world.World weWorld, Block[][][] regionBlocks, List<String> masks, List<Vector> points, BlockState[] blocks, boolean matchElevation, boolean connectLineEnds){
        return drawSplineWithMasks(localSession, actor, weWorld, regionBlocks, masks, points, blocks, matchElevation, 1, 0, -1, 10, 0, true, connectLineEnds);
    }




    /**
     * Draws a Line in a region with the given masks and a pattern.
     *
     * @param localSession The local session of the actor
     * @param actor The actor who should perform the operation
     * @param weWorld The WorldEdit world in which the region is located
     * @param regionBlocks The blocks of the region
     * @param masks The masks to use. If no mask should be used, pass an empty list.
     * @param point1 The first point of the curve
     * @param point2 The second point of the curve
     * @param blocks The blocks to use for the curve
     * @param matchElevation Whether the elevation of the points should be matched to the region
     */
    public static CompletableFuture<Void> drawLineWithMasks(LocalSession localSession, Actor actor, com.sk89q.worldedit.world.World weWorld, Block[][][] regionBlocks, List<String> masks, Vector point1, Vector point2, BlockState[] blocks, boolean matchElevation) {
        if(blocks == null || blocks.length == 0)
            throw new IllegalArgumentException("BlockState[] to is empty");

        // If no mask is provided, add an empty mask
        if(masks.isEmpty()) {
            masks = new ArrayList<>();
            masks.add("");
        }

        CompletableFuture<Void> future = new CompletableFuture<>();
        List<String> finalMasks = masks;
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            try (EditSession editSession = WorldEdit.getInstance().newEditSession(weWorld)) {

                ParserContext parserContext = new ParserContext();
                parserContext.setActor(actor);
                parserContext.setWorld(weWorld);
                parserContext.setSession(localSession);
                parserContext.setExtent(editSession);

                for (String maskString : finalMasks) {
                    ChatHelper.logDebug("Drawing line with expression mask: " + maskString.replace("%", "'PCT'") + " with " + Arrays.toString(blocks) + " from " + point1 + " to " + point2);

                    // Set the mask
                    if(!maskString.isEmpty()) {
                        Mask mask = new MaskFactory(WorldEdit.getInstance()).parseFromInput(maskString, parserContext);
                        editSession.setMask(mask);
                    }

                    // Set the pattern
                    Pattern pattern;

                    if(blocks.length == 1)
                        pattern = blocks[0];
                    else{
                        RandomPattern randomPattern = new RandomPattern();
                        double chance = 100.0 / blocks.length;

                        for(BlockState blockState : blocks)
                            randomPattern.add(blockState, chance);

                        pattern = randomPattern;
                    }

                    Vector newPoint1 = point1;
                    Vector newPoint2 = point2;

                    // Adjust the points to the elevation of the region if matchElevation is true
                    if(matchElevation){
                        newPoint1 = getXYZ(point1, regionBlocks);
                        newPoint2 = getXYZ(point2, regionBlocks);
                    }

                    // Set the block vectors
                    BlockVector3 point1BlockVector3 = BlockVector3.at(newPoint1.getBlockX(), newPoint1.getBlockY(), newPoint1.getBlockZ());
                    BlockVector3 point2BlockVector3 = BlockVector3.at(newPoint2.getBlockX(), newPoint2.getBlockY(), newPoint2.getBlockZ());

                    editSession.drawLine(pattern, point1BlockVector3, point2BlockVector3, 0, true);

                    saveEditSession(editSession, localSession, actor);
                }
            } catch(IncompleteRegionException | MaxChangedBlocksException | InputParseException e){
                throw new RuntimeException(e);
            }

            future.complete(null);
        });

        return future;
    }


    /**
     * Pastes a schematic at a given location.
     *
     * @param localSession The local session of the actor
     * @param weWorld The WorldEdit world in which the region is located
     * @param blocks The blocks to paste
     * @param masks The masks to use
     * @param schematicPath The path to the schematic
     * @param loc The location to paste the schematic
     * @param rotation The rotation of the schematic
     */
    public static CompletableFuture<Void> pasteSchematicWithMasks(LocalSession localSession, Actor actor, com.sk89q.worldedit.world.World weWorld, Block[][][] blocks, List<String> masks, String schematicPath, Location loc, double rotation) {
        int offsetY = 1;

        // If no mask is provided, add an empty mask
        if (masks.isEmpty()) {
            masks = new ArrayList<>();
            masks.add("");
        }

        int maxHeight = loc.getBlockY();

        if(blocks != null)
            maxHeight = GeneratorUtils.getMaxHeight(blocks, loc.getBlockX(), loc.getBlockZ(), getIgnoredMaterials());
        if(maxHeight == 0)
            maxHeight = loc.getBlockY();

        CompletableFuture<Void> future = new CompletableFuture<>();
        int finalMaxHeight = maxHeight;
        final List<String> finalMasks = masks;
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            try (EditSession editSession = WorldEdit.getInstance().newEditSession(weWorld)) {
                File schematicFile = new File(getWorldEditSchematicsFolderPath() + schematicPath);

                ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);
                ClipboardReader reader;

                if (format == null)
                    return;

                try {
                    reader = format.getReader(Files.newInputStream(schematicFile.toPath()));
                    Clipboard clipboard = reader.read();

                    AffineTransform transform = new AffineTransform();
                    transform = transform.rotateY(rotation);

                    ClipboardHolder holder = new ClipboardHolder(clipboard);
                    holder.setTransform(transform);

                    ParserContext parserContext = new ParserContext();
                    parserContext.setActor(actor);
                    parserContext.setWorld(weWorld);
                    parserContext.setSession(localSession);
                    parserContext.setExtent(editSession);

                    for (String maskString : finalMasks) {
                        // Set the mask
                        Mask mask = null;
                        if(!maskString.isEmpty())
                            mask = new MaskFactory(WorldEdit.getInstance()).parseFromInput(maskString, parserContext);

                        Operation op = holder
                                .createPaste(editSession)
                                .to(BlockVector3.at(loc.getBlockX(), finalMaxHeight + offsetY, loc.getBlockZ()))
                                .ignoreAirBlocks(true)
                                .maskSource(mask)
                                .build();
                        Operations.complete(op);
                    }
                } catch (IOException | WorldEditException e) {
                    throw new RuntimeException(e);
                }

                saveEditSession(editSession, localSession, actor);

                ChatHelper.logDebug("Pasted schematic: " + schematicPath + " at " + loc + " with rotation " + rotation);
            }

            future.complete(null);
        });

        return future;
    }

    public static CompletableFuture<Void> pasteSchematic(LocalSession localSession, Actor actor, com.sk89q.worldedit.world.World weWorld, Block[][][] blocks, String schematicPath, Location loc, double rotation){
        return pasteSchematicWithMasks(localSession, actor, weWorld, blocks, new ArrayList<>(), schematicPath, loc, rotation);
    }


    /**
     * Expands the WorldEdit selection by a given vector.
     *
     * @param localSession The local session of the actor
     * @param vector The vector to expand the selection by
     */
    public static void expandSelection(LocalSession localSession, Vector vector){
        BlockVector3 blockVector3 = BlockVector3.at(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());

        try {
            localSession.getSelection().expand(blockVector3);
        } catch (IncompleteRegionException | RegionOperationException e) {
            throw new RuntimeException(e);
        }

        ChatHelper.logDebug("Expanded selection by: " + vector);
    }


    /**
     * Clears the history of a LocalSession.
     * @param session The local session to clear the history of
     */
    public static void clearHistory(LocalSession session){
        session.clearHistory();
        ChatHelper.logDebug("Cleared history");
    }

    /**
     * Undoes the last action of a LocalSession.
     * @param session The local session to undo the last action of
     * @param player The player who created the structure
     * @param amount The amount of actions to undo
     */
    public static void undo(LocalSession session, Player player, Actor actor, int amount){
        com.sk89q.worldedit.entity.Player wePlayer = BukkitAdapter.adapt(player);

        for(int i = 0; i < amount; i++) {
            BlockBag blockBag = session.getBlockBag(wePlayer);
            EditSession undoSession = session.undo(blockBag, wePlayer);

            if (undoSession != null)
                WorldEdit.getInstance().flushBlockBag(actor, undoSession);
            else
                break;
        }
    }

    /**
    * Redoes the last action of a LocalSession.
    * @param session The local session to redo the last action of
    * @param player The player who created the structure
    * @param amount The amount of actions to redo
    */
    public static void redo(LocalSession session, Player player, Actor actor, int amount){
        com.sk89q.worldedit.entity.Player wePlayer = BukkitAdapter.adapt(player);

        for(int i = 0; i < amount; i++) {
            BlockBag blockBag = session.getBlockBag(wePlayer);
            EditSession redoSession = session.redo(blockBag, wePlayer);

            if (redoSession != null)
                WorldEdit.getInstance().flushBlockBag(actor, redoSession);
            else
                break;
        }
    }



    /**
     * Sets the gmask of a LocalSession.
     *
     * @param session The local session to set the gmask of
     * @param mask The mask to set. If the mask is null or empty, the gmask will be disabled.
     */
    public static void setGmask(LocalSession session, String mask){
        if(mask == null || mask.isEmpty()) {
            session.setMask(null);
            ChatHelper.logDebug("Disabled gmask");
            return;
        }

        try {
            ParserContext parserContext = new ParserContext();
            parserContext.setExtent(session.getSelectionWorld());

            Mask newMask = WorldEdit.getInstance().getMaskFactory().parseFromInput(mask,parserContext);
            session.setMask(newMask);
        } catch (InputParseException e) {
            throw new RuntimeException(e);
        }

        ChatHelper.logDebug("Set gmask to: " + mask);
    }




    /**
     * Commits and saves an edit session.
     *
     * @param editSession The edit session to commit
     * @param localSession The local session to save the edit session to
     */
    public static void saveEditSession(EditSession editSession, LocalSession localSession, Actor actor){
        editSession.commit();
        editSession.flushQueue();

        if(isFastAsyncWorldEditEnabled())
            localSession.remember(actor, localSession.getSelectionWorld(), editSession.getChangeSet(), FaweLimit.MAX);

        editSession.close();
    }







    /*=============================================**

                VECTOR HELPER FUNCTIONS

     **=============================================*/



    /**
     * Adjusts the height of a list of vectors so that they are on the surface of the terrain.
     *
     * @param points List of vectors to adjust
     * @param blocks List of blocks in polygon region
     */
    public static void adjustHeight(List<Vector> points, Block[][][] blocks){
        for (Vector point : points)
            point.setY(getMaxHeight(blocks, point.getBlockX(), point.getBlockZ(), getIgnoredMaterials()));
    }

    /** As long as two neighboring vectors are further than a given distance of blocks apart, add a new vector in between them
     *
     * @param points   The points to populate
     * @param distance The distance between the points
     * @return         The populated points
     */
    public static List<Vector> populatePoints(List<Vector> points, int distance){
        List<Vector> result = new ArrayList<>();

        // Go through all points
        boolean found = true;
        while(found){
            found = false;

            // Go through all vectors
            for (int i = 0; i < points.size() - 1; i++) {

                // Get the two neighboring vectors
                Vector p1 = points.get(i).clone();
                Vector p2 = points.get(i+1).clone();

                //ChatHelper.logDebug("p1: " + p1 + " p2: " + p2 + " distance: " + distance);

                // Add the first point
                result.add(p1);

                // If the distance between the two points is greater than the given distance, add a new point in between them
                if(p1.distance(p2) > distance){
                    Vector v1 = p2.clone().subtract(p1);
                    Vector v2 = v1.clone().multiply(0.5);
                    Vector v3 = p1.clone().add(v2);

                    // Add the new point
                    result.add(v3);
                    found = true;
                    //ChatHelper.logDebug("Adding new point in between: " + v3);
                }
            }

            result.add(points.getLast());
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
        Vector p4 = vectors.getLast();

        // Get the vectors between the points
        Vector v1 = p1.clone().subtract(p2);
        Vector v2 = p4.clone().subtract(p3);

        result.add(p1.clone().add(v1));
        result.addAll(vectors);
        result.add(p4.clone().add(v2));

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
        Vector p4 = vectors.getLast();

        // Get the vectors between the points
        Vector v1 = p2.clone().subtract(p1);
        Vector v2 = p3.clone().subtract(p4);

        // Shorten the vectors
        v1 = v1.clone().normalize().multiply(distance);
        v2 = v2.clone().normalize().multiply(distance);

        // Remove the first and last points
        vectors.removeFirst();
        vectors.removeLast();

        // Add the shortened vectors
        result.add(p1.clone().add(v1));
        result.addAll(vectors);
        result.add(p4.clone().add(v2));

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

        if(closestVector == null)
            return null;

        return closestVector.clone();
    }

    /**
     * Returns the given vector with the height of the point matching the surface of the terrain.
     *
     * @param vector The vector to get the height from
     * @param blocks The dataset to get the height from
     * @return The vector with the height matching the surface of the terrain
     */
    private static Vector getXYZ(Vector vector, Block[][][] blocks){
        int maxHeight = vector.getBlockY();

        if(blocks != null)
            maxHeight = getMaxHeight(blocks, vector.getBlockX(), vector.getBlockZ(), getIgnoredMaterials());
        if(maxHeight == 0)
            maxHeight = vector.getBlockY();

        return vector.setY(maxHeight);
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
            maxHeight = getMaxHeight(blocks, vector.getBlockX(), vector.getBlockZ(), getIgnoredMaterials()) + offset;
        if(maxHeight == 0)
            maxHeight = vector.getBlockY();

        return vector.getBlockX() + "," + maxHeight + "," + vector.getBlockZ();
    }

    private static Path64 convertVectorListToPath64(List<Vector> vectors, Vector reference){
        List<Point64> points = new ArrayList<>();
        for(Vector vector : vectors)
            points.add(new Point64(vector.getBlockX() - reference.getBlockX(), vector.getBlockZ() - reference.getBlockZ()));

        return new Path64(points);
    }

    private static List<List<Vector>> convertPathsToVectorList(Paths64 pathsD, Vector reference, int minHeight, int maxHeight){
        List<List<Vector>> vectors = new ArrayList<>();

        for(Path64 path : new ArrayList<>(pathsD)) {
            List<Vector> vectorList = new ArrayList<>();

            for(Point64 point : new ArrayList<>(path))
                vectorList.add(new Vector(point.x + reference.getX(), minHeight, point.y + reference.getZ()));

            Vector vector = vectorList.getLast().setY(maxHeight);
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
        Vector reference = vectors.getFirst();
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
    private static int getMaxHeight(List<Vector> vectors){
        int maxHeight = Integer.MIN_VALUE;
        for(Vector vector : vectors)
            maxHeight = Math.max(maxHeight, vector.getBlockY());

        return maxHeight;
    }





    /*=============================================**

                PREPARATION CHECK FUNCTIONS

     **=============================================*/



    /**
     * Checks if Schematic Brush is installed and sends the player a message if it isn't.
     *
     * @param p The player to check for
     * @return Whether Schematic Brush is installed
     */
    public static boolean checkIfSchematicBrushIsInstalled(Player p){
        // Check if WorldEdit is enabled
        if (!isSchematicBrushEnabled()) {
            p.sendMessage("§cPlease install Schematic Brush to use this tool. You can ask the server administrator to install it.");
            sendWikiLink(p);
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
            sendWikiLink(p);

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
            sendWikiLink(p);

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
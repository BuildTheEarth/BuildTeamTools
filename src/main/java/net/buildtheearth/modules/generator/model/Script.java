package net.buildtheearth.modules.generator.model;

import com.cryptomorin.xseries.XMaterial;
import com.fastasyncworldedit.core.registry.state.PropertyKey;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import lombok.Getter;
import net.buildtheearth.modules.generator.GeneratorModule;
import net.buildtheearth.modules.generator.utils.GeneratorUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Script {

    @Getter
    private final Player player;
    @Getter
    private final GeneratorComponent generatorComponent;
    @Getter
    private final Region region;
    @Getter
    protected final List<Operation> operations = new ArrayList<>();
    @Getter
    protected final World weWorld;
    @Getter
    protected final Actor actor;
    @Getter
    protected final LocalSession localSession;
    @Getter
    private int changes = 0;


    public Script(Player player, GeneratorComponent generatorComponent) {
        this.player = player;
        this.generatorComponent = generatorComponent;
        this.region = GeneratorUtils.getWorldEditSelection(player);
        this.weWorld = BukkitAdapter.adapt(getPlayer().getWorld());
        this.actor = BukkitAdapter.adapt(getPlayer());
        this.localSession = WorldEdit.getInstance().getSessionManager().get(actor);

        clearHistory();
        setGmask(null);
    }


    protected void finish(Block[][][] blocks, List<Vector> points){
        createSelection(points);

        this.operations.add(new Operation(Operation.OperationType.BREAKPOINT));

        GeneratorModule.getInstance().getGeneratorCommands().add(new Command(this, blocks));
        GeneratorModule.getInstance().getPlayerHistory(getPlayer()).addHistoryEntry(new History.HistoryEntry(getGeneratorComponent().getGeneratorType(), changes));
    }


    /**
     * Get the block state for a stair in the given direction, half and shape
     *
     * @param blockType The block type of the stair
     * @param facing The direction the stair is facing (north, east, south, west)
     * @param half The site of the bigger part of the stair (top, bottom)
     * @param shape The shape of the stair (straight, inner_left, inner_right, outer_left, outer_right)
     * @return The block state for the stair
     */
    protected BlockState getStair(BlockType blockType, String facing, String half, String shape){
        if(blockType == null)
            return null;

        BlockState blockState = blockType.getDefaultState();

        return blockState
                .with(PropertyKey.FACING, Direction.valueOf(facing.toUpperCase()))
                .with(PropertyKey.HALF, half)
                .with(PropertyKey.SHAPE, shape);
    }

    /**
     * Get the block state for a slab with the given type
     *
     * @param blockType The block type of the slab
     * @param type The type of the slab (top, bottom, double)
     * @return The block state for the slab
     */
    protected BlockState getSlab(BlockType blockType, String type){
        if(blockType == null)
            return null;

        BlockState blockState = blockType.getDefaultState();
        return blockState.with(PropertyKey.TYPE, type);
    }

    /**
     * This method is used to add a command to the operations list.
     * It creates a new Operation with type COMMAND and adds it to the list of operations to execute.
     * A command can be something like "//set 1", "//replace 1 2", "//copy", "//paste" etc.
     *
     * @param command The command to add
     */
    public void createCommand(String command){
        operations.add(new Operation(command));
    }

    /**
     * This method is used to create a break point in the script.
     * When this command is reached, the script will pause and wait for the Operation to finish.
     * <p/>
     * In Async WorldEdit operations such as FAWE, the commands might be executed in parallel.
     * Unfortunately there is currently no way to check in the FAWE API if a command was processed or not.
     * That's why break points are used in specific places to make sure that the next command is only executed after the previous command has finished.
     * <p/>
     * If the next command relies on changes that the previous command made, a break point should be added.
     * The break point adds a command to the list which changes the Pos2 of the Region to a Barrier block.
     * <p/>
     * <b>Note</b>: This method clears the current gmask of the player so don't use it if you want to keep the current gmask.
     */
    public void createBreakPoint(){
        operations.add(new Operation(Operation.OperationType.BREAKPOINT));
    }

    /**
     * This method is used to paste a schematic at a specific location.
     * It creates a new Operation with type PASTE_SCHEMATIC and adds it to the list of operations to execute.
     *
     * @param pathToSchematic The path to the schematic file
     * @param location        The location where the schematic should be pasted
     * @param rotation        The rotation at which the schematic should be pasted
     */
    public void pasteSchematic(String pathToSchematic, Location location, double rotation){
        operations.add(new Operation(Operation.OperationType.PASTE_SCHEMATIC, pathToSchematic, location, rotation));
    }


    /**
     * This method is used to create a new selection of any type given a list of points.
     * If the list of points contains 2 points, a cuboid selection is created.
     * If the list of points contains more than 2 points, a polygonal selection is created.
     *
     * @param points The list of points to create the selection from
     */
    public void createSelection(List<Vector> points){
        if(points.size() < 2)
            throw new IllegalArgumentException("The list of points must contain at least 2 points");

        if(points.size() == 2)
            createCuboidSelection(points.get(0), points.get(1));
        else
            createPolySelection(points);
    }

    /**
     * This method is used to create a new cuboid selection.
     * It creates a new Operation with type CUBOID_SELECTION and adds it to the list of operations to execute.
     *
     * @param vector1 Position 1
     * @param vector2 Position 2
     */
    public void createCuboidSelection(Vector vector1, Vector vector2){
        operations.add(new Operation(Operation.OperationType.CUBOID_SELECTION, vector1, vector2));
    }

    /**
     * This method is used to create a new polygonal selection.
     * It creates a new Operation with type POLY_SELECTION and adds it to the list of operations to execute.
     *
     * @param points The list of points to create the selection from
     */
    public void createPolySelection(List<Vector> points){
        operations.add(new Operation(Operation.OperationType.POLYGONAL_SELECTION, (Object) points.toArray(new Vector[0])));
    }

    /**
     * This method is uesd to create a new convex selection.
     * It creates a new Operation with type CONVEX_SELECTION and adds it to the list of operations to execute.
     *
     * @param points The list of points to create the selection from
     */
    public void createConvexSelection(List<Vector> points){
        operations.add(new Operation(Operation.OperationType.CONVEX_SELECTION, (Object) points.toArray(new Vector[0])));
    }

    /**
     * This method is used to clear the history of the LocalSession.
     * It creates a new Operation with type CLEAR_HISTORY and adds it to the list of operations to execute.
     */
    public void clearHistory(){
        operations.add(new Operation(Operation.OperationType.CLEAR_HISTORY));
    }

    /**
     * This method is used to set the gmask of the player.
     *
     * @param mask The mask to set. If the mask is null, the gmask will be disabled.
      */
    public void setGmask(String mask){
        operations.add(new Operation(Operation.OperationType.SET_GMASK, mask));
    }

    /**
     * This method is used to expand the selection by a specific amount of blocks.
     * It creates a new Operation with type EXPAND_SELECTION and adds it to the list of operations to execute.
     */
    public void expandSelection(Vector vector) {
        operations.add(new Operation(Operation.OperationType.EXPAND_SELECTION, vector));
    }

    /**
     * This method is used to replace one block type with another.
     * It creates a new Operation with type REPLACE_BLOCKS and adds it to the list of operations to execute.
     *
     * @param from The block type to replace
     * @param to The block types to replace with
     */
    public void replaceBlocks(BlockState from, BlockState[] to){
        operations.add(new Operation(Operation.OperationType.REPLACE_BLOCKSTATES, from, to));
        changes++;
    }
    public void replaceBlocks(XMaterial from, XMaterial[] to){
        replaceBlocks(GeneratorUtils.getBlockState(from), GeneratorUtils.getBlockState(to));
    }
    public void replaceBlocks(XMaterial from, XMaterial to){
        replaceBlocks(from, new XMaterial[]{to});
    }
    public void replaceBlocks(BlockState from, BlockState to){
        replaceBlocks(from, new BlockState[]{to});
    }

    /**
     * Set blocks with a mask.
     * It creates a new Operation with type SET_BLOCKS_WITH_EXPRESSION_MASK and adds it to the list of operations to execute.
     *
     * @param masks The expression masks
     * @param blockState The blockState to set the blocks to
     * @param iterations The number of iterations to execute
     */
    public void setBlocksWithMask(List<String> masks, BlockState[] blockState, int iterations) {
        operations.add(new Operation(Operation.OperationType.REPLACE_BLOCKSTATES_WITH_MASKS, masks.toArray(new String[0]), null, blockState, iterations));
        changes += masks.size()*iterations;
    }
    public void setBlocksWithMask(List<String> masks, XMaterial[] blockState, int iterations) {
        setBlocksWithMask(masks, GeneratorUtils.getBlockState(blockState), iterations);
    }
    public void setBlocksWithMask(String mask, XMaterial[] blockState) {
        List<String> masks = new ArrayList<>();
        masks.add(mask);
        setBlocksWithMask(masks, blockState, 1);
    }
    public void setBlocksWithMask(List<String> masks, XMaterial material, int iterations) {
        setBlocksWithMask(masks, new XMaterial[]{material}, iterations);
    }
    public void setBlocksWithMask(String mask, XMaterial material, int iterations) {
        List<String> masks = new ArrayList<>();
        masks.add(mask);
        setBlocksWithMask(masks, new XMaterial[]{material}, iterations);
    }
    public void setBlocksWithMask(String mask, XMaterial material) {
        setBlocksWithMask(mask, material, 1);
    }
    public void setBlocksWithMask(String mask, BlockState blockState, int iterations) {
        List<String> masks = new ArrayList<>();
        masks.add(mask);
        setBlocksWithMask(masks, new BlockState[]{blockState}, iterations);
    }
    public void setBlocksWithMask(String mask, BlockState blockState) {
        setBlocksWithMask(mask, blockState, 1);
    }


    /**
     * Replace blocks with an expression mask.
     * It creates a new Operation with type REPLACE_BLOCKS_WITH_EXPRESSION_MASK and adds it to the list of operations to execute.
     *
     * @param masks The expression masks
     * @param from The block type to replace
     * @param to The block types to replace with
     * @param iterations The number of iterations to execute
     */
    public void replaceBlocksWithMask(List<String> masks, BlockState from, BlockState[] to, int iterations) {
        operations.add(new Operation(Operation.OperationType.REPLACE_BLOCKSTATES_WITH_MASKS, masks.toArray(new String[0]), from, to, iterations));
        changes += masks.size()*iterations;
    }
    public void replaceBlocksWithMask(List<String> masks, XMaterial from, XMaterial[] to, int iterations) {
        replaceBlocksWithMask(masks, GeneratorUtils.getBlockState(from), GeneratorUtils.getBlockState(to), iterations);
    }
    public void replaceBlocksWithMask(List<String> masks, BlockState from, BlockState to, int iterations) {
        replaceBlocksWithMask(masks, from, new BlockState[]{to}, iterations);
    }
    public void replaceBlocksWithMask(List<String> masks, XMaterial from, XMaterial to, int iterations) {
        replaceBlocksWithMask(masks, GeneratorUtils.getBlockState(from), GeneratorUtils.getBlockState(to), iterations);
    }
    public void replaceBlocksWithMask(String mask, XMaterial from, XMaterial to, int iterations) {
        List<String> masks = new ArrayList<>();
        masks.add(mask);
        replaceBlocksWithMask(masks, from, to, iterations);
    }
    public void replaceBlocksWithMask(String mask, XMaterial from, XMaterial to) {
        replaceBlocksWithMask(mask, from, to, 1);
    }

    public void replaceBlocksWithMask(String mask, BlockState from, BlockState to, int iterations) {
        List<String> masks = new ArrayList<>();
        masks.add(mask);
        replaceBlocksWithMask(masks, from, to, iterations);
    }
    public void replaceBlocksWithMask(String mask, BlockState from, BlockState to) {
        replaceBlocksWithMask(mask, from, to, 1);
    }



    /**
     * Draw a curve with an expression mask.
     * It creates a new Operation with type DRAW_CURVE_WITH_MASKS and adds it to the list of operations to execute.
     *
     * @param masks The expression masks
     * @param points The points to draw the curve through
     * @param blocks The block states to set the blocks to
     * @param matchElevation Whether the elevation of the points should be matched to the region
     */
    public void drawCurveWithMask(List<String> masks, List<Vector> points, BlockState[] blocks, boolean matchElevation) {
        operations.add(new Operation(Operation.OperationType.DRAW_CURVE_WITH_MASKS, masks.toArray(new String[0]), points.toArray(new Vector[0]), blocks, matchElevation));
        changes += masks.size();
    }

    public void drawCurveWithMask(List<String> masks, List<Vector> points, XMaterial[] blocks, boolean matchElevation) {
        drawCurveWithMask(masks, points, GeneratorUtils.getBlockState(blocks), matchElevation);
    }

    public void drawCurveWithMask(String mask, List<Vector> points, XMaterial[] blocks, boolean matchElevation) {
        List<String> masks = new ArrayList<>();
        masks.add(mask);
        drawCurveWithMask(masks, points, blocks, matchElevation);
    }

    public void drawCurveWithMask(String mask, List<Vector> points, XMaterial block, boolean matchElevation) {
        drawCurveWithMask(mask, points, new XMaterial[]{block}, matchElevation);
    }

    public void drawCurve(List<Vector> points, XMaterial[] blocks, boolean matchElevation) {
        drawCurveWithMask(new ArrayList<>(), points, blocks, matchElevation);
    }

    public void drawCurve(List<Vector> points, XMaterial block, boolean matchElevation) {
        drawCurveWithMask(new ArrayList<>(), points, new XMaterial[]{block}, matchElevation);
    }


    /**
     * Draw a poly line with an expression mask.
     * It creates a new Operation with type DRAW_POLY_LINE_WITH_MASKS and adds it to the list of operations to execute.
     *
     * @param masks The expression masks
     * @param points The points to draw the poly line through
     * @param blocks The block states to set the blocks to
     * @param matchElevation Whether the elevation of the points should be matched to the region
     */
    public void drawPolyLineWithMask(List<String> masks, List<Vector> points, BlockState[] blocks, boolean matchElevation) {
        operations.add(new Operation(Operation.OperationType.DRAW_POLY_LINE_WITH_MASKS, masks.toArray(new String[0]), points.toArray(new Vector[0]), blocks, matchElevation));
        changes += masks.size();
    }

    public void drawPolyLineWithMask(List<String> masks, List<Vector> points, XMaterial[] blocks, boolean matchElevation) {
        drawPolyLineWithMask(masks, points, GeneratorUtils.getBlockState(blocks), matchElevation);
    }

    public void drawPolyLineWithMask(String mask, List<Vector> points, XMaterial[] blocks, boolean matchElevation) {
        List<String> masks = new ArrayList<>();
        masks.add(mask);
        drawPolyLineWithMask(masks, points, blocks, matchElevation);
    }

    public void drawPolyLineWithMask(String mask, List<Vector> points, XMaterial block, boolean matchElevation) {
        drawPolyLineWithMask(mask, points, new XMaterial[]{block}, matchElevation);
    }

    public void drawPolyLine(List<Vector> points, XMaterial[] blocks, boolean matchElevation) {
        drawPolyLineWithMask(new ArrayList<>(), points, blocks, matchElevation);
    }

    public void drawPolyLine(List<Vector> points, XMaterial block, boolean matchElevation) {
        drawPolyLineWithMask(new ArrayList<>(), points, new XMaterial[]{block}, matchElevation);
    }



    /**
     * Draw a line with an expression mask.
     * It creates a new Operation with type DRAW_LINE_WITH_MASKS and adds it to the list of operations to execute.
     *
     * @param masks The expression masks
     * @param point1 The start point of the line
     * @param point2 The end point of the line
     * @param blocks The block states to set the blocks to
     * @param matchElevation Whether the elevation of the points should be matched to the region
     */
    public void drawLineWithMask(List<String> masks, Vector point1, Vector point2, BlockState[] blocks, boolean matchElevation) {
        operations.add(new Operation(Operation.OperationType.DRAW_LINE_WITH_MASKS, masks.toArray(new String[0]), point1, point2, blocks, matchElevation));
        changes += masks.size();
    }

    public void drawLineWithMask(List<String> masks, Vector point1, Vector point2, XMaterial[] blocks, boolean matchElevation) {
        drawLineWithMask(masks, point1, point2, GeneratorUtils.getBlockState(blocks), matchElevation);
    }

    public void drawLineWithMask(String mask, Vector point1, Vector point2, XMaterial[] blocks, boolean matchElevation) {
        List<String> masks = new ArrayList<>();
        masks.add(mask);
        drawLineWithMask(masks, point1, point2, blocks, matchElevation);
    }

    public void drawLineWithMask(String mask, Vector point1, Vector point2, XMaterial block, boolean matchElevation) {
        drawLineWithMask(mask, point1, point2, new XMaterial[]{block}, matchElevation);
    }

    public void drawLine(Vector point1, Vector point2, XMaterial[] blocks, boolean matchElevation) {
        drawLineWithMask(new ArrayList<>(), point1, point2, blocks, matchElevation);
    }

    public void drawLine(Vector point1, Vector point2, XMaterial block, boolean matchElevation) {
        drawLineWithMask(new ArrayList<>(), point1, point2, new XMaterial[]{block}, matchElevation);
    }
}

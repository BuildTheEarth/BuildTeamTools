package net.buildtheearth.modules.generator.model;

import com.cryptomorin.xseries.XMaterial;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BlockState;
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
    protected int changes = 0;


    public Script(Player player, GeneratorComponent generatorComponent) {
        this.player = player;
        this.generatorComponent = generatorComponent;
        this.region = GeneratorUtils.getWorldEditSelection(player);
    }


    protected void finish(Block[][][] blocks){
        this.operations.add(new Operation(Operation.OperationType.BREAKPOINT));

        GeneratorModule.getInstance().getGeneratorCommands().add(new Command(this, blocks));
        GeneratorModule.getInstance().getPlayerHistory(getPlayer()).addHistoryEntry(new History.HistoryEntry(getGeneratorComponent().getGeneratorType(), changes));
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
    public void createPasteSchematic(String pathToSchematic, Location location, double rotation){
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
            createPolySelection(operations, points);
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
     * @param commands The list of commands to add the selection to
     * @param points The list of points to create the selection from
     */
    public void createPolySelection(List<Operation> commands, List<Vector> points){
        commands.add(new Operation(Operation.OperationType.POLYGONAL_SELECTION, points.toArray()));
    }

    /**
     * This method is uesd to create a new convex selection.
     * It creates a new Operation with type CONVEX_SELECTION and adds it to the list of operations to execute.
     *
     * @param commands The list of commands to add the selection to
     * @param points The list of points to create the selection from
     */
    public void createConvexSelection(List<Operation> commands, List<Vector> points){
        commands.add(new Operation(Operation.OperationType.CONVEX_SELECTION, points.toArray()));
    }

    /**
     * This method is used to clear the history of the LocalSession.
     * It creates a new Operation with type CLEAR_HISTORY and adds it to the list of operations to execute.
     */
    public void clearHistory(){
        operations.add(new Operation(Operation.OperationType.CLEAR_HISTORY));
    }

    /**
     * This method is used to disable the gmask of the player.
     * It creates a new Operation with type DISABLE_GMASK and adds it to the list of operations to execute.
     */
    public void disableGmask(){
        operations.add(new Operation(Operation.OperationType.DISABLE_GMASK));
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
     * @param to The block type to replace with
     */
    public void replaceBlocks(XMaterial from, XMaterial to){
        operations.add(new Operation(Operation.OperationType.REPLACE_XMATERIALS, from, to));
    }
    public void replaceBlocks(BlockState from, BlockState to){
        operations.add(new Operation(Operation.OperationType.REPLACE_BLOCKSTATES, from, to));
    }

    /**
     * Set blocks with a mask.
     * It creates a new Operation with type SET_BLOCKS_WITH_EXPRESSION_MASK and adds it to the list of operations to execute.
     *
     * @param masks The expression masks
     * @param material The material to set the blocks to
     * @param iterations The number of iterations to execute
     */
    public void setBlocksWithMask(List<String> masks, XMaterial material, int iterations) {
        operations.add(new Operation(Operation.OperationType.REPLACE_XMATERIALS_WITH_MASKS, masks.toArray(new String[0]), null, material, iterations));
    }
    public void setBlocksWithMask(String mask, XMaterial material, int iterations) {
        List<String> masks = new ArrayList<>();
        masks.add(mask);
        setBlocksWithMask(masks, material, iterations);
    }
    public void setBlocksWithMask(String mask, XMaterial material) {
        setBlocksWithMask(mask, material, 1);
    }
    public void setBlocksWithMask(List<String> masks, BlockState blockState, int iterations) {
        operations.add(new Operation(Operation.OperationType.REPLACE_BLOCKSTATES_WITH_MASKS, masks.toArray(new String[0]), null, blockState, iterations));
    }
    public void setBlocksWithMask(String mask, BlockState blockState, int iterations) {
        List<String> masks = new ArrayList<>();
        masks.add(mask);
        setBlocksWithMask(masks, blockState, iterations);
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
     * @param to The block type to replace with
     * @param iterations The number of iterations to execute
     */
    public void replaceBlocksWithMask(List<String> masks, XMaterial from, XMaterial to, int iterations) {
        operations.add(new Operation(Operation.OperationType.REPLACE_XMATERIALS_WITH_MASKS, masks.toArray(new String[0]), from, to, iterations));
    }
    public void replaceBlocksWithMask(String mask, XMaterial from, XMaterial to, int iterations) {
        List<String> masks = new ArrayList<>();
        masks.add(mask);
        replaceBlocksWithMask(masks, from, to, iterations);
    }
    public void replaceBlocksWithMask(String mask, XMaterial from, XMaterial to) {
        replaceBlocksWithMask(mask, from, to, 1);
    }
    public void replaceBlocksWithMask(List<String> masks, BlockState from, BlockState to, int iterations) {
        operations.add(new Operation(Operation.OperationType.REPLACE_BLOCKSTATES_WITH_MASKS, masks.toArray(new String[0]), from, to, iterations));
    }
    public void replaceBlocksWithMask(String mask, BlockState from, BlockState to, int iterations) {
        List<String> masks = new ArrayList<>();
        masks.add(mask);
        replaceBlocksWithMask(masks, from, to, iterations);
    }
    public void replaceBlocksWithMask(String mask, BlockState from, BlockState to) {
        replaceBlocksWithMask(mask, from, to, 1);
    }
}

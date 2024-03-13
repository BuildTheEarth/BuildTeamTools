package net.buildtheearth.modules.generator.model;

import com.sk89q.worldedit.regions.Region;
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
     * Adds a command to the operations list.
     * A command can be something like "//set 1", "//replace 1 2", "//copy", "//paste" etc.
     *
     * @param command The command to add
     */
    public void createCommand(String command){
        operations.add(new Operation(command));
    }

    /**
     * Adds a break point to the operations list.
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
     * Creates a new Operation with type PASTE_SCHEMATIC and adds it to the list of operations to execute.
     *
     * @param pathToSchematic The path to the schematic file
     * @param location        The location where the schematic should be pasted
     * @param rotation        The rotation at which the schematic should be pasted
     */
    public void createPasteSchematic(String pathToSchematic, Location location, double rotation){
        operations.add(new Operation(Operation.OperationType.PASTE_SCHEMATIC, pathToSchematic, location, rotation));
    }

    /**
     * Creates a new Operation with type CUBOID_SELECTION and adds it to the list of operations to execute.
     *
     * @param vector1 Position 1
     * @param vector2 Position 2
     */
    public void createCuboidSelection(Vector vector1, Vector vector2){
        operations.add(new Operation(Operation.OperationType.CUBOID_SELECTION, vector1, vector2));
    }

    /**
     * Creates a new Operation with type POLY_SELECTION and adds it to the list of operations to execute.
     *
     * @param commands The list of commands to add the selection to
     * @param points The list of points to create the selection from
     */
    public void createPolySelection(List<Operation> commands, List<Vector> points){
        commands.add(new Operation(Operation.OperationType.POLYGONAL_SELECTION, points));
    }

    /**
     * Creates a new Operation with type CONVEX_SELECTION and adds it to the list of operations to execute.
     *
     * @param commands The list of commands to add the selection to
     * @param points The list of points to create the selection from
     */
    public void createConvexSelection(List<Operation> commands, List<Vector> points){
        commands.add(new Operation(Operation.OperationType.CONVEX_SELECTION, points));
    }
}

package net.buildtheearth.modules.generator.model;

import com.sk89q.worldedit.regions.Region;
import lombok.Getter;
import net.buildtheearth.modules.generator.GeneratorModule;
import net.buildtheearth.modules.generator.utils.GeneratorUtils;
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
    protected final List<String> commands = new ArrayList<>();

    private final Vector[] minMax;

    public Script(Player player, GeneratorComponent generatorComponent) {
        this.player = player;
        this.generatorComponent = generatorComponent;

        this.region = GeneratorUtils.getWorldEditSelection(player);
        this.minMax = GeneratorUtils.getMinMaxPoints(region);
    }

    protected void finish(int operations, Block[][][] blocks){
        GeneratorModule.getInstance().getGeneratorCommands().add(new Command(getPlayer(), getGeneratorComponent(), commands, operations, blocks));
        GeneratorModule.getInstance().getPlayerHistory(getPlayer()).addHistoryEntry(new History.HistoryEntry(getGeneratorComponent().getGeneratorType(), operations));
    }

    /**
     * Adds a break point to the command list.
     * When this command is reached, the script will pause and wait for the Operation to finish.
     *
     * In Async WorldEdit operations such as FAWE, the commands might be executed in parallel.
     * Unfortunately there is currently no way to check in the FAWE API if a command was processed or not.
     * That's why break points are used in specific places to make sure that the next command is only executed after the previous command has finished.
     *
     * If the next command relies on changes that the previous command made, a break point should be added.
     * The break point adds a command to the list which changes the Pos2 of the Region to a Barrier block.
     */
    protected void addBreakPoint(){
        Vector point = minMax[0];
        commands.add("/pos2 " + point.getBlockX() + " " + point.getBlockY() + " " + point.getBlockZ());
        commands.add("/pos2 " + point.getBlockX() + " " + point.getBlockY() + " " + point.getBlockZ());
    }
}

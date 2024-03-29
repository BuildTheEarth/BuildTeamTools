package net.buildtheearth.modules.generator;

import com.sk89q.worldedit.LocalSession;
import lombok.Getter;
import net.buildtheearth.modules.Module;
import net.buildtheearth.modules.common.CommonModule;
import net.buildtheearth.modules.generator.commands.GeneratorCommand;
import net.buildtheearth.modules.generator.components.field.Field;
import net.buildtheearth.modules.generator.components.house.House;
import net.buildtheearth.modules.generator.components.kml.KmlCommand;
import net.buildtheearth.modules.generator.components.kml.KmlTabCompleter;
import net.buildtheearth.modules.generator.components.rail.Rail;
import net.buildtheearth.modules.generator.components.road.Road;
import net.buildtheearth.modules.generator.components.tree.Tree;
import net.buildtheearth.modules.generator.model.Command;
import net.buildtheearth.modules.generator.model.GeneratorCollections;
import net.buildtheearth.modules.generator.model.History;
import org.bukkit.entity.Player;

import java.util.*;

public class GeneratorModule extends Module {

    public static String INSTALL_WIKI = "https://github.com/BuildTheEarth/BuildTeamTools/wiki/Installation";

    private final HashMap<UUID, History> playerHistory = new HashMap<>();

    @Getter
    private final List<Command> generatorCommands = new ArrayList<>();

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



    private static GeneratorModule instance = null;

    public GeneratorModule() {
        super("Generator");
    }

    public static GeneratorModule getInstance() {
        return instance == null ? instance = new GeneratorModule() : instance;
    }

    @Override
    public void enable() {
        // Check if WorldEdit is enabled
        if (!CommonModule.getInstance().getDependencyComponent().isWorldEditEnabled()) {
            shutdown("§cWorldEdit is not installed.");
            return;
        }

        house = new House();
        road = new Road();
        rail = new Rail();
        tree = new Tree();
        field = new Field();

        GeneratorCollections.GENERATOR_COLLECTIONS_VERSION = GeneratorCollections.getRepositoryReleaseVersionString("BuildTheEarth", "GeneratorCollections");

        // In case the version could not be retrieved, set it to 3.0 as a fallback
        if(GeneratorCollections.GENERATOR_COLLECTIONS_VERSION == null)
            GeneratorCollections.GENERATOR_COLLECTIONS_VERSION = "3.0";

        // Check if the GeneratorCollections plugin is installed and up to date
        GeneratorCollections.checkIfGeneratorCollectionsIsInstalled(null);

        LocalSession.MAX_HISTORY_SIZE = 500;

        super.enable();
    }

    @Override
    public void registerCommands() {
        registerCommand("generate", new GeneratorCommand());
        registerCommand("kml", new KmlCommand(), new KmlTabCompleter());
    }

    @Override
    public void registerListeners() {

    }

    /**
     * Processes the command queues one after another and lets the waiting players know their position in the queue and the percentage of the current generation.
     * <p>
     * Relations:
     *
     * @see Command
     * @see Command#tick()
     */
    public void tick() {
        if (generatorCommands.size() == 0)
            return;

        if (generatorCommands.get(0).getCommands().size() == 0) {
            generatorCommands.remove(0);
            return;
        }

        for (int i = 1; i < generatorCommands.size(); i++) {
            boolean isInQueue = false;

            for (int j = i - 1; j > 0; j--)
                if (generatorCommands.get(i).getPlayer().getUniqueId().equals(generatorCommands.get(j).getPlayer().getUniqueId()))
                    isInQueue = true;

            if (generatorCommands.get(0).getPlayer().getUniqueId().equals(generatorCommands.get(i).getPlayer().getUniqueId()))
                isInQueue = true;

            if (isInQueue)
                continue;

            generatorCommands.get(i).getPlayer().sendActionBar("§c§lOther Generation in Progress. Position: §e" + i + "/" + generatorCommands.size() + " (" + generatorCommands.get(0).getPercentage() + "%)");
        }

        generatorCommands.get(0).tick();
    }

    /**
     * Returns the Generator History of a player.
     *
     * @param p The player whose history should be returned.
     * @return The Generator History of the player.
     */
    public History getPlayerHistory(Player p) {
        if (!playerHistory.containsKey(p.getUniqueId()))
            playerHistory.put(p.getUniqueId(), new History(p));

        return playerHistory.get(p.getUniqueId());
    }
}
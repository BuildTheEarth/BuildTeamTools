package net.buildtheearth.buildteamtools.modules.generator;

import com.alpsbte.alpslib.utils.GeneratorUtils;
import com.sk89q.worldedit.LocalSession;
import lombok.Getter;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.Module;
import net.buildtheearth.buildteamtools.modules.common.CommonModule;
import net.buildtheearth.buildteamtools.modules.generator.commands.GeneratorCommand;
import net.buildtheearth.buildteamtools.modules.generator.components.field.Field;
import net.buildtheearth.buildteamtools.modules.generator.components.house.House;
import net.buildtheearth.buildteamtools.modules.generator.components.kml.KmlCommand;
import net.buildtheearth.buildteamtools.modules.generator.components.kml.KmlTabCompleter;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.Rail;
import net.buildtheearth.buildteamtools.modules.generator.components.road.Road;
import net.buildtheearth.buildteamtools.modules.generator.components.tree.Tree;
import net.buildtheearth.buildteamtools.modules.generator.listeners.GeneratorListener;
import net.buildtheearth.buildteamtools.modules.generator.model.Command;
import net.buildtheearth.buildteamtools.modules.generator.model.GeneratorCollections;
import net.buildtheearth.buildteamtools.modules.generator.model.History;
import net.buildtheearth.buildteamtools.utils.WikiLinks;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GeneratorModule extends Module {

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
        super("Generator", WikiLinks.GEN);
    }

    public static GeneratorModule getInstance() {
        return instance == null ? instance = new GeneratorModule() : instance;
    }

    @Override
    public void enable() {
        GeneratorUtils.plugin = BuildTeamTools.getInstance();

        // Check if WorldEdit is enabled
        if (!CommonModule.getInstance().getDependencyComponent().isFastAsyncWorldEditEnabled()) {
            shutdown("§FastAsyncWorldEdit is not installed.");
            return;
        }

        house = new House();
        road = new Road();
        rail = new Rail();
        tree = new Tree();
        field = new Field();

        GeneratorCollections.generatorCollectionsVersion = GeneratorCollections.getRepositoryReleaseVersionString("BuildTheEarth", "GeneratorCollections");

        // In case the version could not be retrieved, set it to 3.0 as a fallback
        if (GeneratorCollections.generatorCollectionsVersion == null)
            GeneratorCollections.generatorCollectionsVersion = "3.0";

        // Check if the GeneratorCollections plugin is installed and up to date
        GeneratorCollections.hasUpdatedGeneratorCollections(null);

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
        super.registerListeners(
            new GeneratorListener()
        );
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
        if (generatorCommands.isEmpty())
            return;

        // Tick all commands in the queue
        for(Command command : new ArrayList<>(generatorCommands)){
            if (command.getOperations().isEmpty()) {
                if(!command.isFinished())
                    command.finish();
                generatorCommands.remove(command);
                continue;
            }

            command.tick();
        }
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

    /**
     * Checks if a player is currently generating.
     * @param p The player to check.
     * @return True if the player is currently generating, false otherwise.
     */
    public boolean isGenerating(Player p){
        for (Command command : generatorCommands)
            if (command.getPlayer().getUniqueId().equals(p.getUniqueId()))
                return true;

        return false;
    }
}
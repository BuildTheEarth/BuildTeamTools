package net.buildtheearth.modules.generator.components.tree;

import com.alpsbte.alpslib.utils.GeneratorUtils;
import lombok.Getter;
import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.generator.model.GeneratorCollections;
import net.buildtheearth.modules.generator.model.GeneratorComponent;
import net.buildtheearth.modules.generator.model.GeneratorType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Tree extends GeneratorComponent {

    public static final String TREE_PACK_VERSION = "v2.0";
    @Getter
    private Set<String> heights = new HashSet<>();

    public Tree() {
        super(GeneratorType.TREE);
        Bukkit.getScheduler().runTaskAsynchronously(BuildTeamTools.getInstance(), () -> {
            Path basePath = Path.of(GeneratorUtils.getWorldEditSchematicsFolderPath(), "GeneratorCollections", "treepack");
            try (Stream<Path> paths = Files.walk(basePath, 2)) {
                heights = paths
                        .filter(p -> basePath.relativize(p).getNameCount() == 2)
                        .filter(Files::isDirectory)
                        .map(p -> p.getFileName().toString())
                        .collect(Collectors.toSet());
            } catch (IOException e) {
                BuildTeamTools.getInstance().getComponentLogger().warn("Failed to load tree pack heights:", e);
            }
        });
    }

    @Override
    public boolean checkForPlayer(Player p) {
        if (!GeneratorCollections.hasUpdatedGeneratorCollections(p))
            return false;

        return GeneratorUtils.checkIfSchematicBrushIsInstalled(p);
    }

    @Override
    public void generate(Player p) {
        if (!checkForPlayer(p))
            return;

        TreeScripts.treescript_v_1_0(p, this);
    }
}

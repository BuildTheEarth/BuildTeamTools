package net.buildtheearth.modules.generator.components.tree;

import net.buildtheearth.modules.generator.GeneratorModule;
import net.buildtheearth.modules.generator.model.GeneratorCollections;
import net.buildtheearth.modules.generator.model.GeneratorComponent;
import net.buildtheearth.modules.generator.model.GeneratorType;
import net.buildtheearth.modules.generator.utils.GeneratorUtils;
import org.bukkit.entity.Player;

public class Tree extends GeneratorComponent {

    public static final String TREE_PACK_VERSION = "v2.0";


    public Tree() {
        super(GeneratorType.TREE);
    }

    @Override
    public boolean checkForPlayer(Player p) {
        if (!GeneratorCollections.checkIfGeneratorCollectionsIsInstalled(p))
            return false;

        return GeneratorUtils.checkIfSchematicBrushIsInstalled(p);
    }

    @Override
    public void generate(Player p) {
        if (!GeneratorModule.getInstance().getRoad().checkForPlayer(p))
            return;

        TreeScripts.treescript_v_1_0(p, this);
    }
}

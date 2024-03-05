package net.buildtheearth.modules.generator.components.tree;

import net.buildtheearth.modules.generator.GeneratorModule;
import net.buildtheearth.modules.generator.model.GeneratorComponent;
import net.buildtheearth.modules.generator.model.GeneratorType;
import net.buildtheearth.modules.generator.utils.GeneratorUtils;
import org.bukkit.entity.Player;

public class Tree extends GeneratorComponent {

    public static String TREE_PACK_VERSION = "v2.0";


    public Tree() {
        super(GeneratorType.TREE);

        wikiPage = "https://github.com/BuildTheEarth/BuildTeamTools/wiki/Tree-Command";
    }

    @Override
    public boolean checkPlayer(Player p) {

        if (!GeneratorUtils.checkIfTreePackIsInstalled(p, true))
            return false;

        if (!GeneratorUtils.checkIfSchematicBrushIsInstalled(p))
            return false;

        if (getPlayerSettings().get(p.getUniqueId()).getBlocks() == null)
            getPlayerSettings().get(p.getUniqueId()).setBlocks(GeneratorUtils.analyzeRegion(p, p.getWorld()));

        return true;
    }

    @Override
    public void generate(Player p) {
        if (!GeneratorModule.getInstance().getRoad().checkPlayer(p))
            return;

        TreeScripts.treescript_v_1_0(p, this);

        sendSuccessMessage(p);
    }
}

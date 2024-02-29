package net.buildtheearth.modules.generator.modules.tree;

import net.buildtheearth.Main;
import net.buildtheearth.modules.generator.GeneratorModule;
import net.buildtheearth.modules.generator.model.GeneratorType;
import org.bukkit.entity.Player;

public class Tree extends net.buildtheearth.modules.generator.model.GeneratorModule {

    public static String TREE_PACK_VERSION = "v2.0";


    public Tree() {
        super(GeneratorType.TREE);

        wikiPage = "https://github.com/BuildTheEarth/BuildTeamTools/wiki/Tree-Command";
    }

    @Override
    public boolean checkPlayer(Player p) {
        if (!GeneratorModule.checkIfWorldEditIsInstalled(p))
            return false;

        if (!GeneratorModule.checkIfTreePackIsInstalled(p, true))
            return false;

        if (!GeneratorModule.checkIfSchematicBrushIsInstalled(p))
            return false;

        if (getPlayerSettings().get(p.getUniqueId()).getBlocks() == null)
            getPlayerSettings().get(p.getUniqueId()).setBlocks(GeneratorModule.analyzeRegion(p, p.getWorld()));

        return true;
    }

    @Override
    public void generate(Player p) {
        if (!Main.getBuildTeamTools().getGeneratorModule().getRoad().checkPlayer(p))
            return;

        TreeScripts.treescript_v_1_0(p, this);

        sendSuccessMessage(p);
    }
}

package net.buildtheearth.buildteam.components.generator.tree;

import net.buildtheearth.Main;
import net.buildtheearth.buildteam.components.generator.Generator;
import net.buildtheearth.buildteam.components.generator.GeneratorModule;
import net.buildtheearth.buildteam.components.generator.GeneratorType;
import org.bukkit.entity.Player;

public class Tree extends GeneratorModule {

    public Tree() {
        super(GeneratorType.TREE);

        wikiPage = "https://github.com/BuildTheEarth/BuildTeamTools/wiki/Tree-Command";
    }

    @Override
    public boolean checkPlayer(Player p) {
        if(!Generator.checkIfWorldEditIsInstalled(p))
            return false;

        if(!Generator.checkIfGeneratorCollectionsIsInstalled(p))
            return false;

        if(!Generator.checkIfSchematicBrushIsInstalled(p))
            return false;

        if(getPlayerSettings().get(p.getUniqueId()).getBlocks() == null)
            getPlayerSettings().get(p.getUniqueId()).setBlocks(Generator.analyzeRegion(p, p.getWorld()));

        return true;
    }

    @Override
    public void generate(Player p) {
        if(!Main.getBuildTeam().getGenerator().getRoad().checkPlayer(p))
            return;

        TreeScripts.treescript_v_1_0(p, this);

        sendSuccessMessage(p);
    }
}

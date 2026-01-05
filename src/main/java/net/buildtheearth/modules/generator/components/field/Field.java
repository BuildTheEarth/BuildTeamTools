package net.buildtheearth.modules.generator.components.field;

import net.buildtheearth.modules.generator.model.GeneratorComponent;
import net.buildtheearth.modules.generator.model.GeneratorType;
import net.buildtheearth.modules.generator.utils.GeneratorUtils;
import org.bukkit.entity.Player;

public class Field extends GeneratorComponent {

    public Field() {
        super(GeneratorType.FIELD);
        wikiPage = "https://github.com/BuildTheEarth/BuildTeamTools/wiki/Field-Command";
    }

    @Override
    public boolean checkForPlayer(Player p) {
        return !GeneratorUtils.checkForNoWorldEditSelection(p);

        /* Only needed if block checks are made afterwards like in House Generator
        if (getPlayerSettings().get(p.getUniqueId()).getBlocks() == null)
            getPlayerSettings().get(p.getUniqueId()).setBlocks(GeneratorUtils.analyzeRegion(p, p.getWorld()));*/
    }

    @Override
    public void generate(Player p) {
        if (!checkForPlayer(p))
            return;

        new FieldScripts(p, this);
    }
}

package net.buildtheearth.modules.generator.components.field;

import com.sk89q.worldedit.regions.Region;
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
        if (GeneratorUtils.checkForNoWorldEditSelection(p)) {
            return true;
        }

        if (getPlayerSettings().get(p.getUniqueId()).getBlocks() == null)
            getPlayerSettings().get(p.getUniqueId()).setBlocks(GeneratorUtils.analyzeRegion(p, p.getWorld()));

        return false;
    }

    @Override
    public void generate(Player p) {
        if (!checkForPlayer(p))
            return;

        Region region = GeneratorUtils.getWorldEditSelection(p);

        if(region != null)
            FieldScripts.fieldScript_v_1_0(p, this, region);
    }
}

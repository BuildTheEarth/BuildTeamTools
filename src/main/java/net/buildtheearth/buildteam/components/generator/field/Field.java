package net.buildtheearth.buildteam.components.generator.field;

import com.sk89q.worldedit.regions.Region;
import net.buildtheearth.buildteam.components.generator.Generator;
import net.buildtheearth.buildteam.components.generator.GeneratorModule;
import net.buildtheearth.buildteam.components.generator.GeneratorType;
import org.bukkit.entity.Player;

public class Field extends GeneratorModule {

    public Field() {
        super(GeneratorType.FIELD);
        wikiPage = "https://github.com/BuildTheEarth/BuildTeamTools/wiki/Field-Command";
    }

    @Override
    public boolean checkForNoPlayer(Player p) {
        if (Generator.checkIfWorldEditIsNotInstalled(p))
            return true;

        if (Generator.checkForNoWorldEditSelection(p)) {
            return true;
        }

        if (getPlayerSettings().get(p.getUniqueId()).getBlocks() == null)
            getPlayerSettings().get(p.getUniqueId()).setBlocks(Generator.analyzeRegion(p, p.getWorld()));

        return false;
    }

    @Override
    public void generate(Player p) {
        if (checkForNoPlayer(p)) return;

        Region region = Generator.getWorldEditSelection(p);

        if(region != null)
            FieldScripts.fieldScript_v_1_0(p, this, region);
    }
}

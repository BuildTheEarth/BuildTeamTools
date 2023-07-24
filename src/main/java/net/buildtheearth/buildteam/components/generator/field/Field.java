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
    public boolean checkPlayer(Player p) {
        if(!Generator.checkIfWorldEditIsInstalled(p))
            return false;

        if(!Generator.checkForWorldEditSelection(p) && !Generator.checkForPolySelection(p))
            return false;

        if(getPlayerSettings().get(p.getUniqueId()).getBlocks() == null)
            getPlayerSettings().get(p.getUniqueId()).setBlocks(Generator.analyzeRegion(p, p.getWorld()));

        return true;
    }

    @Override
    public void generate(Player p) {
        if(!checkPlayer(p)) return;

        Region polyRegion = Generator.getWorldEditSelection(p);

        FieldScripts.fieldscript_v_1_0(p, this, polyRegion);
    }
}

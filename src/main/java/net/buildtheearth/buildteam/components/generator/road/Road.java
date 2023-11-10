package net.buildtheearth.buildteam.components.generator.road;

import com.sk89q.worldedit.regions.Region;
import net.buildtheearth.buildteam.components.generator.Generator;
import net.buildtheearth.buildteam.components.generator.GeneratorModule;
import net.buildtheearth.buildteam.components.generator.GeneratorType;
import org.bukkit.entity.Player;

public class Road extends GeneratorModule {
    
    public Road() {
        super(GeneratorType.ROAD);

        wikiPage = "https://github.com/BuildTheEarth/BuildTeamTools/wiki/Road-Command";
    }

    @Override
    public boolean checkForNoPlayer(Player p){

        if(Generator.checkIfWorldEditIsNotInstalled(p))
            return true;

        if(Generator.checkForNoWorldEditSelection(p))
            return true;

        if(getPlayerSettings().get(p.getUniqueId()).getBlocks() == null)
            getPlayerSettings().get(p.getUniqueId()).setBlocks(Generator.analyzeRegion(p, p.getWorld()));

        return false;
    }

    @Override
    public void generate(Player p){
        if(checkForNoPlayer(p))
            return;

        Region region = Generator.getWorldEditSelection(p);

        if(region != null)
            RoadScripts.roadScript_v_2_0(p, this, region);
    }
}

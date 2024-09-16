package net.buildtheearth.modules.generator.components.road;

import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.Region;
import net.buildtheearth.modules.generator.GeneratorModule;
import net.buildtheearth.modules.generator.model.GeneratorComponent;
import net.buildtheearth.modules.generator.model.GeneratorType;
import net.buildtheearth.modules.generator.utils.GeneratorUtils;
import org.bukkit.entity.Player;

public class Road extends GeneratorComponent {

    public Road() {
        super(GeneratorType.ROAD);

        wikiPage = "https://github.com/BuildTheEarth/BuildTeamTools/wiki/Road-Command";
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
        if (!GeneratorModule.getInstance().getRoad().checkForPlayer(p))
            return;

        new RoadScripts(p, this);
    }
}

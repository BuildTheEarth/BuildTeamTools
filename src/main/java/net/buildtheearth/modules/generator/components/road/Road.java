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
    public boolean checkForNoPlayer(Player p) {

        if (GeneratorUtils.checkForNoWorldEditSelection(p))
            return false;

        if (GeneratorUtils.checkForNoConvexSelection(p))
            return false;

        if (getPlayerSettings().get(p.getUniqueId()).getBlocks() == null)
            getPlayerSettings().get(p.getUniqueId()).setBlocks(GeneratorUtils.analyzeRegion(p, p.getWorld()));

        return true;
    }

    @Override
    public void generate(Player p) {
        if (!GeneratorModule.getInstance().getRoad().checkForNoPlayer(p))
            return;

        Region region = GeneratorUtils.getWorldEditSelection(p);

        if (region == null || !(region instanceof ConvexPolyhedralRegion))
            return;

        ConvexPolyhedralRegion convexRegion = (ConvexPolyhedralRegion) region;

        RoadScripts.roadScript_v_2_0(p, this, convexRegion);
    }
}

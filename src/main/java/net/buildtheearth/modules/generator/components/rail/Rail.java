package net.buildtheearth.modules.generator.components.rail;

import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.Region;
import net.buildtheearth.Main;
import net.buildtheearth.modules.generator.GeneratorModule;
import net.buildtheearth.modules.generator.model.GeneratorComponent;
import net.buildtheearth.modules.generator.model.GeneratorType;
import org.bukkit.entity.Player;

public class Rail extends GeneratorComponent {

    public Rail() {
        super(GeneratorType.RAILWAY);

        wikiPage = "https://github.com/BuildTheEarth/BuildTeamTools/wiki/Road-Command";
    }

    @Override
    public boolean checkPlayer(Player p) {
        if (!GeneratorModule.checkIfWorldEditIsInstalled(p))
            return false;

        if (!GeneratorModule.checkForWorldEditSelection(p))
            return false;

        if (!GeneratorModule.checkForConvexSelection(p))
            return false;

        if (getPlayerSettings().get(p.getUniqueId()).getBlocks() == null)
            getPlayerSettings().get(p.getUniqueId()).setBlocks(GeneratorModule.analyzeRegion(p, p.getWorld()));

        return true;
    }

    @Override
    public void generate(Player p) {
        if (!GeneratorModule.getInstance().getRail().checkPlayer(p))
            return;

        Region region = GeneratorModule.getWorldEditSelection(p);

        if (region == null || !(region instanceof ConvexPolyhedralRegion))
            return;

        ConvexPolyhedralRegion convexRegion = (ConvexPolyhedralRegion) region;

        RailScripts.railscript_v_1_3(p, this, convexRegion);

        sendSuccessMessage(p);
    }
}
package net.buildtheearth.modules.generator.components.rail;

import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.Region;
import net.buildtheearth.modules.generator.GeneratorModule;
import net.buildtheearth.modules.generator.model.GeneratorComponent;
import net.buildtheearth.modules.generator.model.GeneratorType;
import net.buildtheearth.modules.generator.utils.GeneratorUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Rail extends GeneratorComponent {

    public Rail() {
        super(GeneratorType.RAILWAY);

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
        if (!GeneratorModule.getInstance().getRail().checkForPlayer(p))
            return;

        new RailScripts(p, this);
    }
}
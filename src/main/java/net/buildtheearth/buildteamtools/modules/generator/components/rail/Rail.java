package net.buildtheearth.buildteamtools.modules.generator.components.rail;

import com.alpsbte.alpslib.utils.GeneratorUtils;
import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import net.buildtheearth.buildteamtools.modules.generator.GeneratorModule;
import net.buildtheearth.buildteamtools.modules.generator.model.GeneratorComponent;
import net.buildtheearth.buildteamtools.modules.generator.model.GeneratorType;
import org.bukkit.entity.Player;

public class Rail extends GeneratorComponent {

    public Rail() {
        super(GeneratorType.RAILWAY);
    }

    @Override
    public boolean checkForPlayer(Player player) {
        if (GeneratorUtils.checkForNoWorldEditSelection(player)) {
            player.sendMessage("§cRail Generator requires an active WorldEdit selection.");
            return false;
        }

        Region region = GeneratorUtils.getWorldEditSelection(player);

        if (!(region instanceof CuboidRegion)
                && !(region instanceof Polygonal2DRegion)
                && !(region instanceof ConvexPolyhedralRegion)) {
            player.sendMessage("§cRail Generator only supports cuboid, polygonal and convex WorldEdit selections.");
            return false;
        }

        return true;
    }

    @Override
    public void generate(Player player) {
        if (!GeneratorModule.getInstance().getRail().checkForPlayer(player))
            return;

        new RailScripts(player, this);
    }
}
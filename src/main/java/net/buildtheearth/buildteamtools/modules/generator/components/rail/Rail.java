package net.buildtheearth.buildteamtools.modules.generator.components.rail;

import com.alpsbte.alpslib.utils.GeneratorUtils;
import com.sk89q.worldedit.regions.Region;
import net.buildtheearth.buildteamtools.modules.generator.GeneratorModule;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.selection.RailSelectionPointReader;
import net.buildtheearth.buildteamtools.modules.generator.model.GeneratorComponent;
import net.buildtheearth.buildteamtools.modules.generator.model.GeneratorType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

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
        RailSelectionPointReader reader = new RailSelectionPointReader(player, region);

        if (!reader.isSupportedSelection()) {
            player.sendMessage("§cRail Generator only supports cuboid, polygonal and convex WorldEdit selections.");
            return false;
        }

        List<Vector> controlPoints = reader.readControlPoints();

        if (controlPoints.size() < 2) {
            player.sendMessage("§cRail Generator could not read enough points from this selection.");
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
package net.buildtheearth.modules.generator.components.road;

import net.buildtheearth.modules.generator.GeneratorModule;
import net.buildtheearth.modules.generator.model.GeneratorComponent;
import net.buildtheearth.modules.generator.model.GeneratorType;
import net.buildtheearth.modules.generator.utils.GeneratorUtils;
import org.bukkit.entity.Player;

public class Road extends GeneratorComponent {

    public Road() {
        super(GeneratorType.ROAD);
    }

    @Override
    public boolean checkForPlayer(Player p) {
        return !GeneratorUtils.checkForNoWorldEditSelection(p);
    }

    @Override
    public void generate(Player p) {
        if (!GeneratorModule.getInstance().getRoad().checkForPlayer(p))
            return;

        new RoadScripts(p, this);
    }
}

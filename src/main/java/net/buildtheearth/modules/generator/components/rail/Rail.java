package net.buildtheearth.modules.generator.components.rail;

import com.alpsbte.alpslib.utils.GeneratorUtils;
import net.buildtheearth.modules.generator.GeneratorModule;
import net.buildtheearth.modules.generator.model.GeneratorComponent;
import net.buildtheearth.modules.generator.model.GeneratorType;
import org.bukkit.entity.Player;

public class Rail extends GeneratorComponent {

    public Rail() {
        super(GeneratorType.RAILWAY);
    }

    @Override
    public boolean checkForPlayer(Player p) {
        return !GeneratorUtils.checkForNoWorldEditSelection(p);
    }

    @Override
    public void generate(Player p) {
        if (!GeneratorModule.getInstance().getRail().checkForPlayer(p))
            return;

        new RailScripts(p, this);
    }
}
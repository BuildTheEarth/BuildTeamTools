package net.buildtheearth.modules.generator.components.house;

import com.sk89q.worldedit.regions.Region;
import net.buildtheearth.modules.generator.GeneratorModule;
import net.buildtheearth.modules.generator.model.GeneratorComponent;
import net.buildtheearth.modules.generator.model.GeneratorType;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class House extends GeneratorComponent {

    public House() {
        super(GeneratorType.HOUSE);
        wikiPage = "https://github.com/BuildTheEarth/BuildTeamTools/wiki/House-Command";
    }

    @Override
    public boolean checkPlayer(Player p) {
        if (!GeneratorModule.checkForWorldEditSelection(p))
            return false;

        if (getPlayerSettings().get(p.getUniqueId()).getBlocks() == null)
            getPlayerSettings().get(p.getUniqueId()).setBlocks(GeneratorModule.analyzeRegion(p, p.getWorld()));

        Block[][][] blocks = getPlayerSettings().get(p.getUniqueId()).getBlocks();

        if (!GeneratorModule.checkForBrickOutline(blocks, p))
            return false;
        return GeneratorModule.checkForWoolBlock(blocks, p);
    }

    @Override
    public void generate(Player p) {
        if (!checkPlayer(p))
            return;

        Region polyRegion = GeneratorModule.getWorldEditSelection(p);

        HouseScripts.buildscript_v_1_2(p, this, polyRegion);

        sendSuccessMessage(p);
    }
}

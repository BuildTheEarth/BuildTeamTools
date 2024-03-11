package net.buildtheearth.modules.generator.components.house;

import com.sk89q.worldedit.regions.Region;
import net.buildtheearth.modules.generator.model.GeneratorComponent;
import net.buildtheearth.modules.generator.model.GeneratorType;
import net.buildtheearth.modules.generator.utils.GeneratorUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class House extends GeneratorComponent {

    public House() {
        super(GeneratorType.HOUSE);
        wikiPage = "https://github.com/BuildTheEarth/BuildTeamTools/wiki/House-Command";
    }

    @Override
    public boolean checkForPlayer(Player p) {
        if (GeneratorUtils.checkForNoWorldEditSelection(p))
            return false;

        if (getPlayerSettings().get(p.getUniqueId()).getBlocks() == null)
            getPlayerSettings().get(p.getUniqueId()).setBlocks(GeneratorUtils.analyzeRegion(p, p.getWorld()));

        Block[][][] blocks = getPlayerSettings().get(p.getUniqueId()).getBlocks();

        if (!GeneratorUtils.checkForBrickOutline(blocks, p))
            return false;

        return GeneratorUtils.checkForWoolBlock(blocks, p);
    }

    @Override
    public void generate(Player p) {
        if (!checkForPlayer(p))
            return;

        Region polyRegion = GeneratorUtils.getWorldEditSelection(p);

        HouseScripts.buildscript_v_1_2(p, this, polyRegion);

        sendSuccessMessage(p);
    }
}

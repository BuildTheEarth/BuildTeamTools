package net.buildtheearth.buildteam.components.generator.house;

import com.sk89q.worldedit.regions.Region;
import net.buildtheearth.buildteam.components.generator.Generator;
import net.buildtheearth.buildteam.components.generator.GeneratorModule;
import net.buildtheearth.buildteam.components.generator.GeneratorType;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class House extends GeneratorModule {

    public House() {
        super(GeneratorType.HOUSE);
        wikiPage = "https://github.com/BuildTheEarth/BuildTeamTools/wiki/House-Command";
    }

    @Override
    public boolean checkForNoPlayer(Player p){
        if(Generator.checkForNoWorldEditSelection(p)) return true;

        if(getPlayerSettings().get(p.getUniqueId()).getBlocks() == null)
            getPlayerSettings().get(p.getUniqueId()).setBlocks(Generator.analyzeRegion(p, p.getWorld()));

        Block[][][] blocks = getPlayerSettings().get(p.getUniqueId()).getBlocks();

        if(!Generator.checkForBrickOutline(blocks, p)) return true;
        return !Generator.checkForWoolBlock(blocks, p);
    }

    @Override
    public void generate(Player p){
        if(checkForNoPlayer(p)) return;

        Region region = Generator.getWorldEditSelection(p);

        if(region != null)
            HouseScripts.buildscript_v_1_2(p, this, region);
    }
}

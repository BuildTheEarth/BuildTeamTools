package net.buildtheearth.buildteam.components.generator.rail;

import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.Region;
import net.buildtheearth.Main;
import net.buildtheearth.buildteam.components.generator.Generator;
import net.buildtheearth.buildteam.components.generator.GeneratorModule;
import net.buildtheearth.buildteam.components.generator.GeneratorType;
import org.bukkit.entity.Player;

public class Rail extends GeneratorModule {

    public Rail() {
        super(GeneratorType.RAILWAY);

        wikiPage = "https://github.com/BuildTheEarth/BuildTeamTools/wiki/Road-Command";
    }

    @Override
    public boolean checkForNoPlayer(Player p) {
        if(Generator.checkIfWorldEditIsNotInstalled(p))
            return true;

        if(Generator.checkForNoWorldEditSelection(p))
            return true;

        if(Generator.checkForNoConvexSelection(p))
            return true;

        if(getPlayerSettings().get(p.getUniqueId()).getBlocks() == null)
            getPlayerSettings().get(p.getUniqueId()).setBlocks(Generator.analyzeRegion(p, p.getWorld()));

        return false;
    }

    @Override
    public void generate(Player p){
        if(Main.getBuildTeam().getGenerator().getRail().checkForNoPlayer(p))
            return;

        Region region = Generator.getWorldEditSelection(p);

        if(!(region instanceof ConvexPolyhedralRegion))
            return;

        ConvexPolyhedralRegion convexRegion = (ConvexPolyhedralRegion) region;

        RailScripts.railScript_v_1_3(p, this, convexRegion);

        sendSuccessMessage(p);
    }
}
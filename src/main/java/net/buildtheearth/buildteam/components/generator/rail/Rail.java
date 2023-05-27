package net.buildtheearth.buildteam.components.generator.rail;

import com.sk89q.worldedit.regions.Region;
import net.buildtheearth.Main;
import net.buildtheearth.buildteam.BuildTeamTools;
import net.buildtheearth.buildteam.components.generator.Generator;
import net.buildtheearth.buildteam.components.generator.GeneratorModule;
import net.buildtheearth.buildteam.components.generator.GeneratorType;
import net.buildtheearth.buildteam.components.generator.road.RoadFlag;
import net.buildtheearth.buildteam.components.generator.road.RoadScripts;
import net.buildtheearth.buildteam.components.generator.road.RoadSettings;
import org.bukkit.entity.Player;

import java.util.HashMap;


public class Rail extends GeneratorModule {

    public Rail() {
        super(GeneratorType.RAILWAY);

        WIKI_PAGE = "https://github.com/BuildTheEarth/BuildTeamTools/wiki/Road-Command";
    }

    @Override
    public void analyzeCommand(Player p, String[] args){

        if(args.length == 2)
            if(args[1].equals("info") || args[1].equals("help") || args[1].equals("?")) {
                sendHelp(p);
                return;
            }

        /** Conversion:
         *
         * Command: /gen rail -m 123:12 -r 456:78
         * args: ["-m", "123:12", "-lm", "456:78"]
         * RailSettings:
         *  RAIL_MATERIAL: 123:12
         *  MARKING_MATERIAL:  456:78
         */

        getPlayerSettings().put(p.getUniqueId(), new RailSettings(p));

        for(String flag : Generator.convertArgsToFlags(args)){
            String[] flagAndValue = Generator.convertToFlagAndValue(flag, p);
            String flagName = flagAndValue[0];
            String flagValue = flagAndValue[1];

            if(flagName == null)
                continue;

            RailFlag railFlag = RailFlag.byString(flagName);

            if(railFlag == null)
                continue;
            if(!(getPlayerSettings().get(p.getUniqueId()) instanceof RailSettings))
                continue;

            RailSettings settings = (RailSettings) getPlayerSettings().get(p.getUniqueId());
            settings.setValue(railFlag, flagValue);
        }

        if(getPlayerSettings().get(p.getUniqueId()).getValues().size() == 0 && args.length > 1){
            sendHelp(p);
            return;
        }

        generate(p);
    }

    @Override
    public boolean checkPlayer(Player p) {
        if(!Generator.checkIfWorldEditIsInstalled(p))
            return false;

        if(!Generator.checkForWorldEditSelection(p))
            return false;

        if(getPlayerSettings().get(p.getUniqueId()).getBlocks() == null)
            getPlayerSettings().get(p.getUniqueId()).setBlocks(Generator.analyzeRegion(p, p.getWorld()));

        return true;
    }

    @Override
    public void generate(Player p){
        if(!Main.getBuildTeam().getGenerator().getRail().checkPlayer(p))
            return;

        HashMap<Object, String> flags = getPlayerSettings().get(p.getUniqueId()).getValues();

        int laneCount = Integer.parseInt(flags.get(RailFlag.LANE_COUNT));

        RailScripts.railscript_1_2_beta(p);


        String command = "/gen rail";
        for(Object object : flags.keySet()) {
            if (!(object instanceof RailFlag))
                continue;

            RailFlag railFlag = (RailFlag) object;
            command += " -" + railFlag.getFlag() + " " + flags.get(railFlag);
        }

        sendSuccessMessage(p, command);
    }
}
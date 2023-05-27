package net.buildtheearth.buildteam.components.generator.road;

import com.sk89q.worldedit.regions.Region;
import net.buildtheearth.Main;
import net.buildtheearth.buildteam.BuildTeamTools;
import net.buildtheearth.buildteam.components.generator.Generator;
import net.buildtheearth.buildteam.components.generator.GeneratorModule;
import net.buildtheearth.buildteam.components.generator.GeneratorType;
import net.buildtheearth.buildteam.components.generator.house.*;
import net.buildtheearth.utils.Item;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class Road extends GeneratorModule {
    
    public Road() {
        super(GeneratorType.ROAD);

        WIKI_PAGE = "https://github.com/BuildTheEarth/BuildTeamTools/wiki/Road-Command";
    }


    public void analyzeCommand(Player p, String[] args){

        if(args.length == 2)
            if(args[1].equals("info") || args[1].equals("help") || args[1].equals("?")) {
                sendHelp(p);
                return;
            }

        /** Conversion:
         *
         * Command: /gen road -m 123:12 -r 456:78
         * args: ["-m", "123:12", "-lm", "456:78"]
         * RoadSettings:
         *  ROAD_MATERIAL: 123:12
         *  MARKING_MATERIAL:  456:78
         */

        getPlayerSettings().put(p.getUniqueId(), new RoadSettings(p));

        for(String flag : Generator.convertArgsToFlags(args)){
            String[] flagAndValue = Generator.convertToFlagAndValue(flag, p);
            String flagName = flagAndValue[0];
            String flagValue = flagAndValue[1];

            if(flagName == null)
                continue;

            RoadFlag roadFlag = RoadFlag.byString(flagName);

            if(roadFlag == null)
                continue;
            if(!(getPlayerSettings().get(p.getUniqueId()) instanceof RoadSettings))
                continue;

            RoadSettings settings = (RoadSettings) getPlayerSettings().get(p.getUniqueId());
            settings.setValue(roadFlag, flagValue);
        }

        if(getPlayerSettings().get(p.getUniqueId()).getValues().size() == 0 && args.length > 1){
            sendHelp(p);
            return;
        }

        generate(p);
    }

    public boolean checkPlayer(Player p){

        if(!Generator.checkIfWorldEditIsInstalled(p))
            return false;

        if(!Generator.checkForWorldEditSelection(p))
            return false;

        if(getPlayerSettings().get(p.getUniqueId()).getBlocks() == null)
            getPlayerSettings().get(p.getUniqueId()).setBlocks(Generator.analyzeRegion(p, p.getWorld()));

        return true;
    }


    public void generate(Player p){
        if(!Main.getBuildTeam().getGenerator().getRoad().checkPlayer(p))
            return;

        HashMap<Object, String> flags = getPlayerSettings().get(p.getUniqueId()).getValues();

        String roadMaterial = flags.get(RoadFlag.ROAD_MATERIAL);
        String markingMaterial = flags.get(RoadFlag.MARKING_MATERIAL);
        String sidewalkMaterial = flags.get(RoadFlag.SIDEWALK_MATERIAL);

        int laneCount = Integer.parseInt(flags.get(RoadFlag.LANE_COUNT));
        int laneWidth = Integer.parseInt(flags.get(RoadFlag.LANE_WIDTH));
        int laneGap = Integer.parseInt(flags.get(RoadFlag.LANE_GAP));
        int markingLength = Integer.parseInt(flags.get(RoadFlag.MARKING_LENGTH));
        int markingGap = Integer.parseInt(flags.get(RoadFlag.MARKING_GAP));
        int sidewalkWidth = Integer.parseInt(flags.get(RoadFlag.SIDEWALK_WIDTH));


        RoadScripts.roadscript_v_1_3(p, roadMaterial, markingMaterial, sidewalkMaterial, laneCount, laneWidth, laneGap,markingLength, markingGap, sidewalkWidth);


        String command = "/gen road";
        for(Object object : flags.keySet()) {
            if (!(object instanceof RoadFlag))
                continue;

            RoadFlag roadFlag = (RoadFlag) object;
            command += " -" + roadFlag.getFlag() + " " + flags.get(roadFlag);
        }

        sendSuccessMessage(p, command);
    }
}

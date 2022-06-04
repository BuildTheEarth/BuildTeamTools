package net.buildtheearth.buildteam.components.generator.road;

import com.sk89q.worldedit.regions.Region;
import net.buildtheearth.buildteam.BuildTeam;
import net.buildtheearth.buildteam.components.generator.Generator;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;

public class Road {

    public static String ROAD_WIKI = "https://github.com/BuildTheEarth/BuildTeamTools/wiki/Road-Command";
    public static String INSTALL_WIKI = "https://github.com/BuildTheEarth/BuildTeamTools/wiki/Installation";

    // Default road generation values
    //TODO: move into config file
    private final static String def_lane_count = "1";
    private final static String def_lane_width = "4";
    private final static String def_road_material = "252:7";
    private final static String def_lane_gap = "0";
    private final static String def_marking_length = "3";
    private final static String def_marking_gap = "5";
    private final static String def_marking_material = "251";
    private final static String def_sidewalk_width = "1";
    private final static String def_sidewalk_material = "42";

    public static void analyzeCommand(Player p, String[] args){

        if(args.length >= 2)
            if(args[1].equals("info") || args[1].equals("help") || args[1].equals("?")) {
                sendHelp(p);
                return;
            }


        /* Conversion:
         *
         * Command: /gen road -w 123:12 -r 456:78
         * args: ["-w", "123:12", "-r", "456:78"]
         * HashMap:
         *  WALL_COLOR: 123:12
         *  ROOF_TYPE:  456:78
         */

        HashMap<RoadFlag, String> flags = new HashMap<>();

        String argsString = " " + StringUtils.join(Arrays.copyOfRange(args, 1, args.length), " ");
        String[] argsArray = argsString.split(" -");
        String[] flagsArray = Arrays.copyOfRange(argsArray, 1, argsArray.length);


        for(String flagAndValue : flagsArray){
            String[] values = flagAndValue.split(" ");
            String flagName = values[0];
            String flagValue = StringUtils.join(Arrays.copyOfRange(values, 1, values.length), " ");

            RoadFlag roadFlag = RoadFlag.byString(flagName);

            if(roadFlag == null) {
                p.sendMessage("§cInvalid flag: -" + flagName);
                sendMoreInfo(p);
                return;
            }


            flags.put(roadFlag, flagValue);
        }

        if(flags.size() == 0 && args.length > 1){
            sendHelp(p);
            return;
        }

        generate(p, flags);
    }

    public static void sendHelp(Player p){
        //TODO send road help
        p.sendMessage("TODO send road help");
    }

    public static void sendMoreInfo(Player p){
        p.sendMessage(" ");
        p.sendMessage("§cFor more information take a look at the wiki:");
        p.sendMessage("§c" + ROAD_WIKI);
    }

    public static void sendError(Player p){
        p.sendMessage("§cThere was an error while generating the road. Please contact the admins");
    }

    public static void generate(Player p, HashMap<RoadFlag, String> flags){
        // Check if WorldEdit is enabled
        if(!BuildTeam.DependencyManager.isWorldEditEnabled()){
            p.sendMessage("§cPlease install WorldEdit to use this tool.");
            sendMoreInfo(p);
        }

        // Get WorldEdit selection of player
        Region plotRegion = Generator.getWorldEditSelection(p);

        if(plotRegion == null){
            p.sendMessage("§cPlease make a WorldEdit Selection first.");
            sendMoreInfo(p);
        }



        // Read the flags and convert the values

        // Fixed values if not set
        if(!flags.containsKey(RoadFlag.LANE_COUNT))
            flags.put(RoadFlag.LANE_COUNT, def_lane_count);
        if(!flags.containsKey(RoadFlag.LANE_WIDTH))
            flags.put(RoadFlag.LANE_WIDTH, def_lane_width);
        if(!flags.containsKey(RoadFlag.ROAD_MATERIAL))
            flags.put(RoadFlag.ROAD_MATERIAL, def_road_material);
        if(!flags.containsKey(RoadFlag.LANE_GAP))
            flags.put(RoadFlag.LANE_GAP, def_lane_gap);

        if(!flags.containsKey(RoadFlag.MARKING_LENGTH))
            flags.put(RoadFlag.MARKING_LENGTH, def_marking_length);
        if(!flags.containsKey(RoadFlag.MARKING_GAP))
            flags.put(RoadFlag.MARKING_GAP, def_marking_gap);
        if(!flags.containsKey(RoadFlag.MARKING_MATERIAL))
            flags.put(RoadFlag.MARKING_MATERIAL, def_marking_material);

        if(!flags.containsKey(RoadFlag.SIDEWALK_WIDTH))
            flags.put(RoadFlag.SIDEWALK_WIDTH, def_sidewalk_width);
        if(!flags.containsKey(RoadFlag.SIDEWALK_MATERIAL))
            flags.put(RoadFlag.SIDEWALK_MATERIAL, def_sidewalk_material);



        int laneCount = Integer.parseInt(flags.get(RoadFlag.LANE_COUNT));
        int laneWidth = Integer.parseInt(flags.get(RoadFlag.LANE_WIDTH));
        int laneGap = Integer.parseInt(flags.get(RoadFlag.LANE_GAP));
        int markingLength = Integer.parseInt(flags.get(RoadFlag.MARKING_LENGTH));
        int markingGap = Integer.parseInt(flags.get(RoadFlag.MARKING_GAP));
        int sidewalkWidth = Integer.parseInt(flags.get(RoadFlag.SIDEWALK_WIDTH));





        RoadScripts.roadscript_v_1_3(p,
                flags.get(RoadFlag.ROAD_MATERIAL),
                flags.get(RoadFlag.MARKING_MATERIAL),
                flags.get(RoadFlag.SIDEWALK_MATERIAL),
                laneCount,
                laneWidth,
                laneGap,
                markingLength,
                markingGap,
                sidewalkWidth);

    }


}


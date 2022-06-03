package net.buildtheearth.buildteam.components.generator.house;

import com.sk89q.worldedit.regions.Region;
import net.buildtheearth.buildteam.BuildTeam;
import net.buildtheearth.buildteam.components.generator.Generator;
import net.buildtheearth.utils.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;

public class House {

    public static String HOUSE_WIKI = "https://github.com/BuildTheEarth/BuildTeamTools/wiki/House-Command";
    public static String INSTALL_WIKI = "https://github.com/BuildTheEarth/BuildTeamTools/wiki/Installation";


    public static void analyzeCommand(Player p, String[] args){

        if(args.length == 2)
            if(args[1].equals("info") || args[1].equals("help") || args[1].equals("?")) {
                sendHelp(p);
                return;
            }


        /** Conversion:
         *
         * Command: /gen house -w 123:12 -r 456:78
         * args: ["-w", "123:12", "-r", "456:78"]
         * HashMap:
         *  WALL_COLOR: 123:12
         *  ROOF_TYPE:  456:78
         */

        HashMap<HouseFlag, String> flags = new HashMap<>();

        String argsString = " " + StringUtils.join(Arrays.copyOfRange(args, 1, args.length), " ");
        String[] argsArray = argsString.split(" -");
        String[] flagsArray = Arrays.copyOfRange(argsArray, 1, argsArray.length);


        for(String flagAndValue : flagsArray){
            String[] values = flagAndValue.split(" ");
            String flagName = values[0];
            String flagValue = StringUtils.join(Arrays.copyOfRange(values, 1, values.length), " ");

            HouseFlag houseFlag = HouseFlag.byString(flagName);

            if(houseFlag == null)
                continue;

            flags.put(houseFlag, flagValue);
        }

        if(flags.size() == 0 && args.length > 1){
            sendHelp(p);
            return;
        }

        generate(p, flags);
    }

    public static void sendHelp(Player p){
        //TODO send houses help
        p.sendMessage("TODO send Houses Help");
    }

    public static void sendMoreInfo(Player p){
           p.sendMessage(" ");
           p.sendMessage("§cFor more information take a look at the wiki:");
           p.sendMessage("§c" + HOUSE_WIKI);
    }

    public static void sendError(Player p){
        p.sendMessage("§cThere was an error while generating the house. Please contact the admins");
    }

    public static void generate(Player p, HashMap<HouseFlag, String> flags){
        // Check if WorldEdit is enabled
        if(!BuildTeam.DependencyManager.isWorldEditEnabled()){
            p.sendMessage("§cPlease install WorldEdit to use this tool.");
            p.sendMessage(" ");
            p.sendMessage("§cFor more installation help take a look at the wiki:");
            p.sendMessage("§c" + INSTALL_WIKI);
        }

        // Get WorldEdit selection of player
        Region plotRegion = Generator.getWorldEditSelction(p);

        if(plotRegion == null){
            p.sendMessage("§cPlease make a WorldEdit Selection first.");
            sendMoreInfo(p);
        }




        // Read the flags and convert the values

        // Random values if not set
        if(!flags.containsKey(HouseFlag.ROOF_TYPE)){
            RoofType roofType = (RoofType) Utils.pickRandom(RoofType.values());
            flags.put(HouseFlag.ROOF_TYPE, roofType.getType());
        }
        if(!flags.containsKey(HouseFlag.WALL_COLOR)){
            ItemStack block = (ItemStack) Utils.pickRandom(Utils.WALL_BLOCKS);
            flags.put(HouseFlag.WALL_COLOR, Utils.getBlockID(block));
        }
        if(!flags.containsKey(HouseFlag.BASE_COLOR)){
            ItemStack block = (ItemStack) Utils.pickRandom(Utils.WALL_BLOCKS);
            flags.put(HouseFlag.BASE_COLOR, Utils.getBlockID(block));
        }
        if(!flags.containsKey(HouseFlag.ROOF_COLOR)){
            ItemStack block = (ItemStack) Utils.pickRandom(Utils.STAIRS);

            if(flags.get(HouseFlag.ROOF_TYPE).equalsIgnoreCase(RoofType.SLABS.getType()))
                block = (ItemStack) Utils.pickRandom(Utils.SLABS);
            else if(flags.get(HouseFlag.ROOF_TYPE).equalsIgnoreCase(RoofType.FLAT.getType()))
                block = (ItemStack) Utils.pickRandom(Utils.SLABS);

            flags.put(HouseFlag.ROOF_COLOR, Utils.getBlockID(block));
        }
        if(!flags.containsKey(HouseFlag.FLOOR_COUNT)){
            flags.put(HouseFlag.FLOOR_COUNT, "" + ((int)(Math.random()*3.0) + 1));
        }

        // Fixed values if not set
        if(!flags.containsKey(HouseFlag.WINDOW_COLOR))
            flags.put(HouseFlag.WINDOW_COLOR, "95:15");
        if(!flags.containsKey(HouseFlag.FLOOR_HEIGHT))
            flags.put(HouseFlag.FLOOR_HEIGHT, "3");
        if(!flags.containsKey(HouseFlag.BASE_HEIGHT))
            flags.put(HouseFlag.BASE_HEIGHT, "1");
        if(!flags.containsKey(HouseFlag.WINDOW_HEIGHT))
            flags.put(HouseFlag.WINDOW_HEIGHT, "2");
        if(!flags.containsKey(HouseFlag.WINDOW_WIDTH))
            flags.put(HouseFlag.WINDOW_WIDTH, "2");
        if(!flags.containsKey(HouseFlag.WINDOW_DISTANCE))
            flags.put(HouseFlag.WINDOW_DISTANCE, "2");
        if(!flags.containsKey(HouseFlag.MAX_ROOF_HEIGHT))
            flags.put(HouseFlag.MAX_ROOF_HEIGHT, "10");



        String wallColor = flags.get(HouseFlag.WALL_COLOR);
        String roofColor = flags.get(HouseFlag.ROOF_COLOR);
        String baseColor = flags.get(HouseFlag.BASE_COLOR);
        String windowColor = flags.get(HouseFlag.WINDOW_COLOR);
        RoofType roofType = RoofType.byString(flags.get(HouseFlag.ROOF_TYPE));

        int floorCount = Integer.parseInt(flags.get(HouseFlag.FLOOR_COUNT));
        int floorHeight = Integer.parseInt(flags.get(HouseFlag.FLOOR_HEIGHT));
        int baseHeight = Integer.parseInt(flags.get(HouseFlag.BASE_HEIGHT));
        int windowHeight = Integer.parseInt(flags.get(HouseFlag.WINDOW_HEIGHT));
        int windowWidth = Integer.parseInt(flags.get(HouseFlag.WINDOW_WIDTH));
        int windowDistance = Integer.parseInt(flags.get(HouseFlag.WINDOW_DISTANCE));
        int maxRoofHeight = Integer.parseInt(flags.get(HouseFlag.MAX_ROOF_HEIGHT));




        HouseScripts.buildscript_v_1_2(p,wallColor,roofColor,baseColor,windowColor,roofType,floorCount,floorHeight,baseHeight,windowHeight,windowWidth,windowDistance,maxRoofHeight);
    }


}

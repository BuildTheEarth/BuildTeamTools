package net.buildtheearth.buildteam.components.generator.house;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import net.buildtheearth.buildteam.BuildTeam;
import net.buildtheearth.buildteam.components.generator.Generator;
import net.buildtheearth.utils.MenuItems;
import net.buildtheearth.utils.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

public class House {

    public static String WIKI_PAGE = "https://github.com/MineFact/BuildTeamPlugin/blob/master/wiki/generator/houses.md";

    public static HashMap<UUID, HouseSettings> playerHouseSettings = new HashMap<>();


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

        playerHouseSettings.put(p.getUniqueId(), new HouseSettings(p));

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

            playerHouseSettings.get(p).setValue(houseFlag, flagValue);
        }

        if(playerHouseSettings.get(p).getValues().size() == 0 && args.length > 1){
            sendHelp(p);
            return;
        }

        generate(p);
    }

    public static void sendHelp(Player p){
        //TODO send houses help
        p.sendMessage("TODO send Houses Help");
    }

    public static void sendMoreInfo(Player p){
           p.sendMessage(" ");
           p.sendMessage("§cFor more information take a look at the wiki:");
           p.sendMessage("§c" + WIKI_PAGE);
    }

    public static void sendError(Player p){
        p.sendMessage("§cThere was an error while generating the house. Please contact the admins");
    }

    public static void generate(Player p){
        HashMap<HouseFlag, String> flags = playerHouseSettings.get(p.getUniqueId()).getValues();

        // Check if WorldEdit is enabled
        if(!BuildTeam.DependencyManager.isWorldEditEnabled()){
            p.sendMessage("§cPlease install WorldEdit to use this tool.");
            sendMoreInfo(p);
        }

        // Get WorldEdit selection of player
        Region plotRegion = Generator.getWorldEditSelction(p);

        if(plotRegion == null){
            p.sendMessage("§cPlease make a WorldEdit Selection first.");
            sendMoreInfo(p);
        }


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

        p.sendMessage(BuildTeam.PREFIX + "Building §asuccessfully §7generated.");
    }


}

package net.buildtheearth.buildteam.components.generator.road;

import com.sk89q.worldedit.regions.Region;
import net.buildtheearth.buildteam.BuildTeamTools;
import net.buildtheearth.buildteam.components.generator.Generator;
import net.buildtheearth.buildteam.components.generator.house.*;
import net.buildtheearth.utils.Item;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class Road {

    public static String WIKI_PAGE = "https://github.com/BuildTheEarth/BuildTeamTools/wiki/Road-Command";

    public static HashMap<UUID, RoadSettings> playerRoadSettings = new HashMap<>();


    public static void analyzeCommand(Player p, String[] args){

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

        playerRoadSettings.put(p.getUniqueId(), new RoadSettings(p));

        String argsString = " " + StringUtils.join(Arrays.copyOfRange(args, 1, args.length), " ");
        String[] argsArray = argsString.split(" -");
        String[] flagsArray = Arrays.copyOfRange(argsArray, 1, argsArray.length);

        for(String flagAndValue : flagsArray){
            String[] values = flagAndValue.split(" ");
            String flagName = values[0];
            String flagValue;

            try {
                flagValue = StringUtils.join(Arrays.copyOfRange(values, 1, values.length), " ");
            } catch (ArrayIndexOutOfBoundsException e) {
                p.sendMessage("§cInvalid flag value: -" + flagName + "§n§c ???");
                return;
            }

            RoadFlag roadFlag = RoadFlag.byString(flagName);

            if(roadFlag == null)
                continue;

            playerRoadSettings.get(p.getUniqueId()).setValue(roadFlag, flagValue);
        }

        if(playerRoadSettings.get(p.getUniqueId()).getValues().size() == 0 && args.length > 1){
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

    public static boolean checkPlayer(Player p){

        // Check if WorldEdit is enabled
        if(!BuildTeamTools.DependencyManager.isWorldEditEnabled()){
            p.sendMessage("§cPlease install WorldEdit to use this tool.");
            sendMoreInfo(p);
            return false;
        }

        // Get WorldEdit selection of player
        Region plotRegion = Generator.getWorldEditSelection(p);

        if(plotRegion == null){
            p.sendMessage("§cPlease make a WorldEdit Selection first.");
            p.closeInventory();
            p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            House.sendMoreInfo(p);
            return false;
        }

        return true;
    }


    public static void generate(Player p){
        HashMap<RoadFlag, String> flags = playerRoadSettings.get(p.getUniqueId()).getValues();

        if(!Road.checkPlayer(p))
            return;


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
        for(RoadFlag roadFlag : flags.keySet())
            command += " -" + roadFlag.getFlag() + " " + flags.get(roadFlag);


        p.sendMessage(" ");
        p.sendMessage(" ");
        p.sendMessage(" ");

        TextComponent tc = new TextComponent(BuildTeamTools.PREFIX + "Road §asuccessfully §7generated. §e[Copy Command]");
        tc.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
        tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to copy command").create()));

        p.spigot().sendMessage(tc);

        p.sendMessage(" ");
        p.sendMessage("§cNote: You can undo the edit with /gen undo.");



    }
}

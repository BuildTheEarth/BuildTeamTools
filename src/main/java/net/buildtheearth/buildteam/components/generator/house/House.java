package net.buildtheearth.buildteam.components.generator.house;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.Region;
import net.buildtheearth.buildteam.BuildTeamTools;
import net.buildtheearth.buildteam.components.generator.Generator;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class House {

    public static String WIKI_PAGE = "https://github.com/BuildTheEarth/BuildTeamTools/wiki/House-Command";

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

            playerHouseSettings.get(p.getUniqueId()).setValue(houseFlag, flagValue);
        }

        if(playerHouseSettings.get(p.getUniqueId()).getValues().size() == 0 && args.length > 1){
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

    /**
     * Checks if polygon region contains a sign and update sign text
     * @param polyRegion WorldEdit region
     * @param world Region world
     * @return true if polygon region contains a sign, false otherwise
     */
    public static boolean containsBlock(Region polyRegion, World world, Material blockType, byte data) {
        boolean hasBlock = false;
        for (int i = polyRegion.getMinimumPoint().getBlockX(); i <= polyRegion.getMaximumPoint().getBlockX(); i++)
        for (int j = polyRegion.getMinimumPoint().getBlockY(); j <= polyRegion.getMaximumPoint().getBlockY(); j++)
        for (int k = polyRegion.getMinimumPoint().getBlockZ(); k <= polyRegion.getMaximumPoint().getBlockZ(); k++)
            if (polyRegion.contains(new Vector(i, j, k))) {
                Block block = world.getBlockAt(i, j, k);
                if(block.getType() == blockType && (data == 0 || block.getData() == data)) {
                    hasBlock = true;
                }
            }

        return hasBlock;
    }

    public static void generate(Player p){
        HashMap<HouseFlag, String> flags = playerHouseSettings.get(p.getUniqueId()).getValues();

        // Check if WorldEdit is enabled
        if(!BuildTeamTools.DependencyManager.isWorldEditEnabled()){
            p.sendMessage("§cPlease install WorldEdit to use this tool.");
            sendMoreInfo(p);
        }

        // Get WorldEdit selection of player
        Region plotRegion = Generator.getWorldEditSelection(p);

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

        String command = "/gen house";
        for(HouseFlag houseFlag : flags.keySet())
            command += " -" + houseFlag.getFlag() + " " + flags.get(houseFlag);


        p.sendMessage(" ");
        p.sendMessage(" ");
        p.sendMessage(" ");

        TextComponent tc = new TextComponent(BuildTeamTools.PREFIX + "Building §asuccessfully §7generated. §e[Copy Command]");
        tc.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
        tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to copy command").create()));

        p.spigot().sendMessage(tc);

        p.sendMessage(" ");
        p.sendMessage("§cNote: You can undo the edit with /gen undo.");



    }


}

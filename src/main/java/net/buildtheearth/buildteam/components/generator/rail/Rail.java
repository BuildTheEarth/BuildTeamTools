package net.buildtheearth.buildteam.components.generator.rail;

import com.sk89q.worldedit.regions.Region;
import net.buildtheearth.buildteam.BuildTeam;
import net.buildtheearth.buildteam.components.generator.Generator;
import org.bukkit.entity.Player;


public class Rail {
    public static String RAIL_WIKI = "https://github.com/BuildTheEarth/BuildTeamTools/wiki/Rail-Command";
    public static String INSTALL_WIKI = "https://github.com/BuildTheEarth/BuildTeamTools/wiki/Installation";


    public static void analyzeCommand(Player p, String[] args){

        if(args.length >= 2)
            if(args[1].equals("info") || args[1].equals("help") || args[1].equals("?")) {
                sendHelp(p);
                return;
            }


        generate(p);
    }

    public static void sendHelp(Player p){
        //TODO send rail help
        p.sendMessage("TODO send rail Help");
    }

    public static void sendMoreInfo(Player p){
        p.sendMessage(" ");
        p.sendMessage("§cFor more information, check out the wiki:");
        p.sendMessage("§c" + RAIL_WIKI);
    }

    public static void generate(Player p) {
        // Check if WorldEdit is enabled
        if (!BuildTeam.DependencyManager.isWorldEditEnabled()) {
            p.sendMessage("§cPlease install WorldEdit to use this tool.");
            p.sendMessage(" ");
            p.sendMessage("§cFor more installation help, please see the wiki:");
            p.sendMessage("§c" + INSTALL_WIKI);
            return;
        }

        // Get WorldEdit selection of player
        Region plotRegion = Generator.getWorldEditSelection(p);
        if (plotRegion == null) {
            p.sendMessage("§cPlease make a WorldEdit Selection first.");
            sendMoreInfo(p);
        }

        RailScripts.railscript_1_2_beta(p);

    }
}
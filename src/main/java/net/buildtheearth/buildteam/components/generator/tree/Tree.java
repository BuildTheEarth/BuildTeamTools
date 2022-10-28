package net.buildtheearth.buildteam.components.generator.tree;

import net.buildtheearth.buildteam.BuildTeamTools;
import net.buildtheearth.utils.ChatUtil;
import org.bukkit.entity.Player;

public class Tree {

    private static String TREE_WIKI = "placeholder";


    public static void analyzeCommand(Player p, String[] args) {


        if(args.length >= 2) {
            if (args[1].equals("info") || args[1].equals("help") || args[1].equals("?")) {
                sendHelp(p);
                return;
            }
            if(args.length > 4) {
                sendHelp(p);
                return;
            }
        }

        if(args.length >= 2) {
            String type = args[1];

            if(args.length == 2) {
                if(type.equalsIgnoreCase("any")) {
                    p.chat("//schbr newtrees/*@** -place:bottom -yoff:2");
                } else {
                    p.chat("//schbr newtrees/%"+ type + "%*@** -place:bottom -yoff:2");
                }

            }
            else if(args.length == 3) {
                String scale = args[2];
                if(type.equalsIgnoreCase("any")) {
                    p.chat("//schbr newtrees/*/%" + scale + "%/*@** -place:bottom -yoff:2");
                } else {
                    p.chat("//schbr newtrees/%"+ type + "%/%" + scale + "%/*@** -place:bottom -yoff:2");
                }

            }
            else if(args.length == 4) {
                String scale = args[2];
                String option = args[3];
                if(type.equalsIgnoreCase("any")) {
                    if(scale.equalsIgnoreCase("any")) {
                        p.chat("//schbr newtrees/*/*/*%"+ option +"%/*@** -place:bottom -yoff:2");
                    } else {
                        p.chat("//schbr newtrees/*/%" + scale + "%/*%" + option + "%/*@** -place:bottom -yoff:2");
                    }
                } else {
                    if(scale.equalsIgnoreCase("any")) {
                        p.chat("//schbr newtrees/%" + type + "%/*/*%" + option + "%/*@** -place:bottom -yoff:2");
                    } else {
                        p.chat("//schbr newtrees/%" + type + "%/%" + scale + "%/*%" + option +" %/*@** -place:bottom -yoff:2");
                    }
                }
            }

        }

    }

    public static void sendHelp(Player p){
        //TODO send road help
        p.sendMessage("TODO send road help");
    }

    public static void sendMoreInfo(Player p){
        p.sendMessage(" ");
        p.sendMessage(BuildTeamTools.PREFIX + "For more help, take a look at the wiki:");
        p.sendMessage(BuildTeamTools.PREFIX + "§e§n" + TREE_WIKI);
    }

}

package net.buildtheearth.buildteam.components.generator.tree;

import net.buildtheearth.utils.ChatUtil;
import org.bukkit.entity.Player;

public class Tree {

    private static String TREE_WIKI


    public static void analyzeCommand(Player p, String[] args) {


        if(args.length >= 2)
            if(args[1].equals("info") || args[1].equals("help") || args[1].equals("?")) {
                sendHelp(p);
                return;
            }

        if



    }

    public static void sendHelp(Player p){
        //TODO send road help
        p.sendMessage("TODO send road help");
    }

    public static void sendMoreInfo(Player p){
        p.sendMessage(" ");
        p.sendMessage(ChatUtil.getPrefixMessage() + "For more help, take a look at the wiki:");
        p.sendMessage(ChatUtil.getPrefixMessage() + "§e§n" + TREE_WIKI);
    }

}

package net.buildtheearth.buildteam.components.generator.tree;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.world.registry.WorldData;
import net.buildtheearth.Main;
import net.buildtheearth.buildteam.BuildTeamTools;
import net.buildtheearth.buildteam.components.generator.Generator;
import net.buildtheearth.utils.ChatUtil;
import net.buildtheearth.utils.Item;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Tree {

    private static String TREE_WIKI = "placeholder";
    public static String TREE_PACK_VERSION = "v2.0";


    public static void analyzeCommand(Player p, String[] args) {

        if(!Generator.checkIfTreePackIsInstalled(p,true))
            return;

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

        // In case the player is holding no item, give him a diamond sword
        if(p.getItemOnCursor() == null || p.getInventory().getItemInMainHand().getType() == Material.AIR)
            p.getInventory().setItem(p.getInventory().getHeldItemSlot(), Item.create(Material.DIAMOND_SWORD));


        if(args.length == 1) {
            p.chat("//schbr newtrees/*@** -place:bottom -yoff:2");

        }else if(args.length >= 2) {
            String type = args[1];

            if(args.length == 2) {
                if(type.equalsIgnoreCase("any"))
                    p.chat("//schbr newtrees/*@** -place:bottom -yoff:2");
                else
                    p.chat("//schbr newtrees/" + type + "*@** -place:bottom -yoff:2");

            }else if(args.length == 3) {
                String scale = args[2];

                if(type.equalsIgnoreCase("any"))
                    p.chat("//schbr newtrees/*/" + scale + "/*@** -place:bottom -yoff:2");
                else
                    p.chat("//schbr newtrees/"+ type + "/" + scale + "/*@** -place:bottom -yoff:2");

            }else if(args.length == 4) {
                String scale = args[2];
                String option = args[3];

                if(type.equalsIgnoreCase("any")) {
                    if(scale.equalsIgnoreCase("any"))
                        p.chat("//schbr newtrees/*/*/*"+ option +"/*@** -place:bottom -yoff:2");
                    else
                        p.chat("//schbr newtrees/*/" + scale + "/*" + option + "/*@** -place:bottom -yoff:2");
                } else {
                    if(scale.equalsIgnoreCase("any"))
                        p.chat("//schbr newtrees/" + type + "/*/*" + option + "/*@** -place:bottom -yoff:2");
                    else
                        p.chat("//schbr newtrees/" + type + "/" + scale + "/*" + option +"/*@** -place:bottom -yoff:2");
                }
            }
        }

        TextComponent tc = new TextComponent(BuildTeamTools.PREFIX + "Tree type §asuccessfully §7selected. §e[Copy Command]");
        tc.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, getCommand(p, args)));
        tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Click to copy command").create()));

        p.spigot().sendMessage(tc);

        p.sendMessage(" ");
        p.sendMessage("§cNote: Click to brush your tree. You can undo the edit with //undo.");
    }

    public static String getCommand(Player p, String[] args){
        String command = "/gen ";
        for(String arg : args)
            command += arg + " ";

        return command;
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

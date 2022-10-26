package net.buildtheearth.buildteam.commands;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditOperation;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditAPI;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.adapter.BukkitImplAdapter;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.session.SessionOwner;
import net.buildtheearth.Main;
import net.buildtheearth.buildteam.BuildTeam;
import net.buildtheearth.buildteam.components.generator.Generator;
import net.buildtheearth.buildteam.components.generator.GeneratorMenu;
import net.buildtheearth.buildteam.components.generator.History;
import net.buildtheearth.buildteam.components.generator.house.House;
import net.buildtheearth.buildteam.components.generator.road.Road;
import net.buildtheearth.utils.ChatUtil;
import net.buildtheearth.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.swing.text.DateFormatter;
import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class generate_command implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String cmdlabel, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("§cOnly players can execute this command.");
            return true;
        }

        Player p = (Player) sender;

        // Check if WorldEdit is enabled
        if(!BuildTeam.DependencyManager.isWorldEditEnabled()){
            p.sendMessage("§cPlease install WorldEdit to use this tool.");
            Generator.sendMoreInfo(p);
            p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            return true;
        }



        // Command Usage: /gen
        if(args.length == 0){
            new GeneratorMenu(p);
            return true;
        }

        // Command Usage: /gen house ...
        if(args[0].equals("house")){
            House.analyzeCommand(p, args);
            return true;
        }


        // Command Usage: /gen road ...
        if(args[0].equals("road")){
            Road.analyzeCommand(p, args);
            return true;
        }

        // Command Usage: /gen history
        if(args[0].equals("history")){
            if(Generator.getPlayerHistory(p).getHistoryEntries().size() == 0){
                p.sendMessage("§cYou didn't generate any structures yet. Use /gen to create one.");
                return true;
            }

            ChatUtil.sendMessageBox(sender, "Generator History for " + p.getName(), new Runnable() {
                @Override
                public void run() {
                    for(History.HistoryEntry history : Generator.getPlayerHistory(p).getHistoryEntries()){
                        long timeDifference = System.currentTimeMillis() - history.getTimeCreated();

                        p.sendMessage("§e- " + history.getGeneratorType().name() + " §7-§e " + Utils.toDate(p, timeDifference) + " ago §7-§e " + history.getWorldEditCommandCount() + " Commands executed");
                    }
                }
            });
            return true;
        }

        if(args[0].equals("undo")){
            if(Generator.getPlayerHistory(p).getHistoryEntries().size() == 0){
                p.sendMessage("§cYou didn't generate any structures yet. Use /gen to create one.");
                return true;
            }

            p.chat("//undo 500");
            Generator.getPlayerHistory(p).getHistoryEntries().clear();
            return true;
        }

        sendHelp(p);
        return true;
    }

    public static void sendHelp(Player p){
        // TODO make help pretty
        p.sendMessage("TODO create help message");
    }


}

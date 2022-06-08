package net.buildtheearth.buildteam.commands;

import net.buildtheearth.buildteam.components.generator.house.House;
import net.buildtheearth.buildteam.components.generator.Inventories;
import net.buildtheearth.buildteam.components.generator.rail.Rail;
import net.buildtheearth.buildteam.components.generator.rail.RailScripts;
import net.buildtheearth.buildteam.components.generator.road.Road;
import net.buildtheearth.utils.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class generate_command implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String cmdlabel, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(ChatUtil.getPrefixMessage() + "§cOnly players can execute this command.");
            return true;
        }


        Player p = (Player) sender;

        // Command Usage: /gen
        if(args.length == 0){
            Inventories.openGeneratorInventory(p);
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

        if(args[0].equals("rail")) {
            Rail.analyzeCommand(p, args);
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

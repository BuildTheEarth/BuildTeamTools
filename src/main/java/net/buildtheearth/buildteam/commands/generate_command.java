package net.buildtheearth.buildteam.commands;

import net.buildtheearth.buildteam.components.generator.GeneratorMenu;
import net.buildtheearth.buildteam.components.generator.house.House;
import net.buildtheearth.buildteam.components.generator.road.Road;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class generate_command implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String cmdlabel, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("§cOnly players can execute this command.");
            return true;
        }


        Player p = (Player) sender;

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

        sendHelp(p);
        return true;
    }

    public static void sendHelp(Player p){
        // TODO make help pretty
        p.sendMessage("TODO create help message");
    }
}

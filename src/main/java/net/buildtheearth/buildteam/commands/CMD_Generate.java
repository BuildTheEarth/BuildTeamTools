package net.buildtheearth.buildteam.commands;

import net.buildtheearth.Main;
import net.buildtheearth.buildteam.BuildTeamTools;
import net.buildtheearth.buildteam.components.generator.Generator;
import net.buildtheearth.buildteam.components.generator.GeneratorMenu;
import net.buildtheearth.buildteam.components.generator.History;
import net.buildtheearth.utils.ChatUtil;
import net.buildtheearth.utils.Utils;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMD_Generate implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("§cOnly players can execute this command.");
            return true;
        }

        Player p = (Player) sender;

        // Check if WorldEdit is enabled
        if(BuildTeamTools.DependencyManager.isWorldEditDisabled()){
            p.sendMessage("§cPlease install WorldEdit to use this tool.");
            Generator.sendMoreInfo(p);
            p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            return true;
        }

        // Command Usage: /gen
        if(args.length == 0){
            if(Generator.checkIfWorldEditIsNotInstalled(p)) return true;
            new GeneratorMenu(p, true);
            return true;
        }

        switch (args[0]) {
            case "house": // Command Usage: /gen house ...
                Main.getBuildTeam().getGenerator().getHouse().analyzeCommand(p, args);
                return true;
            case "road": // Command Usage: /gen road ...
                Main.getBuildTeam().getGenerator().getRoad().analyzeCommand(p, args);
                return true;
            case "rail": // Command Usage: /gen rail ...
                Main.getBuildTeam().getGenerator().getRail().analyzeCommand(p, args);
                return true;
            case "tree": // Command Usage: /gen tree ...
                Main.getBuildTeam().getGenerator().getTree().analyzeCommand(p, args);
                return true;
            case "field": // Command Usage: /gen tree ...
                Main.getBuildTeam().getGenerator().getField().analyzeCommand(p, args);
                return true;
            case "history": // Command Usage: /gen history
                if(Generator.getPlayerHistory(p).getHistoryEntries().isEmpty()) {
                    p.sendMessage("§cYou didn't generate any structures yet. Use /gen to create one.");
                    return true;
                }

                ChatUtil.sendMessageBox(sender, "Generator History for " + p.getName(), () -> {
                    for(History.HistoryEntry history : Generator.getPlayerHistory(p).getHistoryEntries()){
                        long timeDifference = System.currentTimeMillis() - history.getTimeCreated();
                        p.sendMessage("§e- " + history.getGeneratorType().name() + " §7-§e " + Utils.toDate(p, timeDifference) + " ago §7-§e " + history.getWorldEditCommandCount() + " Commands executed");
                    }
                });
                return true;
            case "undo":
                if(Generator.getPlayerHistory(p).getHistoryEntries().isEmpty()){
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

    public static void sendHelp(CommandSender sender){
        ChatUtil.sendMessageBox(sender, "Generator Command", () -> {
            sender.sendMessage("§eHouse Generator:§7 /gen house help");
            sender.sendMessage("§eRoad Generator:§7 /gen road help");
            sender.sendMessage("§eRail Generator:§7 /gen rail help");
            sender.sendMessage("§eTree Generator:§7 /gen tree help");
        });
    }
}

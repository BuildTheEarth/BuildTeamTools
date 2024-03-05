package net.buildtheearth.modules.generator.commands;

import net.buildtheearth.modules.generator.GeneratorModule;
import net.buildtheearth.modules.generator.menu.GeneratorMenu;
import net.buildtheearth.modules.generator.model.History;
import net.buildtheearth.modules.utils.ChatHelper;
import net.buildtheearth.modules.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GeneratorCommand implements CommandExecutor {


    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can execute this command.");
            return true;
        }

        Player p = (Player) sender;


        // Command Usage: /gen
        if (args.length == 0) {
            new GeneratorMenu(p);
            return true;
        }


        // Command Usage: /gen house ...
        if (args[0].equals("house")) {
            GeneratorModule.getInstance().getHouse().analyzeCommand(p, args);
            return true;
        }

        // Command Usage: /gen road ...
        if (args[0].equals("road")) {
            GeneratorModule.getInstance().getRoad().analyzeCommand(p, args);
            return true;
        }

        // Command Usage: /gen rail ...
        if (args[0].equals("rail")) {
            GeneratorModule.getInstance().getRail().analyzeCommand(p, args);
            return true;
        }

        // Command Usage: /gen tree ...
        if (args[0].equals("tree")) {
            GeneratorModule.getInstance().getTree().analyzeCommand(p, args);
            return true;
        }


        // Command Usage: /gen history
        if (args[0].equals("history")) {
            if (GeneratorModule.getInstance().getPlayerHistory(p).getHistoryEntries().isEmpty()) {
                p.sendMessage("§cYou didn't generate any structures yet. Use /gen to create one.");
                return true;
            }

            ChatHelper.sendMessageBox(sender, "Generator History for " + p.getName(), () -> {
                for (History.HistoryEntry history : GeneratorModule.getInstance().getPlayerHistory(p).getHistoryEntries()) {
                    long timeDifference = System.currentTimeMillis() - history.getTimeCreated();

                    p.sendMessage("§e- " + history.getGeneratorType().name() + " §7-§e " + Utils.toDate(p, timeDifference) + " ago §7-§e " + history.getWorldEditCommandCount() + " Commands executed");
                }
            });
            return true;
        }

        if (args[0].equals("undo")) {
            GeneratorModule.getInstance().getPlayerHistory(p).undo(p);
            return true;
        }

        if (args[0].equals("redo")) {
            GeneratorModule.getInstance().getPlayerHistory(p).redo(p);
            return true;
        }

        sendHelp(p);
        return true;
    }

    public static void sendHelp(CommandSender sender) {
        ChatHelper.sendMessageBox(sender, "Generator Command", () -> {

            sender.sendMessage("§eHouse Generator:§7 /gen house help");
            sender.sendMessage("§eRoad Generator:§7 /gen road help");
            sender.sendMessage("§eRail Generator:§7 /gen rail help");
            sender.sendMessage("§eTree Generator:§7 /gen tree help");

        });
    }
}

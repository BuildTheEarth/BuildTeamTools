package net.buildtheearth.modules.generator.commands;

import net.buildtheearth.Main;
import net.buildtheearth.modules.generator.Generator;
import net.buildtheearth.modules.generator.menu.GeneratorMenu;
import net.buildtheearth.modules.generator.model.History;
import net.buildtheearth.modules.updater.DependencyManager;
import net.buildtheearth.modules.utils.ChatUtil;
import net.buildtheearth.modules.utils.Utils;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GeneratorCommand implements CommandExecutor {

    public static void sendHelp(CommandSender sender) {
        ChatUtil.sendMessageBox(sender, "Generator Command", new Runnable() {
            @Override
            public void run() {
                sender.sendMessage("§eHouse Generator:§7 /gen house help");
                sender.sendMessage("§eRoad Generator:§7 /gen road help");
                sender.sendMessage("§eRail Generator:§7 /gen rail help");
                sender.sendMessage("§eTree Generator:§7 /gen tree help");
            }
        });
    }

    public boolean onCommand(CommandSender sender, Command cmd, String cmdlabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can execute this command.");
            return true;
        }

        Player p = (Player) sender;

        // Check if WorldEdit is enabled
        if (!DependencyManager.isWorldEditEnabled()) {
            p.sendMessage("§cPlease install WorldEdit to use this tool.");
            Generator.sendMoreInfo(p);
            p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
            return true;
        }


        // Command Usage: /gen
        if (args.length == 0) {
            if (!Generator.checkIfWorldEditIsInstalled(p))
                return true;

            new GeneratorMenu(p);
            return true;
        }


        // Command Usage: /gen house ...
        if (args[0].equals("house")) {
            Main.buildTeamTools.getGenerator().getHouse().analyzeCommand(p, args);
            return true;
        }

        // Command Usage: /gen road ...
        if (args[0].equals("road")) {
            Main.buildTeamTools.getGenerator().getRoad().analyzeCommand(p, args);
            return true;
        }

        // Command Usage: /gen rail ...
        if (args[0].equals("rail")) {
            Main.getBuildTeamTools().getGenerator().getRail().analyzeCommand(p, args);
            return true;
        }

        // Command Usage: /gen tree ...
        if (args[0].equals("tree")) {
            Main.getBuildTeamTools().getGenerator().getTree().analyzeCommand(p, args);
            return true;
        }


        // Command Usage: /gen history
        if (args[0].equals("history")) {
            if (Generator.getPlayerHistory(p).getHistoryEntries().size() == 0) {
                p.sendMessage("§cYou didn't generate any structures yet. Use /gen to create one.");
                return true;
            }

            ChatUtil.sendMessageBox(sender, "Generator History for " + p.getName(), () -> {
                for (History.HistoryEntry history : Generator.getPlayerHistory(p).getHistoryEntries()) {
                    long timeDifference = System.currentTimeMillis() - history.getTimeCreated();

                    p.sendMessage("§e- " + history.getGeneratorType().name() + " §7-§e " + Utils.toDate(p, timeDifference) + " ago §7-§e " + history.getWorldEditCommandCount() + " Commands executed");
                }
            });
            return true;
        }

        if (args[0].equals("undo")) {
            if (Generator.getPlayerHistory(p).getHistoryEntries().size() == 0) {
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


}

package net.buildtheearth.modules.network.commands;

import net.buildtheearth.Main;
import net.buildtheearth.modules.network.model.Continent;
import net.buildtheearth.modules.network.model.Country;
import net.buildtheearth.modules.updater.UpdateChecker;
import net.buildtheearth.modules.utils.ChatHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class BuildTeamToolsCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (args.length > 0 && args[0].equalsIgnoreCase("help")) {
            ChatHelper.sendMessageBox(sender, "Build Team Help", new Runnable() {
                @Override
                public void run() {
                    sender.sendMessage("§e/btt help - §7List of all sub commands.");
                    sender.sendMessage("§e/btt communicators - §7List of players who communicate with the network.");
                    sender.sendMessage("§e/btt cache - §7View the cache.");
                    sender.sendMessage("§e/btt uploadCache - §7Upload the cache to the network.");
                }
            });
            return true;
        }

        if(args.length > 0 && args[0].equalsIgnoreCase("communicators")) {
            ChatHelper.sendMessageBox(sender, "Build Team Communicators", new Runnable() {
                @Override
                public void run() {
                    for (UUID uuid : Main.getBuildTeamTools().getProxyManager().getCommunicators())
                        sender.sendMessage("§7- §e" + uuid.toString());
                }
            });
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("cache")) {
            ChatHelper.sendMessageBox(sender, "Build Team Cache", new Runnable() {
                @Override
                public void run() {
                    sender.sendMessage(Main.buildTeamTools.getStatsManager().getCurrentCache().toJSONString());
                }
            });
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("uploadCache")) {
            Main.buildTeamTools.getStatsManager().updateAndSave();
            sender.sendMessage("§7Cache uploaded to the network.");
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("debug")) {
            if(!(args.length > 1)) {
                sender.sendMessage("§7You need to add a value: true/false");
                return true;
            }
            boolean debug = Boolean.parseBoolean(args[1]);

            Main.buildTeamTools.setDebug(debug);
            sender.sendMessage("§7Debug Mode was set to: " + debug);
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("checkForUpdates")) {
            boolean wasDebug = Main.buildTeamTools.isDebug();

            Main.buildTeamTools.setDebug(true);
            String result = UpdateChecker.checkForUpdates();
            Main.buildTeamTools.setDebug(wasDebug);
            sender.sendMessage("§7Checked for updates. " + result + " Please take a look at the console for details.");
            return true;
        }

        ChatHelper.sendMessageBox(sender, "Build Team Tools", new Runnable() {
            @Override
            public void run() {
                String buildTeamID = "-";
                if (Main.buildTeamTools.getProxyManager().getBuildTeamID() != null)
                    buildTeamID = Main.buildTeamTools.getProxyManager().getBuildTeamID();

                String serverName = "-";
                if (Main.buildTeamTools.getProxyManager().getServerName() != null)
                    serverName = Main.buildTeamTools.getProxyManager().getServerName();

                String status = "§c§lDISCONNECTED";
                if (Main.buildTeamTools.getProxyManager().isConnected() && !buildTeamID.equals("-") && !serverName.equals("-"))
                    status = "§a§lCONNECTED";
                else if (!buildTeamID.equals("-") && !serverName.equals("-"))
                    status = "§6§lSTANDBY";

                sender.sendMessage("§eStatus: " + status);
                sender.sendMessage("§eVersion: §7" + Main.instance.getDescription().getVersion());
                sender.sendMessage("§eBuildTeam ID: §7" + buildTeamID);
                sender.sendMessage("§eServer Name: §7" + serverName);
                sender.sendMessage("");
                sender.sendMessage("§eCountries: §7");
                for(Continent continent : Continent.values()) {
                    boolean sentContinentMessage = false;
                    for(Country country : continent.getCountries()) {
                        if(country.getTeamID().equals(Main.getBuildTeamTools().getProxyManager().getBuildTeamID())) {
                            if(!sentContinentMessage) {
                                sender.sendMessage("§7§n" + continent.getLabel());
                                sentContinentMessage = true;
                            }
                            sender.sendMessage("§7" + country.getName());
                        }
                    }
                }


                sender.sendMessage("");
                sender.sendMessage("§7Sub-Command list with §e/btt help§7.");
            }
        });
        return true;
    }
}

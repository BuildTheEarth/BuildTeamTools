package net.buildtheearth.modules.common.commands;

import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.Module;
import net.buildtheearth.modules.ModuleHandler;
import net.buildtheearth.modules.common.CommonModule;
import net.buildtheearth.modules.network.NetworkModule;
import net.buildtheearth.modules.network.model.Region;
import net.buildtheearth.modules.stats.StatsModule;
import net.buildtheearth.modules.utils.ChatHelper;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
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
                    for (UUID uuid : NetworkModule.getInstance().getCommunicators())
                        sender.sendMessage("§7- §e" + uuid.toString());
                }
            });
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("cache")) {
            ChatHelper.sendMessageBox(sender, "Build Team Cache", new Runnable() {
                @Override
                public void run() {
                    sender.sendMessage(StatsModule.getInstance().getCurrentCache().toJSONString());
                }
            });
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("updateCache")) {
            StatsModule.getInstance().updateAndSave();
            NetworkModule.getInstance().updateCache();
            sender.sendMessage("§7Cache successfully updated.");
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("debug")) {
            if(!(args.length > 1)) {
                sender.sendMessage("§7You need to add a value: true/false");
                return true;
            }
            boolean debug = Boolean.parseBoolean(args[1]);

            BuildTeamTools.getInstance().setDebug(debug);
            sender.sendMessage("§7Debug Mode was set to: " + debug);
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("checkForUpdates")) {
            boolean wasDebug = BuildTeamTools.getInstance().isDebug();

            BuildTeamTools.getInstance().setDebug(true);
            String result = CommonModule.getInstance().getUpdaterComponent().checkForUpdates();
            BuildTeamTools.getInstance().setDebug(wasDebug);
            sender.sendMessage("§7Checked for updates. " + result + " Please take a look at the console for details.");
            return true;
        }

        ChatHelper.sendMessageBox(sender, "Build Team Tools", () -> {

            String buildTeamID = "-";
            if (NetworkModule.getInstance().getBuildTeam() != null
             && NetworkModule.getInstance().getBuildTeam().getID() != null)
                buildTeamID = NetworkModule.getInstance().getBuildTeam().getID();

            String serverName = "-";
            if (NetworkModule.getInstance().getBuildTeam() != null
             && NetworkModule.getInstance().getBuildTeam().getServerName() != null)
                serverName = NetworkModule.getInstance().getBuildTeam().getServerName();

            String continent = "-";
            if (NetworkModule.getInstance().getBuildTeam() != null
             && NetworkModule.getInstance().getBuildTeam().getContinent() != null)
                continent = NetworkModule.getInstance().getBuildTeam().getContinent().getLabel();

            String status = "§c§lDISCONNECTED";
            if (NetworkModule.getInstance().getBuildTeam() != null
            && NetworkModule.getInstance().getBuildTeam().isConnected() && !buildTeamID.equals("-") && !serverName.equals("-"))
                status = "§a§lCONNECTED";
            else if (!buildTeamID.equals("-") && !serverName.equals("-"))
                status = "§6§lSTANDBY";

            boolean debug = BuildTeamTools.getInstance().isDebug();

            sender.sendMessage("§eStatus: " + status);
            sender.sendMessage("§eVersion: §7" + BuildTeamTools.getInstance().getDescription().getVersion());

            if(debug)
                sender.sendMessage("§eDebug Mode: §a§lON");

            sender.sendMessage("§eModules:");
            for (Module module : ModuleHandler.getInstance().getModules()) {
                TextComponent comp = new TextComponent("§7- " + module.getModuleName() + " §7[" + (module.isEnabled() ? "§a§l✔" : "§c§l✖") + "§7]");

                if(!module.isEnabled() && module.getError() != null && !module.getError().isEmpty())
                    comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§c" + module.getError()).create()));
                else if(!module.isEnabled())
                    comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§cDisabled").create()));
                else
                    comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aEnabled").create()));

                sender.spigot().sendMessage(comp);
            }

            if(NetworkModule.getInstance().getBuildTeam() != null){
                sender.sendMessage("§eBuildTeam ID: §7" + buildTeamID);
                sender.sendMessage("§eServer Name: §7" + serverName);

                sender.sendMessage("");
                sender.sendMessage("§eContinent: §7" + continent);
                sender.sendMessage("§eRegions: §7");

                for(Region region : NetworkModule.getInstance().getBuildTeam().getRegions())
                    sender.sendMessage("- §7" + region.getName());
            }

            sender.sendMessage("");
            sender.sendMessage("§7Sub-Command list with §e/btt help§7.");

        });
        return true;
    }
}

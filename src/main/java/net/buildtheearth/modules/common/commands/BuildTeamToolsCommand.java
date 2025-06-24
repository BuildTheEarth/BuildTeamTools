package net.buildtheearth.modules.common.commands;

import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.Module;
import net.buildtheearth.modules.ModuleHandler;
import net.buildtheearth.modules.common.CommonModule;
import net.buildtheearth.modules.network.NetworkModule;
import net.buildtheearth.modules.network.model.Permissions;
import net.buildtheearth.modules.network.model.Region;
import net.buildtheearth.modules.stats.StatsModule;
import net.buildtheearth.utils.ChatUtil;
import net.buildtheearth.utils.Utils;
import net.buildtheearth.utils.lang.LangPaths;
import net.buildtheearth.utils.lang.LangUtil;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class BuildTeamToolsCommand implements CommandExecutor, TabCompleter {

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        // Check if the player has permission to use this command
        if(!sender.hasPermission(Permissions.BUILD_TEAM_TOOLS)){
            ChatUtil.sendError(sender, LangPaths.ERROR.PLAYER_HAS_NO_PERMISSIONS);
            return true;
        }


        // Command: /btt
        if(args.length == 0){
            sendBuildTeamToolsInfo(sender);
            return true;
        }


        // Command: /btt help
        if (args[0].equalsIgnoreCase("help")) {
            ChatUtil.sendMessageBox(sender, "Build Team Help", () -> {
                sender.sendMessage("§e/btt cache [upload] §8- §7View the cache or upload it to the network.");
                sender.sendMessage("§e/btt checkForUpdates §8- §7Check for updates.");
                sender.sendMessage("§e/btt communicators §8- §7List of players who communicate with the network.");
                sender.sendMessage("§e/btt debug <true/false> §8- §7Enable or disable debug mode.");
                sender.sendMessage("§e/btt help §8- §7List of all sub commands.");
                sender.sendMessage("§e/btt reload §8- §7Reload all modules.");
            });
            return true;
        }


        // Command: /btt communicators
        if(args[0].equalsIgnoreCase("communicators")) {
            if(!sender.hasPermission(Permissions.BUILD_TEAM_TOOLS_COMMUNICATORS)){
                ChatUtil.sendError(sender, LangPaths.ERROR.PLAYER_HAS_NO_PERMISSIONS);
                return true;
            }

            ChatUtil.sendMessageBox(sender, "Build Team Communicators", () -> {
                for (UUID uuid : NetworkModule.getInstance().getCommunicators())
                    sender.sendMessage("§7- §e" + uuid.toString());
            });
            return true;
        }


        // Command: /btt cache
        if (args[0].equalsIgnoreCase("cache")) {
            if(!sender.hasPermission(Permissions.BUILD_TEAM_TOOLS_CACHE)){
                ChatUtil.sendError(sender, LangPaths.ERROR.PLAYER_HAS_NO_PERMISSIONS);
                return true;
            }

            if(args.length > 1 && args[1].equalsIgnoreCase("update")) {
                StatsModule.getInstance().updateAndSave();
                NetworkModule.getInstance().updateCache();
                sender.sendMessage("§7Cache successfully updated.");
                return true;
            }

            ChatUtil.sendMessageBox(sender, "Build Team Cache", () ->
                    sender.sendMessage(StatsModule.getInstance().getCurrentCache().toJSONString()));
            return true;
        }


        // Command: /btt debug
        if (args[0].equalsIgnoreCase("debug")) {
            if(!sender.hasPermission(Permissions.BUILD_TEAM_TOOLS_DEBUG)){
                ChatUtil.sendError(sender, LangPaths.ERROR.PLAYER_HAS_NO_PERMISSIONS);
                return true;
            }

            if(!(args.length > 1)) {
                sender.sendMessage("§c" + LangUtil.getInstance().get(sender, LangPaths.Common.BOOL));
                return true;
            }

            if(!args[1].equalsIgnoreCase("true") && !args[1].equalsIgnoreCase("false")) {
                sender.sendMessage("§c" + LangUtil.getInstance().get(sender, LangPaths.Common.BOOL));
                return true;
            }

            boolean debug = Boolean.parseBoolean(args[1]);

            BuildTeamTools.getInstance().setDebug(debug);
            sender.sendMessage(ChatUtil.getStandardString("§7Debug Mode was set to: %s", debug));
            return true;
        }


        // Command: /btt checkForUpdates
        if (args[0].equalsIgnoreCase("checkForUpdates")) {
            if(!sender.hasPermission(Permissions.BUILD_TEAM_TOOLS_CHECK_FOR_UPDATES)){
                ChatUtil.sendError(sender, LangPaths.ERROR.PLAYER_HAS_NO_PERMISSIONS);
                return true;
            }

            boolean wasDebug = BuildTeamTools.getInstance().isDebug();

            BuildTeamTools.getInstance().setDebug(true);
            String result = CommonModule.getInstance().getUpdaterComponent().checkForUpdates();
            BuildTeamTools.getInstance().setDebug(wasDebug);
            sender.sendMessage("§7Checked for updates. " + result + " Please take a look at the console for details.");
            return true;
        }


        // Command: /btt reload
        if(args[0].equalsIgnoreCase("reload")) {
            if(!sender.hasPermission(Permissions.BUILD_TEAM_TOOLS_RELOAD)){
                ChatUtil.sendError(sender, LangPaths.ERROR.PLAYER_HAS_NO_PERMISSIONS);
                return true;
            }

            sender.sendMessage(ChatUtil.getStandardString("§7Reloading all modules..."));
            ModuleHandler.getInstance().reloadAll(sender);
            sender.sendMessage(ChatUtil.getStandardString("§7All modules have been reloaded."));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1)
            return Arrays.asList("help", "communicators", "checkForUpdates", "cache", "debug", "reload");

        List<String> debugSuggestions = Utils.getTabCompleterArgs(args, "debug", 2, Arrays.asList("true", "false"));
        if(debugSuggestions != null)
            return debugSuggestions;

        List<String> cacheSuggestions = Utils.getTabCompleterArgs(args, "cache", 2, Collections.singletonList("upload"));
        if(cacheSuggestions != null)
            return cacheSuggestions;

        return null;
    }

    public static void sendBuildTeamToolsInfo(CommandSender sender){
        ChatUtil.sendMessageBox(sender, "Build Team Tools", () -> {

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
                sender.sendMessage("");
                sender.sendMessage("§eBuildTeam ID: §7" + buildTeamID);
                sender.sendMessage("§eServer Name: §7" + serverName);
                sender.sendMessage("§eContinent: §7" + continent);
                sender.sendMessage("§eRegions: §7");

                for(Region region : NetworkModule.getInstance().getBuildTeam().getRegions())
                    sender.sendMessage("- §7" + region.getName());
            }

            sender.sendMessage("");
            sender.sendMessage("§7Sub-Command list with §e/btt help§7.");

        });
    }
}

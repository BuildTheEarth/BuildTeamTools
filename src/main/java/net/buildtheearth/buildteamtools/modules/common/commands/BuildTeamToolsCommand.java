package net.buildtheearth.buildteamtools.modules.common.commands;

import com.alpsbte.alpslib.utils.ChatHelper;
import com.google.gson.Gson;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.Module;
import net.buildtheearth.buildteamtools.modules.ModuleHandler;
import net.buildtheearth.buildteamtools.modules.common.CommonModule;
import net.buildtheearth.buildteamtools.modules.network.NetworkModule;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import net.buildtheearth.buildteamtools.modules.network.model.Region;
import net.buildtheearth.buildteamtools.modules.stats.StatsModule;
import net.buildtheearth.buildteamtools.utils.Utils;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BuildTeamToolsCommand implements CommandExecutor, TabCompleter {

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {

        if(!sender.hasPermission(Permissions.BUILD_TEAM_TOOLS)){
            sender.sendMessage("§cYou don't have permission to execute this command.");
            return true;
        }

        if(args.length == 0){
            sendBuildTeamToolsInfo(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("help")) {
            ChatHelper.sendMessageBox(sender, "Build Team Help", () -> {
                sender.sendMessage("§e/btt cache [upload] §8- §7View the cache or upload it to the network.");
                sender.sendMessage("§e/btt checkForUpdates §8- §7Check for updates.");
                sender.sendMessage("§e/btt communicators §8- §7List of players who communicate with the network.");
                sender.sendMessage("§e/btt debug <true/false> §8- §7Enable or disable debug mode.");
                sender.sendMessage("§e/btt help §8- §7List of all sub commands.");
                sender.sendMessage("§e/btt reload-config §8- §7Reload all configs for the modules");
            });
            return true;
        }

        if(args[0].equalsIgnoreCase("communicators")) {
            if(!sender.hasPermission(Permissions.BUILD_TEAM_TOOLS_COMMUNICATORS)){
                sender.sendMessage("§cYou don't have permission to execute this command.");
                return true;
            }

            ChatHelper.sendMessageBox(sender, "Build Team Communicators", () -> {
                for (UUID uuid : NetworkModule.getInstance().getCommunicators())
                    sender.sendMessage("§7- §e" + uuid.toString());
            });
            return true;
        }

        if (args[0].equalsIgnoreCase("cache")) {
            if(!sender.hasPermission(Permissions.BUILD_TEAM_TOOLS_CACHE)){
                sender.sendMessage("§cYou don't have permission to execute this command.");
                return true;
            }

            if(args.length > 1 && args[1].equalsIgnoreCase("update")) {
                StatsModule.getInstance().updateAndSave();
                NetworkModule.getInstance().updateCache();
                sender.sendMessage("§7Cache successfully updated.");
                return true;
            }

            ChatHelper.sendMessageBox(sender, "Build Team Cache", () ->
                    sender.sendMessage(StatsModule.getInstance().getCurrentCache().toJSONString()));
            return true;
        }

        if (args[0].equalsIgnoreCase("debug")) {
            if(!sender.hasPermission(Permissions.BUILD_TEAM_TOOLS_DEBUG)){
                sender.sendMessage("§cYou don't have permission to execute this command.");
                return true;
            }

            if(args.length <= 1) {
                sender.sendMessage("§cYou need to add a value: true/false");
                return true;
            }

            if(!args[1].equalsIgnoreCase("true") && !args[1].equalsIgnoreCase("false")) {
                sender.sendMessage("§cYou need to set the value to true or false.");
                return true;
            }

            boolean debug = Boolean.parseBoolean(args[1]);

            BuildTeamTools.getInstance().setDebug(debug);
            sender.sendMessage(ChatHelper.getStandardString("§7Debug Mode was set to: %s", debug));
            return true;
        }

        if (args[0].equalsIgnoreCase("checkForUpdates")) {
            if(!sender.hasPermission(Permissions.BUILD_TEAM_TOOLS_CHECK_FOR_UPDATES)){
                sender.sendMessage("§cYou don't have permission to execute this command.");
                return true;
            }

            boolean wasDebug = BuildTeamTools.getInstance().isDebug();

            BuildTeamTools.getInstance().setDebug(true);
            String result = CommonModule.getInstance().getUpdaterComponent().checkForUpdates();
            BuildTeamTools.getInstance().setDebug(wasDebug);
            sender.sendMessage("§7Checked for updates. " + result + " Please take a look at the console for details.");
            return true;
        }

        if(args[0].equalsIgnoreCase("reload-config")) {
            if(!sender.hasPermission(Permissions.BUILD_TEAM_TOOLS_RELOAD)){
                sender.sendMessage("§cYou don't have permission to execute this command.");
                return true;
            }

            sender.sendMessage(ChatHelper.getStandardString("§7Reloading all configs..."));
            BuildTeamTools.getInstance().reloadConfig();
            sender.sendMessage(ChatHelper.getStandardString("§7All configs have been reloaded. For some changes to apply you have to restart the server."));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(args.length == 1)
            return Arrays.asList("help", "communicators", "checkForUpdates", "cache", "debug", "reload-config");

        List<String> debugSuggestions = Utils.getTabCompleterArgs(args, "debug", 2, Arrays.asList("true", "false"));
        if(debugSuggestions != null)
            return debugSuggestions;

        List<String> cacheSuggestions = Utils.getTabCompleterArgs(args, "cache", 2, Collections.singletonList("upload"));
        return cacheSuggestions;
    }

    public static void sendBuildTeamToolsInfo(CommandSender sender){
        ChatHelper.sendMessageBox(sender, "Build Team Tools", () -> {

            String buildTeamID = "-";
            if (NetworkModule.getInstance().getBuildTeam() != null
                    && NetworkModule.getInstance().getBuildTeam().getID() != null)
                buildTeamID = NetworkModule.getInstance().getBuildTeam().getID();

            String serverName = "-";
            if (NetworkModule.getInstance().getBuildTeam() != null
                    && NetworkModule.getInstance().getBuildTeam().getServerName() != null)
                serverName = NetworkModule.getInstance().getBuildTeam().getServerName();

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

                List<String> regions = new ArrayList<>();
                List<String> continents = new ArrayList<>();
                for (Region region : NetworkModule.getInstance().getBuildTeam().getRegions()) {
                    if (region.getContinent() != null && !continents.contains(region.getContinent().getLabel()))
                        continents.add(region.getContinent().getLabel());

                    if (!regions.contains(region.getName()))
                        regions.add(region.getName());
                }

                Gson gson = new Gson();

                sender.sendMessage("");
                sender.sendMessage("§eBuildTeam ID: §7" + buildTeamID);
                sender.sendMessage("§eServer Name: §7" + serverName);
                sender.sendMessage("§eContinents: §7" + gson.toJson(continents));
                sender.sendMessage("§eRegions: §7" + gson.toJson(regions));
            }

            sender.sendMessage("");
            sender.sendMessage("§7Sub-Command list with §e/btt help§7.");

        });
    }
}

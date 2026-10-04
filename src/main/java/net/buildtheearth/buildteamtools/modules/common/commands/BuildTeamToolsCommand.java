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
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BuildTeamToolsCommand {

    public void register(Module owner) {
        var commandManager = BuildTeamTools.getInstance().getCommandManager();
        commandManager.registerRoot(owner, BuildTeamToolsCommand::sendBuildTeamToolsInfo);
        commandManager.registerRootHelp(owner);
        commandManager.registerRootSubcommand(owner, "communicators",
                "List players currently communicating with the network.",
                Permissions.BUILD_TEAM_TOOLS_COMMUNICATORS,
                (sender, label, args) -> communicatorsCommand(sender, args));
        commandManager.registerRootSubcommand(owner, "cache",
                "View the network cache or upload and refresh it.",
                Permissions.BUILD_TEAM_TOOLS_CACHE,
                (sender, label, args) -> cacheCommand(sender));
        commandManager.registerRootNestedSubcommand(owner, "cache", "update",
                "Upload and refresh the network cache.",
                Permissions.BUILD_TEAM_TOOLS_CACHE, context -> updateCacheCommand(context.sender().getSender()));
        commandManager.registerRootSubcommand(owner, "debug",
                "Show or change debug mode.",
                Permissions.BUILD_TEAM_TOOLS_DEBUG,
                (sender, label, args) -> debugCommand(sender));
        commandManager.registerRootNestedSubcommand(owner, "debug", "on",
                "Enable debug mode.",
                Permissions.BUILD_TEAM_TOOLS_DEBUG, context -> setDebugMode(context.sender().getSender(), true));
        commandManager.registerRootNestedSubcommand(owner, "debug", "off",
                "Disable debug mode.",
                Permissions.BUILD_TEAM_TOOLS_DEBUG, context -> setDebugMode(context.sender().getSender(), false));
        commandManager.registerRootSubcommand(owner, "checkforupdates",
                "Check whether a plugin update is available.",
                Permissions.BUILD_TEAM_TOOLS_CHECK_FOR_UPDATES,
                (sender, label, args) -> checkForUpdatesCommand(sender, args));
        commandManager.registerRootSubcommand(owner, "reload-config",
                "Reload module configuration files.",
                Permissions.BUILD_TEAM_TOOLS_RELOAD,
                (sender, label, args) -> reloadCommand(sender));
        commandManager.registerRootSubcommand(owner, "reload",
                "Reload module configuration files.",
                Permissions.BUILD_TEAM_TOOLS_RELOAD,
                (sender, label, args) -> reloadCommand(sender));
        commandManager.registerRootSubcommand(owner, "update",
                "Update the plugin to the latest version.",
                Permissions.BUILD_TEAM_TOOLS_UPDATE,
                (sender, label, args) -> updateCommand(sender));
    }

    private void communicatorsCommand(@NotNull CommandSender sender, String @NotNull [] args) {
        ChatHelper.sendMessageBox(sender, "Build Team Communicators", () -> {
            for (UUID uuid : NetworkModule.getInstance().getCommunicators())
                sender.sendMessage("§7- §e" + uuid);
        });
    }

    private void checkForUpdatesCommand(@NotNull CommandSender sender, String @NotNull [] args) {
        CommonModule.getInstance().getUpdaterComponent().checkForUpdates(sender);
    }

    private static void debugCommand(@NonNull CommandSender sender) {
        if (!sender.hasPermission(Permissions.BUILD_TEAM_TOOLS_DEBUG)) {
            Permissions.sendNoPermissionMessage(sender, Permissions.BUILD_TEAM_TOOLS_DEBUG);
            return;
        }

        sender.sendMessage(ChatHelper.getStandardComponent(true, "Current Debug Mode: %s.",
                BuildTeamTools.getInstance().isDebug() ? "ON" : "OFF"));
    }

    private static void setDebugMode(@NonNull CommandSender sender, boolean debug) {
        BuildTeamTools.getInstance().setDebug(debug);
        sender.sendMessage(ChatHelper.getStandardComponent(true, "Debug Mode was set to: %s", debug));
    }

    private static void cacheCommand(@NonNull CommandSender sender) {
        if (!sender.hasPermission(Permissions.BUILD_TEAM_TOOLS_CACHE)) {
            Permissions.sendNoPermissionMessage(sender, Permissions.BUILD_TEAM_TOOLS_CACHE);
            return;
        }

        displayCache(sender);
    }

    private static void updateCacheCommand(@NonNull CommandSender sender) {
        if (!sender.hasPermission(Permissions.BUILD_TEAM_TOOLS_CACHE)) {
            Permissions.sendNoPermissionMessage(sender, Permissions.BUILD_TEAM_TOOLS_CACHE);
            return;
        }

        NetworkModule.getInstance().updateCache();
        if (NetworkModule.getInstance().getBuildTeam() != null) NetworkModule.getInstance().enableDisabledModules();
        StatsModule.getInstance().updateAndSave();
        sender.sendMessage(ChatHelper.getSuccessComponent("Cache successfully updated."));
        displayCache(sender);
    }

    private static void displayCache(CommandSender sender) {
        if (StatsModule.getInstance().isEnabled()) {
            ChatHelper.sendMessageBox(sender, "Build Team Cache", () ->
                    sender.sendMessage(StatsModule.getInstance().getCurrentCache().toJSONString()));
        } else {
            sender.sendMessage(ChatHelper.getErrorComponent("The Stats Module is not enabled, so there is no cache to show" +
                    "."));
        }
    }

    private static void reloadCommand(@NonNull CommandSender sender) {
        if (!sender.hasPermission(Permissions.BUILD_TEAM_TOOLS_RELOAD)) {
            Permissions.sendNoPermissionMessage(sender, Permissions.BUILD_TEAM_TOOLS_RELOAD);
            return;
        }

        sender.sendMessage(ChatHelper.getStandardComponent(true, "Reloading all configs..."));
        BuildTeamTools.getInstance().reloadConfig();
        if (NetworkModule.getInstance().getBuildTeam() == null) {
            NetworkModule.getInstance().updateCache();
            if (NetworkModule.getInstance().getBuildTeam() != null) NetworkModule.getInstance().enableDisabledModules();
        }

        sender.sendMessage(ChatHelper.getStandardString("All configs have been reloaded. For some changes to apply you have" +
                " to restart the server."));
    }

    private void updateCommand(@NotNull CommandSender sender) {
        if (!sender.hasPermission(Permissions.BUILD_TEAM_TOOLS_UPDATE)) {
            Permissions.sendNoPermissionMessage(sender, Permissions.BUILD_TEAM_TOOLS_UPDATE);
            return;
        }

        CommonModule.getInstance().getUpdaterComponent().update(sender, true);
    }

    public static void sendBuildTeamToolsInfo(CommandSender sender) {
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
            sender.sendMessage("§eVersion: §7" + BuildTeamTools.getInstance().getPluginMeta().getVersion());

            if (debug)
                sender.sendMessage("§eDebug Mode: §a§lON");

            sender.sendMessage("§eModules:");
            for (Module module : ModuleHandler.getInstance().getModules()) {
                TextComponent comp = new TextComponent("§7- " + module.getModuleName() + " §7[" + (module.isEnabled() ? "§a§l✔" : "§c§l✖") + "§7]");

                if (!module.isEnabled() && module.getError() != null && !module.getError().isEmpty())
                    comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§c" + module.getError()).create()));
                else if (!module.isEnabled())
                    comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§cDisabled").create()));
                else
                    comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aEnabled").create()));

                sender.spigot().sendMessage(comp);
            }

            if (NetworkModule.getInstance().getBuildTeam() != null) {

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

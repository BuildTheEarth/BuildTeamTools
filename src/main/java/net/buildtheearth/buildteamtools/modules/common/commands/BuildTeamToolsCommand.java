package net.buildtheearth.buildteamtools.modules.common.commands;

import com.google.gson.Gson;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import net.buildtheearth.buildteamtools.i18n.Messages;
import net.buildtheearth.buildteamtools.modules.Module;
import net.buildtheearth.buildteamtools.modules.ModuleHandler;
import net.buildtheearth.buildteamtools.modules.common.CommonModule;
import net.buildtheearth.buildteamtools.modules.network.NetworkModule;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import net.buildtheearth.buildteamtools.modules.network.model.Region;
import net.buildtheearth.buildteamtools.modules.stats.StatsModule;
import net.buildtheearth.buildteamtools.utils.MessageBoxes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BuildTeamToolsCommand {

    private static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();

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
        commandManager.registerRootSubcommand(owner, "update",
                "Update the plugin to the latest version.",
                Permissions.BUILD_TEAM_TOOLS_UPDATE,
                (sender, label, args) -> updateCommand(sender));
    }

    private void communicatorsCommand(@NotNull CommandSender sender, String @NotNull [] args) {
        MessageBoxes.send(sender, plainTitle(sender, MessageKeys.Common.COMMUNICATORS_TITLE), () -> {
            for (UUID uuid : NetworkModule.getInstance().getCommunicators())
                sender.sendMessage(Messages.component(
                        MessageKeys.Common.COMMUNICATOR_ENTRY, Component.text(uuid.toString())));
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

        Component state = Messages.component(BuildTeamTools.getInstance().isDebug()
                ? MessageKeys.Common.DEBUG_ON
                : MessageKeys.Common.DEBUG_OFF);
        sender.sendMessage(Messages.component(MessageKeys.Common.DEBUG_CURRENT, state));
    }

    private static void setDebugMode(@NonNull CommandSender sender, boolean debug) {
        BuildTeamTools.getInstance().setDebug(debug);
        sender.sendMessage(Messages.component(MessageKeys.Common.DEBUG_SET, Component.text(debug)));
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
        sender.sendMessage(Messages.component(MessageKeys.Common.CACHE_UPDATED));
        displayCache(sender);
    }

    private static void displayCache(CommandSender sender) {
        if (StatsModule.getInstance().isEnabled()) {
            MessageBoxes.send(sender, plainTitle(sender, MessageKeys.Common.CACHE_TITLE), () ->
                    sender.sendMessage(StatsModule.getInstance().getCurrentCache().toJSONString()));
        } else {
            sender.sendMessage(Messages.component(MessageKeys.Common.CACHE_STATS_DISABLED));
        }
    }

    private static void reloadCommand(@NonNull CommandSender sender) {
        if (!sender.hasPermission(Permissions.BUILD_TEAM_TOOLS_RELOAD)) {
            Permissions.sendNoPermissionMessage(sender, Permissions.BUILD_TEAM_TOOLS_RELOAD);
            return;
        }

        sender.sendMessage(Messages.component(MessageKeys.Common.RELOADING_CONFIGS));
        BuildTeamTools.getInstance().reloadConfig();
        if (NetworkModule.getInstance().getBuildTeam() == null) {
            NetworkModule.getInstance().updateCache();
            if (NetworkModule.getInstance().getBuildTeam() != null) NetworkModule.getInstance().enableDisabledModules();
        }

        sender.sendMessage(Messages.component(MessageKeys.Common.CONFIGS_RELOADED));
    }

    private void updateCommand(@NotNull CommandSender sender) {
        if (!sender.hasPermission(Permissions.BUILD_TEAM_TOOLS_UPDATE)) {
            Permissions.sendNoPermissionMessage(sender, Permissions.BUILD_TEAM_TOOLS_UPDATE);
            return;
        }

        CommonModule.getInstance().getUpdaterComponent().update(sender, true);
    }

    public static void sendBuildTeamToolsInfo(CommandSender sender) {
        MessageBoxes.send(sender, plainTitle(sender, MessageKeys.Common.INFO_TITLE), () -> {

            String buildTeamID = "-";
            if (NetworkModule.getInstance().getBuildTeam() != null
                    && NetworkModule.getInstance().getBuildTeam().getID() != null)
                buildTeamID = NetworkModule.getInstance().getBuildTeam().getID();

            String serverName = "-";
            if (NetworkModule.getInstance().getBuildTeam() != null
                    && NetworkModule.getInstance().getBuildTeam().getServerName() != null)
                serverName = NetworkModule.getInstance().getBuildTeam().getServerName();

            sender.sendMessage(Messages.component(
                    MessageKeys.Common.INFO_STATUS,
                    infoStatus(buildTeamID, serverName)));
            sender.sendMessage(Messages.component(
                    MessageKeys.Common.INFO_VERSION,
                    Component.text(BuildTeamTools.getInstance().getPluginMeta().getVersion())));

            if (BuildTeamTools.getInstance().isDebug())
                sender.sendMessage(Messages.component(MessageKeys.Common.INFO_DEBUG_ON));

            sender.sendMessage(Messages.component(MessageKeys.Common.INFO_MODULES));
            for (Module module : ModuleHandler.getInstance().getModules()) {
                Component mark = Messages.component(module.isEnabled()
                        ? MessageKeys.Common.INFO_MODULE_MARK_ON
                        : MessageKeys.Common.INFO_MODULE_MARK_OFF);

                sender.sendMessage(Messages.component(
                                MessageKeys.Common.INFO_MODULE_LINE,
                                Component.text(module.getModuleName()),
                                mark)
                        .hoverEvent(HoverEvent.showText(moduleHover(module))));
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

                sender.sendMessage(Component.empty());
                sender.sendMessage(Messages.component(
                        MessageKeys.Common.INFO_BUILD_TEAM_ID, Component.text(buildTeamID)));
                sender.sendMessage(Messages.component(
                        MessageKeys.Common.INFO_SERVER_NAME, Component.text(serverName)));
                sender.sendMessage(Messages.component(
                        MessageKeys.Common.INFO_CONTINENTS, Component.text(gson.toJson(continents))));
                sender.sendMessage(Messages.component(
                        MessageKeys.Common.INFO_REGIONS, Component.text(gson.toJson(regions))));
            }

            sender.sendMessage(Component.empty());
            sender.sendMessage(Messages.component(MessageKeys.Common.INFO_HELP_HINT));

        });
    }

    private static Component infoStatus(String buildTeamID, String serverName) {
        if (NetworkModule.getInstance().getBuildTeam() != null
                && NetworkModule.getInstance().getBuildTeam().isConnected()
                && !buildTeamID.equals("-")
                && !serverName.equals("-")) {
            return Messages.component(MessageKeys.Common.INFO_STATUS_CONNECTED);
        }
        if (!buildTeamID.equals("-") && !serverName.equals("-")) {
            return Messages.component(MessageKeys.Common.INFO_STATUS_STANDBY);
        }
        return Messages.component(MessageKeys.Common.INFO_STATUS_DISCONNECTED);
    }

    private static Component moduleHover(Module module) {
        if (!module.isEnabled() && module.getError() != null && !module.getError().isEmpty()) {
            return Messages.component(
                    MessageKeys.Common.INFO_MODULE_HOVER_ERROR, Component.text(module.getError()));
        }
        if (!module.isEnabled()) {
            return Messages.component(MessageKeys.Common.INFO_MODULE_HOVER_DISABLED);
        }
        return Messages.component(MessageKeys.Common.INFO_MODULE_HOVER_ENABLED);
    }

    private static String plainTitle(CommandSender sender, String key) {
        return PLAIN.serialize(Messages.render(sender, Messages.component(key)));
    }
}

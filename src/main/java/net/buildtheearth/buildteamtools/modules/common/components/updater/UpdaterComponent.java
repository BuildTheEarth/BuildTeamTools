package net.buildtheearth.buildteamtools.modules.common.components.updater;

import com.alpsbte.alpslib.utils.ChatHelper;
import io.papermc.paper.util.Tick;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.ModuleComponent;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import net.buildtheearth.buildteamtools.utils.Utils;
import net.buildtheearth.buildteamtools.utils.io.ConfigPaths;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.lushplugins.pluginupdater.api.exception.InvalidVersionFormatException;
import org.lushplugins.pluginupdater.api.updater.Updater;
import org.lushplugins.pluginupdater.api.version.VersionDifference;
import org.lushplugins.pluginupdater.api.version.comparator.SemVerComparator;
import org.lushplugins.pluginupdater.api.version.comparator.VersionComparator;
import org.lushplugins.pluginupdater.paper.api.PaperUpdater;
import org.lushplugins.pluginupdater.paper.api.notification.PaperUpdateNotifier;

import java.time.Duration;


public class UpdaterComponent extends ModuleComponent {

    private final Updater updater;
    private final BuildTeamTools plugin;

    public UpdaterComponent(@NonNull BuildTeamTools plugin) {
        super("Updater");
        updater = PaperUpdater.builder(plugin)
                .github("BuildTheEarth/BuildTeamTools")
                .notify(true)
                .notificationMessage("<#ffe27a>A new <#e0c01b>%plugin% <#ffe27a>update is now available! " +
                        "<#e0c01b>%current_version% <#ffe27a>-> <#e0c01b>%latest_version%") // Default message is no minimsg
                .notificationPermission(Permissions.NOTIFY_UPDATE)
                .build();
        this.plugin = plugin;

        if (plugin.getConfig().getBoolean(ConfigPaths.AUTO_UPDATE)) runAutoUpdateAfterCheck();
    }

    private void runAutoUpdateAfterCheck() {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
            if (updater.getPluginData().hasCheckRan()) {
                update(Bukkit.getConsoleSender(), false);
                return;
            }

            updater.checkForUpdate().thenAcceptAsync(update -> {
                if (Boolean.TRUE.equals(update)) {
                    update(Bukkit.getConsoleSender(), false);
                }
            });
        }, Tick.tick().fromDuration(Duration.ofSeconds(1)));
    }

    public void update(CommandSender sender, boolean checkMsg) {
        if (updater.isAlreadyDownloaded() || !updater.isUpdateAvailable()) {
            if (checkMsg) {
                sender.sendMessage(ChatHelper.getStandardComponent(
                        true,
                        "It looks like there is no new update available!"
                ));
            }
            return;
        }


        updater.attemptDownload().thenAccept(success -> {
            if (Boolean.TRUE.equals(success)) {
                sender.sendMessage(ChatHelper.getSuccessComponent(
                        "Successfully updated plugin to version %s, restart the server to apply changes!",
                        updater.getPluginData().getLatestVersion()
                ));

                notifyAllPlayersAboutUpdate(sender);
            }
        });
    }


    public void checkForUpdates(@NonNull CommandSender sender) {
        if (!sender.hasPermission(Permissions.BUILD_TEAM_TOOLS_CHECK_FOR_UPDATES)) {
            Utils.sendNoPermissionMessage(sender, Permissions.BUILD_TEAM_TOOLS_CHECK_FOR_UPDATES);
            return;
        }

        updater.checkForUpdate().whenCompleteAsync((update, throwable) -> {
            if (throwable != null) {
                sender.sendMessage(ChatHelper.getErrorComponent(
                        "Could not check for updates. The update API rejected the request or is unavailable. Error: ",
                        throwable.getMessage()
                ));
                return;
            }

            if (!Boolean.TRUE.equals(update)) {
                sender.sendMessage(ChatHelper.getSuccessComponent(
                        "The BuildTeamTools plugin is up to date."
                ));
                return;
            }

            if (!sender.hasPermission(Permissions.NOTIFY_UPDATE)) {
                sender.sendMessage(ChatHelper.getErrorComponent(
                        "A new update is available for the BuildTeamTools plugin. " +
                                "You don't have the permission (%s) to get the detailed notification.",
                        Permissions.NOTIFY_UPDATE
                ));
                return;
            }

            if (sender instanceof Player p && updater.getNotifier() instanceof PaperUpdateNotifier paperUpdater) {
                paperUpdater.handle(p);
            } else {
                sender.sendMessage(ChatHelper.getSuccessComponent(
                        "New update available for BuildTeamTools plugin: v%s.",
                        updater.getPluginData().getLatestVersion()
                ));
            }
        });
    }

    /**
     * Notify a player that the plugin was updated to a newer version.
     * Only if the player has the permission buildteam.notifyUpdate
     *
     * @param p The Player to notify
     */
    public void notifyUpdate(@NonNull Player p) {
        if (!updater.getPluginData().isAlreadyDownloaded()) return;

        if (p.hasPermission(Permissions.NOTIFY_UPDATE)) {
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
            p.sendMessage("");
            p.sendMessage("§6§l[BuildTeam Plugin] §eThe server automatically installed a new update (v" + updater.getPluginData().getLatestVersion() + ").");
            p.sendMessage("§6>> §ePlease restart or reload the server to activate it.");
            p.sendMessage("");
        }
    }

    /**
     * Notifies all online players with the permission about the update.
     */
    public void notifyAllPlayersAboutUpdate(CommandSender exceptPlayer) {
        if (!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(plugin, () -> notifyAllPlayersAboutUpdate(exceptPlayer));
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.equals(exceptPlayer)) {
                notifyUpdate(player);
            }
        }
    }

    /**
     * Checks if it should be updated
     *
     * @param newVersion remote version
     * @param oldVersion current version
     * @param source The source of the version check, used for logging. Should be a human-readable string.
     */
    public boolean shouldUpdate(@NonNull String newVersion, String oldVersion, String source) {
        return switch (getVersionDifference(newVersion, oldVersion, source)) {
            case MAJOR, MINOR, PATCH, BUILD -> true;
            case UNKNOWN, LATEST -> false;
        };
    }


    /**
     * Checks the (Semver) difference of the two provided versions. Parser also works for kinda server versions.
     *
     * @param latestVersion  The latest available version that will be checked against
     * @param currentVersion The currently installed version
     * @param source         The source of the version check, used for logging. Should be a human-readable string.
     * @return The difference between the two versions. Unknown and latest mean the version is up to date/newer.
     */
    public VersionDifference getVersionDifference(@NonNull String latestVersion, String currentVersion, String source) {

        VersionComparator comparator = SemVerComparator.INSTANCE;
        VersionDifference versionDifference;
        try {
            versionDifference = comparator.getVersionDifference(currentVersion, latestVersion);
        } catch (InvalidVersionFormatException e) {
            ChatHelper.logError("Failed to compare versions for '%s': %s", e, source);
            return VersionDifference.UNKNOWN;
        }

        return versionDifference;
    }
}

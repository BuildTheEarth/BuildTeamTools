package net.buildtheearth.modules.updater;

import net.buildtheearth.Main;
import net.buildtheearth.BuildTeamTools;
import org.bukkit.Bukkit;

import java.io.File;

public class UpdateChecker {

    private static File pluginFile;

    public static String start(File pluginFile) {
        UpdateChecker.pluginFile = pluginFile;

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, UpdateChecker::checkForUpdates, 20*60*60);
        return checkForUpdates();
    }

    public static String checkForUpdates(){
        Updater updater = new Updater(Main.instance, BuildTeamTools.SPIGOT_PROJECT_ID, pluginFile, Updater.UpdateType.CHECK_DOWNLOAD, Main.getBuildTeamTools().isDebug());
        Updater.Result result = updater.getResult();

        String resultMessage = "";
        switch (result){
            case BAD_ID: resultMessage = "Failed to update the plugin: Wrong Spigot ID."; break;
            case FAILED: resultMessage = "Failed to update the plugin."; break;
            case NO_UPDATE: resultMessage = "The plugin is up to date."; break;
            case SUCCESS: resultMessage = "Plugin successfully updated."; break;
            case UPDATE_FOUND: resultMessage = "Found an update for the plugin."; break;
            default: resultMessage = "No result for update search."; break;
        }

        return resultMessage;
    }
}

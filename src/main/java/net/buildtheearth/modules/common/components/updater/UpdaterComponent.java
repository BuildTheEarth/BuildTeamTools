package net.buildtheearth.modules.common.components.updater;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.buildtheearth.modules.ModuleComponent;
import net.buildtheearth.modules.network.model.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;


public class UpdaterComponent extends ModuleComponent {
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.54 Safari/537.36";
    private static final String DOWNLOAD = "/download";
    private static final String VERSIONS = "/versions";
    private static final String PAGE = "?page=";
    private static final String API_RESOURCE = "https://api.spiget.org/v2/resources/";
    // Direct download link
    private String downloadLink;
    // Provided plugin
    private final Plugin plugin;
    // The folder where update will be downloaded
    private final File updateFolder;
    // The plugin file
    private final File file;
    // ID of a project
    private final int id;
    // return a page
    private int page = 1;
    // Set the update type
    private final UpdateType updateType;
    // Get the outcome result
    private Result result = Result.SUCCESS;
    // If next page is empty set it to true, and get info from previous page.
    private boolean emptyPage;
    // Version returned from spigot
    private String version;
    // If true updater is going to log progress to the console.
    private final boolean logger;
    // Updater thread
    private Thread thread;

    private File pluginFile;

    private boolean updateInstalled;
    private String newVersion;





    public UpdaterComponent(Plugin plugin, int id, File file, UpdateType updateType, boolean logger) {
        super("Updater");

        this.plugin = plugin;
        this.updateFolder = plugin.getServer().getUpdateFolderFile();
        this.id = id;
        this.file = file;
        this.updateType = updateType;
        this.logger = logger;

        downloadLink = API_RESOURCE + id;
    }


    public String checkForUpdates() {
        if(thread != null && thread.isAlive())
            return "Update check is already running.";

        thread = new Thread(new UpdaterRunnable());
        thread.start();

        String resultMessage = "";
        switch (result) {
            case BAD_ID:
                resultMessage = "Failed to update the plugin: Wrong Spigot ID.";
                break;
            case FAILED:
                resultMessage = "Failed to update the plugin.";
                break;
            case NO_UPDATE:
                resultMessage = "The plugin is up to date.";
                break;
            case SUCCESS:
                resultMessage = "Plugin successfully updated.";
                break;
            case UPDATE_FOUND:
                resultMessage = "Found an update for the plugin.";
                break;
            default:
                resultMessage = "No result for update search.";
                break;
        }

        return resultMessage;
    }

    /**
     * Notify a player that the plugin was updated to a newer version.
     * Only if the player has the permission buildteam.notifyUpdate
     *
     * @param p The Player to notify
     */
    public void notifyUpdate(Player p) {
        if (!updateInstalled)
            return;

        if (p.hasPermission(Permissions.NOTIFY_UPDATE)) {
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
            p.sendMessage("");
            p.sendMessage("§6§l[BuildTeam Plugin] §eThe server automatically installed a new update (v" + newVersion + ").");
            p.sendMessage("§6>> §ePlease restart or reload the server to activate it.");
            p.sendMessage("");
        }
    }

    /**
     * Sets the current version of the plugin that is installed
     *
     * @param newVersion The current installed version
     */
    public void setUpdateInstalled(String newVersion) {
        this.newVersion = newVersion;
        this.updateInstalled = true;

        for (Player p : Bukkit.getOnlinePlayers()) {
            notifyUpdate(p);
        }
    }

    /**
     * Get the result of the update.
     *
     * @return result of the update.
     * @see Result
     */
    public Result getResult() {
        waitThread();
        return result;
    }

    /**
     * Get the latest version from spigot.
     *
     * @return latest version.
     */
    public String getVersion() {
        waitThread();
        return version;
    }

    /**
     * Check if id of resource is valid
     *
     * @param link link of the resource
     * @return true if id of resource is valid
     */
    private boolean checkResource(String link) {
        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent", USER_AGENT);

            int code = connection.getResponseCode();

            if (code != 200) {
                connection.disconnect();
                result = Result.BAD_ID;
                return false;
            }
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Checks if there is any update available.
     */
    private void checkUpdate() {
        try {
            String page = Integer.toString(this.page);

            URL url = new URL(API_RESOURCE + id + VERSIONS + PAGE + page);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent", USER_AGENT);

            InputStream inputStream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);

            JsonElement element = new JsonParser().parse(reader);
            JsonArray jsonArray = element.getAsJsonArray();

            if (jsonArray.size() == 10 && !emptyPage) {
                connection.disconnect();
                this.page++;
                checkUpdate();
            } else if (jsonArray.size() == 0) {
                emptyPage = true;
                this.page--;
                checkUpdate();
            } else if (jsonArray.size() < 10) {
                if (logger)
                    plugin.getLogger().info("Found " + jsonArray.size() + " versions.");
                element = jsonArray.get(jsonArray.size() - 1);
                JsonObject object = element.getAsJsonObject();
                element = object.get("name");

                version = element.toString().replaceAll("\"", "").replace("v", "");
                if (logger) {
                    plugin.getLogger().info("Current version on this server: " + plugin.getDescription().getVersion());
                    plugin.getLogger().info("Latest version available: " + version);
                }
                if (logger)
                    plugin.getLogger().info("Checking for update...");
                if (shouldUpdate(version, plugin.getDescription().getVersion()) && updateType == UpdateType.VERSION_CHECK) {
                    result = Result.UPDATE_FOUND;
                    if (logger)
                        plugin.getLogger().info("Update found!");
                } else if (updateType == UpdateType.DOWNLOAD) {
                    if (logger)
                        plugin.getLogger().info("Downloading update... version not checked");
                    download();
                } else if (updateType == UpdateType.CHECK_DOWNLOAD) {
                    if (shouldUpdate(version, plugin.getDescription().getVersion())) {
                        if (logger)
                            plugin.getLogger().info("Update found, downloading now...");
                        download();
                    } else {
                        if (logger)
                            plugin.getLogger().info("Update not necessary. Plugin is at the latest version.");
                        result = Result.NO_UPDATE;
                    }
                } else {
                    if (logger)
                        plugin.getLogger().info("Update not found");
                    result = Result.NO_UPDATE;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if plugin should be updated
     *
     * @param newVersion remote version
     * @param oldVersion current version
     */
    public boolean shouldUpdate(String newVersion, String oldVersion) {
        // If version has format 1.0.0
        if (newVersion.contains(".")) {
            String[] newVersionSplit = newVersion.split("\\.");
            String[] oldVersionSplit = oldVersion.split("\\.");

            for (int i = 0; i < newVersionSplit.length; i++) {
                try {
                    if (Integer.parseInt(newVersionSplit[i]) > Integer.parseInt(oldVersionSplit[i]))
                        return true;
                    else if (Integer.parseInt(newVersionSplit[i]) < Integer.parseInt(oldVersionSplit[i]))
                        return false;
                } catch (NumberFormatException e) {
                    return !newVersion.equalsIgnoreCase(oldVersion);
                }
            }

            return false;

            // If version is an integer
        } else if (newVersion.matches("[0-9]+")) {
            return Integer.parseInt(newVersion) > Integer.parseInt(oldVersion);

            // If version has a different format
        } else
            return !newVersion.equalsIgnoreCase(oldVersion);
    }

    /**
     * Downloads the file
     */
    private void download() {
        BufferedInputStream in = null;
        FileOutputStream fout = null;

        try {
            URL url = new URL(downloadLink);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent", USER_AGENT);
            InputStream inputStream = connection.getInputStream();

            in = new BufferedInputStream(inputStream);
            if (!updateFolder.exists())
                updateFolder.mkdirs();
            fout = new FileOutputStream(new File(updateFolder, file.getName()));

            final byte[] data = new byte[4096];
            int count;
            while ((count = in.read(data, 0, 4096)) != -1) {
                fout.write(data, 0, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (logger)
                plugin.getLogger().log(Level.SEVERE, "Updater tried to download the update, but was unsuccessful.");
            result = Result.FAILED;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                this.plugin.getLogger().log(Level.SEVERE, null, e);
                e.printStackTrace();
            }
            try {
                if (fout != null) {
                    fout.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
                this.plugin.getLogger().log(Level.SEVERE, null, e);
            }

            setUpdateInstalled(version);
        }
    }

    /**
     * Updater depends on thread's completion, so it is necessary to wait for thread to finish.
     */
    private void waitThread() {
        if (thread != null && thread.isAlive()) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                this.plugin.getLogger().log(Level.SEVERE, null, e);
            }
        }
    }

    public enum UpdateType {
        // Checks only the version
        VERSION_CHECK,
        // Downloads without checking the version
        DOWNLOAD,
        // If updater finds new version automatically it downloads it.
        CHECK_DOWNLOAD

    }

    public enum Result {

        UPDATE_FOUND,

        NO_UPDATE,

        SUCCESS,

        FAILED,

        BAD_ID
    }

    public class UpdaterRunnable implements Runnable {

        public void run() {
            if (checkResource(downloadLink)) {
                downloadLink = downloadLink + DOWNLOAD;
                checkUpdate();
            }
        }
    }
}

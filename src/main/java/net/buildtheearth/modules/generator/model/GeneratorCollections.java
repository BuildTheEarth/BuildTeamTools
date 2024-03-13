package net.buildtheearth.modules.generator.model;

import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.common.CommonModule;
import net.buildtheearth.modules.generator.GeneratorModule;
import net.buildtheearth.utils.ChatHelper;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class GeneratorCollections {

    public static String GENERATOR_COLLECTIONS_VERSION;


    /**
     * Checks if the GeneratorCollections is installed and sends the player a message if it isn't.
     *
     * @param p The player to check for. If null, the console will be used instead.
     * @return Whether the Generator Collections package is installed
     */
    public static boolean checkIfGeneratorCollectionsIsInstalled(@Nullable Player p){
        // Load the schematic file
        try {
            String folder;
            if(CommonModule.getInstance().getDependencyComponent().isFastAsyncWorldEditEnabled())
                folder = "/../FastAsyncWorldEdit/schematics/";
            else if(CommonModule.getInstance().getDependencyComponent().isWorldEditEnabled())
                folder = "/../WorldEdit/schematics/";
            else
                return false;

            String filepath = "GeneratorCollections/treepack/oak41.schematic";
            File myFile = new File(BuildTeamTools.getInstance().getDataFolder().getAbsolutePath() + folder + filepath);

            if(!myFile.exists())
                return installGeneratorCollections(p, false);

            Clipboard clipboard = null;

            // For FastAsyncWorldEdit
            if(CommonModule.getInstance().getDependencyComponent().isFastAsyncWorldEditEnabled()) {
                clipboard = FaweAPI.load(myFile);

            // For Legacy WorldEdit
            }else if(CommonModule.getInstance().getDependencyComponent().isLegacyWorldEdit()) {
                Class<?> formatClass = ClipboardFormat.class;
                Method findByFile = formatClass.getMethod("findByFile", File.class);
                Method getReader = ClipboardFormat.class.getMethod("getReader", InputStream.class);

                ClipboardFormat format = (ClipboardFormat) findByFile.invoke(null, myFile);
                ClipboardReader reader = null;

                if (format != null)
                    reader = (ClipboardReader) getReader.invoke(format, Files.newInputStream(myFile.toPath()));

                BukkitWorld bukkitWorld;
                if(p != null)
                    bukkitWorld = new BukkitWorld(p.getWorld());
                else
                    bukkitWorld = new BukkitWorld(Bukkit.getWorlds().get(0));

                if (reader != null){
                    Class<?> readerClass = reader.getClass();
                    Method read = readerClass.getMethod("read", Class.forName("com.sk89q.worldedit.world.registry.WorldData"));

                    Method getWorldDataMethod = bukkitWorld.getClass().getMethod("getWorldData");
                    Object worldData = getWorldDataMethod.invoke(bukkitWorld);

                    clipboard = (Clipboard) read.invoke(reader, worldData);
                }

            // For latest WorldEdit
            }else if(CommonModule.getInstance().getDependencyComponent().isWorldEditEnabled()) {

                ClipboardFormat format = ClipboardFormats.findByFile(myFile);
                ClipboardReader reader = null;

                if (format != null)
                    reader = format.getReader(Files.newInputStream(myFile.toPath()));

                if (reader != null)
                    clipboard = reader.read();

            }else{
                return false;
            }



            if(clipboard == null)
                return installGeneratorCollections(p, false);
            else
                return checkIfGeneratorCollectionsIsUpToDate(p);

        } catch (Exception e) {
            e.printStackTrace();
            return installGeneratorCollections(p, true);
        }
    }


    /** Returns the latest release version of a repository on GitHub
     *
     * @param owner The owner of the repository
     * @param repo The name of the repository
     * @return The latest release version of the repository
     */
    public static String getRepositoryReleaseVersionString(String owner, String repo){
        try {
            String url = "https://api.github.com/repos/" + owner + "/" + repo + "/releases";

            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONArray releases = new JSONArray(response.toString());
                if (releases.length() > 0) {
                    JSONObject latestRelease = releases.getJSONObject(0); // The first object in the array is the latest release

                    return latestRelease.getString("tag_name").replace("v", "");
                } else
                    return null;
            } else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }




    /** Installs and extracts a zip folder on the system
     *
     * @param filename The name of the zip folder to install. Example: "newtrees.zip"
     * @param path The path to extract the zip folder to. Parent Folder is the plugin folder. Example: "/../WorldEdit/schematics/"
     */
    private static boolean installZipFolder(String parentURL, String filename, String path) throws IOException {
        path = BuildTeamTools.getInstance().getDataFolder().getAbsolutePath() + path;
        String zipFilePath = path + "/" + filename;
        URL url = new URL(parentURL + filename);

        File file = new File(path);
        if(!file.exists()) {
            boolean created = file.mkdir();

            if(!created)
                return false;
        }
        // Open a connection to the URL
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestProperty("User-Agent", "Mozilla/5.0");
        int responseCode = httpConn.getResponseCode();


        // Check HTTP response code, 200 means OK
        if (responseCode == HttpURLConnection.HTTP_OK) {

            // Save the zip file to the path
            try (BufferedInputStream in = new BufferedInputStream(httpConn.getInputStream());
                 FileOutputStream out = new FileOutputStream(path + "/" + filename)) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
        httpConn.disconnect();

        // Extract the zip file
        return unzip(zipFilePath, path);
    }


    /** Extracts a zip folder on the system
     *
     * @param zipFilePath The path to the zip folder. Example: "/../WorldEdit/schematics/newtrees.zip"
     * @param destDirectory The path to extract the zip folder to. Parent Folder is the plugin folder. Example: "/../WorldEdit/schematics/"
     */
    private static boolean unzip(String zipFilePath, String destDirectory) {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            boolean success = destDir.mkdir();
            if(!success)
                return false;
        }

        try (ZipInputStream zipIn = new ZipInputStream(Files.newInputStream(Paths.get(zipFilePath)))) {
            ZipEntry entry = zipIn.getNextEntry();

            while (entry != null) {
                String filePath = destDirectory + File.separator + entry.getName();

                if (!entry.isDirectory()) {
                    File file = new File(filePath);
                    File parentDir = file.getParentFile();
                    if (!parentDir.exists()) {
                        if (!parentDir.mkdirs()) {
                            throw new IOException("Failed to create parent directories for: " + filePath);
                        }
                    }

                    try (BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(Paths.get(filePath)))) {
                        byte[] bytesIn = new byte[4096];
                        int read;
                        while ((read = zipIn.read(bytesIn)) != -1) {
                            bos.write(bytesIn, 0, read);
                        }
                    }
                } else {
                    File dir = new File(filePath);
                    boolean success = dir.mkdirs();

                    if(!success)
                        return false;
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }

            // Delete the old zip file
            deleteFile(zipFilePath);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /** Deletes a directory from the system
     *
     * @param path The path to the directory to delete
     * @return Whether the directory was deleted successfully
     */
    private static boolean deleteDirectory(String path) {
        File dir = new File(path);

        if (dir.isDirectory()) {
            String[] children = dir.list();

            if (children != null)
                for (String child : children) {
                    boolean success = deleteDirectory(new File(dir, child).getAbsolutePath());
                    if (!success) {
                        return false; // Return false if deletion is unsuccessful
                    }
                }
        }
        return dir.delete(); // Return true if directory is deleted successfully
    }

    /**
     * Deletes a file from the system
     *
     * @param path The path to the file to delete
     */
    private static void deleteFile(String path) {
        File file = new File(path);
        boolean success = file.delete();
        if(!success)
            System.out.println("Failed to delete file: " + path);
    }

    /**
     * Checks if the GeneratorCollections is up-to-date and sends the player a message if it isn't.
     *
     * @param p The player to check for
     * @return Whether the Generator Collections package is up-to-date or not
     */
    private static boolean checkIfGeneratorCollectionsIsUpToDate(Player p){
        // Load the schematic file
        try {
            String folder;
            if(CommonModule.getInstance().getDependencyComponent().isFastAsyncWorldEditEnabled())
                folder = "/../FastAsyncWorldEdit/schematics/";
            else if(CommonModule.getInstance().getDependencyComponent().isWorldEditEnabled())
                folder = "/../WorldEdit/schematics/";
            else
                return false;

            String filepath = "GeneratorCollections/";
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(new File(BuildTeamTools.getInstance().getDataFolder().getAbsolutePath() + folder + filepath, "config.yml"));

            if(!cfg.contains("version"))
                return installGeneratorCollections(p, true);

            String oldVersion = cfg.getString("version");

            if(!CommonModule.getInstance().getUpdaterComponent().shouldUpdate(GENERATOR_COLLECTIONS_VERSION, oldVersion))
                return true;
            else
                return installGeneratorCollections(p, true);

        } catch (Exception e) {
            return installGeneratorCollections(p, true);
        }
    }

    /**
     * Sends the player and console a message with more information about the generator collections package in case it isn't installed.
     *
     * @see #checkIfGeneratorCollectionsIsInstalled(Player)
     *
     * @param p The player to send the message to
     */
    private static void sendGeneratorCollectionsError(@Nullable Player p){
        ChatHelper.logPlayerAndConsole(p, "§cAn error occurred while installing the Generator Collections.", Level.INFO);
        ChatHelper.logPlayerAndConsole(p, "§cPlease install the Generator Collections v" + GENERATOR_COLLECTIONS_VERSION + " to use this tool. You can ask the server administrator to install it.", Level.INFO);
        ChatHelper.logPlayerAndConsole(p, " ", Level.INFO);
        ChatHelper.logPlayerAndConsole(p, "§cFor more installation help, please see the wiki:", Level.INFO);
        ChatHelper.logPlayerAndConsole(p, "§c" + GeneratorModule.INSTALL_WIKI, Level.INFO);
    }


    /** Installs or updates the Generator Collections on the system by downloading it from the buildtheearth cdn and extracting it.
     *
     * @param p The player to send the error message to in case the installation fails
     * @param update Whether the Generator Collections package is already installed or should just be updated
     * @return Whether the installation was successful
     */
    private static boolean installGeneratorCollections(@Nullable Player p, boolean update){
        String parentURL = "https://github.com/BuildTheEarth/GeneratorCollections/releases/latest/download/";
        String filename = "GeneratorCollections.zip";
        String fileDirectory = "GeneratorCollections/";

        String path;
        if(CommonModule.getInstance().getDependencyComponent().isFastAsyncWorldEditEnabled())
            path = "/../FastAsyncWorldEdit/schematics/";
        else if(CommonModule.getInstance().getDependencyComponent().isWorldEditEnabled())
            path = "/../WorldEdit/schematics/";
        else
            return false;

        if(update) {
            ChatHelper.logPlayerAndConsole(p, "§cThe Generator Collections package is outdated. Updating...", Level.INFO);

            deleteDirectory(BuildTeamTools.getInstance().getDataFolder().getAbsolutePath() + path + fileDirectory);
        } else
            ChatHelper.logPlayerAndConsole(p, "§cThe Generator Collections package wasn't found on your server. Installing...", Level.INFO);



        try {
            boolean success = installZipFolder(parentURL, filename, path);

            if(success) {
                ChatHelper.logPlayerAndConsole(p, "§7Successfully installed §eGenerator Collections v" + GENERATOR_COLLECTIONS_VERSION + "§7!", Level.INFO);

                return true;
            }else {
                sendGeneratorCollectionsError(p);
                return false;
            }

        } catch (IOException e) {
            sendGeneratorCollectionsError(p);
            return false;
        }
    }

}

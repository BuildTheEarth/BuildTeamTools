package net.buildtheearth.modules.generator.model;

import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import lombok.experimental.UtilityClass;
import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.common.CommonModule;
import net.buildtheearth.utils.ChatHelper;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.codehaus.plexus.util.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jspecify.annotations.NonNull;

import java.io.*;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@UtilityClass
public class GeneratorCollections {

    public static String generatorCollectionsVersion;

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
                    bukkitWorld = new BukkitWorld(Bukkit.getWorlds().getFirst());

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
            BuildTeamTools.getInstance().getComponentLogger().warn("Failed to check if Generator Collections is installed:", e);
            return installGeneratorCollections(p, true);
        }
    }


    /** Returns the latest release version of a repository on GitHub
     *
     * @param owner The owner of the repository
     * @param repo The name of the repository
     * @return The latest release version of the repository
     */
    public static @Nullable String getRepositoryReleaseVersionString(String owner, String repo) {
        try {
            String url = "https://api.github.com/repos/" + owner + "/" + repo + "/releases";

            HttpURLConnection con = (HttpURLConnection) URI.create(url).toURL().openConnection();
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
                if (!releases.isEmpty()) {
                    JSONObject latestRelease = releases.getJSONObject(0); // The first object in the array is the latest release

                    return latestRelease.getString("tag_name").replace("v", "");
                } else
                    return null;
            } else
                return null;
        } catch (Exception e) {
            BuildTeamTools.getInstance().getComponentLogger().warn("Failed to get latest release version of repository {}/{}:", owner, repo, e);
            return null;
        }
    }




    /** Installs and extracts a zip folder on the system
     *
     * @param filename The name of the zip folder to install. Example: "newtrees.zip"
     * @param path The path to extract the zip folder to. Parent Folder is the plugin folder. Example: "/../WorldEdit/schematics/"
     * @param extractionFolder The path where the downloaded zip file is temporarily saved
     */
    private static boolean installZipFolder(String parentURL, String filename, Path path, @NonNull Path extractionFolder) throws IOException {
        if (!extractionFolder.toFile().exists() && !extractionFolder.toFile().mkdirs()) {
            throw new IOException("Failed to create generator module folder: " + extractionFolder);
        }

        var zipFilePath = extractionFolder.resolve(filename);
        URL url = URI.create(parentURL + filename).toURL();

        File file = path.toFile();
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
                 FileOutputStream out = new FileOutputStream(zipFilePath.toFile())) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }catch (Exception e){
                BuildTeamTools.getInstance().getComponentLogger().warn("Failed to download zip file from {}:", url, e);
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
     * @param zipFilePath The path to the zip folder. Example: "/../BuildTeamTols/modules/generator/GeneratorCollections.zip"
     * @param destDirectory The path to extract the zip folder to. Parent Folder is the plugin folder. Example: "/../WorldEdit/schematics/"
     */
    private static boolean unzip(Path zipFilePath, @NonNull Path destDirectory) {
        File destDir = destDirectory.toFile();
        if (!destDir.exists()) {
            boolean success = destDir.mkdir();
            if(!success)
                return false;
        }

        try (ZipInputStream zipIn = new ZipInputStream(Files.newInputStream(zipFilePath))) {
            ZipEntry entry = zipIn.getNextEntry();

            while (entry != null) {
                var filePath = destDirectory.resolve(entry.getName());

                if (!entry.isDirectory()) {
                    File parentDir = filePath.getParent().toFile();
                    if (!parentDir.exists() && !parentDir.mkdirs()) {
                            throw new IOException("Failed to create parent directories for: " + filePath);
                        }

                    try (BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(filePath))) {
                        byte[] bytesIn = new byte[4096];
                        int read;
                        while ((read = zipIn.read(bytesIn)) != -1) {
                            bos.write(bytesIn, 0, read);
                        }
                    }
                } else {
                    boolean success = filePath.toFile().mkdirs();

                    if(!success)
                        return false;
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }

            deleteFile(zipFilePath); // Delete the old zip file
        }catch (Exception e){
            BuildTeamTools.getInstance().getComponentLogger().warn("Failed to unzip zip file:", e);
            return false;
        }

        return true;
    }

    /** Deletes a directory from the system
     *
     * @param path The path to the directory to delete
     */
    private static void deleteDirectory(@NonNull Path path) {
        try {
            FileUtils.deleteDirectory(path.toFile());
        } catch (IOException e) {
            BuildTeamTools.getInstance().getComponentLogger().warn("Failed to delete directory: {}", path);
        }
    }

    /**
     * Deletes a file from the system
     *
     * @param path The path to the file to delete
     */
    private static void deleteFile(@NonNull Path path) {
        try {
            Files.delete(path);
        } catch (IOException e) {
            BuildTeamTools.getInstance().getComponentLogger().warn("Failed to delete file: {}", path, e);
        }
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
            var cfgFile = BuildTeamTools.getInstance().getDataFolder().toPath().resolve("modules").resolve("generator").resolve("generatorCollectionsVersion.yml");
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(cfgFile.toFile());

            if(!cfg.contains("version"))
                return installGeneratorCollections(p, true);

            String oldVersion = cfg.getString("version");

            if (!CommonModule.getInstance().getUpdaterComponent().shouldUpdate(generatorCollectionsVersion, oldVersion))
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
        ChatHelper.logPlayerAndConsole(p, "§cAn error occurred while installing the Generator Collections. Please report that with the log!", Level.INFO);
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

        var path = Bukkit.getPluginsFolder().toPath();
        if(CommonModule.getInstance().getDependencyComponent().isFastAsyncWorldEditEnabled())
            path = path.resolve("FastAsyncWorldEdit").resolve("schematics");
        else if(CommonModule.getInstance().getDependencyComponent().isWorldEditEnabled())
            path = path.resolve("WorldEdit").resolve("schematics");
        else
            return false;

        if(update) {
            ChatHelper.logPlayerAndConsole(p, "§cThe Generator Collections package is outdated. Updating...", Level.INFO);
            deleteDirectory(path.resolve(fileDirectory));
        } else
            ChatHelper.logPlayerAndConsole(p, "§cThe Generator Collections package wasn't found on your server. Installing...", Level.INFO);

        var generatorModulePath = BuildTeamTools.getInstance().getDataFolder().toPath().resolve("modules").resolve("generator");
        try {
            boolean success = installZipFolder(parentURL, filename, path, generatorModulePath);
            if (success)
                success = moveVersionFile(generatorModulePath, path.resolve("GeneratorCollections"), "config.yml", "generatorCollectionsVersion.yml");

            if(success) {
                ChatHelper.logPlayerAndConsole(p, "§7Successfully installed §eGenerator Collections v" + generatorCollectionsVersion + "§7!", Level.INFO);

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

    /**
     * Moves a file from the old directory to the new directory
     *
     * @param newDir      The new directory to move the file to
     * @param oldDir      The old directory to move the file from
     * @param oldFileName The name of the file to move
     * @param newFileName The new name of the file
     * @return Whether the move was successful
     */
    private static boolean moveVersionFile(@NotNull Path newDir, @NotNull Path oldDir, String oldFileName, String newFileName) {
        Path source = oldDir.resolve(oldFileName);
        Path target = newDir.resolve(newFileName);

        try {
            // Ensure target parent exists
            Files.createDirectories(target.getParent());

            // Move with replace to avoid failure if target exists
            Files.move(source, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            return true;
        } catch (IOException e) {
            BuildTeamTools.getInstance().getComponentLogger()
                    .warn("Failed to move '{}' to '{}': {}", source, target, e.toString());
            return false;
        }
    }

}

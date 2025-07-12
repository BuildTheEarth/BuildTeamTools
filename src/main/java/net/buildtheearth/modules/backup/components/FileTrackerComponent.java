package net.buildtheearth.modules.backup.components;

import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.ModuleComponent;
import net.buildtheearth.utils.ChatHelper;
import net.buildtheearth.utils.io.ConfigPaths;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class FileTrackerComponent extends ModuleComponent {

    private static final String DB_FILE = "backup/region_file_tracker.db";
    private static final String TABLE_NAME = "backup_region_table";
    private static final File REGION_FOLDER = getRegionFolder(BuildTeamTools.getInstance().getConfig().getString(ConfigPaths.EARTH_WORLD));

    private Connection connection;

    public FileTrackerComponent() {
        super("FileTracker");
    }

    @Override
    public void enable() {
        super.enable();
        File dbFile = new File(BuildTeamTools.getInstance().getDataFolder(), DB_FILE);
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            initSchema();
        } catch (SQLException e) {
            ChatHelper.logError("Failed to connect to the backup SQLite database.");
        }
    }

    @Override
    public void disable() {
        super.disable();
        try {
            connection.close();
        } catch (SQLException e) {
            ChatHelper.logError("Failed to close the connection to the backup SQLite database.");
        }
    }

    private static File getRegionFolder(String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            ChatHelper.logError("Tried to get region folder for nonexisting world: %s", worldName);
            return null;
        }
        File worldFolder = world.getWorldFolder();
        return new File(worldFolder, "region");
    }


    private void initSchema() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "region TEXT PRIMARY KEY, " +
                "last_uploaded INTEGER NOT NULL" +
                ");";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    public Optional<Long> getLastUploaded(String regionFileName) {
        String sql = "SELECT last_uploaded FROM " + TABLE_NAME + " WHERE region = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, regionFileName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(rs.getLong("last_uploaded"));
            }
        } catch (SQLException e) {
            ChatHelper.logError("Failed to get the last updated time of %s.", regionFileName);
        }
        return Optional.empty();
    }

    public void markUploaded(String regionFileName, long timestamp) {
        String sql = "INSERT INTO " + TABLE_NAME + " (region, last_uploaded) " +
                "VALUES (?, ?) " +
                "ON CONFLICT(region) DO UPDATE SET last_uploaded = excluded.last_uploaded;";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, regionFileName);
            stmt.setLong(2, timestamp);
            stmt.executeUpdate();
        } catch (SQLException e) {
            ChatHelper.logError("Failed to mark %s as uploaded.", regionFileName);
        }
    }

    public List<File> getChangedRegionFiles() {
        List<File> changed = new ArrayList<>();

        File[] files = REGION_FOLDER.listFiles();
        if (files == null) return changed;

        for (File file : files) {
            if (!file.getName().endsWith(".mca")) continue;

            String fileName = file.getName();
            long lastModified = file.lastModified();

            Optional<Long> uploaded = getLastUploaded(fileName);

            // Add to queue if new or modified since last upload
            if (!uploaded.isPresent() || lastModified > uploaded.get()) {
                changed.add(file);
            }
        }

        return changed;
    }
}

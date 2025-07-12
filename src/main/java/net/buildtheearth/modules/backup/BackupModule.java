package net.buildtheearth.modules.backup;

import lombok.Getter;
import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.Module;
import net.buildtheearth.modules.backup.components.FileTrackerComponent;
import net.buildtheearth.modules.backup.components.FileUploadComponent;
import net.buildtheearth.modules.backup.tasks.FileSyncTask;
import org.bukkit.Bukkit;

public final class BackupModule extends Module {

    private static BackupModule instance = null;

    @Getter
    private FileTrackerComponent fileTrackerComponent;

    @Getter
    private FileUploadComponent fileUploadComponent;

    public BackupModule() {
        super("Backup");
    }

    @Override
    public void enable() {
        super.enable();

        fileTrackerComponent = new FileTrackerComponent();
        fileUploadComponent = new FileUploadComponent();

        new FileSyncTask(fileTrackerComponent, fileUploadComponent)
                .runTaskTimerAsynchronously(BuildTeamTools.getInstance(), 0L, 24 * 60 * 60 * 20L); // 24h = 24 * 60 * 60 * 20 ticks
    }

    @Override
    public void registerListeners() {
        // TODO
    }

    @Override
    protected void registerCommands() {
        // TODO
    }

    public static BackupModule getInstance() {
        return instance == null ? instance = new BackupModule() : instance;
    }
}

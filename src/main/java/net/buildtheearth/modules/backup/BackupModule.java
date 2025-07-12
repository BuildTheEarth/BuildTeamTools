package net.buildtheearth.modules.backup;

import lombok.Getter;
import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.Module;
import net.buildtheearth.modules.backup.components.FileTrackerComponent;
import net.buildtheearth.modules.backup.components.FileUploadComponent;
import net.buildtheearth.modules.backup.listeners.ChunkModifyListener;
import net.buildtheearth.modules.backup.listeners.ChunkUnloadListener;
import net.buildtheearth.modules.backup.tasks.FileProcessTask;
import net.buildtheearth.modules.backup.tasks.FileSyncTask;

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
        fileUploadComponent = new FileUploadComponent(fileTrackerComponent);

        new FileSyncTask(fileTrackerComponent, fileUploadComponent)
                .runTaskTimerAsynchronously(BuildTeamTools.getInstance(), 0L, 24 * 60 * 60 * 20L); // 24h = 24 * 60 * 60 * 20 ticks

        new FileProcessTask(fileUploadComponent)
                .runTaskTimerAsynchronously(BuildTeamTools.getInstance(), 0L, 2 * 20L); // 2 seconds : 2 * 20 ticks
    }

    @Override
    public void registerListeners() {
        super.registerListeners(
                new ChunkUnloadListener(fileTrackerComponent, fileUploadComponent),
                new ChunkModifyListener(fileTrackerComponent)
        );
    }

    @Override
    protected void registerCommands() {
        // TODO
    }

    public static BackupModule getInstance() {
        return instance == null ? instance = new BackupModule() : instance;
    }
}

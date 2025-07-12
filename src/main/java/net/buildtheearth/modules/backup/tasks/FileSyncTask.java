package net.buildtheearth.modules.backup.tasks;

import net.buildtheearth.modules.backup.components.FileTrackerComponent;
import net.buildtheearth.modules.backup.components.FileUploadComponent;
import net.buildtheearth.utils.ChatHelper;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.List;

public class FileSyncTask extends BukkitRunnable {

    private final FileTrackerComponent fileTrackerComponent;
    private final FileUploadComponent fileUploadComponent;

    public FileSyncTask(FileTrackerComponent fileTrackerComponent, FileUploadComponent fileUploadComponent) {
        this.fileTrackerComponent = fileTrackerComponent;
        this.fileUploadComponent = fileUploadComponent;
    }

    @Override
    public void run() {
        List<File> changedFiles = fileTrackerComponent.getChangedRegionFiles();

        ChatHelper.log("Backing up %s files...", changedFiles.size());
        try {
            fileUploadComponent.connect();
            for(File file : changedFiles) {
                fileUploadComponent.uploadFile(file);
            }
            fileUploadComponent.disconnect();
        } catch (Exception e) {
            ChatHelper.logError("Failed to back up the unchanged files.");
        }
    }
}

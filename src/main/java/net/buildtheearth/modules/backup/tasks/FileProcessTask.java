package net.buildtheearth.modules.backup.tasks;

import net.buildtheearth.modules.backup.components.FileTrackerComponent;
import net.buildtheearth.modules.backup.components.FileUploadComponent;
import net.buildtheearth.utils.ChatHelper;
import org.bukkit.scheduler.BukkitRunnable;

public class FileProcessTask extends BukkitRunnable {

    private final FileUploadComponent fileUploadComponent;

    public FileProcessTask(FileUploadComponent fileUploadComponent) {
        this.fileUploadComponent = fileUploadComponent;
    }

    @Override
    public void run() {
        try {
            fileUploadComponent.processQueue();
        } catch (Exception e) {
            ChatHelper.logError("Failed to process backup queue.");
        }
    }
}

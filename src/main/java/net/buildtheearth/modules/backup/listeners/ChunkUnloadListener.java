package net.buildtheearth.modules.backup.listeners;

import net.buildtheearth.modules.backup.components.FileTrackerComponent;
import net.buildtheearth.modules.backup.components.FileUploadComponent;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkUnloadListener implements Listener {

    private final FileTrackerComponent fileTrackerComponent;
    private final FileUploadComponent fileUploadComponent;

    public ChunkUnloadListener(FileTrackerComponent fileTrackerComponent, FileUploadComponent fileUploadComponent) {
        this.fileTrackerComponent = fileTrackerComponent;
        this.fileUploadComponent = fileUploadComponent;
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        event.setSaveChunk(true);

        Chunk chunk = event.getChunk();

        if (fileTrackerComponent.getModifiedChunks().contains(chunk)) {
            fileUploadComponent.enqueueByChunk(chunk);
            fileTrackerComponent.getModifiedChunks().remove(chunk);
        }
    }
}

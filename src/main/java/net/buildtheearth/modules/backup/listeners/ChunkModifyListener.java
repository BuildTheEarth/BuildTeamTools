package net.buildtheearth.modules.backup.listeners;

import net.buildtheearth.modules.backup.components.FileTrackerComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.PortalCreateEvent;

public class ChunkModifyListener implements Listener {

    private final FileTrackerComponent fileTrackerComponent;

    public ChunkModifyListener(FileTrackerComponent fileTrackerComponent) {
        this.fileTrackerComponent = fileTrackerComponent;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        fileTrackerComponent.setChunkAsModified(event.getBlock().getChunk());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        fileTrackerComponent.setChunkAsModified(event.getBlock().getChunk());
    }

    @EventHandler
    public void onPortalCreate(PortalCreateEvent event) {
        fileTrackerComponent.setChunkAsModified(event.getBlocks().get(0).getChunk());
    }

    // TODO ADD MORE EVENTS THAT MODIFY A CHUNK
}

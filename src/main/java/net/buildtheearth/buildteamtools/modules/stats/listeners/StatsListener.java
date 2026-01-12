package net.buildtheearth.buildteamtools.modules.stats.listeners;

import net.buildtheearth.buildteamtools.modules.stats.StatsModule;
import net.buildtheearth.buildteamtools.modules.stats.model.StatsPlayerType;
import net.buildtheearth.buildteamtools.modules.stats.model.StatsServerType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Handles stats tracking
 */
public class StatsListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        StatsModule.getInstance().getStatsServer().addValue(StatsServerType.JOINS, 1);
        StatsModule.getInstance().getStatsPlayer(e.getPlayer().getUniqueId()).addValue(StatsPlayerType.JOINS, 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.isCancelled())
            return;

        StatsModule.getInstance().getStatsServer().addValue(StatsServerType.BROKEN_BLOCKS, 1);
        StatsModule.getInstance().getStatsPlayer(e.getPlayer().getUniqueId()).addValue(StatsPlayerType.BROKEN_BLOCKS, 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent e) {
        if (e.isCancelled())
            return;

        StatsModule.getInstance().getStatsServer().addValue(StatsServerType.PLACED_BLOCKS, 1);
        StatsModule.getInstance().getStatsPlayer(e.getPlayer().getUniqueId()).addValue(StatsPlayerType.PLACED_BLOCKS, 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMessage(AsyncPlayerChatEvent e) {
        StatsModule.getInstance().getStatsServer().addValue(StatsServerType.MESSAGES, 1);
        StatsModule.getInstance().getStatsPlayer(e.getPlayer().getUniqueId()).addValue(StatsPlayerType.MESSAGES, 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (e.isCancelled())
            return;

        StatsModule.getInstance().getStatsServer().addValue(StatsServerType.COMMANDS, 1);
        StatsModule.getInstance().getStatsPlayer(e.getPlayer().getUniqueId()).addValue(StatsPlayerType.COMMANDS, 1);

        if (e.getMessage().startsWith("//")) {
            StatsModule.getInstance().getStatsServer().addValue(StatsServerType.WORLD_EDIT_COMMANDS, 1);
            StatsModule.getInstance().getStatsPlayer(e.getPlayer().getUniqueId()).addValue(StatsPlayerType.WORLD_EDIT_COMMANDS, 1);
        }
    }
}

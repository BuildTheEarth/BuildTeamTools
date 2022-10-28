package net.buildtheearth.buildteam.listeners;

import net.buildtheearth.Main;
import net.buildtheearth.buildteam.components.stats.StatsPlayerType;
import net.buildtheearth.buildteam.components.stats.StatsServerType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class Stats_Listener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e){
        Main.buildTeamTools.getBTENetwork().getStatsManager().getStatsServer().addValue(StatsServerType.JOINS, 1);
        Main.buildTeamTools.getBTENetwork().getStatsManager().getStatsPlayer(e.getPlayer().getUniqueId()).addValue(StatsPlayerType.JOINS, 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent e){
        if(e.isCancelled())
            return;

        Main.buildTeamTools.getBTENetwork().getStatsManager().getStatsServer().addValue(StatsServerType.BROKEN_BLOCKS, 1);
        Main.buildTeamTools.getBTENetwork().getStatsManager().getStatsPlayer(e.getPlayer().getUniqueId()).addValue(StatsPlayerType.BROKEN_BLOCKS, 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent e){
        if(e.isCancelled())
            return;

        Main.buildTeamTools.getBTENetwork().getStatsManager().getStatsServer().addValue(StatsServerType.PLACED_BLOCKS, 1);
        Main.buildTeamTools.getBTENetwork().getStatsManager().getStatsPlayer(e.getPlayer().getUniqueId()).addValue(StatsPlayerType.PLACED_BLOCKS, 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMessage(AsyncPlayerChatEvent e){
        Main.buildTeamTools.getBTENetwork().getStatsManager().getStatsServer().addValue(StatsServerType.MESSAGES, 1);
        Main.buildTeamTools.getBTENetwork().getStatsManager().getStatsPlayer(e.getPlayer().getUniqueId()).addValue(StatsPlayerType.MESSAGES, 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCommand(PlayerCommandPreprocessEvent e){
        if(e.isCancelled())
            return;

        Main.buildTeamTools.getBTENetwork().getStatsManager().getStatsServer().addValue(StatsServerType.COMMANDS, 1);
        Main.buildTeamTools.getBTENetwork().getStatsManager().getStatsPlayer(e.getPlayer().getUniqueId()).addValue(StatsPlayerType.COMMANDS, 1);

        if(e.getMessage().startsWith("//")){
            Main.buildTeamTools.getBTENetwork().getStatsManager().getStatsServer().addValue(StatsServerType.WORLD_EDIT_COMMANDS, 1);
            Main.buildTeamTools.getBTENetwork().getStatsManager().getStatsPlayer(e.getPlayer().getUniqueId()).addValue(StatsPlayerType.WORLD_EDIT_COMMANDS, 1);
        }
    }
}

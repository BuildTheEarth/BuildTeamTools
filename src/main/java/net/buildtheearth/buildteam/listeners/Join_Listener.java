package net.buildtheearth.buildteam.listeners;

import net.buildtheearth.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Join_Listener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();

        //Send ping to the proxy to add player to the communicators list if response on ping is received
        Main.getBuildTeam().getBTENetwork().ping(p);

        //Notify the admins if a new update got installed automatically
        Main.buildTeamTools.notifyUpdate(p);

        Main.buildTeamTools.fetchAndLoadPreferences(p.getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();

        //Remove player from the communicator list
        Main.getBuildTeam().getBTENetwork().getCommunicators().remove(p.getUniqueId());
    }

}

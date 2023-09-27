package net.buildtheearth.buildteam.listeners;

import net.buildtheearth.Main;
import net.buildtheearth.buildteam.Network;
import net.buildtheearth.buildteam.components.universal_experience.universal_tpll.TpllListener;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Join_Listener implements Listener
{
    private Network network;

    public Join_Listener(Network network)
    {
        this.network = network;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();

        //Send ping to the proxy to add player to the communicators list if response on ping is received
        Main.getBuildTeam().getBTENetwork().ping(p);

        //Notify the admins if a new update got installed automatically
        Main.buildTeamTools.notifyUpdate(p);

        network.fetchAndLoadPreferences(p.getUniqueId());

        //Deals with universal tpll event
        Location tpllTarget = network.getTpllEvents().get(p.getUniqueId());
        if (tpllTarget != null)
        {
            //Teleports the player
            if (tpllTarget.getWorld() == null)
                p.sendMessage(ChatColor.RED +"The earth world for this build team is unknown");
            else
                p.teleport(tpllTarget);

            //Removes the tpll event from the list
            network.getTpllEvents().remove(p.getUniqueId());
        }

        //Deals with universal warps event
        Location location = network.getWarpEvents().get(p.getUniqueId());
        if (location != null)
        {
            //Teleports the player
            if (location.getWorld() == null)
                p.sendMessage(ChatColor.RED +"The earth world for this build team is unknown");
            else
                p.teleport(location);

            //Removes the tpll event from the list
            network.getWarpEvents().remove(p.getUniqueId());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e)
    {
        Player p = e.getPlayer();

        //Remove player from the communicator list
        Main.getBuildTeam().getBTENetwork().getCommunicators().remove(p.getUniqueId());
    }

}

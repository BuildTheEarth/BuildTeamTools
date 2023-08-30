package net.buildtheearth.buildteam.components;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import lombok.Setter;
import net.buildtheearth.Main;
import net.buildtheearth.buildteam.components.stats.StatsManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BTENetwork {

    public static int CACHE_UPLOAD_SPEED = 20*60*10 + 20;

    @Setter @Getter
    private String buildTeamID;
    @Setter @Getter
    private String serverID;
    @Getter
    private List<UUID> communicators;

    @Getter
    private StatsManager statsManager;

    @Getter
    private final boolean bIsConnected;

    public BTENetwork(){
        bIsConnected = start();
    }

    public boolean start(){
        communicators = new ArrayList<>();
        statsManager = new StatsManager();

        //Ping all online players on startup
        for(Player p : Bukkit.getOnlinePlayers())
            ping(p);

        return (communicators.size() > 0);
    }

    public boolean isConnected() {
        return bIsConnected;
    }

    /** Sends a ping to the network.
     *  If the player is playing over the network the proxy will answer with another ping message.
     *  Afterwards the player will be added to the communicators list.
     *
     * @param p: Player who executes the ping.
     */
    public void ping(Player p){
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Ping");
        out.writeUTF(p.getUniqueId().toString());
        out.writeUTF("Version: " + Main.instance.getDescription().getVersion());
        p.sendPluginMessage(Main.instance, "BuildTeam", out.toByteArray());
    }

    /** Sends the current cache to the network.
     *  Once it was received it confirms that and the cache gets resetted.
     *
     * @return true if success, false if failed
     */
    public boolean update(){
        if(getCommunicators().size() == 0)
            return false;

        if(!Main.instance.isEnabled())
            return false;

        Player p = Bukkit.getPlayer(getCommunicators().get(0));

        if(p == null) {
            getCommunicators().remove(0);
            return false;
        }
        if(!p.isOnline()) {
            getCommunicators().remove(0);
            return false;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Stats");
        out.writeUTF(p.getUniqueId().toString());
        out.writeUTF(getStatsManager().getCurrentCache().toJSONString());
        p.sendPluginMessage(Main.instance, "BuildTeam", out.toByteArray());

        return true;
    }
}

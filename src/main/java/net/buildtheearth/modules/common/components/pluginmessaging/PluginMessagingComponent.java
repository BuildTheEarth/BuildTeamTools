package net.buildtheearth.modules.common.components.pluginmessaging;

import com.alpsbte.alpslib.utils.ChatHelper;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.ModuleComponent;
import net.buildtheearth.modules.navigation.NavigationModule;
import net.buildtheearth.modules.network.NetworkModule;
import net.buildtheearth.modules.stats.StatsModule;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.List;
import java.util.UUID;

public class PluginMessagingComponent extends ModuleComponent implements PluginMessageListener {

    public PluginMessagingComponent() {
        super("Plugin Messages");

        // Register an incoming & outgoing Plugin Messaging Channel
        BuildTeamTools.getInstance().getServer().getMessenger().registerOutgoingPluginChannel(BuildTeamTools.getInstance(), "BungeeCord");
        BuildTeamTools.getInstance().getServer().getMessenger().registerIncomingPluginChannel(BuildTeamTools.getInstance(), "BungeeCord", this);
        BuildTeamTools.getInstance().getServer().getMessenger().registerOutgoingPluginChannel(BuildTeamTools.getInstance(), "btt:buildteam");
        BuildTeamTools.getInstance().getServer().getMessenger().registerIncomingPluginChannel(BuildTeamTools.getInstance(), "btt:buildteam", this);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (channel.equals("BuildTeam")) {

            // If the player is not on the list of people communicating with the network, add his uuid to that list
            List<UUID> communicators = NetworkModule.getInstance().getCommunicators();
            if (!communicators.contains(player.getUniqueId())) {
                communicators.add(player.getUniqueId());
            }

            // Read the incoming data
            ByteArrayDataInput in = ByteStreams.newDataInput(message);
            String subChannel = in.readUTF();

            ChatHelper.logDebug("Plugin Message received: %s from Player: %s", subChannel, player.getName());

            if (subChannel.equalsIgnoreCase("Ping")) {
                // Do something? not sure what tbh
            }

            // Reset the stats cache
            if (subChannel.equalsIgnoreCase("Stats")) {
                String status = in.readUTF();
                if (status.equals("OK")) {
                    StatsModule.getInstance().resetCache();
                }
            }

            // Add a new universal tpll target to the queue
            if (subChannel.equals("TPLL")) {
                NavigationModule.getInstance().getTpllComponent().addTpllToQueue(in, player);
            }

            // Add a new universal warp target to the queue
            if (subChannel.equals("UniversalWarps")) {
                NavigationModule.getInstance().getWarpsComponent().addWarpToQueue(in, player);
            }
        }
    }
}

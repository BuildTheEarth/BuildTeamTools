package net.buildtheearth.modules.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.buildtheearth.Main;
import org.bukkit.entity.Player;

/**
 * Functions related to Network wide functionality
 */
public class ProxyUtil
{
    /**
     * Sends a player server switch request to the proxy. The player will then be sent to the target server.
     * @param player The player aiming to switch server
     * @param szServer The name of the server the player must switch to
     */
    public static void SwitchServer(Player player, String szServer)
    {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(szServer);
        player.sendPluginMessage(Main.instance, "BungeeCord", out.toByteArray());
    }
}

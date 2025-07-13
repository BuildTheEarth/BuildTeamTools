package net.buildtheearth.modules.navigation;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.network.NetworkModule;
import net.buildtheearth.modules.network.model.BuildTeam;
import net.buildtheearth.utils.ChatHelper;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.UnsafeValues;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NavUtils {
    public static void sendPlayerToConnectedServer(Player player, String server) {
        if (NetworkModule.getInstance().getBuildTeam() == null
                || !NetworkModule.getInstance().getBuildTeam().isConnected())
            return;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        player.sendPluginMessage(BuildTeamTools.getInstance(), "BungeeCord", out.toByteArray());
    }

    public static boolean isTransferCapable(@NotNull Player clickPlayer, @NotNull BuildTeam targetBuildTeam) {
        // Must be 1.20.5 / 766 or higher for transfer capability
        int playerVersion = clickPlayer.getProtocolVersion();
        UnsafeValues unsafeValues = Bukkit.getUnsafe();
        int serverProtocolVersion = unsafeValues.getProtocolVersion();

        ChatHelper.logDebug("Transfer check - Player protocol: %d, Server protocol: %s, Allows transfers:  %b", playerVersion, serverProtocolVersion, targetBuildTeam.isAllowsTransfers());

        return targetBuildTeam.isAllowsTransfers() && playerVersion >= 766 && serverProtocolVersion >= 766;
    }

    /**
     * Sends a message to the player that the server is not connected to the network yet.
     * Instead of the server name the server IP will be displayed so the player can join the other server ip manually.
     *
     * @param player   The player to send the message to
     * @param serverIP The IP of the server the player should join
     */
    public static void sendNotConnectedMessage(@NotNull Player player, String serverIP, String teamName) {
        TextComponent comp = new TextComponent("§e" + serverIP + " §7(Click to copy)");
        comp.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, serverIP));
        comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§eClick to copy").create()));

        String notConnected = "§cThe team " + teamName + " does not run on Minecraft Version 1.20.5+ yet & is not connected to the network. Meanwhile please use this Server IP to connect to their server:";

        player.closeInventory();
        player.sendMessage(notConnected);
        player.sendMessage("");
        player.spigot().sendMessage(comp);
        player.sendMessage("");
        player.sendMessage("§cClick the IP to copy → Press ESC → Back to Menu → Multiplayer → Add Server → Ctrl+V → Done → Join.");
    }

    /**
     * Sends a message to the player that the build team have no ip specified.
     *
     * @param player    The player to send the message to
     * @param buildteam The name of the buildteam the player should join
     */
    public static void sendNoIpMessage(@NotNull Player player, String buildteam) {
        player.sendMessage("IP of " + buildteam + " is missing in Network API.");
    }
}

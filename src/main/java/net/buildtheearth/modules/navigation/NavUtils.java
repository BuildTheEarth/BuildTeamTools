package net.buildtheearth.modules.navigation;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.experimental.UtilityClass;
import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.navigation.components.warps.model.WarpGroup;
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
import org.jetbrains.annotations.Nullable;

@UtilityClass
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

    public static boolean isTransferCapable(@NotNull Player player, @NotNull BuildTeam targetBuildTeam) {
        // Must be 1.20.5 / 766 or higher for transfer capability
        int playerVersion = player.getProtocolVersion();
        UnsafeValues unsafeValues = Bukkit.getUnsafe();
        int serverProtocolVersion = unsafeValues.getProtocolVersion();

        ChatHelper.logDebug("Transfer check - Player protocol: %d, Server protocol: %s, BuildTeam: %s, Allows transfers:  %b, IP: %s", playerVersion, serverProtocolVersion, targetBuildTeam.getBlankName(), targetBuildTeam.isAllowsTransfers(), targetBuildTeam.getIP());

        return targetBuildTeam.isAllowsTransfers() && playerVersion >= 766 && serverProtocolVersion >= 766;
    }

    public static void transferPlayer(@NotNull Player player, @NotNull String ip) {
        int sep = ip.indexOf(':');
        String server = sep >= 0 ? ip.substring(0, sep) : ip;
        int port = sep >= 0 ? Integer.parseInt(ip.substring(sep + 1)) : 25565;
        player.transfer(server, port);
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
        player.sendMessage("§cPress Ctrl + A and Ctrl + C to copy the ip → Press ESC → Back to Menu → Multiplayer → Add Server → Ctrl+V → Done → Join.");
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

    public static @Nullable NavSwitchType determineSwitchPossibilityOrMsgPlayerIfNone(@NotNull Player player, @NotNull BuildTeam targetBuildTeam) {
        if (targetBuildTeam.isConnected() && targetBuildTeam.getServerName() != null) {
            return NavSwitchType.NETWORK;
        } else if (targetBuildTeam.getIP() != null) {
            if (isTransferCapable(player, targetBuildTeam)) {
                return NavSwitchType.TRANSFER;
            } else {
                sendNotConnectedMessage(player, targetBuildTeam.getIP(), targetBuildTeam.getName());
                return null;
            }
        } else {
            sendNoIpMessage(player, targetBuildTeam.getName());
            return null;
        }
    }

    public enum NavSwitchType {
        TRANSFER, NETWORK
    }

    public static void switchToTeam(BuildTeam team, Player clickPlayer) {
        var type = NavUtils.determineSwitchPossibilityOrMsgPlayerIfNone(clickPlayer, team);

        if (type != null) {
            if (type == NavUtils.NavSwitchType.NETWORK) {
                NavUtils.sendPlayerToConnectedServer(clickPlayer, team.getServerName());
            } else if (type == NavUtils.NavSwitchType.TRANSFER) {
                NavUtils.transferPlayer(clickPlayer, team.getIP());
            }
        }
    }

    public static @NotNull WarpGroup createOtherWarpGroup() {
        // Create an "other" Warp Group for warps that don't belong to a warp group
        return new WarpGroup(NetworkModule.getInstance().getBuildTeam(), "Other", "Other warps", -1, null);
    }
}

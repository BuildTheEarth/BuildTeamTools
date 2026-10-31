package net.buildtheearth.buildteamtools.modules.navigation;

import com.alpsbte.alpslib.utils.ChatHelper;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.experimental.UtilityClass;
import net.buildtheearth.OutOfProjectionBoundsException;
import net.buildtheearth.Projection;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import net.buildtheearth.buildteamtools.i18n.Messages;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.WarpGroup;
import net.buildtheearth.buildteamtools.modules.network.NetworkModule;
import net.buildtheearth.buildteamtools.modules.network.model.BuildTeam;
import net.buildtheearth.buildteamtools.modules.network.model.Region;
import net.buildtheearth.model.GeographicalCoordinate;
import net.buildtheearth.model.MinecraftCoordinate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.UnsafeValues;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

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

        ChatHelper.logDebug("Transfer check - Player protocol: %d, Server protocol: %s, BuildTeam: %s, Allows transfers:  %b, " +
                        "IP: %s", playerVersion, serverProtocolVersion, targetBuildTeam.getBlankName(),
                targetBuildTeam.isAllowsTransfers(), targetBuildTeam.getIP());

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
        Component ipLine = Messages.component(MessageKeys.Navigation.NOT_CONNECTED_IP, Component.text(serverIP))
                .clickEvent(ClickEvent.suggestCommand(serverIP))
                .hoverEvent(HoverEvent.showText(Messages.component(MessageKeys.Navigation.NOT_CONNECTED_HOVER)));

        player.closeInventory();
        player.sendMessage(Messages.component(MessageKeys.Navigation.NOT_CONNECTED, Component.text(teamName)));
        player.sendMessage(Component.empty());
        player.sendMessage(ipLine);
        player.sendMessage(Component.empty());
        player.sendMessage(Messages.component(MessageKeys.Navigation.NOT_CONNECTED_HINT));
    }

    /**
     * Sends a message to the player that the build team have no ip specified.
     *
     * @param player    The player to send the message to
     * @param buildteam The name of the buildteam the player should join
     */
    public static void sendNoIpMessage(@NotNull Player player, String buildteam) {
        player.sendMessage(Messages.component(MessageKeys.Navigation.NO_IP, Component.text(buildteam)));
    }

    /**
     * Sends a message when a connected build team's main IP cannot be mapped to a network server.
     */
    public static void sendInvalidNetworkMappingMessage(@NotNull Player player, String buildteam) {
        player.sendMessage(Messages.component(MessageKeys.Navigation.INVALID_NETWORK_MAPPING, Component.text(buildteam)));
    }

    public static @Nullable NavSwitchType determineSwitchPossibilityOrMsgPlayerIfNone(@NotNull Player player,
                                                                                      @NotNull BuildTeam targetBuildTeam) {
        if (targetBuildTeam.isNetworkMappingMismatch()) {
            sendInvalidNetworkMappingMessage(player, targetBuildTeam.getBlankName());
            return null;
        }

        if (targetBuildTeam.isConnected() && targetBuildTeam.getServerName() != null && NetworkModule.getInstance().getBuildTeam() != null && NetworkModule.getInstance().getBuildTeam().isConnected()) {
            return NavSwitchType.NETWORK;
        }

        if (targetBuildTeam.getIP() == null) {
            sendNoIpMessage(player, targetBuildTeam.getName());
            return null;
        }

        if (isTransferCapable(player, targetBuildTeam)) {
            return NavSwitchType.TRANSFER;
        }

        sendNotConnectedMessage(player, targetBuildTeam.getIP(), targetBuildTeam.getName());
        return null;
    }

    public enum NavSwitchType {
        TRANSFER, NETWORK
    }

    public static void switchToTeam(BuildTeam team, Player clickPlayer) {
        NavSwitchType type = determineSwitchPossibilityOrMsgPlayerIfNone(clickPlayer, team);
        if (type == null) {
            return;
        }

        switch (type) {
            case NETWORK -> sendPlayerToConnectedServer(clickPlayer, team.getServerName());
            case TRANSFER -> transferPlayer(clickPlayer, team.getIP());
        }
    }

    public static @NotNull WarpGroup createOtherWarpGroup(BuildTeam team) {
        // Create an "other" Warp Group for warps that don't belong to a warp group
        return new WarpGroup(team, "Other", Messages.plain(MessageKeys.Warp.OTHER_GROUP_DESCRIPTION), -1, null);
    }

    /**
     * Creates a minecraft location object for the specified coordinates, yaw and pitch from the BTE projection.
     * Height is extracted from the world.
     * <p>
     * Note: Height returned is actually terrain elevation +2. This is because this method internally uses
     * Bukkits @see World::getHighestBlockYAt() already returns elevation+1, and this method deliberately
     * adds one to the location elevation on top.
     * </p>
     * The world is extracted from the server config's "earth world". If no earth world is specified then the height defaults
     * to 64
     * and the world is nullified.
     *
     * @param coordinate Latitude and longitude of the location
     * @param yaw        Player's yaw
     * @param pitch      Player's pitch
     * @return A bukkit location matching the coordinates, yaw and pitch specified. Height is terrain elevation +2.
     */
    @Contract("_, _, _ -> new")
    public static @NonNull Location getLocationFromCoordinatesYawPitch(GeographicalCoordinate coordinate, float yaw, float pitch) {
        try {
            MinecraftCoordinate mcCoord = Projection.toMinecraft(coordinate);

            World earthWorld = BuildTeamTools.getInstance().getEarthWorld();
            int y = 64;

            if (earthWorld != null)
                y = earthWorld.getHighestBlockYAt(mcCoord.blockX(), mcCoord.blockZ()) + 1;

            return new Location(earthWorld, mcCoord.x(), y, mcCoord.z(), yaw, pitch);
        } catch (OutOfProjectionBoundsException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a minecraft location object for the specified coordinates from the BTE projection.
     * Height is extracted from the world.
     * <p>
     * Note: Height returned is actually terrain elevation +2. This is because this method internally uses
     * Bukkits @see World::getHighestBlockYAt() already returns elevation+1, and this method deliberately
     * adds one to the location elevation on top.
     * </p>
     * The world is extracted from the server config's "earth world". If no earth world is specified then the height defaults
     * to 64
     * and the world is nullified.
     *
     * @param coordinate Latitude and longitude of the location
     * @return A bukkit location matching the coordinates. Height is terrain elevation +2.
     */
    @Contract("_ -> new")
    public static @NonNull Location getLocationFromCoordinates(GeographicalCoordinate coordinate) {
        return getLocationFromCoordinatesYawPitch(coordinate, 0, 0);
    }

    /**
     * Returns the CCA2 code of the country of the given country name.
     */
    public static String getCCA2FromCountryName(String countryName, Player clickPlayer) {
        Region region = findRegionByName(countryName);
        if (region != null) {
            return region.getCountryCodeCca2();
        }

        clickPlayer.sendMessage(Messages.component(MessageKeys.Navigation.COUNTRY_NOT_FOUND));
        return "";
    }

    private static @Nullable Region findRegionByName(String countryName) {
        BuildTeam buildTeam = NetworkModule.getInstance().getBuildTeam();
        if (buildTeam == null) {
            return null;
        }

        return buildTeam.getRegions().stream()
                .filter(region -> region.getName().equals(countryName))
                .findFirst()
                .orElse(null);
    }
}

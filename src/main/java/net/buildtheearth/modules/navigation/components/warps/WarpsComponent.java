package net.buildtheearth.modules.navigation.components.warps;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.ModuleComponent;
import net.buildtheearth.modules.navigation.NavUtils;
import net.buildtheearth.modules.navigation.components.warps.menu.WarpEditMenu;
import net.buildtheearth.modules.navigation.components.warps.menu.WarpGroupEditMenu;
import net.buildtheearth.modules.navigation.components.warps.menu.WarpGroupMenu;
import net.buildtheearth.modules.navigation.components.warps.menu.WarpMenu;
import net.buildtheearth.modules.navigation.components.warps.model.Warp;
import net.buildtheearth.modules.navigation.components.warps.model.WarpGroup;
import net.buildtheearth.modules.network.NetworkModule;
import net.buildtheearth.modules.network.api.OpenStreetMapAPI;
import net.buildtheearth.modules.network.model.BuildTeam;
import net.buildtheearth.modules.network.model.Continent;
import net.buildtheearth.utils.ChatHelper;
import net.buildtheearth.utils.GeometricUtils;
import net.buildtheearth.utils.geo.CoordinateConversion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class WarpsComponent extends ModuleComponent {

    public static final NamespacedKey WARP_COOKIE_KEY = NamespacedKey.minecraft("btt_warp");

    public WarpsComponent() {
        super("Warps");
    }

    /**
     * Stores a List of the warp operations that need to happen on join
     */
    private final HashMap<UUID, Location> warpQueue = new HashMap<>();

    /**
     * Adds a warp operation to the queue
     *
     * @param in     The ByteArray received through the PluginMessageChannel
     * @param player The player to whom the warp operation belongs
     */
    public void addWarpToQueue(@NotNull ByteArrayDataInput in, Player player) {
        // Extracts the warp key from the plugin message
            String warpKey = in.readUTF();
        Warp warp = getWarpByKey(warpKey);

            if (warp == null) {
                player.sendMessage(ChatHelper.getErrorString("The warp you tried to warp to does not exist anymore."));
                return;
            }

            Location targetWarpLocation = GeometricUtils.getLocationFromCoordinatesYawPitch(new double[]{warp.getLat(), warp.getLon()}, warp.getYaw(), warp.getPitch());
            targetWarpLocation.setY(warp.getY());
            targetWarpLocation.setWorld(Bukkit.getWorld(warp.getWorldName()));

            // Adds the event to the list, to be dealt with by the join listener
            warpQueue.put(player.getUniqueId(), targetWarpLocation);
    }

    /**
     * Checks if there is a warp in the queue of the current server and teleports the player if this is the case
     * @param player the player to check the queue for
     */
    public void processQueueForPlayer(Player player) {
        Location targetWarpLocation = warpQueue.get(player.getUniqueId());

        if (targetWarpLocation == null) {
            return;
        }

        if(player.teleport(targetWarpLocation)) {
            ChatHelper.sendSuccessfulMessage(player, "Successfully warped you to the desired location!");
        } else {
            player.sendMessage(ChatHelper.getErrorString("Something went wrong trying to warp you to the desired location."));
        }

        warpQueue.remove(player.getUniqueId());
    }


    /**
     * Sends a plugin message to add the warp to the queue of the target server
     * Then switches the player to that server
     * @param player The player to warp
     * @param warp The warp to teleport the player to
     */
    public void warpPlayer(Player player, @NotNull Warp warp) {
        // If the warp is in the same team, just teleport the player
        if(warp.getWarpGroup().getBuildTeam().getID().equals(NetworkModule.getInstance().getBuildTeam().getID())) {
            ChatHelper.logDebug("Warping player %s to warp %s", player.getName(), warp.getName());
            Location loc = GeometricUtils.getLocationFromCoordinatesYawPitch(new double[]{warp.getLat(), warp.getLon()}, warp.getYaw(), warp.getPitch());

            if(loc.getWorld() == null) {
                World world = Bukkit.getWorld(warp.getWorldName()) == null ? player.getWorld() : Bukkit.getWorld(warp.getWorldName());
                loc.setWorld(world);
            }

            loc.setY(warp.getY());

            player.teleportAsync(loc);
            ChatHelper.sendSuccessfulMessage(player, "Successfully warped you to %s.", warp.getName());
            return;
        }

        ChatHelper.logDebug("Determining switch possibility for warp %s, because it's the wrong server...", warp.getName());

        var type = NavUtils.determineSwitchPossibilityOrMsgPlayerIfNone(player, warp.getWarpGroup().getBuildTeam());

        if (type == NavUtils.NavSwitchType.NETWORK) {
            // Send a plugin message to that server which adds the warp to the queue
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("UniversalWarps");
            out.writeUTF(warp.getId().toString());

            player.sendPluginMessage(BuildTeamTools.getInstance(), "btt:buildteam", out.toByteArray());

            // Switch the player to the target server
            NetworkModule.getInstance().switchServer(player, warp.getWarpGroup().getBuildTeam().getServerName());
        } else if (type == NavUtils.NavSwitchType.TRANSFER) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF(String.valueOf(warp.getId()));
            player.storeCookie(WARP_COOKIE_KEY, out.toByteArray());
            NavUtils.transferPlayer(player, warp.getWarpGroup().getBuildTeam().getIP());
        }
    }

    public WarpGroup getOtherWarpGroup() {
        return NetworkModule.getInstance().getBuildTeam().getWarpGroups().stream().filter(warpGroup -> warpGroup.getName().equalsIgnoreCase("Other")).findFirst().orElse(null);
    }

    public void createWarp(Player creator) {
        WarpGroup group = getOtherWarpGroup();
        if (group == null) {
            group = NavUtils.createOtherWarpGroup();
        }
        createWarp(creator, group);
    }

    /** Creates a warp at the player's location and opens the warp edit menu.
     *
     * @param creator The player that is creating the warp
     */
    public void createWarp(Player creator, WarpGroup group) {
        // Get the geographic coordinates of the player's location.
        Location location = creator.getLocation();
        double[] coordinates = CoordinateConversion.convertToGeo(location.getX(), location.getZ());

        //Get the country belonging to the coordinates
        CompletableFuture<String[]> future = OpenStreetMapAPI.getCountryFromLocationAsync(coordinates);

        future.thenAccept(result -> {
            String regionName = result[0];
            String countryCodeCCA2 = result[1].toUpperCase();

            //Check if the team owns this region/country
            boolean ownsRegion = NetworkModule.getInstance().ownsRegion(regionName, countryCodeCCA2);

            if(!ownsRegion) {
                creator.sendMessage(ChatHelper.getErrorString("This team does not own the country %s!", result[0]));
                return;
            }

            // Create a default name for the warp
            String name = creator.getName() + "'s Warp";

            // Create an instance of the warp POJO
            Warp warp = new Warp(group, name, countryCodeCCA2, "cca2", null, null, null, location.getWorld().getName(), coordinates[0], coordinates[1], location.getY(), location.getYaw(), location.getPitch(), false);

            Bukkit.getScheduler().runTask(BuildTeamTools.getInstance(), () ->
                    new WarpEditMenu(creator, warp, false, true));

        }).exceptionally(e -> {
            creator.sendMessage(ChatHelper.getErrorString("An error occurred while creating the warp! %s", e.getMessage()));
            BuildTeamTools.getInstance().getComponentLogger().error("An error occurred while creating the warp!", e);
            return null;
        });
    }


    public void createWarpGroup(@NotNull Player creator) {
        // Create a default name for the warp
        String name = creator.getName() + "'s Warp Group";
        String description = "This is a warp group.";
        int slot = -1;

        WarpGroup warpGroup = new WarpGroup(NetworkModule.getInstance().getBuildTeam(), name, description, slot, null);

        new WarpGroupEditMenu(creator, warpGroup, false, true);
    }


    // ------------------------- //
    //          GETTER           //
    // ------------------------- //

    public Warp getWarpByName(String name){
        return getWarpByName(NetworkModule.getInstance().getBuildTeam(), name);
    }

    public Warp getWarpByName(@NotNull BuildTeam buildTeam, String name) {
        return buildTeam.getWarpGroups().stream().flatMap(warpGroup -> warpGroup.getWarps().stream())
                .filter(warp1 -> warp1.getName() != null && warp1.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void processCookie(@NotNull Player player, byte[] cookie) {
        ByteArrayDataInput in = ByteStreams.newDataInput(cookie);
        Warp warp = getWarpByKey(in.readUTF());
        ChatHelper.logDebug("Processing cookie for warp %s", warp.getName());
        warpPlayer(player, warp);
    }

    public Warp getWarpByKey(String key) {
        ChatHelper.logDebug("Retrieving warp with key %s", key);
        return NetworkModule.getInstance().getBuildTeam().getWarpGroups().stream().flatMap(warpGroup -> warpGroup.getWarps().stream())
                .filter(warp1 -> warp1.getId().toString().equals(key)).findFirst().orElse(null);
    }

    public static void openWarpMenu(@NotNull Player player) {
        openWarpMenu(player, NetworkModule.getInstance().getBuildTeam(), null);
    }

    public static void openWarpMenu(@NotNull Player player, @NotNull BuildTeam buildTeam, @Nullable Continent continent) {
        int warpGroupCount = buildTeam.getWarpGroups().size();

        switch (warpGroupCount) {
            case 0 -> player.sendMessage(ChatHelper.getErrorString("This server does not have any warps yet!"));
            case 1 -> new WarpMenu(player, buildTeam.getWarpGroups().getFirst(), false, true);
            default -> new WarpGroupMenu(player, buildTeam, continent != null, true, continent);
        }
    }
}

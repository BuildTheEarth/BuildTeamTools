package net.buildtheearth.buildteamtools.modules.navigation.components.bluemap;

import com.flowpowered.math.vector.Vector3d;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.POIMarker;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.ModuleComponent;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.Warp;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.WarpGroup;
import net.buildtheearth.buildteamtools.modules.network.NetworkModule;
import net.buildtheearth.buildteamtools.utils.geo.CoordinateConversion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * BlueMap integration component for visualizing warp groups and warps on the BlueMap.
 * <p>
 * This component registers all warp groups and their warps as POI (Point of Interest) markers
 * on BlueMap, organized by warp group and world. Each warp is converted from geographic
 * coordinates (latitude/longitude) to Minecraft world coordinates for accurate map positioning.
 * </p>
 * <p>
 * Warps are grouped by their world name to ensure proper organization across multiple worlds.
 * If a world doesn't exist or isn't loaded in BlueMap, its warps are simply skipped without
 * causing errors.
 * </p>
 */
public class BluemapComponent extends ModuleComponent {

    /**
     * Initializes the BlueMap component and registers warp markers.
     * <p>
     * Checks if BlueMapAPI is available before proceeding. If not, the component is disabled.
     * Once BlueMap is ready, all warp groups from the current BuildTeam are processed and
     * their warps are registered as markers, organized by world.
     * </p>
     */
    public BluemapComponent() {
        super("BlueMap");

        Optional<BlueMapAPI> optionalApi = BlueMapAPI.getInstance();
        if (optionalApi.isEmpty()) {
            disable();
            return;
        }

        BuildTeamTools.getInstance().getComponentLogger().info(Component.text("Loading BlueMap integration for WarpGroups & Warps...", NamedTextColor.GREEN));

        // Register marker loading when BlueMap is ready
        BlueMapAPI.onEnable(api -> NetworkModule.getInstance().getBuildTeam().getWarpGroups()
                .forEach(warpGroup -> registerWarpGroupMarkers(api, warpGroup)));
    }

    /**
     * Registers all warps from a warp group as markers on BlueMap.
     * <p>
     * This method groups warps by their world name and creates separate marker sets for each world.
     * This ensures that warps are correctly positioned on their respective world maps.
     * </p>
     *
     * @param api       the BlueMapAPI instance
     * @param warpGroup the warp group containing the warps to register
     */
    private void registerWarpGroupMarkers(BlueMapAPI api, @NotNull WarpGroup warpGroup) {
        // Group warps by world name to handle multi-world setups properly
        Map<String, List<Warp>> warpsByWorld = warpGroup.getWarps().stream()
                .collect(Collectors.groupingBy(Warp::getWorldName));

        warpsByWorld.forEach((worldName, warps) -> registerWarpsForWorld(api, warpGroup, worldName, warps));
    }

    /**
     * Registers all warps for a specific world as markers in BlueMap.
     * <p>
     * Creates a marker set for the warp group and adds POI markers for each warp.
     * Each warp's geographic coordinates are converted to Minecraft world coordinates.
     * If the world is not found in BlueMap, this operation gracefully skips it.
     * </p>
     *
     * @param api       the BlueMapAPI instance
     * @param warpGroup the warp group being registered
     * @param worldName the name of the world to register warps for
     * @param warps     the list of warps in this world
     */
    private void registerWarpsForWorld(BlueMapAPI api, @NotNull WarpGroup warpGroup, String worldName, @NotNull List<Warp> warps) {
        // Create a marker set for this warp group
        MarkerSet markerSet = MarkerSet.builder()
                .label(warpGroup.getName())
                .build();

        // Add each warp as a POI marker
        warps.forEach(warp -> addWarpMarker(markerSet, warp));

        // Register the marker set with all maps in the world
        World bukkitWorld = Bukkit.getWorld(worldName);
        if (bukkitWorld != null) {
            api.getWorld(bukkitWorld).ifPresent(world -> {
                for (BlueMapMap map : world.getMaps()) {
                    map.getMarkerSets().put(warpGroup.getId().toString(), markerSet);
                }
            });
        } else {
            BuildTeamTools.getInstance().getComponentLogger().warn(Component.text("World '" + worldName + "' not found for WarpGroup '" + warpGroup.getName() + "'. Skipping BlueMap marker registration for this world.", NamedTextColor.YELLOW));
        }
    }

    /**
     * Adds a single warp as a POI marker to a marker set.
     * <p>
     * Converts the warp's geographic coordinates (latitude/longitude) to Minecraft world
     * coordinates and creates a POI marker with the warp's name.
     * </p>
     *
     * @param markerSet the marker set to add the marker to
     * @param warp      the warp to convert into a marker
     */
    private void addWarpMarker(@NotNull MarkerSet markerSet, @NotNull Warp warp) {
        // Convert geographic coordinates to Minecraft world coordinates
        double[] xz = CoordinateConversion.convertFromGeo(warp.getLat(), warp.getLon());

        // Create a POI marker for the warp
        POIMarker marker = POIMarker.builder()
                .label(warp.getName())
                .position(new Vector3d(xz[0], warp.getY(), xz[1]))
                .build();

        // Add marker to the marker set
        markerSet.getMarkers().put(warp.getId().toString(), marker);
    }
}

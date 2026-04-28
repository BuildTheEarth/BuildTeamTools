package net.buildtheearth.buildteamtools.utils;

import net.buildtheearth.OutOfProjectionBoundsException;
import net.buildtheearth.Projection;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.utils.io.ConfigPaths;
import net.buildtheearth.model.GeographicalCoordinate;
import net.buildtheearth.model.MinecraftCoordinate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class GeometricUtils {
    /**
     * Creates a minecraft location object for the specified coordinates, yaw and pitch from the BTE projection.
     * Height is extracted from the world.
     * <p>
     * Note: Height returned is actually terrain elevation +2. This is because this method internally uses
     * Bukkits @see World::getHighestBlockYAt() already returns elevation+1, and this method deliberately 
     * adds one to the location elevation on top.
     * </p
     * The world is extracted from the server config's "earth world". If no earth world is specified then the height defaults to 64
     * and the world is nullified.
     *
     * @param coordinates Latitude and longitude of the location
     * @param yaw        Player's yaw
     * @param pitch      Player's pitch
     * @return A bukkit location matching the coordinates, yaw and pitch specified. Height is terrain elevation +2.
     */
    public static Location getLocationFromCoordinatesYawPitch(double[] coordinates, float yaw, float pitch) {
        try {
            MinecraftCoordinate coordinate = Projection.toMinecraft(new GeographicalCoordinate(coordinates[0], coordinates[1]));

            //Creates the location
            Location location;
            World tpWorld = Bukkit.getWorld(BuildTeamTools.getInstance().getConfig().getString(ConfigPaths.EARTH_WORLD));
            if (tpWorld == null)
                location = new Location(null, coordinate.x(), 64, coordinate.z(), yaw, pitch);
            else
                location = new Location(tpWorld, coordinate.x(), (tpWorld.getHighestBlockYAt((int) coordinate.x(), (int) coordinate.z()) + 1), coordinate.z(), yaw, pitch);

            return location;
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
     * </p
     * The world is extracted from the server config's "earth world". If no earth world is specified then the height defaults to 64
     * and the world is nullified.
     *
     * @param coordinates Latitude and longitude of the location
     * @return A bukkit location matching the coordinates. Height is terrain elevation +2.
     */
    public static Location getLocationFromCoordinates(double[] coordinates) {
        try {
            MinecraftCoordinate coordinate = Projection.toMinecraft(new GeographicalCoordinate(coordinates[0], coordinates[1]));

            //Creates the location
            Location location;
            World tpWorld = Bukkit.getWorld(BuildTeamTools.getInstance().getConfig().getString(ConfigPaths.EARTH_WORLD));

            if (tpWorld == null)
                location = new Location(null, coordinate.x(), 64, coordinate.z());
            else
                location = new Location(tpWorld, coordinate.x(), (tpWorld.getHighestBlockYAt((int) coordinate.x(), (int) coordinate.z()) + 1), coordinate.z());

            return location;
        } catch (OutOfProjectionBoundsException e) {
            throw new RuntimeException(e);
        }

    }
}

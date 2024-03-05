package net.buildtheearth.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Reflections {

    public static String convertLocationToString(Location loc, boolean XYZinInteger, boolean containsDirection) {
        if (loc == null)
            return null;

        String location = loc.getWorld().getName() + "/";

        if (!XYZinInteger)
            location = location + loc.getX() + "/" + loc.getY() + "/" + loc.getZ();
        else
            location = location + loc.getBlockX() + "/" + loc.getBlockY() + "/" + loc.getBlockZ();

        if (containsDirection)
            location = location + "/" + loc.getYaw() + "/" + loc.getPitch();

        return location;
    }

    public static Location convertStringToLocation(String loc) {
        if (loc == null)
            return null;

        String[] data = loc.split("/");
        Location location = null;

        if (data.length == 4)
            location = new Location(Bukkit.getWorld(data[0]), Double.parseDouble(data[1]), Double.parseDouble(data[2]), Double.parseDouble(data[3]));
        else if (data.length == 6)
            location = new Location(Bukkit.getWorld(data[0]), Double.parseDouble(data[1]), Double.parseDouble(data[2]), Double.parseDouble(data[3]), Float.parseFloat(data[4]), Float.parseFloat(data[5]));
        else
            return null;

        return location;
    }
}

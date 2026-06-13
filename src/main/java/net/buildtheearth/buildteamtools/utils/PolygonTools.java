package net.buildtheearth.buildteamtools.utils;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class PolygonTools {


    public static List<Triangle> triangulatePolygon(List<Location> polygon) {
        return new ArrayList<>();
    }


    public static List<BlockLocation> rasterizeTriangle(Triangle triangle) {
        //TODO implement
        return new ArrayList<>();
    }


    public record Triangle(Location vertex1, Location vertex2, Location vertex3) {
    }
}

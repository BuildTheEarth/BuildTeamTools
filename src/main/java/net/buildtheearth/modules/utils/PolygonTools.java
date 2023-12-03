package net.buildtheearth.modules.utils;

import org.bukkit.Location;


import java.util.ArrayList;
import java.util.List;

public class PolygonTools {



    public static List<Triangle> triangulatePolygon(List<Location> polygon) {
        List<Triangle> result = new ArrayList<>();

        return result;
    }




    public static List<BlockLocation> rasterizeTriangle(Triangle triangle) {
        List<BlockLocation> rasterizedPoints = new ArrayList<>();
        //TODO implement

        return rasterizedPoints;
    }


    


    public static class Triangle {
        private final Location vertex1;
        private final Location vertex2;
        private final Location vertex3;

        public Triangle(Location vertex1, Location vertex2, Location vertex3) {
            this.vertex1 = vertex1;
            this.vertex2 = vertex2;
            this.vertex3 = vertex3;
        }

        public Location getVertex1() {
            return vertex1;
        }

        public Location getVertex2() {
            return vertex2;
        }

        public Location getVertex3() {
            return vertex3;
        }
    }
}

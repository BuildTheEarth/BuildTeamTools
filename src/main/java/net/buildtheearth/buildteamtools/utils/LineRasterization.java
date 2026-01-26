package net.buildtheearth.buildteamtools.utils;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class LineRasterization {
    
    public static List<BlockLocation> rasterizeLine(Location start, Location end){
        return rasterizeLine(
                    start.getBlockX(),start.getBlockY(),start.getBlockZ(),
                    end.getBlockX(),end.getBlockY(),end.getBlockZ());
    }

    /** 
     * Interpolates block positions between to given locations, similarly to what 
     * the WorldEdit command "//line" does. Internally uses Bresenhams algorithm to
     * calculate the intermediate positions
     * 
     * @param start Start position
     * @param end End position
     * @return List<BlockLocation> all locations forming the line, including start and endpoint.
     */
    public static List<BlockLocation> rasterizeLine(int x0, int y0, int z0, int x1, int y1, int z1){
        
        List<BlockLocation> rasterLine = new ArrayList<>();

        //Bresenhams algorithm for 3D. Works on integer coordinates
        //void bresenham3D (int x0, int y0, int z0, int x1, int y1, int z1)


        int dx = Math.abs(x1-x0);
        int sx = x0<x1 ? 1:-1;
        int dy = Math.abs(y1-y0);
        int sy = y0<y1 ? 1:-1;
        int dz = Math.abs(z1-z0);
        int sz = z0<z1 ? 1:-1;
        int dm = Math.max(dx,Math.max(dy,dz));
        int i = dm; /* maximum difference */

        for (x1 = y1 = z1 = i/2; i-- >= 0; ) { /* loop */
            //setPixel(x0,y0,z0);
            rasterLine.add(new BlockLocation(x0,y0,z0));
            x1 -= dx; 
            if (x1 < 0) {
                 x1 += dm; x0 += sx;
            }
            y1 -= dy; 
            if (y1 < 0) { 
                y1 += dm; y0 += sy; 
            }
            z1 -= dz; 
            if (z1 < 0) {
                z1 += dm; z0 += sz;
            }
        }
        
        return rasterLine;
    }

}

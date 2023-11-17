package net.buildtheearth.modules.utils;

import org.bukkit.block.Block;

import javafx.geometry.Point3D;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.List;
import java.util.ArrayList;

public class LineRasterization {
    public static List<BlockLocation> rasterizeLine(Location start, Location end){
        
        List<BlockLocation> rasterLine = new ArrayList<>();

        //Bresenhams algorithm for 3D. Works on integer coordinates
        //void bresenham3D (int x0, int y0, int z0, int x1, int y1, int z1)
        int x0 = start.getBlockX();
        int y0 = start.getBlockY();
        int z0 = start.getBlockZ();

        int x1 = end.getBlockX();
        int y1 = end.getBlockY();
        
        int z1 = end.getBlockZ();
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

    public static void fillLineWithBlocks(Location start, Location end, World world, Material blockType){
        //TODO World from server-config as in GeometricUtils
        ////World tpWorld = Bukkit.getWorld(Main.instance.getConfig().getString("universal_tpll.earth_world"));
        
        List<BlockLocation> rasterLine = rasterizeLine(start, end);
        for (BlockLocation loc : rasterLine)
        {
            world.getBlockAt(loc.x, loc.y, loc.z).setType(blockType);
        }
    }

}

package net.buildtheearth.utils;

import org.bukkit.Location;
import org.bukkit.World;

/** 
 * Integer-based triplet for Block positions 
 * */
public class BlockLocation {
    public int x,y,z;

    public BlockLocation (int x, int y, int z){
        this.x=x;
        this.y=y;
        this.z=z;
    }

    public BlockLocation (Location loc){
        this.x=loc.getBlockX();
        this.y=loc.getBlockY();
        this.z=loc.getBlockZ();
    }
    
    /** 
     * 
     * @param world the world to associate with this position
     * @return Bukkit Location object for this position
     */
    public Location getLocation(World world){
        
        return new Location(world, (double)x, (double)y, (double)z);
    }
}

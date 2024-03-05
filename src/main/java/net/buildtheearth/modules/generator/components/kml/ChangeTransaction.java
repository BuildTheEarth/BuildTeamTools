package net.buildtheearth.modules.generator.components.kml;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;


import net.buildtheearth.utils.BlockLocation;

/**
 * A set of block changes that should be bundled together. 
 * Can be used to keep track of changes stemming from a single command, and for undo.
 * 
 * Use @see addBlockChange() to add to the set of changes
 * 
 * Use @see commit() to execute the changes, and @see undo() to revert the changes. 
 */
public class ChangeTransaction {

    public class BlockModification{
        public BlockModification(Block block, Material newType)
        {
            this.block = block;
            this.previousType = block.getType();
            this.newType = newType; 
        }
        
        public Block block;
        public Material previousType;
        public Material newType;
    };

    //------------------------------------

    public ChangeTransaction(Player p){
        this.player = p;
        changes = new ArrayList<>();
        blockPositions = new HashSet<BlockLocation>();
    }

    
    /** 
     * adds a block change to this change-set. If this already has a change for this location, the change is ignored.
     * use @see commit() to execute the changes.
     * 
     * @param location the location to change
     * @param newType the new material / block type for the location
     * @return boolean: indicate if the change was accepted or not (i.e. wether this already had a change for the location or not)
     */
    public boolean addBlockChange(Location location, Material newType){
        return addBlockChange(new BlockLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ()), location.getWorld(), newType);
    }

    
    /** 
     * adds a block change to this change-set. If this already has a change for this location, the change is ignored.
     * use @see commit() to execute the changes.
     * 
     * @param world the world for the change
     * @param location the location to change
     * @param newType the new material / block type for the location
     * @return boolean: indicate if the change was accepted or not (i.e. wether this already had a change for the location or not)
     */
    public boolean addBlockChange(BlockLocation location, World world, Material newType){
        //check if we already have a change for this location.
        
        if (blockPositions.contains(location)){
            return false; //ignore change
            //TODO maybe instead update type?
        }
        blockPositions.add(location);

        BlockModification mod = new BlockModification(
            world.getBlockAt(location.getLocation(world)), newType);
        changes.add(mod);
        return true;
    }

    public int commit(){
        for (BlockModification change : changes){
            change.block.setType(change.newType);
        }
        return changes.size();
    }

    public void undo(){
        for (BlockModification change : changes){
            change.block.setType(change.previousType);
        }
    }

    public int size() {
        return changes.size();
    }

    public Player player;
    List<BlockModification> changes;
    Set<BlockLocation> blockPositions; //check to prevent duplicates


}

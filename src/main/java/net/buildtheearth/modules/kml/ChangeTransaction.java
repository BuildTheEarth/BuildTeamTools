package net.buildtheearth.modules.kml;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;


import net.buildtheearth.modules.utils.BlockLocation;

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

    public void addBlockChange(Location location, Material newType){
        addBlockChange(new BlockLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ()), location.getWorld(), newType);
    }

    public void addBlockChange(BlockLocation location, World world, Material newType){
        //check if we already have a change for this location.
        
        if (blockPositions.contains(location)){
            return; //ignore change
            //TODO maybe instead update type?
        }
        blockPositions.add(location);

        BlockModification mod = new BlockModification(
            world.getBlockAt(location.getLocation(world)), newType);
        changes.add(mod);
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

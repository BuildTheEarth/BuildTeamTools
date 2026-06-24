package net.buildtheearth.buildteamtools.modules.generator.model;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class BlockChange {

    @Getter
    private final String worldName;

    @Getter
    private final int x;

    @Getter
    private final int y;

    @Getter
    private final int z;

    @Getter
    private final String oldBlockData;

    @Getter
    @Setter
    private String newBlockData;

    public BlockChange(String worldName, int x, int y, int z, String oldBlockData, String newBlockData) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.oldBlockData = oldBlockData;
        this.newBlockData = newBlockData;
    }

    public void applyOld() {
        apply(oldBlockData);
    }

    public void applyNew() {
        apply(newBlockData);
    }

    private void apply(String blockDataString) {
        World world = Bukkit.getWorld(worldName);

        if (world == null)
            return;

        Block block = world.getBlockAt(x, y, z);
        BlockData blockData = Bukkit.createBlockData(blockDataString);
        block.setBlockData(blockData, false);
    }
}

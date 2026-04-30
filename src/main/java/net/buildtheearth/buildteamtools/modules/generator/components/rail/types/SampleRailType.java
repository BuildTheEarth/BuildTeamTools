package net.buildtheearth.buildteamtools.modules.generator.components.rail.types;

import com.sk89q.worldedit.math.BlockVector3;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.placement.RailBlockPlacement;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.placement.RailBlockRole;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.util.Vector;

public class SampleRailType implements RailType {

    private static final Material[] CENTER_MATERIALS = new Material[]{
            Material.DEAD_FIRE_CORAL_BLOCK,
            Material.STONE,
            Material.COBBLESTONE
    };

    private static final Material SIDE_MATERIAL = Material.ANVIL;

    @Override
    public String getName() {
        return "Sample Railway";
    }

    @Override
    public BlockData createBlockData(RailBlockPlacement placement) {
        if (placement.getRole() == RailBlockRole.CENTER)
            return createCenterBlockData(placement.getPosition());

        return createSideBlockData(placement.getDirection());
    }

    private BlockData createCenterBlockData(BlockVector3 position) {
        int index = Math.floorMod(position.x() * 31 + position.z() * 17, CENTER_MATERIALS.length);
        return CENTER_MATERIALS[index].createBlockData();
    }

    private BlockData createSideBlockData(Vector direction) {
        BlockData data = SIDE_MATERIAL.createBlockData();

        if (data instanceof Directional directional)
            directional.setFacing(toBlockFace(direction));

        return data;
    }

    private BlockFace toBlockFace(Vector direction) {
        int dx = direction.getBlockX();
        int dz = direction.getBlockZ();

        if (Math.abs(dx) >= Math.abs(dz)) {
            if (dx > 0)
                return BlockFace.EAST;

            if (dx < 0)
                return BlockFace.WEST;
        }

        if (dz > 0)
            return BlockFace.SOUTH;

        if (dz < 0)
            return BlockFace.NORTH;

        return BlockFace.EAST;
    }
}
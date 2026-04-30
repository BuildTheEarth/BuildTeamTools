package net.buildtheearth.buildteamtools.modules.generator.components.rail.side;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.util.Vector;

public class RailSideBlock {

    private final BlockVector3 position;
    private int directionX;
    private int directionZ;

    public RailSideBlock(BlockVector3 position) {
        this.position = position;
    }

    public BlockVector3 getPosition() {
        return position;
    }

    public void addDirection(Vector direction) {
        directionX += direction.getBlockX();
        directionZ += direction.getBlockZ();
    }

    public Vector getAverageDirection() {
        int dx = Integer.compare(directionX, 0);
        int dz = Integer.compare(directionZ, 0);

        if (dx == 0 && dz == 0)
            return new Vector(1, 0, 0);

        return new Vector(dx, 0, dz);
    }
}
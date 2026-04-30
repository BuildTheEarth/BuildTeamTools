package net.buildtheearth.buildteamtools.modules.generator.components.rail.placement;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.util.Vector;

public class RailBlockPlacement {

    private final BlockVector3 position;
    private final RailBlockRole role;
    private final Vector direction;

    public RailBlockPlacement(BlockVector3 position, RailBlockRole role, Vector direction) {
        this.position = position;
        this.role = role;
        this.direction = direction == null ? new Vector(1, 0, 0) : direction;
    }

    public BlockVector3 getPosition() {
        return position;
    }

    public RailBlockRole getRole() {
        return role;
    }

    public Vector getDirection() {
        return direction;
    }
}
package net.buildtheearth.buildteamtools.modules.generator.components.rail.placement;

import com.sk89q.worldedit.math.BlockVector3;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.path.RailPath;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.side.RailOrientationResolver;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.side.RailSideBlock;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.side.RailSideBuilder;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class RailPlacementBuilder {

    private final RailSideBuilder sideBuilder;
    private final RailOrientationResolver orientationResolver;

    public RailPlacementBuilder() {
        this.sideBuilder = new RailSideBuilder();
        this.orientationResolver = new RailOrientationResolver();
    }

    public List<RailBlockPlacement> buildPlacements(RailPath railPath) {
        List<RailBlockPlacement> placements = new ArrayList<>();

        LinkedHashSet<BlockVector3> centerPositions = sideBuilder.getCenterPositions(railPath);
        Map<BlockVector3, RailSideBlock> sideBlocks = sideBuilder.buildSideBlocks(railPath);

        addSidePlacements(placements, sideBlocks);
        addCenterPlacements(placements, centerPositions);

        return placements;
    }

    private void addSidePlacements(List<RailBlockPlacement> placements, Map<BlockVector3, RailSideBlock> sideBlocks) {
        for (RailSideBlock sideBlock : sideBlocks.values()) {
            Vector direction = orientationResolver.resolveDirection(sideBlock, sideBlocks);

            placements.add(new RailBlockPlacement(
                    sideBlock.getPosition(),
                    RailBlockRole.SIDE,
                    direction
            ));
        }
    }

    private void addCenterPlacements(List<RailBlockPlacement> placements, LinkedHashSet<BlockVector3> centerPositions) {
        for (BlockVector3 centerPosition : centerPositions) {
            placements.add(new RailBlockPlacement(
                    centerPosition,
                    RailBlockRole.CENTER,
                    new Vector(1, 0, 0)
            ));
        }
    }
}
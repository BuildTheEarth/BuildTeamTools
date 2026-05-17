package net.buildtheearth.buildteamtools.modules.generator.components.rail.placement;

import com.sk89q.worldedit.math.BlockVector3;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.types.RailType;
import net.buildtheearth.buildteamtools.modules.generator.model.History;
import net.buildtheearth.buildteamtools.modules.generator.model.Script;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RailWorldEditPlacer {

    public List<History.BlockChange> placeWithBukkitHistory(
            Script script,
            List<RailBlockPlacement> placements,
            RailType railType
    ) {
        Map<BlockVector3, BlockData> blockMap = createBlockMap(placements, railType);
        List<History.BlockChange> changes = new ArrayList<>();

        for (Map.Entry<BlockVector3, BlockData> entry : blockMap.entrySet()) {
            BlockVector3 position = entry.getKey();
            BlockData newData = entry.getValue();

            Block block = script.getPlayer().getWorld().getBlockAt(position.x(), position.y(), position.z());
            BlockData oldData = block.getBlockData();

            if (oldData.matches(newData))
                continue;

            changes.add(new History.BlockChange(
                    script.getPlayer().getWorld().getName(),
                    position.x(),
                    position.y(),
                    position.z(),
                    oldData.getAsString(),
                    newData.getAsString()
            ));

            block.setBlockData(newData, false);
        }

        return changes;
    }

    private Map<BlockVector3, BlockData> createBlockMap(List<RailBlockPlacement> placements, RailType railType) {
        Map<BlockVector3, BlockData> blockMap = new LinkedHashMap<>();

        for (RailBlockPlacement placement : placements)
            blockMap.put(placement.getPosition(), railType.createBlockData(placement));

        return blockMap;
    }
}
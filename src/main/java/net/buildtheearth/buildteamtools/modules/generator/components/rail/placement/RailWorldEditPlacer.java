package net.buildtheearth.buildteamtools.modules.generator.components.rail.placement;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.types.RailType;
import net.buildtheearth.buildteamtools.modules.generator.model.History;
import net.buildtheearth.buildteamtools.modules.generator.model.Script;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class RailWorldEditPlacer {

    public boolean placeWithWorldEdit(Script script, List<RailBlockPlacement> placements, RailType railType) {
        Map<BlockVector3, BlockData> blockMap = createBlockMap(placements, railType);

        try (EditSession editSession = WorldEdit.getInstance()
                .newEditSessionBuilder()
                .world(script.getWeWorld())
                .actor(script.getActor())
                .build()) {

            for (Map.Entry<BlockVector3, BlockData> entry : blockMap.entrySet()) {
                BlockVector3 position = entry.getKey();
                BlockState blockState = BukkitAdapter.adapt(entry.getValue());

                editSession.setBlock(position.x(), position.y(), position.z(), blockState);
            }

            editSession.flushQueue();
            script.getLocalSession().remember(editSession);

            return true;
        } catch (Throwable throwable) {
            BuildTeamTools.getInstance().getLogger().log(
                    Level.SEVERE,
                    "Failed to place railway with WorldEdit.",
                    throwable
            );
            return false;
        }
    }

    public List<History.BlockChange> placeWithBukkitFallback(
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

            changes.add(new History.BlockChange(
                    script.getPlayer().getWorld().getName(),
                    position.x(),
                    position.y(),
                    position.z(),
                    block.getBlockData().getAsString(),
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
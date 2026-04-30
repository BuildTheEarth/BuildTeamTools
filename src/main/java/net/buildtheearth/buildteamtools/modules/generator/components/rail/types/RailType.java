package net.buildtheearth.buildteamtools.modules.generator.components.rail.types;

import net.buildtheearth.buildteamtools.modules.generator.components.rail.placement.RailBlockPlacement;
import org.bukkit.block.data.BlockData;

public interface RailType {

    String getName();

    BlockData createBlockData(RailBlockPlacement placement);
}
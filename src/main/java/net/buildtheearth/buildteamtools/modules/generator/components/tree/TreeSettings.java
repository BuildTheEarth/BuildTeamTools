package net.buildtheearth.buildteamtools.modules.generator.components.tree;

import net.buildtheearth.buildteamtools.modules.generator.components.road.RoadFlag;
import net.buildtheearth.buildteamtools.modules.generator.model.Settings;
import org.bukkit.entity.Player;

public class TreeSettings extends Settings {

    public TreeSettings(Player player) {
        super(player);
    }

    public void setDefaultValues() {

        // Lane Count (Default: Fixed Value)
        setValue(TreeFlag.TYPE, TreeType.ANY);

        // Lane Count (Default: Fixed Value)
        setValue(TreeFlag.WIDTH, TreeWidth.ANY);

        // Lane Count (Default: Fixed Value)
        setValue(RoadFlag.LANE_COUNT, "2");
    }
}

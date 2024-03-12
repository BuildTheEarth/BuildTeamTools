package net.buildtheearth.modules.generator.components.tree;

import net.buildtheearth.modules.generator.model.Settings;
import net.buildtheearth.modules.generator.components.road.RoadFlag;
import org.bukkit.entity.Player;

public class TreeSettings extends Settings {

    public TreeSettings(Player player) {
        super(player);
    }

    public void setDefaultValues() {

        // Lane Count (Default: Fixed Value)
        setValue(TreeFlag.TYPE, "any");

        // Lane Count (Default: Fixed Value)
        setValue(TreeFlag.WIDTH, "any");

        // Lane Count (Default: Fixed Value)
        setValue(RoadFlag.LANE_COUNT, "2");
    }
}

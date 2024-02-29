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
        getValues().put(TreeFlag.TYPE, "any");

        // Lane Count (Default: Fixed Value)
        getValues().put(TreeFlag.WIDTH, "any");

        // Lane Count (Default: Fixed Value)
        getValues().put(RoadFlag.LANE_COUNT, "2");
    }
}

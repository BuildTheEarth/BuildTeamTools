package net.buildtheearth.buildteamtools.modules.generator.components.rail;

import net.buildtheearth.buildteamtools.modules.generator.model.Settings;
import org.bukkit.entity.Player;

public class RailSettings extends Settings {

    public RailSettings(Player player) {
        super(player);
    }

    public void setDefaultValues() {

        // Lane Count (Default: Fixed Value)
        setValue(RailFlag.LANE_COUNT, 1);
    }
}

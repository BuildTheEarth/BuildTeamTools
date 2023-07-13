package net.buildtheearth.buildteam.components.generator.rail;

import net.buildtheearth.buildteam.components.generator.Settings;
import net.buildtheearth.buildteam.components.generator.road.RoadFlag;
import org.bukkit.entity.Player;

public class RailSettings extends Settings {

    public RailSettings(Player player){
        super(player);
    }

    public void setDefaultValues(){

        // Lane Count (Default: Fixed Value)
        getValues().put(RailFlag.LANE_COUNT, "1");
    }
}

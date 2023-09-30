package net.buildtheearth.modules.generator.modules.rail;

import net.buildtheearth.modules.generator.model.Settings;
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

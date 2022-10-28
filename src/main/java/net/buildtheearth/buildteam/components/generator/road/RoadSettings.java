package net.buildtheearth.buildteam.components.generator.road;

import lombok.Getter;
import net.buildtheearth.buildteam.components.generator.house.HouseFlag;
import net.buildtheearth.buildteam.components.generator.house.RoofType;
import net.buildtheearth.utils.MenuItems;
import net.buildtheearth.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class RoadSettings {

    @Getter
    private Player player;

    @Getter
    private HashMap<RoadFlag, String> values;


    public RoadSettings(Player player){
        this.player = player;
        this.values = new HashMap<>();

        setDefaultValues();
    }

    public void setValue(RoadFlag roadFlag, String value){
        values.put(roadFlag, value);
    }

    public void setDefaultValues(){

        // Lane Count (Default: Fixed Value)
        values.put(RoadFlag.LANE_COUNT, "1");

        // Lane Width (Default: Fixed Value)
        values.put(RoadFlag.LANE_WIDTH, "4");

        // Road Material (Default: Fixed Value)
        values.put(RoadFlag.ROAD_MATERIAL, "252:7");

        // Lane Gap (Default: Fixed Value)
        values.put(RoadFlag.LANE_GAP, "0");

        // Marking Length (Default: Fixed Value)
        values.put(RoadFlag.MARKING_LENGTH, "3");

        // Marking Gap (Default: Fixed Value)
        values.put(RoadFlag.MARKING_GAP, "5");

        // Marking Material (Default: Fixed Value)
        values.put(RoadFlag.MARKING_MATERIAL, "251");

        // Sidewalk Width (Default: Fixed Value)
        values.put(RoadFlag.SIDEWALK_WIDTH, "1");

        // Sidewalk Material (Default: Fixed Value)
        values.put(RoadFlag.SIDEWALK_MATERIAL, "43");
    }
}

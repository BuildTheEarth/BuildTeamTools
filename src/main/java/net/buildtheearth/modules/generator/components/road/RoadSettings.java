package net.buildtheearth.modules.generator.components.road;

import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.generator.model.Flag;
import net.buildtheearth.modules.generator.model.Settings;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RoadSettings extends Settings {

    public static List<String> streetLampTypes = new ArrayList<>();


    public RoadSettings(Player player){
        super(player);

        File directory = new File(BuildTeamTools.getInstance().getDataFolder().getAbsolutePath() + "/../WorldEdit/schematics/GeneratorCollections/roadpack/");
        File[] files = directory.listFiles();

        for(File file : files)
            if(file.getName().contains("streetlamp"))
                streetLampTypes.add(file.getName().replace(".schematic", "").replace("streetlamp", ""));
    }

    public void setDefaultValues(){

        // Lane Count (Default: Fixed Value)
        getValues().put(RoadFlag.LANE_COUNT, "2");

        // Lane Width (Default: Fixed Value)
        getValues().put(RoadFlag.LANE_WIDTH, "4");

        // Road Material (Default: Fixed Value)
        getValues().put(RoadFlag.ROAD_MATERIAL, "252:7");

        // Lane Gap (Default: Fixed Value)
        getValues().put(RoadFlag.LANE_GAP, "0");

        // Marking Length (Default: Fixed Value)
        getValues().put(RoadFlag.MARKING_LENGTH, "3");

        // Marking Gap (Default: Fixed Value)
        getValues().put(RoadFlag.MARKING_GAP, "5");

        // Marking Material (Default: Fixed Value)
        getValues().put(RoadFlag.MARKING_MATERIAL, "251:0");

        // Sidewalk Width (Default: Fixed Value)
        getValues().put(RoadFlag.SIDEWALK_WIDTH, "5");

        // Sidewalk Material (Default: Fixed Value)
        getValues().put(RoadFlag.SIDEWALK_MATERIAL, "43");

        // Sidewalk Slab Material (Default: Fixed Value)
        getValues().put(RoadFlag.SIDEWALK_SLAB_COLOR, "44:0");

        // Road Slab Material (Default: Fixed Value)
        getValues().put(RoadFlag.ROAD_SLAB_COLOR, Flag.DISABLED);

        // Crosswalk (Default: Fixed Value)
        getValues().put(RoadFlag.CROSSWALK, Flag.ENABLED);

        // Street Lamp Type (Default: Fixed Value)
        getValues().put(RoadFlag.STREET_LAMP_TYPE, "001");

        // Street Lamp Distance (Default: Fixed Value)
        getValues().put(RoadFlag.STREET_LAMP_DISTANCE, "40");

        // Road Side (Default: Fixed Value)
        getValues().put(RoadFlag.ROAD_SIDE, "10");
    }
}

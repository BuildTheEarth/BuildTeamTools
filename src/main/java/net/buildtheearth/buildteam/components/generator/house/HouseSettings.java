package net.buildtheearth.buildteam.components.generator.house;

import lombok.Getter;
import lombok.Setter;
import net.buildtheearth.utils.MenuItems;
import net.buildtheearth.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class HouseSettings {

    @Getter
    private Player player;

    @Getter
    private HashMap<HouseFlag, String> values;


    public HouseSettings(Player player){
        this.player = player;
        this.values = new HashMap<>();

        setDefaultValues();
    }

    public void setValue(HouseFlag houseFlag, String value){
        values.put(houseFlag, value);
    }

    public void setDefaultValues(){
        // Roof Type (Default: Random)
        RoofType roofType = (RoofType) Utils.pickRandom(RoofType.values());
        values.put(HouseFlag.ROOF_TYPE, roofType.getType());

        // Wall Color (Default: Random)
        ItemStack block = (ItemStack) Utils.pickRandom(MenuItems.WALL_BLOCKS);
        values.put(HouseFlag.WALL_COLOR, Utils.getBlockID(block));

        // Wall Color (Default: Random)
        block = (ItemStack) Utils.pickRandom(MenuItems.WALL_BLOCKS);
        values.put(HouseFlag.BASE_COLOR, Utils.getBlockID(block));

       // Roof Color (Default: Random)
        block = (ItemStack) Utils.pickRandom(MenuItems.STAIRS);

        if(values.get(HouseFlag.ROOF_TYPE).equalsIgnoreCase(RoofType.SLABS.getType()))
            block = (ItemStack) Utils.pickRandom(MenuItems.SLABS);
        else if(values.get(HouseFlag.ROOF_TYPE).equalsIgnoreCase(RoofType.FLAT.getType()))
            block = (ItemStack) Utils.pickRandom(MenuItems.SLABS);

        values.put(HouseFlag.ROOF_COLOR, Utils.getBlockID(block));

        // Floor Count (Default: Random)
        values.put(HouseFlag.FLOOR_COUNT, "" + ((int)(Math.random()*3.0) + 1));

        // Window Color (Default: Fixed Value)
        values.put(HouseFlag.WINDOW_COLOR, "95:15");

        // Floor Height (Default: Fixed Value)
        values.put(HouseFlag.FLOOR_HEIGHT, "3");

        // Base Height (Default: Fixed Value)
        values.put(HouseFlag.BASE_HEIGHT, "1");

        // Window Height (Default: Fixed Value)
        values.put(HouseFlag.WINDOW_HEIGHT, "2");

        // Window Width (Default: Fixed Value)
        values.put(HouseFlag.WINDOW_WIDTH, "2");

        // Window Distance (Default: Fixed Value)
        values.put(HouseFlag.WINDOW_DISTANCE, "2");

        // Max Roof Height (Default: Fixed Value)
        values.put(HouseFlag.MAX_ROOF_HEIGHT, "10");
    }
}

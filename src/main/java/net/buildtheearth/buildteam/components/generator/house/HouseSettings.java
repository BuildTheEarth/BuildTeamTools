package net.buildtheearth.buildteam.components.generator.house;

import lombok.Getter;
import lombok.Setter;
import net.buildtheearth.buildteam.components.generator.Settings;
import net.buildtheearth.utils.MenuItems;
import net.buildtheearth.utils.Utils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class HouseSettings extends Settings {

    
    public HouseSettings(Player player){
        super(player);
    }

    public void setDefaultValues(){
        // Roof Type (Default: Random)
        RoofType roofType = (RoofType) Utils.pickRandom(RoofType.values());
        getValues().put(HouseFlag.ROOF_TYPE, roofType.getType());

        // Wall Color (Default: Random)
        ItemStack block = (ItemStack) Utils.pickRandom(MenuItems.WALL_BLOCKS);
        getValues().put(HouseFlag.WALL_COLOR, Utils.getBlockID(block));

        // Wall Color (Default: Random)
        block = (ItemStack) Utils.pickRandom(MenuItems.WALL_BLOCKS);
        getValues().put(HouseFlag.BASE_COLOR, Utils.getBlockID(block));

       // Roof Color (Default: Random)
        block = (ItemStack) Utils.pickRandom(MenuItems.STAIRS);

        if( getValues().get(HouseFlag.ROOF_TYPE).equalsIgnoreCase(RoofType.FLATTER_SLABS.getType()) ||
            getValues().get(HouseFlag.ROOF_TYPE).equalsIgnoreCase(RoofType.MEDIUM_SLABS.getType()) ||
            getValues().get(HouseFlag.ROOF_TYPE).equalsIgnoreCase(RoofType.STEEP_SLABS.getType()))
            block = (ItemStack) Utils.pickRandom(MenuItems.SLABS);
        else if(getValues().get(HouseFlag.ROOF_TYPE).equalsIgnoreCase(RoofType.FLAT.getType()))
            block = (ItemStack) Utils.pickRandom(MenuItems.SLABS);

        getValues().put(HouseFlag.ROOF_COLOR, Utils.getBlockID(block));

        // Floor Count (Default: Random)
        getValues().put(HouseFlag.FLOOR_COUNT, "" + ((int)(Math.random()*3.0) + 1));

        // Window Color (Default: Fixed Value)
        getValues().put(HouseFlag.WINDOW_COLOR, "95:15");

        // Floor Height (Default: Fixed Value)
        getValues().put(HouseFlag.FLOOR_HEIGHT, "3");

        // Base Height (Default: Fixed Value)
        getValues().put(HouseFlag.BASE_HEIGHT, "1");

        // Window Height (Default: Fixed Value)
        getValues().put(HouseFlag.WINDOW_HEIGHT, "2");

        // Window Width (Default: Fixed Value)
        getValues().put(HouseFlag.WINDOW_WIDTH, "2");

        // Window Distance (Default: Fixed Value)
        getValues().put(HouseFlag.WINDOW_DISTANCE, "2");

        // Max Roof Height (Default: Fixed Value)
        getValues().put(HouseFlag.MAX_ROOF_HEIGHT, "10");
    }
}

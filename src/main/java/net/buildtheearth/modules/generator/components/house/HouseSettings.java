package net.buildtheearth.modules.generator.components.house;

import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.modules.generator.model.Settings;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.MenuItems;
import net.buildtheearth.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HouseSettings extends Settings {


    public HouseSettings(Player player) {
        super(player);
    }

    public void setDefaultValues() {
        // Roof Type (Default: Random)
        RoofType roofType = (RoofType) Utils.pickRandom(RoofType.values());
        if(roofType != null)
            setValue(HouseFlag.ROOF_TYPE, roofType.getType());

        // Wall Color (Default: Random)
        ItemStack block = (ItemStack) Utils.pickRandom(MenuItems.getSolidBlocks().toArray());
        if(block != null)
            setValue(HouseFlag.WALL_COLOR, Item.getUniqueMaterialString(block));

        // Wall Color (Default: Random)
        block = (ItemStack) Utils.pickRandom(MenuItems.getSolidBlocks().toArray());
        if(block != null)
            setValue(HouseFlag.BASE_COLOR, XMaterial.matchXMaterial(block).getId() + "");

        // Roof Color (Default: Random)
        if(roofType == RoofType.STAIRS)
            block = (ItemStack) Utils.pickRandom(MenuItems.getStairs().toArray());
        else if(roofType == RoofType.FLATTER_SLABS || roofType == RoofType.MEDIUM_SLABS || roofType == RoofType.STEEP_SLABS)
            block = (ItemStack) Utils.pickRandom(MenuItems.getSlabs().toArray());
        else if(roofType == RoofType.FLAT)
            block = (ItemStack) Utils.pickRandom(MenuItems.getSlabs().toArray());

        if(block != null)
            setValue(HouseFlag.ROOF_COLOR, Item.getUniqueMaterialString(block));

        // Balcony Color (Default: Random)
        setValue(HouseFlag.BALCONY_COLOR, Item.getUniqueMaterialString(XMaterial.BLACK_CONCRETE.parseItem()));

        // Balcony Fence Color (Default: Random)
        setValue(HouseFlag.BALCONY_FENCE_COLOR, Item.getUniqueMaterialString(XMaterial.IRON_TRAPDOOR.parseItem()));


        // Floor Count (Default: Random)
        setValue(HouseFlag.FLOOR_COUNT, "" + ((int)(Math.random()*3.0) + 1));

        // Window Color (Default: Fixed Value)
        setValue(HouseFlag.WINDOW_COLOR, Item.getUniqueMaterialString(XMaterial.BLACK_STAINED_GLASS.parseItem()));

        // Floor Height (Default: Fixed Value)
        setValue(HouseFlag.FLOOR_HEIGHT, "3");

        // Base Height (Default: Fixed Value)
        setValue(HouseFlag.BASE_HEIGHT, "1");

        // Window Height (Default: Fixed Value)
        setValue(HouseFlag.WINDOW_HEIGHT, "2");

        // Window Width (Default: Fixed Value)
        setValue(HouseFlag.WINDOW_WIDTH, "2");

        // Window Distance (Default: Fixed Value)
        setValue(HouseFlag.WINDOW_DISTANCE, "2");

        // Max Roof Height (Default: Fixed Value)
        setValue(HouseFlag.MAX_ROOF_HEIGHT, "10");
    }
}

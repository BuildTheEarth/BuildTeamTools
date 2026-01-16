package net.buildtheearth.buildteamtools.modules.generator.components.house;

import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.buildteamtools.modules.generator.model.Settings;
import net.buildtheearth.buildteamtools.utils.MenuItems;
import net.buildtheearth.buildteamtools.utils.Utils;
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
            setValue(HouseFlag.ROOF_TYPE, roofType);

        // Wall Color (Default: Random)
        ItemStack block = (ItemStack) Utils.pickRandom(MenuItems.getSolidBlocks().toArray());
        if(block != null)
            setValue(HouseFlag.WALL_COLOR, new XMaterial[]{XMaterial.matchXMaterial(block)});

        // Wall Color (Default: Random)
        block = (ItemStack) Utils.pickRandom(MenuItems.getSolidBlocks().toArray());
        if(block != null)
            setValue(HouseFlag.BASE_COLOR, new XMaterial[]{XMaterial.matchXMaterial(block)});

        // Roof Color (Default: Random)
        if(roofType == RoofType.STAIRS)
            block = (ItemStack) Utils.pickRandom(MenuItems.getStairs().toArray());
        else if(roofType == RoofType.FLATTER_SLABS || roofType == RoofType.MEDIUM_SLABS || roofType == RoofType.STEEP_SLABS)
            block = (ItemStack) Utils.pickRandom(MenuItems.getSlabs().toArray());
        else if(roofType == RoofType.FLAT)
            block = (ItemStack) Utils.pickRandom(MenuItems.getSlabs().toArray());

        if(block != null)
            setValue(HouseFlag.ROOF_COLOR, new XMaterial[]{XMaterial.matchXMaterial(block)});

        // Balcony Color (Default: Random)
        setValue(HouseFlag.BALCONY_COLOR, new XMaterial[]{XMaterial.WHITE_CONCRETE});

        // Balcony Fence Color (Default: Random)
        setValue(HouseFlag.BALCONY_FENCE_COLOR, new XMaterial[]{XMaterial.IRON_BARS});


        // Floor Count (Default: Random)
        setValue(HouseFlag.FLOOR_COUNT, (int)(Math.random()*3.0) + 1);

        // Window Color (Default: Fixed Value)
        setValue(HouseFlag.WINDOW_COLOR, new XMaterial[]{XMaterial.BLACK_STAINED_GLASS});

        // Floor Height (Default: Fixed Value)
        setValue(HouseFlag.FLOOR_HEIGHT, 3);

        // Base Height (Default: Fixed Value)
        setValue(HouseFlag.BASE_HEIGHT, 1);

        // Window Height (Default: Fixed Value)
        setValue(HouseFlag.WINDOW_HEIGHT, 2);

        // Window Width (Default: Fixed Value)
        setValue(HouseFlag.WINDOW_WIDTH, 2);

        // Window Distance (Default: Fixed Value)
        setValue(HouseFlag.WINDOW_DISTANCE, 2);

        // Max Roof Height (Default: Fixed Value)
        setValue(HouseFlag.MAX_ROOF_HEIGHT, 10);
    }
}

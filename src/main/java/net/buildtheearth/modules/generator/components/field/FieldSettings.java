package net.buildtheearth.modules.generator.components.field;

import com.cryptomorin.xseries.XMaterial;
import net.buildtheearth.modules.generator.model.Settings;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.MenuItems;
import net.buildtheearth.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FieldSettings extends Settings {

    public FieldSettings(Player player) {
        super(player);
    }

    @Override
    public void setDefaultValues() {
        // Crop Type (Default: Random)
        Crop crop = (Crop) Utils.pickRandom(Crop.values());
        if(crop != null)
            setValue(FieldFlag.CROP_TYPE, crop.getIdentifier());

        // Crop Stage (Default: Random)
        CropStage cropStage = CropStage.FALLBACK;
        if(crop != null){
            if (crop.equals(Crop.POTATO)) {
                cropStage = (CropStage) Utils.pickRandom(new CropStage[]{CropStage.LOW, CropStage.TALL});
            } else if (crop.equals(Crop.CORN)) {
                cropStage = (CropStage) Utils.pickRandom(new CropStage[]{CropStage.TALL, CropStage.HARVESTED});
            } else if (crop.equals(Crop.WHEAT)) {
                cropStage = (CropStage) Utils.pickRandom(new CropStage[]{CropStage.LIGHT, CropStage.DARK});
            } else if (crop.equals(Crop.HARVESTED) || crop.equals(Crop.OTHER)) {
                cropStage = (CropStage) Utils.pickRandom(new CropStage[]{CropStage.DRY, CropStage.WET});
            }
        }
        if(cropStage != null)
            setValue(FieldFlag.CROP_STAGE, cropStage.getIdentifier());
        
        // Fence (Default: Random)
        ItemStack block = (ItemStack) Utils.pickRandom(MenuItems.getFences().toArray());
        if(block != null)
            setValue(FieldFlag.FENCE, Item.getUniqueMaterialString(block));
    }

}

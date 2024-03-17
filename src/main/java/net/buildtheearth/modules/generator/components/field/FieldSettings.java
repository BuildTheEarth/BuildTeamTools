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
        CropType cropType = (CropType) Utils.pickRandom(CropType.values());
        if(cropType != null)
            setValue(FieldFlag.CROP_TYPE, cropType);

        // Crop Stage (Default: Random)
        CropStage cropStage = CropStage.FALLBACK;
        if(cropType != null){
            if (cropType.equals(CropType.POTATO)) {
                cropStage = (CropStage) Utils.pickRandom(new CropStage[]{CropStage.LOW, CropStage.TALL});
            } else if (cropType.equals(CropType.CORN)) {
                cropStage = (CropStage) Utils.pickRandom(new CropStage[]{CropStage.TALL, CropStage.HARVESTED});
            } else if (cropType.equals(CropType.WHEAT)) {
                cropStage = (CropStage) Utils.pickRandom(new CropStage[]{CropStage.LIGHT, CropStage.DARK});
            } else if (cropType.equals(CropType.HARVESTED) || cropType.equals(CropType.OTHER)) {
                cropStage = (CropStage) Utils.pickRandom(new CropStage[]{CropStage.DRY, CropStage.WET});
            }
        }
        if(cropStage != null)
            setValue(FieldFlag.CROP_STAGE, cropStage);
        
        // Fence (Default: Random)
        ItemStack block = (ItemStack) Utils.pickRandom(MenuItems.getFences().toArray());
        if(block != null)
            setValue(FieldFlag.FENCE, new XMaterial[]{XMaterial.matchXMaterial(block)});
    }

}

package net.buildtheearth.modules.generator.components.field;

import net.buildtheearth.modules.generator.model.Settings;
import net.buildtheearth.utils.Utils;
import org.bukkit.entity.Player;

public class FieldSettings extends Settings {

    public FieldSettings(Player player) {
        super(player);
    }

    @Override
    public void setDefaultValues() {
        Crop crop = (Crop) Utils.pickRandom(Crop.values());
        getValues().put(FieldFlag.CROP_TYPE, crop.getIdentifier());

        CropStage cropStage = CropStage.FALLBACK;
        if (crop.equals(Crop.POTATO)) {
            cropStage = (CropStage) Utils.pickRandom(new CropStage[]{CropStage.LOW, CropStage.TALL});
        } else if (crop.equals(Crop.CORN)) {
            cropStage = (CropStage) Utils.pickRandom(new CropStage[]{CropStage.TALL, CropStage.HARVESTED});
        } else if (crop.equals(Crop.WHEAT)) {
            cropStage = (CropStage) Utils.pickRandom(new CropStage[]{CropStage.LIGHT, CropStage.DARK});
        } else if (crop.equals(Crop.HARVESTED) || crop.equals(Crop.OTHER)) {
            cropStage = (CropStage) Utils.pickRandom(new CropStage[]{CropStage.DRY, CropStage.WET});
        }

        getValues().put(FieldFlag.CROP_STAGE, cropStage.getIdentifier());
        getValues().put(FieldFlag.FENCE, "188");
    }

}

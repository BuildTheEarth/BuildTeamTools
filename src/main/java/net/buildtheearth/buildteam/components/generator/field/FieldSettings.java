package net.buildtheearth.buildteam.components.generator.field;

import net.buildtheearth.buildteam.components.generator.Settings;
import net.buildtheearth.utils.Utils;
import org.bukkit.entity.Player;

public class FieldSettings extends Settings {

    public FieldSettings(Player player) {
        super(player);
    }

    @Override
    public void setDefaultValues() {

        Crop crop = (Crop) Utils.pickRandom(Crop.values());
        getValues().put(FieldFlag.CROP, crop.getIdentifier());

        CropStage cropStage = CropStage.FALLBACK;
        if (crop.equals(Crop.POTATO))
            cropStage = (CropStage) Utils.pickRandom(new CropStage[]{CropStage.LOW, CropStage.TALL});
        if (crop.equals(Crop.CORN))
            cropStage = (CropStage) Utils.pickRandom(new CropStage[]{CropStage.TALL, CropStage.HARVESTED});
        if (crop.equals(Crop.WHEAT))
            cropStage = (CropStage) Utils.pickRandom(new CropStage[]{CropStage.LIGHT, CropStage.DARK});
        if (crop.equals(Crop.HARVESTED) || crop.equals(Crop.OTHER))
            cropStage = (CropStage) Utils.pickRandom(new CropStage[]{CropStage.DRY, CropStage.WET});
        if (cropStage == null) cropStage = CropStage.FALLBACK;
        getValues().put(FieldFlag.TYPE, cropStage.getIdentifier());

        getValues().put(FieldFlag.FENCE, "188");
    }
}

package net.buildtheearth.buildteamtools.modules.generator.model;

import com.alpsbte.alpslib.utils.item.Item;
import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import lombok.Setter;
import net.buildtheearth.buildteamtools.modules.generator.components.field.CropStage;
import net.buildtheearth.buildteamtools.modules.generator.components.field.CropType;
import net.buildtheearth.buildteamtools.modules.generator.components.house.RoofType;
import net.buildtheearth.buildteamtools.modules.generator.components.rail.RailType;
import net.buildtheearth.buildteamtools.modules.generator.components.tree.TreeWidth;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;

public abstract class Settings {

    @Getter
    private final Player player;

    @Getter
    private final HashMap<Flag, Object> values;

    @Getter
    @Setter
    private Block[][][] blocks;

    public Settings(Player player) {
        this.player = player;
        this.values = new HashMap<>();

        setDefaultValues();
    }

    public abstract void setDefaultValues();

    public void setValue(Flag flag, Object value) {
        if (FlagType.validateFlagType(flag, value) != null) {
            value = FlagType.convertToFlagType(flag, value.toString());

            String errorMessage = FlagType.validateFlagType(flag, value);

            if (errorMessage != null) {
                player.sendMessage(errorMessage);
                return;
            }
        }

        getValues().put(flag, value);
    }

    public HashMap<Flag, String> getValuesAsString() {
        HashMap<Flag, String> values = new HashMap<>();

        for (Flag flag : getValues().keySet()) {
            Object valueObject = getValues().get(flag);

            if (valueObject == null)
                continue;

            StringBuilder valueStr;

            switch (flag.getFlagType()) {
                case XMATERIAL:
                    valueStr = new StringBuilder(Item.getUniqueMaterialString((XMaterial) valueObject));
                    break;
                case XMATERIAL_LIST:
                    XMaterial[] materials = (XMaterial[]) valueObject;

                    if (materials.length == 0) {
                        // Skip this entry if no materials
                        continue;
                    }

                    valueStr = new StringBuilder(Item.getUniqueMaterialString(materials[0]));
                    for (int i = 1; i < materials.length; i++)
                        valueStr.append(",").append(Item.getUniqueMaterialString(materials[i]));

                    break;
                case ROOF_TYPE:
                    valueStr = new StringBuilder(((RoofType) valueObject).getType());
                    break;
                case CROP_TYPE:
                    valueStr = new StringBuilder(((CropType) valueObject).getIdentifier());
                    break;
                case CROP_STAGE:
                    valueStr = new StringBuilder(((CropStage) valueObject).getIdentifier());
                    break;
                case RAIL_TYPE:
                    valueStr = new StringBuilder(((RailType) valueObject).getIdentifier());
                    break;
                case TREE_WIDTH:
                    valueStr = new StringBuilder(((TreeWidth) valueObject).getName());
                    break;
                default:
                    valueStr = new StringBuilder(getValues().get(flag).toString());
                    break;
            }

            values.put(flag, valueStr.toString());
        }

        return values;
    }
}

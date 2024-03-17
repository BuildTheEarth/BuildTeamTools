package net.buildtheearth.modules.generator.model;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import lombok.Setter;
import net.buildtheearth.modules.generator.components.field.CropStage;
import net.buildtheearth.modules.generator.components.field.CropType;
import net.buildtheearth.modules.generator.components.house.RoofType;
import net.buildtheearth.modules.generator.components.tree.TreeWidth;
import net.buildtheearth.utils.Item;
import org.bukkit.TreeType;
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

    public Settings(Player player){
        this.player = player;
        this.values = new HashMap<>();

        setDefaultValues();
    }

    public abstract void setDefaultValues();

    public void setValue(Flag flag, Object value){
        if(FlagType.validateFlagType(flag, value) != null){
            value = FlagType.convertToFlagType(flag, value.toString());

            String errorMessage = FlagType.validateFlagType(flag, value);

            if(errorMessage != null){
                player.sendMessage(errorMessage);
                return;
            }
        }

        getValues().put(flag, value);
    }

    public HashMap<Flag, String> getValuesAsString(){
        HashMap<Flag, String> values = new HashMap<>();

        for(Flag flag : getValues().keySet()){
            Object valueObject = getValues().get(flag);
            StringBuilder value;

            if(valueObject == null)
                continue;

            switch(flag.getFlagType()){
                case XMATERIAL:
                    value = new StringBuilder(Item.getUniqueMaterialString((XMaterial) valueObject));
                    break;
                case XMATERIAL_LIST:
                    XMaterial[] materials = (XMaterial[]) valueObject;

                    if(materials == null || materials.length == 0){
                        value = null;
                        break;
                    }

                    value = new StringBuilder(Item.getUniqueMaterialString(((XMaterial[]) valueObject)[0]));
                    for(int i = 1; i < materials.length; i++)
                        value.append(",").append(Item.getUniqueMaterialString(materials[i]));

                    break;
                case ROOF_TYPE:
                    value = new StringBuilder(((RoofType) valueObject).getType());
                    break;
                case CROP_TYPE:
                    value = new StringBuilder(((CropType) valueObject).getIdentifier());
                    break;
                case CROP_STAGE:
                    value = new StringBuilder(((CropStage) valueObject).getIdentifier());
                    break;
                case TREE_WIDTH:
                    value = new StringBuilder(((TreeWidth) valueObject).getName());
                    break;
                default:
                    value = new StringBuilder(getValues().get(flag).toString());
                    break;
            }

            if(value == null)
                continue;

            values.put(flag, value.toString());
        }

        return values;
    }
}

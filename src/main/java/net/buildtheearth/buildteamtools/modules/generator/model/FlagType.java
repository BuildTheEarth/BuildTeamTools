package net.buildtheearth.buildteamtools.modules.generator.model;

import com.alpsbte.alpslib.utils.ChatHelper;
import com.alpsbte.alpslib.utils.item.Item;
import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import net.buildtheearth.buildteamtools.modules.generator.components.field.CropStage;
import net.buildtheearth.buildteamtools.modules.generator.components.field.CropType;
import net.buildtheearth.buildteamtools.modules.generator.components.house.RoofType;
import net.buildtheearth.buildteamtools.modules.generator.components.tree.TreeType;
import net.buildtheearth.buildteamtools.modules.generator.components.tree.TreeWidth;

public enum FlagType {

    STRING(String.class),
    INTEGER(Integer.class),
    DOUBLE(Double.class),
    FLOAT(Float.class),
    BOOLEAN(Boolean.class),
    XMATERIAL(XMaterial.class),
    XMATERIAL_LIST(XMaterial[].class),
    ROOF_TYPE(RoofType.class),
    CROP_TYPE(CropType.class),
    CROP_STAGE(CropStage.class),
    TREE_TYPE(TreeType.class),
    TREE_WIDTH(TreeWidth.class);

    @Getter
    private final Class<?> classType;

    FlagType(Class<?> classType){
        this.classType = classType;
    }

    /** Validates that the given flag value is of the correct type
     *
     * @param flag The flag to validate
     * @param value The value to validate
     * @return The error message if the value is invalid, null if the value is valid
     */
    public static String validateFlagType(Flag flag, Object value){
        if(value == null)
            return null;

        if(!flag.getFlagType().getClassType().isInstance(value))
            return ChatHelper.getErrorString("Invalid value for flag %s: expected %s, got %s", flag.getFlag(), flag.getFlagType().getClassType().getSimpleName(), value.getClass().getSimpleName());

        return null;
    }

    /** Converts the given string to the correct type for the flag
     *
     * @param flag The flag to convert the value for
     * @param value The value to convert
     * @return The converted value
     */
    public static Object convertToFlagType(Flag flag, String value){
        if(value == null)
            return null;

        switch (flag.getFlagType()){
            case STRING:
                return value;
            case INTEGER:
                return Integer.parseInt(value);
            case DOUBLE:
                return Double.parseDouble(value);
            case FLOAT:
                return Float.parseFloat(value);
            case BOOLEAN:
                return Boolean.parseBoolean(value);
            case XMATERIAL:
                return Item.convertStringToXMaterial(value);
            case XMATERIAL_LIST:
                String[] values = value.split(",");
                XMaterial[] materials = new XMaterial[values.length];

                for(int i = 0; i < values.length; i++)
                    materials[i] = Item.convertStringToXMaterial(values[i]);

                return materials;
            case ROOF_TYPE:
                return RoofType.byString(value);
            case CROP_TYPE:
                return CropType.getByIdentifier(value);
            case CROP_STAGE:
                return CropStage.getByIdentifier(value);
            case TREE_TYPE:
                return TreeType.byString(value);
            case TREE_WIDTH:
                return TreeWidth.byString(value);
        }
        return null;
    }
}

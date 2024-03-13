package net.buildtheearth.modules.generator.components;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import net.buildtheearth.modules.generator.components.field.CropStage;
import net.buildtheearth.modules.generator.components.field.CropType;
import net.buildtheearth.modules.generator.components.house.RoofType;
import net.buildtheearth.modules.generator.components.tree.TreeType;
import net.buildtheearth.modules.generator.components.tree.TreeWidth;
import net.buildtheearth.modules.generator.model.Flag;
import org.bukkit.Bukkit;
import org.bukkit.Material;

public enum FlagType {

    STRING(String.class),
    INTEGER(Integer.class),
    DOUBLE(Double.class),
    FLOAT(Float.class),
    BOOLEAN(Boolean.class),
    XMATERIAL(XMaterial.class),
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
            return String.format("Invalid value for flag %s: expected %s, got %s", flag.getFlag(), flag.getFlagType().getClassType().getSimpleName(), value.getClass().getSimpleName());

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
                XMaterial material;

                if(XMaterial.matchXMaterial(value).isPresent())
                    material = XMaterial.matchXMaterial(value).get();
                else {
                    Material mat = Material.matchMaterial(value);

                    if(mat != null)
                        material = XMaterial.matchXMaterial(mat);
                    else
                        return null;
                }

                return material;
            case ROOF_TYPE:
                return RoofType.byString(value);
            case CROP_TYPE:
                return CropType.getByIdentifier(value);
            case CROP_STAGE:
                return CropStage.getByIdentifier(value);
            case TREE_TYPE:
                return TreeType.valueOf(value);
            case TREE_WIDTH:
                return TreeWidth.valueOf(value);
        }
        return null;
    }
}

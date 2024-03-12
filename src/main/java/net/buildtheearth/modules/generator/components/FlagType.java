package net.buildtheearth.modules.generator.components;

import net.buildtheearth.modules.generator.model.Flag;

public enum FlagType {

    ENUM,
    STRING,
    INTEGER,
    DOUBLE,
    FLOAT,
    BOOLEAN,
    BLOCKS;

    /** Validates that the given flag value is of the correct type
     *
     * @param flag The flag to validate
     * @param value The value to validate
     * @return The error message if the value is invalid, null if the value is valid
     */
    public static String validateFlagType(Flag flag, String value){
        switch (flag.getFlagType()){
            case INTEGER:
                try {
                    Integer.parseInt(value);
                } catch (NumberFormatException e){
                    return "§cThe value §e" + value + " §cof the flag §e" + flag.getFlag() + " §cis not a valid integer.";
                }
                break;
            case DOUBLE:
                try {
                    Double.parseDouble(value);
                } catch (NumberFormatException e){
                    return "§cThe value §e" + value + " §cof the flag §e" + flag.getFlag() + " §cis not a valid double.";
                }
                break;
            case BOOLEAN:
                if(!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false"))
                    return "§cThe value §e" + value + " §cof the flag §e" + flag.getFlag() + " §cis not a valid boolean.";
                break;
            default:
                break;
        }

        return null;
    }
}

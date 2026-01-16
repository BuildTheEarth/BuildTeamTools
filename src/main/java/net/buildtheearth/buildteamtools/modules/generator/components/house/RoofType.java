package net.buildtheearth.buildteamtools.modules.generator.components.house;

public enum RoofType {

    FLAT("FLAT"),

    FLATTER_SLABS("FLATTER_SLABS"),

    MEDIUM_SLABS("MEDIUM_SLABS"),

    STEEP_SLABS("STEEP_SLABS"),
    STAIRS("STAIRS");

    private final String type;

    RoofType(String type) {
        this.type = type;
    }

    public static RoofType byString(String type) {
        for (RoofType roofType : RoofType.values())
            if (roofType.getType().equalsIgnoreCase(type))
                return roofType;

        return null;
    }

    public String getType() {
        return type;
    }
}

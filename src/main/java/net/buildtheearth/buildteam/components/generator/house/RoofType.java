package net.buildtheearth.buildteam.components.generator.house;

public enum RoofType {

    FLAT("FLAT"),
    SLABS("SLABS"),
    STAIRS("STAIRS");

    private String type;

    RoofType(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static RoofType byString(String type){
        for(RoofType roofType : RoofType.values())
        if(roofType.getType().equalsIgnoreCase(type))
            return roofType;

        return null;
    }
}

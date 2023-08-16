package net.buildtheearth.buildteam.components.generator.field;

import net.buildtheearth.buildteam.components.generator.house.HouseFlag;
import net.buildtheearth.buildteam.components.generator.house.RoofType;

public enum Crop {

    POTATO("POTATO", true),
    CORN("CORN", false),
    WHEAT("WHEAT", false),
    VINEYARD("VINEYARD", true), //NOTE: special lines
    PEAR("PEAR", true), //NOTE: special lines
    CATTLE("CATTLE", false),
    MEADOW("MEADOW", false),
    HARVESTED("HARVESTED", true),
    OTHER("OTHER", true);

    private final String identifier;
    private final boolean linesRequired;

    Crop(String identifier, boolean linesRequired) {
        this.identifier = identifier;
        this.linesRequired = linesRequired;
    }

    public String getIdentifier() {
        return identifier;
    }

    public boolean isLinesRequired() {
        return linesRequired;
    }

    public static Crop getByIdentifier(String identifier) {
        for(Crop crop : Crop.values())
            if(crop.getIdentifier().equalsIgnoreCase(identifier))
                return crop;

        return POTATO;
    }
}

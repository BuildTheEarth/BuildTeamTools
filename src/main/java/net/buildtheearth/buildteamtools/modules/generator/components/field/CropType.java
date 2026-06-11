package net.buildtheearth.buildteamtools.modules.generator.components.field;

import lombok.Getter;

public enum CropType {

    POTATO("POTATO", true, true),
    WHEAT("WHEAT", false, true),
    CORN("CORN", false, true),
    VINEYARD("VINEYARD", true, false), //NOTE: alternating lines
    PEAR("PEAR", true, false), //NOTE: alternating lines
    CATTLE("CATTLE", false, false),
    MEADOW("MEADOW", false, false),
    HARVESTED("HARVESTED", true, true),
    OTHER("OTHER", true, true);

    @Getter
    private final String identifier;
    @Getter
    private final boolean linesRequired;
    private final boolean hasStages;

    CropType(String identifier, boolean linesRequired, boolean hasStages) {
        this.identifier = identifier;
        this.linesRequired = linesRequired;
        this.hasStages = hasStages;
    }

    public static CropType getByIdentifier(String identifier) {
        for (CropType cropType : CropType.values()) {
            if (cropType.getIdentifier().equalsIgnoreCase(identifier)) {
                return cropType;
            }
        }
        return POTATO;
    }

    public boolean hasStages() {
        return hasStages;
    }
}

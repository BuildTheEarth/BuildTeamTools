package net.buildtheearth.buildteam.components.generator.field;

public enum CropStage {

    FALLBACK("FALLBACK"),
    LOW("LOW"),
    TALL("TALL"),
    HARVESTED("HARVESTED"),
    LIGHT("LIGHT"),
    DARK("DARK"),
    DRY("DRY"),
    WET("WET");


    private final String identifier;

    CropStage(String identifier) {
        this.identifier = identifier;
    }

    public static CropStage getByIdentifier(String identifier) {
        for (CropStage cropStage : CropStage.values()) {
            if (cropStage.getIdentifier().equalsIgnoreCase(identifier)) {
                return cropStage;
            }
        }
        return FALLBACK;
    }

    public String getIdentifier() {
        return identifier;
    }
}

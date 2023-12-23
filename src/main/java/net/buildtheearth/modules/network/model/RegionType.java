package net.buildtheearth.modules.network.model;

import lombok.Getter;
import lombok.NonNull;

public enum RegionType {
    CONTINENT("Continent"),
    COUNTRY("Country"),
    STATE("State"),
    CITY("City"),
    OTHER("Other");

    @Getter
    private final String label;

    RegionType(String label) {
        this.label = label;
    }

    public static @NonNull RegionType getByLabel(String label) {
        for(RegionType continent : RegionType.values())
            if(continent.getLabel().equalsIgnoreCase(label)) return continent;

        return RegionType.COUNTRY;
    }
}

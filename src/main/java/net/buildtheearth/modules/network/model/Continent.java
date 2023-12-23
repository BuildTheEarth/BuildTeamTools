package net.buildtheearth.modules.network.model;

import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

public enum Continent {
    North_America("North America", 9),
    South_America("South America", 11),
    Europe("Europe", 13),
    Africa("Africa", 15),
    Asia("Asia", 17),
    Other("Other", 22);

    @Getter
    private final String label;
    @Getter
    private final int slot;
    @Getter
    private final List<Region> regions;

    Continent(String label, int slot) {
        this.label = label;
        this.slot = slot;
        this.regions = new ArrayList<>();
    }

    public static @NonNull Continent getByLabel(String label) {
        for(Continent continent : Continent.values())
            if(continent.getLabel().equalsIgnoreCase(label)) return continent;

        return Continent.Other;
    }
}


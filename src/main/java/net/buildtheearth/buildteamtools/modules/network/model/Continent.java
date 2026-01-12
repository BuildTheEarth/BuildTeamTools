package net.buildtheearth.buildteamtools.modules.network.model;

import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

public enum Continent {
    NORTH_AMERICA("North America", 9),
    SOUTH_AMERICA("South America", 11),
    EUROPE("Europe", 13),
    AFRICA("Africa", 15),
    ASIA("Asia", 17),
    OTHER("Other", 22);

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

    public List<Region> getCountries() {
        List<Region> countries = new ArrayList<>();
        for(Region region : regions)
            if(region.getType() == RegionType.COUNTRY)
                countries.add(region);

        return countries;
    }

    public static @NonNull Continent getByLabel(String label) {
        for(Continent continent : Continent.values())
            if(continent.getLabel().equalsIgnoreCase(label)) return continent;

        return Continent.OTHER;
    }
}


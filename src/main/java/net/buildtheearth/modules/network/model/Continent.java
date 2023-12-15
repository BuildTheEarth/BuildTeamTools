package net.buildtheearth.modules.network.model;

import java.util.ArrayList;
import java.util.List;

public enum Continent {
    North_America("North America", 9),
    South_America("South America", 11),
    Europe("Europe", 13),
    Africa("Africa", 15),
    Asia("Asia", 17),
    Other("Other", 22);

    private final String label;
    private final int slot;
    private final List<Country> countries;

    Continent(String label, int slot) {
        this.label = label;
        this.slot = slot;

        // This line makes a set that automatically sorts the countries on connection status and alphabetically
        this.countries = new ArrayList<>();
    }

    public static Continent getBySlot(int slot) {
        for(Continent continent : Continent.values())
            if(continent.getSlot() == slot) return continent;
        return Other;
    }

    public String getLabel() {
        return label;
    }

    public int getSlot() {
        return slot;
    }

    public List<Country> getCountries() {
        return countries;
    }

    public static Continent getByLabel(String label) {
        for(Continent continent : Continent.values()) {
            if(continent.getLabel().equals(label)) return continent;
        }
        return null;
    }
}


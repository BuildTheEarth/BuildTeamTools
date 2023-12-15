package net.buildtheearth.modules.network.model;

import java.util.HashSet;
import java.util.Set;

public enum Continent {
    North_America("North America", 9),
    South_America("South America", 11),
    Europe("Europe", 13),
    Africa("Africa", 15),
    Asia("Asia", 17),
    Other("Other", 22);

    private final String label;
    private final int slot;
    private final Set<Country> countries;

    Continent(String label, int slot) {
        this.label = label;
        this.slot = slot;
        this.countries = new HashSet<>();
    }

    public String getLabel() {
        return label;
    }

    public int getSlot() {
        return slot;
    }

    public Set<Country> getCountries() {
        return countries;
    }

    public static Continent getBySlot(int slot) {
        for(Continent continent : Continent.values()) {
            if (continent.slot == slot) return continent;
        }
        return Continent.Europe;
    }

    public static Continent getByLabel(String label) {
        for(Continent continent : Continent.values()) {
            if(continent.getLabel().equals(label)) return continent;
        }
        return null;
    }
}


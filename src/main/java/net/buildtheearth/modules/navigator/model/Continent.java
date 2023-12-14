package net.buildtheearth.modules.navigator.model;

public enum Continent {
    North_America("North America", 9),
    South_America("South America", 11),
    Europe("Europe", 13),
    Africa("Africa", 15),
    Asia("Asia", 17),
    Other("Other", 22);

    public final String label;
    public final int slot;

    Continent(String label, int slot) {
        this.label = label;
        this.slot = slot;
    }

    public static Continent getBySlot(int slot) {
        for(Continent continent : Continent.values()) {
            if (continent.slot == slot) return continent;
        }
        return Continent.Europe;
    }
}


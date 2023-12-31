package net.buildtheearth.modules.network.model;

import lombok.Getter;

public class Region {

    @Getter
    private final Continent continent;
    @Getter
    private final BuildTeam buildTeam;
    @Getter
    private final String name;
    @Getter
    private final RegionType type;
    @Getter
    private String headBase64;
    @Getter
    private int area;

    public Region(String name, RegionType regionType, Continent continent, BuildTeam buildTeam, String headBase64, int area) {
        this.continent = continent;
        this.name = name;
        this.type = regionType;
        this.buildTeam = buildTeam;
        this.headBase64 = headBase64;
        this.area = area;
    }

    public static Region getByName(String name) {
        Region lastRegion = null;
        for(Continent continent : Continent.values()) {
            for (Region region : continent.getRegions()) {
                lastRegion = region;
                if(region.getName().equals(name)) return region;
            }
        }
        return lastRegion;
    }

    // Getter
    public boolean isConnected() {
        return buildTeam != null && buildTeam.isConnected();
    }
}


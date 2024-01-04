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
    private final String countryCodeCca3;
    @Getter
    private final String countryCodeCca2;
    @Getter
    private final RegionType type;
    @Getter
    private final String headBase64;
    @Getter
    private final int area;


    /** Constructor for any region type
     *
     * @param name The name of the region
     * @param regionType The type of the region
     * @param continent The continent the region is in
     * @param buildTeam The build team that owns the region
     * @param headBase64 The base64 string of the head of the region
     */
    public Region(String name, RegionType regionType, Continent continent, BuildTeam buildTeam, String headBase64) {
        this.continent = continent;
        this.name = name;
        this.type = regionType;
        this.buildTeam = buildTeam;
        this.headBase64 = headBase64;

        this.area = 0;
        this.countryCodeCca2 = null;
        this.countryCodeCca3 = null;
    }

    /** Constructor for a country
     *
     * @param name The name of the country
     * @param continent The continent the country is in
     * @param buildTeam The build team that owns the country
     * @param headBase64 The base64 string of the head of the country
     * @param area The size of the country in km²
     * @param countryCodeCca2 The country code of the country in ISO 3166-1 alpha-2 format
     * @param countryCodeCca3 The country code of the country in ISO 3166-1 alpha-3 format
     */
    public Region(String name, Continent continent, BuildTeam buildTeam, String headBase64, int area, String countryCodeCca2, String countryCodeCca3) {
        if(countryCodeCca2.length() != 2)
            throw new IllegalArgumentException("Country code must be in ISO 3166-1 alpha-2 format!");
        if(countryCodeCca3.length() != 3)
            throw new IllegalArgumentException("Country code must be in ISO 3166-1 alpha-3 format!");

        this.type = RegionType.COUNTRY;

        this.continent = continent;
        this.name = name;
        this.buildTeam = buildTeam;
        this.headBase64 = headBase64;
        this.area = area;
        this.countryCodeCca2 = countryCodeCca2;
        this.countryCodeCca3 = countryCodeCca3;
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


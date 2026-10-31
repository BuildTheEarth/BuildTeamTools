package net.buildtheearth.buildteamtools.modules.network.model;

import org.jspecify.annotations.Nullable;

public record Region(
        Continent continent,
        BuildTeam buildTeam,
        String name,
        String countryCodeCca3,
        String countryCodeCca2,
        RegionType type,
        String headBase64,
        int area
) {

    public Region {
        if (type == RegionType.COUNTRY) {
            if (countryCodeCca2 == null || countryCodeCca2.length() != 2)
                throw new IllegalArgumentException("Country code must be in ISO 3166-1 alpha-2 format!");
            if (countryCodeCca3 == null || countryCodeCca3.length() != 3)
                throw new IllegalArgumentException("Country code must be in ISO 3166-1 alpha-3 format!");
        }
    }

    /**
     * Constructor for any region type
     *
     * @param name       The name of the region
     * @param regionType The type of the region
     * @param continent  The continent the region is in
     * @param buildTeam  The build team that owns the region
     * @param headBase64 The base64 string of the head of the region
     */
    public Region(String name, RegionType regionType, Continent continent, BuildTeam buildTeam, String headBase64) {
        this(continent, buildTeam, name, null, null, regionType, headBase64, 0);
    }

    /**
     * Constructor for a country
     *
     * @param name            The name of the country
     * @param continent       The continent the country is in
     * @param buildTeam       The build team that owns the country
     * @param headBase64      The base64 string of the head of the country
     * @param area            The size of the country in km²
     * @param countryCodeCca2 The country code of the country in ISO 3166-1 alpha-2 format
     * @param countryCodeCca3 The country code of the country in ISO 3166-1 alpha-3 format
     */
    public Region(String name, Continent continent, BuildTeam buildTeam, String headBase64, int area, String countryCodeCca2,
                  String countryCodeCca3) {
        this(continent, buildTeam, name, countryCodeCca3, countryCodeCca2, RegionType.COUNTRY, headBase64, area);
    }

    public Continent getContinent() {
        return continent;
    }

    public BuildTeam getBuildTeam() {
        return buildTeam;
    }

    public String getName() {
        return name;
    }

    public String getCountryCodeCca3() {
        return countryCodeCca3;
    }

    public String getCountryCodeCca2() {
        return countryCodeCca2;
    }

    public RegionType getType() {
        return type;
    }

    public String getHeadBase64() {
        return headBase64;
    }

    public int getArea() {
        return area;
    }

    public static @Nullable Region getByName(String name) {
        for (Continent continent : Continent.values()) {
            for (Region region : continent.getRegions()) {
                if (region.getName().equals(name)) return region;
            }
        }
        return null;
    }
}

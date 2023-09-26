package net.buildtheearth.buildteam.components.warps.model;

import java.util.UUID;

public class Warp {

    private final UUID key;
    private final String countryCode;
    private final double lat;
    private final double lon;

    public Warp(String countryCode, double lat, double lon) {
        this.key = UUID.randomUUID();
        this.countryCode = countryCode;
        this.lat = lat;
        this.lon = lon;
    }

    public UUID getKey() {
        return key;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}

package net.buildtheearth.buildteam.components.warps.model;

import java.util.UUID;


public class Warp {

    private final String key;
    private final String countryCode;

    private final double lat;
    private final double lon;

    private final double pitch;
    private final double yaw;

    public Warp(String key, String countryCode, double lat, double lon, double pitch, double yaw) {
        this.key = key;
        this.countryCode = countryCode;

        this.lat = lat;
        this.lon = lon;

        this.pitch = pitch;
        this.yaw = yaw;
    }

    public String getKey() {
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

    public double getPitch() {
        return pitch;
    }

    public double getYaw() {
        return yaw;
    }
}

package net.buildtheearth.buildteam.components.warps.model;


public class Warp {

    private final String key;
    private final String countryCode;
    private final String subRegion;
    private final String city;

    private final double lat;
    private final double lon;
    private final double y;

    private final double pitch;
    private final double yaw;

    private final boolean isHighlight;

    public Warp(String key, String countryCode, String subRegion, String city, double lat, double lon, double y, double pitch, double yaw, boolean isHighlight) {
        this.key = key;
        this.countryCode = countryCode;
        this.subRegion = subRegion;
        this.city = city;

        this.lat = lat;
        this.lon = lon;
        this.y = y;

        this.pitch = pitch;
        this.yaw = yaw;

        this.isHighlight = isHighlight;
    }

    public String getKey() {
        return key;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getSubRegion() {
        return subRegion;
    }

    public String getCity() {
        return city;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public double getY() {
        return y;
    }

    public double getPitch() {
        return pitch;
    }

    public double getYaw() {
        return yaw;
    }

    public boolean isHighlight() {
        return isHighlight;
    }
}

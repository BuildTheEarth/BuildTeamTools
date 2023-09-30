package net.buildtheearth.modules.warp.model;

public class Warp {
    private final String worldName;

    private final String key;
    private final String countryCode;
    private final String subRegion;
    private final String city;

    private final double lat;
    private final double lon;
    private final double y;

    private final float yaw;
    private final float pitch;

    private final boolean isHighlight;

    public Warp(String key, String countryCode, String subRegion, String city, String worldName, double lat, double lon, double y, float pitch, float yaw, boolean isHighlight) {
        this.worldName = worldName;

        this.key = key;
        this.countryCode = countryCode;
        this.subRegion = subRegion;
        this.city = city;

        this.lat = lat;
        this.lon = lon;
        this.y = y;

        this.yaw = yaw;
        this.pitch = pitch;

        this.isHighlight = isHighlight;
    }

    public String getWorldName() {
        return worldName;
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

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public boolean isHighlight() {
        return isHighlight;
    }
}

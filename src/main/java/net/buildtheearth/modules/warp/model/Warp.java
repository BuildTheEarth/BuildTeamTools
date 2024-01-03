package net.buildtheearth.modules.warp.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class Warp {

    @Getter
    private UUID id;

    @Getter
    @Setter
    private final String key;
    @Getter
    private final String countryCode;
    @Getter
    private final String countryCodeType = "cca2";

    @Getter
    private final String subRegion;
    @Getter
    private final String city;

    @Getter
    private final String worldName;

    @Getter
    private final double lat;
    @Getter
    private final double lon;
    @Getter
    private final double y;

    @Getter
    private final float yaw;
    @Getter
    private final float pitch;

    @Getter
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

    public Warp(String key, String countryCode, String countryCodeType, String subRegion, String city, String worldName, double lat, double lon, double y, float pitch, float yaw, boolean isHighlight) {
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
}

package net.buildtheearth.modules.warp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
public class Warp {

    @Getter
    private final UUID id;

    @Getter
    private UUID warpGroupID;

    @Getter
    private String name;

    @Getter
    private String countryCode;

    @Getter
    private String countryCodeType = "cca2";

    @Getter
    private String address;

    @Getter
    private String worldName;

    @Getter
    private double lat;

    @Getter
    private double lon;

    @Getter
    private double y;

    @Getter
    private float yaw;

    @Getter
    private float pitch;

    @Getter
    private boolean isHighlight;

    /** Create a warp with a random warp ID. */
    public Warp(UUID warpGroupID, String name, String countryCode, String countryCodeType, String address, String worldName, double lat, double lon, double y, float yaw, float pitch, boolean isHighlight) {
        this(UUID.randomUUID(), warpGroupID, name, countryCode, countryCodeType, address, worldName, lat, lon, y, yaw, pitch, isHighlight);
    }
}

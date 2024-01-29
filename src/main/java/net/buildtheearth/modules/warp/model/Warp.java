package net.buildtheearth.modules.warp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.util.UUID;

@AllArgsConstructor
public class Warp {

    @Getter
    private final UUID id;

    @Getter @Setter
    private WarpGroup warpGroup;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String countryCode;

    @Getter
    private String countryCodeType = "cca2";

    @Getter
    private String address;

    @Getter @Setter
    private String worldName;

    @Getter @Setter
    private double lat;

    @Getter @Setter
    private double lon;

    @Getter @Setter
    private double y;

    @Getter @Setter
    private float yaw;

    @Getter @Setter
    private float pitch;

    @Getter @Setter
    private boolean isHighlight;

    /** Create a warp with a random warp ID. */
    public Warp(WarpGroup warpGroup, String name, String countryCode, String countryCodeType, String address, String worldName, double lat, double lon, double y, float yaw, float pitch, boolean isHighlight) {
        this(UUID.randomUUID(), warpGroup, name, countryCode, countryCodeType, address, worldName, lat, lon, y, yaw, pitch, isHighlight);
    }

    public JSONObject toJSON(){
        JSONObject json = new JSONObject();

        json.put("id", id.toString());
        json.put("warpGroupID", warpGroup.getId().toString());
        json.put("name", name);
        json.put("countryCode", countryCode);
        json.put("countryCodeType", countryCodeType);
        json.put("worldName", worldName);
        json.put("lat", lat);
        json.put("lon", lon);
        json.put("y", y);
        json.put("yaw", yaw);
        json.put("pitch", pitch);
        json.put("isHighlight", isHighlight);

        if(address != null)
            json.put("address", address);

        return json;
    }
}

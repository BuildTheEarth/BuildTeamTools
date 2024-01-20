package net.buildtheearth.modules.warp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@AllArgsConstructor
public class Warp {

    @Getter
    private final UUID id = UUID.randomUUID();

    @Getter
    @Setter
    private String key;

    @Getter
    @Setter
    private String countryCode;

    @Getter
    @Setter
    private String countryCodeType = "cca2";

    @Getter
    @Setter
    private String group = null;

    @Getter
    @Setter
    private String worldName;

    @Getter
    @Setter
    private double lat;

    @Getter
    @Setter
    private double lon;

    @Getter
    @Setter
    private double y;

    @Getter
    private float yaw;
    @Getter
    private float pitch;

    @Getter
    @Setter
    private boolean isHighlight;


}

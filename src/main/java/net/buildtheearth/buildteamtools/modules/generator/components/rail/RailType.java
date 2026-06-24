package net.buildtheearth.buildteamtools.modules.generator.components.rail;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

public enum RailType {

    STANDARD("standard", "Standard", XMaterial.RAIL);

    @Getter
    private final String identifier;
    @Getter
    private final String displayName;
    @Getter
    private final XMaterial icon;

    RailType(String identifier, String displayName, XMaterial icon) {
        this.identifier = identifier;
        this.displayName = displayName;
        this.icon = icon;
    }

    public static @Nullable RailType byString(String value) {
        for (RailType railType : RailType.values()) {
            if (railType.getIdentifier().equalsIgnoreCase(value) || railType.name().equalsIgnoreCase(value))
                return railType;
        }

        return null;
    }
}

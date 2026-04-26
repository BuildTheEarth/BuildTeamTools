package net.buildtheearth.buildteamtools.modules.navigation.components.warps.model;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum WarpMigrationSource {
    ESSENTIALS("essentials");

    private final String[] validNames;

    WarpMigrationSource(String... validNames) {
        this.validNames = validNames;
    }

    public static WarpMigrationSource fromString(String name) {
        for (WarpMigrationSource warpMigrationSource : WarpMigrationSource.values()) {
            if (Arrays.asList(warpMigrationSource.validNames).contains(name)) {
                return warpMigrationSource;
            }
        }
        return null;
    }
}

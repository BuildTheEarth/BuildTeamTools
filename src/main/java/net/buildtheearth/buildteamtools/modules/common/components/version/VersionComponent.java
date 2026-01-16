package net.buildtheearth.buildteamtools.modules.common.components.version;

import lombok.Getter;
import net.buildtheearth.buildteamtools.modules.ModuleComponent;
import org.bukkit.Bukkit;

import java.util.Arrays;

public class VersionComponent extends ModuleComponent {


    public VersionComponent() {
        super("Version");
    }

    public Version getVersion() {
        String rawVersion = Bukkit.getBukkitVersion().split("-")[0]; // e.g., "1.21.4"
        try {
            return Version.fromBukkitVersion(rawVersion);

        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Unsupported server version: " + rawVersion, e);
        }
    }

    public boolean is_1_20() {
        return getVersion().equals(Version.v1_20_R1) || getVersion().equals(Version.v1_20_R2) || getVersion().equals(Version.v1_20_R3);
    }

    public boolean is_1_12() {
        return getVersion().equals(Version.v1_12_R1);
    }

    @Getter
    public enum Version {
        v1_12_R1(12, "1.12", "1.12.1", "1.12.2"),
        v1_13_R1(13, "1.13", "1.13.1"),
        v1_13_R2(13, "1.13.2"),
        v1_14_R1(14, "1.14", "1.14.1", "1.14.2", "1.14.3", "1.14.4"),
        v1_15_R1(15, "1.15", "1.15.1", "1.15.2"),
        v1_16_R1(16, "1.16", "1.16.1"),
        v1_16_R2(16, "1.16.2", "1.16.3"),
        v1_16_R3(16, "1.16.4", "1.16.5"),
        v1_17_R1(17, "1.17", "1.17.1"),
        v1_18_R1(18, "1.18", "1.18.1"),
        v1_18_R2(18, "1.18.2"),
        v1_19_R1(19, "1.19", "1.19.1", "1.19.2"),
        v1_19_R2(19, "1.19.3"),
        v1_19_R3(19, "1.19.4"),
        v1_20_R1(20, "1.20", "1.20.1"),
        v1_20_R2(20, "1.20.2", "1.20.3"),
        v1_20_R3(20, "1.20.4"),
        v1_21_R1(21, "1.21", "1.21.1", "1.21.2", "1.21.3", "1.21.4", "1.21.5", "1.21.6", "1.21.7", "1.21.8", "1.21.10", "1.21.11");

        private final int xseriesVersion;
        private final String[] bukkitVersions;

        Version(int xseriesVersion, String... bukkitVersions) {
            this.xseriesVersion = xseriesVersion;
            this.bukkitVersions = bukkitVersions;
        }

        public String getLatestBukkitVersion() {
            return bukkitVersions[bukkitVersions.length - 1];
        }

        public static Version fromBukkitVersion(String version) {
            for (Version v : values())
                if (Arrays.asList(v.bukkitVersions).contains(version))
                    return v;

            throw new IllegalArgumentException("Unsupported Bukkit version: " + version);
        }
    }
}

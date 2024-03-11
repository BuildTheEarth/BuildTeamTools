package net.buildtheearth.modules.common.components.version;

import lombok.Getter;
import net.buildtheearth.modules.Component;
import org.bukkit.Bukkit;

public class VersionComponent extends Component {


    public VersionComponent() {
        super("Version");
    }

    public Version getVersion() {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        return Version.valueOf(version);
    }

    public boolean is_1_20() {
        return getVersion().equals(Version.v1_20_R1) || getVersion().equals(Version.v1_20_R2) || getVersion().equals(Version.v1_20_R3);
    }

    public boolean is_1_12() {
        return getVersion().equals(Version.v1_12_R1);
    }

    enum Version{
        v1_12_R1("1.12", 12),
        v1_13_R1("1.13", 13),
        v1_13_R2("1.13.2", 13),
        v1_14_R1("1.14.4", 14),
        v1_15_R1("1.15.2", 15),
        v1_16_R1("1.16.1", 16),
        v1_16_R2("1.16.3", 16),
        v1_16_R3("1.16.5", 16),
        v1_17_R1("1.17.1", 17),
        v1_18_R1("1.18.1", 18),
        v1_18_R2("1.18.2", 18),
        v1_19_R1("1.19.2", 19),
        v1_19_R2("1.19.3", 19),
        v1_19_R3("1.19.4", 19),
        v1_20_R1("1.20.1", 20),
        v1_20_R2("1.20.2", 20),
        v1_20_R3("1.20.4", 20);

        @Getter
        private final String bukkitVersion;

        @Getter
        private final int xseriesVersion;

        Version(String bukkitVersion, int xseriesVersion){
            this.bukkitVersion = bukkitVersion;
            this.xseriesVersion = xseriesVersion;
        }
    }
}

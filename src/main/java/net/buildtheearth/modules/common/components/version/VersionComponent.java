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
        v1_12_R1("1.12"),
        v1_13_R1("1.13"),
        v1_13_R2("1.13.2"),
        v1_14_R1("1.14.4"),
        v1_15_R1("1.15.2"),
        v1_16_R1("1.16.1"),
        v1_16_R2("1.16.3"),
        v1_16_R3("1.16.5"),
        v1_17_R1("1.17.1"),
        v1_18_R1("1.18.1"),
        v1_18_R2("1.18.2"),
        v1_19_R1("1.19.2"),
        v1_19_R2("1.19.3"),
        v1_19_R3("1.19.4"),
        v1_20_R1("1.20.1"),
        v1_20_R2("1.20.2"),
        v1_20_R3("1.20.4");

        @Getter
        private final String bukkitVersion;

        Version(String bukkitVersion){
            this.bukkitVersion = bukkitVersion;
        }
    }
}

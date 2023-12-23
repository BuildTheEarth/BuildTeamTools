package net.buildtheearth.modules.network.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class BuildTeam {

    @Getter
    private final String ID;
    @Getter
    private final String IP;
    @Getter
    private final String serverName;
    @Getter
    private final boolean isConnected;
    @Getter
    private final boolean hasBTToolsInstalled;
    @Getter
    private final List<Region> regions;
    @Getter
    private final Continent continent;

    public BuildTeam(String ID, String serverIP, String serverName, Continent continent, boolean isConnected, boolean hasBTToolsInstalled) {
        this.ID = ID;
        this.serverName = serverName;
        this.continent = continent;
        this.isConnected = isConnected;
        this.hasBTToolsInstalled = hasBTToolsInstalled;

        this.regions = new ArrayList<>();

        if(!isConnected)
            this.IP = serverIP;
        else
            this.IP = null;
    }
}

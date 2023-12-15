package net.buildtheearth.modules.network.model;

public class Country {

    private boolean isConnected;
    private boolean hasBTToolsInstalled;
    private String IP;

    private final Continent continent;
    private final String name;

    private final String teamID;
    private final String serverName;

    public Country(Continent continent, String name, String teamID, String serverName, boolean isConnected, boolean hasBTToolsInstalled) {
        this.continent = continent;
        this.name = name;
        this.teamID = teamID;
        this.serverName = serverName;
        this.isConnected = isConnected;
        this.hasBTToolsInstalled = hasBTToolsInstalled;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public boolean isBTToolsInstalled() {
        return hasBTToolsInstalled;
    }

    public String getIP() {
        return IP;
    }

    public Continent getContinent() {
        return continent;
    }

    public String getName() {
        return name;
    }

    public String getTeamID() {
        return teamID;
    }

    public String getServerName() {
        return serverName;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }
}

package net.buildtheearth.modules.network.model;

public class Country {

    private boolean isConnected;
    private boolean hasBTToolsInstalled;
    private String IP;

    private final Continent continent;
    private final String name;

    private final String teamID;
    private final String serverName;

    private final String headBase64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWJkNTFmNDY5M2FmMTc0ZTZmZTE5NzkyMzNkMjNhNDBiYjk4NzM5OGUzODkxNjY1ZmFmZDJiYTU2N2I1YTUzYSJ9fX0=";

    public Country(Continent continent, String name, String teamID, String serverName, boolean isConnected, boolean hasBTToolsInstalled) {
        this.continent = continent;
        this.name = name;
        this.teamID = teamID;
        this.serverName = serverName;
        this.isConnected = isConnected;
        this.hasBTToolsInstalled = hasBTToolsInstalled;
    }

    public static Country getByName(String name) {
        Country lastCountry = null;
        for(Continent continent : Continent.values()) {
            for (Country country : continent.getCountries()) {
                lastCountry = country;
                if(country.getName().equals(name)) return country;
            }
        }
        return lastCountry;
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

    public String getHeadBase64() {
        return headBase64;
    }
}

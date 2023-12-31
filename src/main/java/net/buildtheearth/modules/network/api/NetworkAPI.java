package net.buildtheearth.modules.network.api;

import net.buildtheearth.Main;
import net.buildtheearth.modules.network.ProxyManager;
import net.buildtheearth.modules.network.model.BuildTeam;
import net.buildtheearth.modules.network.model.Continent;
import net.buildtheearth.modules.network.model.Region;
import net.buildtheearth.modules.network.model.RegionType;
import net.buildtheearth.modules.utils.APIUtil;
import net.buildtheearth.modules.utils.ChatHelper;
import net.buildtheearth.modules.utils.io.ConfigPaths;
import net.buildtheearth.modules.warp.model.Warp;
import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;


public class NetworkAPI {

    //TODO CHANGE PARAMETER TO serverName
    public static Collection<String> getCountryCodesByKey(String apiKey) {

        //TODO change url once functionality is added to API

        JSONObject jsonObject;
        try {
            URL url = new URL("https://nwapi.buildtheearth.net/api/teams/" + apiKey);
            jsonObject = APIUtil.createJSONObject(APIUtil.get(url));
        } catch (MalformedURLException e) {
            return null;
        }

        Bukkit.getLogger().info(jsonObject.toString());
        return null;
    }

    public static Warp getWarpByKey(String warpKey) {
        //TODO IMPLEMENT METHOD
        return null;
    }

    public static void removeWarpByLocation(double[] coordinates) {
        //TODO IMPLEMENT METHOD
    }


    // Add all currently connected regions to their respective continents
    public static void getBuildTeamInformation() {
        API.getAsync("https://nwapi.buildtheearth.net/api/teams", new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                JSONArray responseArray = APIUtil.createJSONArray(response);

                // Clear all regions from the continents
                for(Continent continent : Continent.values())
                    continent.getRegions().clear();

                // Clear all regions from the build teams
                for(BuildTeam buildTeam : Main.getBuildTeamTools().getProxyManager().getBuildTeams())
                    buildTeam.getRegions().clear();

                // Clear all build teams
                Main.getBuildTeamTools().getProxyManager().getBuildTeams().clear();

                // Clear all regions
                Main.getBuildTeamTools().getProxyManager().getRegions().clear();


                // Add all countries to their respective continents
                for(Object object : responseArray.toArray()) {

                    // Check if the object is a JSON object
                    if(!(object instanceof JSONObject)) return;
                    JSONObject teamObject = (JSONObject) object;

                    // Extract a JSON array containing the regions belonging to the team
                    Object regions = teamObject.get("Regions");
                    if(!(regions instanceof JSONArray)) return;
                    JSONArray regionArray = (JSONArray) regions;

                    // Get some values that will be needed in the for loop
                    Continent continent = Continent.getByLabel((String) teamObject.get("Continent"));
                    boolean isConnected = (boolean) teamObject.get("isConnectedToNetwork");
                    boolean hasBuildTeamToolsInstalled = (long) teamObject.get("hasBuildTeamToolsInstalled") == 1;
                    String mainServerIP = (String) teamObject.get("MainServerIP");
                    String teamID = (String) teamObject.get("ID");
                    String serverName = getMainServerName(teamObject);
                    String name = (String) teamObject.get("Name");
                    String blankName = (String) teamObject.get("BlankName");

                    BuildTeam buildTeam = new BuildTeam(teamID, mainServerIP, name, blankName, serverName, continent, isConnected, hasBuildTeamToolsInstalled);
                    Main.getBuildTeamTools().getProxyManager().getBuildTeams().add(buildTeam);

                    // Add all the regions of the team to their respective continents
                    for(Object regionJSON : regionArray.toArray()) {
                        if(!(regionJSON instanceof JSONObject)) return;
                        JSONObject regionObject = (JSONObject) regionJSON;

                        String regionName = (String) regionObject.get("RegionName");
                        String headBase64 = (String) regionObject.get("Head");
                        RegionType regionType = RegionType.getByLabel((String) regionObject.get("RegionType"));
                        int area = getArea(regionObject);

                        Region region = new Region(regionName, regionType, continent, buildTeam, headBase64, area);

                        continent.getRegions().add(region);
                        buildTeam.getRegions().add(region);
                        Main.getBuildTeamTools().getProxyManager().getRegions().add(region);
                    }
                }
            }



            private String getMainServerName(JSONObject teamObject) {
                String mainServerIP = (String) teamObject.get("MainServerIP");

                Object serversObject = teamObject.get("Servers");
                if(!(serversObject instanceof JSONArray)) return null;
                JSONArray serversArray = (JSONArray) serversObject;

                for(Object object : serversArray.toArray()) {
                    if(!(object instanceof JSONObject)) return null;
                    JSONObject serverObject = (JSONObject) object;

                    String serverIP = (String) serverObject.get("IP");
                    if(serverIP.equals(mainServerIP)) return (String) serverObject.get("Name");
                }
                return null;
            }

            private int getArea(JSONObject regionObject) {
                if(regionObject == null) return 0;
                if(regionObject.get("area") == null) return 0;

                if (regionObject.get("area") instanceof Long)
                    return ((Long) regionObject.get("area")).intValue();

                return ((Double) regionObject.get("area")).intValue();
            }

            @Override
            public void onFailure(IOException e) {
                ChatHelper.logError("Failed to get team & server information from the network API: %s", e);
            }
        });
    }

    public static void setupCurrentServerData() {
        API.getAsync("https://nwapi.buildtheearth.net/api/teams/" + Main.instance.getConfig().getString(ConfigPaths.API_KEY), new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                JSONObject teamObject = APIUtil.createJSONObject(response);

                String teamID = (String) teamObject.get("ID");

                ProxyManager proxyManager = Main.getBuildTeamTools().getProxyManager();
                BuildTeam buildTeam = proxyManager.getBuildTeamByID(teamID);
                proxyManager.setBuildTeam(buildTeam);
            }

            @Override
            public void onFailure(IOException e) {
                ChatHelper.logError("Failed to get team & server information from the network API: %s", e);
            }
        });
    }
}

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
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;

public class NetworkAPI {

    /**
     * Notifies the network API about the presence of the plugin
     * @param installed if the plugin is installed
     */
    public static void setBuildTeamToolsInstalled(boolean installed) {
        String apiKey = Main.instance.getConfig().getString(ConfigPaths.API_KEY);
        String requestBodyString =
                "[" +
                    "{" +
                        "\"hasBuildTeamToolsInstalled\":"+installed +
                    "}" +
                "]";
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), requestBodyString);

        API.putAsync("https://nwapi.buildtheearth.net/api/teams/" + apiKey + "/hasBuildTeamToolsInstalled", requestBody, new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                ChatHelper.logDebug("Notified the network API of the plugins presence: %s", response);
            }

            @Override
            public void onFailure(IOException e) {
                ChatHelper.logError("Something went wrong while notifying the network API of the plugins presence: %s", e.getMessage());
            }
        });
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


                        Region region;

                        if(regionType.equals(RegionType.COUNTRY)) {
                            int area = getArea(regionObject);
                            String regionCodeCca3 = (String) regionObject.get("RegionCode");
                            String regionCodeCca2 = (String) regionObject.get("cca2");

                            region = new Region(regionName, continent, buildTeam, headBase64, area, regionCodeCca2, regionCodeCca3);
                        }else
                            region = new Region(regionName, regionType, continent, buildTeam, headBase64);


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

    public static void createWarp(Warp warp) {
        String apiKey = Main.instance.getConfig().getString(ConfigPaths.API_KEY);
        String requestBodyString = "{" +
                "\"key\": \"" + warp.getKey() + "\"," +
                "\"countryCode\": \"" + warp.getCountryCode() + "\"," +
                "\"countryCodeType\": \"" + "cca2" + "\"," +
                "\"subRegion\": \"" + warp.getSubRegion() + "\"," +
                "\"city\": \"" + warp.getCity() + "\"," +
                "\"worldName\": \"" + warp.getWorldName() + "\"," +
                "\"lat\": " + warp.getLat() + "," +
                "\"lon\": " + warp.getLon() + "," +
                "\"y\": " + warp.getY() + "," +
                "\"yaw\": " + warp.getYaw() + "," +
                "\"pitch\": " + warp.getPitch() + "," +
                "\"isHighlight\": " + warp.isHighlight() +
                "}";
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), requestBodyString);


        API.postAsync("https://nwapi.buildtheearth.net/api/teams/"+apiKey+"/warps", requestBody, new API.ApiResponseCallback(){
            @Override
            public void onResponse(String response) {

            }

            @Override
            public void onFailure(IOException e) {

            }
        });
    }
}

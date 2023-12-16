package net.buildtheearth.modules.network.api;

import net.buildtheearth.Main;
import net.buildtheearth.modules.network.ProxyManager;
import net.buildtheearth.modules.network.model.Continent;
import net.buildtheearth.modules.network.model.Country;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;


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

    // 100% keep

    public static CompletableFuture<String> getCurrentServerNameAsync() {
        CompletableFuture<String> future = new CompletableFuture<>();

        String currentServerTeam = Main.getBuildTeamTools().getProxyManager().getBuildTeamID();
        API.getAsync("https://nwapi.buildtheearth.net/api/teams/" + currentServerTeam + "/servers", new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = APIUtil.createJSONArray(response);
                for (Object obj : jsonArray) {
                    if (!(obj instanceof JSONObject)) continue;
                    JSONObject serverObject = (JSONObject) obj;
                    String serverName = (String) serverObject.get("Name");
                    boolean isCurrentServer = Bukkit.getServer().getIp() == serverObject.get("IP");
                    if (!isCurrentServer) continue;
                    future.complete(serverName);
                    return;
                }
            }

            @Override
            public void onFailure(IOException e) {
                future.completeExceptionally(e);
                ChatHelper.logError("Failed to get the current server name: %s", e);
            }
        });

        return future;
    }

    public static CompletableFuture<String> getTeamIPByCountryAsync(String targetCountry) {
        CompletableFuture<String> future = new CompletableFuture<>();

        getTeamIdByCountryAsync(targetCountry).thenAcceptAsync(teamID -> {
            API.getAsync("https://nwapi.buildtheearth.net/api/teams/" + teamID + "/servers", new API.ApiResponseCallback() {
                @Override
                public void onResponse(String response) {
                    JSONArray jsonArray = APIUtil.createJSONArray(response);
                    for (Object obj : jsonArray) {
                        if (!(obj instanceof JSONObject)) continue;
                        JSONObject serverObject = (JSONObject) obj;

                        future.complete((String) serverObject.get("IP"));
                        return;
                    }
                }

                @Override
                public void onFailure(IOException e) {
                    future.completeExceptionally(e);
                    ChatHelper.logError("Failed to get Server IP by Country: %s.", targetCountry);
                }
            });
        });

        return future;
    }

    public static CompletableFuture<String> getServerNameByTeamId(String teamID) {
        CompletableFuture<String> future = new CompletableFuture<>();

        API.getAsync("https://nwapi.buildtheearth.net/api/teams/" + teamID + "/servers", new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray = APIUtil.createJSONArray(response);
                for (Object obj : jsonArray) {
                    if (!(obj instanceof JSONObject)) continue;
                    JSONObject serverObject = (JSONObject) obj;
                    String serverName = (String) serverObject.get("Name");
                    future.complete(serverName);
                    return;
                }
            }

            @Override
            public void onFailure(IOException e) {
                future.completeExceptionally(e);
                ChatHelper.logError("Failed to get Server Name by Team ID: %s.", teamID);
            }
        });

        return future;
    }

    public static CompletableFuture<String> getTeamIdByCountryAsync(String targetCountry) {
        CompletableFuture<String> future = new CompletableFuture<>();

        API.getAsync("https://nwapi.buildtheearth.net/api/teams/countries", new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonResponse = APIUtil.createJSONArray(response);

                for (Object obj : jsonResponse) {
                    if (obj instanceof JSONObject) {
                        JSONObject countryObject = (JSONObject) obj;
                        String countryName = (String) countryObject.get("RegionName");

                        if (countryName.equals(targetCountry)) {
                            future.complete((String) countryObject.get("BuildTeam"));
                        }
                    }
                }
            }

            @Override
            public void onFailure(IOException e) {
                future.completeExceptionally(e);
                ChatHelper.logError("Failed to get Team ID by country: %s.", targetCountry);
            }
        });

        return future;
    }

    //100% keep

    // Add all currently connected regions to their respective continents
    public static void getCountries() {
        API.getAsync("https://nwapi.buildtheearth.net/api/teams", new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                JSONArray responseArray = APIUtil.createJSONArray(response);

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
                    boolean hasBuildTeamToolsInstalled = (long) teamObject.get("hasBuildTeamToolsInstalled") == 1 ? true : false;
                    String mainServerIP = (String) teamObject.get("MainServerIP");

                    // Add all the regions of the team to their respective continents
                    for(Object region : regionArray.toArray()) {
                        if(!(region instanceof JSONObject)) return;
                        JSONObject regionObject = (JSONObject) region;

                        String regionName = (String) regionObject.get("RegionName");

                        String teamID = (String) teamObject.get("ID");
                        String serverName = getMainServerName(teamObject);

                        Country country = new Country(continent, regionName, teamID, serverName, isConnected, hasBuildTeamToolsInstalled);
                        if(!isConnected && hasBuildTeamToolsInstalled) country.setIP(mainServerIP);

                        String headBase64 = (String) regionObject.get("Head");
                        if(headBase64 != null) country.setHeadBase64(headBase64);
                        continent.getCountries().add(country);
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

                boolean isConnected = (boolean) teamObject.get("isConnectedToNetwork");
                String teamID = (String) teamObject.get("ID");
                String serverName = getMainServerName(teamObject);

                ProxyManager proxyManager = Main.getBuildTeamTools().getProxyManager();
                proxyManager.setServerName(serverName);
                proxyManager.setBuildTeamID(teamID);
                proxyManager.setConnected(isConnected);
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

            @Override
            public void onFailure(IOException e) {
                ChatHelper.logError("Failed to get team & server information from the network API: %s", e);
            }
        });
    }
}

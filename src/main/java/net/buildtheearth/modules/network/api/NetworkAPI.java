package net.buildtheearth.modules.network.api;

import net.buildtheearth.Main;
import net.buildtheearth.modules.utils.APIUtil;
import net.buildtheearth.modules.utils.ChatHelper;
import net.buildtheearth.modules.warp.model.Warp;
import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
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


    public static CompletableFuture<List<String>> getCountriesByActiveServers() {
        CompletableFuture<List<String>> future = new CompletableFuture<>();

        List<String> targetBuildTeams = new ArrayList<>();
        for(String serverName : Main.getBuildTeamTools().getProxyManager().getActiveServers()) {
            //TODO ADD TEAMS BELONGING TO SERVERNAME TO TARGET BUILDTEAMS
        }


        List<String> matchingCountries = new ArrayList<>();
        API.getAsync("https://nwapi.buildtheearth.net/api/teams/countries", new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonResponse = APIUtil.createJSONArray(response);

                for (Object obj : jsonResponse) {
                    if (obj instanceof JSONObject) {
                        JSONObject country = (JSONObject) obj;
                        String buildTeam = (String) country.get("BuildTeam");

                        // Check if the BuildTeam matches the target
                        if (targetBuildTeams.contains(buildTeam)) {
                            String regionName = (String) country.get("RegionName");
                            matchingCountries.add(regionName);
                        }
                    }
                }
                future.complete(matchingCountries);
            }

            @Override
            public void onFailure(IOException e) {
                future.completeExceptionally(e);
                Bukkit.getLogger().info(ChatHelper.highlight("Failed to get countries by active servers from the Network API."));
            }
        });

        return future;
    }
}

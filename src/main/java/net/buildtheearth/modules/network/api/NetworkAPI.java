package net.buildtheearth.modules.network.api;

import lombok.experimental.UtilityClass;
import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.navigation.NavUtils;
import net.buildtheearth.modules.navigation.components.warps.model.Warp;
import net.buildtheearth.modules.navigation.components.warps.model.WarpGroup;
import net.buildtheearth.modules.network.NetworkModule;
import net.buildtheearth.modules.network.model.BuildTeam;
import net.buildtheearth.modules.network.model.Continent;
import net.buildtheearth.modules.network.model.Region;
import net.buildtheearth.modules.network.model.RegionType;
import net.buildtheearth.utils.ChatHelper;
import net.buildtheearth.utils.io.ConfigPaths;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@UtilityClass
public class NetworkAPI {

    /**
     * Notifies the network API about the presence of the plugin
     * @param installed if the plugin is installed
     */
    public static void setBuildTeamToolsInstalled(boolean installed) {
        String apiKey = BuildTeamTools.getInstance().getConfig().getString(ConfigPaths.API_KEY);

        JSONObject requestBodyJson = new JSONObject();
        requestBodyJson.put("hasBuildTeamToolsInstalled", installed);
        String requestBodyString = requestBodyJson.toString();

        RequestBody requestBody = RequestBody.create(requestBodyString, MediaType.parse("application/json"));

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
    public static @NotNull CompletableFuture<Void> getBuildTeamInformation() {
        CompletableFuture<Void> future = new CompletableFuture<>();

        getAsync("https://nwapi.buildtheearth.net/api/teams", new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray responseArray = API.createJSONArray(response);

                    // Clear all regions from the continents
                    for (Continent continent : Continent.values())
                        continent.getRegions().clear();

                    // Clear all regions from the build teams
                    for (BuildTeam buildTeam : NetworkModule.getInstance().getBuildTeams())
                        buildTeam.getRegions().clear();

                    // Clear all build teams
                    NetworkModule.getInstance().getBuildTeams().clear();

                    // Clear all regions
                    NetworkModule.getInstance().getRegions().clear();


                    // Add all teams to the proxy manager
                    for (Object object : responseArray.toArray()) {

                        // Check if the object is a JSON object
                        if (!(object instanceof JSONObject teamObject) ||
                                !(teamObject.get("Regions") instanceof JSONArray regions) ||
                                !(teamObject.get("Warps") instanceof JSONArray warps) ||
                                !(teamObject.get("WarpGroups") instanceof JSONArray warpGroups)) {
                            continue;
                        }

                        // Get some values to add to the build team
                        Continent continent = Continent.getByLabel((String) teamObject.get("Continent"));
                        boolean isConnected = (boolean) teamObject.get("isConnectedToNetwork");
                        boolean hasBuildTeamToolsInstalled = (long) teamObject.get("hasBuildTeamToolsInstalled") == 1;
                        String mainServerIP = (String) teamObject.get("MainServerIP");
                        String teamID = (String) teamObject.get("ID");
                        String serverName = getMainServerName(teamObject);
                        String name = (String) teamObject.get("Name");
                        String blankName = (String) teamObject.get("BlankName");
                        boolean allowsTransfers = (long) teamObject.get("AllowsTransfers") == 1;
                        String tag = (String) teamObject.get("Tag");

                        BuildTeam buildTeam = new BuildTeam(teamID, mainServerIP, name, blankName, serverName,
                                isConnected, hasBuildTeamToolsInstalled, allowsTransfers, tag);
                        NetworkModule.getInstance().getBuildTeams().add(buildTeam);

                        WarpGroup otherWarpGroup = NavUtils.createOtherWarpGroup();

                        // Add all the warp groups of the team to their respective build teams
                        for (Object warpGroupJSON : warpGroups.toArray()) {
                            if (!(warpGroupJSON instanceof JSONObject)) continue;
                            JSONObject warpGroupObject = (JSONObject) warpGroupJSON;

                            UUID warpGroupID = UUID.fromString((String) warpGroupObject.get("ID"));
                            String warpGroupName = (String) warpGroupObject.get("Name");
                            String warpGroupDescription = (String) warpGroupObject.get("Description");
                            int warpSlot = (int) (long) warpGroupObject.get("Slot");
                            String warpMaterial = (String) warpGroupObject.get("Material");

                            WarpGroup warpGroup = new WarpGroup(warpGroupID, buildTeam, warpGroupName, warpGroupDescription, warpSlot, warpMaterial);

                            buildTeam.getWarpGroups().add(warpGroup);
                        }

                        if (!otherWarpGroup.getWarps().isEmpty()) buildTeam.getWarpGroups().add(otherWarpGroup);

                        // Add all the warps of the team to their respective warp groups
                        for (Object warpJSON : warps.toArray()) {
                            if (!(warpJSON instanceof JSONObject)) continue;
                            JSONObject warpObject = (JSONObject) warpJSON;

                            String warpIDString = (String) warpObject.get("ID");
                            UUID warpID = warpIDString != null ? UUID.fromString(warpIDString) : null;
                            String warpGroupIDString = (String) warpObject.get("WarpGroup");
                            UUID warpGroupID = warpGroupIDString != null ? UUID.fromString(warpGroupIDString) : null;
                            String warpName = (String) warpObject.get("Name");
                            String countryCode = (String) warpObject.get("CountryCode");
                            String address = (String) warpObject.get("Address");
                            Warp.AddressType addressType = Warp.AddressType.fromValue((String) warpObject.get("AddressType"));
                            String material = (String) warpObject.get("Material");
                            String warpWorldName = (String) warpObject.get("WorldName");
                            double warpLat = (double) warpObject.get("Latitude");
                            double warpLon = (double) warpObject.get("Longitude");
                            int warpHeight = (int) (long) warpObject.get("Height");
                            float warpYaw = Float.parseFloat(warpObject.get("Yaw") + "");
                            float warpPitch = Float.parseFloat(warpObject.get("Pitch") + "");
                            boolean isHighlight = warpObject.get("isHighlight") != null && (long) warpObject.get("isHighlight") == 1;

                            if (material != null && material.isEmpty())
                                material = null;

                            WarpGroup warpGroup = null;

                            // If the warp group ID is not null, get the warp group with that ID
                            if (warpGroupID != null)
                                warpGroup = buildTeam.getWarpGroups().stream()
                                        .filter(warpGroup1 -> warpGroup1 != null && warpGroup1.getId() != null && warpGroup1.getId().equals(warpGroupID)).findFirst().orElse(null);

                            // If the warp group is null, set it to the "other" warp group
                            if (warpGroup == null)
                                warpGroup = otherWarpGroup;

                            Warp warp = new Warp(warpID, warpGroup, warpName, countryCode, "cca3", address, addressType, material, warpWorldName, warpLat, warpLon, warpHeight, warpYaw, warpPitch, isHighlight);

                            // If the warp belongs to a warp group, add it to that, otherwise add it to the "other" warp group.
                            if (warpGroupID == null) {
                                otherWarpGroup.getWarps().add(warp);
                            } else {
                                boolean added = false;

                                for (WarpGroup wg : buildTeam.getWarpGroups())
                                    if (wg.getId().equals(warpGroupID)) {
                                        wg.getWarps().add(warp);
                                        added = true;
                                        break;
                                    }

                                if (!added)
                                    otherWarpGroup.getWarps().add(warp);
                            }
                        }

                        // Add all the regions of the team to their respective continents
                        for (Object regionJSON : regions.toArray()) {
                            if (!(regionJSON instanceof JSONObject)) continue;
                            JSONObject regionObject = (JSONObject) regionJSON;

                            String regionName = (String) regionObject.get("RegionName");
                            String headBase64 = (String) regionObject.get("Head");
                            RegionType regionType = RegionType.getByLabel((String) regionObject.get("RegionType"));


                            Region region;

                            if (regionType.equals(RegionType.COUNTRY)) {
                                int area = getArea(regionObject);
                                String regionCodeCca3 = (String) regionObject.get("RegionCode");
                                String regionCodeCca2 = (String) regionObject.get("cca2");

                                region = new Region(regionName, continent, buildTeam, headBase64, area, regionCodeCca2, regionCodeCca3);
                            } else
                                region = new Region(regionName, regionType, continent, buildTeam, headBase64);


                            continent.getRegions().add(region);
                            buildTeam.getRegions().add(region);
                            NetworkModule.getInstance().getRegions().add(region);
                        }
                    }
                }catch (Exception e) {
                    future.completeExceptionally(e);
                    return;
                }

                future.complete(null);
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

                if (regionObject.get("area") instanceof Long area)
                    return area.intValue();

                return ((Double) regionObject.get("area")).intValue();
            }

            @Override
            public void onFailure(IOException e) {
                ChatHelper.logError("Failed to get teams information from the network API: %s", e);

                // Handle failure scenario
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    public static CompletableFuture<Void> setupCurrentServerData() {
        CompletableFuture<Void> future = new CompletableFuture<>();

        API.getAsync("https://nwapi.buildtheearth.net/api/teams/" + BuildTeamTools.getInstance().getConfig().getString(ConfigPaths.API_KEY), new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject teamObject = API.createJSONObject(response);

                    String teamID = (String) teamObject.get("ID");

                    NetworkModule networkModule = NetworkModule.getInstance();
                    BuildTeam buildTeam = networkModule.getBuildTeamByID(teamID);
                    networkModule.setBuildTeam(buildTeam);
                } catch (Exception e) {
                    future.completeExceptionally(e);
                    return;
                }

                future.complete(null);
            }

            @Override
            public void onFailure(IOException e) {
                ChatHelper.logError("Failed to get team & server information from the network API: %s", e);

                future.completeExceptionally(e);
            }
        });

        return future;
    }

    public static void createWarp(Warp warp, API.ApiResponseCallback callback) {
        String apiKey = BuildTeamTools.getInstance().getConfig().getString(ConfigPaths.API_KEY);

        RequestBody requestBody = RequestBody.create(warp.toJSON().toString(), MediaType.parse("application/json"));
        API.postAsync("https://nwapi.buildtheearth.net/api/teams/"+apiKey+"/warps", requestBody, callback);
    }

    public static void createWarpGroup(WarpGroup warpGroup, API.ApiResponseCallback callback) {
        String apiKey = BuildTeamTools.getInstance().getConfig().getString(ConfigPaths.API_KEY);

        RequestBody requestBody = RequestBody.create(warpGroup.toJSON().toString(), MediaType.parse("application/json"));
        API.postAsync("https://nwapi.buildtheearth.net/api/teams/"+apiKey+"/warpgroups", requestBody, callback);
    }

    public static void updateWarp(Warp warp, API.ApiResponseCallback callback) {
        String apiKey = BuildTeamTools.getInstance().getConfig().getString(ConfigPaths.API_KEY);

        RequestBody requestBody = RequestBody.create(warp.toJSON().toString(), MediaType.parse("application/json"));
        API.putAsync("https://nwapi.buildtheearth.net/api/teams/"+apiKey+"/warps", requestBody, callback);
    }

    public static void updateWarpGroup(WarpGroup warpGroup, API.ApiResponseCallback callback) {
        String apiKey = BuildTeamTools.getInstance().getConfig().getString(ConfigPaths.API_KEY);

        RequestBody requestBody = RequestBody.create(warpGroup.toJSON().toString(), MediaType.parse("application/json"));
        API.putAsync("https://nwapi.buildtheearth.net/api/teams/"+apiKey+"/warpgroups", requestBody, callback);
    }

    public static void deleteWarp(Warp warp, API.ApiResponseCallback callback) {
        String apiKey = BuildTeamTools.getInstance().getConfig().getString(ConfigPaths.API_KEY);

        JSONObject requestBodyJson = new JSONObject();
        requestBodyJson.put("key", warp.getId().toString());
        String requestBodyString = requestBodyJson.toString();

        RequestBody requestBody = RequestBody.create(requestBodyString, MediaType.parse("application/json"));

        API.deleteAsync("https://nwapi.buildtheearth.net/api/teams/"+apiKey+"/warps", requestBody, callback);
    }

    public static void deleteWarpGroup(WarpGroup warpGroup, API.ApiResponseCallback callback) {
        String apiKey = BuildTeamTools.getInstance().getConfig().getString(ConfigPaths.API_KEY);

        JSONObject requestBodyJson = new JSONObject();
        requestBodyJson.put("key", warpGroup.getId().toString());
        String requestBodyString = requestBodyJson.toString();
        RequestBody requestBody = RequestBody.create(requestBodyString, MediaType.parse("application/json"));

        API.deleteAsync("https://nwapi.buildtheearth.net/api/teams/"+apiKey+"/warpgroups", requestBody, callback);
    }

    public static void syncPlayerList() {
        if(NetworkModule.getInstance().getBuildTeam().isConnected())
            return;

        String apiKey = BuildTeamTools.getInstance().getConfig().getString(ConfigPaths.API_KEY);

        JSONArray requestBodyArray = new JSONArray();
        for (Player player : Bukkit.getOnlinePlayers())
            requestBodyArray.add(new String[]{player.getUniqueId().toString(), player.getName()});

        RequestBody requestBody = RequestBody.create(requestBodyArray.toString(), MediaType.parse("application/json"));

        API.postAsync("https://nwapi.buildtheearth.net/api/teams/"+apiKey+"/playerlist", requestBody, new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                ChatHelper.logDebug("Synced the player list with the network API: %s", response);
            }

            @Override
            public void onFailure(IOException e) {
                ChatHelper.getErrorString("Failed to sync the player list with the network API: %s", e);
            }
        });
    }

    public static void getAsync(String url, API.ApiResponseCallback callback) {
        var path = BuildTeamTools.getInstance().getDataPath().resolve("buildteams.json");
        if (BuildTeamTools.getInstance().isDebug() && path.toFile().exists()) {
            // If the file exists, read from it instead of making an API call
            try {
                String content = new String(java.nio.file.Files.readAllBytes(path));
                callback.onResponse(content);
                return;
            } catch (IOException e) {
                ChatHelper.logError("Failed to read from local buildteams.json: %s", e.getMessage());
            }
        }

        API.getAsync(url, callback);
    }
}

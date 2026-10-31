package net.buildtheearth.buildteamtools.modules.network.api;

import com.alpsbte.alpslib.utils.ChatHelper;
import lombok.experimental.UtilityClass;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.navigation.NavUtils;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.Warp;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.model.WarpGroup;
import net.buildtheearth.buildteamtools.modules.network.NetworkModule;
import net.buildtheearth.buildteamtools.modules.network.model.BuildTeam;
import net.buildtheearth.buildteamtools.modules.network.model.Continent;
import net.buildtheearth.buildteamtools.modules.network.model.Region;
import net.buildtheearth.buildteamtools.modules.network.model.RegionType;
import net.buildtheearth.model.GeographicalCoordinate;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@UtilityClass
public class NetworkAPI {

    /**
     * Notifies the network API about the presence of the plugin
     *
     * @param installed if the plugin is installed
     */
    public static void setBuildTeamToolsInstalled(boolean installed) {
        String apiKey = BuildTeamTools.getInstance().getMainConfig().apiKey();

        JSONObject requestBodyJson = new JSONObject();
        requestBodyJson.put("hasBuildTeamToolsInstalled", installed);

        RequestBody requestBody = RequestBody.create(
                requestBodyJson.toString(),
                MediaType.parse("application/json")
        );

        API.putAsync("https://nwapi.buildtheearth.net/api/teams/" + apiKey + "/hasBuildTeamToolsInstalled", requestBody, new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                ChatHelper.logDebug("Notified the network API of the plugins presence: %s", response);
            }

            @Override
            public void onFailure(IOException e) {
                ChatHelper.logError("Something went wrong while notifying the network API of the plugins presence: " + e.getMessage());
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
                    for (Object object : responseArray) {

                        // Check if the object is a JSON object
                        if (!(object instanceof JSONObject teamObject)) {
                            continue;
                        }
                        JSONArray regions = teamObject.optJSONArray("Regions");
                        JSONArray warps = teamObject.optJSONArray("Warps");
                        JSONArray warpGroups = teamObject.optJSONArray("WarpGroups");
                        if (regions == null || warps == null || warpGroups == null) {
                            continue;
                        }

                        // Get some values to add to the build team
                        Continent continent;
                        boolean isConnected = teamObject.optBoolean("isConnectedToNetwork");
                        boolean hasBuildTeamToolsInstalled = asLong(teamObject.opt("hasBuildTeamToolsInstalled")) == 1;
                        String mainServerIP = teamObject.optString("MainServerIP", null);
                        String teamID = teamObject.optString("ID", null);
                        MainServerResolver.Resolution mainServerResolution = getMainServerResolution(teamObject);
                        String serverName = mainServerResolution.serverName();
                        String name = teamObject.optString("Name", null);
                        String blankName = teamObject.optString("BlankName", null);
                        boolean allowsTransfers = asLong(teamObject.opt("AllowsTransfers")) == 1;
                        String tag = teamObject.optString("Tag", null);

                        if (isConnected && mainServerIP != null && serverName == null) {
                            BuildTeamTools.getInstance().getLogger().warning(String.format(
                                    "Connected build team has an inconsistent network server mapping: ID=%s, tag=%s, mainServerIP=%s, registeredServerIPs=%s",
                                    teamID, tag, mainServerIP, mainServerResolution.registeredServerIPs()));
                        }

                        BuildTeam buildTeam = new BuildTeam(teamID, mainServerIP, name, blankName, serverName,
                                isConnected, hasBuildTeamToolsInstalled, allowsTransfers, tag);
                        NetworkModule.getInstance().getBuildTeams().add(buildTeam);

                        WarpGroup otherWarpGroup = NavUtils.createOtherWarpGroup(buildTeam);

                        // Add all the warp groups of the team to their respective build teams
                        for (Object warpGroupJSON : warpGroups) {
                            if (!(warpGroupJSON instanceof JSONObject warpGroupObject)) continue;

                            UUID warpGroupID = UUID.fromString(warpGroupObject.getString("ID"));
                            String warpGroupName = warpGroupObject.optString("Name", null);
                            String warpGroupDescription = warpGroupObject.optString("Description", null);
                            int warpSlot = (int) asLong(warpGroupObject.opt("Slot"));
                            String warpMaterial = warpGroupObject.optString("Material", null);

                            WarpGroup warpGroup = new WarpGroup(warpGroupID, buildTeam, warpGroupName, warpGroupDescription, warpSlot, warpMaterial);

                            buildTeam.getWarpGroups().add(warpGroup);
                        }

                        // Add all the warps of the team to their respective warp groups
                        for (Object warpJSON : warps) {
                            if (!(warpJSON instanceof JSONObject warpObject)) continue;

                            String warpIDString = warpObject.optString("ID", null);
                            UUID warpID = warpIDString != null && !warpIDString.isEmpty() ? UUID.fromString(warpIDString) : null;
                            String warpGroupIDString = warpObject.optString("WarpGroup", null);
                            UUID warpGroupID = warpGroupIDString != null && !warpGroupIDString.isEmpty()
                                    ? UUID.fromString(warpGroupIDString) : null;
                            String warpName = warpObject.optString("Name", null);
                            String countryCode = warpObject.optString("CountryCode", null);
                            String address = warpObject.optString("Address", null);
                            Warp.AddressType addressType = Warp.AddressType.fromValue(warpObject.optString("AddressType", null));
                            String material = warpObject.optString("Material", null);
                            String warpWorldName = warpObject.optString("WorldName", null);
                            double warpLat = warpObject.optDouble("Latitude");
                            double warpLon = warpObject.optDouble("Longitude");
                            int warpHeight = (int) asLong(warpObject.opt("Height"));
                            float warpYaw = Float.parseFloat(String.valueOf(warpObject.opt("Yaw")));
                            float warpPitch = Float.parseFloat(String.valueOf(warpObject.opt("Pitch")));
                            boolean isHighlight = warpObject.has("isHighlight") && asLong(warpObject.opt("isHighlight")) == 1;

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

                            Warp warp = new Warp(warpID, warpGroup, warpName, countryCode, "cca3", address, addressType,
                                    material, warpWorldName, new GeographicalCoordinate(warpLat, warpLon), warpHeight, warpYaw,
                                    warpPitch, isHighlight);

                            // If the warp belongs to a warp group, add it to that, otherwise add it to the "other" warp group.
                            boolean added = false;

                            for (WarpGroup wg : buildTeam.getWarpGroups())
                                if (wg.getId().equals(warpGroup.getId())) {
                                    wg.getWarps().add(warp);
                                    added = true;
                                    break;
                                }

                            if (!added)
                                otherWarpGroup.getWarps().add(warp);
                        }

                        if (!otherWarpGroup.getWarps().isEmpty()) buildTeam.getWarpGroups().add(otherWarpGroup);

                        // Add all the regions of the team to their respective continents
                        for (Object regionJSON : regions) {
                            if (!(regionJSON instanceof JSONObject regionObject)) continue;

                            String regionName = regionObject.optString("RegionName", null);
                            String headBase64 = regionObject.optString("Head", null);
                            RegionType regionType = RegionType.getByLabel(regionObject.optString("RegionType", null));


                            Region region;

                            if (regionType.equals(RegionType.COUNTRY)) {
                                int area = getArea(regionObject);
                                String regionCodeCca3 = regionObject.optString("RegionCode", null);
                                String regionCodeCca2 = regionObject.optString("cca2", null);
                                continent = Continent.getByLabel(regionObject.optString("region", null));
                                if (continent == Continent.OTHER)
                                    continent = Continent.getByLabel(regionObject.optString("subregion", null));

                                region = new Region(regionName, continent, buildTeam, headBase64, area, regionCodeCca2, regionCodeCca3);
                            } else {
                                continent = Continent.getByLabel(teamObject.optString("Continent", null));
                                region = new Region(regionName, regionType, continent, buildTeam, headBase64);
                            }


                            continent.getRegions().add(region);
                            buildTeam.getRegions().add(region);
                            NetworkModule.getInstance().getRegions().add(region);
                        }
                    }
                } catch (Exception e) {
                    future.completeExceptionally(e);
                    return;
                }

                future.complete(null);
            }


            private MainServerResolver.Resolution getMainServerResolution(@NonNull JSONObject teamObject) {
                String mainServerIP = teamObject.optString("MainServerIP", null);

                JSONArray serversArray = teamObject.optJSONArray("Servers");
                if (serversArray == null) {
                    return MainServerResolver.resolve(mainServerIP, List.of());
                }

                List<MainServerResolver.ServerEntry> servers = new ArrayList<>();

                for (Object object : serversArray) {
                    if (!(object instanceof JSONObject serverObject)) continue;

                    Object serverIP = serverObject.opt("IP");
                    Object serverName = serverObject.opt("Name");
                    servers.add(new MainServerResolver.ServerEntry(
                            serverName instanceof String ? (String) serverName : null,
                            serverIP instanceof String ? (String) serverIP : null));
                }

                return MainServerResolver.resolve(mainServerIP, servers);
            }

            private int getArea(JSONObject regionObject) {
                if (regionObject == null || !regionObject.has("area")) {
                    return 0;
                }

                Object area = regionObject.opt("area");
                if (area instanceof Number number) {
                    return number.intValue();
                }
                return 0;
            }

            @Override
            public void onFailure(IOException e) {
                ChatHelper.logError("Failed to get teams information from the network API: " + e.getMessage());

                // Handle failure scenario
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    public static @NotNull CompletableFuture<Void> setupCurrentServerData() {
        CompletableFuture<Void> future = new CompletableFuture<>();

        getOneAsync("https://nwapi.buildtheearth.net/api/teams/" + BuildTeamTools.getInstance().getMainConfig().apiKey(), new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject teamObject = API.createJSONObject(response);

                    String teamID = teamObject.getString("ID");

                    NetworkModule networkModule = NetworkModule.getInstance();
                    BuildTeam buildTeam = networkModule.getBuildTeamByID(teamID);
                    networkModule.setBuildTeam(buildTeam);
                    validateBuildTeamConnectionState(buildTeam);
                    if (!buildTeam.isHasBTToolsInstalled()) setBuildTeamToolsInstalled(true);
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

    /**
     * Ensures the BuildTeam connection state is consistent with the server's
     * proxy configuration. On versions prior to 1.21.5, proxy detection is
     * unavailable and the state is left unchanged.
     */
    private static void validateBuildTeamConnectionState(@NonNull BuildTeam buildTeam) {
        try {
            if (buildTeam.isConnected() && !Bukkit.getServerConfig().isProxyEnabled()) {
                buildTeam.setConnected(false);
                BuildTeamTools.getInstance().getComponentLogger().warn("The server is configured as not being a proxy, but the " +
                        "network API indicates that the build team is connected to the Network.");
            }
        } catch (NoSuchMethodError e) { /* it's fine - we assume proxy is enabled. This Method only exists in 1.21.5+ */}
    }

    public static void createWarp(Warp warp, API.ApiResponseCallback callback) {
        String apiKey = BuildTeamTools.getInstance().getMainConfig().apiKey();

        RequestBody requestBody = RequestBody.create(warp.toJSON().toString(), MediaType.parse("application/json"));
        API.postAsync("https://nwapi.buildtheearth.net/api/teams/" + apiKey + "/warps", requestBody, callback);
    }

    public static void createWarpGroup(WarpGroup warpGroup, API.ApiResponseCallback callback) {
        String apiKey = BuildTeamTools.getInstance().getMainConfig().apiKey();

        RequestBody requestBody = RequestBody.create(warpGroup.toJSON().toString(), MediaType.parse("application/json"));
        API.postAsync("https://nwapi.buildtheearth.net/api/teams/" + apiKey + "/warpgroups", requestBody, callback);
    }

    public static void updateWarp(Warp warp, API.ApiResponseCallback callback) {
        String apiKey = BuildTeamTools.getInstance().getMainConfig().apiKey();

        RequestBody requestBody = RequestBody.create(warp.toJSON().toString(), MediaType.parse("application/json"));
        API.putAsync("https://nwapi.buildtheearth.net/api/teams/" + apiKey + "/warps", requestBody, callback);
    }

    public static void updateWarpGroup(WarpGroup warpGroup, API.ApiResponseCallback callback) {
        String apiKey = BuildTeamTools.getInstance().getMainConfig().apiKey();

        RequestBody requestBody = RequestBody.create(warpGroup.toJSON().toString(), MediaType.parse("application/json"));
        API.putAsync("https://nwapi.buildtheearth.net/api/teams/" + apiKey + "/warpgroups", requestBody, callback);
    }

    public static void deleteWarp(Warp warp, API.ApiResponseCallback callback) {
        String apiKey = BuildTeamTools.getInstance().getMainConfig().apiKey();

        JSONObject requestBodyJson = new JSONObject();
        requestBodyJson.put("key", warp.getId().toString());

        RequestBody requestBody = RequestBody.create(requestBodyJson.toString(), MediaType.parse("application/json"));

        API.deleteAsync("https://nwapi.buildtheearth.net/api/teams/" + apiKey + "/warps", requestBody, callback);
    }

    public static void deleteWarpGroup(WarpGroup warpGroup, API.ApiResponseCallback callback) {
        String apiKey = BuildTeamTools.getInstance().getMainConfig().apiKey();

        JSONObject requestBodyJson = new JSONObject();
        requestBodyJson.put("key", warpGroup.getId().toString());
        RequestBody requestBody = RequestBody.create(requestBodyJson.toString(), MediaType.parse("application/json"));

        API.deleteAsync("https://nwapi.buildtheearth.net/api/teams/" + apiKey + "/warpgroups", requestBody, callback);
    }

    public static void syncPlayerList() {
        BuildTeam buildTeam = NetworkModule.getInstance().getBuildTeam();
        if (buildTeam != null && buildTeam.isConnected())
            return;

        String apiKey = BuildTeamTools.getInstance().getMainConfig().apiKey();

        JSONArray players = new JSONArray();

        for (Player player : Bukkit.getOnlinePlayers()) {
            JSONArray playerEntry = new JSONArray();
            playerEntry.put(player.getUniqueId().toString());
            playerEntry.put(player.getName());
            players.put(playerEntry);
        }

        RequestBody requestBody = RequestBody.create(
                players.toString(),
                MediaType.parse("application/json")
        );

        API.postAsync("https://nwapi.buildtheearth.net/api/teams/" + apiKey + "/playerlist", requestBody, new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                ChatHelper.logDebug("Synced the player list with the network API: %s", response);
            }

            @Override
            public void onFailure(IOException e) {
                ChatHelper.logError("Failed to sync the player list with the network API: %s", e.getMessage());
            }
        });
    }

    private static void getOneAsync(String url, API.ApiResponseCallback callback) {
        var path = BuildTeamTools.getInstance().getDataPath().resolve("buildteam.json");
        if (BuildTeamTools.getInstance().isDebug() && path.toFile().exists()) {
            // If the file exists, read from it instead of making an API call
            try {
                String content = new String(java.nio.file.Files.readAllBytes(path));
                callback.onResponse(content);
                BuildTeamTools.getInstance().getComponentLogger().warn("[DEBUG] Read connected buildteam from local file buildteam.json. Remove the file if you want up to date data.");
                return;
            } catch (IOException e) {
                ChatHelper.logError("Failed to read from local buildteam.json: %s", e.getMessage());
            }
        }

        API.getAsync(url, callback);
    }

    private static void getAsync(String url, API.ApiResponseCallback callback) {
        var path = BuildTeamTools.getInstance().getDataPath().resolve("buildteams.json");
        if (BuildTeamTools.getInstance().isDebug() && path.toFile().exists()) {
            // If the file exists, read from it instead of making an API call
            try {
                String content = new String(java.nio.file.Files.readAllBytes(path));
                callback.onResponse(content);
                BuildTeamTools.getInstance().getComponentLogger().warn("[DEBUG] Read buildteams from local file buildteams.json. Remove the file if you want up to date data.");
                return;
            } catch (IOException e) {
                ChatHelper.logError("Failed to read from local buildteams.json: %s", e.getMessage());
            }
        }

        API.getAsync(url, callback);
    }

    private static long asLong(Object value) {
        return value instanceof Number number ? number.longValue() : 0L;
    }
}

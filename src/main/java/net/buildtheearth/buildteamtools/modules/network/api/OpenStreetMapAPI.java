package net.buildtheearth.buildteamtools.modules.network.api;

import com.alpsbte.alpslib.geo.AdminLevel;
import com.alpsbte.alpslib.utils.ChatHelper;
import net.buildtheearth.model.GeographicalCoordinate;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.navigation.NavigationModule;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class OpenStreetMapAPI extends API {

    /**
     * @param coordinates The latitude & longitude coordinates to get the country, region & city/town from
     * @return The country name and country code belonging to this location
     */
    public static @NotNull CompletableFuture<String[]> getCountryFromLocationAsync(@NotNull GeographicalCoordinate coordinates) {
        CompletableFuture<String[]> future = new CompletableFuture<>();

        if (NavigationModule.getInstance().isEnabled() && NavigationModule.getInstance().getRgcHandler() != null) {
            ChatHelper.logDebug("Using custom file API to get country from location: %s, %s", coordinates.latitude(), coordinates.longitude());

            // If we are not on main thread, schedule lookup on main thread and complete the outer future there
            if (!Bukkit.isPrimaryThread()) {
                ChatHelper.logDebug("Not on main thread: scheduling RGC lookup on main thread...");
                Bukkit.getScheduler().runTask(BuildTeamTools.getInstance(), () -> {
                    try {
                        var rgcGeoLocation = NavigationModule.getInstance().getRgcHandler()
                                .locationFromCoordinates((float) coordinates.latitude(), (float) coordinates.longitude());
                        ChatHelper.logDebug("RGC lookup successful: %s", rgcGeoLocation);
                        future.complete(new String[]{rgcGeoLocation.get(AdminLevel.COUNTRY), ""});
                    } catch (Exception ex) {
                        future.completeExceptionally(ex);
                    }
                });
                return future;
            }

            // We're on the main thread already — run synchronously
            try {
                var location = Objects.requireNonNull(NavigationModule.getInstance().getRgcHandler()).locationFromCoordinates((float) coordinates.latitude(), (float) coordinates.longitude());
                ChatHelper.logDebug("RGC lookup successful: %s", location);
                future.complete(new String[]{location.get(AdminLevel.COUNTRY), ""});
            } catch (Exception ex) {
                future.completeExceptionally(ex);
            }
            return future;
        }

        String url = "https://photon.komoot.io/reverse?lat=" + coordinates.latitude() + "&lon=" + coordinates.longitude() + "&lang=en";

        ChatHelper.logDebug("Requesting country from location: %s", url);

        API.getAsync(url, new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = API.createJSONObject(response);

                ChatHelper.logDebug("Response from OpenStreetMap: %s", jsonObject);

                JSONArray featuresArray = (JSONArray) jsonObject.get("features");

                if (featuresArray == null || featuresArray.isEmpty()) {
                    future.completeExceptionally(new IllegalStateException("No location data found for these coordinates. The " +
                            "location may be in the ocean or outside mapped areas."));
                    return;
                }

                JSONObject featuresObject = (JSONObject) featuresArray.getFirst();

                JSONObject propertiesObject = (JSONObject) featuresObject.get("properties");

                String countryCodeCca2 = (String) propertiesObject.get("countrycode");
                String countryName = (String) propertiesObject.get("country");

                future.complete(new String[]{countryName, countryCodeCca2});
            }

            @Override
            public void onFailure(IOException e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }
}

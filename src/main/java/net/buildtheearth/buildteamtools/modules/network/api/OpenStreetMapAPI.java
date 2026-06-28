package net.buildtheearth.buildteamtools.modules.network.api;

import com.alpsbte.alpslib.geo.AdminLevel;
import com.alpsbte.alpslib.utils.ChatHelper;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.navigation.NavigationModule;
import net.buildtheearth.model.GeographicalCoordinate;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class OpenStreetMapAPI extends API {

    /**
     * @param coordinate The latitude & longitude coordinates to get the country, region & city/town from
     * @param forcePhoton If true, the photon API will be used instead of the RGC API. This is useful when the RGC API is not
     *                    working properly.
     * @return The country name and country code belonging to this location
     */
    public static @NotNull CompletableFuture<RegionLookupResult> getCountryFromLocationAsync(@NotNull GeographicalCoordinate coordinate, boolean forcePhoton) {
        if (!forcePhoton && canUseRgcHandler()) {
            return getCountryFromRgcAsync(coordinate);
        }
        return getCountryFromPhotonAsync(coordinate);
    }

    private static boolean canUseRgcHandler() {
        return NavigationModule.getInstance().isEnabled()
                && NavigationModule.getInstance().getRgcHandler() != null;
    }

    private static @NotNull CompletableFuture<RegionLookupResult> getCountryFromRgcAsync(@NotNull GeographicalCoordinate coordinates) {
        CompletableFuture<RegionLookupResult> future = new CompletableFuture<>();
        ChatHelper.logDebug("Using custom file API to get country from location: %s, %s", coordinates.latitude(), coordinates.longitude());

        if (!Bukkit.isPrimaryThread()) {
            ChatHelper.logDebug("Not on main thread: scheduling RGC lookup on main thread...");
            Bukkit.getScheduler().runTask(BuildTeamTools.getInstance(), () -> completeRgcLookup(coordinates, future));
            return future;
        }

        completeRgcLookup(coordinates, future);
        return future;
    }

    private static void completeRgcLookup(@NotNull GeographicalCoordinate coordinates,
                                          CompletableFuture<RegionLookupResult> future) {
        try {
            if (NavigationModule.getInstance().getRgcHandler() == null) throw new AssertionError("RgcHandler have to be initialized first");
            var location = NavigationModule.getInstance().getRgcHandler()
                    .locationFromCoordinates((float) coordinates.latitude(), (float) coordinates.longitude());
            ChatHelper.logDebug("RGC lookup successful: %s", location);
            future.complete(new RegionLookupResult(location.get(AdminLevel.COUNTRY), null));
        } catch (Exception ex) {
            future.completeExceptionally(ex);
        }
    }

    private static @NotNull CompletableFuture<RegionLookupResult> getCountryFromPhotonAsync(@NotNull GeographicalCoordinate coordinates) {
        CompletableFuture<RegionLookupResult> future = new CompletableFuture<>();
        String url = "https://photon.komoot.io/reverse?lat=" + coordinates.latitude() + "&lon=" + coordinates.longitude() + "&lang=en";

        ChatHelper.logDebug("Requesting country from location: %s", url);

        API.getAsync(url, new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                completePhotonLookup(response, future);
            }

            @Override
            public void onFailure(IOException e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    private static void completePhotonLookup(String response, @NonNull CompletableFuture<RegionLookupResult> future) {
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

        future.complete(new RegionLookupResult(countryName, countryCodeCca2.toUpperCase(Locale.ROOT)));
    }

}

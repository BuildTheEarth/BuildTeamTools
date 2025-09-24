package net.buildtheearth.modules.network.api;

import net.buildtheearth.utils.ChatHelper;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class OpenStreetMapAPI extends API {

    /**
     * @param coordinates The latitude & longitude coordinates to get the country, region & city/town from
     * @return The country name and country code belonging to this location
     */
    public static @NotNull CompletableFuture<String[]> getCountryFromLocationAsync(double @NotNull [] coordinates) {
        CompletableFuture<String[]> future = new CompletableFuture<>();
        String url = "https://photon.komoot.io/reverse?lon=" + coordinates[0] + "&lat=" + coordinates[1] + "&lang=en";

        ChatHelper.logDebug("Requesting country from location: %s", url);

        API.getAsync(url, new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = API.createJSONObject(response);

                ChatHelper.logDebug("Response from OpenStreetMap: %s", jsonObject);

                JSONObject featuresObject = (JSONObject) ((JSONArray) jsonObject.get("features")).get(0);

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

package net.buildtheearth.modules.network.api;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class OpenStreetMapAPI extends API {

    /**
     * @param coordinates The latitude & longitude coordinates to get the country, region & city/town from
     * @return The country name and country code belonging to this location
     */
    public static CompletableFuture<String[]> getCountryFromLocationAsync(double[] coordinates) {
        CompletableFuture<String[]> future = new CompletableFuture<>();
        String url = "https://nominatim.openstreetmap.org/reverse?lat=" + coordinates[0] + "&lon=" + coordinates[1] + "&zoom=10&format=geocodejson&accept-language=en";

        API.getAsync(url, new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = API.createJSONObject(response);

                JSONArray featuresArray = (JSONArray) jsonObject.get("features");
                JSONObject featuresObject = (JSONObject) featuresArray.get(0);

                JSONObject propertiesObject = (JSONObject) featuresObject.get("properties");
                JSONObject geoCodingObject = (JSONObject) propertiesObject.get("geocoding");

                String countryCodeCca2 = (String) geoCodingObject.get("country_code");
                String countryName = (String) geoCodingObject.get("country");

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

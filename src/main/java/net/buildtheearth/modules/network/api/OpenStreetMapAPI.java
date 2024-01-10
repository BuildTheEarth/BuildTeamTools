package net.buildtheearth.modules.network.api;

import net.buildtheearth.modules.utils.APIUtil;
import net.buildtheearth.modules.utils.ChatHelper;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class OpenStreetMapAPI extends API {

    /**
     * @param coordinates The latitude & longitude coordinates to get the country, region & city/town from
     * @return A String array with [0] = country, [1] = region & [2] = city/town
     */
    public static CompletableFuture<String[]> getCountryAndSubRegionsFromLocationAsync(double[] coordinates) {
        System.out.println("coordinates = " + coordinates[0] + " " + coordinates[1]);
        CompletableFuture<String[]> future = new CompletableFuture<>();
        String url = "https://nominatim.openstreetmap.org/reverse?lat=" + coordinates[0] + "&lon=" + coordinates[1] + "&zoom=10&format=jsonv2";

        API.getAsync(url, new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = APIUtil.createJSONObject(response);
                JSONObject addressObject = (JSONObject) jsonObject.get("address");

                String countryName = (String) addressObject.get("country");
                String countryCode = (String) addressObject.get("country_code"); //cca2
                String subRegion = (String) addressObject.get("region");
                String city = (String) addressObject.get("city");

                if (city == null) {
                    city = addressObject.get("town").toString();
                }

                future.complete(new String[]{countryName, subRegion, city, countryCode});
            }

            @Override
            public void onFailure(IOException e) {
                future.completeExceptionally(e);
                ChatHelper.logDebug("Failed to get address for location %s.", coordinates);
            }
        });

        return future;
    }
}

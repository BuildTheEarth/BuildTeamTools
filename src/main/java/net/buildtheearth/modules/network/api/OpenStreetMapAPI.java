package net.buildtheearth.modules.network.api;

import net.buildtheearth.modules.utils.APIUtil;
import net.buildtheearth.modules.utils.ChatHelper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class OpenStreetMapAPI extends API {

    /**
     * @param coordinates The latitude & longitude coordinates to get the country, region & city/town from
     * @return A String array with [0] = country, [1] = region, [2] = city/town & [3] = cca2CountryCode  region & city/town can be countryName
     */
    public static CompletableFuture<String[]> getCountryAndSubRegionsFromLocationAsync(double[] coordinates) {
        System.out.println("coordinates = " + coordinates[0] + " " + coordinates[1]);
        CompletableFuture<String[]> future = new CompletableFuture<>();
        String url = "https://nominatim.openstreetmap.org/reverse?lat=" + coordinates[0] + "&lon=" + coordinates[1] + "&zoom=10&format=geocodejson&accept-language=en";

        API.getAsync(url, new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = APIUtil.createJSONObject(response);

                JSONArray featuresArray = (JSONArray) jsonObject.get("features");
                JSONObject featuresObject = (JSONObject) featuresArray.get(0);

                JSONObject propertiesObject = (JSONObject) featuresObject.get("properties");
                JSONObject geoCodingObject = (JSONObject) propertiesObject.get("geocoding");

                String countryCodeCca2 = (String) geoCodingObject.get("country_code");
                String countryName = (String) geoCodingObject.get("country");

                JSONObject adminObject = (JSONObject) geoCodingObject.get("admin");

                String subRegion = null;
                byte exitLevel = 0;
                for(byte i = 6; i < 11; i++) {
                    if(adminObject.get("level"+i) != null) {
                        subRegion = (String) adminObject.get("level"+i);
                        exitLevel = i;
                        break;
                    }
                }

                String city = null;
                for(byte i = (byte) (exitLevel+1); i < 11; i++) {
                    if(adminObject.get("level"+i) != null) {
                        city = (String) adminObject.get("level"+i);
                        break;
                    }
                }

                if(subRegion == null) {
                    subRegion = countryName;
                    city = countryName;
                }

                if(city == null) {
                    city = countryName;
                }



                future.complete(new String[]{countryName, subRegion, city, countryCodeCca2});
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

package net.buildtheearth.modules.network.api;

import net.buildtheearth.modules.utils.APIUtil;
import net.buildtheearth.modules.utils.ChatHelper;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class OpenStreetMapAPI extends API {

    //TODO MAKE ASYNC/ASYNC ALTERNATIVES TO THESE METHODS

    public static double[] getLocationFromAddress(String address) {
        if (address.contains(" ")) {
            address = address.replace(" ", "+");
        }

        String response;
        try {
            URL url = new URL("https://nominatim.openstreetmap.org/search?q=" + address + "&format=json&addressdetails=1&limit=1");
            response = APIUtil.get(url);
            if (response == null) return null;
        } catch (MalformedURLException e) {
            Bukkit.getLogger().severe("Failed to form the GET request for 'getLocationFromAddress'");
            return null;
        }

        response = response.substring(1, response.length() - 1); // Remove outside []
        JSONObject jsonObject = APIUtil.createJSONObject(response);

        double lat = Double.parseDouble(jsonObject.get("lat").toString());
        double lon = Double.parseDouble(jsonObject.get("lon").toString());

        return new double[]{lat, lon};
    }


    public static String getAddressFromLocation(double[] coordinates) {
        String response;
        try {
            URL url = new URL("https://nominatim.openstreetmap.org/reverse.php?lat=" + coordinates[0] + "&lon=" + coordinates[1] + "&zoom=18&format=jsonv2");
            response = APIUtil.get(url);
            if (response == null) return null;
        } catch (MalformedURLException e) {
            Bukkit.getLogger().severe("Failed to form the GET request for 'getAddressFromLocation'");
            return null;
        }

        JSONObject jsonObject = APIUtil.createJSONObject(response);
        JSONObject addressObject = (JSONObject) jsonObject.get("address");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(addressObject.get("road"));
        stringBuilder.append(" ");
        stringBuilder.append(addressObject.get("house_number"));
        stringBuilder.append(", ");
        stringBuilder.append(addressObject.get("postcode"));
        stringBuilder.append(" ");
        stringBuilder.append(addressObject.get("town"));

        return stringBuilder.toString();
    }


    /**
     * @param coordinates The latitude & longitude coordinates to get the country, region & city/town from
     * @return A String array with [0] = country, [1] = region & [2] = city/town
     */
    public static CompletableFuture<String[]> getCountryAndSubRegionsFromLocationAsync(double[] coordinates) {
        CompletableFuture<String[]> future = new CompletableFuture<>();
        String url = "https://nominatim.openstreetmap.org/reverse.php?lat=" + coordinates[0] + "&lon=" + coordinates[1] + "&zoom=10&format=jsonv2";

        API.getAsync(url, new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = APIUtil.createJSONObject(response);
                JSONObject addressObject = (JSONObject) jsonObject.get("address");

                String countryName = (String) addressObject.get("country");
                String subRegion = (String) addressObject.get("region");
                String city = (String) addressObject.get("city");

                if (city == null) {
                    city = addressObject.get("town").toString();
                }

                future.complete(new String[]{countryName, subRegion, city});
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

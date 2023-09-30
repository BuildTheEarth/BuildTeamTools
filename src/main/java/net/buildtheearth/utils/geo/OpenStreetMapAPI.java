package net.buildtheearth.utils.geo;

import net.buildtheearth.utils.APIUtil;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class OpenStreetMapAPI {

    //TODO MAKE ASYNC/ASYNC ALTERNATIVES TO THESE METHODS

    public static double[] getLocationFromAddress(String address) {
        if (address.contains(" ")) {
            address = address.replace(" ", "+");
        }

        String response;
        try {
            URL url = new URL("https://nominatim.openstreetmap.org/search?q=" + address + "&format=json&addressdetails=1&limit=1");
            response = APIUtil.get(url);
            if(response == null) return null;
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
            if(response == null) return null;
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
     *
     * @param coordinates The latitude & longitude coordinates to get the country, region & city/town from
     * @return A String array with [0] = country, [1] = region & [2] = city/town
     */
    public static String[] getCountryAndSubRegionsFromLocation(double[] coordinates) {
        String response;
        try {
            URL url = new URL("https://nominatim.openstreetmap.org/reverse.php?lat=" + coordinates[0] + "&lon=" + coordinates[1] + "&zoom=10&format=jsonv2");
            response = APIUtil.get(url);
            if(response == null) return null;
        } catch (MalformedURLException e) {
            Bukkit.getLogger().severe("Failed to form the GET request for 'getAddressFromLocation'");
            return null;
        }

        JSONObject jsonObject = APIUtil.createJSONObject(response);
        JSONObject addressObject = (JSONObject) jsonObject.get("address");

        String countryCode = addressObject.get("country_code").toString();
        String subRegion = addressObject.get("region").toString();
        String city = addressObject.get("city").toString();
        if(city == null) city = addressObject.get("town").toString();

        return new String[]{countryCode, subRegion, city};
    }
}

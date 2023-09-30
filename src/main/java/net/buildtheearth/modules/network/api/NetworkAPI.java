package net.buildtheearth.modules.network.api;

import net.buildtheearth.modules.utils.APIUtil;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;


public class NetworkAPI {

    public static Collection<String> getCountryCodesByKey(String apiKey) {

        //TODO change url once functionality is added to API

        JSONObject jsonObject;
        try {
            URL url = new URL("https://nwapi.buildtheearth.net/api/teams/"+ apiKey);
            jsonObject = APIUtil.createJSONObject(APIUtil.get(url));
        } catch (MalformedURLException e) {
            return null;
        }

        Bukkit.getLogger().info(jsonObject.toString());
        return null;
    }
}

package net.buildtheearth.modules.network.api;

import net.buildtheearth.modules.utils.APIUtil;
import net.buildtheearth.modules.warp.model.Warp;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;


public class NetworkAPI {

    //TODO CHANGE PARAMETER TO serverName
    public static Collection<String> getCountryCodesByKey(String apiKey) {

        //TODO change url once functionality is added to API

        JSONObject jsonObject;
        try {
            URL url = new URL("https://nwapi.buildtheearth.net/api/teams/" + apiKey);
            jsonObject = APIUtil.createJSONObject(APIUtil.get(url));
        } catch (MalformedURLException e) {
            return null;
        }

        Bukkit.getLogger().info(jsonObject.toString());
        return null;
    }

    public static Warp getWarpByKey(String warpKey) {
        //TODO IMPLEMENT METHOD
        return null;
    }

    public static void removeWarpByLocation(double[] coordinates) {
        //TODO IMPLEMENT METHOD
    }

    public static String getServerNameByCountryCode(String countryCode) {
        //TODO IMPLEMENT METHOD
        return null;
    }

    public static String getCurrentServerName() {
        //TODO IMPLEMENT METHOD
        // https://nwapi.buildtheearth.net/api/teams/%BUILD_TEAM_ID%/servers
        return null;
    }

    public static List<String> getCountriesByActiveServers() {
        //TODO IMPLEMENT METHOD
        return null;
    }
}

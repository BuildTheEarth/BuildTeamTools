package net.buildtheearth.buildteam;

import net.buildtheearth.buildteam.components.universal.BuildTeam;
import net.buildtheearth.buildteam.components.universal.Category;
import net.buildtheearth.buildteam.components.universal.Country;
import net.buildtheearth.buildteam.components.universal.universal_navigator.ExploreMenu;
import net.buildtheearth.utils.APIUtil;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

public class NetworkAPI {
    public static Category[] getAllLocationsForCountry(String name) {
        // TODO implement methods
        return new Category[10];
    }

    public static Country[] getAllCountriesInContinent(ExploreMenu.Continent continent) {
        // TODO implement methods
        return new Country[10];
    }

    public static BuildTeam getTeamFromCountry(String szName) {
        // TODO implement methods
        return new BuildTeam();
    }

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

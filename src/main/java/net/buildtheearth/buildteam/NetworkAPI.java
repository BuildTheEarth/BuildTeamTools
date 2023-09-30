package net.buildtheearth.buildteam;

import net.buildtheearth.buildteam.components.universal_experience.BuildTeam;
import net.buildtheearth.buildteam.components.universal_experience.Category;
import net.buildtheearth.buildteam.components.universal_experience.Country;
import net.buildtheearth.buildteam.components.universal_experience.Location;
import net.buildtheearth.buildteam.components.universal_experience.universal_navigator.ExploreMenu;
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
    
    //An API call
    public static Country[] getAllCountriesInContinent(ExploreMenu.Continent continent){
    }

    //An API call
    public static BuildTeam getTeamFromCountry(String szName)
    {
        //I believe there is already an API function for this
    }

    /**
     *
     * @return
     */
    public static Category[] getAllLocationsForCountry(String szCountry)
    {
        //Get the categories
        Category[] categories = getAllCategoriesForCountry(szCountry);

        for (Category category: categories)
        {
            category.setLocations(getAllLocationsForCategory(category));
        }
    }

    //An API call
    private static Category[] getAllCategoriesForCountry(String szCountry)
    {
        Category[] categories = new Category[0];

        return categories;
    }

    //An API call
    private static Location[] getAllLocationsForCategory(Category category)
    {
        Location[] locations = new Location[0];

        return locations;
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

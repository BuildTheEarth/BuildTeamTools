package net.buildtheearth.buildteam;

import net.buildtheearth.buildteam.components.universal_experience.BuildTeam;
import net.buildtheearth.buildteam.components.universal_experience.Category;
import net.buildtheearth.buildteam.components.universal_experience.Country;
import net.buildtheearth.buildteam.components.universal_experience.Location;
import net.buildtheearth.buildteam.components.universal_experience.universal_navigator.ExploreMenu;

public class NetworkAPI
{
    //An API call
    public static Country[] getAllCountriesInContinent(ExploreMenu.Continent continent)
    {

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
}

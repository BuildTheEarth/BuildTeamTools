package net.buildtheearth.buildteam;

import net.buildtheearth.buildteam.components.universal_experience.BuildTeam;
import net.buildtheearth.buildteam.components.universal_experience.Category;
import net.buildtheearth.buildteam.components.universal_experience.Country;
import net.buildtheearth.buildteam.components.universal_experience.universal_navigator.ExploreMenu;

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
}

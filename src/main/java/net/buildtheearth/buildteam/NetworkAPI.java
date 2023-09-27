package net.buildtheearth.buildteam;

import net.buildtheearth.buildteam.components.universal.BuildTeam;
import net.buildtheearth.buildteam.components.universal.Category;
import net.buildtheearth.buildteam.components.universal.Country;
import net.buildtheearth.buildteam.components.universal.universal_navigator.ExploreMenu;

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

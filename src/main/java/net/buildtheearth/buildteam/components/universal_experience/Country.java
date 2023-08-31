package net.buildtheearth.buildteam.components.universal_experience;

import net.buildtheearth.buildteam.NetworkAPI;
import net.buildtheearth.buildteam.components.universal_experience.universal_navigator.ExploreMenu;

public class Country
{
    private String szName;
    private String szFlagHead;
    private ExploreMenu.Continent continent;
    private BuildTeam buildTeam;

    public Country(String szName, ExploreMenu.Continent continent)
    {
        this.szName = szName;
        this.continent = continent;

    }

    public Country(String szName, ExploreMenu.Continent continent, String szFlagHead)
    {
        this.szName = szName;
        this.continent = continent;
        this.szFlagHead = szFlagHead;
    }

    private void fetchTeamInfoFromDB()
    {
        this.buildTeam = NetworkAPI.getTeamFromCountry(szName);
    }

    public String getName()
    {
        return szName;
    }

    public String getFlagHeadID()
    {
        return szFlagHead;
    }

    public ExploreMenu.Continent getContinent()
    {
        return continent;
    }
    /**
     * If Build Team is null, will fetch from the API
     * @return The build team who manage this country
     */
    public BuildTeam getBuildTeam()
    {
        if (buildTeam == null)
            fetchTeamInfoFromDB();

        return this.buildTeam;
    }

}

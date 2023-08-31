package net.buildtheearth.buildteam.components.universal_experience;

/**
 * Represents a global BTE warp, as stored in the categories table of the BTE Network DB
 */
public class Category
{
    private String szCategoryName;
    private String szCountryName;

    //All the locations belonging to this category
    private Location[] locations;

    public String getCategoryName()
    {
        return szCategoryName;
    }

    public Location[] getLocations()
    {
        return locations;
    }

    public void setLocations(Location[] locations)
    {
        this.locations = locations;
    }
}

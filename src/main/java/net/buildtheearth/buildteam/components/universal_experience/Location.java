package net.buildtheearth.buildteam.components.universal_experience;

/**
 * Represents a global BTE warp, as stored in the locations table of the BTE Network DB
 */
public class Location
{
    private String szName;
    private boolean bCategorised;
    private String szCategory;
    private double x;
    private double y;
    private double z;
    private float fYaw;
    private float fPitch;

    public Location(org.bukkit.Location bukkitLocation)
    {
        this.szName = "";
        this.bCategorised = false;
        this.x = bukkitLocation.getX();
        this.y = bukkitLocation.getY();
        this.z = bukkitLocation.getZ();
        this.fYaw = bukkitLocation.getYaw();
        this.fPitch = bukkitLocation.getPitch();
    }

//    public Location(LocationRequest request)
//    {
//        this.szName = request.szName;
//        // etc
//    }

    public String getName()
    {
        return szName;
    }

    public void setName(String szName)
    {
        this.szName = szName;
    }
}

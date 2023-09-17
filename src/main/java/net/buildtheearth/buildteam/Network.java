package net.buildtheearth.buildteam;

import net.buildtheearth.Main;
import net.buildtheearth.buildteam.commands.Navigator;
import net.buildtheearth.buildteam.components.BTENetwork;
import net.buildtheearth.buildteam.components.universal_experience.PreferenceType;
import net.buildtheearth.buildteam.database.User;
import net.buildtheearth.buildteam.listeners.CancelledEvents;
import net.buildtheearth.buildteam.listeners.InteractEvent;
import net.buildtheearth.buildteam.listeners.Join_Listener;
import net.buildtheearth.buildteam.listeners.Stats_Listener;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.geo.LatLng;
import net.buildtheearth.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Used to handle all network and interface features of the plugin
 */
public class Network
{
    private BTENetwork BTENetwork;

    private ItemStack navigator;
    private final int iSlot; //0 index

    //Maps each preference to its preference map
    private HashMap<PreferenceType, HashMap> userPreferences;

    /**
     * Stores the pending tpll events which are handled by Join_Event
     */
    private HashMap<UUID, LatLng> tpllEvents = new HashMap<>();

    /**
     * Stores the pending warp events which are handled by Join_Event
     */
    private HashMap<UUID, Location> warpEvents = new HashMap<>();

    public Network()
    {
        this.iSlot = Main.instance.getConfig().getInt("navigator.slot");;
    }

    //-------------------------------------
    //---------------Getters---------------
    //-------------------------------------
    public BTENetwork getBTENetwork()
    {
        return this.BTENetwork;
    }

    public ItemStack getNavigator()
    {
        return navigator;
    }

    public int getNavSlot()
    {
        return iSlot;
    }

//    public DBConnection getDBConnection()
//    {
//        return dbConnection;
//    }

    public HashMap<UUID, LatLng> getTpllEvents()
    {
        return tpllEvents;
    }

    public HashMap<UUID, Location> getWarpEvents()
    {
        return warpEvents;
    }

    public boolean isBTENetworkConnected()
    {
        return BTENetwork.isConnected();
    }

    public void addTpllEvent(UUID uuid, LatLng coordinates)
    {
        tpllEvents.put(uuid, coordinates);
    }

    public void addWarpEvent(UUID uuid, Location location)
    {
        warpEvents.put(uuid, location);
    }

    //-------------------------------------
    //--------------Functions--------------
    //-------------------------------------
    public void start()
    {
        //Creates a bte network object, and loads its variables
        BTENetwork = new BTENetwork();

        //Create the navigator icon
        ArrayList<String> lore = new ArrayList<>();
        lore.add(Utils.loreText("Click to open the navigator."));
        navigator = Item.create(Material.NETHER_STAR, Utils.menuIconTitle("Navigator"), 1, lore);

        initialisePreferences();

        //connectToDB();
        //createDBTables();

        //Register commands
        Main.instance.getCommand("navigator").setExecutor(new Navigator());

        //Register listeners
        registerListeners();
    }

    /** Registers all global Listeners of the network component. */
    private void registerListeners()
    {
        //Navigator hotbar item
        new InteractEvent(Main.instance, this);

        Bukkit.getPluginManager().registerEvents(new CancelledEvents(), Main.instance);
        Bukkit.getPluginManager().registerEvents(new Join_Listener(this), Main.instance);
        Bukkit.getPluginManager().registerEvents(new Stats_Listener(), Main.instance);
    }

    /**
     * Initialises the DB connection object and connects to the database
     */
//    private void connectToDB()
//    {
//        dbConnection = new DBConnection();
//        dbConnection.mysqlSetup(Main.instance.getConfig());
//        dbConnection.connect();
//    }

    public void stop()
    {
        BTENetwork.update();
    }

    //-------------------------------------------------
    //-------------------Preferences-------------------
    //-------------------------------------------------

    /**
     * Creates each preference list and adds it to the big list of everyone's preferences
     */
    private void initialisePreferences()
    {
        this.userPreferences = new HashMap<>(PreferenceType.values().length+1, 1);
        HashMap<UUID, Boolean> navigatorEnabled = new HashMap<>();
        this.userPreferences.put(PreferenceType.NavigatorEnabled, navigatorEnabled);
    }

    public void fetchAndLoadPreferences(UUID uuid)
    {
        User user = User.fetchUser(uuid);
        userPreferences.get(PreferenceType.NavigatorEnabled).put(uuid, user.bNavigatorEnabled);
    }

    //Toggles a boolean preference
    public void updatePreference(PreferenceType preferenceType, UUID uuid)
    {
        if (userPreferences.get(preferenceType).get(uuid) instanceof Boolean)
        {
            Boolean bValue = (Boolean) userPreferences.get(preferenceType).get(uuid);
            userPreferences.get(preferenceType).replace(uuid, Boolean.valueOf(!bValue.booleanValue()));
        }

        //Todo: Make the change in the DB as well
    }


    /**
     * This method is called every second.
     * It contains all systems that have to run once a second.
     */
    public void tickSeconds()
    {
        if (this.isBTENetworkConnected())
        {
            if (Main.instance.getConfig().getBoolean("navigator.enabled"))
            {
                ItemStack navLocation;
                //Cycles through all players
                for (Player player: Bukkit.getOnlinePlayers())
                {
                    //If navigator is enabled check if they have it in the relevant slot.
                    if ((Boolean) userPreferences.get(PreferenceType.NavigatorEnabled).get(player.getUniqueId()))
                    {
                        //Gets what's in the relevant slot
                        navLocation = player.getInventory().getItem(this.iSlot);

                        //Give the player the inventory
                        if (navLocation == null)
                        {
                            player.getInventory().setItem(this.iSlot, this.navigator);
                        }
                        else if (!(navLocation.equals(this.navigator)))
                        {
                            player.getInventory().setItem(this.iSlot, this.navigator);
                        }
                    }
                }
            }
        }
    }
}

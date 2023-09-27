package net.buildtheearth.buildteam.components.universal.universal_tpll;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.buildtheearth.Main;
import net.buildtheearth.buildteam.components.universal.universal_tpll.projection.Airocean;
import net.buildtheearth.buildteam.components.universal.universal_tpll.projection.ModifiedAirocean;
import net.buildtheearth.utils.geo.CoordinateParseUtils;
import net.buildtheearth.utils.geo.LatLng;
import net.buildtheearth.utils.ProxyUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class TpllListener implements Listener
{
    private String szCurrentServer;

    public TpllListener(Main plugin, String szCurrentServer)
    {
        this.szCurrentServer = szCurrentServer;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    //Lowest will be used by tutorials
    @EventHandler(priority = EventPriority.LOW)
    public void tpllEvent(PlayerCommandPreprocessEvent event)
    {
        //Re-routes some common misspelling
        String szCommand = event.getMessage();
        if (szCommand.startsWith("/rpll"))
            event.setMessage(szCommand.replace("rpll", "tpll"));
        else if (szCommand.startsWith("/ypll"))
            event.setMessage(szCommand.replace("ypll", "tpll"));
        else if (szCommand.startsWith("/toll"))
            event.setMessage(szCommand.replace("toll", "tpll"));
        else if (szCommand.startsWith("/t[ll"))
            event.setMessage(szCommand.replace("t[ll", "tpll"));
        else if (szCommand.startsWith("/tpkk"))
            event.setMessage(szCommand.replace("tpkk", "tpll"));
        //Each letter left shifted
        else if (szCommand.startsWith("/rokk"))
            event.setMessage(szCommand.replace("rokk", "tpll"));
        //Each letter right shifted
        else if (szCommand.startsWith("/y[;;"))
            event.setMessage(szCommand.replace("y[;;", "tpll"));

        //Now extracts the coordinates and tests them
        szCommand = event.getMessage();
        szCommand = szCommand.replace("/tpll ", "");
        LatLng latLong = CoordinateParseUtils.parseVerbatimCoordinates(szCommand.replace(", ", " "));

        //Checks that the coordinates were established
        if (latLong == null)
        {
            //Let it play out, terra++ will deal with it
            event.setCancelled(false);
            return;
        }

        //Coordinates were correctly established
        Player player = event.getPlayer();

        //Locate the server
        String szTargetServer = ""; //ServerLocator.getServerFromLocation(latLong.getLat(), latLong.getLng());

        //Checks to see whether the player needs to switch server
        if (szTargetServer.equals(szCurrentServer))
        {
            //Performs the tpll command immediately, bypasses sending messages and switching server
            performTpll(player, latLong.getLat(), latLong.getLng());
        }
        else
        {
            //---Sends the tpll command message---

            ByteArrayDataOutput out = ByteStreams.newDataOutput();

            //Specify sub channel
            out.writeUTF("Tpll");

            //Specify the target server
            out.writeUTF(szTargetServer);

            //Specify the coordinates
            out.writeUTF(latLong.getLat()+"");
            out.writeUTF(latLong.getLng()+"");

            //Specifies the pitch and yaw
            out.writeUTF(player.getLocation().getYaw()+"");
            out.writeUTF(player.getLocation().getPitch()+"");

            //Sends the message
            player.sendPluginMessage(Main.instance, "BuildTeam", out.toByteArray());

            //Connects the player to the relevant server
            ProxyUtil.SwitchServer(player, szTargetServer);
        }
    }

    /**
     * Tplls a player. Used when the tpll is to the same server as the user is currently on
     * @param player The player executing the tpll command
     * @param dLatitude The target latitude
     * @param dLongitude The target longitude
     */
    public static void performTpll(Player player, double dLatitude, double dLongitude)
    {
        //Do the tpll
        Airocean projection = new ModifiedAirocean();
        double mpu = projection.metersPerUnit();

        double[] xz = projection.fromGeo(dLongitude, dLatitude);

        double x = xz[0] * mpu;
        double z = -xz[1] * mpu;

        World tpWorld = Bukkit.getWorld(Main.instance.getConfig().getString("universal_tpll.earth_world"));
        if (tpWorld == null)
            player.sendMessage(ChatColor.RED +"The earth world for this build team is unknown");
        else
        {
            int y = tpWorld.getHighestBlockYAt((int) x, (int) z) + 1;
            player.teleport(new Location(tpWorld, x, y, z));
        }
    }

    /**
     * Tplls a player. Used when the tpll is to a different server, so the location is created when the message is received by the plugin
     * @param player The player executing the tpll command
     * @param location The target location
     */
    public static void performTpll(Player player, Location location)
    {
        //Teleports the player
        if (location.getWorld() == null)
            player.sendMessage(ChatColor.RED +"The earth world for this build team is unknown");
        else
            player.teleport(location);
    }
}

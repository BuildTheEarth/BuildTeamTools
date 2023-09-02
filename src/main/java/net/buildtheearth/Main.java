package net.buildtheearth;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.buildtheearth.buildteam.BuildTeamTools;
import net.buildtheearth.buildteam.components.universal_experience.universal_tpll.projection.Airocean;
import net.buildtheearth.buildteam.components.universal_experience.universal_tpll.projection.ModifiedAirocean;
import net.buildtheearth.buildteam.components.updater.Updater;
import net.buildtheearth.terraminusminus.util.geo.LatLng;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;


public class Main extends JavaPlugin implements PluginMessageListener
{
	public static Main instance;

	/**
	 * Handles all of the plugin's feature
	 */
	public static BuildTeamTools buildTeamTools;


	@Override
	public void onEnable()
	{
		instance = this;

		buildTeamTools = new BuildTeamTools();
		buildTeamTools.start();

		String resultMessage = startUpdateChecker();
		getLogger().info("Plugin with version " + getDescription().getVersion() + " started. " + resultMessage);
	}
	
	@Override
	public void onDisable(){
		buildTeamTools.stop();

		getLogger().info("Plugin stopped.");
	}
	
	@Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (channel.equals("BuildTeam"))
		{
			if(!Main.getBuildTeam().getBTENetwork().getCommunicators().contains(player.getUniqueId()))
				Main.getBuildTeam().getBTENetwork().getCommunicators().add(player.getUniqueId());


			ByteArrayDataInput in = ByteStreams.newDataInput(message);
			String subchannel = in.readUTF();

			getLogger().info("Message received: " + subchannel + " from Player: " + player.getName());

			if(subchannel.equalsIgnoreCase("Ping")){
				String serverID = in.readUTF();
				String buildTeamID = in.readUTF();

				Main.getBuildTeam().getBTENetwork().setBuildTeamID(buildTeamID);
				Main.getBuildTeam().getBTENetwork().setServerID(serverID);
			}else if(subchannel.equalsIgnoreCase("Stats")){
				String status = in.readUTF();

				if(status.equals("OK"))
					this.buildTeamTools.getBTENetwork().getStatsManager().resetCache();
			}

			//Handles universal tpll commands
			else if (subchannel.equals("Tpll"))
			{
				//Check the target server
				String szServer = in.readUTF();
				if (szServer.equals(Main.getBuildTeam().getBTENetwork().getServerID()))
				{
					//Extracts the coordinares
					double dLatitude = Double.parseDouble(in.readUTF());
					double dLongitude = Double.parseDouble(in.readUTF());

					//Extracts the yaw and pitch
					float fYaw = Float.parseFloat(in.readUTF());
					float fPitch = Float.parseFloat(in.readUTF());

					Airocean projection = new ModifiedAirocean();
					double mpu = projection.metersPerUnit();

					double[] xz = projection.fromGeo(dLongitude, dLatitude);

					double x = xz[0] * mpu;
					double z = -xz[1] * mpu;

					//Creates the location
					Location tpllTPLocation;
					World tpWorld = Bukkit.getWorld(Main.instance.getConfig().getString("universal_tpll.earth_world"));
					if (tpWorld == null)
						tpllTPLocation = new Location(tpWorld, x, 64, z, fYaw, fPitch);
					else
						tpllTPLocation = new Location(tpWorld, x, (tpWorld.getHighestBlockYAt((int) x, (int) z) + 1), z, fYaw, fPitch);

					//Adds the event to the list
					this.buildTeamTools.getNetwork().addTpllEvent(player.getUniqueId(), tpllTPLocation);
				}
			}

			//Handles universal tpll commands
			else if (subchannel.equals("UniversalWarps"))
			{
				//Check the target server
				String szServer = in.readUTF();
				if (szServer.equals(Main.getBuildTeam().getBTENetwork().getServerID()))
				{
					//Extracts the coordinares
					double X = Double.parseDouble(in.readUTF());
					double Y = Double.parseDouble(in.readUTF());
					double Z = Double.parseDouble(in.readUTF());
					float fYaw = Float.parseFloat(in.readUTF());
					float fPitch = Float.parseFloat(in.readUTF());

					//Creates the location
					World tpWarp = Bukkit.getWorld(this.getConfig().getString("universal_tpll.earth_world"));
					Location warpTPLocation = new Location(tpWarp, X, Y, Z, fYaw, fPitch);

					//Adds the event to the list, to be dealt with by join listener
					this.buildTeamTools.getNetwork().addWarpEvent(player.getUniqueId(), warpTPLocation);
				}
			}
		}
	}

	private String startUpdateChecker(){
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run() {
				checkForUpdates();
			}
		}, 20*60*60);

		return checkForUpdates();
	}

	public String checkForUpdates(){
		Updater updater = new Updater(this, BuildTeamTools.SPIGOT_PROJECT_ID, this.getFile(), Updater.UpdateType.CHECK_DOWNLOAD, buildTeamTools.isDebug());
		Updater.Result result = updater.getResult();

		String resultMessage = "";
		switch (result){
			case BAD_ID: resultMessage = "Failed to update the plugin: Wrong Spigot ID."; break;
			case FAILED: resultMessage = "Failed to update the plugin."; break;
			case NO_UPDATE: resultMessage = "The plugin is up to date."; break;
			case SUCCESS: resultMessage = "Plugin successfully updated."; break;
			case UPDATE_FOUND: resultMessage = "Found an update for the plugin."; break;
			default: resultMessage = "No result for update search."; break;
		}

		return resultMessage;
	}

	public static BuildTeamTools getBuildTeam() {
		return buildTeamTools;
	}

	public static void setBuildTeam(BuildTeamTools buildTeamTools) {
		Main.buildTeamTools = buildTeamTools;
	}


}

package net.buildtheearth;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.buildtheearth.buildteam.BuildTeamTools;
import net.buildtheearth.buildteam.components.updater.Updater;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;


public class Main extends JavaPlugin implements PluginMessageListener{
	
	public static Main instance;
	public static BuildTeamTools buildTeamTools;

	@Override
	public void onEnable(){
		instance = this;
		buildTeamTools = new BuildTeamTools();
		buildTeamTools.start();

		String resultMessage = startUpdateChecker();
		System.out.println("[BuildTeam] Plugin with version " + getDescription().getVersion() + " started. " + resultMessage);
	}
	
	@Override
	public void onDisable(){
		System.out.println("[BuildTeam] Plugin stopped.");
		
		buildTeamTools.stop();
	}
	
	@Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
	    if (!channel.equals("BuildTeam")) {
	      return;
	    }

	    if(!Main.getBuildTeam().getBTENetwork().getCommunicators().contains(player.getUniqueId()))
			Main.getBuildTeam().getBTENetwork().getCommunicators().add(player.getUniqueId());


		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subchannel = in.readUTF();

		System.out.println("Message received: " + subchannel + " from Player: " + player.getName());

		if(subchannel.equalsIgnoreCase("Ping")){
			String serverID = in.readUTF();
			String buildTeamID = in.readUTF();

			Main.getBuildTeam().getBTENetwork().setBuildTeamID(buildTeamID);
			Main.getBuildTeam().getBTENetwork().setServerID(serverID);
		}else if(subchannel.equalsIgnoreCase("Stats")){
			String status = in.readUTF();

			if(status.equals("OK"))
				Main.buildTeamTools.getBTENetwork().getStatsManager().resetCache();
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

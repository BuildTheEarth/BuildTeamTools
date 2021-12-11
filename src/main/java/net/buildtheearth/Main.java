package net.buildtheearth;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.buildtheearth.buildteam.BuildTeam;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class Main extends JavaPlugin implements PluginMessageListener{
	
	public static Main instance;
	public static BuildTeam buildTeam;

	@Override
	public void onEnable(){
		instance = this;
		buildTeam = new BuildTeam();
		buildTeam.start();

		System.out.println("[BuildTeam] Plugin started.");
	}
	
	@Override
	public void onDisable(){	
		System.out.println("[BuildTeam] Plugin stopped.");
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
				Main.buildTeam.getBTENetwork().getStatsManager().resetCache();
		}


	}

	public static BuildTeam getBuildTeam() {
		return buildTeam;
	}

	public static void setBuildTeam(BuildTeam buildTeam) {
		Main.buildTeam = buildTeam;
	}
}

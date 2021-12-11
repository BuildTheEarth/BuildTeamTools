package net.buildtheearth.buildteam.commands;

import net.buildtheearth.Main;
import net.buildtheearth.utils.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class buildteam_command implements CommandExecutor{
	
	public boolean onCommand(CommandSender sender, Command cmd, String cmdlabel, String[] args){

		if(args.length == 0){

			ChatUtil.sendMessageBox(sender, "Build Team Plugin", new Runnable() {
				@Override
				public void run() {
					String buildTeam = "-";
					if(Main.buildTeam.getBTENetwork().getBuildTeamID() != null)
						buildTeam = Main.buildTeam.getBTENetwork().getBuildTeamID();

					String serverID = "-";
					if(Main.buildTeam.getBTENetwork().getServerID() != null)
						serverID = Main.buildTeam.getBTENetwork().getServerID();

					String status = "§c§lDISCONNECTED";
					if(Main.buildTeam.getBTENetwork().isConnected() &&! buildTeam.equals("-") &&! serverID.equals("-"))
						status = "§a§lCONNECTED";
					else if(! buildTeam.equals("-") &&! serverID.equals("-"))
						status = "§6§lSTANDBY";

					sender.sendMessage("§eStatus: " + status);
					sender.sendMessage("§eBuildTeam ID: §7" + buildTeam);
					sender.sendMessage("§eServer ID: §7" + serverID);
					sender.sendMessage("");
					sender.sendMessage("§7Sub-Command list with §e/bt help§7.");
				}
			});

			return true;
		}

		if(args[0].equalsIgnoreCase("help")){
			ChatUtil.sendMessageBox(sender, "Build Team Help", new Runnable() {
				@Override
				public void run() {
					sender.sendMessage("§e/bt help - §7List of all sub commands.");
					sender.sendMessage("§e/bt communicators - §7List of players who communicate with the network.");

				}
			});

			return true;
		}else if(args[0].equalsIgnoreCase("communicators")){
			ChatUtil.sendMessageBox(sender, "Build Team Communicators", new Runnable() {
				@Override
				public void run() {
					for(UUID uuid : Main.getBuildTeam().getBTENetwork().getCommunicators())
						sender.sendMessage("§7- §e" + uuid.toString());
				}
			});
		}else if(args[0].equalsIgnoreCase("cache")){
			ChatUtil.sendMessageBox(sender, "Build Team Cache", new Runnable() {
				@Override
				public void run() {
					sender.sendMessage(Main.buildTeam.getBTENetwork().getStatsManager().getCurrentCache().toJSONString());
				}
			});
		}else if(args[0].equalsIgnoreCase("update")){
			Main.buildTeam.getBTENetwork().update();
			sender.sendMessage("Cache uploaded to the network.");
		}



		return true;
	}
}

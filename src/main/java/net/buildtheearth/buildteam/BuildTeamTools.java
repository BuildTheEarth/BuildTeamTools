package net.buildtheearth.buildteam;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import net.buildtheearth.buildteam.commands.buildteamtools_command;
import net.buildtheearth.buildteam.commands.generate_command;
import net.buildtheearth.buildteam.components.BTENetwork;
import net.buildtheearth.buildteam.components.stats.StatsPlayerType;
import net.buildtheearth.buildteam.components.stats.StatsServerType;
import net.buildtheearth.buildteam.listeners.CancelledEvents;
import net.buildtheearth.buildteam.components.ConfigManager;
import net.buildtheearth.buildteam.listeners.Join_Listener;
import net.buildtheearth.buildteam.listeners.Stats_Listener;
import org.bukkit.Bukkit;

import net.buildtheearth.Main;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.ipvp.canvas.MenuFunctionListener;

import java.util.ArrayList;
import java.util.List;

public class BuildTeamTools {

	public static int SPIGOT_PROJECT_ID = 101854;
	public static String PREFIX = "§9§lBTE §8> §7";

	private boolean debug;
	private boolean updateInstalled;
	private String newVersion;

	private long time;
	private BTENetwork bteNetwork;

	public BuildTeamTools() {}
	
	public void start() {
		registerCommands();
		registerListeners();
		startTimer();

		ConfigManager.setStandard();
		ConfigManager.readData();

		Main.instance.getServer().getMessenger().registerOutgoingPluginChannel(Main.instance, "BuildTeam");
		Main.instance.getServer().getMessenger().registerIncomingPluginChannel(Main.instance, "BuildTeam", Main.instance);

		bteNetwork = new BTENetwork();
		LocalSession.MAX_HISTORY_SIZE = 500;
	}

	public void stop(){
		Main.buildTeamTools.getBTENetwork().update();
	}
	
	
	
	/** This method is called every second.
	 *  It contains all systems that have to run once a second.
	 */
	private void tickSeconds() {

	}
	
	
	/** The main Timer of the plugin that runs once a second.
	 *  It calls the tick() function.
	 */
	private void startTimer() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.instance, new Runnable() {
			
			@Override
			public void run() {
				time++;

				// Jede Stunde
				if(time%(20*60*60) == 0){
				}

				// Jede 10 Minuten (+1 Sekunde)
				if(time%(BTENetwork.CACHE_UPLOAD_SPEED) == 0) {
					Main.buildTeamTools.getBTENetwork().update();
				}

				// Jede Minute
				if(time%(20*60) == 0) {
					Main.buildTeamTools.getBTENetwork().getStatsManager().getStatsServer().addValue(StatsServerType.UPTIME, 1);

					for (Player p : Bukkit.getOnlinePlayers()) {
						Main.buildTeamTools.getBTENetwork().getStatsManager().getStatsServer().addValue(StatsServerType.PLAYTIME, 1);
						Main.buildTeamTools.getBTENetwork().getStatsManager().getStatsPlayer(p.getUniqueId()).addValue(StatsPlayerType.PLAYTIME, 1);
					}
				}

				// Alle 5 Sekunden
				if(time%100 == 0){
				}

				// Jede Sekunde
				if(time%20 == 0){
					tickSeconds();
				}

				// Jede viertel Sekunde
				if(time%5 == 0){
				}
			}
		},0,0);
	}

	public void notifyUpdate(Player p){
		if(!updateInstalled)
			return;

		if(p.hasPermission("buildteam.notifyUpdate")) {
			p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
			p.sendMessage("");
			p.sendMessage("§6§l[BuildTeam Plugin] §eThe server automatically installed a new update (v" + newVersion + ").");
			p.sendMessage("§6>> §ePlease restart or reload the server to activate it.");
			p.sendMessage("");
		}
	}
	
	/** Registers all Commands of the plugin. */
	private void registerCommands() {
		Main.instance.getCommand("buildteam").setExecutor(new buildteamtools_command());
		Main.instance.getCommand("generate").setExecutor(new generate_command());

	}

	/** Registers all Listeners of the plugin. */
	private void registerListeners() {
		Bukkit.getPluginManager().registerEvents(new MenuFunctionListener(), Main.instance);
		Bukkit.getPluginManager().registerEvents(new CancelledEvents(), Main.instance);
		Bukkit.getPluginManager().registerEvents(new Join_Listener(), Main.instance);
		Bukkit.getPluginManager().registerEvents(new Stats_Listener(), Main.instance);
	}



	public static class DependencyManager {

		// List with all missing dependencies
		private final static List<String> missingDependencies = new ArrayList<>();

		/**
		 * Check for all required dependencies and inform in console about missing dependencies
		 * @return True if all dependencies are present
		 */
		private static boolean checkForRequiredDependencies() {
			PluginManager pluginManager = Main.instance.getServer().getPluginManager();

			if (!pluginManager.isPluginEnabled("WorldEdit")) {
				missingDependencies.add("WorldEdit (V6.1.9)");
			}

			return missingDependencies.isEmpty();
		}

		/**
		 * @return True if WorldEdit is present
		 */
		public static boolean isWorldEditEnabled() {
			return Main.instance.getServer().getPluginManager().isPluginEnabled("WorldEdit");
		}

		/**
		 * @return World Edit instance
		 */
		public static WorldEdit getWorldEdit() {
			return WorldEdit.getInstance();
		}
	}

	public BTENetwork getBTENetwork() {
		return bteNetwork;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void setUpdateInstalled(String newVersion) {
		this.newVersion = newVersion;
		this.updateInstalled = true;

		for(Player p : Bukkit.getOnlinePlayers())
			notifyUpdate(p);
	}
}

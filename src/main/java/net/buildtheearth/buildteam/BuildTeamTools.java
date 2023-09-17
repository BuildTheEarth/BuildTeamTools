package net.buildtheearth.buildteam;

import com.alpsbte.alpslib.io.YamlFileFactory;
import com.alpsbte.alpslib.io.config.ConfigNotImplementedException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import net.buildtheearth.buildteam.commands.buildteamtools_command;
import net.buildtheearth.buildteam.commands.generate_command;
import net.buildtheearth.buildteam.commands.statistics_command;
import net.buildtheearth.buildteam.components.BTENetwork;
import net.buildtheearth.buildteam.components.generator.Generator;
import net.buildtheearth.buildteam.components.stats.StatsPlayerType;
import net.buildtheearth.buildteam.components.stats.StatsServerType;
import net.buildtheearth.utils.io.ConfigUtil;
import org.bukkit.Bukkit;

import net.buildtheearth.Main;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.ipvp.canvas.MenuFunctionListener;

import java.util.*;

/**
 * The parent of all components of Build Team Tools, including BTE network components and building components
 */
public class BuildTeamTools {

	public static int SPIGOT_PROJECT_ID = 101854;
	public static String PREFIX = "§9§lBTE §8> §7";

	private boolean debug;
	private boolean updateInstalled;
	private String newVersion;

	private long time;
	private Generator generator;

	/**
	 * Handles all network and interface features
	 */
	private Network network;

	public BuildTeamTools() {}

	//Getters
	public BTENetwork getBTENetwork()
	{
		return network.getBTENetwork();
	}

	public Network getNetwork() {
		return network;
	}

	public Generator getGenerator() {
		return generator;
	}


	public void start() {
		String errorPrefix = ChatColor.DARK_GRAY + "[" + ChatColor.RED + "X" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY;

		// Load config, if it throws an exception disable plugin
		try {
			YamlFileFactory.registerPlugin(Main.instance);
			ConfigUtil.init();
		} catch (ConfigNotImplementedException ex) {
			Bukkit.getConsoleSender().sendMessage(errorPrefix + "Could not load BuildTeamTools configuration file.");
			Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "The config file must be configured!");

			Main.instance.getServer().getPluginManager().disablePlugin(Main.instance);
			return;
		}
		ConfigUtil.getInstance().reloadFiles();

		// Register Plugin Messaging Channel
		Main.instance.getServer().getMessenger().registerOutgoingPluginChannel(Main.instance, "BuildTeam");
		Main.instance.getServer().getMessenger().registerIncomingPluginChannel(Main.instance, "BuildTeam", Main.instance);



		//Starts the network and interface features
		network = new Network();
		network.start();

		//Starts the generator module
		generator = new Generator();

		registerCommands();
		registerListeners();

		LocalSession.MAX_HISTORY_SIZE = 500;

		startTimer();
	}

	public void stop()
	{
		if(network != null)
			network.stop();
	}

	/** This method is called every second.
	 *  It contains all systems that have to run once a second.
	 */
	private void tickSeconds()
	{
		network.tickSeconds();
	}

	/** The main Timer of the plugin that runs once a second.
	 *  It calls the tick() function.
	 */
	private void startTimer()
	{
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.instance, () -> {
			time++;

			// Jede Stunde
			if(time%(20*60*60) == 0){
			}

			// Jede 10 Minuten (+1 Sekunde)
			if(time%(BTENetwork.CACHE_UPLOAD_SPEED) == 0) {
				this.network.getBTENetwork().update();
			}

			// Jede Minute
			if(time%(20*60) == 0) {
				this.network.getBTENetwork().getStatsManager().getStatsServer().addValue(StatsServerType.UPTIME, 1);

				for (Player p : Bukkit.getOnlinePlayers()) {
					this.network.getBTENetwork().getStatsManager().getStatsServer().addValue(StatsServerType.PLAYTIME, 1);
					this.network.getBTENetwork().getStatsManager().getStatsPlayer(p.getUniqueId()).addValue(StatsPlayerType.PLAYTIME, 1);
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

			generator.tick();
		},0,0);
	}


	/** Registers all global Commands of the plugin. */
	private void registerCommands() {
		Main.instance.getCommand("buildteam").setExecutor(new buildteamtools_command());
		Main.instance.getCommand("generate").setExecutor(new generate_command());
		Main.instance.getCommand("statistics").setExecutor(new statistics_command());
	}

	/** Registers all global Listeners of the plugin. */
	private void registerListeners()
	{
		Bukkit.getPluginManager().registerEvents(new MenuFunctionListener(), Main.instance);
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
		 * @return True if SchematicBrush is present
		 */
		public static boolean isSchematicBrushEnabled() {
			return Main.instance.getServer().getPluginManager().isPluginEnabled("SchematicBrush");
		}

		/**
		 * @return World Edit instance
		 */
		public static WorldEdit getWorldEdit() {
			return WorldEdit.getInstance();
		}
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

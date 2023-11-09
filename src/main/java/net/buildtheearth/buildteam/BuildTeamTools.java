package net.buildtheearth.buildteam;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import net.buildtheearth.buildteam.commands.CMD_BuildTeamTools;
import net.buildtheearth.buildteam.commands.CMD_Generate;
import net.buildtheearth.buildteam.components.BTENetwork;
import net.buildtheearth.buildteam.components.generator.Generator;
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

	public static final int SPIGOT_PROJECT_ID = 101854;
	public static final String PREFIX = "§9§lBTE §8> §7";

	private boolean debug;
	private boolean updateInstalled;
	private String newVersion;

	private long time;
	private BTENetwork bteNetwork;
	private Generator generator;

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
		generator = new Generator();
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
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.instance, () -> {
            time++;

			// Each 10 minutes (+1 second)
            if(time%(BTENetwork.CACHE_UPLOAD_SPEED) == 0) {
                Main.buildTeamTools.getBTENetwork().update();
            }

            // Each minute
            if(time%(20*60) == 0) {
                Main.buildTeamTools.getBTENetwork().getStatsManager().getStatsServer().addValue(StatsServerType.UPTIME, 1);

                for (Player p : Bukkit.getOnlinePlayers()) {
                    Main.buildTeamTools.getBTENetwork().getStatsManager().getStatsServer().addValue(StatsServerType.PLAYTIME, 1);
                    Main.buildTeamTools.getBTENetwork().getStatsManager().getStatsPlayer(p.getUniqueId()).addValue(StatsPlayerType.PLAYTIME, 1);
                }
            }

			// Each second
            if(time%20 == 0){
                tickSeconds();
            }

            generator.tick();
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
		Main.instance.getCommand("buildteam").setExecutor(new CMD_BuildTeamTools());
		Main.instance.getCommand("generate").setExecutor(new CMD_Generate());

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
		public static boolean isWorldEditDisabled() {
			return !Main.instance.getServer().getPluginManager().isPluginEnabled("WorldEdit");
		}

		/**
		 * @return True if SchematicBrush is present
		 */
		public static boolean isSchematicBrushDisabled() {
			return !Main.instance.getServer().getPluginManager().isPluginEnabled("SchematicBrush");
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

	public Generator getGenerator() {
		return generator;
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

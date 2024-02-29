package net.buildtheearth;

import com.alpsbte.alpslib.io.YamlFileFactory;
import com.alpsbte.alpslib.io.config.ConfigNotImplementedException;
import com.sk89q.worldedit.LocalSession;
import lombok.Getter;
import net.buildtheearth.modules.ModuleHandler;
import net.buildtheearth.modules.generator.GeneratorModule;
import net.buildtheearth.modules.generator.commands.GeneratorCommand;
import net.buildtheearth.modules.navigator.commands.NavigatorCommand;
import net.buildtheearth.modules.navigator.listeners.NavigatorOpenListener;
import net.buildtheearth.modules.network.ProxyModule;
import net.buildtheearth.modules.network.api.NetworkAPI;
import net.buildtheearth.modules.network.commands.BuildTeamToolsCommand;
import net.buildtheearth.modules.network.listeners.NetworkJoinListener;
import net.buildtheearth.modules.network.listeners.NetworkQuitListener;
import net.buildtheearth.modules.network.model.Permissions;
import net.buildtheearth.modules.stats.StatsModule;
import net.buildtheearth.modules.stats.StatsPlayerType;
import net.buildtheearth.modules.stats.StatsServerType;
import net.buildtheearth.modules.stats.listeners.StatsListener;
import net.buildtheearth.modules.tpll.TpllModule;
import net.buildtheearth.modules.tpll.listeners.TpllJoinListener;
import net.buildtheearth.modules.tpll.listeners.TpllListener;
import net.buildtheearth.modules.utils.io.ConfigPaths;
import net.buildtheearth.modules.utils.io.ConfigUtil;
import net.buildtheearth.modules.warp.WarpModule;
import net.buildtheearth.modules.warp.commands.WarpCommand;
import net.buildtheearth.modules.warp.listeners.WarpJoinListener;
import net.buildtheearth.modules.kml.KmlCommand;
import net.buildtheearth.modules.kml.KmlTabCompleter;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.ipvp.canvas.MenuFunctionListener;

/**
 * The parent of all modules of the Build Team Tools plugin
 */
public class BuildTeamTools {

    public static int SPIGOT_PROJECT_ID = 101854;
    public static String PREFIX = "§9§lBTE §8> §7";
    public static String CONSOLE_PREFIX = "[BuildTeamTools] ";

    private boolean debug;
    private boolean updateInstalled;
    private String newVersion;

    private long time;

    @Getter
    private GeneratorModule generatorModule;
    @Getter
    private TpllModule tpllModule;
    @Getter
    private WarpModule warpManager;
    @Getter
    private ProxyModule proxyModule;
    @Getter
    private StatsModule statsModule;

    /**
     * Tries to start up an instance of BuildTeamTools
     *
     * @return true if successful, false if unsuccessful
     */
    public boolean start() {
        // Try to load the configuration, if it throws an exception disable the plugin.
        try {
            YamlFileFactory.registerPlugin(Main.instance);
            ConfigUtil.init();
        } catch (ConfigNotImplementedException ex) {}

        // Reload the configuration file
        ConfigUtil.getInstance().reloadFiles();
        debug = Main.instance.getConfig().getBoolean(ConfigPaths.DEBUG, false);


        //Initialize the modules
        generatorModule = new GeneratorModule();
        tpllModule = new TpllModule();
        warpManager = new WarpModule();
        proxyModule = new ProxyModule();
        statsModule = new StatsModule();


        // Register Modules
        ModuleHandler.getInstance().registerModules(generatorModule, tpllModule, warpManager, proxyModule, statsModule);

        ModuleHandler.getInstance().enableAll();


        // Register an incoming & outgoing Plugin Messaging Channel
        Main.instance.getServer().getMessenger().registerOutgoingPluginChannel(Main.instance, "BungeeCord");
        Main.instance.getServer().getMessenger().registerIncomingPluginChannel(Main.instance, "BungeeCord", Main.instance);
        Main.instance.getServer().getMessenger().registerOutgoingPluginChannel(Main.instance, "BuildTeam");
        Main.instance.getServer().getMessenger().registerIncomingPluginChannel(Main.instance, "BuildTeam", Main.instance);

        // Let the network API know the BT-Tools plugin is installed
        NetworkAPI.setBuildTeamToolsInstalled(true);



        // Register all commands & listeners
        registerCommands();
        registerListeners();

        LocalSession.MAX_HISTORY_SIZE = 500;

        // Start the timer which executes operations every x time units
        startTimer();

        return true;
    }

    /**
     * Stops the instance of BuildTeamTools which is currently running
     */
    public void stop() {
        if(statsModule != null)
            statsModule.updateAndSave();
    }

    /**
     * The main Timer of the plugin that runs once a second.
     * It calls the tick() function.
     */
    private void startTimer() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.instance, () -> {
            time++;

            // Every hour
            if (time % (20 * 60 * 60) == 0) {
                // Do something
            }

            // Every 10 minutes (+1 second)
            if (time % (ProxyModule.CACHE_UPLOAD_SPEED) == 0) {
                statsModule.updateAndSave();
            }

            // Every minute
            if (time % (20 * 60) == 0) {
                statsModule.getStatsServer().addValue(StatsServerType.UPTIME, 1);

                for (Player p : Bukkit.getOnlinePlayers()) {
                    statsModule.getStatsServer().addValue(StatsServerType.PLAYTIME, 1);
                    statsModule.getStatsPlayer(p.getUniqueId()).addValue(StatsPlayerType.PLAYTIME, 1);
                }
            }

            // Every 5 seconds
            if (time % 100 == 0) {
                // Do something
            }

            // Every second
            if (time % 20 == 0) {
                // Do something
            }

            // Every 0.25 seconds
            if (time % 5 == 0) {
                // Do something
            }

            generatorModule.tick();
        }, 0, 0);
    }


    /**
     * Register all commands of the plugin.
     */
    private void registerCommands() {
        Main.instance.getCommand("buildteam").setExecutor(new BuildTeamToolsCommand());
        Main.instance.getCommand("generate").setExecutor(new GeneratorCommand());
        Main.instance.getCommand("warp").setExecutor(new WarpCommand());
        Main.instance.getCommand("navigator").setExecutor(new NavigatorCommand());
        Main.instance.getCommand("kml").setExecutor(new KmlCommand());
        Main.instance.getCommand("kml").setTabCompleter(new KmlTabCompleter());
    }

    /**
     * Register all listeners of the plugin.
     */
    private void registerListeners() {
        // Register the listener for the canvas Gui Library
        Bukkit.getPluginManager().registerEvents(new MenuFunctionListener(), Main.instance);

        Bukkit.getPluginManager().registerEvents(new StatsListener(), Main.instance);

        Bukkit.getPluginManager().registerEvents(new TpllJoinListener(), Main.instance);
        Bukkit.getPluginManager().registerEvents(new TpllListener(), Main.instance);

        Bukkit.getPluginManager().registerEvents(new WarpJoinListener(), Main.instance);

        Bukkit.getPluginManager().registerEvents(new NavigatorOpenListener(proxyModule), Main.instance);

        Bukkit.getPluginManager().registerEvents(new NetworkJoinListener(), Main.instance);
        Bukkit.getPluginManager().registerEvents(new NetworkQuitListener(), Main.instance);

    }

    /**
     * Notify a player that the plugin was updated to a newer version.
     * Only if the player has the permission buildteam.notifyUpdate
     *
     * @param p The Player to notify
     */
    public void notifyUpdate(Player p) {
        if (!updateInstalled)
            return;

        if (p.hasPermission(Permissions.NOTIFY_UPDATE)) {
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
            p.sendMessage("");
            p.sendMessage("§6§l[BuildTeam Plugin] §eThe server automatically installed a new update (v" + newVersion + ").");
            p.sendMessage("§6>> §ePlease restart or reload the server to activate it.");
            p.sendMessage("");
        }
    }

    /**
     * Sets the current version of the plugin that is installed
     *
     * @param newVersion The current installed version
     */
    public void setUpdateInstalled(String newVersion) {
        this.newVersion = newVersion;
        this.updateInstalled = true;

        for (Player p : Bukkit.getOnlinePlayers()) {
            notifyUpdate(p);
        }
    }
    // Getters & Setters


    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

}

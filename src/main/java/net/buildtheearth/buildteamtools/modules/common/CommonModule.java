package net.buildtheearth.buildteamtools.modules.common;

import com.alpsbte.alpslib.io.YamlFileFactory;
import com.alpsbte.alpslib.io.config.ConfigNotImplementedException;
import com.alpsbte.alpslib.utils.ChatHelper;
import lombok.Getter;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.Module;
import net.buildtheearth.buildteamtools.modules.common.commands.BuildTeamToolsCommand;
import net.buildtheearth.buildteamtools.modules.common.components.dependency.DependencyComponent;
import net.buildtheearth.buildteamtools.modules.common.components.pluginmessaging.PluginMessagingComponent;
import net.buildtheearth.buildteamtools.modules.common.components.updater.UpdaterComponent;
import net.buildtheearth.buildteamtools.modules.common.components.version.VersionComponent;
import net.buildtheearth.buildteamtools.modules.common.listeners.CommandListener;
import net.buildtheearth.buildteamtools.modules.common.listeners.ExceptionListener;
import net.buildtheearth.buildteamtools.modules.common.metrics.MetricsManager;
import net.buildtheearth.buildteamtools.modules.generator.GeneratorModule;
import net.buildtheearth.buildteamtools.modules.network.NetworkModule;
import net.buildtheearth.buildteamtools.modules.stats.StatsModule;
import net.buildtheearth.buildteamtools.modules.stats.model.StatsPlayerType;
import net.buildtheearth.buildteamtools.modules.stats.model.StatsServerType;
import net.buildtheearth.buildteamtools.utils.WikiLinks;
import net.buildtheearth.buildteamtools.utils.io.ConfigPaths;
import net.buildtheearth.buildteamtools.utils.io.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.ipvp.canvas.MenuFunctionListener;

public class CommonModule extends Module {


    @Getter
    private UpdaterComponent updaterComponent;
    @Getter
    private PluginMessagingComponent pluginMessagingComponent;
    @Getter
    private DependencyComponent dependencyComponent;

    @Getter
    private VersionComponent versionComponent;


    private long time;


    private static CommonModule instance = null;

    public CommonModule() {
        super("Common", WikiLinks.ENTRY);
    }

    public static CommonModule getInstance() {
        return instance == null ? instance = new CommonModule() : instance;
    }


    @Override
    public void enable() {
        // Try to load the configuration, if it throws an exception disable the plugin.
        try {
            YamlFileFactory.registerPlugin(BuildTeamTools.getInstance());
            ConfigUtil.init();
            ChatHelper.init(BuildTeamTools.getInstance(), BuildTeamTools.getInstance().isDebug(), BuildTeamTools.PREFIX, BuildTeamTools.CONSOLE_PREFIX);
        } catch (ConfigNotImplementedException ex) { // Fine?
        }

        // Reload the configuration file
        ConfigUtil.getInstance().reloadFiles();

        // Set the debug mode
        BuildTeamTools.getInstance().setDebug(BuildTeamTools.getInstance().getConfig().getBoolean(ConfigPaths.DEBUG, false));

        // Initialize the components
        updaterComponent = new UpdaterComponent(BuildTeamTools.getInstance(), BuildTeamTools.SPIGOT_PROJECT_ID, BuildTeamTools.getInstance().getPluginFile(), UpdaterComponent.UpdateType.CHECK_DOWNLOAD, BuildTeamTools.getInstance().isDebug());
        pluginMessagingComponent = new PluginMessagingComponent();
        dependencyComponent = new DependencyComponent();
        versionComponent = new VersionComponent();

        MetricsManager.init(BuildTeamTools.getInstance());

        // Start the timer
        startTimer();

        super.enable();
    }

    @Override
    public void registerCommands() {
        registerCommand("buildteamtools", new BuildTeamToolsCommand());
    }

    @Override
    public void registerListeners() {
        super.registerListeners(new MenuFunctionListener(), new CommandListener(), new ExceptionListener());
    }


    /**
     * The main Timer of the plugin that runs once a second.
     * It calls the tick() function.
     */
    private void startTimer() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(BuildTeamTools.getInstance(), () -> {
            time++;

            // Every 10 minutes (+1 second)
            if (time % (NetworkModule.CACHE_UPLOAD_SPEED) == 0 && StatsModule.getInstance().isEnabled())
                StatsModule.getInstance().updateAndSave();


            // Every minute
            if (time % (20 * 60) == 0) {
                if (StatsModule.getInstance().isEnabled()) {
                    StatsModule.getInstance().getStatsServer().addValue(StatsServerType.UPTIME, 1);

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        StatsModule.getInstance().getStatsServer().addValue(StatsServerType.PLAYTIME, 1);
                        StatsModule.getInstance().getStatsPlayer(p.getUniqueId()).addValue(StatsPlayerType.PLAYTIME, 1);
                    }
                }

                if (CommonModule.getInstance().isEnabled()) ExceptionListener.limiter = false;
            }

            GeneratorModule.getInstance().tick();
        }, 0, 0);
    }

}

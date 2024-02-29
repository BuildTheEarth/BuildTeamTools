package net.buildtheearth.modules.common;

import com.alpsbte.alpslib.io.YamlFileFactory;
import com.alpsbte.alpslib.io.config.ConfigNotImplementedException;
import lombok.Getter;
import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.Module;
import net.buildtheearth.modules.common.commands.BuildTeamToolsCommand;
import net.buildtheearth.modules.common.components.dependency.DependencyComponent;
import net.buildtheearth.modules.common.components.pluginmessaging.PluginMessagingComponent;
import net.buildtheearth.modules.common.components.updater.UpdaterComponent;
import net.buildtheearth.modules.generator.GeneratorModule;
import net.buildtheearth.modules.network.NetworkModule;
import net.buildtheearth.modules.stats.StatsModule;
import net.buildtheearth.modules.stats.model.StatsPlayerType;
import net.buildtheearth.modules.stats.model.StatsServerType;
import net.buildtheearth.modules.utils.io.ConfigUtil;
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


    private long time;


    private static CommonModule instance = null;

    public CommonModule() {
        super("Common");
    }

    public static CommonModule getInstance() {
        return instance == null ? instance = new CommonModule() : instance;
    }


    @Override
    public void onEnable() {
        // Initialize the components
        updaterComponent = new UpdaterComponent(BuildTeamTools.getInstance(), BuildTeamTools.SPIGOT_PROJECT_ID, BuildTeamTools.getInstance().getPluginFile(), UpdaterComponent.UpdateType.CHECK_DOWNLOAD, BuildTeamTools.getInstance().isDebug());
        pluginMessagingComponent = new PluginMessagingComponent();
        dependencyComponent = new DependencyComponent();

        // Try to load the configuration, if it throws an exception disable the plugin.
        try {
            YamlFileFactory.registerPlugin(BuildTeamTools.getInstance());
            ConfigUtil.init();
        } catch (ConfigNotImplementedException ex) {}

        // Reload the configuration file
        ConfigUtil.getInstance().reloadFiles();

        startTimer();

        super.onEnable();
    }

    @Override
    public void registerCommands() {
        BuildTeamTools.getInstance().getCommand("buildteam").setExecutor(new BuildTeamToolsCommand());
    }

    @Override
    public void registerListeners() {
        // Register the listener for the canvas Gui Library
        Bukkit.getPluginManager().registerEvents(new MenuFunctionListener(), BuildTeamTools.getInstance());
    }



    /**
     * The main Timer of the plugin that runs once a second.
     * It calls the tick() function.
     */
    private void startTimer() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(BuildTeamTools.getInstance(), () -> {
            time++;

            // Every hour
            if (time % (20 * 60 * 60) == 0) {
                // Do something
            }

            // Every 10 minutes (+1 second)
            if (time % (NetworkModule.CACHE_UPLOAD_SPEED) == 0) {
                if(StatsModule.getInstance().isEnabled())
                    StatsModule.getInstance().updateAndSave();
            }

            // Every minute
            if (time % (20 * 60) == 0) {
                if(StatsModule.getInstance().isEnabled()){
                    StatsModule.getInstance().getStatsServer().addValue(StatsServerType.UPTIME, 1);

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        StatsModule.getInstance().getStatsServer().addValue(StatsServerType.PLAYTIME, 1);
                        StatsModule.getInstance().getStatsPlayer(p.getUniqueId()).addValue(StatsPlayerType.PLAYTIME, 1);
                    }
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

            GeneratorModule.getInstance().tick();
        }, 0, 0);
    }

}

package net.buildtheearth.buildteamtools;

import com.alpsbte.alpslib.utils.ChatHelper;
import lombok.Getter;
import net.buildtheearth.buildteamtools.modules.ModuleHandler;
import net.buildtheearth.buildteamtools.modules.common.CommonModule;
import net.buildtheearth.buildteamtools.modules.generator.GeneratorModule;
import net.buildtheearth.buildteamtools.modules.miscellaneous.MiscModule;
import net.buildtheearth.buildteamtools.modules.navigation.NavigationModule;
import net.buildtheearth.buildteamtools.modules.network.NetworkModule;
import net.buildtheearth.buildteamtools.modules.plotsystem.PlotSystemModule;
import net.buildtheearth.buildteamtools.modules.stats.StatsModule;
import net.buildtheearth.buildteamtools.utils.io.ConfigurationLoader;
import net.buildtheearth.buildteamtools.utils.io.GeneratorConfig;
import net.buildtheearth.buildteamtools.utils.io.MainConfig;
import net.buildtheearth.buildteamtools.utils.io.NavigationConfig;
import net.buildtheearth.buildteamtools.utils.io.PlotSystemConfig;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The parent of all modules of the Build Team Tools plugin
 */
public class BuildTeamTools extends JavaPlugin {

    public static final String PREFIX = "&9&lBTE &8> &7";
    public static final String CONSOLE_PREFIX = "[BuildTeamTools] ";

    @Getter
    private boolean debug;

    @Getter
    private static BuildTeamTools instance = null;

    private World earthWorld;
    private ConfigurationLoader<MainConfig> mainConfigLoader;
    private ConfigurationLoader<NavigationConfig> navigationConfigLoader;
    private ConfigurationLoader<GeneratorConfig> generatorConfigLoader;
    private ConfigurationLoader<PlotSystemConfig> plotSystemConfigLoader;

    @Override
    public void onEnable() {
        instance = this;
        mainConfigLoader = new ConfigurationLoader<>(MainConfig.class, getDataFolder().toPath(), getSLF4JLogger());
        navigationConfigLoader = new ConfigurationLoader<>(NavigationConfig.class,
                getDataFolder().toPath().resolve("modules/navigation"), getSLF4JLogger());
        generatorConfigLoader = new ConfigurationLoader<>(GeneratorConfig.class,
                getDataFolder().toPath().resolve("modules/generator"), getSLF4JLogger());
        plotSystemConfigLoader = new ConfigurationLoader<>(PlotSystemConfig.class,
                getDataFolder().toPath().resolve("modules/plotsystem"), getSLF4JLogger());
        MainConfig mainConfig = mainConfigLoader.load();
        NavigationConfig navigationConfig = navigationConfigLoader.load();
        GeneratorConfig generatorConfig = generatorConfigLoader.load();
        plotSystemConfigLoader.load();

        // Register Modules
        ModuleHandler.getInstance().replaceModules(
                new CommonModule(mainConfig),
                new NetworkModule(mainConfig),
                new GeneratorModule(generatorConfig),
                new NavigationModule(navigationConfig),
                MiscModule.getInstance(),
                StatsModule.getInstance(),
                PlotSystemModule.getInstance()
        );
        ModuleHandler.getInstance().enableAll(null);
    }

    @Override
    public void onDisable() {
        ModuleHandler.getInstance().disableAll(null);
    }


    @Override
    public void reloadConfig() {
        mainConfigLoader.reload();
        navigationConfigLoader.reload();
        generatorConfigLoader.reload();
        plotSystemConfigLoader.reload();
    }

    public MainConfig getMainConfig() {
        return mainConfigLoader.getConfig();
    }

    @Override
    public void saveConfig() {
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
        ChatHelper.DEBUG = debug;
    }

    public World getEarthWorld() {
        if (earthWorld != null)
            return earthWorld;

        String worldName = getMainConfig().earthWorld();
        if (worldName == null || worldName.isEmpty())
            return null;

        earthWorld = Bukkit.getWorld(worldName);
        return earthWorld;
    }
}

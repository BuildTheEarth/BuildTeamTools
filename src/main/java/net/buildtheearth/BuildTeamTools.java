package net.buildtheearth;

import lombok.Getter;
import lombok.Setter;
import net.buildtheearth.modules.ModuleHandler;
import net.buildtheearth.modules.common.CommonModule;
import net.buildtheearth.modules.generator.GeneratorModule;
import net.buildtheearth.modules.miscellaneous.MiscModule;
import net.buildtheearth.modules.navigation.NavigationModule;
import net.buildtheearth.modules.network.NetworkModule;
import net.buildtheearth.modules.plotsystem.PlotSystemModule;
import net.buildtheearth.modules.stats.StatsModule;
import net.buildtheearth.utils.io.ConfigUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * The parent of all modules of the Build Team Tools plugin
 */
public class BuildTeamTools extends JavaPlugin {

    public static int SPIGOT_PROJECT_ID = 101854;
    public static String PREFIX = "§9§lBTE §8> §7";
    public static String CONSOLE_PREFIX = "[BuildTeamTools] ";

    @Getter @Setter
    private boolean debug;

    @Getter
    private static BuildTeamTools instance = null;


    @Override
    public void onEnable() {
        instance = this;

        // Register Modules

        // We need to register the Common Module first to have the Config System Available for other modules/logic.
        ModuleHandler.getInstance().registerModule(CommonModule.getInstance());
        ModuleHandler.getInstance().enable(CommonModule.getInstance(), null);

        ModuleHandler.getInstance().registerModules(
                NetworkModule.getInstance(),
                GeneratorModule.getInstance(),
                NavigationModule.getInstance(),
                PlotSystemModule.getInstance(),
                StatsModule.getInstance(),
                MiscModule.getInstance()
        );
        ModuleHandler.getInstance().enableAll(null);
    }

    @Override
    public void onDisable() {
        try {
            ModuleHandler.getInstance().disableAll(null);
        } catch (NoClassDefFoundError ignored) {}
    }


    @Override
    public @NotNull FileConfiguration getConfig() {
        return getConfig(ConfigUtil.MAIN);
    }

    public FileConfiguration getConfig(ConfigUtil configType) {
        if(ConfigUtil.getInstance() == null)
            return null;

        return ConfigUtil.getInstance().configs[configType.ordinal()];
    }

    @Override
    public void reloadConfig() {
        ConfigUtil.getInstance().reloadFiles();
    }

    @Override
    public void saveConfig() {
        ConfigUtil.getInstance().saveFiles();
    }

    public File getPluginFile() {
        return this.getFile();
    }
}
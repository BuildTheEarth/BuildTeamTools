package net.buildtheearth;

import lombok.Getter;
import lombok.Setter;
import net.buildtheearth.modules.ModuleHandler;
import net.buildtheearth.modules.common.CommonModule;
import net.buildtheearth.modules.generator.GeneratorModule;
import net.buildtheearth.modules.navigation.NavigationModule;
import net.buildtheearth.modules.network.NetworkModule;
import net.buildtheearth.modules.stats.StatsModule;
import net.buildtheearth.modules.utils.io.ConfigPaths;
import net.buildtheearth.modules.utils.io.ConfigUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

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

    private static BuildTeamTools instance = null;
    public static BuildTeamTools getInstance() {
        return instance == null ? instance = new BuildTeamTools() : instance;
    }



    @Override
    public void onEnable() {
        debug = BuildTeamTools.getInstance().getConfig().getBoolean(ConfigPaths.DEBUG, false);

        // Register Modules
        ModuleHandler.getInstance().registerModules(
                CommonModule.getInstance(),
                GeneratorModule.getInstance(),
                NavigationModule.getInstance(),
                NetworkModule.getInstance(),
                StatsModule.getInstance()
        );
        ModuleHandler.getInstance().enableAll();

    }

    @Override
    public void onDisable() {
        ModuleHandler.getInstance().disableAll();
    }


    @Override
    public FileConfiguration getConfig() {
        return ConfigUtil.getInstance().configs[0];
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

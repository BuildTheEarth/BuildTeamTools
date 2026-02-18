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
import net.buildtheearth.buildteamtools.utils.io.ConfigUtil;
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

    @Getter
    private boolean debug;

    @Getter
    private static BuildTeamTools instance = null;


    @Override
    public void onEnable() {
        instance = this;

        // Register Modules
        ModuleHandler.getInstance().registerModules(
                CommonModule.getInstance(),
                NetworkModule.getInstance(),
                GeneratorModule.getInstance(),
                NavigationModule.getInstance(),
                MiscModule.getInstance(),
                StatsModule.getInstance(),
                PlotSystemModule.getInstance()
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

    public void setDebug(boolean debug) {
        this.debug = debug;
        ChatHelper.DEBUG = debug;
    }
}
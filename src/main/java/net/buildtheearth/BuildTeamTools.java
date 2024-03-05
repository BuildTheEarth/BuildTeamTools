package net.buildtheearth;

import lombok.Getter;
import lombok.Setter;
import net.buildtheearth.modules.ModuleHandler;
import net.buildtheearth.modules.common.CommonModule;
import net.buildtheearth.modules.generator.GeneratorModule;
import net.buildtheearth.modules.navigation.NavigationModule;
import net.buildtheearth.modules.network.NetworkModule;
import net.buildtheearth.modules.plotsystem.PlotSystemModule;
import net.buildtheearth.modules.stats.StatsModule;
import net.buildtheearth.utils.io.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

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

        Bukkit.getLogger().log(Level.INFO, "§e--------------- BuildTeamTools V" + getDescription().getVersion() + " ----------------");
        Bukkit.getLogger().log(Level.INFO, " ");

        // Register Modules
        ModuleHandler.getInstance().registerModules(
            CommonModule.getInstance(),
            NetworkModule.getInstance(),
            GeneratorModule.getInstance(),
            NavigationModule.getInstance(),
            PlotSystemModule.getInstance(),
            StatsModule.getInstance()
        );
        ModuleHandler.getInstance().enableAll(null, true);

        Bukkit.getLogger().log(Level.INFO," ");
        Bukkit.getLogger().log(Level.INFO,"§e------------------------------------------------------------");
        Bukkit.getLogger().log(Level.INFO,"§8> §7Made by §bBuildTheEarth");
        Bukkit.getLogger().log(Level.INFO,"§8> §7GitHub:§f https://github.com/BuildTheEarth/BuildTeamTools");
        Bukkit.getLogger().log(Level.INFO,"§e------------------------------------------------------------");
    }

    @Override
    public void onDisable() {
        ModuleHandler.getInstance().disableAll(null);
    }


    @Override
    public FileConfiguration getConfig() {
        if(ConfigUtil.getInstance() == null)
            return null;

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

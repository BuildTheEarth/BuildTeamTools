package net.buildtheearth.modules.plotsystem;

import java.util.logging.Level;

import org.bukkit.Bukkit;

import com.alpsbte.alpslib.libpsterra.core.Connection;
import com.alpsbte.alpslib.libpsterra.core.PSTerraSetup;
import com.alpsbte.alpslib.libpsterra.core.config.ConfigManager;
import com.alpsbte.alpslib.libpsterra.core.plotsystem.PlotCreator;
import com.alpsbte.alpslib.libpsterra.core.plotsystem.PlotPaster;

import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.Module;
import net.buildtheearth.modules.common.CommonModule;
import net.buildtheearth.modules.plotsystem.commands.PlotSystemTerraCommand;

public class PlotSystemModule extends Module {


    private static PlotSystemModule instance = null;

    private ConfigManager configManager;
    private PlotPaster plotPaster;

    private boolean pluginEnabled = false;
    public String version;
    public String newVersion;
    public boolean updateInstalled = false;


    private Connection connection;
    private PlotCreator plotCreator;

    public PlotSystemModule() {
        super("PlotSystem");
    }

    public static PlotSystemModule getInstance() {
        return instance == null ? instance = new PlotSystemModule() : instance;
    }

    @Override
    public void enable() {
        // Check if WorldEdit is enabled
        if (!CommonModule.getInstance().getDependencyComponent().isWorldEditEnabled()) {
            shutdown("§cWorldEdit is not installed.");
            return;
        }

        // Check if HeadDatabase is enabled
        if (!CommonModule.getInstance().getDependencyComponent().isWorldEditEnabled()) {
            shutdown("§cHeadDatabase is not installed.");
            return;
        }
        BuildTeamTools plugin = BuildTeamTools.getInstance();
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog"); // Disable Logging

        version = plugin.getDescription().getVersion();

        try {
            PSTerraSetup setup = PSTerraSetup.setupPlugin(plugin, version);
            this.connection = setup.connection;
            this.plotCreator = setup.plotCreator;
            this.plotPaster = setup.plotPaster;
            this.configManager = setup.configManager;
        } catch (Exception ex) {
            Bukkit.getConsoleSender().sendMessage("Error setting up PlotSystemTerra: " + ex.getMessage());
            Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);

            shutdown("Error setting up PlotSystemTerra from AlpsLib.");
            return;
        }



        

        Bukkit.getConsoleSender().sendMessage("Enabled Plot-System-Terra module of BuildTeamTools.");


        super.enable();
    }


    @Override
    public void registerCommands() {
        registerCommand("plotsystemterra", new PlotSystemTerraCommand());
    }

    @Override
    public void registerListeners() {}


}

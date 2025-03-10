package net.buildtheearth.modules.plotsystem;


import com.alpsbte.alpslib.libpsterra.core.Connection;
import com.alpsbte.alpslib.libpsterra.core.PSTerraSetup;
import com.alpsbte.alpslib.libpsterra.core.config.ConfigManager;
import com.alpsbte.alpslib.libpsterra.core.config.PSInitializer;
import com.alpsbte.alpslib.libpsterra.core.plotsystem.PlotCreator;
import com.alpsbte.alpslib.libpsterra.core.plotsystem.PlotPaster;
import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.Module;
import net.buildtheearth.modules.common.CommonModule;
import net.buildtheearth.modules.network.NetworkModule;
import net.buildtheearth.modules.plotsystem.commands.PlotSystemTerraCommand;
import net.buildtheearth.utils.ChatHelper;
import net.buildtheearth.utils.io.ConfigPaths;
import net.buildtheearth.utils.io.ConfigUtil;
import net.buildtheearth.utils.io.Constants;
import net.buildtheearth.utils.io.Errors;
import org.bukkit.Bukkit;

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
            shutdown(Errors.WORLD_EDIT_NOT_INSTALLED);
            return;
        }

        // Check if HeadDatabase is enabled
        if (!CommonModule.getInstance().getDependencyComponent().isHeadDatabaseEnabled()) {
            shutdown(Errors.HEAD_DATABASE_NOT_INSTALLED);
            return;
        }

        // If the DataMode is set to API, check if the NetworkModule is enabled
        String dataMode = BuildTeamTools.getInstance().getConfig(ConfigUtil.PLOTSYSTEM).getString(ConfigPaths.PlotSystem.DATA_MODE);
        if (dataMode == null || dataMode.equalsIgnoreCase("API")) {

            // Check if the API Key is configured
            String API_KEY = BuildTeamTools.getInstance().getConfig().getString(ConfigPaths.API_KEY);
            if(API_KEY == null || API_KEY.isEmpty() || API_KEY.equals(Constants.DEFAULT_API_KEY)){
                shutdown(Errors.API_KEY_NOT_CONFIGURED);
                return;
            }

            // Check if the NetworkModule is enabled
            if (!NetworkModule.getInstance().isEnabled()) {
                shutdown(Errors.BUILD_TEAM_NOT_LOADED);
                return;
            }
        }

        // If the config world is not loaded, disable the module
        String world = BuildTeamTools.getInstance().getConfig().getString(ConfigPaths.EARTH_WORLD);
        if (world == null || Bukkit.getWorld(world) == null) {
            shutdown(Errors.WORLD_NOT_LOADED);
            return;
        }

        BuildTeamTools plugin = BuildTeamTools.getInstance();
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog"); // Disable Logging

        version = plugin.getDescription().getVersion();

        try {
            PSTerraSetup setup = PSTerraSetup.setupPlugin(plugin, version, new PSInitializer(plugin,
                false,
                false,
                BuildTeamTools.getInstance().isDebug(),
                world,
                false,
                Constants.PLOTSYSTEM_FOLDER,
                Constants.CONFIG_FILE,
                Constants.CONFIG_FILE,
                Constants.API_URL,
                Constants.API_PORT,
                BuildTeamTools.getInstance().getConfig().getString(ConfigPaths.API_KEY)
            ));

            this.connection = setup.connection;
            this.plotCreator = setup.plotCreator;
            this.plotPaster = setup.plotPaster;
            this.configManager = setup.configManager;

        } catch (Exception | NoClassDefFoundError ex) {
            ChatHelper.logDebug("Error setting up PlotSystemTerra: " + ex.getMessage());
            if(BuildTeamTools.getInstance().isDebug())
                ex.printStackTrace();

            shutdown("Error setting up PlotSystemTerra from AlpsLib: " + ex.getMessage());
            return;
        }

        super.enable();
    }


    @Override
    public void registerCommands() {
        registerCommand("plotsystemterra", new PlotSystemTerraCommand());
    }

    @Override
    public void registerListeners() {}


}

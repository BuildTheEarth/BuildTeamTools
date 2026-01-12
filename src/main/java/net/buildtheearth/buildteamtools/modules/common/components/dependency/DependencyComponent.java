package net.buildtheearth.buildteamtools.modules.common.components.dependency;

import com.sk89q.worldedit.WorldEdit;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.ModuleComponent;

import java.util.Objects;

/**
 * This class handles checking if the required dependencies for certain features are present
 */
public class DependencyComponent extends ModuleComponent {


    public DependencyComponent() {
        super("Dependencies");
    }



    /**
     * Checks if the plugin "WorldEdit" is enabled.
     *
     * @return True if WorldEdit is enabled, false if it is not enabled
     */
    public boolean isWorldEditEnabled() {
        return BuildTeamTools.getInstance().getServer().getPluginManager().isPluginEnabled("WorldEdit");
    }

    public boolean isLegacyWorldEdit(){
        if(isFastAsyncWorldEditEnabled())
            return false;
        if(!isWorldEditEnabled())
            return false;

        String version = Objects.requireNonNull(BuildTeamTools.getInstance().getServer().getPluginManager()
                .getPlugin("WorldEdit"), "World Edit was not found!").getPluginMeta().getVersion();
        int major = Integer.parseInt(version.split("\\.")[0]);

        return major < 7;
    }

    /**
     * Checks if the plugin "FastAsyncWorldEdit" is enabled.
     *
     * @return True if FastAsyncWorldEdit is enabled, false if it is not enabled
     */
    public boolean isFastAsyncWorldEditEnabled() {
        return BuildTeamTools.getInstance().getServer().getPluginManager().isPluginEnabled("FastAsyncWorldEdit");
    }

    /**
     * Checks if the plugin "SchematicBrush" is enabled.
     *
     * @return True if SchematicBrush is enabled, false if it is not enabled
     */
    public boolean isSchematicBrushEnabled() {
        return BuildTeamTools.getInstance().getServer().getPluginManager().isPluginEnabled("SchematicBrushReborn");
    }

    /**
     * Checks if the plugin "HeadDatabase" is enabled.
     *
     * @return True if HeadDatabase is enabled, false if it is not enabled
     */
    public boolean isHeadDatabaseEnabled() {
        return BuildTeamTools.getInstance().getServer().getPluginManager().isPluginEnabled("HeadDatabase");
    }

    /**
     * Checks if the plugin <a href="https://github.com/AlpsBTE/Plot-System-Terra">PlotSystem-Terra</a> by BTE Alps is already enabled.
     *
     * @return True if PlotSystemTerra is enabled, false if it is not enabled
     */
    public boolean isPlotSystemTerraEnabled() {
        return BuildTeamTools.getInstance().getServer().getPluginManager().isPluginEnabled("PlotSystem-Terra");
    }

    /**
     * Get an instance of WorldEdit
     *
     * @return An instance of WorldEdit
     */
    public WorldEdit getWorldEdit() {
        return WorldEdit.getInstance();
    }
}

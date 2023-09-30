package net.buildtheearth.modules.updater;

import com.sk89q.worldedit.WorldEdit;
import net.buildtheearth.Main;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles checking if the required dependencies for certain features are present
 */
public class DependencyManager {

    // List which contains all missing dependencies
    private final static List<String> missingDependencies = new ArrayList<>();

    /**
     * Check for all required dependencies and inform the console about missing dependencies
     * @return True if all dependencies are present
     */
    private static boolean checkForRequiredDependencies() {
        PluginManager pluginManager = Main.instance.getServer().getPluginManager();

        if (!pluginManager.isPluginEnabled("WorldEdit")) {
            missingDependencies.add("WorldEdit (V6.1.9)");
        }

        return missingDependencies.isEmpty();
    }

    /**
     * Checks if the plugin "WorldEdit" is enabled.
     * @return True if WorldEdit is enabled, false if it is not enabled
     */
    public static boolean isWorldEditEnabled() {
        return Main.instance.getServer().getPluginManager().isPluginEnabled("WorldEdit");
    }

    /**
     * Checks if the plugin "SchematicBrush" is enabled.
     * @return True if SchematicBrush is enabled, false if it is not enabled
     */
    public static boolean isSchematicBrushEnabled() {
        return Main.instance.getServer().getPluginManager().isPluginEnabled("SchematicBrush");
    }

    /**
     * Get an instance of WorldEdit
     * @return An instance of WorldEdit
     */
    public static WorldEdit getWorldEdit() {
        return WorldEdit.getInstance();
    }
}

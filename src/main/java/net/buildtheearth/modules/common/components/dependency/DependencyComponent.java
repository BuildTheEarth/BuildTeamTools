package net.buildtheearth.modules.common.components.dependency;

import com.sk89q.worldedit.WorldEdit;
import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.Component;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles checking if the required dependencies for certain features are present
 */
public class DependencyComponent extends Component {

    // List which contains all missing dependencies
    private final List<String> missingDependencies = new ArrayList<>();

    public DependencyComponent() {
        super("Dependencies");
    }


    /**
     * Check for all required dependencies and inform the console about missing dependencies
     *
     * @return True if all dependencies are present
     */
    private boolean checkForRequiredDependencies() {
        PluginManager pluginManager = BuildTeamTools.getInstance().getServer().getPluginManager();

        if (!pluginManager.isPluginEnabled("WorldEdit")) {
            missingDependencies.add("WorldEdit (V6.1.9)");
        }

        return missingDependencies.isEmpty();
    }

    /**
     * Checks if the plugin "WorldEdit" is enabled.
     *
     * @return True if WorldEdit is enabled, false if it is not enabled
     */
    public boolean isWorldEditEnabled() {
        return BuildTeamTools.getInstance().getServer().getPluginManager().isPluginEnabled("WorldEdit");
    }

    /**
     * Checks if the plugin "SchematicBrush" is enabled.
     *
     * @return True if SchematicBrush is enabled, false if it is not enabled
     */
    public boolean isSchematicBrushEnabled() {
        return BuildTeamTools.getInstance().getServer().getPluginManager().isPluginEnabled("SchematicBrush");
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

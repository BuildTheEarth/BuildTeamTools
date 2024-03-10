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
        return BuildTeamTools.getInstance().getServer().getPluginManager().isPluginEnabled("SchematicBrush");
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
     * Get an instance of WorldEdit
     *
     * @return An instance of WorldEdit
     */
    public WorldEdit getWorldEdit() {
        return WorldEdit.getInstance();
    }
}

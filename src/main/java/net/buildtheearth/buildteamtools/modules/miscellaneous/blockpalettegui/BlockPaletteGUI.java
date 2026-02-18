package net.buildtheearth.buildteamtools.modules.miscellaneous.blockpalettegui;

import net.buildtheearth.buildteamtools.modules.ModuleComponent;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockPaletteGUI extends ModuleComponent {

    private static final String COMPONENT_NAME = "BlockPaletteGUI";

    private final JavaPlugin plugin;
    private final BlockPaletteManager manager;

    public BlockPaletteGUI(JavaPlugin plugin) {
        super(COMPONENT_NAME);
        this.plugin = plugin;
        this.manager = new BlockPaletteManager(plugin);
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public BlockPaletteManager getManager() {
        return manager;
    }

    @Override
    public void enable() {
        super.enable();
    }

    @Override
    public void disable() {
        if (!isEnabled()) return;
        super.disable();
    }
}

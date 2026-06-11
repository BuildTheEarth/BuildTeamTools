package net.buildtheearth.buildteamtools.modules.miscellaneous.blockpalettegui;

import lombok.Getter;
import net.buildtheearth.buildteamtools.modules.ModuleComponent;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class BlockPaletteGUI extends ModuleComponent {

    private static final String COMPONENT_NAME = "BlockPaletteGUI";

    private final JavaPlugin plugin;
    private final BlockPaletteManager manager;

    public BlockPaletteGUI(JavaPlugin plugin) {
        super(COMPONENT_NAME);
        this.plugin = plugin;
        this.manager = new BlockPaletteManager(plugin);
    }
}

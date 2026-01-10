package net.buildtheearth.modules.miscellaneous;

import net.buildtheearth.modules.Module;
import net.buildtheearth.modules.miscellaneous.blockpalettegui.BlockPaletteCommand;
import net.buildtheearth.modules.miscellaneous.blockpalettegui.BlockPaletteGUI;
import net.buildtheearth.modules.miscellaneous.blockpalettegui.BlockPaletteManager;
import net.buildtheearth.utils.WikiLinks;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.Constructor;

public class MiscModule extends Module {

    private BlockPaletteGUI blockPaletteGUI;
    private static MiscModule instance = null;

    public MiscModule() {
        super("Misc", WikiLinks.MISC);
    }

    public static MiscModule getInstance() {
        return instance == null ? instance = new MiscModule() : instance;
    }

    @Override
    public void enable() {
        super.enable();

        JavaPlugin plugin = resolvePlugin();

        blockPaletteGUI = new BlockPaletteGUI(plugin);
        blockPaletteGUI.enable();

        registerCommandSafely(plugin, "blockpalette", blockPaletteGUI.getManager());
    }

    @Override
    public void disable() {
        if (!isEnabled()) return;

        if (blockPaletteGUI != null) {
            blockPaletteGUI.disable();
            blockPaletteGUI = null;
        }

        super.disable();
    }

    @Override
    public void registerListeners() {
        // No Listeners
    }

    private @NonNull JavaPlugin resolvePlugin() {
        try {
            return JavaPlugin.getProvidingPlugin(MiscModule.class);
        } catch (Exception t) {
            throw new IllegalStateException("Cannot resolve JavaPlugin for MiscModule", t);
        }
    }

    private void registerCommandSafely(@NonNull JavaPlugin plugin, String name, BlockPaletteManager manager) {
        PluginCommand cmd = plugin.getCommand(name);
        if (cmd == null) {
            return;
        }

        Object obj = createCommandInstance(manager, plugin);
        if (obj instanceof CommandExecutor exec) cmd.setExecutor(exec);
        if (obj instanceof TabCompleter tab) cmd.setTabCompleter(tab);
    }

    private @NonNull Object createCommandInstance(BlockPaletteManager manager, JavaPlugin plugin) {
        try {
            Class<?> c = BlockPaletteCommand.class;
            Constructor<?> k;

            try {
                k = c.getConstructor(BlockPaletteManager.class, JavaPlugin.class);
                return k.newInstance(manager, plugin);
            } catch (NoSuchMethodException ignored) {}

            try {
                k = c.getConstructor(BlockPaletteManager.class);
                return k.newInstance(manager);
            } catch (NoSuchMethodException ignored) {}

            try {
                k = c.getConstructor(JavaPlugin.class);
                return k.newInstance(plugin);
            } catch (NoSuchMethodException ignored) {}

            return c.getDeclaredConstructor().newInstance();
        } catch (Exception t) {
            throw new RuntimeException("Cannot construct BlockPaletteCommand", t);
        }
    }
}

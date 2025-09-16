package net.buildtheearth.modules.blockpalletegui;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;

public final class BlockPaletteModule {

    private static BlockPaletteModule INSTANCE;
    private static JavaPlugin PLUGIN;

    private BlockPalletManager manager;

    private BlockPaletteModule() {}

    public static void initialize(JavaPlugin plugin) {
        PLUGIN = plugin;
        INSTANCE = new BlockPaletteModule();
        INSTANCE.enable();
    }

    public static BlockPaletteModule getInstance() { return INSTANCE; }

    public void enable() {
        this.manager = new BlockPalletManager(PLUGIN);

        registerCommandSafely("blockpalette");
    }

    public void disable() {}

    public BlockPalletManager getManager() { return manager; }

    private void registerCommandSafely(String name) {
        PluginCommand cmd = PLUGIN.getCommand(name);
        if (cmd == null) return;

        Object obj = createCommandInstance();
        if (obj instanceof CommandExecutor exec) cmd.setExecutor(exec);
        if (obj instanceof TabCompleter tab) cmd.setTabCompleter(tab);
    }

    private Object createCommandInstance() {
        try {
            Class<?> c = BlockPalletCommand.class;
            Constructor<?> k;
            try {
                k = c.getConstructor(BlockPalletManager.class, JavaPlugin.class);
                return k.newInstance(manager, PLUGIN);
            } catch (NoSuchMethodException ignored) {}
            try {
                k = c.getConstructor(BlockPalletManager.class);
                return k.newInstance(manager);
            } catch (NoSuchMethodException ignored) {}
            try {
                k = c.getConstructor(JavaPlugin.class);
                return k.newInstance(PLUGIN);
            } catch (NoSuchMethodException ignored) {}
            return c.getDeclaredConstructor().newInstance();
        } catch (Throwable t) {
            throw new RuntimeException("Cannot construct BlockPalletCommand", t);
        }
    }
}

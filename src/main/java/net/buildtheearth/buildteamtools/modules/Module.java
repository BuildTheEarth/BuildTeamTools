package net.buildtheearth.buildteamtools.modules;

import com.alpsbte.alpslib.utils.ChatHelper;
import com.alpsbte.alpslib.utils.WikiDocumented;
import lombok.Getter;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An interface for BuildTeamTools modules
 *
 * @author MineFact, Noah Husby
 */
public abstract class Module implements WikiDocumented {

    @Getter
    private boolean enabled = false;

    @Getter
    private String error;

    @Getter
    private final String moduleName;


    @Getter
    private final List<Listener> listeners = new ArrayList<>();

    @Getter
    private final List<Module> dependsOnModules = new ArrayList<>();

    @Getter
    private final String wikiPage;


    /**
     * Initializes a new module.
     *
     * @param moduleName       The name of the module
     * @param wikiPage         The wiki page of the module
     * @param dependsOnModules The modules that this module depends on. If any of these modules are disabled, this module will
     *                         be disabled as well.
     */
    protected Module(String moduleName, String wikiPage, Module... dependsOnModules) {
        this.moduleName = moduleName;
        this.dependsOnModules.addAll(Arrays.asList(dependsOnModules));
        this.wikiPage = wikiPage;

        registerCommands();
        registerListeners();
    }


    /**
     * Enables the module
     */
    public void enable() {
        checkForModuleDependencies();

        loadListeners();

        enabled = true;
    }

    /**
     * Disables the module
     */
    public void disable() {
        unregisterListeners();

        enabled = false;
    }

    /**
     * Shuts down the module with a reason.
     * This can be used to disable the module if it encounters an error.
     *
     * @param reason The reason for the shutdown like an error message
     */
    public void shutdown(String reason) {
        this.error = reason;

        if (isEnabled())
            ChatHelper.logError("The %s Module crashed because of following error: " + reason, moduleName);

        disable();
    }


    /**
     * Registers commands for the module.
     * Note that this method will only register the commands in the module, but it won't load them in Bukkit.
     * To load the commands, use the loadCommands() method.
     */
    protected void registerCommands() {
    }



    /**
     * Registers listeners for the module.
     * Note that this method will only register the listeners in the module, but it won't load them in Bukkit.
     * To load the listeners, use the loadListeners() method.
     */
    public void registerListeners(Listener... listeners) {
        this.listeners.addAll(Arrays.asList(listeners));
    }

    public abstract void registerListeners();

    /**
     * Loads the listeners for the module into Bukkit
     */
    private void loadListeners() {
        for (Listener listener : listeners)
            if (listener != null)
                Bukkit.getPluginManager().registerEvents(listener, BuildTeamTools.getInstance());
    }

    /**
     * Unregisters all listeners from Bukkit and removes them from the module
     */
    private void unregisterListeners() {
        for (Listener listener : listeners)
            if (listener != null)
                HandlerList.unregisterAll(listener);

        listeners.clear();
    }


    /**
     * Checks if the module has all its dependencies enabled
     * If not, it will disable the module and log an error.
     */
    protected void checkForModuleDependencies() {
        for (Module module : dependsOnModules)
            if (!module.isEnabled()) {
                String moduleDepsError = "The " + module.getModuleName() + " Module is currently disabled.";

                if (module.getError() != null && !module.getError().isEmpty())
                    moduleDepsError = module.getError();

                shutdown(moduleDepsError);
                return;
            }
    }
}

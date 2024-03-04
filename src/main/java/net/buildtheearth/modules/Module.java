package net.buildtheearth.modules;

import lombok.Getter;
import net.buildtheearth.BuildTeamTools;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * An interface for BuildTeamTools modules
 *
 * @authors MineFact, Noah Husby
 */
public abstract class Module {

    @Getter
    private boolean enabled = false;

    @Getter
    private String error;

    @Getter
    private final String moduleName;


    @Getter
    private final List<Listener> listeners = new ArrayList<>();

    @Getter
    private final HashMap<PluginCommand, CommandExecutor> commands = new HashMap<>();

    @Getter
    private final List<Module> dependsOnModules = new ArrayList<>();


    /** Initializes a new module.
     *
     * @param moduleName The name of the module
     * @param dependsOnModules The modules that this module depends on. If any of these modules are disabled, this module will be disabled as well.
     */
    protected Module(String moduleName, Module... dependsOnModules) {
        this.moduleName = moduleName;
        this.dependsOnModules.addAll(Arrays.asList(dependsOnModules));

        registerCommands();
        registerListeners();
    }


    /** Enables the module */
    public void enable(){
        checkForModuleDependencies();

        loadCommands();
        loadListeners();

        enabled = true;
    }

    /** Disables the module */
    public void disable(){
        unregisterListeners();

        enabled = false;
    }

    /** Shuts down the module with a reason.
     *  This can be used to disable the module if it encounters an error.
     *
     * @param reason The reason for the shutdown like an error message
     */
    public void shutdown(String reason){
        this.error = reason;

        disable();
    }



    /** Registers commands for the module.
     * Note that this method will only register the commands in the module, but it won't load them in Bukkit.
     * To load the commands, use the loadCommands() method.
     */
    protected void registerCommands(){}

    /** Registers a command for the module.
     * Note that this method will only register the command in the module, but it won't load it in Bukkit.
     * To load the command, use the loadCommands() method.
     *
     * @param command The command to register
     * @param executor The executor for the command
     */
    protected void registerCommand(String command, CommandExecutor executor){
        commands.put(BuildTeamTools.getInstance().getCommand(command), executor);
    }

    /** Loads the commands for the module into Bukkit */
    private void loadCommands(){
        for(PluginCommand command : commands.keySet())
            command.setExecutor(commands.get(command));
    }


    /** Registers listeners for the module.
     * Note that this method will only register the listeners in the module, but it won't load them in Bukkit.
     * To load the listeners, use the loadListeners() method.
     */
    public void registerListeners(Listener... listeners){
        this.listeners.addAll(Arrays.asList(listeners));
    }

    public abstract void registerListeners();

    /** Loads the listeners for the module into Bukkit */
    private void loadListeners(){
        for(Listener listener : listeners)
            Bukkit.getPluginManager().registerEvents(listener, BuildTeamTools.getInstance());
    }

    /** Unregisters all listeners from Bukkit and removes them from the module */
    private void unregisterListeners(){
        for(Listener listener : listeners)
            HandlerList.unregisterAll(listener);

        listeners.clear();
    }


    /** Checks if the module has all its dependencies enabled
     * If not, it will disable the module and log an error.
     */
    protected void checkForModuleDependencies(){
        for(Module module : dependsOnModules)
            if(!module.isEnabled()) {
                String error = "The " + module.getModuleName() + " Module is currently disabled.";

                if(module.getError() != null && !module.getError().isEmpty())
                    error = module.getError();

                shutdown(error);
                return;
            }
    }
}

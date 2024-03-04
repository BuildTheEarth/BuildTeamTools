package net.buildtheearth.modules;

import lombok.Getter;
import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.utils.ChatHelper;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * @author Noah Husby
 */
public class ModuleHandler {
    private static ModuleHandler instance = null;

    public static ModuleHandler getInstance() {
        return instance == null ? instance = new ModuleHandler() : instance;
    }

    private ModuleHandler() {
    }

    @Getter
    private final List<Module> modules = new ArrayList<>();

    /**
     * Register a new module
     *
     * @param module {@link Module}
     */
    public void registerModule(Module module) {
        modules.add(module);
    }

    /**
     * Registers an array of modules
     *
     * @param modules {@link Module}
     */
    public void registerModules(Module... modules) {
        for (Module m : modules)
            registerModule(m);
    }

    /**
     * Enables a specific module
     *
     * @param module {@link Module}
     * @param isStarting if the server is starting
     * @return True if successfully enabled, false if not
     */
    public boolean enable(Module module, boolean isStarting) {
        for(Module m : modules)
            if(m.getModuleName().equals(module.getModuleName()) && m.isEnabled())
                return false;

        boolean containsDisabledDependencyModule = false;
        for(Module m : module.getDependsOnModules())
            if(!m.isEnabled()) {
                module.checkForModuleDependencies();
                containsDisabledDependencyModule = true;
            }

        try{
            if(!containsDisabledDependencyModule)
                module.enable();
        } catch (Exception ex) {
            if(BuildTeamTools.getInstance().isDebug()){
                ChatHelper.logError("An error occurred while enabling the %s Module: %s", module.getModuleName(), ex.getMessage());
                ex.printStackTrace();
            }

            module.shutdown(ex.getMessage());
        }

        if(isStarting) {
            if (module.isEnabled())
                Bukkit.getLogger().log(Level.INFO, "§7[§a✔§7] Successfully loaded §e" + module.getModuleName() + " Module§7.");
            else {
                String reason = "";

                if(module.getError() != null && !module.getError().isEmpty())
                    reason = " Reason: §c" + module.getError();

                Bukkit.getLogger().log(Level.INFO, "§7[§c✖§7] Failed to load the §e" + module.getModuleName() + " Module§7." + reason);
            }
        }else{
            if (module.isEnabled())
                ChatHelper.log("Successfully enabled %s Module", module.getModuleName());
            else {
                String reason = "";

                if(module.getError() != null && !module.getError().isEmpty())
                    reason = " Reason: §c" + module.getError();

                ChatHelper.logError("Failed to enable the %s Module%s", module.getModuleName(), reason);
            }
        }

        return true;
    }

    /**
     * Disables a specific module
     *
     * @param module {@link Module}
     * @return True if successfully disabled, false if not
     */
    public boolean disable(Module module) {
        boolean contains = false;
        for(Module m : modules)
            if(m.getModuleName().equals(module.getModuleName()))
                contains = true;

        if(!contains)
            return false;

        module.disable();

        if (!module.isEnabled())
            ChatHelper.log("Successfully disabled %s Module", module.getModuleName());
        else {
            String reason = "";

            if(module.getError() != null && !module.getError().isEmpty())
                reason = " Reason: §c" + module.getError();

            ChatHelper.logError("Failed to disable the %s Module%s", module.getModuleName(), reason);
        }

        return true;
    }

    /** Enables all modules
     *
     * @param isStarting if the server is starting
     */
    public void enableAll(boolean isStarting) {
        for (Module module : new ArrayList<>(modules))
            if (!module.isEnabled())
                enable(module, isStarting);
    }

    /** Disables all modules
     *
     * @param isStopping if the server is stopping
     */
    public void disableAll(boolean isStopping) {
        for (Module module : new ArrayList<>(modules))
            if (module.isEnabled())
                disable(module);
    }

    /**
     * Reloads all modules
     */
    public void reloadAll() {
        disableAll(false);
        enableAll(false);
    }
}

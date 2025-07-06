package net.buildtheearth.modules;

import lombok.Getter;
import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.utils.ChatHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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
     * @param executor the sender that executed the command. If null, the system executed the command.
     * @param isStarting if the server is starting
     * @return True if successfully enabled, false if not
     */
    public boolean enable(Module module, CommandSender executor, boolean isStarting) {
        for (Module m : modules)
            if (m.getModuleName().equals(module.getModuleName()) && m.isEnabled())
                return false;

        boolean containsDisabledDependencyModule = false;
        for (Module m : module.getDependsOnModules())
            if (!m.isEnabled()) {
                module.checkForModuleDependencies();
                containsDisabledDependencyModule = true;
            }

        try {
            if (!containsDisabledDependencyModule)
                module.enable();
        } catch (Exception ex) {
            if (BuildTeamTools.getInstance().isDebug()) {
                ChatHelper.logError("An error occurred while enabling the %s Module: %s", module.getModuleName(), ex.getMessage());
                ex.printStackTrace();
            }

            module.shutdown(ex.getMessage());
        }

        if (!isStarting) {
            if (module.isEnabled() && BuildTeamTools.getInstance().isDebug())
                ChatHelper.log("Successfully enabled %s Module", module.getModuleName());
            else {
                String reason = "";

                if(module.getError() != null && !module.getError().isEmpty())
                    reason = " Reason: §c" + module.getError();

                ChatHelper.logError("Failed to enable the %s Module%s", module.getModuleName(), reason);
            }
        }

        if (executor != null) {
            if (module.isEnabled())
                executor.sendMessage("§7[§a✔§7] Successfully§a enabled §7the §e" + module.getModuleName() + " Module§7.");
            else {
                String reason = "";

                if (module.getError() != null && !module.getError().isEmpty())
                    reason = " Reason: §c" + module.getError();

                executor.sendMessage("§7[§c✖§7] Failed to§a enable §7the §e" + module.getModuleName() + " Module§7." + reason);
            }

        }

        return true;
    }

    /**
     * Disables a specific module
     *
     * @param module {@link Module}
     * @param executor the sender that executed the command. If null, the system executed the command.
     * @return True if successfully disabled, false if not
     */
    public boolean disable(Module module, CommandSender executor) {
        boolean contains = false;
        for(Module m : modules)
            if (m.getModuleName().equals(module.getModuleName())) {
                contains = true;
                break;
            }

        if(!contains)
            return false;

        module.disable();

        if (!module.isEnabled()) {
            if(BuildTeamTools.getInstance().isDebug())
                ChatHelper.log("Successfully disabled %s Module", module.getModuleName());
        }else {
            String reason = "";

            if(module.getError() != null && !module.getError().isEmpty())
                reason = " Reason: §c" + module.getError();

            ChatHelper.logError("Failed to disable the %s Module%s", module.getModuleName(), reason);
        }

        if (executor != null) {
            if (!module.isEnabled())
                executor.sendMessage("§7[§a✔§7] Successfully§c disabled §7the §e" + module.getModuleName() + " Module§7.");
            else {
                String reason = "";

                if (module.getError() != null && !module.getError().isEmpty())
                    reason = " Reason: §c" + module.getError();

                executor.sendMessage("§7[§c✖§7] Failed to§c disable §7the §e" + module.getModuleName() + " Module§7." + reason);
            }
        }

        return true;
    }

    /** Enables all modules
     *
     * @param executor the player that executed the command. If null, the command was executed by the system.
     * @param isStarting if the server is starting
     */
    public void enableAll(@Nullable CommandSender executor, boolean isStarting) {
        for (Module module : new ArrayList<>(modules))
            if (!module.isEnabled())
                enable(module, executor, isStarting);

        if(isStarting)
            sendBuildTeamToolsConsoleStartupMessage();
    }

    /** Disables all modules
     *
     * @param executor the player that executed the command. If null, the command was executed by the system.
     */
    public void disableAll(@Nullable CommandSender executor) {
        for (Module module : new ArrayList<>(modules))
            if (module.isEnabled())
                disable(module, executor);
    }

    /**
     * Reloads all modules
     */
    public void reloadAll(CommandSender executor) {
        disableAll(executor);
        enableAll(executor, false);
    }

    private void sendBuildTeamToolsConsoleStartupMessage(){
        Bukkit.getConsoleSender().sendMessage(" ");
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "--------------- BuildTeamTools V" + BuildTeamTools.getInstance().getDescription().getVersion() + " ----------------");
        Bukkit.getConsoleSender().sendMessage(" ");

        for (Module module : new ArrayList<>(modules))
            if (module.isEnabled())
                Bukkit.getConsoleSender().sendMessage("§7[§aOK§7] Successfully loaded §e" + module.getModuleName() + " Module§7.");
            else {
                String reason = "";

                if (module.getError() != null && !module.getError().isEmpty())
                    reason = " Reason: §c" + module.getError();

                Bukkit.getConsoleSender().sendMessage( "§7[§cX§7]  Failed to load the §e" + module.getModuleName() + " Module§7." + reason);
            }

        Bukkit.getConsoleSender().sendMessage(" ");
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "------------------------------------------------------------");
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "> " + ChatColor.GRAY + "Made by §bBuildTheEarth");
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GRAY + "> " + ChatColor.GRAY + "GitHub:" + ChatColor.WHITE + " https://github.com/BuildTheEarth/BuildTeamTools");
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "------------------------------------------------------------");
        Bukkit.getConsoleSender().sendMessage(" ");
    }
}

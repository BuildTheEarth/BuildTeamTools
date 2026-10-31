package net.buildtheearth.buildteamtools.modules;

import com.alpsbte.alpslib.utils.ChatHelper;
import lombok.Getter;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import net.buildtheearth.buildteamtools.i18n.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public void registerModules(Module @NotNull ... modules) {
        for (Module m : modules)
            registerModule(m);
    }

    /**
     * Replaces the registered modules, disabling the previous instances first.
     *
     * <p>This is used when the plugin is re-enabled so modules bound to newly
     * loaded configuration shells become the active instances.</p>
     *
     * @param modules the module instances for the current plugin lifecycle
     */
    public void replaceModules(Module @NotNull ... modules) {
        disableAll(null);
        this.modules.clear();
        registerModules(modules);
    }

    /**
     * Enables a specific module
     *
     * @param module   {@link Module}
     * @param executor the sender, if it's triggered via command, else null, when it's called by btt on plugin start.
     * @return True if successfully enabled, false if not
     */
    public boolean enable(@NotNull Module module, @Nullable CommandSender executor) {
        for (Module m : modules)
            if (m.getModuleName().equals(module.getModuleName()) && m.isEnabled())
                return false;

        if (!module.getModuleName().equals("Common") && BuildTeamTools.getInstance().getMainConfig()
                .disabledModules().stream().anyMatch(module.getModuleName()::equalsIgnoreCase)) {
            module.shutdown("This module is disabled in the config.");
        } else {
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
                    ChatHelper.logError("An error occurred while enabling the %s Module: %s", ex, module.getModuleName(),
                            ex.getMessage());
                }

                module.shutdown(ex.getMessage());
            }
        }


        if (executor != null) {
            if (module.isEnabled() && BuildTeamTools.getInstance().isDebug())
                ChatHelper.log("Successfully enabled %s Module", module.getModuleName());
            else {
                String reason = "";

                if (module.getError() != null && !module.getError().isEmpty())
                    reason = " Reason: " + module.getError();

                ChatHelper.logError("Failed to enable the %s Module%s", module.getModuleName(), reason);
            }
        }

        if (executor != null) {
            if (module.isEnabled())
                executor.sendMessage(Messages.component(
                        MessageKeys.Module.ENABLE_SUCCESS, Component.text(module.getModuleName())));
            else {
                Component reason = module.getError() != null && !module.getError().isEmpty()
                        ? Messages.component(MessageKeys.Module.FAILURE_REASON, Component.text(module.getError()))
                        : Component.empty();
                executor.sendMessage(Messages.component(
                        MessageKeys.Module.ENABLE_FAILED,
                        Component.text(module.getModuleName()),
                        reason));
            }

        }

        return true;
    }

    /**
     * Disables a specific module
     *
     * @param module   {@link Module}
     * @param executor the sender, if it's triggered via command, else null - when it's called by btt on plugin stop).
     * @return True if successfully disabled, false if not
     */
    public boolean disable(@NotNull Module module, @Nullable CommandSender executor) {
        boolean contains = false;
        for (Module m : modules)
            if (m.getModuleName().equals(module.getModuleName())) {
                contains = true;
                break;
            }

        if (!contains)
            return false;

        module.disable();

        if (!module.isEnabled()) {
            if (BuildTeamTools.getInstance().isDebug())
                ChatHelper.log("Successfully disabled %s Module", module.getModuleName());
        } else {
            String reason = "";

            if (module.getError() != null && !module.getError().isEmpty())
                reason = " Reason: " + module.getError();

            ChatHelper.logError("Failed to disable the %s Module%s", module.getModuleName(), reason);
        }

        if (executor != null) {
            if (!module.isEnabled())
                executor.sendMessage(Messages.component(
                        MessageKeys.Module.DISABLE_SUCCESS, Component.text(module.getModuleName())));
            else {
                Component reason = module.getError() != null && !module.getError().isEmpty()
                        ? Messages.component(MessageKeys.Module.FAILURE_REASON, Component.text(module.getError()))
                        : Component.empty();
                executor.sendMessage(Messages.component(
                        MessageKeys.Module.DISABLE_FAILED,
                        Component.text(module.getModuleName()),
                        reason));
            }
        }

        return true;
    }

    /**
     * Enables all modules
     *
     * @param executor the player, if it's triggered via command, else null (when it's called by btt on plugin start).
     */
    public void enableAll(@Nullable CommandSender executor) {
        for (Module module : new ArrayList<>(modules))
            if (!module.isEnabled())
                enable(module, executor);

        if (executor == null)
            sendBuildTeamToolsConsoleStartupMessage();
    }

    /**
     * Disables all modules
     *
     * @param executor the player, if it's triggered via command, else null (when it's called by btt on plugin stop).
     */
    public void disableAll(@Nullable CommandSender executor) {
        for (Module module : new ArrayList<>(modules))
            if (module.isEnabled())
                disable(module, executor);
    }

    /**
     * Reloads all modules
     */
    public void reloadAll(@Nullable CommandSender executor) {
        disableAll(executor);
        enableAll(executor);
    }

    private void sendBuildTeamToolsConsoleStartupMessage() {
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        String version = BuildTeamTools.getInstance().getPluginMeta().getVersion();

        console.sendMessage(Component.empty());
        console.sendMessage(Component.text(
                "--------------- BuildTeamTools V" + version + " ----------------",
                NamedTextColor.YELLOW));
        console.sendMessage(Component.empty());

        for (Module module : new ArrayList<>(modules)) {
            if (module.isEnabled()) {
                console.sendMessage(Component.text()
                        .append(Component.text("[", NamedTextColor.GRAY))
                        .append(Component.text("OK", NamedTextColor.GREEN))
                        .append(Component.text("] Successfully loaded ", NamedTextColor.GRAY))
                        .append(Component.text(module.getModuleName(), NamedTextColor.YELLOW))
                        .append(Component.text(" Module.", NamedTextColor.GRAY))
                        .build());
            } else {
                var line = Component.text()
                        .append(Component.text("[", NamedTextColor.GRAY))
                        .append(Component.text("X", NamedTextColor.RED))
                        .append(Component.text("]  Failed to load the ", NamedTextColor.GRAY))
                        .append(Component.text(module.getModuleName(), NamedTextColor.YELLOW))
                        .append(Component.text(" Module.", NamedTextColor.GRAY));
                if (module.getError() != null && !module.getError().isEmpty()) {
                    line.append(Component.text(" Reason: ", NamedTextColor.GRAY))
                            .append(Component.text(module.getError(), NamedTextColor.RED));
                }
                console.sendMessage(line.build());
            }
        }

        console.sendMessage(Component.empty());
        console.sendMessage(Component.text(
                "------------------------------------------------------------",
                NamedTextColor.YELLOW));
        console.sendMessage(Component.text()
                .append(Component.text("> ", NamedTextColor.DARK_GRAY))
                .append(Component.text("Made by ", NamedTextColor.GRAY))
                .append(Component.text("BuildTheEarth", NamedTextColor.AQUA))
                .build());
        console.sendMessage(Component.text()
                .append(Component.text("> ", NamedTextColor.DARK_GRAY))
                .append(Component.text("GitHub: ", NamedTextColor.GRAY))
                .append(Component.text(
                        "https://github.com/BuildTheEarth/BuildTeamTools",
                        NamedTextColor.WHITE))
                .build());
        console.sendMessage(Component.text(
                "------------------------------------------------------------",
                NamedTextColor.YELLOW));
        console.sendMessage(Component.empty());
    }
}

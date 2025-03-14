package net.buildtheearth.modules.common.listeners;

import net.buildtheearth.modules.Module;
import net.buildtheearth.modules.ModuleHandler;
import net.buildtheearth.utils.ChatHelper;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class CommandListener implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        String message = e.getMessage();


        for(Module module : ModuleHandler.getInstance().getModules())
            if(!module.isEnabled())
                for(PluginCommand command : module.getCommands().keySet()) {
                    List<String> commands = command.getAliases();
                    commands.add(command.getLabel());

                    for (String alias : commands) {
                        if (message.toLowerCase().startsWith("/" + alias.toLowerCase())) {
                            String reason = "";

                            if (module.getError() != null && !module.getError().isEmpty())
                                reason = " Reason: " + module.getError();

                            ChatHelper.sendErrorMessage(e.getPlayer(), "The Module " + module.getModuleName() + " is currently disabled." + reason);
                            e.setCancelled(true);
                            return;
                        }
                    }
                }
    }
}

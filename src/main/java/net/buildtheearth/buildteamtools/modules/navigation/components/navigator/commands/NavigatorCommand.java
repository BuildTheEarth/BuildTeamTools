package net.buildtheearth.buildteamtools.modules.navigation.components.navigator.commands;

import com.alpsbte.alpslib.utils.ChatHelper;
import net.buildtheearth.buildteamtools.modules.navigation.NavigationModule;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.Module;
import net.buildtheearth.buildteamtools.modules.navigation.menu.MainMenu;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public class NavigatorCommand {
    public void register(Module owner) {
        var commandManager = BuildTeamTools.getInstance().getCommandManager();
        commandManager.registerNoArguments(owner, "navigator", Permissions.NAVIGATOR_USE, this::execute);
        commandManager.registerSubcommand(owner, "navigator", "toggle",
                "Toggle the navigator item on or off.",
                builder -> builder.permission(Permissions.NAVIGATOR_USE),
                context -> toggle(context.sender().getSender()));
    }

    private void execute(@NonNull CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatHelper.getErrorString("You must be a %s to %s this command!", "player", "execute"));
            return;
        }

        if (!player.hasPermission(Permissions.NAVIGATOR_USE)) {
            player.sendMessage(ChatHelper.getErrorString("You don't have permission to use this command!"));
            return;
        }

        new MainMenu(player);
    }

    private void toggle(@NonNull CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatHelper.getErrorString("You must be a %s to %s this command!", "player", "execute"));
            return;
        }
        if (!player.hasPermission(Permissions.NAVIGATOR_USE)) {
            player.sendMessage(ChatHelper.getErrorString("You don't have permission to use this command!"));
            return;
        }
        NavigationModule.getInstance().getNavigatorComponent().toggle(player);
    }
}

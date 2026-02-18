package net.buildtheearth.buildteamtools.modules.navigation.components.navigator.commands;

import com.alpsbte.alpslib.utils.ChatHelper;
import net.buildtheearth.buildteamtools.modules.navigation.NavigationModule;
import net.buildtheearth.buildteamtools.modules.navigation.menu.MainMenu;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NavigatorCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatHelper.getErrorString("You must be a %s to %s this command!", "player", "execute"));
            return true;
        }

        Player player = (Player) sender;

        if(!player.hasPermission(Permissions.NAVIGATOR_USE)) {
            player.sendMessage(ChatHelper.getErrorString("You don't have permission to use this command!"));
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("toggle")) {
            // Toggle the navigator item on/off
            NavigationModule.getInstance().getNavigatorComponent().toggle(player);
        } else {
            // Opens the navigator
            new MainMenu(player);
        }
        return true;
    }
}


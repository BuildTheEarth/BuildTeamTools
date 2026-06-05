package net.buildtheearth.buildteamtools.modules.navigation.components.navigator.commands;

import com.alpsbte.alpslib.utils.ChatHelper;
import net.buildtheearth.buildteamtools.modules.navigation.menu.ExploreMenu;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command to open the BuildTeams explore menu.
 * Usage: /explore
 */
public class ExploreCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatHelper.getErrorString("You must be a %s to %s this command!", "player", "execute"));
            return true;
        }

        if (!player.hasPermission(Permissions.NAVIGATOR_USE)) {
            player.sendMessage(ChatHelper.getErrorString("You don't have permission to use this command!"));
            return true;
        }

        // Open the buildteams explore menu with a back reference to the main navigator
        new ExploreMenu(player, true);

        return true;
    }
}


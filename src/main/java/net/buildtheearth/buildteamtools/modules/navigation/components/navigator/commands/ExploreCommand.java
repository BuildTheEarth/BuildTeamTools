package net.buildtheearth.buildteamtools.modules.navigation.components.navigator.commands;

import com.alpsbte.alpslib.utils.ChatHelper;
import net.buildtheearth.buildteamtools.modules.navigation.menu.ExploreMenu;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.Module;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

/**
 * Command to open the BuildTeams explore menu.
 * Usage: /explore
 */
public class ExploreCommand {
    public void register(Module owner) {
        BuildTeamTools.getInstance().getCommandManager().registerNoArguments(
                owner, "explore", Permissions.NAVIGATOR_USE, this::execute);
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

        // Open the buildteams explore menu with a back reference to the main navigator
        new ExploreMenu(player, true);
    }
}

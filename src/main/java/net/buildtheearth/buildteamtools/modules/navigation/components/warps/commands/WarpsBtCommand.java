package net.buildtheearth.buildteamtools.modules.navigation.components.warps.commands;

import net.buildtheearth.buildteamtools.modules.navigation.components.navigator.commands.BuildteamCommand;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.WarpsComponent;
import net.buildtheearth.buildteamtools.modules.network.model.BuildTeam;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public class WarpsBtCommand extends BuildteamCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {
        return btCommand(sender, label, args, Permissions.WARP_USE);
    }

    @Override
    public void execute(Player player, BuildTeam team) {
        WarpsComponent.openWarpMenu(player, team, null);
    }
}


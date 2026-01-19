package net.buildtheearth.modules.navigation.components.warps.commands;

import net.buildtheearth.modules.navigation.components.navigator.commands.BuildteamCommand;
import net.buildtheearth.modules.navigation.components.warps.WarpsComponent;
import net.buildtheearth.modules.network.model.BuildTeam;
import net.buildtheearth.modules.network.model.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WarpsBtCommand extends BuildteamCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        return btCommand(sender, label, args, Permissions.WARP_USE);
    }

    @Override
    public void execute(Player player, BuildTeam team) {
        WarpsComponent.openWarpMenu(player, team, null);
    }
}


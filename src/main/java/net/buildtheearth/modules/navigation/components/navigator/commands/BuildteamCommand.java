package net.buildtheearth.modules.navigation.components.navigator.commands;

import net.buildtheearth.modules.navigation.NavUtils;
import net.buildtheearth.modules.network.NetworkModule;
import net.buildtheearth.modules.network.model.BuildTeam;
import net.buildtheearth.modules.network.model.Permissions;
import net.buildtheearth.utils.ChatHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class BuildteamCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatHelper.getErrorString("You must be a %s to %s this command!", "player", "execute"));
            return true;
        }

        if(!player.hasPermission(Permissions.NAVIGATOR_USE)) {
            player.sendMessage(ChatHelper.getErrorString("You don't have permission to use this command!"));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(ChatHelper.getErrorString("Usage: /%s <buildteam>", label));
            return true;
        } else {
            String buildTeamName = args[0];
            var teams = NetworkModule.getInstance().getBuildTeams().stream()
                    .filter(buildTeam -> buildTeam.getTag().equalsIgnoreCase(buildTeamName) || buildTeam.getName().equalsIgnoreCase(buildTeamName)).toArray();
            if (teams.length == 0 || !(teams[0] instanceof BuildTeam bt)) {
                player.sendMessage(ChatHelper.getErrorString("Build team '%s' does not exist!", buildTeamName));
                return true;
            }
            NavUtils.switchToTeam(bt, player);

        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            return NetworkModule.getInstance().getBuildTeams().stream()
                    .flatMap(bt -> Stream.of(bt.getTag(), bt.getName()))
                    .filter(s -> s.toLowerCase().startsWith(partial))
                    .toList();
        }
        return Collections.emptyList();
    }
}


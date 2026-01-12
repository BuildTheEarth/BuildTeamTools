package net.buildtheearth.buildteamtools.modules.navigation.components.navigator.commands;

import com.alpsbte.alpslib.utils.ChatHelper;
import net.buildtheearth.buildteamtools.modules.navigation.NavUtils;
import net.buildtheearth.buildteamtools.modules.network.NetworkModule;
import net.buildtheearth.buildteamtools.modules.network.model.BuildTeam;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
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
        return btCommand(sender, label, args, Permissions.NAVIGATOR_USE);
    }

    protected boolean btCommand(@NotNull CommandSender sender, @NotNull String label, String[] args, String permission) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatHelper.getErrorString("You must be a %s to %s this command!", "player", "execute"));
            return true;
        }

        if (!player.hasPermission(permission)) {
            player.sendMessage(ChatHelper.getErrorString("You don't have permission to use this command!"));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(ChatHelper.getErrorString("Usage: /%s <buildteam>", label));
            return true;
        } else {
            String buildTeamName = args[0];
            var teams = NetworkModule.getInstance().getBuildTeams().stream()
                    .filter(buildTeam -> buildTeam.getTag().equalsIgnoreCase(buildTeamName) || buildTeam.getBlankName().equalsIgnoreCase(buildTeamName)).toArray();
            if (teams.length == 0 || !(teams[0] instanceof BuildTeam bt)) {
                player.sendMessage(ChatHelper.getErrorString("Build team '%s' does not exist!", buildTeamName));
                return true;
            }
            execute(player, bt);
        }
        return true;
    }

    public void execute(Player player, BuildTeam team) {
        NavUtils.switchToTeam(team, player);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length >= 1) {
            String partial = args[0].toLowerCase();
            return NetworkModule.getInstance().getBuildTeams().stream()
                    .flatMap(bt -> Stream.of(bt.getTag(), bt.getBlankName()))
                    .filter(s -> s.toLowerCase().startsWith(partial))
                    .toList();
        }
        return Collections.emptyList();
    }
}


package net.buildtheearth.buildteamtools.modules.navigation.components.navigator.commands;

import com.alpsbte.alpslib.utils.ChatHelper;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.Module;
import net.buildtheearth.buildteamtools.modules.common.commands.BttCommandManager;
import net.buildtheearth.buildteamtools.modules.navigation.NavUtils;
import net.buildtheearth.buildteamtools.modules.network.NetworkModule;
import net.buildtheearth.buildteamtools.modules.network.model.BuildTeam;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.parser.standard.StringParser;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.stream.Stream;

public class BuildteamCommand {

    public void register(Module owner) {
        BuildTeamTools.getInstance().getCommandManager().registerRequiredArgument(owner, "buildteam",
                "buildteam", "Build team name or tag",
                StringParser.greedyStringParser(),
                Permissions.NAVIGATOR_USE,
                (sender, input) -> suggestions(input),
                (sender, buildTeamName) -> btCommand(sender, buildTeamName, Permissions.NAVIGATOR_USE));
    }

    protected void btCommand(@NonNull CommandSender sender, String buildTeamName, String permission) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatHelper.getErrorString("You must be a %s to %s this command!", "player", "execute"));
            return;
        }

        if (!player.hasPermission(permission)) {
            player.sendMessage(ChatHelper.getErrorString("You don't have permission to use this command!"));
            return;
        }

        BuildTeam team = NetworkModule.getInstance().getBuildTeams().stream()
                .filter(buildTeam -> buildTeam.getTag().equalsIgnoreCase(buildTeamName)
                        || buildTeam.getBlankName().equalsIgnoreCase(buildTeamName))
                .findFirst()
                .orElse(null);
        if (team == null) {
            player.sendMessage(ChatHelper.getErrorString("Build team '%s' does not exist!", buildTeamName));
            return;
        }
        execute(player, team);
    }

    public void execute(Player player, BuildTeam team) {
        NavUtils.switchToTeam(team, player);
    }

    protected List<String> suggestions(String input) {
        List<String> candidates = NetworkModule.getInstance().getBuildTeams().stream()
                .flatMap(team -> Stream.of(team.getTag(), team.getBlankName()))
                .toList();
        return BttCommandManager.matchingSuggestions(candidates, input);
    }

}

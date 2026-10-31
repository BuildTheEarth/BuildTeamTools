package net.buildtheearth.buildteamtools.modules.navigation.components.navigator.commands;

import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import net.buildtheearth.buildteamtools.i18n.Messages;
import net.buildtheearth.buildteamtools.modules.Module;
import net.buildtheearth.buildteamtools.modules.common.commands.BttCommandManager;
import net.buildtheearth.buildteamtools.modules.common.metrics.FeatureUsage;
import net.buildtheearth.buildteamtools.modules.common.metrics.UsageDetail;
import net.buildtheearth.buildteamtools.modules.navigation.NavUtils;
import net.buildtheearth.buildteamtools.modules.network.NetworkModule;
import net.buildtheearth.buildteamtools.modules.network.model.BuildTeam;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import net.kyori.adventure.text.Component;
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
            sender.sendMessage(Messages.playerOnly());
            return;
        }

        if (!player.hasPermission(permission)) {
            player.sendMessage(Messages.noPermission(permission));
            return;
        }

        BuildTeam team = NetworkModule.getInstance().getBuildTeams().stream()
                .filter(buildTeam -> buildTeam.getTag().equalsIgnoreCase(buildTeamName)
                        || buildTeam.getBlankName().equalsIgnoreCase(buildTeamName))
                .findFirst()
                .orElse(null);
        if (team == null) {
            player.sendMessage(Messages.component(
                    MessageKeys.Navigator.BUILD_TEAM_NOT_FOUND,
                    Component.text(buildTeamName)));
            return;
        }
        execute(player, team);
    }

    public void execute(Player player, BuildTeam team) {
        FeatureUsage.record(UsageDetail.NAVIGATOR_BUILDTEAM);
        NavUtils.switchToTeam(team, player);
    }

    protected List<String> suggestions(String input) {
        List<String> candidates = NetworkModule.getInstance().getBuildTeams().stream()
                .flatMap(team -> Stream.of(team.getTag(), team.getBlankName()))
                .toList();
        return BttCommandManager.matchingSuggestions(candidates, input);
    }

}

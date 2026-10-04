package net.buildtheearth.buildteamtools.modules.navigation.components.warps.commands;

import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.Module;
import net.buildtheearth.buildteamtools.modules.navigation.components.navigator.commands.BuildteamCommand;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.WarpsComponent;
import net.buildtheearth.buildteamtools.modules.network.model.BuildTeam;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import org.bukkit.entity.Player;
import org.incendo.cloud.parser.standard.StringParser;

public class BtWarpsCommand extends BuildteamCommand {
    @Override
    public void register(Module owner) {
        BuildTeamTools.getInstance().getCommandManager().registerRequiredArgument(owner, "btwarps",
                "buildteam", "Build team name or tag",
                StringParser.greedyStringParser(),
                Permissions.WARP_USE,
                (sender, input) -> suggestions(input),
                (sender, buildTeamName) -> btCommand(sender, buildTeamName, Permissions.WARP_USE));
    }

    @Override
    public void execute(Player player, BuildTeam team) {
        WarpsComponent.openWarpMenu(player, team, null);
    }
}

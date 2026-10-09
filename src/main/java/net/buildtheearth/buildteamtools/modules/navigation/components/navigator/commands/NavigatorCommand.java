package net.buildtheearth.buildteamtools.modules.navigation.components.navigator.commands;

import com.alpsbte.alpslib.utils.ChatHelper;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.Module;
import net.buildtheearth.buildteamtools.modules.common.commands.BttCommandManager;
import net.buildtheearth.buildteamtools.modules.navigation.NavigationModule;
import net.buildtheearth.buildteamtools.modules.navigation.components.warps.WarpsComponent;
import net.buildtheearth.buildteamtools.modules.navigation.menu.CitySelectorMenu;
import net.buildtheearth.buildteamtools.modules.navigation.menu.CountrySelectorMenu;
import net.buildtheearth.buildteamtools.modules.navigation.menu.ExploreMenu;
import net.buildtheearth.buildteamtools.modules.navigation.menu.MainMenu;
import net.buildtheearth.buildteamtools.modules.navigation.menu.StateSelectorMenu;
import net.buildtheearth.buildteamtools.modules.network.NetworkModule;
import net.buildtheearth.buildteamtools.modules.network.model.BuildTeam;
import net.buildtheearth.buildteamtools.modules.network.model.Continent;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import net.buildtheearth.buildteamtools.modules.network.model.Region;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.Command;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.suggestion.SuggestionProvider;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class NavigatorCommand {
    private static final String CONTINENT_ARGUMENT = "continent";
    private static final String STATE_ARGUMENT = "state";
    private static final String PLAYER_ONLY_MESSAGE = "You must be a %s to %s this command!";

    public void register(Module owner) {
        var commandManager = BuildTeamTools.getInstance().getCommandManager();
        commandManager.registerNoArguments(owner, "navigator", Permissions.NAVIGATOR_USE, this::openNavigator);
        registerSubcommand(owner, "explore", "Open the continent selection menu.",
                context -> openPlayer(context.sender().getSender(), player -> new ExploreMenu(player, true)));
        registerSubcommand(owner, "countries", "Open the country selection menu for a continent.",
                builder -> builder
                        .permission(Permissions.NAVIGATOR_USE)
                        .required(CONTINENT_ARGUMENT, StringParser.greedyStringParser(),
                                RichDescription.of(Component.text("Continent name")),
                                SuggestionProvider.blockingStrings((context, input) ->
                                        BttCommandManager.matchingSuggestions(continentNames(), input.remainingInput()))),
                context -> {
                    CommandSender sender = context.sender().getSender();
                    Continent continent = parseContinent(context.get(CONTINENT_ARGUMENT));
                    if (continent == null) {
                        sender.sendMessage(ChatHelper.getErrorString("Unknown continent. Available continents: %s",
                                String.join(", ", continentNames())));
                        return;
                    }
                    openPlayer(sender, player -> new CountrySelectorMenu(player, continent, true));
                });
        registerSubcommand(owner, "states", "Open the USA state selection menu.",
                context -> openPlayer(context.sender().getSender(),
                        player -> new StateSelectorMenu(player, Continent.NORTH_AMERICA, true)));
        registerSubcommand(owner, "cities", "Open the city selection menu for a supported state.",
                builder -> builder
                        .permission(Permissions.NAVIGATOR_USE)
                        .required(STATE_ARGUMENT, StringParser.greedyStringParser(),
                                RichDescription.of(Component.text("State name")),
                                SuggestionProvider.blockingStrings((context, input) ->
                                        BttCommandManager.matchingSuggestions(cityStateNames(), input.remainingInput()))),
                context -> {
                    CommandSender sender = context.sender().getSender();
                    String stateName = context.<String>get(STATE_ARGUMENT).replace('_', ' ');
                    Region state = StateSelectorMenu.getCityStates().stream()
                            .filter(region -> region.getName().equalsIgnoreCase(stateName))
                            .findFirst().orElse(null);
                    if (state == null) {
                        sender.sendMessage(ChatHelper.getErrorString(
                                "No city menu is available for that state. Available states: %s",
                                String.join(", ", cityStateNames())));
                        return;
                    }
                    openPlayer(sender, player -> new CitySelectorMenu(
                            state, StateSelectorMenu.getCityTeamsForState(state), player, true));
                });
        registerSubcommand(owner, "warps", "Open this build team's warp menu.",
                builder -> builder.permission(Permission.allOf(
                        Permission.of(Permissions.NAVIGATOR_USE),
                        Permission.of(Permissions.WARP_USE))),
                context -> openWarps(context.sender().getSender()));
        registerSubcommand(owner, "build", "Run the configured Build menu action.",
                context -> openPlayer(context.sender().getSender(), MainMenu::openBuild));
        registerSubcommand(owner, "plotsystem", "Run the configured Plot System menu action.",
                context -> openPlayer(context.sender().getSender(), MainMenu::openPlotSystem));
        registerSubcommand(owner, "tutorials", "Open the tutorial menu or run its configured action.",
                context -> openPlayer(context.sender().getSender(), MainMenu::openTutorials));
        registerSubcommand(owner, "toggle", "Toggle the navigator hotbar item.",
                context -> openPlayer(context.sender().getSender(),
                        NavigationModule.getInstance().getNavigatorComponent()::toggle));
    }

    private void registerSubcommand(
            Module owner,
            String name,
            String description,
            Consumer<CommandContext<CommandSourceStack>> handler
    ) {
        registerSubcommand(owner, name, description,
                builder -> builder.permission(Permissions.NAVIGATOR_USE), handler);
    }

    private void registerSubcommand(
            Module owner,
            String name,
            String description,
            UnaryOperator<Command.Builder<CommandSourceStack>> argumentBuilder,
            Consumer<CommandContext<CommandSourceStack>> handler
    ) {
        BuildTeamTools.getInstance().getCommandManager().registerSubcommand(
                owner, "navigator", name, description,
                argumentBuilder,
                handler);
    }

    private void openNavigator(@NonNull CommandSender sender) {
        openPlayer(sender, MainMenu::new);
    }

    private void openWarps(@NonNull CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sendPlayerOnlyMessage(sender);
            return;
        }
        BuildTeam team = NetworkModule.getInstance().getBuildTeam();
        if (team == null || team.getWarpGroups() == null) {
            player.sendMessage(ChatHelper.getErrorString("The build team's warps are currently unavailable."));
            return;
        }
        WarpsComponent.openWarpMenu(player, team, new MainMenu(player, false));
    }

    private void openPlayer(@NonNull CommandSender sender, Consumer<Player> action) {
        if (!(sender instanceof Player player)) {
            sendPlayerOnlyMessage(sender);
            return;
        }
        action.accept(player);
    }

    private void sendPlayerOnlyMessage(@NonNull CommandSender sender) {
        sender.sendMessage(ChatHelper.getErrorString(PLAYER_ONLY_MESSAGE, "player", "execute"));
    }

    private static List<String> continentNames() {
        return Arrays.stream(Continent.values())
                .map(continent -> continent.name().toLowerCase(Locale.ROOT))
                .toList();
    }

    private static Continent parseContinent(String name) {
        String normalized = name.replace('_', ' ').replace('-', ' ');
        return Arrays.stream(Continent.values())
                .filter(continent -> continent.getLabel().equalsIgnoreCase(normalized))
                .findFirst().orElse(null);
    }

    private static List<String> cityStateNames() {
        return StateSelectorMenu.getCityStates().stream()
                .map(region -> region.getName().replace(' ', '_'))
                .distinct().toList();
    }
}

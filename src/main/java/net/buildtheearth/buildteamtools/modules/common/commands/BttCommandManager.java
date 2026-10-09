package net.buildtheearth.buildteamtools.modules.common.commands;

import com.alpsbte.alpslib.utils.ChatHelper;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.Module;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import net.buildtheearth.buildteamtools.utils.io.MainConfig;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.component.DefaultValue;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.exception.ArgumentParseException;
import org.incendo.cloud.exception.InvalidCommandSenderException;
import org.incendo.cloud.exception.InvalidSyntaxException;
import org.incendo.cloud.exception.NoPermissionException;
import org.incendo.cloud.minecraft.extras.MinecraftHelp;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.SuggestionProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class BttCommandManager {

    private static final Set<String> COMMAND_NAMES = Set.of(
            "btt", "generate", "kml", "geopoints", "geopath", "georing", "geosurface",
            "navigator", "buildteam", "btwarps", "address", "warp", "blockpalette",
            "help", "communicators", "cache", "debug", "checkforupdates", "reload-config", "reload", "update"
    );
    private static final ParserDescriptor<CommandSourceStack, String> ARGUMENT_PARSER =
            StringParser.greedyStringParser();

    private final Logger logger;
    private final MainConfig config;
    private final CommandManager<CommandSourceStack> manager;
    private final MinecraftHelp<CommandSourceStack> help;
    private final Set<String> claimedLabels = new HashSet<>(COMMAND_NAMES);
    private final Map<String, List<String>> aliasesByCommand = new HashMap<>();

    public BttCommandManager(BuildTeamTools plugin) {
        this(PaperCommandManager.builder()
                .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
                .buildOnEnable(plugin), plugin.getMainConfig(), plugin.getLogger());
    }

    BttCommandManager(CommandManager<CommandSourceStack> manager, MainConfig config, Logger logger) {
        this.manager = manager;
        this.config = config;
        this.logger = logger;
        help = MinecraftHelp.create("/btt help", manager, CommandSourceStack::getSender);

        manager.exceptionController().registerHandler(InvalidSyntaxException.class, context ->
                context.context().sender().getSender().sendMessage(ChatHelper.getErrorString(
                        "Invalid command syntax. Usage: %s", context.exception().correctSyntax())));
        manager.exceptionController().registerHandler(ArgumentParseException.class, context ->
                context.context().sender().getSender().sendMessage(ChatHelper.getErrorString(
                        "Invalid command argument: %s", context.exception().getMessage())));
        manager.exceptionController().registerHandler(NoPermissionException.class, context ->
                context.context().sender().getSender().sendMessage(ChatHelper.getErrorString(
                        "You don't have permission to use this command.")));
        manager.exceptionController().registerHandler(InvalidCommandSenderException.class, context ->
                context.context().sender().getSender().sendMessage(ChatHelper.getErrorString(
                        "This command cannot be used by your sender type.")));
    }

    public void registerRoot(Module owner, Consumer<CommandSender> handler) {
        List<String> aliases = validatedAliases(
                "btt",
                config.commands().aliases().btt(),
                true
        );
        aliasesByCommand.put("btt", aliases);
        try {
            manager.command(manager.commandBuilder("btt", aliases.toArray(String[]::new))
                    .commandDescription(description("Show BuildTeamTools status and connection information."))
                    .permission(Permissions.BUILD_TEAM_TOOLS)
                    .handler(context -> execute(owner, context.sender().getSender(), "btt",
                            () -> handler.accept(context.sender().getSender()))));
        } catch (RuntimeException exception) {
            logger.log(Level.SEVERE, "Failed to register the BTT command root.", exception);
            throw exception;
        }
    }

    public void registerRootSubcommand(
            Module owner,
            String name,
            String description,
            String permission,
            CommandHandler handler
    ) {
        manager.command(rootBuilder()
                .literal(name, description(description))
                .commandDescription(description(description))
                .permission(Permission.allOf(
                        Permission.of(Permissions.BUILD_TEAM_TOOLS),
                        Permission.of(permission)
                ))
                .handler(context -> execute(owner, context.sender().getSender(), name, () ->
                        handler.execute(context.sender().getSender(), name, new String[0]))));
    }

    public void registerRootNestedSubcommand(
            Module owner,
            String parent,
            String name,
            String description,
            String permission,
            Consumer<CommandContext<CommandSourceStack>> handler
    ) {
        RichDescription parentDescription = description(rootSubcommandDescription(parent));
        manager.command(rootBuilder()
                .literal(parent, parentDescription)
                .literal(name, description(description))
                .commandDescription(description(description))
                .permission(Permission.allOf(
                        Permission.of(Permissions.BUILD_TEAM_TOOLS),
                        Permission.of(permission)
                ))
                .handler(context -> execute(owner, context.sender().getSender(),
                        parent + " " + name, () -> handler.accept(context))));
    }

    public void registerRootHelp(Module owner) {
        manager.command(rootBuilder()
                .literal("help", description("Show available commands or details for a command."))
                .optional("query", ARGUMENT_PARSER, DefaultValue.constant(""))
                .commandDescription(description("Show available commands or details for a command."))
                .permission(Permissions.BUILD_TEAM_TOOLS)
                .handler(context -> execute(owner, context.sender().getSender(), "btt help", () ->
                        help.queryCommands(context.<String>optional("query").orElse(""), context.sender()))));
    }

    public void register(Module owner, String name, BiConsumer<CommandSender, String[]> handler) {
        registerAliased(owner, name, configuredAliases(name),
                (sender, label, arguments) -> handler.accept(sender, arguments));
    }

    public void register(
            Module owner,
            String name,
            String permission,
            BiConsumer<CommandSender, String[]> handler,
            BiFunction<CommandSender, String, List<String>> suggestions
    ) {
        registerAliased(owner, name, configuredAliases(name),
                (sender, label, arguments) -> handler.accept(sender, arguments),
                (sender, label, input) -> suggestions.apply(sender, input),
                permission);
    }

    public void register(
            Module owner,
            String name,
            BiConsumer<CommandSender, String[]> handler,
            BiFunction<CommandSender, String, List<String>> suggestions
    ) {
        registerAliased(owner, name, configuredAliases(name),
                (sender, label, arguments) -> handler.accept(sender, arguments), suggestions);
    }

    public void registerAliased(Module owner, String name, List<String> aliases, CommandHandler handler) {
        registerAliased(owner, name, aliases, handler, (sender, label, input) -> List.of());
    }

    public void registerAliased(
            Module owner,
            String name,
            List<String> aliases,
            CommandHandler handler,
            BiFunction<CommandSender, String, List<String>> suggestions
    ) {
        registerAliased(owner, name, aliases, handler,
                (sender, label, input) -> suggestions.apply(sender, input));
    }

    public void registerAliased(
            Module owner,
            String name,
            List<String> aliases,
            CommandHandler handler,
            SuggestionHandler suggestions
    ) {
        registerAliased(owner, name, aliases, handler, suggestions, null);
    }

    private void registerAliased(
            Module owner,
            String name,
            List<String> aliases,
            CommandHandler handler,
            SuggestionHandler suggestions,
            String permission
    ) {
        List<String> validAliases = validatedAliases(name, aliases, false);
        aliasesByCommand.put(name, validAliases);
        try {
            for (var base : featureBuilders(name)) {
                var builder = base
                        .commandDescription(descriptionFor(name))
                        .optional("arguments", ARGUMENT_PARSER,
                                suggestionProvider((sender, input) ->
                                        suggestions.suggest(sender.getSender(), name, input)));
                if (permission != null) builder = builder.permission(permission);
                manager.command(builder.handler(context ->
                        execute(owner, context.sender().getSender(), name, () ->
                                handler.execute(context.sender().getSender(), name,
                                        splitArguments(context.<String>optional("arguments").orElse(""))))));
            }
        } catch (RuntimeException exception) {
            logger.log(Level.SEVERE, "Failed to register command '" + name + "'.", exception);
            throw exception;
        }
    }

    public void registerNoArguments(Module owner, String name, Consumer<CommandSender> handler) {
        registerNoArguments(owner, name, null, handler);
    }

    public void registerNoArguments(
            Module owner,
            String name,
            String permission,
            Consumer<CommandSender> handler
    ) {
        List<String> aliases = validatedAliases(name, configuredAliases(name), false);
        aliasesByCommand.put(name, aliases);
        for (var base : featureBuilders(name)) {
            var builder = base.commandDescription(descriptionFor(name));
            if (permission != null) builder = builder.permission(permission);
            manager.command(builder.handler(context -> execute(owner, context.sender().getSender(), name,
                    () -> handler.accept(context.sender().getSender()))));
        }
    }

    public <T> void registerRequiredArgument(
            Module owner,
            String name,
            String argumentName,
            String argumentDescription,
            ParserDescriptor<CommandSourceStack, T> parser,
            BiConsumer<CommandSender, T> handler
    ) {
        registerRequiredArgument(owner, name, argumentName, argumentDescription, parser,
                null, (sender, input) -> List.of(), handler);
    }

    public <T> void registerRequiredArgument(
            Module owner,
            String name,
            String argumentName,
            String argumentDescription,
            ParserDescriptor<CommandSourceStack, T> parser,
            BiFunction<CommandSender, String, List<String>> suggestions,
            BiConsumer<CommandSender, T> handler
    ) {
        registerRequiredArgument(owner, name, argumentName, argumentDescription, parser,
                null, suggestions, handler);
    }

    public <T> void registerRequiredArgument(
            Module owner,
            String name,
            String argumentName,
            String argumentDescription,
            ParserDescriptor<CommandSourceStack, T> parser,
            String permission,
            BiFunction<CommandSender, String, List<String>> suggestions,
            BiConsumer<CommandSender, T> handler
    ) {
        List<String> aliases = validatedAliases(name, configuredAliases(name), false);
        aliasesByCommand.put(name, aliases);
        for (var base : featureBuilders(name)) {
            var builder = base.commandDescription(descriptionFor(name))
                    .required(argumentName, parser, description(argumentDescription),
                            suggestionProvider((sender, input) -> suggestions.apply(sender.getSender(), input)));
            if (permission != null) builder = builder.permission(permission);
            manager.command(builder.handler(context -> execute(owner, context.sender().getSender(), name, () ->
                    handler.accept(context.sender().getSender(), context.get(argumentName)))));
        }
    }

    public void registerSubcommand(
            Module owner,
            String commandName,
            String subcommand,
            String description,
            UnaryOperator<Command.Builder<CommandSourceStack>> argumentBuilder,
            Consumer<CommandContext<CommandSourceStack>> handler
    ) {
        RichDescription richDescription = description(description);
        for (var base : featureBuilders(commandName)) {
            var builder = base
                    .literal(subcommand, richDescription)
                    .commandDescription(richDescription);
            manager.command(argumentBuilder.apply(builder)
                    .handler(context -> execute(owner, context.sender().getSender(),
                            commandName + " " + subcommand, () -> handler.accept(context))));
        }
    }

    private Command.Builder<CommandSourceStack> rootBuilder() {
        return manager.commandBuilder("btt", aliasesByCommand.getOrDefault("btt", List.of()).toArray(String[]::new));
    }

    private List<Command.Builder<CommandSourceStack>> featureBuilders(String name) {
        List<String> aliases = aliasesByCommand.get(name);
        if (aliases == null) {
            throw new IllegalStateException("Register command '" + name + "' before its subcommands.");
        }
        List<Command.Builder<CommandSourceStack>> builders = new ArrayList<>();
        if (!aliases.isEmpty()) {
            builders.add(manager.commandBuilder(aliases.getFirst(),
                    aliases.subList(1, aliases.size()).toArray(String[]::new)));
        }
        String[] subcommandAliases = aliases.stream().filter(alias -> !alias.equals(name)).toArray(String[]::new);
        builders.add(rootBuilder().literal(name, descriptionFor(name), subcommandAliases));
        return builders;
    }

    private List<String> configuredAliases(String name) {
        MainConfig.Aliases aliases = config.commands().aliases();
        return switch (name) {
            case "generate" -> aliases.generate();
            case "kml" -> aliases.kml();
            case "geopoints" -> aliases.geopoints();
            case "geopath" -> aliases.geopath();
            case "georing" -> aliases.georing();
            case "geosurface" -> aliases.geosurface();
            case "navigator" -> aliases.navigator();
            case "buildteam" -> aliases.buildteam();
            case "btwarps" -> aliases.btwarps();
            case "address" -> aliases.address();
            case "warp" -> aliases.warp();
            case "blockpalette" -> aliases.blockpalette();
            default -> List.of();
        };
    }

    private List<String> validatedAliases(String command, List<String> aliases, boolean rootCommand) {
        List<String> accepted = new ArrayList<>();
        if (aliases == null) {
            logger.warning("Ignoring null aliases configured for " + command + ".");
            return accepted;
        }
        for (String alias : aliases) {
            String normalized = alias == null ? "" : alias.toLowerCase(Locale.ROOT);
            boolean validLabel = normalized.matches("[a-z0-9_-]+");
            boolean configuredRootAlias = rootCommand && normalized.equals("buildteamtools");
            boolean canonicalLabel = !rootCommand && normalized.equals(command);
            if (!validLabel || (!configuredRootAlias && !canonicalLabel && claimedLabels.contains(normalized))) {
                logger.warning("Ignoring invalid alias '" + alias + "' configured for " + command + ".");
                continue;
            }
            if (accepted.contains(normalized) || (!canonicalLabel && !claimedLabels.add(normalized))) {
                logger.warning("Ignoring duplicate alias '" + alias + "' configured for " + command + ".");
                continue;
            }
            accepted.add(normalized);
        }
        return List.copyOf(accepted);
    }

    private static SuggestionProvider<CommandSourceStack> suggestionProvider(
            BiFunction<CommandSourceStack, String, List<String>> suggestions
    ) {
        return SuggestionProvider.blockingStrings((context, input) ->
                suggestions.apply(context.sender(), input.remainingInput()));
    }

    private static RichDescription description(String description) {
        return RichDescription.of(Component.text(description));
    }

    private static RichDescription descriptionFor(String name) {
        String description = switch (name) {
            case "btt" -> "Show BuildTeamTools status and connection information.";
            case "generate" -> "Generate geographic data for a build team.";
            case "kml", "geopoints", "geopath", "georing", "geosurface" ->
                    "Create KML geometry from selected map points.";
            case "navigator" -> "Open the build team navigation menu.";
            case "buildteam" -> "Switch to a build team by name or tag.";
            case "btwarps" -> "Open the warp menu for a build team.";
            case "address" -> "Look up the address at your current location.";
            case "warp" -> "Warp to a saved location.";
            case "blockpalette" -> "Open the block palette menu.";
            default -> "BuildTeamTools command.";
        };
        return description(description);
    }

    private static String[] splitArguments(String arguments) {
        return arguments.isBlank() ? new String[0] : arguments.trim().split("\\s+");
    }

    private static String rootSubcommandDescription(String name) {
        return switch (name) {
            case "cache" -> "View the network cache or upload and refresh it.";
            case "debug" -> "Show or change debug mode.";
            default -> "BuildTeamTools administrative subcommand.";
        };
    }

    public static List<String> matchingSuggestions(List<String> options, String input) {
        String partial = input.substring(input.lastIndexOf(' ') + 1);
        return options.stream()
                .filter(value -> value.toLowerCase(Locale.ROOT).startsWith(partial.toLowerCase(Locale.ROOT)))
                .toList();
    }

    private static void execute(Module owner, CommandSender sender, String label, Runnable handler) {
        if (!owner.isEnabled()) {
            String reason = owner.getError() == null || owner.getError().isBlank()
                    ? ""
                    : " Reason: " + owner.getError();
            sender.sendMessage(ChatHelper.getErrorString("The Module %s is currently disabled.%s",
                    owner.getModuleName(), reason));
            return;
        }
        try {
            handler.run();
        } catch (RuntimeException exception) {
            BuildTeamTools.getInstance().getSLF4JLogger().error("Command '{}' failed", label, exception);
            sender.sendMessage(ChatHelper.getErrorString("An error occurred while executing this command."));
        }
    }

    @FunctionalInterface
    public interface CommandHandler {
        void execute(CommandSender sender, String label, String[] arguments);
    }

    @FunctionalInterface
    public interface SuggestionHandler {
        List<String> suggest(CommandSender sender, String label, String input);
    }
}

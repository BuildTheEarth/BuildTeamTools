package net.buildtheearth.buildteamtools.modules.generator.commands;

import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import net.buildtheearth.buildteamtools.i18n.Messages;
import net.buildtheearth.buildteamtools.modules.generator.GeneratorModule;
import net.buildtheearth.buildteamtools.modules.Module;
import net.buildtheearth.buildteamtools.modules.common.commands.BttCommandManager;
import net.buildtheearth.buildteamtools.modules.generator.menu.GeneratorMenu;
import net.buildtheearth.buildteamtools.modules.generator.model.GeneratorType;
import net.buildtheearth.buildteamtools.modules.generator.model.HistoryEntry;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import net.buildtheearth.buildteamtools.utils.MessageBoxes;
import net.buildtheearth.buildteamtools.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.incendo.cloud.component.DefaultValue;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.SuggestionProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class GeneratorCommand {

    private static final List<String> HISTORY_COMMANDS = List.of("history", "undo", "redo");
    private static final List<String> HELP_ARGUMENTS = List.of("help", "info", "?");

    public void register(Module owner) {
        var commandManager = BuildTeamTools.getInstance().getCommandManager();
        commandManager.register(owner, "generate", Permissions.GENERATOR_USE, this::execute, this::suggestions);
        commandManager.registerSubcommand(owner, "generate", "history", "Show your recent generation history.",
                builder -> builder.permission(Permissions.GENERATOR_USE),
                context -> handleHistoryCommand(context.sender().getSender()));
        commandManager.registerSubcommand(owner, "generate", "undo", "Undo your last generation.",
                builder -> builder.permission(Permissions.GENERATOR_USE),
                context -> handleHistoryAction(context.sender().getSender(), true));
        commandManager.registerSubcommand(owner, "generate", "redo", "Redo your last undone generation.",
                builder -> builder.permission(Permissions.GENERATOR_USE),
                context -> handleHistoryAction(context.sender().getSender(), false));

        for (GeneratorType generatorType : GeneratorType.values()) {
            String generatorName = generatorType.getCommandName();
            commandManager.registerSubcommand(owner, "generate", generatorName,
                    "Generate " + generatorType.getName().toLowerCase() + " structures.",
                    builder -> builder.permission(Permissions.GENERATOR_USE)
                            .optional("arguments", StringParser.greedyStringParser(),
                                    DefaultValue.constant(""),
                                    org.incendo.cloud.minecraft.extras.RichDescription.of(
                                            Component.text("Generator flags and values")),
                                    SuggestionProvider.blockingStrings((context, input) ->
                                            BttCommandManager.matchingSuggestions(
                                                    HELP_ARGUMENTS, input.remainingInput()))),
                    context -> runGeneratorCommand(context.sender().getSender(), generatorType,
                            context.<String>optional("arguments").orElse("")));
        }
    }

    private void execute(@NotNull CommandSender sender, String @NotNull [] args) {
        Player player = playerWithPermission(sender);
        if (player == null) return;

        if (args.length == 0) {
            new GeneratorMenu(player, true);
        } else {
            sendHelp(player);
        }
    }

    public static void sendHelp(CommandSender sender) {
        String[] generatorHelp = getGeneratorHelpCommands();
        MessageBoxes.send(
                sender,
                plain(sender, MessageKeys.Generator.Command.HELP_BOX_TITLE),
                () -> sender.sendMessage(Messages.component(
                                MessageKeys.Generator.Command.HELP_GENERATORS,
                                Component.text(generatorHelp[0]),
                                Component.text(generatorHelp[1]),
                                Component.text(generatorHelp[2]),
                                Component.text(generatorHelp[3]),
                                Component.text(generatorHelp[4]))
                        .appendNewline()
                        .append(Messages.component(
                                MessageKeys.Generator.Command.HELP_HISTORY,
                                Component.text("/btt generate history"),
                                Component.text("/btt generate undo"),
                                Component.text("/btt generate redo"))))
        );
    }

    private void handleHistoryCommand(CommandSender sender) {
        Player player = playerWithPermission(sender);
        if (player == null) return;

        List<HistoryEntry> entries = GeneratorModule.getInstance().getPlayerHistory(player).getHistoryEntries();
        if (entries.isEmpty()) {
            player.sendMessage(Messages.component(MessageKeys.Generator.Command.HISTORY_EMPTY));
            return;
        }

        MessageBoxes.send(
                sender,
                plain(player, MessageKeys.Generator.Command.HISTORY_BOX_TITLE, Component.text(player.getName())),
                () -> {
                    for (HistoryEntry history : entries) {
                        long timeDifference = System.currentTimeMillis() - history.getTimeCreated();
                        player.sendMessage(Messages.component(
                                MessageKeys.Generator.Command.HISTORY_ENTRY,
                                Component.text(history.getGeneratorType().name()),
                                Component.text(Utils.toDate(timeDifference)),
                                Component.text(String.valueOf(history.getWorldEditCommandCount()))
                        ));
                    }
                }
        );
    }

    private static String plain(CommandSender sender, String key, ComponentLike... args) {
        Audience audience = sender instanceof Audience adventureAudience ? adventureAudience : null;
        return PlainTextComponentSerializer.plainText().serialize(
                Messages.render(audience, Messages.component(key, args)));
    }

    private void handleHistoryAction(CommandSender sender, boolean undo) {
        Player player = playerWithPermission(sender);
        if (player == null) return;
        if (undo) {
            GeneratorModule.getInstance().getPlayerHistory(player).undoCommand(player);
        } else {
            GeneratorModule.getInstance().getPlayerHistory(player).redoCommand(player);
        }
    }

    private void runGeneratorCommand(CommandSender sender, GeneratorType generatorType, String arguments) {
        Player player = playerWithPermission(sender);
        if (player == null) return;

        if (generatorType == GeneratorType.FIELD) {
            player.sendMessage(Messages.component(MessageKeys.Generator.Command.FIELD_DISABLED));
            return;
        }

        generatorType.getComponent(GeneratorModule.getInstance()).analyzeCommand(
                player, prependGeneratorName(generatorType.getCommandName(), arguments));
    }

    private static Player playerWithPermission(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Messages.playerOnly());
            return null;
        }
        if (!player.hasPermission(Permissions.GENERATOR_USE)) {
            player.sendMessage(Messages.noPermission(Permissions.GENERATOR_USE));
            return null;
        }
        return player;
    }

    private static String[] prependGeneratorName(String generatorName, String arguments) {
        String[] remaining = arguments.isBlank() ? new String[0] : arguments.trim().split("\\s+");
        String[] commandArguments = new String[remaining.length + 1];
        commandArguments[0] = generatorName;
        System.arraycopy(remaining, 0, commandArguments, 1, remaining.length);
        return commandArguments;
    }

    private List<String> suggestions(CommandSender sender, String input) {
        if (!sender.hasPermission(Permissions.GENERATOR_USE)) return List.of();
        List<String> topLevel = new ArrayList<>();
        Arrays.stream(GeneratorType.values()).map(GeneratorType::getCommandName).forEach(topLevel::add);
        topLevel.addAll(HISTORY_COMMANDS);

        String[] arguments = input.trim().split("\\s+");
        if (input.isBlank() || (arguments.length == 1 && !input.endsWith(" ")))
            return BttCommandManager.matchingSuggestions(topLevel, input);
        if (arguments.length == 1 && GeneratorType.fromCommandName(arguments[0]) != null)
            return HELP_ARGUMENTS;
        if (arguments.length == 2 && GeneratorType.fromCommandName(arguments[0]) != null)
            return BttCommandManager.matchingSuggestions(HELP_ARGUMENTS, arguments[1]);
        return List.of();
    }

    private static String[] getGeneratorHelpCommands() {
        return Arrays.stream(GeneratorType.values())
                .map(type -> "/btt generate " + type.getCommandName() + " help")
                .toArray(String[]::new);
    }
}

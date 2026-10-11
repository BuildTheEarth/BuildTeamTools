package net.buildtheearth.buildteamtools.modules.generator.commands;

import com.alpsbte.alpslib.utils.ChatHelper;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.generator.GeneratorModule;
import net.buildtheearth.buildteamtools.modules.Module;
import net.buildtheearth.buildteamtools.modules.common.commands.BttCommandManager;
import net.buildtheearth.buildteamtools.modules.generator.menu.GeneratorMenu;
import net.buildtheearth.buildteamtools.modules.generator.model.GeneratorType;
import net.buildtheearth.buildteamtools.modules.generator.model.HistoryEntry;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import net.buildtheearth.buildteamtools.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;
import org.incendo.cloud.component.DefaultValue;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.SuggestionProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
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
        ChatHelper.sendMessageBox(
                sender,
                "Generator Command",
                () -> sender.sendMessage(ChatHelper.getStandardComponent(
                                false,
                                "Generators: " + createPlaceholders(GeneratorType.values().length),
                                (Object[]) getGeneratorHelpCommands())
                        .appendNewline()
                        .append(ChatHelper.getStandardComponent(
                                false,
                                "History: %s, %s, %s",
                                "/btt generate history",
                                "/btt generate undo",
                                "/btt generate redo")))
        );
    }

    private void handleHistoryCommand(CommandSender sender) {
        Player player = playerWithPermission(sender);
        if (player == null) return;

        List<HistoryEntry> entries = GeneratorModule.getInstance().getPlayerHistory(player).getHistoryEntries();
        if (entries.isEmpty()) {
            player.sendMessage(ChatHelper.PREFIX_COMPONENT.append(ChatHelper.getErrorComponent(
                    "You didn't generate any structures yet. Use /btt generate to create one."
            )));
            return;
        }

        ChatHelper.sendMessageBox(sender, "Generator History for " + player.getName(), () -> {
            for (HistoryEntry history : entries) {
                long timeDifference = System.currentTimeMillis() - history.getTimeCreated();
                player.sendMessage(ChatHelper.getStandardComponent(
                        false,
                        "- %s - %s ago - %s Commands executed",
                        history.getGeneratorType().name(),
                        Utils.toDate(timeDifference),
                        history.getWorldEditCommandCount()
                ));
            }
        });
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
            player.sendMessage(ChatHelper.PREFIX_COMPONENT.append(ChatHelper.getErrorComponent(
                    "This generator has serious issues and is currently disabled."
            )));
            return;
        }

        generatorType.getComponent(GeneratorModule.getInstance()).analyzeCommand(
                player, prependGeneratorName(generatorType.getCommandName(), arguments));
    }

    private static Player playerWithPermission(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatHelper.PREFIX_COMPONENT.append(ChatHelper.getErrorComponent(
                    "Only players can execute this command."
            )));
            return null;
        }
        if (!player.hasPermission(Permissions.GENERATOR_USE)) {
            player.sendMessage(ChatHelper.getErrorString("You don't have permission to use this command!"));
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

    private static String createPlaceholders(int count) {
        return String.join(", ", Collections.nCopies(count, "%s"));
    }
}

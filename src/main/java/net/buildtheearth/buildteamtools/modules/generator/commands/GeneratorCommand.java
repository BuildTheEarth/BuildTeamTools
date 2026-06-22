package net.buildtheearth.buildteamtools.modules.generator.commands;

import com.alpsbte.alpslib.utils.ChatHelper;
import net.buildtheearth.buildteamtools.modules.generator.GeneratorModule;
import net.buildtheearth.buildteamtools.modules.generator.menu.GeneratorMenu;
import net.buildtheearth.buildteamtools.modules.generator.model.HistoryEntry;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import net.buildtheearth.buildteamtools.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GeneratorCommand implements CommandExecutor, TabCompleter {

    private static final List<String> SUB_COMMANDS = List.of(
            "house",
            "road",
            "rail",
            "tree",
            "field",
            "history",
            "undo",
            "redo"
    );

    private static final List<String> HELP_ARGUMENTS = List.of("help", "info", "?");

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String cmdLabel, String @NotNull [] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(ChatHelper.PREFIX_COMPONENT.append(ChatHelper.getErrorComponent(
                    "Only players can execute this command."
            )));
            return true;
        }

        if (!p.hasPermission(Permissions.GENERATOR_USE)) {
            p.sendMessage(ChatHelper.getErrorString("You don't have permission to use this command!"));
            return true;
        }

        if (args.length == 0) {
            new GeneratorMenu(p, true);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "house":
                GeneratorModule.getInstance().getHouse().analyzeCommand(p, args);
                return true;

            case "road":
                GeneratorModule.getInstance().getRoad().analyzeCommand(p, args);
                return true;

            case "rail":
                GeneratorModule.getInstance().getRail().analyzeCommand(p, args);
                return true;

            case "tree":
                GeneratorModule.getInstance().getTree().analyzeCommand(p, args);
                return true;

            case "field":
                p.sendMessage(ChatHelper.PREFIX_COMPONENT.append(ChatHelper.getErrorComponent(
                        "This generator has serious issues and is currently disabled."
                )));
                return true;

            case "history":
                if (GeneratorModule.getInstance().getPlayerHistory(p).getHistoryEntries().isEmpty()) {
                    p.sendMessage(ChatHelper.PREFIX_COMPONENT.append(ChatHelper.getErrorComponent(
                            "You didn't generate any structures yet. Use /gen to create one."
                    )));
                    return true;
                }

                ChatHelper.sendMessageBox(sender, "Generator History for " + p.getName(), () -> {
                    for (HistoryEntry history : GeneratorModule.getInstance().getPlayerHistory(p).getHistoryEntries()) {
                        long timeDifference = System.currentTimeMillis() - history.getTimeCreated();
                        p.sendMessage(ChatHelper.getStandardComponent(
                                false,
                                "- %s - %s ago - %s Commands executed",
                                history.getGeneratorType().name(),
                                Utils.toDate(timeDifference),
                                history.getWorldEditCommandCount()
                        ));
                    }
                });
                return true;

            case "undo":
                GeneratorModule.getInstance().getPlayerHistory(p).undoCommand(p);
                return true;

            case "redo":
                GeneratorModule.getInstance().getPlayerHistory(p).redoCommand(p);
                return true;

            default:
                sendHelp(p);
                return true;
        }
    }

    public static void sendHelp(CommandSender sender) {
        ChatHelper.sendMessageBox(
                sender,
                "Generator Command",
                () -> sender.sendMessage(ChatHelper.getStandardComponent(
                                false,
                                "Generators: %s, %s, %s, %s, %s",
                                "/gen house help",
                                "/gen road help",
                                "/gen rail help",
                                "/gen tree help",
                                "/gen field help")
                        .appendNewline()
                        .append(ChatHelper.getStandardComponent(
                                false,
                                "History: %s, %s, %s",
                                "/gen history",
                                "/gen undo",
                                "/gen redo")))
        );
    }

    @Override
    public List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            String @NotNull [] args
    ) {
        if (!sender.hasPermission(Permissions.GENERATOR_USE))
            return List.of();

        if (args.length == 1)
            return getMatchingCompletions(SUB_COMMANDS, args[0]);

        if (args.length == 2 && isGeneratorSubCommand(args[0]))
            return getMatchingCompletions(HELP_ARGUMENTS, args[1]);

        return List.of();
    }

    private boolean isGeneratorSubCommand(String value) {
        return value.equalsIgnoreCase("house")
                || value.equalsIgnoreCase("road")
                || value.equalsIgnoreCase("rail")
                || value.equalsIgnoreCase("tree")
                || value.equalsIgnoreCase("field");
    }

    private List<String> getMatchingCompletions(List<String> options, String input) {
        String normalizedInput = input.toLowerCase();
        List<String> completions = new ArrayList<>();

        for (String option : options)
            if (option.startsWith(normalizedInput))
                completions.add(option);

        return completions;
    }
}

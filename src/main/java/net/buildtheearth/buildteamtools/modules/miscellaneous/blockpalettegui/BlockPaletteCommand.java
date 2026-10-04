package net.buildtheearth.buildteamtools.modules.miscellaneous.blockpalettegui;

import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.common.commands.BttCommandManager;
import net.buildtheearth.buildteamtools.modules.Module;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.component.DefaultValue;
import org.incendo.cloud.minecraft.extras.RichDescription;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.SuggestionProvider;
import org.jspecify.annotations.NonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * /bp
 * /bp menu
 * /bp filter
 * /bp filter <filter1> <filter2> …
 */
public class BlockPaletteCommand {

    private final Supplier<BlockPaletteManager> blockPalletManager;
    private final JavaPlugin plugin;

    public BlockPaletteCommand(Supplier<BlockPaletteManager> blockPalletManager, JavaPlugin plugin) {
        this.blockPalletManager = blockPalletManager;
        this.plugin = plugin;
    }

    public void register(Module owner) {
        var commandManager = BuildTeamTools.getInstance().getCommandManager();
        commandManager.register(owner, "blockpalette", Permissions.BLOCK_PALETTE_USE, this::execute,
                (sender, input) -> {
                    String[] arguments = input.trim().split("\\s+");
                    if (input.isBlank() || arguments.length == 1)
                        return BttCommandManager.matchingSuggestions(List.of("menu", "filter"), input);
                    return List.of();
                });
        commandManager.registerSubcommand(owner, "blockpalette", "menu",
                "Open the block palette and reset its filters.", builder ->
                        builder.permission(Permissions.BLOCK_PALETTE_USE), context -> {
                    CommandSender sender = context.sender().getSender();
                    if (sender instanceof Player player)
                        blockPalletManager.get().setPlayerFiltersAndOpen(player);
                    else
                        sender.sendMessage("This command can only be used by players.");
                });
        commandManager.registerSubcommand(owner, "blockpalette", "filter",
                "Open the filter menu, optionally applying filters first.", builder ->
                    builder.permission(Permissions.BLOCK_PALETTE_USE)
                            .optional("filters", StringParser.greedyStringParser(),
                                    DefaultValue.constant(""),
                                    RichDescription.of(Component.text("Filter names")),
                                    SuggestionProvider.blockingStrings((context, input) ->
                                            BttCommandManager.matchingSuggestions(
                                                    BlockPaletteMenuType.FILTER_OPTIONS,
                                                    input.remainingInput()
                                            ))), context -> {
                    CommandSender sender = context.sender().getSender();
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage("This command can only be used by players.");
                        return;
                    }

                    String filterInput = context.<String>optional("filters").orElse("");
                    BlockPaletteManager manager = blockPalletManager.get();
                    if (!filterInput.isBlank()) {
                        Set<String> newFilters = new HashSet<>(List.of(filterInput.split("\\s+")));
                        manager.updatePlayerFilters(player, newFilters);
                    }
                    new ChoosePaletteMenu(manager, player, plugin).open();
                });
    }

    private void execute(@NonNull CommandSender sender, String @NonNull [] args) {
        if (!Permissions.checkPermission(sender, Permissions.BLOCK_PALETTE_USE)) return;
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return;
        }

        BlockPaletteManager manager = blockPalletManager.get();
        // /bp ⇒ open block menu with remembered filters (do NOT reset to "color")
        if (args.length == 0) {
            manager.openBlockMenu(player);
            return;
        }

        sender.sendMessage("§cUsage: §7/btt blockpalette\n"
                + "§c   or §7/btt blockpalette menu\n"
                + "§c   or §7/btt blockpalette filter\n"
                + "§c   or §7/btt blockpalette filter <filter1> <filter2> …");
    }
}

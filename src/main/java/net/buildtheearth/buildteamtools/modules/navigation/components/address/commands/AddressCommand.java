package net.buildtheearth.buildteamtools.modules.navigation.components.address.commands;

import com.alpsbte.alpslib.utils.ChatHelper;
import net.buildtheearth.Projection;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.Module;
import net.buildtheearth.buildteamtools.modules.common.commands.BttCommandManager;
import net.buildtheearth.buildteamtools.modules.network.api.PhotonAPI;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import net.buildtheearth.model.GeographicalCoordinate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.parser.standard.StringParser;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class AddressCommand {
    private static final String COMMAND_NAME = "address";
    private static final String PLAYER_ONLY_MESSAGE = "This command can only be used by a player!";

    public void register(Module owner) {
        var commandManager = BuildTeamTools.getInstance().getCommandManager();
        commandManager.register(owner, COMMAND_NAME, this::execute,
                (sender, input) -> {
                    List<String> options = new java.util.ArrayList<>();
                    if (sender.hasPermission(Permissions.ADDRESS_GET)) options.add("get");
                    if (sender.hasPermission(Permissions.ADDRESS_TELEPORT)) options.add("teleport");
                    return BttCommandManager.matchingSuggestions(options, input);
                });
        commandManager.registerSubcommand(owner, COMMAND_NAME, "get",
                "Look up the address at your current location.",
                builder -> builder.permission(Permissions.ADDRESS_GET),
                context -> {
                    if (context.sender().getSender() instanceof Player player)
                        handleGetCommand(player);
                    else
                        context.sender().getSender().sendMessage(
                                ChatHelper.getErrorComponent(PLAYER_ONLY_MESSAGE));
                });
        commandManager.registerSubcommand(owner, COMMAND_NAME, "teleport",
                "Find an address and teleport to its coordinates.",
                builder -> builder.permission(Permissions.ADDRESS_TELEPORT)
                        .required(COMMAND_NAME, StringParser.greedyStringParser(),
                                org.incendo.cloud.minecraft.extras.RichDescription.of(
                                        Component.text("Address to search for"))),
                context -> {
                    if (context.sender().getSender() instanceof Player player)
                        handleTeleportCommand(player, context.get(COMMAND_NAME));
                    else
                        context.sender().getSender().sendMessage(
                                ChatHelper.getErrorComponent(PLAYER_ONLY_MESSAGE));
                });
    }

    private void execute(@NonNull CommandSender sender, String @NonNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatHelper.getErrorComponent(PLAYER_ONLY_MESSAGE));
            return;
        }

        player.sendMessage(ChatHelper.getErrorComponent("Usage: /btt address <get|teleport>"));
    }

    private void handleGetCommand(@NonNull Player player) {
        if (!player.hasPermission(Permissions.ADDRESS_GET)) {
            Permissions.sendNoPermissionMessage(player, Permissions.ADDRESS_GET);
            return;
        }

        player.sendMessage("Getting closest address...");

        try {
            Location loc = player.getLocation();

            GeographicalCoordinate geoCoord = Projection.toGeo(
                    loc.getX(),
                    loc.getZ()
            );

            PhotonAPI.getAddressFromCoordinatesAsync(geoCoord)
                    .thenAccept(address -> {
                        if (address.isBlank()) {
                            player.sendMessage(
                                    ChatHelper.getErrorComponent(
                                            "No address found for your location."
                                    )
                            );
                            return;
                        }
                        Component message = Component.text()
                                .append(Component.text("Address: ", NamedTextColor.GRAY))
                                .append(
                                        Component.text(address, NamedTextColor.WHITE)
                                                .hoverEvent(HoverEvent.showText(
                                                        Component.text("Clip to copy", NamedTextColor.GRAY)
                                                ))
                                                .clickEvent(ClickEvent.copyToClipboard(address))
                                )
                                .build();
                        player.sendMessage(message);
                    })
                    .exceptionally(error -> {
                        ChatHelper.logError(
                                "Failed to retrieve address: %s",
                                error.getMessage()
                        );

                        player.sendMessage(
                                ChatHelper.getErrorComponent(
                                        "Failed to retrieve closest address."
                                )
                        );

                        return null;
                    });

        } catch (Exception e) {
            ChatHelper.logError(
                    "Failed to convert player location to geographical coordinates: %s",
                    e.getMessage()
            );

            player.sendMessage(
                    ChatHelper.getErrorComponent(
                            "Failed to retrieve closest address."
                    )
            );
        }

    }

    private void handleTeleportCommand(@NonNull Player player, String address) {
        if (!player.hasPermission(Permissions.ADDRESS_TELEPORT)) {
            Permissions.sendNoPermissionMessage(player, Permissions.ADDRESS_TELEPORT);
            return;
        }

        String lang = player.locale().getLanguage();

        PhotonAPI.getCoordinatesFromAddressAsync(address, lang)
                .thenAccept(coordinates -> {
                    double latitude = coordinates.latitude();
                    double longitude = coordinates.longitude();
                    player.sendMessage(ChatHelper.getStandardComponent(false,"Address found, teleporting..."));
                    Bukkit.getScheduler().runTask(
                            BuildTeamTools.getInstance(),
                            () -> {
                                player.sendMessage("Address found: " + address);
                                player.sendMessage("Teleporting...");
                                player.performCommand(
                                        "tpll " + latitude + " " + longitude
                                );
                            }
                    );
                })
                .exceptionally(error -> {
                    player.sendMessage(ChatHelper.getErrorComponent(error.getMessage()));
                    return null;
                });

    }
}

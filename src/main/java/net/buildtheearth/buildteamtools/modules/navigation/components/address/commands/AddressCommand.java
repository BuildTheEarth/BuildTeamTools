package net.buildtheearth.buildteamtools.modules.navigation.components.address.commands;

import com.alpsbte.alpslib.utils.ChatHelper;
import net.buildtheearth.Projection;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import net.buildtheearth.buildteamtools.i18n.Messages;
import net.buildtheearth.buildteamtools.modules.Module;
import net.buildtheearth.buildteamtools.modules.common.commands.BttCommandManager;
import net.buildtheearth.buildteamtools.modules.common.metrics.FeatureUsage;
import net.buildtheearth.buildteamtools.modules.common.metrics.UsageDetail;
import net.buildtheearth.buildteamtools.modules.network.api.PhotonAPI;
import net.buildtheearth.buildteamtools.modules.network.api.NominatimAPI;
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
import org.incendo.cloud.parser.flag.CommandFlag;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.parser.standard.StringParser;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;

public class AddressCommand {
    private static final String COMMAND_NAME = "address";

    public void register(Module owner) {
        var commandManager = BuildTeamTools.getInstance().getCommandManager();
        commandManager.register(owner, COMMAND_NAME,
                Permission.anyOf(
                        Permission.of(Permissions.ADDRESS_GET),
                        Permission.of(Permissions.ADDRESS_TELEPORT)
                ),
                this::execute,
                (sender, input) -> {
                    List<String> options = new java.util.ArrayList<>();
                    if (sender.hasPermission(Permissions.ADDRESS_GET)) options.add("get");
                    if (sender.hasPermission(Permissions.ADDRESS_TELEPORT)) options.add("teleport");
                    return BttCommandManager.matchingSuggestions(options, input);
                });
        commandManager.registerSubcommand(owner, COMMAND_NAME, "get",
                "Look up the address at your current location.",
                builder -> builder
                        .permission(Permissions.ADDRESS_GET)
                        .flag(CommandFlag.builder("international").build()),
                context -> {
                    if (context.sender().getSender() instanceof Player player) {
                        boolean international = context.flags().isPresent("international");
                        handleGetCommand(player, international);
                    } else
                        context.sender().getSender().sendMessage(Messages.playerOnly());
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
                        context.sender().getSender().sendMessage(Messages.playerOnly());
                });
    }

    private void execute(@NonNull CommandSender sender, String @NonNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Messages.playerOnly());
            return;
        }

        player.sendMessage(Messages.component(MessageKeys.Address.USAGE));
    }

    private void handleGetCommand(@NonNull Player player, boolean international) {
        if (!player.hasPermission(Permissions.ADDRESS_GET)) {
            Permissions.sendNoPermissionMessage(player, Permissions.ADDRESS_GET);
            return;
        }

        FeatureUsage.record(UsageDetail.ADDRESS_GET);
        player.sendMessage(Messages.component(MessageKeys.Address.GETTING_CLOSEST));

        final GeographicalCoordinate geoCoord;

        try {
            Location loc = player.getLocation();

            geoCoord = Projection.toGeo(
                    loc.getX(),
                    loc.getZ()
            );

        } catch (Exception e) {
            ChatHelper.logError(
                    "Failed to convert player location to geographical coordinates: %s",
                    e.getMessage()
            );

            player.sendMessage(Messages.component(MessageKeys.Address.CONVERT_FAILED));
            return;
        }

        try {
            NominatimAPI.getAddressFromCoordinatesAsync(geoCoord, international)
                    .thenAccept(address -> {
                        if (address.isBlank()) {
                            player.sendMessage(Messages.component(MessageKeys.Address.NOT_FOUND));
                            return;
                        }

                        Component message = Messages.component(MessageKeys.Address.LABEL)
                                .append(
                                        Component.text(address, NamedTextColor.WHITE)
                                                .hoverEvent(HoverEvent.showText(
                                                        Messages.component(MessageKeys.Address.CLICK_TO_COPY)
                                                ))
                                                .clickEvent(ClickEvent.copyToClipboard(address))
                                );

                        player.sendMessage(message);
                    })
                    .exceptionally(error -> {
                        ChatHelper.logError(
                                "Failed to retrieve address: %s",
                                error.getMessage()
                        );

                        player.sendMessage(Messages.component(MessageKeys.Address.RETRIEVE_FAILED));

                        return null;
                    });

        } catch (RejectedExecutionException e) {
            // The request queue is full or shutting down.
            ChatHelper.logError(
                    "Address request rejected: %s",
                    e.getMessage()
            );

            player.sendMessage(Messages.component(MessageKeys.Address.TOO_MANY_REQUESTS));
        }
    }

    private void handleTeleportCommand(@NonNull Player player, String address) {
        if (!player.hasPermission(Permissions.ADDRESS_TELEPORT)) {
            Permissions.sendNoPermissionMessage(player, Permissions.ADDRESS_TELEPORT);
            return;
        }

        FeatureUsage.record(UsageDetail.ADDRESS_TELEPORT);
        String lang = player.locale().getLanguage();

        PhotonAPI.getCoordinatesFromAddressAsync(address, lang)
                .thenAccept(coordinates -> {
                    double latitude = coordinates.latitude();
                    double longitude = coordinates.longitude();
                    player.sendMessage(Messages.component(MessageKeys.Address.FOUND_TELEPORTING));
                    Bukkit.getScheduler().runTask(
                            BuildTeamTools.getInstance(),
                            () -> {
                                player.sendMessage(Messages.component(
                                        MessageKeys.Address.FOUND,
                                        Component.text(address)));
                                player.sendMessage(Messages.component(MessageKeys.Address.TELEPORTING));
                                player.performCommand(
                                        "tpll " + latitude + " " + longitude
                                );
                            }
                    );
                })
                .exceptionally(error -> {
                    player.sendMessage(Messages.component(
                            MessageKeys.Address.ERROR,
                            Component.text(String.valueOf(error.getMessage()))));
                    return null;
                });

    }
}

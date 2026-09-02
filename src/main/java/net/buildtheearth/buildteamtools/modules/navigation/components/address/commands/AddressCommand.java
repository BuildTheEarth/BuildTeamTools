package net.buildtheearth.buildteamtools.modules.navigation.components.address.commands;

import com.alpsbte.alpslib.utils.ChatHelper;
import net.buildtheearth.Projection;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.buildtheearth.buildteamtools.modules.network.api.PhotonAPI;
import net.buildtheearth.buildteamtools.modules.network.model.Permissions;
import net.buildtheearth.buildteamtools.utils.Utils;
import net.buildtheearth.model.GeographicalCoordinate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AddressCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatHelper.getErrorComponent("This command can only be used by a player!"));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(ChatHelper.getErrorComponent("Usage: /address <get|teleport>"));
            return true;
        }

        if (args[0].equalsIgnoreCase("get")) {
            return handleGetCommand(player, args);
        }

        if (args[0].equalsIgnoreCase("teleport")) {
            return handleTeleportCommand(player, args);
        }

        player.sendMessage(ChatHelper.getErrorComponent("Usage: /address <get|teleport>"));
        return true;
    }

    private boolean handleGetCommand(@NonNull Player player, String @NonNull [] args) {
        if (!player.hasPermission(Permissions.ADDRESS_GET)) {
            Utils.sendNoPermissionMessage(player, Permissions.ADDRESS_GET);
            return true;
        }

        if (args.length > 1) {
            player.sendMessage(ChatHelper.getErrorComponent("Usage: /address get"));
            return true;
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


        return true;
    }

    private boolean handleTeleportCommand(@NonNull Player player, String @NonNull [] args) {
        if (!player.hasPermission(Permissions.ADDRESS_TELEPORT)) {
            Utils.sendNoPermissionMessage(player, Permissions.ADDRESS_TELEPORT);
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(ChatHelper.getErrorComponent("Usage: /address teleport <address>"));
            return true;
        }

        String lang = player.locale().getLanguage();
        String address = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

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

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String @NonNull [] args) {
        if (args.length != 1) {
            return Collections.emptyList();
        }
        List<String> list = new ArrayList<>();

        if (sender.hasPermission(Permissions.ADDRESS_GET)) list.add("get");
        if (sender.hasPermission(Permissions.ADDRESS_TELEPORT)) {
            list.add("teleport");
            list.add("teleport <id> <street> <city>");
        }


        return list.stream()
                .filter(entry -> entry.startsWith(args[0].toLowerCase()))
                .toList();
    }
}

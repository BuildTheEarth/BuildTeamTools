package net.buildtheearth.buildteamtools.modules.network.api;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Resolves a build team's main server IP to its TerraBungee server name.
 */
public final class MainServerResolver {

    private MainServerResolver() {
    }

    public static @NonNull Resolution resolve(@Nullable String mainServerIP,
                                               @Nullable Collection<ServerEntry> servers) {
        if (servers == null) {
            return new Resolution(null, List.of());
        }

        List<String> registeredServerIPs = servers.stream()
                .filter(Objects::nonNull)
                .map(ServerEntry::ip)
                .filter(Objects::nonNull)
                .toList();

        if (mainServerIP == null) {
            return new Resolution(null, registeredServerIPs);
        }

        String serverName = servers.stream()
                .filter(Objects::nonNull)
                .filter(server -> mainServerIP.equals(server.ip()))
                .map(ServerEntry::name)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        return new Resolution(serverName, registeredServerIPs);
    }

    public record ServerEntry(@Nullable String name, @Nullable String ip) {
    }

    public record Resolution(@Nullable String serverName,
                             @NonNull List<String> registeredServerIPs) {
    }
}

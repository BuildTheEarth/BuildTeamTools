package net.buildtheearth.buildteamtools.modules.network.api;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MainServerResolverTest {

    @Test
    void resolvesMatchingMainServer() {
        MainServerResolver.Resolution result = MainServerResolver.resolve(
                "51.222.244.19:23005",
                List.of(new MainServerResolver.ServerEntry("NYC-1", "51.222.244.19:23005")));

        assertEquals("NYC-1", result.serverName());
        assertEquals(List.of("51.222.244.19:23005"), result.registeredServerIPs());
    }

    @Test
    void exposesMismatchingRegisteredAddressForDiagnostics() {
        MainServerResolver.Resolution result = MainServerResolver.resolve(
                "51.222.244.19:23005",
                List.of(new MainServerResolver.ServerEntry("NYC-1", "142.44.137.53:25590")));

        assertNull(result.serverName());
        assertEquals(List.of("142.44.137.53:25590"), result.registeredServerIPs());
    }

    @Test
    void handlesMissingMainServerIp() {
        MainServerResolver.Resolution result = MainServerResolver.resolve(
                null,
                List.of(new MainServerResolver.ServerEntry("NYC-1", "51.222.244.19:23005")));

        assertNull(result.serverName());
        assertEquals(List.of("51.222.244.19:23005"), result.registeredServerIPs());
    }

    @Test
    void ignoresMalformedServerEntries() {
        MainServerResolver.Resolution result = MainServerResolver.resolve(
                "51.222.244.19:23005",
                Arrays.asList(
                        null,
                        new MainServerResolver.ServerEntry(null, null),
                        new MainServerResolver.ServerEntry(null, "51.222.244.19:23005"),
                        new MainServerResolver.ServerEntry("NYC-1", "51.222.244.19:23005")));

        assertEquals("NYC-1", result.serverName());
        assertEquals(List.of("51.222.244.19:23005", "51.222.244.19:23005"), result.registeredServerIPs());
    }

    @Test
    void handlesMissingServerList() {
        MainServerResolver.Resolution result = MainServerResolver.resolve("51.222.244.19:23005", null);

        assertNull(result.serverName());
        assertEquals(List.of(), result.registeredServerIPs());
    }
}

package net.buildtheearth.buildteamtools.modules.network.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BuildTeamRoutingStateTest {

    @Test
    void connectedTeamWithMatchingServerHasNetworkRoute() {
        BuildTeam team = team("51.222.244.19:23005", "NYC-1", true);

        assertFalse(team.isNetworkMappingMismatch());
        assertEquals("NYC-1", team.getServerName());
    }

    @Test
    void connectedTeamWithUnmappedMainIpIsMarkedAsInconsistent() {
        BuildTeam team = team("51.222.244.19:23005", null, true);

        assertTrue(team.isNetworkMappingMismatch());
        assertEquals("51.222.244.19:23005", team.getConfiguredMainServerIP());
    }

    @Test
    void connectedTeamWithoutMainIpIsNotReportedAsMappingMismatch() {
        BuildTeam team = team(null, null, true);

        assertFalse(team.isNetworkMappingMismatch());
        assertNull(team.getConfiguredMainServerIP());
    }

    @Test
    void externalTeamRetainsTransferAddress() {
        BuildTeam team = team("example.org:25565", null, false);

        assertFalse(team.isNetworkMappingMismatch());
        assertEquals("example.org:25565", team.getIP());
    }

    private BuildTeam team(String mainServerIP, String serverName, boolean connected) {
        return new BuildTeam(
                "Qy2duN4l",
                mainServerIP,
                "§3Team New York City",
                "BTE New York City",
                serverName,
                connected,
                false,
                true,
                "NYC");
    }
}

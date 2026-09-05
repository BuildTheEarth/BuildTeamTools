package net.buildtheearth.buildteamtools.modules.generator.components.rail.configuration;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RailTypeNamingTest {
    @Test
    void renamedFirstCopyFreesItsDisplayName() {
        assertEquals("Custom Rail 2", RailTypeManager.getNextCustomDisplayName(List.of("Standard", "Custom Rail 1")));
        assertEquals("Custom Rail 1", RailTypeManager.getNextCustomDisplayName(List.of("Standard", "Regional Railway")));
    }

    @Test
    void reusesLowestGapWithoutCaseInsensitiveCollisions() {
        assertEquals("Custom Rail 2", RailTypeManager.getNextCustomDisplayName(
                List.of("CUSTOM RAIL 1", "Custom Rail 3", "Custom Rail 4")));
        assertEquals("Custom Rail 1", RailTypeManager.getNextCustomDisplayName(List.of()));
    }
}

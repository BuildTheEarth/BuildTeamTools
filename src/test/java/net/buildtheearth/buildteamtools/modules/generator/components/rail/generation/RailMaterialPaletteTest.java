package net.buildtheearth.buildteamtools.modules.generator.components.rail.generation;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RailMaterialPaletteTest {
    @Test
    void eachAxisUsesTheWholeMixAndRepeatedPositionsStayStable() {
        List<String> palette = List.of("a", "b", "c");
        for (int axis = 0; axis < 3; axis++) {
            Set<String> seen = new HashSet<>();
            for (int coordinate = -128; coordinate <= 128; coordinate++) {
                int x = axis == 0 ? coordinate : 0;
                int y = axis == 1 ? coordinate : 0;
                int z = axis == 2 ? coordinate : 0;
                String selected = RailMaterialPalette.select(palette, x, y, z);
                assertEquals(selected, RailMaterialPalette.select(palette, x, y, z));
                seen.add(selected);
            }
            assertEquals(Set.copyOf(palette), seen);
        }
    }

    @Test
    void handlesSingleMaterialExtremeCoordinatesAndEmptyPalette() {
        assertEquals("only", RailMaterialPalette.select(List.of("only"), Integer.MIN_VALUE, 0, Integer.MAX_VALUE));
        assertTrue(List.of("a", "b").contains(RailMaterialPalette.select(List.of("a", "b"), Integer.MIN_VALUE, Integer.MAX_VALUE, -1)));
        assertThrows(IllegalArgumentException.class, () -> RailMaterialPalette.select(List.of(), 0, 0, 0));
    }
}

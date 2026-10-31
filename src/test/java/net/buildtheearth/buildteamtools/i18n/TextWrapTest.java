package net.buildtheearth.buildteamtools.i18n;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextWrapTest {

    private static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();

    @Test
    void wrapsLongPlainTextOntoMultipleLines() {
        List<String> lines = TextWrap.wrap(
                "Click to explore other build teams around the world from this menu.",
                20
        );
        assertTrue(lines.size() > 1);
        for (String line : lines) {
            assertTrue(TextWrap.visibleLengthForTest(line) <= 20, line);
        }
        assertEquals(
                "Click to explore other build teams around the world from this menu.",
                String.join(" ", lines)
        );
    }

    @Test
    void preservesExplicitNewlines() {
        assertEquals(
                List.of("First line", "Second line that is longer"),
                TextWrap.wrap("First line\nSecond line that is longer", 40)
        );
    }

    @Test
    void wrapsStyledComponentsWithoutLegacyRoundTrip() {
        Component input = Component.text()
                .append(Component.text("Hello ", NamedTextColor.RED))
                .append(Component.text("world this wraps nicely", NamedTextColor.YELLOW))
                .build();

        List<Component> lines = TextWrap.wrap(input, 12);
        assertTrue(lines.size() >= 2);
        for (Component line : lines) {
            assertTrue(PLAIN.serialize(line).length() <= 12, PLAIN.serialize(line));
        }
        assertEquals("Hello world this wraps nicely", String.join(" ", lines.stream().map(PLAIN::serialize).toList()));
    }
}

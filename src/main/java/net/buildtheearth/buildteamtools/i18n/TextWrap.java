package net.buildtheearth.buildteamtools.i18n;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.flattener.FlattenerListener;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/**
 * Word-wrap helpers for inventory lore and other width-limited UI text.
 *
 * <p>Translated strings are often longer than English. Wrapping keeps menus readable without
 * forcing translators to insert manual line breaks for every language.
 */
public final class TextWrap {

    public static final int DEFAULT_LORE_WIDTH = 40;

    private static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();

    private TextWrap() {
    }

    /**
     * Wraps an Adventure component to {@code maxWidth} visible characters, preserving styles and
     * explicit newlines without legacy section/ampersand serialization.
     */
    public static List<Component> wrap(Component component, int maxWidth) {
        if (component == null) {
            return List.of();
        }
        if (maxWidth < 1) {
            throw new IllegalArgumentException("maxWidth must be >= 1");
        }

        List<Run> runs = flatten(component);
        if (runs.isEmpty()) {
            return List.of();
        }

        List<Component> lines = new ArrayList<>();
        List<Run> paragraph = new ArrayList<>();
        for (Run run : runs) {
            String text = run.text();
            int start = 0;
            for (int index = 0; index < text.length(); index++) {
                if (text.charAt(index) != '\n') {
                    continue;
                }
                if (index > start) {
                    paragraph.add(new Run(run.style(), text.substring(start, index)));
                }
                flushParagraph(paragraph, maxWidth, lines);
                paragraph.clear();
                start = index + 1;
            }
            if (start < text.length()) {
                paragraph.add(new Run(run.style(), text.substring(start)));
            }
        }
        flushParagraph(paragraph, maxWidth, lines);
        return List.copyOf(lines);
    }

    public static List<Component> wrapLore(Component component) {
        return wrap(component, DEFAULT_LORE_WIDTH);
    }

    /** Plain-text wrap (no styles). */
    public static List<String> wrap(String text, int maxWidth) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        List<Component> wrapped = wrap(Component.text(text), maxWidth);
        List<String> lines = new ArrayList<>(wrapped.size());
        for (Component line : wrapped) {
            lines.add(PLAIN.serialize(line));
        }
        return lines;
    }

    public static List<String> wrapLore(String text) {
        return wrap(text, DEFAULT_LORE_WIDTH);
    }

    static int visibleLengthForTest(String text) {
        return text == null ? 0 : text.length();
    }

    private static List<Run> flatten(Component component) {
        List<Run> runs = new ArrayList<>();
        Deque<Style> styles = new ArrayDeque<>();
        styles.push(Style.empty());

        ComponentFlattener.basic().flatten(component, new FlattenerListener() {
            @Override
            public void pushStyle(@NotNull Style style) {
                styles.push(Objects.requireNonNull(styles.peek()).merge(style));
            }

            @Override
            public void component(@NotNull String text) {
                if (text.isEmpty()) {
                    return;
                }
                runs.add(new Run(Objects.requireNonNull(styles.peek()), text));
            }

            @Override
            public void popStyle(@NotNull Style style) {
                styles.pop();
            }
        });
        return runs;
    }

    private static void flushParagraph(List<Run> paragraph, int maxWidth, List<Component> lines) {
        if (paragraph.isEmpty()) {
            return;
        }

        StringBuilder plain = new StringBuilder();
        List<Style> charStyles = new ArrayList<>();
        for (Run run : paragraph) {
            for (int index = 0; index < run.text().length(); index++) {
                plain.append(run.text().charAt(index));
                charStyles.add(run.style());
            }
        }

        String text = plain.toString();
        if (text.isBlank()) {
            return;
        }

        int pos = 0;
        final int length = text.length();
        while (pos < length) {
            while (pos < length && text.charAt(pos) == ' ') {
                pos++;
            }
            if (pos >= length) {
                break;
            }

            int lineStart = pos;
            int index = pos;
            int breakAt = -1;

            while (index < length) {
                if (index - lineStart > maxWidth) {
                    break;
                }
                if (text.charAt(index) == ' ') {
                    breakAt = index;
                }
                index++;
                if (index - lineStart > maxWidth) {
                    break;
                }
            }

            int lineEnd;
            if (index >= length && length - lineStart <= maxWidth) {
                lineEnd = length;
                pos = length;
            } else if (breakAt > lineStart) {
                lineEnd = breakAt;
                pos = breakAt + 1;
            } else {
                lineEnd = Math.min(lineStart + maxWidth, length);
                pos = lineEnd;
            }

            if (lineEnd > lineStart) {
                lines.add(buildLine(text, charStyles, lineStart, lineEnd));
            }
        }
    }

    private static Component buildLine(String text, List<Style> styles, int start, int end) {
        Component line = Component.empty();
        int index = start;
        while (index < end) {
            Style style = styles.get(index);
            int runEnd = index + 1;
            while (runEnd < end && styles.get(runEnd).equals(style)) {
                runEnd++;
            }
            line = line.append(Component.text(text.substring(index, runEnd), style));
            index = runEnd;
        }
        return line;
    }

    private record Run(Style style, String text) {
    }
}

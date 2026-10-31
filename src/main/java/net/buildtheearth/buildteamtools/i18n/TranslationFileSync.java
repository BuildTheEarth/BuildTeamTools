package net.buildtheearth.buildteamtools.i18n;

import net.kyori.adventure.translation.Translator;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Keeps editable translation files under {@code plugins/BuildTeamTools/lang/} in sync with
 * bundled classpath defaults.
 *
 * <p>On first run each bundled locale is copied as-is. On later runs, keys present in the JAR but
 * missing on disk are appended (existing edits are never overwritten). Extra
 * {@code messages_&lt;locale&gt;.properties} files dropped into the folder are loaded as additional
 * locales.
 */
public final class TranslationFileSync {

    public static final String DIRECTORY_NAME = "lang";
    public static final String FILE_PREFIX = "messages_";
    public static final String FILE_SUFFIX = ".properties";

    private static final Pattern PROPERTY_KEY = Pattern.compile("^\\s*([^#!\\s][^=]*?)\\s*=");
    private static final String ADDED_KEYS_HEADER = "# ---- Keys added by BuildTeamTools update ----";

    private TranslationFileSync() {
    }

    public record LocaleFile(Locale locale, Path path) {
    }

    public record SyncResult(List<LocaleFile> locales, int keysAdded) {
    }

    /**
     * Ensures the lang directory exists, syncs bundled locales from the classpath, then discovers
     * every {@code messages_*.properties} file in the folder.
     */
    public static SyncResult prepare(Path langDirectory, ClassLoader classLoader, Locale... bundledLocales)
            throws IOException {
        Files.createDirectories(langDirectory);

        int keysAdded = 0;
        for (Locale locale : bundledLocales) {
            keysAdded += syncBundledLocale(langDirectory, classLoader, locale);
        }

        Map<String, LocaleFile> discovered = new LinkedHashMap<>();
        try (var paths = Files.newDirectoryStream(langDirectory, FILE_PREFIX + "*" + FILE_SUFFIX)) {
            for (Path path : paths) {
                Locale locale = localeFromFileName(path.getFileName().toString());
                if (locale == null) {
                    continue;
                }
                discovered.put(locale.toString(), new LocaleFile(locale, path));
            }
        }

        List<LocaleFile> locales = new ArrayList<>(discovered.values());
        locales.sort(Comparator.comparing(file -> file.locale().toString()));
        return new SyncResult(List.copyOf(locales), keysAdded);
    }

    /** Copies a missing bundled file or merges newly introduced keys into an existing one. */
    static int syncBundledLocale(Path langDirectory, ClassLoader classLoader, Locale locale) throws IOException {
        String fileName = fileNameFor(locale);
        Path target = langDirectory.resolve(fileName);
        String resourcePath = classpathResourcePath(locale);

        if (!Files.exists(target)) {
            try (InputStream input = classLoader.getResourceAsStream(resourcePath)) {
                if (input == null) {
                    throw new IOException("Missing bundled translation resource: " + resourcePath);
                }
                Files.copy(input, target);
            }
            return 0;
        }

        try (InputStream input = classLoader.getResourceAsStream(resourcePath)) {
            if (input == null) {
                throw new IOException("Missing bundled translation resource: " + resourcePath);
            }
            return mergeMissingKeys(target, readPropertyLines(input));
        }
    }

    /**
     * Appends property lines for keys that exist in {@code bundledLines} but not in {@code target}.
     * Returns how many keys were added.
     */
    static int mergeMissingKeys(Path target, List<String> bundledLines) throws IOException {
        String content = Files.readString(target, StandardCharsets.UTF_8);
        Set<String> existingKeys = parseKeys(content);

        List<String> missingLines = new ArrayList<>();
        Set<String> queuedKeys = new LinkedHashSet<>();
        for (String line : bundledLines) {
            String key = keyFromLine(line);
            if (key == null || existingKeys.contains(key) || !queuedKeys.add(key)) {
                continue;
            }
            missingLines.add(line);
        }

        if (missingLines.isEmpty()) {
            return 0;
        }

        StringBuilder appendix = new StringBuilder();
        if (!content.isEmpty() && !content.endsWith("\n")) {
            appendix.append('\n');
        }
        appendix.append('\n');
        if (!content.contains(ADDED_KEYS_HEADER)) {
            appendix.append(ADDED_KEYS_HEADER).append('\n');
        }
        for (String line : missingLines) {
            appendix.append(line);
            if (!line.endsWith("\n")) {
                appendix.append('\n');
            }
        }

        Files.writeString(
                target,
                appendix.toString(),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.APPEND
        );
        return missingLines.size();
    }

    static String fileNameFor(Locale locale) {
        return FILE_PREFIX + locale.toString() + FILE_SUFFIX;
    }

    static @Nullable Locale localeFromFileName(String fileName) {
        if (!fileName.startsWith(FILE_PREFIX) || !fileName.endsWith(FILE_SUFFIX)) {
            return null;
        }
        String tag = fileName.substring(FILE_PREFIX.length(), fileName.length() - FILE_SUFFIX.length());
        if (tag.isBlank()) {
            return null;
        }
        return Translator.parseLocale(tag);
    }

    static String classpathResourcePath(Locale locale) {
        return Translations.BUNDLE_BASE_NAME.replace('.', '/') + "_" + locale + FILE_SUFFIX;
    }

    static List<String> readPropertyLines(InputStream input) throws IOException {
        String content = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        return List.of(content.split("\\R", -1));
    }

    static Set<String> parseKeys(String content) {
        Set<String> keys = new LinkedHashSet<>();
        for (String line : content.split("\\R", -1)) {
            String key = keyFromLine(line);
            if (key != null) {
                keys.add(key);
            }
        }
        return keys;
    }

    static @Nullable String keyFromLine(String line) {
        String trimmed = line.stripLeading();
        if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.startsWith("!")) {
            return null;
        }
        Matcher matcher = PROPERTY_KEY.matcher(line);
        if (!matcher.find()) {
            return null;
        }
        return unescapePropertyKey(matcher.group(1).strip());
    }

    /** Minimal {@code .properties} key unescaping for comparison with {@link java.util.Properties}. */
    private static String unescapePropertyKey(String key) {
        StringBuilder out = new StringBuilder(key.length());
        for (int i = 0; i < key.length(); i++) {
            char character = key.charAt(i);
            if (character == '\\' && i + 1 < key.length()) {
                out.append(key.charAt(++i));
            } else {
                out.append(character);
            }
        }
        return out.toString();
    }
}

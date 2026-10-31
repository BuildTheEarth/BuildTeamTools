package net.buildtheearth.buildteamtools.i18n;

import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.translation.MiniMessageTranslationStore;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.Translator;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Registers BuildTeamTools translations with Adventure's {@link GlobalTranslator}.
 *
 * <p>Editable copies live in {@code plugins/BuildTeamTools/lang/}. Bundled defaults are copied on
 * first enable; later plugin updates append any newly introduced keys without overwriting local
 * edits. Strings are UTF-8 {@code .properties} files parsed as MiniMessage, and the configured chat
 * prefix is available in every string as {@code <prefix>}.
 */
public final class Translations {

    public static final Key STORE_KEY = Key.key("buildteamtools", "messages");
    public static final String BUNDLE_BASE_NAME = "net.buildtheearth.buildteamtools.i18n.messages";
    public static final Locale DEFAULT_LOCALE = Locale.US;

    private static @Nullable MiniMessageTranslationStore store;

    private Translations() {
    }

    public static synchronized void enable(BuildTeamTools plugin) {
        if (store != null) {
            return;
        }
        registerStore(plugin);
    }

    /**
     * Re-reads {@code lang/} (after syncing bundled defaults) and replaces the live translation
     * store. Safe to call from {@code /btt reload-config}.
     */
    public static synchronized void reload(BuildTeamTools plugin) {
        disable();
        registerStore(plugin);
    }

    public static synchronized void disable() {
        MiniMessageTranslationStore translationStore = store;
        if (translationStore == null) {
            return;
        }
        GlobalTranslator.translator().removeSource(translationStore);
        store = null;
    }

    static boolean isEnabled() {
        return store != null;
    }

    static MiniMessage miniMessage() {
        return MiniMessage.builder()
                .editTags(tags -> tags.resolver(MessagePrefix.tagResolver()))
                .build();
    }

    private static void registerStore(BuildTeamTools plugin) {
        MiniMessageTranslationStore translationStore = MiniMessageTranslationStore.create(
                STORE_KEY,
                miniMessage()
        );
        translationStore.defaultLocale(DEFAULT_LOCALE);

        Path langDirectory = plugin.getDataFolder().toPath().resolve(TranslationFileSync.DIRECTORY_NAME);
        ClassLoader classLoader = plugin.getClass().getClassLoader();

        try {
            TranslationFileSync.SyncResult sync = TranslationFileSync.prepare(
                    langDirectory,
                    classLoader,
                    DEFAULT_LOCALE
            );

            if (sync.locales().isEmpty()) {
                registerClasspathBundle(translationStore, DEFAULT_LOCALE, classLoader);
                plugin.getComponentLogger().warn(
                        "No translation files found in {}; falling back to bundled {}",
                        langDirectory,
                        BUNDLE_BASE_NAME
                );
            } else {
                for (TranslationFileSync.LocaleFile localeFile : sync.locales()) {
                    registerFileBundle(translationStore, localeFile.locale(), localeFile.path());
                }
                if (sync.keysAdded() > 0) {
                    plugin.getComponentLogger().info(
                            "Added {} new translation key(s) to files under {}",
                            sync.keysAdded(),
                            langDirectory
                    );
                }
                plugin.getComponentLogger().info(
                        "Registered Adventure translations from {} ({} locale{})",
                        langDirectory,
                        sync.locales().size(),
                        sync.locales().size() == 1 ? "" : "s"
                );
            }
        } catch (IOException exception) {
            registerClasspathBundle(translationStore, DEFAULT_LOCALE, classLoader);
            plugin.getComponentLogger().error(
                    "Failed to prepare translation folder {}; using bundled classpath messages",
                    langDirectory,
                    exception
            );
        }

        GlobalTranslator.translator().addSource(translationStore);
        store = translationStore;
    }

    private static void registerFileBundle(
            MiniMessageTranslationStore translationStore,
            Locale locale,
            Path path
    ) throws IOException {
        try (InputStream input = Files.newInputStream(path);
             Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
            ResourceBundle bundle = new PropertyResourceBundle(reader);
            // MiniMessage strings are not MessageFormat patterns; do not escape quotes.
            translationStore.registerAll(locale, bundle, false);
        }
    }

    private static void registerClasspathBundle(
            MiniMessageTranslationStore translationStore,
            Locale locale,
            ClassLoader classLoader
    ) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale, classLoader);
            translationStore.registerAll(locale, bundle, false);
        } catch (MissingResourceException exception) {
            throw new IllegalStateException(
                    "Missing translation bundle '" + BUNDLE_BASE_NAME + "' for locale " + locale,
                    exception
            );
        }
    }

    /** Exposed for tests that need a temporary store without touching the live plugin. */
    public static Translator createStoreForTesting(ClassLoader classLoader) {
        MessagePrefix.apply(MessagePrefix.DEFAULT_MINI_MESSAGE);
        MiniMessageTranslationStore translationStore = MiniMessageTranslationStore.create(
                STORE_KEY,
                miniMessage()
        );
        translationStore.defaultLocale(DEFAULT_LOCALE);
        registerClasspathBundle(translationStore, DEFAULT_LOCALE, classLoader);
        return translationStore;
    }
}

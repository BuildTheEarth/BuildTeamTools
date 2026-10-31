package net.buildtheearth.buildteamtools.i18n;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TranslationFileSyncTest {

    @TempDir
    Path tempDir;

    @Test
    void copiesBundledLocaleWhenMissing() throws Exception {
        TranslationFileSync.SyncResult result = TranslationFileSync.prepare(
                tempDir,
                TranslationFileSyncTest.class.getClassLoader(),
                Locale.US
        );

        Path copied = tempDir.resolve("messages_en_US.properties");
        assertTrue(Files.exists(copied));
        assertEquals(0, result.keysAdded());
        assertEquals(1, result.locales().size());
        assertEquals(Locale.US, result.locales().getFirst().locale());
        assertTrue(Files.readString(copied, StandardCharsets.UTF_8).contains("btt.common.player-only="));
    }

    @Test
    void mergesMissingKeysWithoutOverwritingEdits() throws Exception {
        Path target = tempDir.resolve("messages_en_US.properties");
        Files.writeString(
                target,
                """
                        # custom file
                        btt.common.player-only=<prefix><red>CUSTOM PLAYER ONLY
                        btt.common.no-permission=<prefix><red>CUSTOM DENY <arg:0>
                        """,
                StandardCharsets.UTF_8
        );

        List<String> bundled = List.of(
                "# bundled",
                "btt.common.player-only=<prefix><red>This command can only be used by a player!",
                "btt.common.no-permission=<prefix><red>You don't have permission. <arg:0>",
                "btt.common.cache-updated=<prefix><gray>Cache successfully updated."
        );

        int added = TranslationFileSync.mergeMissingKeys(target, bundled);
        assertEquals(1, added);

        String content = Files.readString(target, StandardCharsets.UTF_8);
        assertTrue(content.contains("btt.common.player-only=<prefix><red>CUSTOM PLAYER ONLY"));
        assertTrue(content.contains("btt.common.cache-updated=<prefix><gray>Cache successfully updated."));
        assertTrue(content.contains("# ---- Keys added by BuildTeamTools update ----"));
        assertFalse(content.contains("This command can only be used by a player!"));
    }

    @Test
    void prepareDiscoversExtraLocales() throws Exception {
        Files.writeString(
                tempDir.resolve("messages_de_DE.properties"),
                "btt.common.player-only=<prefix><red>Nur Spieler!\n",
                StandardCharsets.UTF_8
        );

        TranslationFileSync.SyncResult result = TranslationFileSync.prepare(
                tempDir,
                TranslationFileSyncTest.class.getClassLoader(),
                Locale.US
        );

        assertEquals(2, result.locales().size());
        Set<String> tags = Set.copyOf(result.locales().stream().map(file -> file.locale().toString()).toList());
        assertTrue(tags.contains("en_US"));
        assertTrue(tags.contains("de_DE"));
    }

    @Test
    void localeFromFileNameParsesLanguageAndRegion() {
        assertEquals(Locale.US, TranslationFileSync.localeFromFileName("messages_en_US.properties"));
        assertEquals(Locale.GERMANY, TranslationFileSync.localeFromFileName("messages_de_DE.properties"));
        assertEquals(Locale.forLanguageTag("de"), TranslationFileSync.localeFromFileName("messages_de.properties"));
    }

    @Test
    void parseKeysSkipsComments() {
        Set<String> keys = TranslationFileSync.parseKeys("""
                # comment
                ! also comment
                btt.common.player-only=value
                btt.common.module-disabled-reason=Reason\\: <arg:0>
                """);
        assertEquals(Set.of("btt.common.player-only", "btt.common.module-disabled-reason"), keys);
    }
}

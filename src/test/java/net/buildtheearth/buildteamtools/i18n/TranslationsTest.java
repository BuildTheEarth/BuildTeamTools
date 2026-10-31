package net.buildtheearth.buildteamtools.i18n;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.Translator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TranslationsTest {

    private Translator store;

    @BeforeEach
    void setUp() {
        store = Translations.createStoreForTesting(TranslationsTest.class.getClassLoader());
        GlobalTranslator.translator().addSource(store);
    }

    @AfterEach
    void tearDown() {
        GlobalTranslator.translator().removeSource(store);
    }

    @Test
    void resourceBundleProvidesEnglishCommonMessagesWithConfiguredPrefix() {
        Component rendered = GlobalTranslator.render(
                Messages.component(MessageKeys.Common.PLAYER_ONLY),
                Locale.US
        );
        String plain = PlainTextComponentSerializer.plainText().serialize(rendered);
        assertEquals("BTE > This command can only be used by a player!", plain);
    }

    @Test
    void noPermissionIncludesPermissionNodeArgument() {
        Component rendered = GlobalTranslator.render(
                Messages.noPermission("btt.warp.use"),
                Locale.US
        );
        String plain = PlainTextComponentSerializer.plainText().serialize(rendered);
        assertTrue(plain.startsWith("BTE > "));
        assertTrue(plain.contains("btt.warp.use"));
        assertTrue(plain.contains("You don't have permission to execute this command."));
        assertFalse(plain.contains("btt.common.no-permission"));
    }

    @Test
    void prefixTagTracksMessagePrefixUpdates() {
        MessagePrefix.apply("<gold>[TEST]</gold> ");
        Component rendered = GlobalTranslator.render(
                Messages.component(MessageKeys.Common.PLAYER_ONLY),
                Locale.US
        );
        String plain = PlainTextComponentSerializer.plainText().serialize(rendered);
        assertEquals("[TEST] This command can only be used by a player!", plain);
        MessagePrefix.apply(MessagePrefix.DEFAULT_MINI_MESSAGE);
    }
}

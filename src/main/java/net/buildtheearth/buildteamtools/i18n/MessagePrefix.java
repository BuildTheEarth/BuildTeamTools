package net.buildtheearth.buildteamtools.i18n;

import com.alpsbte.alpslib.utils.ChatHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

/**
 * Plugin chat prefix used by translations via the MiniMessage {@code <prefix>} tag.
 *
 * <p>The appearance is configured in {@code config.yml} ({@code message-prefix}). Translation
 * strings can insert it with {@code <prefix>} so branding stays consistent and easy to restyle.
 */
public final class MessagePrefix {

    /** MiniMessage equivalent of the historical {@code &9&lBTE &8> &7} ChatHelper prefix. */
    public static final String DEFAULT_MINI_MESSAGE =
            "<dark_blue><bold>BTE</bold></dark_blue> <dark_gray>></dark_gray> ";

    private static final MiniMessage PLAIN = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_AMPERSAND =
            LegacyComponentSerializer.legacyAmpersand();

    private static volatile Component component = PLAIN.deserialize(DEFAULT_MINI_MESSAGE);
    private static volatile String miniMessage = DEFAULT_MINI_MESSAGE;

    private MessagePrefix() {
    }

    public static void apply(@NotNull String configuredMiniMessage) {
        String value = configuredMiniMessage.isBlank() ? DEFAULT_MINI_MESSAGE : configuredMiniMessage;
        Component parsed = PLAIN.deserialize(value);
        miniMessage = value;
        component = parsed;
        syncChatHelper(parsed);
    }

    public static Component component() {
        return component;
    }

    public static String miniMessage() {
        return miniMessage;
    }

    /** Legacy ampersand form for AlpsLib {@link ChatHelper} and older call sites. */
    public static String legacyAmpersand() {
        return LEGACY_AMPERSAND.serialize(component);
    }

    /**
     * Resolves {@code <prefix>} / {@code <prefix/>} inside MiniMessage translation strings to the
     * configured prefix component.
     */
    public static TagResolver tagResolver() {
        return TagResolver.resolver("prefix", (args, ctx) -> Tag.selfClosingInserting(component()));
    }

    private static void syncChatHelper(Component prefix) {
        if (ChatHelper.plugin == null) {
            return;
        }
        // ChatHelper stores a TextComponent; round-trip through legacy to match that shape.
        ChatHelper.PREFIX_COMPONENT = LEGACY_AMPERSAND.deserialize(LEGACY_AMPERSAND.serialize(prefix));
    }
}

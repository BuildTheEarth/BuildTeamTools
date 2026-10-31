package net.buildtheearth.buildteamtools.i18n;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

/**
 * Helpers for building and rendering translated Adventure components.
 *
 * <p>Chat, action bars, and titles can usually send {@link Component#translatable} directly;
 * Paper resolves them through {@link GlobalTranslator}. Inventory item names/lore do not, so
 * use {@link #render} / {@link #legacy} / {@link #lore} for menus.
 *
 * <p>Prefer putting {@code <prefix>} inside translation strings when a chat prefix is wanted.
 * {@link #withPrefix(Component)} remains for composing without editing the string.
 */
public final class Messages {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();
    private static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();

    private Messages() {
    }

    public static Component component(String key, ComponentLike... args) {
        return Component.translatable(key, args);
    }

    /** Convenience for {@code %s}/{@code <arg:N>} placeholders from plain values. */
    public static Component component(String key, Object... args) {
        ComponentLike[] components = new ComponentLike[args.length];
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            components[i] = arg instanceof ComponentLike componentLike
                    ? componentLike
                    : Component.text(String.valueOf(arg));
        }
        return component(key, components);
    }

    public static Component withPrefix(Component message) {
        return MessagePrefix.component().append(message);
    }

    public static Component playerOnly() {
        return component(MessageKeys.Common.PLAYER_ONLY);
    }

    public static Component noPermission(String permission) {
        return component(MessageKeys.Common.NO_PERMISSION, Component.text(permission));
    }

    public static Locale locale(@Nullable Audience audience) {
        if (audience == null) {
            return Translations.DEFAULT_LOCALE;
        }
        return audience.get(Identity.LOCALE).orElse(Translations.DEFAULT_LOCALE);
    }

    public static Locale locale(@Nullable Player player) {
        if (player == null) {
            return Translations.DEFAULT_LOCALE;
        }
        return player.locale();
    }

    public static Component render(Component component, Locale locale) {
        return GlobalTranslator.render(component, locale);
    }

    public static Component render(@Nullable Audience audience, Component component) {
        return render(component, locale(audience));
    }

    public static Component render(@Nullable Player player, Component component) {
        return render(component, locale(player));
    }

    public static Component render(@Nullable Player player, String key, ComponentLike... args) {
        return render(player, component(key, args));
    }

    /** Legacy section string for APIs that still require {@link String} item names/lore. */
    public static String legacy(@Nullable Player player, Component component) {
        return LEGACY.serialize(render(player, component));
    }

    public static String legacy(@Nullable Player player, String key, ComponentLike... args) {
        return legacy(player, component(key, args));
    }

    public static String plain(@Nullable Player player, String key, ComponentLike... args) {
        return PLAIN.serialize(render(player, key, args));
    }

    /** Renders with the default locale when no viewer is available. */
    public static String plain(String key, ComponentLike... args) {
        return PLAIN.serialize(render((Audience) null, component(key, args)));
    }

    /** Legacy section string for the default locale when no viewer is available. */
    public static String legacy(String key, ComponentLike... args) {
        return LEGACY.serialize(render((Audience) null, component(key, args)));
    }

    /**
     * Renders a translation as Adventure lore lines (newlines + wrap). Prefer this with
     * {@link net.buildtheearth.buildteamtools.utils.ItemComponents}.
     */
    public static List<Component> loreComponents(Player player, String key, ComponentLike... args) {
        return loreComponents(player, key, TextWrap.DEFAULT_LORE_WIDTH, args);
    }

    public static List<Component> loreComponents(Player player, String key, int maxWidth, ComponentLike... args) {
        return loreComponentLines(render(player, key, args), maxWidth);
    }

    /** Lore components for the default locale when no viewer is available. */
    public static List<Component> loreComponents(String key, ComponentLike... args) {
        return loreComponentLines(render((Audience) null, component(key, args)), TextWrap.DEFAULT_LORE_WIDTH);
    }

    /**
     * @deprecated Prefer {@link #loreComponents}; kept for AnvilGUI / AlpsLib APIs that still take strings.
     */
    @Deprecated
    public static List<String> lore(Player player, String key, ComponentLike... args) {
        return lore(player, key, TextWrap.DEFAULT_LORE_WIDTH, args);
    }

    /** @deprecated Prefer {@link #loreComponents}. */
    @Deprecated
    public static List<String> lore(Player player, String key, int maxWidth, ComponentLike... args) {
        return loreLines(render(player, key, args), maxWidth);
    }

    /** @deprecated Prefer {@link #loreComponents}. */
    @Deprecated
    public static List<String> lore(String key, ComponentLike... args) {
        return loreLines(render((Audience) null, component(key, args)), TextWrap.DEFAULT_LORE_WIDTH);
    }

    private static List<Component> loreComponentLines(Component rendered, int maxWidth) {
        return TextWrap.wrap(rendered, maxWidth);
    }

    private static List<String> loreLines(Component rendered, int maxWidth) {
        return loreComponentLines(rendered, maxWidth).stream().map(LEGACY::serialize).toList();
    }
}

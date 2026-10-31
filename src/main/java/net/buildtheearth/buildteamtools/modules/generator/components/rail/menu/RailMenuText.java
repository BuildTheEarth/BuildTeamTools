package net.buildtheearth.buildteamtools.modules.generator.components.rail.menu;

import lombok.experimental.UtilityClass;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import net.buildtheearth.buildteamtools.i18n.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

@UtilityClass
final class RailMenuText {

    static String enabled(Player player) {
        return Messages.plain(player, MessageKeys.Generator.Rail.Menu.ENABLED);
    }

    static String disabled(Player player) {
        return Messages.plain(player, MessageKeys.Generator.Rail.Menu.DISABLED);
    }

    static Component colored(NamedTextColor color, String text) {
        return Component.text(text, color);
    }
}

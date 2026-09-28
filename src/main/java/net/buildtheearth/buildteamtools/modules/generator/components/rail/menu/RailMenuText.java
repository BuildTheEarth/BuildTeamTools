package net.buildtheearth.buildteamtools.modules.generator.components.rail.menu;

import com.alpsbte.alpslib.utils.ChatHelper;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.format.NamedTextColor;

@UtilityClass
final class RailMenuText {

    static final String ENABLED = "Enabled";
    static final String DISABLED = "Disabled";

    static String color(NamedTextColor color, String text) {
        return ChatHelper.getColorizedString(color, text, false);
    }
}

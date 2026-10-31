package net.buildtheearth.buildteamtools.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.List;

/** Lore helpers for Adventure item text. */
public class ListUtil {

    public static List<Component> grayComponents(String... args) {
        List<Component> list = new ArrayList<>();
        for (String arg : args) {
            if (arg != null) {
                list.add(Component.text(arg, NamedTextColor.GRAY));
            }
        }
        return list;
    }
}

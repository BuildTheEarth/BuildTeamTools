package net.buildtheearth.utils;

import net.md_5.bungee.api.ChatColor;

public class ChatHelper {

    public static String successful(String string, Object... objects) {
        return ChatColor.DARK_GREEN + String.format(string.replaceAll("%s", ChatColor.GREEN + "%s" + ChatColor.DARK_GREEN), objects);
    }

    public static String highlight(String string, Object... objects) {
        return ChatColor.DARK_RED + String.format(string.replaceAll("%s", ChatColor.RED + "%s" + ChatColor.DARK_RED), objects);
    }

    public static String standard(String string, Object... objects) {
        return ChatColor.GOLD + String.format(string.replaceAll("%s", ChatColor.AQUA + "%s" + ChatColor.GOLD), objects);
    }

}

package net.buildtheearth.modules.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

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

    public static void sendMessageBox(CommandSender sender, String title, Runnable runnable) {
        sender.sendMessage("");
        sender.sendMessage("§7§m==============§e§l " + title + " §7§m==============");
        sender.sendMessage("");

        runnable.run();

        int length = org.bukkit.ChatColor.stripColor(title).length();
        char[] array = new char[length];
        Arrays.fill(array, '=');
        String bottom = "==============================" + new String(array);
        sender.sendMessage("");
        sender.sendMessage("§7§m" + bottom);
    }
}

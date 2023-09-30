package net.buildtheearth.modules.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class ChatUtil {

    /**
     * Capitalizes the first letter of each word in a string.
     *
     * @param input The string to capitalize.
     * @return The capitalized string.
     */
    public static String capitalize(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }

        String[] words = input.split("\\s+");
        StringBuilder capitalizedSentence = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                capitalizedSentence.append(Character.toUpperCase(word.charAt(0)));
                capitalizedSentence.append(word.substring(1).toLowerCase());
                capitalizedSentence.append(" ");
            }
        }

        return capitalizedSentence.toString().trim();
    }

    public static void sendMessageBox(CommandSender sender, String title, Runnable runnable) {
        sender.sendMessage("");
        sender.sendMessage("§7§m==============§e§l " + title + " §7§m==============");
        sender.sendMessage("");

        runnable.run();

        int length = ChatColor.stripColor(title).length();
        char[] array = new char[length];
        Arrays.fill(array, '=');
        String bottom = "==============================" + new String(array);
        sender.sendMessage("");
        sender.sendMessage("§7§m" + bottom);
    }
}

package net.buildtheearth.utils;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class ChatUtil {

    @Getter
    private static String prefixMessage = "§8[§2BuildTeamTools§8]§7 ";

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

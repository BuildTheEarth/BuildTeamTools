package net.buildtheearth.modules.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.logging.Level;

public class ChatHelper {

    public static void logError(String errorMessage, Object... objects) {
        Bukkit.getLogger().log(Level.INFO, ChatHelper.highlight(errorMessage, objects));
    }

    public static void logDebug(String errorMessage, Object... objects) {
        Bukkit.getLogger().log(Level.INFO, ChatHelper.standard(errorMessage, objects));
    }

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

    public static void sendMessageToPlayersNearLocation(Location location, String message, double maxDistance)
    {
        
        for (Player player : location.getWorld().getNearbyEntitiesByType(Player.class, location, maxDistance)) {
            player.sendMessage(message);
        }
    }
}

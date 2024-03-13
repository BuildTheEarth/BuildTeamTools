package net.buildtheearth.utils;

import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.utils.io.ConfigPaths;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.logging.Level;

public class ChatHelper {

    public static boolean DEBUG;

    static {
        BuildTeamTools.getInstance().getConfig();
        DEBUG = BuildTeamTools.getInstance().getConfig().getBoolean(ConfigPaths.DEBUG, false);
    }

    public static void logError(String errorMessage, Object... objects) {
        Bukkit.getLogger().log(Level.INFO, ChatHelper.error(errorMessage, objects));
    }

    public static void log(String string, Object... objects) {
        Bukkit.getConsoleSender().sendMessage( console(string, objects));
    }

    public static void logDebug(String string, Object... objects) {
        if(DEBUG || BuildTeamTools.getInstance().isDebug())
            Bukkit.getLogger().log(Level.INFO, console(string, objects));
    }

    public static String console(String string, Object... objects) {
        return BuildTeamTools.CONSOLE_PREFIX + String.format(string, objects);
    }

    public static String successful(String string, Object... objects) {
        return BuildTeamTools.PREFIX + ChatColor.GRAY + String.format(string.replaceAll("%s", ChatColor.GREEN + "%s" + ChatColor.GRAY), objects);
    }

    public static String error(String string, Object... objects) {
        return ChatColor.RED + String.format(string.replaceAll("%s", ChatColor.YELLOW + "%s" + ChatColor.RED), objects);
    }

    public static String standard(String string, Object... objects) {
        return BuildTeamTools.PREFIX + ChatColor.GRAY + String.format(string.replaceAll("%s", ChatColor.YELLOW + "%s" + ChatColor.GRAY), objects);
    }

    public static String standard(boolean containsPrefix, String string, Object... objects) {
        return containsPrefix ? BuildTeamTools.PREFIX : "" + ChatColor.GRAY + String.format(string.replaceAll("%s", ChatColor.YELLOW + "%s" + ChatColor.GRAY), objects);
    }

    public static String colorize(ChatColor color, String string, boolean bold) {
        return bold ? color + "" + ChatColor.BOLD + string : color + string;
    }

    public static String colorize(ChatColor color, ChatColor secondColor, String string, Object... objects) {
        return color + String.format(string.replaceAll("%s", secondColor + "%s" + color), objects);
    }

    /** Sends the given message to the given player and the console
     *
     * @param p player to send the message to. If null, the message will only be sent to the console
     * @param message message to send
     * @param logLevel log level to use for the console
     */
    public static void logPlayerAndConsole(@Nullable Player p, String message, Level logLevel){
        if(p != null)
            p.sendMessage(message);

        BuildTeamTools.getInstance().getLogger().log(logLevel, message);
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

    public static void sendMessageToPlayersNearLocation(Location location, String message, double maxDistance) {
        for (Player player : location.getWorld().getNearbyEntitiesByType(Player.class, location, maxDistance)) {
            player.sendMessage(message);
        }
    }
}

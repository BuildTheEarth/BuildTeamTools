package net.buildtheearth.utils;

import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.utils.io.ConfigPaths;
import net.buildtheearth.utils.lang.LangPaths;
import net.buildtheearth.utils.lang.LangUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.logging.Level;

public class ChatUtil {

    public static TextComponent PREFIX_COMPONENT = LegacyComponentSerializer.legacyAmpersand().deserialize(BuildTeamTools.PREFIX);

    public static boolean DEBUG;

    static {
        BuildTeamTools.getInstance().getConfig();
        DEBUG = BuildTeamTools.getInstance().getConfig().getBoolean(ConfigPaths.DEBUG, false);
    }


    // ------------------ LOGGING ------------------

    public static void logError(String errorMessage, Object... objects) {
        BuildTeamTools.getInstance().getLogger().log(Level.INFO, ChatUtil.getErrorString(errorMessage, objects));
    }

    public static void log(String string, Object... objects) {
        Bukkit.getConsoleSender().sendMessage( getConsoleString(string, objects));
    }

    public static void logDebug(String string, Object... objects) {
        if(DEBUG || BuildTeamTools.getInstance().isDebug())
            BuildTeamTools.getInstance().getLogger().log(Level.INFO, getConsoleString(string, objects));
    }

    public static String getConsoleString(String string, Object... objects) {
        return BuildTeamTools.CONSOLE_PREFIX + String.format(string, objects);
    }



    // ------------------ STANDARD TEXT (Gray & Yellow) ------------------

    public static Component getStandardComponent(boolean containsPrefix, String string, Object... objects) {
        Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(
                String.format(string.replaceAll("%s", "&e%s&7"), objects)
        ).color(NamedTextColor.GRAY);

        return containsPrefix ? PREFIX_COMPONENT.append(component) : component;
    }

    public static String getStandardString(boolean containsPrefix, String string, Object... objects) {
        return convertComponentToLegacyString(getStandardComponent(containsPrefix, string, objects));
    }

    public static String getStandardString(String string, Object... objects) {
        return getStandardString(true, string, objects);
    }

    public static void sendStandardMessage(Player player, String string, Object... objects) {
        player.sendMessage(getStandardComponent(true, string, objects));
    }

    public static void sendStandardMessage(Player player, boolean containsPrefix, String string, Object... objects) {
        player.sendMessage(getStandardComponent(containsPrefix, string, objects));
    }


    // ------------------ SUCCESS TEXT (Gray & Green Text) ------------------

    public static Component getSuccessComponent(String string, Object... objects) {
        return PREFIX_COMPONENT.append(
                LegacyComponentSerializer.legacyAmpersand().deserialize(
                        String.format(string.replaceAll("%s", "&a%s&7"), objects)
                ).color(NamedTextColor.GRAY)
        );
    }

    /**
     * Sends a success message to the given command sender with the specified string and objects.
     * The message is prefixed and formatted in gray and green text.
     *
     * @param sender The command sender to send the message to.
     * @param string The message to send, with %s placeholders for highlighted objects.
     * @param objects The objects to highlight within the message.
     */
    public static void sendSuccessful(CommandSender sender, String string, Object... objects) {
        sender.sendMessage(getSuccessComponent(string, objects));
    }



    // ------------------ ERROR TEXT (Red & Yellow Text) ------------------

    public static Component getErrorComponent(String string, boolean highlighted, Object... objects) {
        // Apply highlighting if needed
        if (highlighted)
            string = string.replaceAll("%s", "&e%s&c");

        return LegacyComponentSerializer.legacyAmpersand().deserialize(String.format(string, objects)).color(NamedTextColor.RED);
    }

    public static String getErrorString(String string, Object... objects) {
        return convertComponentToLegacyString(getErrorComponent(string, true, objects));
    }

    public static String getErrorString(String string, boolean highlighted, Object... objects) {
        return convertComponentToLegacyString(getErrorComponent(string, highlighted, objects));
    }


    /**
     * Sends an error message to the given command sender using a language path from {@link LangPaths}.
     * The message is retrieved from the language utility based on the specified language path.
     *
     * @param sender The command sender to send the error message to.
     * @param langPath The language path key from {@link LangPaths} used to retrieve the localized error message.
     * @param objects The objects to insert and highlight within the error message.
     */
    public static void sendError(CommandSender sender, String langPath, Object... objects){
        sender.sendMessage(getErrorString(LangUtil.getInstance().get(sender, langPath), objects));
    }

    /**
     * Sends an error message to the given command sender using a language path from {@link LangPaths}.
     * The message is retrieved from the language utility based on the specified language path.
     *
     * @param sender The command sender to send the error message to.
     * @param langPath The language path key from {@link LangPaths} used to retrieve the localized error message.
     * @param highlighted Whether to highlight the objects within the error message.
     * @param objects The objects to insert and highlight within the error message.
     */
    public static void sendError(CommandSender sender, String langPath, boolean highlighted, Object... objects){
        sender.sendMessage(getErrorString(LangUtil.getInstance().get(sender, langPath), highlighted, objects));
    }


    // ------------------ COLORIZED TEXT ------------------

    public static Component getColorizedComponent(NamedTextColor color, String string, boolean bold) {
        return Component.text(string).color(color).decoration(TextDecoration.BOLD, bold);
    }

    public static String getColorizedString(NamedTextColor color, String string, boolean bold) {
        return convertComponentToLegacyString(getColorizedComponent(color, string, bold));
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

    private static String convertComponentToLegacyString(Component component) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(component).replace("&", "§");
    }
}

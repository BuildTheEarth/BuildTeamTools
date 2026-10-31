package net.buildtheearth.buildteamtools.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

/**
 * Adventure replacement for AlpsLib {@code ChatHelper.sendMessageBox}, which still embeds {@code §}.
 */
public final class MessageBoxes {

    private MessageBoxes() {
    }

    public static void send(CommandSender sender, String title, Runnable body) {
        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text()
                .append(strikethroughEquals(14))
                .append(Component.text(" " + title + " ", NamedTextColor.YELLOW, TextDecoration.BOLD))
                .append(strikethroughEquals(14))
                .build());
        sender.sendMessage(Component.empty());

        body.run();

        int length = title.length();
        char[] bottom = new char[30 + length];
        Arrays.fill(bottom, '=');
        sender.sendMessage(Component.empty());
        sender.sendMessage(Component.text(new String(bottom), NamedTextColor.GRAY, TextDecoration.STRIKETHROUGH));
    }

    private static Component strikethroughEquals(int count) {
        return Component.text("=".repeat(count), NamedTextColor.GRAY, TextDecoration.STRIKETHROUGH);
    }
}

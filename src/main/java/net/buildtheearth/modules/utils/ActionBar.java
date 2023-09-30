package net.buildtheearth.modules.utils;

import org.bukkit.entity.Player;

public class ActionBar {
    public static void sendMessage(Player p, String msg) {
        p.sendActionBar(msg);
    }
}

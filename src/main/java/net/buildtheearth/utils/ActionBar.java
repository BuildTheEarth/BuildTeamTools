package net.buildtheearth.utils;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class ActionBar {
	public static void sendMessage(Player p, String msg) {
	    p.sendActionBar(msg);
	}
}

package net.buildtheearth.modules;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public interface WikiDocumented {

    /**
     * @return the wiki page URL for this object
     */
    String getWikiPage();

    /**
     * Sends more information about this object to a player.
     */
    default void sendWikiLink(@NonNull Player p) {
        p.sendMessage(" ");
        p.sendMessage("§cFor more information take a look here:");
        p.sendMessage("§c" + getWikiPage());
    }
}


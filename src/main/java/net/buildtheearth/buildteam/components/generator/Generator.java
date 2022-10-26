package net.buildtheearth.buildteam.components.generator;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class Generator {

    public static String WIKI_PAGE = "https://github.com/BuildTheEarth/BuildTeamTools/wiki/Generator";

    private static HashMap<UUID, History> playerHistory = new HashMap<>();

    public static Region getWorldEditSelction(Player p){
        Region plotRegion;

        try {
            plotRegion = Objects.requireNonNull(WorldEdit.getInstance().getSessionManager().findByName(p.getName())).getSelection(
                    Objects.requireNonNull(WorldEdit.getInstance().getSessionManager().findByName(p.getName())).getSelectionWorld());
        } catch (NullPointerException | IncompleteRegionException ex) {
            return null;
        }

        return plotRegion;
    }

    public static History getPlayerHistory(Player p){
        if(!playerHistory.containsKey(p.getUniqueId()))
            playerHistory.put(p.getUniqueId(), new History(p));

        return playerHistory.get(p.getUniqueId());
    }

    public static void sendMoreInfo(Player p){
        p.sendMessage(" ");
        p.sendMessage("§cFor more information take a look here:");
        p.sendMessage("§c" + WIKI_PAGE);
    }
}

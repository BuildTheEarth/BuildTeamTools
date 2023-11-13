package net.buildtheearth.buildteam.components.generator;

import com.sk89q.worldedit.LocalSession;
import lombok.Getter;
import net.buildtheearth.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class History {

    @Getter
    private final Player p;

    @Getter
    private final ArrayList<HistoryEntry> historyEntries;

    @Getter
    private final ArrayList<HistoryEntry> undoHistoryEntries;

    public History(Player p){
        this.p = p;
        this.historyEntries = new ArrayList<>();
        this.undoHistoryEntries = new ArrayList<>();
    }

    public void addHistoryEntry(HistoryEntry entry){
        historyEntries.add(entry);
    }

    public void undo(Player p){
        if(getHistoryEntries().isEmpty()){
            p.sendMessage("§cYou didn't generate any structures yet. Use /gen to create one.");
            return;
        }

        p.chat("//undo " + LocalSession.MAX_HISTORY_SIZE);
        undoHistoryEntries.addAll(getHistoryEntries());
        getHistoryEntries().clear();

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
            p.sendMessage("§7Successfully undid the last structure.");
            p.sendMessage("§7Use /gen redo to redo it.");
        }, 20L);
    }

    public void redo(Player p){
        if(getUndoHistoryEntries().isEmpty()){
            p.sendMessage("§cYou didn't undo any structures yet. Use /gen undo to undo one.");
            return;
        }

        p.chat("//redo " + LocalSession.MAX_HISTORY_SIZE);
        historyEntries.addAll(getUndoHistoryEntries());
        getUndoHistoryEntries().clear();

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, () -> {
            p.sendMessage("§7Successfully redid the last structure.");
            p.sendMessage("§7Use /gen undo to undo it.");
        }, 20L);
    }



    public static class HistoryEntry{

        @Getter
        private final GeneratorType generatorType;
        @Getter
        private final long timeCreated;
        @Getter
        private final int worldEditCommandCount;

        public HistoryEntry(GeneratorType generatorType, int worldEditCommandCount) {
            this.generatorType = generatorType;
            this.timeCreated = System.currentTimeMillis();
            this.worldEditCommandCount = worldEditCommandCount;
        }
    }
}



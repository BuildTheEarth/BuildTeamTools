package net.buildtheearth.buildteam.components.generator;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class History {

    @Getter
    private final Player p;

    @Getter
    private final ArrayList<HistoryEntry> historyEntries;

    public History(Player p){
        this.p = p;
        this.historyEntries = new ArrayList<>();
    }

    public void addHistoryEntry(HistoryEntry entry){
        historyEntries.add(entry);
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



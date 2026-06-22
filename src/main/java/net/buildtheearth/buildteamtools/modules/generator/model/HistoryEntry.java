package net.buildtheearth.buildteamtools.modules.generator.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class HistoryEntry {

    @Getter
    private final GeneratorType generatorType;

    @Getter
    private final long timeCreated;

    @Getter
    private final Script script;

    @Getter
    private final int worldEditCommandCount;

    @Getter
    private final List<BlockChange> blockChanges;

    public HistoryEntry(GeneratorType generatorType, Script script) {
        this.generatorType = generatorType;
        this.timeCreated = System.currentTimeMillis();
        this.worldEditCommandCount = script.getChanges();
        this.script = script;
        this.blockChanges = new ArrayList<>();
    }

    public boolean hasBlockChanges() {
        return blockChanges != null && !blockChanges.isEmpty();
    }

    public void applyUndo() {
        for (int i = blockChanges.size() - 1; i >= 0; i--)
            blockChanges.get(i).applyOld();
    }

    public void applyRedo() {
        for (BlockChange blockChange : blockChanges)
            blockChange.applyNew();
    }
}

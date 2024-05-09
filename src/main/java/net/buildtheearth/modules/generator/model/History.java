package net.buildtheearth.modules.generator.model;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.extension.platform.Actor;
import lombok.Getter;
import net.buildtheearth.BuildTeamTools;
import net.buildtheearth.modules.generator.utils.GeneratorUtils;
import net.buildtheearth.utils.ChatHelper;
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

    public void undoCommand(Player p){
        if(getHistoryEntries().isEmpty()){
            p.sendMessage("§cYou didn't generate any structures yet. Use /gen to create one. You can only undo the last structure.");
            return;
        }

        LocalSession session = getHistoryEntries().get(0).getScript().getLocalSession();
        Actor actor = getHistoryEntries().get(0).getScript().getActor();
        int worldEditCommandCount = getHistoryEntries().get(0).getWorldEditCommandCount();

        GeneratorUtils.undo(session, p, actor, worldEditCommandCount);

        getUndoHistoryEntries().add(getHistoryEntries().get(0));
        getHistoryEntries().clear();

        Bukkit.getScheduler().scheduleSyncDelayedTask(BuildTeamTools.getInstance(), () -> {
            p.sendMessage(ChatHelper.getStandardString("Successfully %s the last structure.", "undid"));
            p.sendMessage(ChatHelper.getStandardString("Use %s to redo it.", "/gen redo"));
        }, 20L);
    }

    public void redoCommand(Player p){
        if(getUndoHistoryEntries().isEmpty()){
            p.sendMessage("§cYou didn't undo any structures yet. Use /gen undo to undo one. You can only redo the last structure.");
            return;
        }

        LocalSession session = getUndoHistoryEntries().get(0).getScript().getLocalSession();
        Actor actor = getUndoHistoryEntries().get(0).getScript().getActor();
        int worldEditCommandCount = getUndoHistoryEntries().get(0).getWorldEditCommandCount();

        GeneratorUtils.redo(session, p, actor, worldEditCommandCount);

        getHistoryEntries().add(getUndoHistoryEntries().get(0));
        getUndoHistoryEntries().clear();

        Bukkit.getScheduler().scheduleSyncDelayedTask(BuildTeamTools.getInstance(), () -> {
            p.sendMessage(ChatHelper.getStandardString("Successfully %s the last structure.", "redid"));
            p.sendMessage(ChatHelper.getStandardString("Use %s to undo it.", "/gen undo"));
        }, 20L);
    }

    public static class HistoryEntry {

        @Getter
        private final GeneratorType generatorType;
        @Getter
        private final long timeCreated;
        @Getter
        private final Script script;
        @Getter
        private final int worldEditCommandCount;

        public HistoryEntry(GeneratorType generatorType, Script script) {
            this.generatorType = generatorType;
            this.timeCreated = System.currentTimeMillis();
            this.worldEditCommandCount = script.getChanges();
            this.script = script;
        }
    }
}



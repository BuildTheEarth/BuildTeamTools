package net.buildtheearth.buildteamtools.modules.generator.model;

import com.alpsbte.alpslib.utils.GeneratorUtils;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.extension.platform.Actor;
import lombok.Getter;
import net.buildtheearth.buildteamtools.i18n.MessageKeys;
import net.buildtheearth.buildteamtools.i18n.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class History {

    @Getter
    private final Player p;

    @Getter
    private final ArrayList<HistoryEntry> historyEntries;

    @Getter
    private final ArrayList<HistoryEntry> undoHistoryEntries;

    public History(Player p) {
        this.p = p;
        this.historyEntries = new ArrayList<>();
        this.undoHistoryEntries = new ArrayList<>();
    }

    public void addHistoryEntry(HistoryEntry entry) {
        historyEntries.add(entry);
        undoHistoryEntries.clear();
    }

    public void undoCommand(Player p) {
        if (getHistoryEntries().isEmpty()) {
            p.sendMessage(Messages.component(MessageKeys.Generator.History.NOTHING_TO_UNDO));
            return;
        }

        HistoryEntry entry = getHistoryEntries().getLast();

        if (entry.hasBlockChanges()) {
            entry.applyUndo();
        } else {
            LocalSession session = entry.getScript().getLocalSession();
            Actor actor = entry.getScript().getActor();
            int worldEditCommandCount = entry.getWorldEditCommandCount();

            GeneratorUtils.undo(session, p, actor, worldEditCommandCount);
        }

        getUndoHistoryEntries().add(entry);
        getHistoryEntries().remove(entry);

        p.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_DESTROY_EGG, 1.0F, 1.0F);

        Component redoMessage = Messages.component(MessageKeys.Generator.History.USE_REDO)
                .clickEvent(ClickEvent.runCommand("/btt generate redo"))
                .hoverEvent(HoverEvent.showText(Messages.component(MessageKeys.Generator.History.HOVER_REDO)));
        p.sendMessage(Messages.component(MessageKeys.Generator.History.UNDO_SUCCESS)
                .appendNewline()
                .append(redoMessage));
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
    }

    public void redoCommand(Player p) {
        if (getUndoHistoryEntries().isEmpty()) {
            p.sendMessage(Messages.component(MessageKeys.Generator.History.NOTHING_TO_REDO));
            return;
        }

        HistoryEntry entry = getUndoHistoryEntries().getLast();

        if (entry.hasBlockChanges()) {
            entry.applyRedo();
        } else {
            LocalSession session = entry.getScript().getLocalSession();
            Actor actor = entry.getScript().getActor();
            int worldEditCommandCount = entry.getWorldEditCommandCount();

            GeneratorUtils.redo(session, p, actor, worldEditCommandCount);
        }

        getHistoryEntries().add(entry);
        getUndoHistoryEntries().remove(entry);

        p.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_DESTROY_EGG, 1.0F, 1.0F);

        Component undoMessage = Messages.component(MessageKeys.Generator.History.USE_UNDO)
                .clickEvent(ClickEvent.runCommand("/btt generate undo"))
                .hoverEvent(HoverEvent.showText(Messages.component(MessageKeys.Generator.History.HOVER_UNDO)));
        p.sendMessage(Messages.component(MessageKeys.Generator.History.REDO_SUCCESS)
                .appendNewline()
                .append(undoMessage));
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
    }
}

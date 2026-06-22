package net.buildtheearth.buildteamtools.modules.generator.model;

import com.alpsbte.alpslib.utils.ChatHelper;
import com.alpsbte.alpslib.utils.GeneratorUtils;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.extension.platform.Actor;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
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
            p.sendMessage(ChatHelper.PREFIX_COMPONENT.append(ChatHelper.getErrorComponent(
                    "You didn't generate any structures yet. Use /gen to create one. You can only undo the last structure."
            )));
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

        ChatHelper.sendSuccessfulMessage(p, "Successfully %s the last structure.", "undid");
        p.sendMessage(ChatHelper.getStandardComponent(true, "Use %s to redo it.", "/gen redo")
                .clickEvent(ClickEvent.runCommand("/gen redo"))
                .hoverEvent(HoverEvent.showText(Component.text("Click to redo the last structure.", NamedTextColor.GRAY)))
        );
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
    }

    public void redoCommand(Player p) {
        if (getUndoHistoryEntries().isEmpty()) {
            p.sendMessage(ChatHelper.PREFIX_COMPONENT.append(ChatHelper.getErrorComponent(
                    "You didn't undo any structures yet. Use /gen undo to undo one. You can only redo the last structure."
            )));
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

        ChatHelper.sendSuccessfulMessage(p, "Successfully %s the last structure.", "redid");
        p.sendMessage(ChatHelper.getStandardComponent(true, "Use %s to undo it.", "/gen undo")
                .clickEvent(ClickEvent.runCommand("/gen undo"))
                .hoverEvent(HoverEvent.showText(Component.text("Click to undo the last structure.", NamedTextColor.GRAY)))
        );
        p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
    }
}

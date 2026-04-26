package net.buildtheearth.buildteamtools.modules.generator.model;

import com.alpsbte.alpslib.utils.ChatHelper;
import com.alpsbte.alpslib.utils.GeneratorUtils;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.extension.platform.Actor;
import lombok.Getter;
import lombok.Setter;
import net.buildtheearth.buildteamtools.BuildTeamTools;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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
            p.sendMessage("§cYou didn't generate any structures yet. Use /gen to create one. You can only undo the last structure.");
            return;
        }

        HistoryEntry entry = getHistoryEntries().get(getHistoryEntries().size() - 1);

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

        Bukkit.getScheduler().scheduleSyncDelayedTask(BuildTeamTools.getInstance(), () -> {
            ChatHelper.sendSuccessfulMessage(p, "Successfully %s the last structure.", "undid");
            p.sendMessage(ChatHelper.getStandardComponent(true, "Use %s to redo it.", "/gen redo")
                    .clickEvent(ClickEvent.runCommand("/gen redo"))
                    .hoverEvent(HoverEvent.showText(Component.text("Click to redo the last structure.", NamedTextColor.GRAY)))
            );
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
        }, 20L);
    }

    public void redoCommand(Player p) {
        if (getUndoHistoryEntries().isEmpty()) {
            p.sendMessage("§cYou didn't undo any structures yet. Use /gen undo to undo one. You can only redo the last structure.");
            return;
        }

        HistoryEntry entry = getUndoHistoryEntries().get(getUndoHistoryEntries().size() - 1);

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

        Bukkit.getScheduler().scheduleSyncDelayedTask(BuildTeamTools.getInstance(), () -> {
            ChatHelper.sendSuccessfulMessage(p, "Successfully %s the last structure.", "redid");
            p.sendMessage(ChatHelper.getStandardComponent(true, "Use %s to undo it.", "/gen undo")
                    .clickEvent(ClickEvent.runCommand("/gen undo"))
                    .hoverEvent(HoverEvent.showText(Component.text("Click to undo the last structure.", NamedTextColor.GRAY)))
            );
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
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

        @Getter
        private final List<BlockChange> blockChanges;

        public HistoryEntry(GeneratorType generatorType, Script script) {
            this.generatorType = generatorType;
            this.timeCreated = System.currentTimeMillis();
            this.worldEditCommandCount = script.getChanges();
            this.script = script;
            this.blockChanges = new ArrayList<>();
        }

        public HistoryEntry(GeneratorType generatorType, Script script, int worldEditCommandCount) {
            this.generatorType = generatorType;
            this.timeCreated = System.currentTimeMillis();
            this.worldEditCommandCount = worldEditCommandCount;
            this.script = script;
            this.blockChanges = new ArrayList<>();
        }

        public HistoryEntry(GeneratorType generatorType, Script script, List<BlockChange> blockChanges) {
            this.generatorType = generatorType;
            this.timeCreated = System.currentTimeMillis();
            this.worldEditCommandCount = 0;
            this.script = script;
            this.blockChanges = blockChanges == null ? new ArrayList<>() : blockChanges;
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

    public static class BlockChange {

        @Getter
        private final String worldName;

        @Getter
        private final int x;

        @Getter
        private final int y;

        @Getter
        private final int z;

        @Getter
        private final String oldBlockData;

        @Getter
        @Setter
        private String newBlockData;

        public BlockChange(String worldName, int x, int y, int z, String oldBlockData, String newBlockData) {
            this.worldName = worldName;
            this.x = x;
            this.y = y;
            this.z = z;
            this.oldBlockData = oldBlockData;
            this.newBlockData = newBlockData;
        }

        public void applyOld() {
            apply(oldBlockData);
        }

        public void applyNew() {
            apply(newBlockData);
        }

        private void apply(String blockDataString) {
            World world = Bukkit.getWorld(worldName);

            if (world == null)
                return;

            Block block = world.getBlockAt(x, y, z);
            BlockData blockData = Bukkit.createBlockData(blockDataString);
            block.setBlockData(blockData, false);
        }
    }
}
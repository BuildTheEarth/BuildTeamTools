package net.buildtheearth.buildteamtools.modules.generator.listeners;

import com.alpsbte.alpslib.utils.ChatHelper;
import net.buildtheearth.buildteamtools.modules.generator.GeneratorModule;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GeneratorListener implements Listener {

    private static final Map<UUID, Queue<String>> INTERNAL_GENERATOR_COMMANDS = new ConcurrentHashMap<>();

    public static void queueInternalGeneratorCommand(Player player, String command) {
        INTERNAL_GENERATOR_COMMANDS
                .computeIfAbsent(player.getUniqueId(), ignored -> new ConcurrentLinkedQueue<>())
                .add(command);
    }

    public static void removeInternalGeneratorCommand(Player player, String command) {
        Queue<String> commands = INTERNAL_GENERATOR_COMMANDS.get(player.getUniqueId());

        if (commands == null)
            return;

        commands.remove(command);

        if (commands.isEmpty())
            INTERNAL_GENERATOR_COMMANDS.remove(player.getUniqueId(), commands);
    }

    private static boolean consumeInternalGeneratorCommand(Player player, String command) {
        Queue<String> commands = INTERNAL_GENERATOR_COMMANDS.get(player.getUniqueId());

        if (commands == null)
            return false;

        String queuedCommand = commands.peek();

        if (!command.equals(queuedCommand))
            return false;

        commands.poll();

        if (commands.isEmpty())
            INTERNAL_GENERATOR_COMMANDS.remove(player.getUniqueId(), commands);

        return true;
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();

        if (!GeneratorModule.getInstance().isGenerating(p))
            return;

        if (!e.getMessage().startsWith("//"))
            return;

        if (consumeInternalGeneratorCommand(p, e.getMessage()))
            return;

        e.setCancelled(true);
        p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
        ChatHelper.sendErrorMessage(p, "You can't use WorldEdit commands while generating a structure.");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        if (!GeneratorModule.getInstance().isGenerating(p))
            return;

        if (e.getItem() == null)
            return;

        if (e.getItem().getType() != Material.WOODEN_AXE)
            return;

        e.setCancelled(true);
        p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
        ChatHelper.sendErrorMessage(p, "You can't use WorldEdit while generating a structure.");
    }
}

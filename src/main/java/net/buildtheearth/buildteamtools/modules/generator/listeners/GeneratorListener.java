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

public class GeneratorListener implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();

        if(!GeneratorModule.getInstance().isGenerating(p))
            return;
        if(!e.getMessage().startsWith("//"))
            return;

        e.setCancelled(true);
        p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
        ChatHelper.sendErrorMessage(p, "You can't use WorldEdit commands while generating a structure.");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();

        if(!GeneratorModule.getInstance().isGenerating(p))
            return;
        if(e.getItem() == null)
            return;
        if(e.getItem().getType() != Material.WOODEN_AXE)
            return;

        e.setCancelled(true);
        p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
        ChatHelper.sendErrorMessage(p, "You can't use WorldEdit while generating a structure.");
    }
}

package net.buildtheearth.buildteam.components.generator.field;

import com.sk89q.worldedit.regions.Region;
import net.buildtheearth.buildteam.components.generator.Generator;
import net.buildtheearth.buildteam.components.generator.GeneratorType;
import net.buildtheearth.buildteam.components.generator.History;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class FieldScripts {

    public static void fieldscript_v_1_0(Player p, Field field, Region region) {
        //TODO ADD VARIABLES NEEDED FOR SCRIPT

        int operations = 0;

        p.chat("/clearhistory");
        p.chat("//gmask");

        // ----------- PREPARATION 01 ----------
        // Replace all non-solid blocks with air

        p.chat("//expand 10 up");
        p.chat("//expand 10 down");

        // Remove all non-solid blocks
        p.chat("//gmask !#solid");
        p.chat("//replace 0");
        operations++;

        // Remove all trees and pumpkins
        p.chat("//gmask");
        p.chat("//replace leaves,log,pumpkin 0");
        operations++;

        Block[][][] blocks = Generator.analyzeRegion(p, p.getWorld());

        int highestBlock = Generator.getMaxHeight(blocks, Material.LOG, Material.LOG_2, Material.LEAVES, Material.LEAVES_2, Material.WOOL);

        // ----------- PREPARATION 02 ----------
        // Replace the field area by yellow wool

        p.chat("//gmask <0");
        p.chat("//set 35:4");
        operations++;






        Generator.getPlayerHistory(p).addHistoryEntry(new History.HistoryEntry(GeneratorType.FIELD, operations));
    }
}

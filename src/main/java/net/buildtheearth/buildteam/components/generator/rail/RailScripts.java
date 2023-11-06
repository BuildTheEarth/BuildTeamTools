package net.buildtheearth.buildteam.components.generator.rail;

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import net.buildtheearth.Main;
import net.buildtheearth.buildteam.components.generator.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RailScripts {

    public static void railScript_v_1_3(Player p, Rail rail, Region region) {
        List<String> commands = new ArrayList<>();
        //HashMap<Flag, String > flags = rail.getPlayerSettings().get(p.getUniqueId()).getValues();

        int xPos = p.getLocation().getBlockX();
        int zPos = p.getLocation().getBlockZ();

        int operations = 0;
        p.chat("/clearhistory");

        int railWidth = 5;

        // Get the points of the region
        List<Vector> points = new ArrayList<>();

        // Add all points of the region to the list no matter what type of region it is
        if (region instanceof Polygonal2DRegion) {
            Polygonal2DRegion polyRegion = (Polygonal2DRegion) region;

            for (BlockVector2D blockVector2D : polyRegion.getPoints())
                points.add(blockVector2D.toVector());

        } else if (region instanceof ConvexPolyhedralRegion) {
            ConvexPolyhedralRegion convexRegion = (ConvexPolyhedralRegion) region;
            points.addAll(convexRegion.getVertices());

        }else if (region instanceof CuboidRegion) {
            CuboidRegion cuboidRegion = (CuboidRegion) region;

            points.add(cuboidRegion.getPos1());
            points.add(cuboidRegion.getPos2());
        } else {
            p.sendMessage("§c§lERROR: §cRegion type not supported!");
            return;
        }

        points = Generator.populatePoints(points, 5);

        // ----------- PREPARATION 01 ----------
        // Replace all unnecessary blocks with air

        List<Vector> polyRegionLine = new ArrayList<>(points);
        polyRegionLine = Generator.extendPolyLine(polyRegionLine);
        List<Vector> polyRegionPoints = Generator.shiftPoints(polyRegionLine, railWidth + 2, true);

        // Create a region from the points
        Generator.createPolySelection(p, polyRegionPoints, null);

        p.chat("//expand 30 up");
        p.chat("//expand 10 down");

        // Remove non-solid blocks
        p.chat("//gmask !#solid");
        p.chat("//replace 0");
        operations++;

        // Remove all trees and pumpkins
        p.chat("//gmask");
        p.chat("//replace leaves,log,pumpkin 0");
        operations++;

        p.chat("//gmask");


        Block[][][] regionBlocks = Generator.analyzeRegion(p, p.getWorld());
        Generator.adjustHeight(points, regionBlocks);


        // ----------- RAILWAY ----------

        // Draw the railway curve

        Generator.createConvexSelection(commands, points);
        commands.add("//gmask !solid");
        commands.add("//curve 42");
        operations++;
        commands.add("//gmask");


        // Create the railway
        Generator.createPolySelection(commands, polyRegionPoints);

        commands.add("//replace \"0 !>42 =queryRel(0,-1,-1,42,-1)||queryRel(0,-1,1,42,-1)\" 145:1");
        operations++;
        commands.add("//replace \"0 !>42 =queryRel(-1,-1,0,42,-1)||queryRel(1,-1,0,42,-1)\" 145:0");
        operations++;

        commands.add("//gmask =(sqrt((x-(" + xPos + "))^2+(z-(" + zPos + "))^2)%3)-2");
        commands.add("//replace \"0 =queryRel(0,0,1,145,-1)||queryRel(0,0,-1,145,-1)||queryRel(1,0,0,145,-1)||queryRel(-1,0,0,145,-1)\" 44:0");
        operations++;
        commands.add("//replace \"!145 !0 <145\" 43:0");
        operations++;
        commands.add("//gmask");
        commands.add("//replace 42 2");
        operations++;

        commands.add("//gmask");
        Generator.createConvexSelection(commands, points);

        Main.buildTeamTools.getGenerator().getCommands().add(new Command(p, rail, commands, operations, regionBlocks));
        Generator.getPlayerHistory(p).addHistoryEntry(new History.HistoryEntry(GeneratorType.RAILWAY, operations));
    }
}
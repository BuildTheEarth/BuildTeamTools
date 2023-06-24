package net.buildtheearth.buildteam.components.generator.rail;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import net.buildtheearth.Main;
import net.buildtheearth.buildteam.components.generator.Command;
import net.buildtheearth.buildteam.components.generator.Generator;
import net.buildtheearth.buildteam.components.generator.GeneratorType;
import net.buildtheearth.buildteam.components.generator.History;
import net.buildtheearth.buildteam.components.generator.road.Road;
import net.buildtheearth.buildteam.components.generator.road.RoadFlag;
import net.buildtheearth.buildteam.components.generator.road.RoadSettings;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RailScripts {

    public static void railscript_v_1_3(Player p, Rail rail, ConvexPolyhedralRegion region) {
        List<String> commands = new ArrayList<>();
        HashMap< Object, String > flags = rail.getPlayerSettings().get(p.getUniqueId()).getValues();

        int xPos = p.getLocation().getBlockX();
        int zPos = p.getLocation().getBlockZ();

        int operations = 0;
        p.chat("/clearhistory");

        int railWidth = 5;

        // Get the points of the region
        List<Vector> points = new ArrayList<>(region.getVertices());
        points = Generator.populatePoints(points, 5);

        // ----------- PREPARATION 01 ----------
        // Replace all unnecessary blocks with air

        List<Vector> polyRegionLine = new ArrayList<>(points);
        polyRegionLine = Generator.extendPolyLine(polyRegionLine);
        List<Vector> polyRegionPoints = Generator.shiftPoints(polyRegionLine, railWidth + 2, true);
        List<Vector> polyRegionPointsExact = Generator.shiftPoints(polyRegionLine, railWidth, true);

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
        points = Generator.adjustHeight(points, regionBlocks);


        // ----------- RAILWAY ----------

        // Draw the railway curve

        Generator.createConvexSelection(commands, points, regionBlocks);
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
        Generator.createConvexSelection(commands, points, null);

        Main.buildTeamTools.getGenerator().getCommands().add(new Command(p, rail, commands, operations, regionBlocks));
        Generator.getPlayerHistory(p).addHistoryEntry(new History.HistoryEntry(GeneratorType.RAILWAY, operations));
    }
}